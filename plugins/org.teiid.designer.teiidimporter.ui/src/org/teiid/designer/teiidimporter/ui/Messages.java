/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.teiidimporter.ui;

import org.eclipse.osgi.util.NLS;

/**
 * @since 8.1
 */
@SuppressWarnings("javadoc")
public class Messages extends NLS {

    public static String TeiidImportWizard_title;
    public static String TeiidImportWizard_DeployDriverMsg;
    
    public static String TeiidImportManager_ImportVDBName;
    public static String TeiidImportManager_deployVdbInterruptedMsg;
    public static String TeiidImportManager_deployVdbMsg;
    public static String TeiidImportManager_deployVdbNoTimeoutMsg;
    public static String TeiidImportManager_ConfirmDialgTitle;
    public static String TeiidImportManager_ContinueImportMsg;
    public static String TeiidImportManager_ImportingMsg;
    public static String TeiidImportManager_ImportingDDLMsg;
    public static String TeiidImportManager_getDdlErrorMsg;
    
    public static String errorNameInvalid;
    
    public static String createDataSourcePanel_name;
    public static String createDataSourcePanel_jndiName;
    public static String createDataSourcePanel_driver;
    public static String createDataSourcePanel_driversGroupTxt;
    public static String createDataSourcePanel_dataSourcePropertiesGroupTxt;
    public static String createDataSourcePanelErrorNameEmpty;
    public static String createDataSourcePanelOk;
    public static String createDataSourcePanelErrorNameExists;
    public static String editDataSourcePanelEnterChanges;
    
    public static String dataSourcePropertiesPanel_nameColText;
    public static String dataSourcePropertiesPanel_valueColText;
    public static String dataSourcePropertiesPanel_resetButton;
    public static String dataSourcePropertiesPanel_resetTooltip;
    public static String dataSourcePropertiesPanel_applyButton;
    public static String dataSourcePropertiesPanel_applyTooltip;
    public static String dataSourcePropertiesPanel_invalidPropertyMsg;
    public static String dataSourcePropertiesPanel_applyPropertyChangesMsg;
    public static String dataSourcePropertiesPanel_validPropertyTooltip;
    public static String dataSourcePropertiesPanel_invalidPropertyTooltip;
    public static String dataSourcePropertiesPanelOk;
    public static String dataSourcePropertiesPanel_requiredLabel;

    public static String dataSourcePanel_newButtonText;
    public static String dataSourcePanel_deleteButtonText;
    public static String dataSourcePanel_editButtonText;
    public static String dataSourcePanel_copyButtonText;
    public static String dataSourcePanel_newButtonTooltip;
    public static String dataSourcePanel_deleteButtonTooltip;
    public static String dataSourcePanel_editButtonTooltip;
    public static String dataSourcePanel_copyButtonTooltip;
    public static String dataSourcePanel_nameColText;
    public static String dataSourcePanel_jndiNameColText;
    public static String dataSourcePanel_typeColText;
    public static String dataSourcePanel_createErrorTitle;
    public static String dataSourcePanel_deleteErrorTitle;
    public static String dataSourcePanel_editErrorTitle;
    public static String dataSourcePanel_copyErrorTitle;
    public static String dataSourcePanel_deleteSourceDialogTitle;
    public static String dataSourcePanel_deleteSourceDialogMsg;
    public static String dataSourcePanel_driverTooltipPrefix;
    
    public static String dataSourceDriversPanelAddHyperlinkTxt;
    public static String dataSourceDriversPanelErrorNoSelection;
    public static String dataSourceDriversPanelAddDialogErrorTitle;
    public static String dataSourceDriversPanelAddDialogErrorDuplicateNameMsg;
    public static String dataSourceDriversPanelItemTooltip;
    public static String dataSourceDriversPanel_colText;
    public static String dataSourceDriversPanelOk;
    
    public static String createDataSourceDialog_title;
    public static String editDataSourceDialog_title;
    public static String copyDataSourceDialog_title;
    public static String copyDataSourceDialogErrorNameEmpty;
    public static String copyDataSourceDialogErrorNameExists;
    public static String copyDataSourceDialogOk;

    public static String selectDataSourcePage_InvalidServerMsg;
    public static String selectDataSourcePage_title;
    public static String selectDataSourcePage_help;
    public static String selectDataSourcePage_dataSourceGroupText;
    public static String selectDataSourcePage_dataSourcePropertiesGroupText;
    public static String selectDataSourcePage_NoSourceSelectedMsg;
    public static String selectDataSourcePage_ConsiderJDBCImporterForSourceTypeMsg;
    
