/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views.status;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiConstants.COMMAND_IDS;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.Messages;
import org.teiid.designer.advisor.ui.actions.AdvisorActionFactory;
import org.teiid.designer.advisor.ui.actions.NewModelAction;
import org.teiid.designer.advisor.ui.actions.ToggleAutoBuildAction;
import org.teiid.designer.advisor.ui.core.IAdvisorActionHandler;
import org.teiid.designer.advisor.ui.core.InfoPopAction;
import org.teiid.designer.advisor.ui.core.status.AdvisorStatus;
import org.teiid.designer.advisor.ui.util.DSPPluginImageHelper;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * 
 */
public class StatusActionHandler implements IAdvisorActionHandler, AdvisorUiConstants.Groups {

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
    private InfoPopAction editVdbIPAction;
    private InfoPopAction executeVdbIPAction;

    private ModelProjectStatus status;

    /**
     * 
     */
    public StatusActionHandler() {
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
        toggleAutoBuildAction.setText(Messages.ToggleAutoBuild);
        this.toggleAutoBuildIPAction = new InfoPopAction(toggleAutoBuildAction,InfoPopAction.TYPE_DO, Messages.ToggleAutoBuild, imageHelper.BUILD_IMAGE);

        CONNECTION_ACTIONS : {
	        IAction openDSEAction = new Action() {
	            @Override
	            public void run() {
	            	UiUtil.showView(AdvisorUiConstants.VIEW_IDS.DATA_SOURCE_EXPLORER);
	            }
	        };
	         
	        openDSEAction.setText(Messages.OpenDatatoolsDataSourceExplorer);
	        openDSEAction.setToolTipText(Messages.OpenDatatoolsDataSourceExplorer_tooltip);
	        openDSEAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.DATA_SOURCE_EXPLORER_VIEW));
	
	        this.openDSEIPAction = new InfoPopAction(openDSEAction, InfoPopAction.TYPE_DO,
	                                                           Messages.Options_Action_OpenDSEAction_description,
	                                                           imageHelper.OPEN_DATA_SOURCE_EXPLORER_IMAGE);
	 
