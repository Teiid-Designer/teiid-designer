/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.AdvisorUiConstants.COMMAND_IDS;
import org.teiid.designer.advisor.ui.AdvisorUiConstants.CONNECTION_PROFILE_IDS;
import org.teiid.designer.advisor.ui.actions.AdvisorActionFactory;
import org.teiid.designer.advisor.ui.actions.NewConnectionProfileAction;
import org.teiid.designer.advisor.ui.actions.NewModelAction;
import org.teiid.designer.advisor.ui.actions.ToggleAutoBuildAction;
import org.teiid.designer.advisor.ui.core.IAdvisorActionHandler;
import org.teiid.designer.advisor.ui.core.InfoPopAction;
import org.teiid.designer.advisor.ui.core.status.AdvisorStatus;
import org.teiid.designer.advisor.ui.util.DSPPluginImageHelper;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * 
 */
public class DSPAdvisorActionHandler implements IAdvisorActionHandler, AdvisorUiConstants.Groups {

    final DSPPluginImageHelper imageHelper = AdvisorUiPlugin.getImageHelper();

    private static final String RELATIONAL_ID = "Relational"; //$NON-NLS-1$

    NewModelAction newRelationalSourceModelAction;
    NewModelAction newRelationalViewModelAction;

    // InfoPopAction's
    
    private InfoPopAction showProblemsViewIPAction;
    
    private InfoPopAction importJdbcIPAction;
    private InfoPopAction importSalesforceIPAction;
    private InfoPopAction importDdlIPAction;
    private InfoPopAction importFlatFileIPAction;
    private InfoPopAction importXmlFileIPAction;
    
    
    private InfoPopAction openDSEIPAction;
    private InfoPopAction newJdbcCPIPAction;
    private InfoPopAction newSFCPIPAction;
    private InfoPopAction newWSSoapIPAction;
    private InfoPopAction importXsdIPAction;
    private InfoPopAction newRelationalSourceModelIPAction;
    private InfoPopAction newRelationalViewModelIPAction;
    private InfoPopAction toggleAutoBuildIPAction;
    
    private InfoPopAction previewDataIPAction;
    
    private InfoPopAction newVdbIPAction;
    private InfoPopAction executeVdbIPAction;

    private ModelProjectStatus status;

    /**
     * 
     */
    public DSPAdvisorActionHandler() {
        super();

        try {
            createActions();
        } catch (CoreException e) {
        	AdvisorUiConstants.UTIL.log(e);
        }

    }

    @SuppressWarnings("unused")
	private void createActions() throws CoreException {

        // --------------------------------------------------------------------------
        // autobuildAction:
        Action toggleAutoBuildAction = new ToggleAutoBuildAction(AdvisorUiPlugin.getDefault().getCurrentWorkbenchWindow());
        toggleAutoBuildAction.setText("Toggle Autobuild");
        this.toggleAutoBuildIPAction = new InfoPopAction(toggleAutoBuildAction,InfoPopAction.TYPE_DO, "Toggle Auto-build", imageHelper.BUILD_IMAGE);

        CONNECTION_ACTIONS : {
	        IAction openDSEAction = new Action() {
	            @Override
	            public void run() {
	            	UiUtil.showView(AdvisorUiConstants.VIEW_IDS.DATA_SOURCE_EXPLORER);
	            }
	        };
	         
	        openDSEAction.setText("Open Data Source Explorer");
	        openDSEAction.setToolTipText("Open Datatools Data Source Explorer View");
	        openDSEAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.DATA_SOURCE_EXPLORER_VIEW));
	
	        this.openDSEIPAction = new InfoPopAction(openDSEAction, InfoPopAction.TYPE_DO,
	                                                           DSPAdvisorI18n.Options_Action_OpenDSEAction_description,
	                                                           imageHelper.OPEN_DATA_SOURCE_EXPLORER_IMAGE);
	 
	        IAction newJdbcCPAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.CREATE_CONNECTION_JDBC);
//	    			INewWizard wiz = (INewWizard) new NewTeiidFilteredCPWizard(CONNECTION_PROFILE_IDS.CATEGORY_JDBC);
	    			