    public static String SelectTargetPage_title;
    public static String SelectTargetPage_TgtModelDefnTab;
    public static String SelectTargetPage_AdvancedTab;
    public static String SelectTargetPage_FilterRedundantUCsCB_Label;
    public static String SelectTargetPage_FilterRedundantUCsCB_ToolTip;
    public static String SelectTargetPage_CreateConnectionProfileCB_Label;
    public static String SelectTargetPage_CreateConnectionProfileCB_ToolTip;
    public static String SelectTargetPage_Location;
    public static String SelectTargetPage_Browse;
    public static String SelectTargetPage_Name;
    public static String SelectTargetPage_ModelStatus;
    public static String SelectTargetPage_EnterModelNameMsg;
    public static String SelectTargetPage_SrcModelUndefined;
    public static String SelectTargetPage_SrcModelSelected;
    public static String SelectTargetPage_NoOpenProjMsg;
    public static String SelectTargetPage_SrcLocationNotSpecified;
    public static String SelectTargetPage_ModelExistsWithThisNameMsg;
    public static String SelectTargetPage_dynamic_vdb_text;
    public static String SelectTargetPage_dynamic_vdb_tooltip;
    public static String SelectTargetPage_defaultServerPrefix;
    public static String SelectTargetPage_TimeoutLabelText;
    public static String SelectTargetPage_TimeoutTooltip;
    public static String SelectTargetPage_TimeoutEmptyMsg;
    public static String SelectTargetPage_TimeoutTextNotParsableMsg;
    public static String SelectTargetPage_TimeoutLessThanMinAllowedMsg;
    public static String SelectTargetPage_TimeoutGreaterThanMaxAllowedMsg;
    
    public static String SelectTranslatorPage_ConfirmDeleteTitle;
    public static String SelectTranslatorPage_ConfirmDeleteMsg;
    
    public static String SelectTranslatorPage_title;
    public static String SelectTranslatorPage_dsNameLabel;
    public static String SelectTranslatorPage_dsTypeLabel;
    public static String SelectTranslatorPage_translatorLabel;
    public static String SelectTranslatorPage_SrcDefnGroup;
    public static String SelectTranslatorPage_NoDataSourceNameMsg;
    public static String SelectTranslatorPage_NoDataSourceDriverMsg;
    public static String SelectTranslatorPage_NoTranslatorMsg;
    public static String SelectTranslatorPage_importPropertiesLabel;

    public static String ShowDDLPage_title;
    public static String ShowDDLPage_DDLContentsGroup;
    public static String ShowDDLPage_exportDDLToFileSystemButton;
    public static String ShowDDLPage_exportDDLToFileSystemButtonTooltip;
    public static String ShowDDLPage_exportDDLToWorkspaceButton;
    public static String ShowDDLPage_exportDDLToWorkspaceButtonTooltip;
    public static String ShowDDLPage_vdbDeploymentErrorMsg;
    public static String ShowDDLPage_vdbDeploymentCheckServerLogMsg;
    public static String ShowDDLPage_exportDDLDialogTitle;
    public static String ShowDDLPage_exportDDLDialogDefaultFileName;
    public static String ShowDDLPage_exportDDLDialogDefaultFileExt;
    public static String ShowDDLPage_exportDDLDialogExportErrorMsg;
    public static String ShowDDLPage_exportDDLDialogExportToWorkspaceErrorMsg;
    
    public static String dataSourceManager_createOk;
    public static String dataSourceManager_createInterruptedMsg;
    public static String dataSourceManager_deleteOk;
    public static String dataSourceManager_deleteInterruptedMsg;
    public static String dataSourceManager_deleteCreateOk;
    public static String dataSourceManager_deleteCreateInterruptedMsg;
    public static String dataSourceManager_copyOk;
    public static String dataSourceManager_copyInterruptedMsg;

    public static String ImportPropertiesPanel_groupTitle;
    public static String ImportPropertiesPanel_name;
    public static String ImportPropertiesPanel_value;
    public static String ImportPropertiesPanel_addNewPropertyButton_tooltip;
    public static String ImportPropertiesPanel_removePropertyButton_tooltip;

    public static String AddGeneralPropertyDialog_title;
    public static String AddGeneralPropertyDialog_message;
    public static String AddGeneralPropertyDialog_lblName_text;
    public static String AddGeneralPropertyDialog_txtName_toolTip;
    public static String AddGeneralPropertyDialog_lblValue_text;
    public static String AddGeneralPropertyDialog_txtValue_toolTip;
    public static String AddGeneralPropertyDialog_customPropertyAlreadyExists;
    public static String AddGeneralPropertyDialog_emptyPropertyName;
    public static String AddGeneralPropertyDialog_invalidPropertyName;
    public static String AddGeneralPropertyDialog_emptyPropertyValue;
    
    public static String ExportDDLToWorkspaceDialog_title;
    public static String ExportDDLToWorkspaceDialog_errorSelectLocation;
    public static String ExportDDLToWorkspaceDialog_errorEnterFileName;
    public static String ExportDDLToWorkspaceDialog_errorCouldNotGetMembers;
    public static String ExportDDLToWorkspaceDialog_locationLabel;
    public static String ExportDDLToWorkspaceDialog_fileNameLabel;
    public static String ExportDDLToWorkspaceDialog_browseButton;
    public static String ExportDDLToWorkspaceDialog_defaultFileName;
    public static String ExportDDLToWorkspaceDialog_Ok;
    
    static {
        NLS.initializeMessages("org.teiid.designer.teiidimporter.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
