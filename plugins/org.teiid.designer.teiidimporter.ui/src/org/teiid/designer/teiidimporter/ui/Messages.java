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
    public static String TeiidImportManager_ConfirmDialgTitle;
    public static String TeiidImportManager_ContinueImportMsg;
    public static String TeiidImportManager_ImportingMsg;
    public static String TeiidImportManager_ImportingDDLMsg;
    public static String TeiidImportManager_getDdlErrorMsg;
    
    public static String errorNameInvalid;
    
    public static String createDataSourcePanel_name;
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
    public static String dataSourcePropertiesPanel_invalidPropertyMsg;
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
    public static String dataSourcePanel_typeColText;
    public static String dataSourcePanel_createErrorTitle;
    public static String dataSourcePanel_deleteErrorTitle;
    public static String dataSourcePanel_editErrorTitle;
    public static String dataSourcePanel_copyErrorTitle;
    public static String dataSourcePanel_deleteSourceDialogTitle;
    public static String dataSourcePanel_deleteSourceDialogMsg;
    public static String dataSourcePanel_driverTooltipPrefix;
    public static String dataSourcePanel_dataSourceDeployErrorTryRestartMsg;
    
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
    public static String selectDataSourcePage_dataSourceGroupText;
    public static String selectDataSourcePage_dataSourcePropertiesGroupText;
    public static String selectDataSourcePage_NoSourceSelectedMsg;

    public static String SelectTranslatorPage_title;
    public static String SelectTranslatorPage_dsNameLabel;
    public static String SelectTranslatorPage_dsTypeLabel;
    public static String SelectTranslatorPage_translatorLabel;
    public static String SelectTranslatorPage_SrcModelDefnGroup;
    public static String SelectTranslatorPage_Location;
    public static String SelectTranslatorPage_Browse;
    public static String SelectTranslatorPage_Name;
    public static String SelectTranslatorPage_ModelStatus;
    public static String SelectTranslatorPage_SrcModelUndefined;
    public static String SelectTranslatorPage_SrcModelSelected;
    public static String SelectTranslatorPage_SelectTargetModelTitle;
    public static String SelectTranslatorPage_SelectTargetModelMsg;
    public static String SelectTranslatorPage_NoOpenProjMsg;
    public static String SelectTranslatorPage_SrcLocationNotSpecified;
    public static String SelectTranslatorPage_ConnProfileInTargetIncompatible;
    public static String SelectTranslatorPage_ConfirmDeleteTitle;
    public static String SelectTranslatorPage_ConfirmDeleteMsg;
    public static String SelectTranslatorPage_NoDataSourceNameMsg;
    public static String SelectTranslatorPage_NoDataSourceDriverMsg;
    public static String SelectTranslatorPage_NoTranslatorMsg;

    public static String ShowDDLPage_title;
    public static String ShowDDLPage_DDLContentsGroup;
    public static String ShowDDLPage_exportDDLButton;
    public static String ShowDDLPage_exportDDLButtonTooltip;
    public static String ShowDDLPage_vdbDeploymentErrorMsg;
    public static String ShowDDLPage_exportDDLDialogTitle;
    public static String ShowDDLPage_exportDDLDialogDefaultFileName;
    public static String ShowDDLPage_exportDDLDialogDefaultFileExt;
    public static String ShowDDLPage_exportDDLDialogExportErrorMsg;
    
    public static String dataSourceManager_createOk;
    public static String dataSourceManager_createInterruptedMsg;
    public static String dataSourceManager_deleteOk;
    public static String dataSourceManager_deleteInterruptedMsg;
    public static String dataSourceManager_deleteCreateOk;
    public static String dataSourceManager_deleteCreateInterruptedMsg;
    public static String dataSourceManager_copyOk;
    public static String dataSourceManager_copyInterruptedMsg;
    
    static {
        NLS.initializeMessages("org.teiid.designer.teiidimporter.ui.messages", Messages.class); //$NON-NLS-1$
    }
}