	        IAction newJdbcCPAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.CREATE_CONNECTION_JDBC, true);
	            }
	        };
	        newJdbcCPAction.setText(Messages.CreateJdbcConnection);
	        newJdbcCPAction.setToolTipText(Messages.CreateJdbcConnection);
	        
	        this.newJdbcCPIPAction = new InfoPopAction(newJdbcCPAction, 
	        			InfoPopAction.TYPE_DO, Messages.CreateJdbcConnection, imageHelper.NEW_CONNECTION_PROFILE_IMAGE);
	        
	        IAction newSalesforceCPAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.CREATE_CONNECTION_JDBC, true);
	            }
	        };
	        newSalesforceCPAction.setText(Messages.CreateSalesforceConnection);
	        newSalesforceCPAction.setToolTipText(Messages.CreateSalesforceConnection);
	        
	        this.newSFCPIPAction = new InfoPopAction(newSalesforceCPAction,
	    			InfoPopAction.TYPE_DO, Messages.CreateSalesforceConnection, imageHelper.NEW_CONNECTION_PROFILE_IMAGE);
	        
	        IAction newWSSoapCPAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA, true);
	            }
	        };
	        newWSSoapCPAction.setText(Messages.CreateOdaWebServicesConnection);
	        newWSSoapCPAction.setToolTipText(Messages.CreateOdaWebServicesConnection);
	        
	        this.newWSSoapIPAction = new InfoPopAction(newWSSoapCPAction,
	    			InfoPopAction.TYPE_DO, Messages.CreateOdaWebServicesConnection, imageHelper.NEW_CONNECTION_PROFILE_IMAGE);

        }
        // --------------------------------------------------------------------------
        // show problems:

        IAction delegateOpenProblemsViewAction = new Action() {
            @Override
            public void run() {
            	UiUtil.showView(AdvisorUiConstants.VIEW_IDS.PROBLEMS_VIEW);
            }
        };
        delegateOpenProblemsViewAction.setText(Messages.Options_Action_OpenProblemsView_description);
        delegateOpenProblemsViewAction.setToolTipText(Messages.Options_Action_OpenProblemsView_description);
        delegateOpenProblemsViewAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.CONFIGURATION_MANAGER_VIEW));

        this.showProblemsViewIPAction = new InfoPopAction(delegateOpenProblemsViewAction, InfoPopAction.TYPE_FIX,
                                                          Messages.Options_Action_OpenProblemsView_description,
                                                          imageHelper.PROBLEMS_VIEW_IMAGE);
        IMPORT_ACTIONS : {
	        //        
	        // =========>>>> JDBC
	        // 
	        IAction delegateImportJdbcAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_JDBC, true);
	            }
	        };
	        delegateImportJdbcAction.setText(Messages.Action_ImportJdbc_text);
	        delegateImportJdbcAction.setToolTipText(Messages.Action_ImportJdbc_tooltip);
	        delegateImportJdbcAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importJdbcIPAction = new InfoPopAction(delegateImportJdbcAction, InfoPopAction.TYPE_DO,
	                                               Messages.Options_Action_ImportJDBC_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);
	        //        
	        // =========>>>> DDL
	        // 
	        IAction delegateImportDdlAction = new Action() {
	            @Override
	            public void run() {
	                AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_SALESFORCE, true);
	            }
	        };
	        delegateImportDdlAction.setText(Messages.Action_ImportDdl_text);
	        delegateImportDdlAction.setToolTipText(Messages.Action_ImportDdl_tooltip);
	        delegateImportDdlAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importDdlIPAction = new InfoPopAction(delegateImportDdlAction, InfoPopAction.TYPE_DO,
	                                               Messages.Options_Action_ImportDdl_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);
	        //        
	        // =========>>>> FLAT FILE
	        // 
	        IAction delegateImportFlatFileAction = new Action() {
	            @Override
	            public void run() {
	                AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_SALESFORCE, true);
	            }
	        };
	        delegateImportFlatFileAction.setText(Messages.Action_ImportFlatFile_text);
	        delegateImportFlatFileAction.setToolTipText(Messages.Action_ImportFlatFile_tooltip);
	        delegateImportFlatFileAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importFlatFileIPAction = new InfoPopAction(delegateImportFlatFileAction, InfoPopAction.TYPE_DO,
	                                               Messages.Options_Action_ImportFlatFile_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);
	        
        	//	        
	        // =========>>>> FLAT FILE
	        // 
	        IAction delegateImportXmlFileAction = new Action() {
	            @Override
	            public void run() {
	                AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_SALESFORCE, true);
	            }
	        };
	        delegateImportXmlFileAction.setText(Messages.Action_ImportXmlFile_text);
	        delegateImportXmlFileAction.setToolTipText(Messages.Action_ImportXmlFile_tooltip);
	        delegateImportXmlFileAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importXmlFileIPAction = new InfoPopAction(delegateImportXmlFileAction, InfoPopAction.TYPE_DO,
	                                               Messages.Options_Action_ImportXmlFile_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);
	        //        
	        // =========>>>> Salesforce
	        // 
	        IAction delegateImportSalesforceAction = new Action() {
	            @Override
	            public void run() {
	                AdvisorActionFactory.executeAction(COMMAND_IDS.IMPORT_SALESFORCE, true);
	            }
	        };
	        delegateImportSalesforceAction.setText(Messages.Action_ImportSalesforce_text);
	        delegateImportSalesforceAction.setToolTipText(Messages.Action_ImportSalesforce_tooltip);
	        delegateImportSalesforceAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_JDBC));
	
	        importSalesforceIPAction = new InfoPopAction(delegateImportSalesforceAction, InfoPopAction.TYPE_DO,
	                                               Messages.Options_Action_ImportSalesforce_description,
	                                               imageHelper.IMPORT_JDBC_IMAGE);

        }
        // --------------------------------------------------------------------------
        // XSD import:
        IAction delegateImportXsdAction = new Action() {
            @Override
            public void run() {
                ModelerUiViewUtils.launchWizard("xsdFileSystemImportWizard", new StructuredSelection(), true); //$NON-NLS-1$
            }
        };
        delegateImportXsdAction.setText(Messages.Action_ImportXsd_text);
        delegateImportXsdAction.setToolTipText(Messages.Action_ImportXsd_tooltip);
        delegateImportXsdAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.IMPORT_XSD));

        importXsdIPAction = new InfoPopAction(delegateImportXsdAction, InfoPopAction.TYPE_DO,
                                              Messages.Options_Action_ImportXsd_description, imageHelper.IMPORT_XSD_IMAGE);

        VDB_AND_EXECUTION : {
	        // --------------------------------------------------------------------------
	        // New VDB
	        // --------------------------------------------------------------------------
	        IAction delegateNewVdbAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.CREATE_VDB, true);
	            }
	        };
	        delegateNewVdbAction.setText(Messages.Action_NewVdb_text);
	        delegateNewVdbAction.setToolTipText(Messages.Action_NewVdb_tooltip);
	        delegateNewVdbAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.NEW_VDB));
	        newVdbIPAction = new InfoPopAction(delegateNewVdbAction, InfoPopAction.TYPE_DO,
                                           Messages.Options_Action_NewVdb_description, imageHelper.NEW_VDB_IMAGE);
	        
	        // --------------------------------------------------------------------------
	        // Edit VDB
	        // --------------------------------------------------------------------------
	        IAction delegateEditVdbAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.EDIT_VDB, true);
	            }
	        };
	        delegateEditVdbAction.setText(Messages.Action_EditVdb_text);
	        delegateEditVdbAction.setToolTipText(Messages.Action_EditVdb_tooltip);
	        delegateEditVdbAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.EDIT_VDB_ACTION));
	        editVdbIPAction = new InfoPopAction(delegateEditVdbAction, InfoPopAction.TYPE_DO,
                                           Messages.Options_Action_EditVdb_description, imageHelper.EDIT_VDB_IMAGE);
	        
	        // --------------------------------------------------------------------------
	        // Execute VDB
	        // --------------------------------------------------------------------------
	        IAction delegateExecuteVdbAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.EXECUTE_VDB, true);
	            }
	        };
	        delegateExecuteVdbAction.setText(Messages.Action_ExecuteVdb_text);
	        delegateExecuteVdbAction.setToolTipText(Messages.Action_ExecuteVdb_tooltip);
	        delegateExecuteVdbAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.EXECUTE_VDB_ACTION));
	        executeVdbIPAction = new InfoPopAction(delegateExecuteVdbAction, InfoPopAction.TYPE_FIX,
                                           Messages.Options_Action_ExecuteVdb_description, imageHelper.EXECUTE_VDB_IMAGE);
	        
	        // --------------------------------------------------------------------------
	        // Execute VDB
	        // --------------------------------------------------------------------------
	        IAction delegatePreviewDataAction = new Action() {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(COMMAND_IDS.PREVIEW_DATA, true);
	            }
	        };
	        delegatePreviewDataAction.setText(Messages.Action_PreviewData_text);
	        delegatePreviewDataAction.setToolTipText(Messages.Action_PreviewData_tooltip);
	        delegatePreviewDataAction.setImageDescriptor(AdvisorUiPlugin.getDefault().getImageDescriptor(AdvisorUiConstants.Images.PREVIEW_DATA));
	        previewDataIPAction = new InfoPopAction(delegatePreviewDataAction, InfoPopAction.TYPE_OTHER,
                                           Messages.Options_Action_PreviewData_description, imageHelper.PREVIEW_DATA_IMAGE);

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
        delegateNewRelationalSourceModelAction.setText(Messages.Action_NewSourceModel_text);
        delegateNewRelationalSourceModelAction.setToolTipText(Messages.Action_NewSourceModel_tooltip);
        newRelationalSourceModelIPAction = new InfoPopAction(delegateNewRelationalSourceModelAction, InfoPopAction.TYPE_DO,
                                                             Messages.Options_Action_NewRelationalSourceModel_description,
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
        delegateNewRelationalViewModelAction.setText(Messages.Action_NewViewModel_text);
        delegateNewRelationalViewModelAction.setToolTipText(Messages.Action_NewViewModel_tooltip);

        newRelationalViewModelIPAction = new InfoPopAction(delegateNewRelationalViewModelAction, InfoPopAction.TYPE_DO,
                                                           Messages.Options_Action_NewRelationalViewModel_description,
                                                           imageHelper.NEW_MODEL_IMAGE);
    }

    public InfoPopAction[] getActions( int groupType ) {
        Collection actions = new ArrayList();
        boolean hasErrors = false;

        switch (groupType) {
            case GROUP_VDBS: {
                actions.add(newVdbIPAction);
                actions.add(editVdbIPAction);
                actions.add(executeVdbIPAction);
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
                String desc = Messages.Options_Action_TurnAutobiuldOn_description;
                if (ResourcesPlugin.getWorkspace().isAutoBuilding()) {
                    desc = Messages.Options_Action_TurnAutobiuldOff_description;
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
                
            case GROUP_TEST: {
            	actions.add(previewDataIPAction);
            	actions.add(executeVdbIPAction);
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
