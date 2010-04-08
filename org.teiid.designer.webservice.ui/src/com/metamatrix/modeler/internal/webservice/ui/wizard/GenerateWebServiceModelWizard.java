/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice.ui.wizard;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.webservice.ui.IUiConstants;
import com.metamatrix.modeler.webservice.util.WebServiceBuildOptions;
import com.metamatrix.modeler.webservice.util.WebServiceBuilderHelper;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

/**
 * This wizard is used to drive the creation of XSD and XML from Relational Selections.
 */
public class GenerateWebServiceModelWizard extends AbstractWizard implements INewWizard, IInternalUiConstants {

    /** Properties key prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(GenerateWebServiceModelWizard.class);

    public static boolean HEADLESS = false; // Flag to set Wizard to run in headless mode for testing

    // The page for driving the user options.
    protected InterfaceDefinitionPage interfaceDefinitionPage;

    // The current workspace selection
    protected ISelection selection;

    private IWizardPage[] wizardPageArray;
    private IProgressMonitor monitor;

    // The result messages to return to the user
    private MultiStatus result;

    WebServiceBuilderHelper webServiceBuilderHelper;
    WebServiceBuildOptions webServiceBuildOptions;

    /**
     * Constructor for NewModelWizard.
     */
    public GenerateWebServiceModelWizard() {
        super(UiPlugin.getDefault(), getString("title"), null); //$NON-NLS-1$
        this.webServiceBuilderHelper = new WebServiceBuilderHelper();
        setNeedsProgressMonitor(true);
    }

    /**
     * Adding the page to the wizard.
     */
    @Override
    public void addPages() {
        interfaceDefinitionPage = new InterfaceDefinitionPage(this.selection);
        addPage(interfaceDefinitionPage);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We will create an operation and run it using wizard as
     * execution context.
     */
    @Override
    public boolean finish() {
        final IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run( final IProgressMonitor monitor ) {
                // Get the WebService edit object from the wizard create the interface
                webServiceBuildOptions = interfaceDefinitionPage.getWebServiceBuildOptions();
                doFinish(monitor);
            }
        };

        // Detmine TXN status and start one if required.
        // This operation is not undoable OR significant.
        final String txnDescr = getString("createWebService.txnDescr"); //$NON-NLS-1$
        final boolean startedTxn = ModelerCore.startTxn(false, false, txnDescr, GenerateWebServiceModelWizard.this);
        try {
            new ProgressMonitorDialog(getShell()).run(false, false, op);
        } catch (Throwable err) {
            UTIL.log(IStatus.ERROR, err, err.getMessage());
        } finally {
            // This operation is NOT undoable or significant... ALWAYS comit to ensure
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
        CoreArgCheck.isNotNull(this.webServiceBuilderHelper);

        this.monitor = monitor == null ? new NullProgressMonitor() : monitor;

        // Initialize the progress monitor
        final String msg = getString("begin"); //$NON-NLS-1$
        monitor.beginTask(msg, 1);

        this.result = this.webServiceBuilderHelper.createWebService(this.webServiceBuildOptions, false, true, this.monitor);

        // Update the monitor
        this.monitor.worked(1);

        // Log the result
        if (!this.result.isOK()) {
            for (int i = 0; i < this.result.getChildren().length; i++) {
                UTIL.log(this.result.getChildren()[i]);
            }
        }

        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                if (webServiceBuilderHelper.getWebServiceModel() != null) {
                    // Changed to use method that insures Object editor mode is on
                    ModelEditorManager.openInEditMode(webServiceBuilderHelper.getWebServiceModel(),
                                                      true,
                                                      UiConstants.ObjectEditor.IGNORE_OPEN_EDITOR);
                }

            }
        });
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

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getNextPage( IWizardPage page ) {
        if (page == interfaceDefinitionPage) {
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

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#canFinish()
     * This Wizard can finish if the Options page is complete.
     */
    @Override
    public boolean canFinish() {
        boolean result = false;
        IWizardPage currentPage = getContainer().getCurrentPage();

        if (currentPage == this.interfaceDefinitionPage) {
            result = currentPage.isPageComplete();
        } else {
            boolean lastPage = (currentPage == wizardPageArray[wizardPageArray.length - 1]);
            result = lastPage && currentPage.isPageComplete();
        }

        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getPageCount()
     */
    @Override
    public int getPageCount() {
        if (wizardPageArray != null) {
            return wizardPageArray.length + 1;
        }
        return 1;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
     */
    @Override
    public IWizardPage getPreviousPage( IWizardPage page ) {

        if (wizardPageArray == null || page == this.interfaceDefinitionPage) {
            return null;
        }
        if (page == wizardPageArray[0]) {
            return this.interfaceDefinitionPage;
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
            this.result = new MultiStatus(IUiConstants.PLUGIN_ID, 0, UTIL.getString("GenerateWebServiceModelWizard.result"), null); //$NON-NLS-1$
        }

        return this.result;
    }

    /**
     * Utility to get localized text.
     * 
     * @param theKey the key whose value is being localized
     * @return the localized text
     */
    private static String getString( String theKey ) {
        return UTIL.getString(new StringBuffer().append(PREFIX).append(theKey).toString());
    }
}
