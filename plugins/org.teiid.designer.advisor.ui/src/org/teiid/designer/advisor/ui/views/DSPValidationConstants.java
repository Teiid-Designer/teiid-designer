/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.core.status.AdvisorStatus;

/**
 * 
 */
public interface DSPValidationConstants extends AdvisorUiConstants {
    /**
     * Constants related to extensions, including all extension ID's.
     * 
     * @since 4.0
     */

    interface ERROR_CATAGORIES {
        int COMPLETED_STATE_CODE = 10;
        int MODEL_STATE_CODE = 100;
        int SOURCES_MODELS_EXIST_CODE = 110;
        int CONNECTOR_BINDINGS_STATE_CODE = 120;
        int VIEWS_MODELS_EXIST_CODE = 130;
        int XML_VIEW_MODELS_EXIST_CODE = 140;
        int WEB_SERVICE_MODELS_EXIST_CODE = 150;
        int SCHEMA_MODELS_EXIST_CODE = 160;
        int XML_VIEW_MAPPINGS_CODE = 170;
        int PREVIEW_WSDL_CODE = 180;
        int VDBS_STATE_CODE = 190;
        int NO_PROJECT_CODE = 200;
    }

    interface STATUS_MSGS {

        public static final Status MODEL_PROBLEMS_OK = new Status(IStatus.OK, PLUGIN_ID, ERROR_CATAGORIES.MODEL_STATE_CODE,
                                                                  DSPAdvisorI18n.Status_ModelProblems_OK, null);
        public static final Status NO_MODELS_NO_PROBLEMS = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                      ERROR_CATAGORIES.MODEL_STATE_CODE,
                                                                      DSPAdvisorI18n.Status_ModelProblems_NoModels, null);
        public static final Status MODEL_PROBLEMS_ERROR = new Status(IStatus.ERROR, PLUGIN_ID, ERROR_CATAGORIES.MODEL_STATE_CODE,
                                                                     DSPAdvisorI18n.Status_ModelProblems_Errors, null);

