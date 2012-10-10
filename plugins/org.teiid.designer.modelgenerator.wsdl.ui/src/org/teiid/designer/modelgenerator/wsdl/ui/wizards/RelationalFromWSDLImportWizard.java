/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.modelgenerator.wsdl.ModelBuildingException;
import org.teiid.designer.modelgenerator.wsdl.RelationalModelBuilder;
import org.teiid.designer.modelgenerator.wsdl.model.Model;
import org.teiid.designer.modelgenerator.wsdl.model.ModelGenerationException;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import org.teiid.designer.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiPlugin;
import org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.OperationsDetailsPage;
import org.teiid.designer.schema.tools.processing.SchemaProcessingException;
import org.teiid.designer.ui.common.wizard.AbstractWizard;


/**
 * Wizard for import of WSDL Source and generation of Relational Model from it.
 *
 * @since 8.0
 */
public class RelationalFromWSDLImportWizard extends AbstractWizard implements IImportWizard, ModelGeneratorWsdlUiConstants {

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(RelationalFromWSDLImportWizard.class);

    private static final String TITLE = getString("title"); //$NON-NLS-1$
    private static final ImageDescriptor IMAGE = ModelGeneratorWsdlUiPlugin.getDefault().getImageDescriptor(Images.IMPORT_WSDL_ICON);

    /** This manager interfaces with the relational from wsdl generator */
    private WSDLImportWizardManager importManager;

    /** The page where the WSDL source file and Relational target model are selected. */
    private WizardPage selectWsdlPage;

    /** The page where the user selects which WSDL operations to build. */
    private WizardPage selectWsdlOperationsPage;
    
    /** The page where the user provides details to the generated create and extract procedures */
    private WizardPage operationsDetailsPage;

    private IStructuredSelection selection;

    /**
     * Creates a wizard for generating relational entities from WSDL source.
     */
    public RelationalFromWSDLImportWizard() {
        super(ModelGeneratorWsdlUiPlugin.getDefault(), TITLE, IMAGE);
    }

    /**
     * Get the localized string text for the provided id
     */
    private static String getString( final String id ) {
        return UTIL.getString(I18N_PREFIX + id);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        super.createPageControls(pageContainer);
    }

    /**
     * Method declared on IWorkbenchWizard.
     */
    @Override
	public void init( IWorkbench workbench,
                      IStructuredSelection currentSelection ) {
        this.importManager = new WSDLImportWizardManager();
        this.selection = currentSelection;

        List selectedResources = IDE.computeSelectedResources(currentSelection);
        if (!selectedResources.isEmpty()) {
            this.selection = new StructuredSelection(selectedResources);
        }

        createWizardPages(this.selection);
        setNeedsProgressMonitor(true);
    }

    /**
     * Create Wizard pages for the wizard
     * 
     * @param theSelection the initial workspace selection
     */
    @SuppressWarnings("unused")
	public void createWizardPages( ISelection theSelection ) {
        this.importManager = new WSDLImportWizardManager();

        // construct pages
        SELECT_WSDL_PAGE : {
	        this.selectWsdlPage = new SelectWsdlPage(this.importManager);
	        this.selectWsdlPage.setPageComplete(false);
	        addPage(this.selectWsdlPage);
        }
        
        SELECT_OPERATIONS_PAGE : {
        	this.selectWsdlOperationsPage = new SelectWsdlOperationsPage(this.importManager);
        	this.selectWsdlOperationsPage.setPageComplete(false);
        	addPage(this.selectWsdlOperationsPage);
        }
        
        OPERATIONS_DETAILS_PAGE : {
        	this.operationsDetailsPage = new OperationsDetailsPage(this.importManager);
        	this.operationsDetailsPage.setPageComplete(false);
        	addPage(this.operationsDetailsPage);
        }

        // give the WSDL selection page the current workspace selection
        ((SelectWsdlPage)this.selectWsdlPage).setInitialSelection(theSelection);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
        boolean result = false;

        // Save object selections from previous page
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            @Override
			public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                // Wrap in transaction so it doesn't result in Significant Undoable
                boolean started = ModelerCore.startTxn(false, false, "Changing Sql Connections", //$NON-NLS-1$
                                                       new Object());
                boolean succeeded = false;
                try {
                    runFinish(monitor);
                    succeeded = true;
                } catch (ModelBuildingException mbe) {
                    mbe.printStackTrace(System.err);
                    throw new InvocationTargetException(mbe);
                } catch (Throwable t) {
                    throw new InvocationTargetException(t);
                } finally {
                    if (started) {
                        if (succeeded) {
                            ModelerCore.commitTxn();
                        } else {
                            ModelerCore.rollbackTxn();
                        }
                    }
                }

            }
        };
        try {
            ProgressMonitorDialog dlg = new ProgressMonitorDialog(getShell());
            dlg.run(true, true, op);
            result = true;
        } catch (Throwable err) {
            if (err instanceof InvocationTargetException) {
                Throwable t = ((InvocationTargetException)err).getTargetException();
                final IStatus iteStatus = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, getString("importError.msg"), t); //$NON-NLS-1$
                ErrorDialog.openError(this.getShell(), getString("importError.title"), getString("importError.msg"), iteStatus); //$NON-NLS-1$  //$NON-NLS-2$
                t.printStackTrace(System.err);
            } else {
                final IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, getString("importError.msg"), err); //$NON-NLS-1$);
                ErrorDialog.openError(this.getShell(), getString("importError.title"), getString("importError.msg"), status); //$NON-NLS-1$  //$NON-NLS-2$
                err.printStackTrace(System.err);
            }
        } finally {
            dispose();
        }

        return result;
    }

    public void runFinish( IProgressMonitor theMonitor ) throws ModelBuildingException, SchemaProcessingException, ModelGenerationException {
        // Target Model Name
//        String modelName = this.importManager.getTargetModelName();

        // Target location for the new model
        IContainer container = this.importManager.getViewModelLocation();

        // The Selected Operations
        Model model = importManager.getWSDLModel();
        RelationalModelBuilder modelBuilder = new RelationalModelBuilder(model, this.importManager.getConnectionProfile());
        try {
			modelBuilder.modelOperations(this.importManager.getSelectedOperations(), container);
		} catch (ModelWorkspaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelerCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    }
}
