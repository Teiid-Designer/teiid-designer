/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class DSPAdvisorI18n extends NLS {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    // --------------------------------------------------------------------------------------------
    // DSPAdvisorPanel
    // --------------------------------------------------------------------------------------------

    public static String StatusSectionTitlePrefix;
    public static String StatusSectionTitle_NoProjectSelected;
    public static String StatusSectionDefaultDescription;
    public static String StatusSectionHelpTooltip;
    public static String AutovalidateOffMessage;
    public static String NoProjectMessage;
    //
    // Status Categories
    //
    public static String ProjectStatusLabel;
    public static String ModelValidationLabel;
    public static String ModelValidationOffLabel;
    public static String SourcesLabel;
    public static String ConnectionFactoriesLabel;
    public static String ViewsLabel;
    public static String XmlViewMappingsLabel;
    public static String XmlSchemaLabel;
    public static String WebServicesLabel;
    public static String VDBsLabel;

    // Misc
    public static String Action_PreviewWsdlLabel;
    public static String Action_FixLabel;

    // Actions & Options
    public static String Action_ExecuteVdb_text;
    public static String Action_ExecuteVdb_tooltip;
    public static String Action_NewVdb_text;
    public static String Action_NewVdb_tooltip;
    public static String Action_SaveVdb_text;
    public static String Action_SaveVdb_tooltip;
    public static String Action_NewSourceModel_text;
    public static String Action_NewSourceModel_tooltip;
    public static String Action_NewViewModel_text;
    public static String Action_NewViewModel_tooltip;
    public static String Action_NewXmlViewModel_text;
    public static String Action_NewXmlViewModel_tooltip;
    public static String Action_NewWebServiceModel_text;
    public static String Action_NewWebServiceModel_tooltip;
    public static String Action_ImportJdbc_text;
    public static String Action_ImportJdbc_tooltip;
    public static String Action_ImportXsd_text;
    public static String Action_ImportXsd_tooltip;
    public static String Action_PreviewData_text;
    public static String Action_PreviewData_tooltip;

    public static String Options_Action_OpenProblemsView_description;
    public static String Options_Action_ImportJDBC_description;
    public static String Options_Action_ImportXsd_description;
    public static String Options_Action_PreviewData_description;
    public static String Options_Action_ExecuteVdb_description;
    public static String Options_Action_NewVdb_description;
    public static String Options_Action_NewRelationalSourceModel_description;
    public static String Options_Action_NewRelationalViewModel_description;
    public static String Options_Action_NewXmlViewModel_description;
    public static String Options_Action_NewWebServiceModel_description;
    public static String Options_Action_TurnAutobiuldOn_description;
    public static String Options_Action_TurnAutobiuldOff_description;
    
    public static String Action_ImportDdl_text;
    public static String Action_ImportDdl_tooltip;
    public static String Options_Action_ImportDdl_description;
    public static String Action_ImportSalesforce_text;
    public static String Action_ImportSalesforce_tooltip;
    public static String Options_Action_ImportSalesforce_description;
    public static String Action_ImportWsdlSource_text;
    public static String Action_ImportWsdlSource_tooltip;
    public static String Options_Action_ImportWsdlSource_description;
    public static String Action_ImportWsdlWS_text;
    public static String Action_ImportWsdlWS_tooltip;
    public static String Options_Action_ImportWsdlWS_description;
    public static String Action_ImportFlatFile_text;
    public static String Action_ImportFlatFile_tooltip;
    public static String Options_Action_ImportFlatFile_description;
    public static String Action_ImportXmlFile_text;
    public static String Action_ImportXmlFile_tooltip;
    public static String Options_Action_ImportXmlFile_description;
    public static String Options_Action_OpenDSEAction_description;
    

    // -------------------------------------------------------------------------------------------
    // WebServiceValidationConstants
    // --------------------------------------------------------------------------------------------

    public static String Status_ModelProblems_OK;
    public static String Status_ModelProblems_NoModels;
    public static String Status_ModelProblems_Errors;
    public static String Status_Sources_OK;
    public static String Status_Sources_NoModels;
    public static String Status_Sources_Errors;
    public static String Status_Bindings_OK;
    public static String Status_Bindings_NoneExist;
    public static String Status_Bindings_Errors;
    public static String Status_Views_OK;
    public static String Status_Views_NoModels;
    public static String Status_Views_Errors;
    public static String Status_XmlSchemas_OK;
    public static String Status_XmlSchemas_NoModels;
    public static String Status_XmlSchemas_Errors;
    public static String Status_XmlMappings_OK;
    public static String Status_XmlMappings_NoModels;
    public static String Status_XmlMappings_Errors;
    public static String Status_WebServices_OK;
    public static String Status_WebServices_NoModels;
    public static String Status_WebServices_Errors;
    public static String Status_PreviewWsdl_OK;
    public static String Status_PreviewWsdl_Incomplete;
    public static String Status_PreviewWsdl_Errors;
    public static String Status_ClickForActions;
    public static String Status_ClickForPreviewWsdlAction;
    public static String Status_VDBs_OK;
    public static String Status_VDBs_NoVDBs;
    public static String Status_VDBs_Errors;
    public static String Status_Project_Not_Selected;
    public static String Status_Project_OK;
    public static String Status_Project_Incomplete;
    public static String Status_Project_Errors;

    // --------------------------------------------------------------------------------------------
    // AdvisorFixDialog contants
    // --------------------------------------------------------------------------------------------

    public static String AdvisorFixDialog_Message_NoActions;
    public static String AdvisorFixDialog_Message_AvailableActions;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    // /////////////////////////////////////////////////////////////////////////////////////////////

    static {
        final String BUNDLE_NAME = "org.teiid.designer.advisor.ui.views.dspAdvisorI18n";//$NON-NLS-1$

        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, DSPAdvisorI18n.class);
    }

}