//	    			WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), (Wizard) wiz);
//	    			wizardDialog.setBlockOnOpen(false);
//	    			wizardDialog.open();
	            }
	        };
	        newJdbcCPAction.setText("Create JDBC Connection Profile");
	        newJdbcCPAction.setToolTipText("Create JDBC Connection Profile");
	        
	        this.newJdbcCPIPAction = new InfoPopAction(newJdbcCPAction, 
	        			InfoPopAction.TYPE_DO, "New JDBC Connection", imageHelper.NEW_CONNECTION_PROFILE_IMAGE);
	        
	        IAction newSalesforceCPAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.CREATE_CONNECTION_JDBC);
	            }
	        };
	        newSalesforceCPAction.setText("Create Salesforce Connection Profile");
	        newSalesforceCPAction.setToolTipText("Create Salesforce Connection Profile");
	        
	        this.newSFCPIPAction = new InfoPopAction(newSalesforceCPAction,
	    			InfoPopAction.TYPE_DO, "New Salesforce Connection", imageHelper.NEW_CONNECTION_PROFILE_IMAGE);
	        
	        IAction newWSSoapCPAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA);
	            }
	        };
	        newWSSoapCPAction.setText("Create SOAP Web Service Connection Profile");
	        newWSSoapCPAction.setToolTipText("Create SOAP Web Service Connection Profile");
	        
	        this.newWSSoapIPAction = new InfoPopAction(newWSSoapCPAction,
	    			InfoPopAction.TYPE_DO, "New SOAP WS Connection", imageHelper.NEW_CONNECTION_PROFILE_IMAGE);

        }
        // --------------------------------------------------------------------------
        // show problems:

        IAction delegateOpenProblemsViewAction = new Action() {
            @Override
            public void run() {
            	UiUtil.showView(AdvisorUiConstants.VIEW_IDS.PROBLEMS_VIEW);
            }
        };
        delegateOpenProblemsViewAction.setText("Open Problems View");
        delegateOpenProblemsViewAction.setToolTipText("Open Problems View Tooltip");
        delegateOpenProblemsViewAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.CONFIGURATION_MANAGER_VIEW));

        this.showProblemsViewIPAction = new InfoPopAction(delegateOpenProblemsViewAction, InfoPopAction.TYPE_FIX,
                                                          DSPAdvisorI18n.Options_Action_OpenProblemsView_description,
                                                          imageHelper.PROBLEMS_VIEW_IMAGE);
        IMPORT_ACTIONS : {
	        //        
	        // =========>>>> JDBC
	        // 
	        IAction delegateImportJdbcAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_JDBC);
	            }
	        };
	        delegateImportJdbcAction.setText(DSPAdvisorI18n.Action_ImportJdbc_text);
	        delegateImportJdbcAction.setToolTipText(DSPAdvisorI18n.Action_ImportJdbc_tooltip);
	        delegateImportJdbcAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importJdbcIPAction = new InfoPopAction(delegateImportJdbcAction, InfoPopAction.TYPE_DO,
	                                               DSPAdvisorI18n.Options_Action_ImportJDBC_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);
	        //        
	        // =========>>>> DDL
	        // 
	        IAction delegateImportDdlAction = new Action() {
	            @Override
	            public void run() {
	                AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_SALESFORCE);
	            }
	        };
	        delegateImportDdlAction.setText(DSPAdvisorI18n.Action_ImportDdl_text);
	        delegateImportDdlAction.setToolTipText(DSPAdvisorI18n.Action_ImportDdl_tooltip);
	        delegateImportDdlAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importDdlIPAction = new InfoPopAction(delegateImportDdlAction, InfoPopAction.TYPE_DO,
	                                               DSPAdvisorI18n.Options_Action_ImportDdl_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);
	        //        
	        // =========>>>> FLAT FILE
	        // 
	        IAction delegateImportFlatFileAction = new Action() {
	            @Override
	            public void run() {
	                AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_SALESFORCE);
	            }
	        };
	        delegateImportFlatFileAction.setText(DSPAdvisorI18n.Action_ImportFlatFile_text);
	        delegateImportFlatFileAction.setToolTipText(DSPAdvisorI18n.Action_ImportFlatFile_tooltip);
	        delegateImportFlatFileAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importFlatFileIPAction = new InfoPopAction(delegateImportFlatFileAction, InfoPopAction.TYPE_DO,
	                                               DSPAdvisorI18n.Options_Action_ImportFlatFile_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);
	        
        	//	        
	        // =========>>>> FLAT FILE
	        // 
	        IAction delegateImportXmlFileAction = new Action() {
	            @Override
	            public void run() {
	                AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_SALESFORCE);
	            }
	        };
	        delegateImportXmlFileAction.setText(DSPAdvisorI18n.Action_ImportXmlFile_text);
	        delegateImportXmlFileAction.setToolTipText(DSPAdvisorI18n.Action_ImportXmlFile_tooltip);
	        delegateImportXmlFileAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importXmlFileIPAction = new InfoPopAction(delegateImportXmlFileAction, InfoPopAction.TYPE_DO,
	                                               DSPAdvisorI18n.Options_Action_ImportXmlFile_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);
	        //        
	        // =========>>>> Salesforce
	        // 
	        IAction delegateImportSalesforceAction = new Action() {
	            @Override
	            public void run() {
	                AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_SALESFORCE);
	            }
	        };
	        delegateImportSalesforceAction.setText(DSPAdvisorI18n.Action_ImportSalesforce_text);
	        delegateImportSalesforceAction.setToolTipText(DSPAdvisorI18n.Action_ImportSalesforce_tooltip);
	        delegateImportSalesforceAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importSalesforceIPAction = new InfoPopAction(delegateImportSalesforceAction, InfoPopAction.TYPE_DO,
	                                               DSPAdvisorI18n.Options_Action_ImportSalesforce_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);

        }
        // --------------------------------------------------------------------------
        // XSD import:
        IAction delegateImportXsdAction = new Action() {
            @Override
            public void run() {
                ModelerUiViewUtils.launchWizard("xsdFileSystemImportWizard", new StructuredSelection()); //$NON-NLS-1$
            }
        };
        delegateImportXsdAction.setText(DSPAdvisorI18n.Action_ImportXsd_text);
        delegateImportXsdAction.setToolTipText(DSPAdvisorI18n.Action_ImportXsd_tooltip);
        delegateImportXsdAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_XSD));

        importXsdIPAction = new InfoPopAction(delegateImportXsdAction, InfoPopAction.TYPE_DO,
                                              DSPAdvisorI18n.Options_Action_ImportXsd_description, imageHelper.IMPORT_XSD_IMAGE);

        VDB_AND_EXECUTION : {
	        // --------------------------------------------------------------------------
	        // New VDB
	        // --------------------------------------------------------------------------
	        IAction delegateNewVdbAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.CREATE_VDB);
	            }
	        };
	        delegateNewVdbAction.setText(DSPAdvisorI18n.Action_NewVdb_text);
	        delegateNewVdbAction.setToolTipText(DSPAdvisorI18n.Action_NewVdb_tooltip);
	        delegateNewVdbAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.NEW_VDB));
	        newVdbIPAction = new InfoPopAction(delegateNewVdbAction, InfoPopAction.TYPE_DO,
                                           DSPAdvisorI18n.Options_Action_NewVdb_description, imageHelper.NEW_VDB_IMAGE);
	        
	        // --------------------------------------------------------------------------
	        // Execute VDB
	        // --------------------------------------------------------------------------
	        IAction delegateExecuteVdbAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.EXECUTE_VDB);
	            }
	        };
	        delegateExecuteVdbAction.setText(DSPAdvisorI18n.Action_ExecuteVdb_text);
	        delegateExecuteVdbAction.setToolTipText(DSPAdvisorI18n.Action_ExecuteVdb_tooltip);
	        delegateExecuteVdbAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.EXECUTE_VDB_ACTION));
	        executeVdbIPAction = new InfoPopAction(delegateNewVdbAction, InfoPopAction.TYPE_FIX,
                                           DSPAdvisorI18n.Options_Action_ExecuteVdb_description, imageHelper.EXECUTE_VDB_IMAGE);
	        
	        // --------------------------------------------------------------------------
	        // Execute VDB
	        // --------------------------------------------------------------------------
	        IAction delegatePreviewDataAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.PREVIEW_DATA);
	            }
	        };
	        delegatePreviewDataAction.setText(DSPAdvisorI18n.Action_PreviewData_text);
	        delegatePreviewDataAction.setToolTipText(DSPAdvisorI18n.Action_PreviewData_tooltip);
	        delegatePreviewDataAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.PREVIEW_DATA));
	        previewDataIPAction = new InfoPopAction(delegatePreviewDataAction, InfoPopAction.TYPE_FIX,
                                           DSPAdvisorI18n.Options_Action_PreviewData_description, imageHelper.PREVIEW_DATA_IMAGE);

        }
        // --------------------------------------------------------------------------
        // New Relational Source Model
        // --------------------------------------------------------------------------
        newRelationalSourceModelAction = new NewModelAction();

        IAction delegateNewRelationalSourceModelAction = new Action() {
            @Override
            public void run() {

                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        newRelationalSourceModelAction.setModelType(ModelType.PHYSICAL_LITERAL);
                        newRelationalSourceModelAction.setMetamodelClass(RELATIONAL_ID);
                        newRelationalSourceModelAction.run();
                    }
                });

            }
        };
        delegateNewRelationalSourceModelAction.setText(DSPAdvisorI18n.Action_NewSourceModel_text);
        delegateNewRelationalSourceModelAction.setToolTipText(DSPAdvisorI18n.Action_NewSourceModel_tooltip);
        newRelationalSourceModelIPAction = new InfoPopAction(delegateNewRelationalSourceModelAction, InfoPopAction.TYPE_DO,
                                                             DSPAdvisorI18n.Options_Action_NewRelationalSourceModel_description,
                                                             imageHelper.NEW_MODEL_IMAGE);

        // --------------------------------------------------------------------------
        // New Relational View Model
        // --------------------------------------------------------------------------
        newRelationalViewModelAction = new NewModelAction();
        IAction delegateNewRelationalViewModelAction = new Action() {
            @Override
            public void run() {

                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        newRelationalViewModelAction.setModelType(ModelType.VIRTUAL_LITERAL);
                        newRelationalViewModelAction.setMetamodelClass(RELATIONAL_ID);
                        newRelationalViewModelAction.run();
                    }
                });

            }
        };
        delegateNewRelationalViewModelAction.setText(DSPAdvisorI18n.Action_NewViewModel_text);
        delegateNewRelationalViewModelAction.setToolTipText(DSPAdvisorI18n.Action_NewViewModel_tooltip);

        newRelationalViewModelIPAction = new InfoPopAction(delegateNewRelationalViewModelAction, InfoPopAction.TYPE_DO,
                                                           DSPAdvisorI18n.Options_Action_NewRelationalViewModel_description,
                                                           imageHelper.NEW_MODEL_IMAGE);
    }

    public InfoPopAction[] getActions( int groupType ) {
        Collection actions = new ArrayList();
        boolean hasErrors = false;

        switch (groupType) {
            case GROUP_VDBS: {
                // actions.add(saveVdbIPAction);
                // actions.add(rebuildVdbIPAction);
                actions.add(newVdbIPAction);
            }
                break;

            case GROUP_MODEL_VALIDATION: {
                // For the model validation category, only include "problems" view action.
                if (getStatus() != null && getModelProjectStatus().getModelStatus() != null) {
                    hasErrors = getModelProjectStatus().getModelStatus().getSeverity() == IStatus.ERROR;
                }
//                if (hasErrors) {
                    actions.add(showProblemsViewIPAction);
//                }
                String desc = DSPAdvisorI18n.Options_Action_TurnAutobiuldOn_description;
                if (ResourcesPlugin.getWorkspace().isAutoBuilding()) {
                    desc = DSPAdvisorI18n.Options_Action_TurnAutobiuldOff_description;
                }
                toggleAutoBuildIPAction.setDescription(desc);
                actions.add(toggleAutoBuildIPAction);
            }
                break;

            case GROUP_SOURCES: {
                // For the source models category.
                if (getStatus() != null && getModelProjectStatus().getSourceModelsStatus() != null) {
                    hasErrors = getModelProjectStatus().getSourceModelsStatus().getSeverity() == IStatus.ERROR;
                }
                if (hasErrors) {
                    actions.add(showProblemsViewIPAction);
                }
                actions.add(importJdbcIPAction);
                actions.add(importDdlIPAction);
                actions.add(importSalesforceIPAction);
                actions.add(newRelationalSourceModelIPAction);
                actions.add(previewDataIPAction);
            }
                break;

            case GROUP_VIEWS: {
                // For the source models category.
                if (getStatus() != null && getModelProjectStatus().getViewModelsStatus() != null) {
                    hasErrors = getModelProjectStatus().getViewModelsStatus().getSeverity() == IStatus.ERROR;
                }
                if (hasErrors) {
                    actions.add(showProblemsViewIPAction);
                }
                actions.add(newRelationalViewModelIPAction);
                actions.add(importFlatFileIPAction);
                actions.add(importXmlFileIPAction);
                actions.add(previewDataIPAction);
            }
                break;

            case GROUP_CONNECTIONS: {
            	actions.add(openDSEIPAction);
            	actions.add(newJdbcCPIPAction);
            	actions.add(newSFCPIPAction);
            	actions.add(newWSSoapIPAction);
            }
                break;

            case GROUP_XML_SCHEMAS: {
                // For the xml schema (xsd) models category
                if (getStatus() != null && getModelProjectStatus().getXmlSchemaFilesStatus() != null) {
                    hasErrors = getModelProjectStatus().getXmlSchemaFilesStatus().getSeverity() == IStatus.ERROR;
                }
                if (hasErrors) {
                    actions.add(showProblemsViewIPAction);
                }
                actions.add(importXsdIPAction);
            }
                break;

            default: {

            }
                break;
        }

        InfoPopAction[] ipArray = new InfoPopAction[actions.size()];
        int iCount = 0;
        for (Iterator iter = actions.iterator(); iter.hasNext();) {
            ipArray[iCount++] = (InfoPopAction)iter.next();
        }
        return ipArray;
    }

    /**
     * @return status
     */
    public AdvisorStatus getStatus() {
        return null;
    }

    private ModelProjectStatus getModelProjectStatus() {
        return this.status;
    }

    /**
     * @param status Sets status to the specified value.
     */
    public void setStatus( ModelProjectStatus status ) {
        this.status = status;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IAdvisorActionHandler.AdvisorActionHandler#setStatus(org.teiid.designer.advisor.ui.core.status.advisor.AdvisorStatus)
     */
    @Override
    public void setStatus( AdvisorStatus status ) {
    }

}