        public static final Status CONNECTOR_BINDINGS_OK = new Status(IStatus.OK, PLUGIN_ID,
                                                                      ERROR_CATAGORIES.CONNECTOR_BINDINGS_STATE_CODE,
                                                                      DSPAdvisorI18n.Status_Bindings_OK, null);
        public static final Status NO_CONNECTOR_BINDINGS_NO_SOURCES = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                                 ERROR_CATAGORIES.CONNECTOR_BINDINGS_STATE_CODE,
                                                                                 DSPAdvisorI18n.Status_Bindings_NoneExist, null);
        public static final Status CONNECTOR_BINDINGS_ERROR = new Status(IStatus.ERROR, PLUGIN_ID,
                                                                         ERROR_CATAGORIES.CONNECTOR_BINDINGS_STATE_CODE,
                                                                         DSPAdvisorI18n.Status_Bindings_Errors, null);

        public static final Status SOURCE_MODELS_ARE_OK = new Status(IStatus.OK, PLUGIN_ID,
                                                                     ERROR_CATAGORIES.SOURCES_MODELS_EXIST_CODE,
                                                                     DSPAdvisorI18n.Status_Sources_OK, null);
        public static final Status NO_SOURCE_MODELS_ARE_DEFINED = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                             ERROR_CATAGORIES.SOURCES_MODELS_EXIST_CODE,
                                                                             DSPAdvisorI18n.Status_Sources_NoModels, null);
        public static final Status SOURCE_MODELS_HAVE_ERRORS = new Status(IStatus.ERROR, PLUGIN_ID,
                                                                          ERROR_CATAGORIES.SOURCES_MODELS_EXIST_CODE,
                                                                          DSPAdvisorI18n.Status_Sources_Errors, null);

        public static final Status VIEW_MODELS_ARE_OK = new Status(IStatus.OK, PLUGIN_ID,
                                                                   ERROR_CATAGORIES.VIEWS_MODELS_EXIST_CODE,
                                                                   DSPAdvisorI18n.Status_Views_OK, null);
        public static final Status NO_VIEW_MODELS_ARE_DEFINED = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                           ERROR_CATAGORIES.VIEWS_MODELS_EXIST_CODE,
                                                                           DSPAdvisorI18n.Status_Views_NoModels, null);
        public static final Status VIEW_MODELS_HAVE_ERRORS = new Status(IStatus.ERROR, PLUGIN_ID,
                                                                        ERROR_CATAGORIES.VIEWS_MODELS_EXIST_CODE,
                                                                        DSPAdvisorI18n.Status_Views_Errors, null);

        public static final Status XML_VIEW_MODELS_ARE_OK = new Status(IStatus.OK, PLUGIN_ID,
                                                                       ERROR_CATAGORIES.XML_VIEW_MODELS_EXIST_CODE,
                                                                       DSPAdvisorI18n.Status_XmlMappings_OK, null);
        public static final Status NO_XML_VIEW_MODELS_ARE_DEFINED = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                               ERROR_CATAGORIES.XML_VIEW_MODELS_EXIST_CODE,
                                                                               DSPAdvisorI18n.Status_XmlMappings_NoModels, null);
        public static final Status XML_VIEW_MODELS_HAVE_ERRORS = new Status(IStatus.ERROR, PLUGIN_ID,
                                                                            ERROR_CATAGORIES.XML_VIEW_MODELS_EXIST_CODE,
                                                                            DSPAdvisorI18n.Status_XmlMappings_Errors, null);

        public static final Status WEB_SERVICE_MODELS_ARE_OK = new Status(IStatus.OK, PLUGIN_ID,
                                                                          ERROR_CATAGORIES.WEB_SERVICE_MODELS_EXIST_CODE,
                                                                          DSPAdvisorI18n.Status_WebServices_OK, null);
        public static final Status NO_WEB_SERVICE_MODELS_ARE_DEFINED = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                                  ERROR_CATAGORIES.WEB_SERVICE_MODELS_EXIST_CODE,
                                                                                  DSPAdvisorI18n.Status_WebServices_NoModels,
                                                                                  null);
        public static final Status WEB_SERVICE_MODELS_HAVE_ERRORS = new Status(IStatus.ERROR, PLUGIN_ID,
                                                                               ERROR_CATAGORIES.WEB_SERVICE_MODELS_EXIST_CODE,
                                                                               DSPAdvisorI18n.Status_WebServices_Errors, null);

        public static final Status SCHEMA_MODELS_ARE_DEFINED = new Status(IStatus.OK, PLUGIN_ID,
                                                                          ERROR_CATAGORIES.SCHEMA_MODELS_EXIST_CODE,
                                                                          DSPAdvisorI18n.Status_XmlSchemas_OK, null);
        public static final Status NO_SCHEMA_MODELS_ARE_DEFINED = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                             ERROR_CATAGORIES.SCHEMA_MODELS_EXIST_CODE,
                                                                             DSPAdvisorI18n.Status_XmlSchemas_NoModels, null);
        public static final Status SCHEMA_MODELS_HAVE_ERRORS = new Status(IStatus.ERROR, PLUGIN_ID,
                                                                          ERROR_CATAGORIES.SCHEMA_MODELS_EXIST_CODE,
                                                                          DSPAdvisorI18n.Status_XmlSchemas_Errors, null);

        public static final Status XML_VIEW_MAPPINGS_OK = new Status(IStatus.OK, PLUGIN_ID,
                                                                     ERROR_CATAGORIES.XML_VIEW_MAPPINGS_CODE,
                                                                     DSPAdvisorI18n.Status_XmlMappings_OK, null);
        public static final Status XML_VIEW_MAPPINGS_ERRORS = new Status(IStatus.ERROR, PLUGIN_ID,
                                                                         ERROR_CATAGORIES.XML_VIEW_MAPPINGS_CODE,
                                                                         DSPAdvisorI18n.Status_XmlMappings_Errors, null);
        public static final Status XML_VIEW_MAPPINGS_NO_MODELS_ERROR = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                                  ERROR_CATAGORIES.XML_VIEW_MAPPINGS_CODE,
                                                                                  DSPAdvisorI18n.Status_XmlMappings_NoModels,
                                                                                  null);

        public static final Status VDBS_OK = new Status(IStatus.OK, PLUGIN_ID, ERROR_CATAGORIES.VDBS_STATE_CODE,
                                                        DSPAdvisorI18n.Status_VDBs_OK, null);
        public static final Status NO_VDBS_ARE_DEFINED = new Status(IStatus.WARNING, PLUGIN_ID, ERROR_CATAGORIES.VDBS_STATE_CODE,
                                                                    DSPAdvisorI18n.Status_VDBs_NoVDBs, null);
        public static final Status VDBS_HAVE_ERRORS = new Status(IStatus.ERROR, PLUGIN_ID, ERROR_CATAGORIES.VDBS_STATE_CODE,
                                                                 DSPAdvisorI18n.Status_VDBs_Errors, null);

        public static final Status PREVIEW_WSDL_OK = new Status(IStatus.OK, PLUGIN_ID, ERROR_CATAGORIES.PREVIEW_WSDL_CODE,
                                                                DSPAdvisorI18n.Status_PreviewWsdl_OK, null);
        public static final Status PREVIEW_WSDL_INCOMPLETE_MODELS = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                               ERROR_CATAGORIES.PREVIEW_WSDL_CODE,
                                                                               DSPAdvisorI18n.Status_PreviewWsdl_Incomplete, null);
        public static final Status PREVIEW_WSDL_ERRORS = new Status(IStatus.ERROR, PLUGIN_ID, ERROR_CATAGORIES.PREVIEW_WSDL_CODE,
                                                                    DSPAdvisorI18n.Status_PreviewWsdl_Errors, null);

        public static final Status COMPLETION_OK = new Status(IStatus.OK, PLUGIN_ID, ERROR_CATAGORIES.COMPLETED_STATE_CODE,
                                                              DSPAdvisorI18n.Status_Project_OK, null);
        public static final Status COMPLETION_INCOMPLETE = new Status(IStatus.WARNING, PLUGIN_ID,
                                                                      ERROR_CATAGORIES.COMPLETED_STATE_CODE,
                                                                      DSPAdvisorI18n.Status_Project_Incomplete, null);
        public static final Status COMPLETION_ERRORS_EXIST = new Status(IStatus.ERROR, PLUGIN_ID,
                                                                        ERROR_CATAGORIES.COMPLETED_STATE_CODE,
                                                                        DSPAdvisorI18n.Status_Project_Errors, null);

        public static final ModelProjectStatus NO_PROJECT_SELECTED = new ModelProjectStatus(
                                                                                            PLUGIN_ID,
                                                                                            ERROR_CATAGORIES.NO_PROJECT_CODE,
                                                                                            DSPAdvisorI18n.Status_Project_Not_Selected,
                                                                                            null);

        public static final AdvisorStatus ADVISOR_NO_PROJECT_SELECTED = new AdvisorStatus(
                                                                                          PLUGIN_ID,
                                                                                          ERROR_CATAGORIES.NO_PROJECT_CODE,
                                                                                          DSPAdvisorI18n.Status_Project_Not_Selected,
                                                                                          null);

    }
}
