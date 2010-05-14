/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.xsd.XSDElementDeclaration;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.xsd.XsdBuilderOptions;
import com.metamatrix.metamodels.xsd.XsdSchemaBuilderImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.notification.util.DefaultIgnorableNotificationSource;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.transaction.UnitOfWorkImpl;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.webservice.util.WebServiceBuilderHelper;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * This wizard is used to drive the creation of XSD and XML from Relational Selections.
 */

public class GenerateXsdWizard extends AbstractWizard implements INewWizard, UiConstants {
    public static boolean HEADLESS = false; // Flag to set Wizard to run in headless mode for testing

    private final PluginUtil Util = UiConstants.Util;

    // The page for driving the user options.
    protected GenerateXsdWizardOptionslPage genXsdWizardOptionsPage;

    // The current workspace selection
    protected ISelection selection;

    private IWizardPage[] wizardPageArray;
    private IProgressMonitor monitor;

    // The result messages to return to the user
    private MultiStatus result;

    // The user options returned from the Options Page.
    XsdBuilderOptions ops;

    // Map of output XmlDocuments to input global Elements
    private HashMap outputToInputMappings = new HashMap();

    private ModelResource wsModel;

    /**
     * Constructor for NewModelWizard.
     */
    public GenerateXsdWizard() {
        super(UiPlugin.getDefault(), UiConstants.Util.getString("GenerateXsdWizard.title"), null); //$NON-NLS-1$
        setNeedsProgressMonitor(true);
    }

    /**
     * @see com.metamatrix.ui.internal.wizard.AbstractWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     * @since 4.3
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        super.createPageControls(pageContainer, false);
    }

    /**
     * Adding the page to the wizard.
     */
    @Override
    public void addPages() {
        genXsdWizardOptionsPage = new GenerateXsdWizardOptionslPage(this.selection);
        addPage(genXsdWizardOptionsPage);

        // this.getShell().setSize(450, 450);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using wizard as
     * execution context.
     */
    @Override
    public boolean finish() {
        final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            @Override
            protected void execute( IProgressMonitor theMonitor ) {
                // Get the options and execute the build.
                ops = genXsdWizardOptionsPage.getOptions();
                doFinish(theMonitor);
            }
        };

        // Detmine TXN status and start one if required.
        // This operation is not undoable OR significant.
        final boolean startedTxn = ModelerCore.startTxn(false,
                                                        false,
                                                        GenerateXsdWizard.this.getWindowTitle(),
                                                        new DefaultIgnorableNotificationSource(GenerateXsdWizard.this));
        if (startedTxn) {
            // Defect 22359 - improve new model performance
            // utilize the overrideRollback txn capability so the UOW doesn't cache commands
            ((UnitOfWorkImpl)ModelerCore.getCurrentUoW()).setOverrideRollback(true);
        }

        try {
            new ProgressMonitorDialog(getShell()).run(false, false, op);
        } catch (Throwable err) {
            Util.log(IStatus.ERROR, err, err.getMessage());
        } finally {
            // This operation is NOT undoable or significant... ALWAYS commit to ensure
            // Nothing is left hanging.
            if (startedTxn) {
                ModelerCore.commitTxn();
            }
        }

        return true;
    }

    /**
     * The worker method. It will find the container, create the file(s) - Made this method public to allow for headless testing.
     * 
     * @param IPRogressMonitor - The progress monitor for this operation.
     */

