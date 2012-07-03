/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.ide.IDE;
import org.teiid.designer.modelgenerator.wsdl.ui.Messages;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.modelgenerator.wsdl.ModelBuildingException;
import com.metamatrix.modeler.modelgenerator.wsdl.RelationalModelBuilder;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.ModelGenerationException;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiConstants.Images;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.ModelGeneratorWsdlUiPlugin;
import com.metamatrix.modeler.modelgenerator.wsdl.ui.internal.wizards.WSDLImportWizardManager;
import com.metamatrix.modeler.schema.tools.processing.SchemaProcessingException;
import com.metamatrix.modeler.ui.viewsupport.DesignerPropertiesUtil;
import com.metamatrix.modeler.ui.viewsupport.IPropertiesContext;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.wizard.AbstractWizard;

public class ImportWsdlSoapWizard extends AbstractWizard implements IImportWizard, IPropertiesContext {

    private static final String TITLE = Messages.ImportWsdlSoapWizard_title;
    private static final ImageDescriptor IMAGE = ModelGeneratorWsdlUiPlugin.getDefault().getImageDescriptor(Images.IMPORT_WSDL_ICON);

    /** This manager interfaces with the relational from wsdl generator */
    private WSDLImportWizardManager importManager = new WSDLImportWizardManager();

    /** The page where the WSDL source file and Relational target model are selected. */
    private WsdlDefinitionPage selectWsdlPage;
    
    /** The page where the user provides details to the generated create and extract procedures */
    private WizardPage operationsDetailsPage;
    
    private WizardPage modelDefinitionPage;

    private IStructuredSelection selection;