    public void doFinish( final IProgressMonitor monitor ) {
        CoreArgCheck.isNotNull(this.ops);
        // Create the XSD BUilder and capture state from the Options
        final XsdSchemaBuilderImpl builder = new XsdSchemaBuilderImpl(this.ops);
        this.monitor = monitor == null ? new NullProgressMonitor() : monitor;
        this.result = getResult();

        // Initialize the progress monitor
        final String msg = Util.getString("GenerateXsdWizard.begin"); //$NON-NLS-1$
        monitor.beginTask(msg, 4);

        // Execute the build of the actual XSD models.
        this.result = builder.buildSchemas(this.monitor, this.result);

        // Update the monitor
        this.monitor.worked(1);

        // Execute the Helper build to finish the XML, XSD, and SQL generation
        final GenerateXsdHelper helper = new GenerateXsdHelper(builder, ops, result);
        outputToInputMappings = helper.execute(monitor);
        // Check if XSDs need to be re-saved since calling helper.execute() can cause XSD imports to be modified as a side-effect.
        for (Iterator iter = builder.getRootElements().iterator(); iter.hasNext();) {
            XSDElementDeclaration elem = (XSDElementDeclaration)iter.next();
            ModelResource resrc = ModelerCore.getModelEditor().findModelResource(elem);
            try {
                if (resrc != null && resrc.hasUnsavedChanges()) {
                    resrc.save(monitor, true);
                }
            } catch (ModelWorkspaceException err) {
                Util.log(err);
            }
        } // for

        // Update the monitor
        this.monitor.worked(1);
        if (ops.genWs()) {
            final Collection webServiceOptions = helper.createWebServiceBuildOptions(outputToInputMappings, ops);
            WebServiceBuilderHelper wsbh = new WebServiceBuilderHelper();
            wsbh.setParentPath(ops.getParentPath());
            wsbh.createWebServices(webServiceOptions, true, result, monitor);
            helper.doSave();
            wsModel = wsbh.getWebServiceModel();
        }

        // Log the result
        if (!this.result.isOK()) {
            for (int i = 0; i < this.result.getChildren().length; i++) {
                Util.log(this.result.getChildren()[i]);
            }
        }
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection selection ) {
        this.selection = selection;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage( IWizardPage page ) {
        if (page == genXsdWizardOptionsPage) {
            return null;
        }

        for (int i = 0; i < wizardPageArray.length; ++i) {
            if (wizardPageArray[i] == page) {
                if (i + 1 < wizardPageArray.length) {
                    return wizardPageArray[i + 1];
                }
            }
        }
        return null;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#canFinish() This Wizard can finish if the Options page is complete.
     */
    @Override
    public boolean canFinish() {
        boolean result = false;
        IWizardPage currentPage = getContainer().getCurrentPage();

        if (currentPage == this.genXsdWizardOptionsPage) {
            result = currentPage.isPageComplete();
        } else {
            boolean lastPage = (currentPage == wizardPageArray[wizardPageArray.length - 1]);
            result = lastPage && currentPage.isPageComplete();
        }

        return result;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#getPageCount()
     */
    @Override
    public int getPageCount() {
        if (wizardPageArray != null) {
            return wizardPageArray.length + 1;
        }
        return 1;
    }

    /**
     * Add a public setter to allow for Headless testing
     * 
     * @param ops
     */
    public void setOptions( XsdBuilderOptions ops ) {
        this.ops = ops;
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getPreviousPage( IWizardPage page ) {

        if (wizardPageArray == null || page == this.genXsdWizardOptionsPage) {
            return null;
        }
        if (page == wizardPageArray[0]) {
            return this.genXsdWizardOptionsPage;
        }
        for (int i = 1; i < wizardPageArray.length; ++i) {
            if (page == wizardPageArray[i]) {
                return wizardPageArray[i - 1];
            }
        }
        return null;
    }

    /**
     * A getter for the result message buffer.
     * 
     * @return The results message buffer
     */
    public MultiStatus getResult() {
        if (this.result == null) {
            this.result = new MultiStatus(UiConstants.PLUGIN_ID, 0, Util.getString("GenerateXsdWizard.result"), null); //$NON-NLS-1$
        }

        return this.result;
    }

    /**
     * Return the map of Output XmlDocument node to Input Xsd Global Element mappings.
     */
    public HashMap getOutPutToInputMappings() {
        return outputToInputMappings;
    }

    public ModelResource getWebServiceModel() {
        return this.wsModel;
    }
}