    /**
     * Creates a wizard for generating relational entities from WSDL source.
     */
    public ImportWsdlSoapWizard() {
        super(ModelGeneratorWsdlUiPlugin.getDefault(), TITLE, IMAGE);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createPageControls( Composite pageContainer ) {
        super.createPageControls(pageContainer);
        updateForProperties();
    }

    /**
     * Method declared on IWorkbenchWizard.
     */
    public void init( IWorkbench workbench,
                      IStructuredSelection currentSelection ) {
        
        this.selection = currentSelection;

        List selectedResources = IDE.computeSelectedResources(currentSelection);
        if (!selectedResources.isEmpty()) {
            this.selection = new StructuredSelection(selectedResources);
        }
        
        if( !ModelerUiViewUtils.workspaceHasOpenModelProjects() ) {
        	IProject newProject = ModelerUiViewUtils.queryUserToCreateModelProject();
        	
        	if( newProject != null ) {
        		this.selection = new StructuredSelection(newProject);
        	}
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

        // construct pages
        SELECT_WSDL_PAGE : {
	        this.selectWsdlPage = new WsdlDefinitionPage(this.importManager, this);
	        this.selectWsdlPage.setPageComplete(false);
	        addPage(this.selectWsdlPage);
        }
        
        SELECT_OPERATIONS_PAGE : {
        	this.modelDefinitionPage = new ModelDefinitionPage(this.importManager, this);
        	this.modelDefinitionPage.setPageComplete(false);
        	addPage(this.modelDefinitionPage);
        }
        
        OPERATIONS_DETAILS_PAGE : {
        	this.operationsDetailsPage = new OperationsDetailsPage(this.importManager);
        	this.operationsDetailsPage.setPageComplete(false);
        	addPage(this.operationsDetailsPage);
        }

        // give the WSDL selection page the current workspace selection
        ((WsdlDefinitionPage)this.selectWsdlPage).setInitialSelection(theSelection);
    }

    /**
     * @see org.eclipse.jface.wizard.IWizard#performFinish()
     * @since 4.0
     */
    @Override
    public boolean finish() {
    	if( !this.importManager.doGenerateDefaultProcedures() ) {
    		runFinishWithDetails();
    		return true;
    	}
    	
        boolean result = false;

        // Save object selections from previous page
        final IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run( final IProgressMonitor monitor ) throws InvocationTargetException {
                // Wrap in transaction so it doesn't result in Significant Undoable
                boolean started = ModelerCore.startTxn(false, false, "Generate Models From WSDL", //$NON-NLS-1$
                                                       new Object());
                boolean succeeded = false;
                try {
                	runFinishAsDefault(monitor);
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
                final IStatus iteStatus = new Status(IStatus.ERROR, ModelGeneratorWsdlUiConstants.PLUGIN_ID, IStatus.ERROR, Messages.ImportWsdlSoapWizard_importError_msg, t);
                ErrorDialog.openError(this.getShell(), 
                		Messages.ImportWsdlSoapWizard_importError_title, 
                		Messages.ImportWsdlSoapWizard_importError_msg, iteStatus);
                t.printStackTrace(System.err);
            } else {
                final IStatus status = new Status(IStatus.ERROR, ModelGeneratorWsdlUiConstants.PLUGIN_ID, IStatus.ERROR, Messages.ImportWsdlSoapWizard_importError_msg, err); 
                ErrorDialog.openError(this.getShell(),
                		Messages.ImportWsdlSoapWizard_importError_title, 
                		Messages.ImportWsdlSoapWizard_importError_msg, status);
                err.printStackTrace(System.err);
            }
        } finally {
            dispose();
        }

        return result;
    }

    private void runFinishAsDefault(final IProgressMonitor theMonitor ) throws ModelGenerationException, SchemaProcessingException, ModelBuildingException {
        // Target Model Name

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
    
    private void runFinishWithDetails( ) {
    	UiUtil.runInSwtThread(new Runnable() {
			@Override
			public void run() {
				ImportWsdlProcessor processor = new ImportWsdlProcessor(importManager, Display.getCurrent().getActiveShell());
				processor.execute();
			}
		}, false);
    }
    
    public void notifyManagerChanged() {
    	// First, check the importManager's doGenerateDefaultProcedures() method.
    	
    	boolean doGenerate = this.importManager.doGenerateDefaultProcedures();
    	if( doGenerate ) {
    		// Remove page 3
    		removePage(this.operationsDetailsPage);
    	} else {
    		addPage(this.operationsDetailsPage);
    	}
    }
    
    @Override
    public void setProperties(Properties props) {
    	this.importManager.setDesignerProperties(props);
    }

    protected void updateForProperties() {
        Properties designerProperties = this.importManager.getDesignerProperties();
        if (designerProperties == null) {
    		return;
    	}
    	
        // Check for sources and views folders in Property Definitions
    	if( this.importManager.getSourceModelLocation() == null) {	
    		IContainer project = DesignerPropertiesUtil.getProject(designerProperties);
            IContainer srcResource = DesignerPropertiesUtil.getSourcesFolder(designerProperties);
            if (srcResource != null) {
                this.importManager.setSourceModelLocation(srcResource);
            } else if( project != null ) {
            	this.importManager.setSourceModelLocation(project);
            }
    	}
    	
        // Check for sources and views folders in Property Definitions
    	if( this.importManager.getViewModelLocation() == null) {
    		
    		IContainer project = DesignerPropertiesUtil.getProject(designerProperties);
            IContainer viewResource = DesignerPropertiesUtil.getViewsFolder(designerProperties);
            if (viewResource != null) {
                this.importManager.setViewModelLocation(viewResource);
            } else if( project != null ) {
            	this.importManager.setViewModelLocation(project);
            }
    	}
    	
    	if( this.importManager.getConnectionProfile() == null ) {
            // check for Connection Profile in property definitions
            String profileName = DesignerPropertiesUtil.getConnectionProfileName(designerProperties);
    		if( profileName != null && !profileName.isEmpty() ) {
    			// Select profile
    			selectWsdlPage.selectConnectionProfile(profileName);
    		}
    	}
    }
}