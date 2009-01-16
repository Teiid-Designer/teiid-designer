/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.dqp.ui;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;


/**
 * @since 4.3
 */
public interface DqpUiConstants {
    public static final char DOT = '.';

    /**
     * The identifier of the containing plugin.
     * @since 4.3
     */
    String PLUGIN_ID = DqpUiConstants.class.getPackage().getName();

    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * @since 4.3
     */
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));

    /**
     * File extension for CDK files
     */
    public static final String CDK_FILE_EXTENSION = ".cdk"; //$NON-NLS-1$

    /**
     * File extension for CAF files
     */
    public static final String CAF_FILE_EXTENSION = ".caf"; //$NON-NLS-1$

    /**
     * File extensions for connector type files
     */
    public static final String[] CDK_FILE_EXTENSIONS = new String[] {"*.cdk", "*.caf"}; //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * File extensions filter names for connector type files
     */
    public static final String[] CDK_FILE_NAMES = new String[] {"Connector Types (*.cdk)", "Connector Types Archive (*.caf)"}; //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * Extension ID of the jdbc client perspective
     */
    String JDBC_CLIENT_PERSPECTIVE_ID = "com.metamatrix.modeler.internal.dqp.ui.jdbc.JdbcClientPluginPerspective"; //$NON-NLS-1$

    /**
     * Extension ID of the jdbc cconnection view
     */
    String JDBC_CONNECTION_VIEW_ID = "net.sourceforge.sqlexplorer.plugin.views.ConnectionsView"; //$NON-NLS-1$

    /**
     * VDB Editor Tab ID for ConnectorBindings
     */
    String VDB_EDITOR_CONNECTOR_BINDINGS_ID = UTIL.getString("ConnectorBindingsEditorPart.tabTitle"); //$NON-NLS-1$

    /**
     * import types
     */
    public static final int IMPORT_CONFIG            = 0;
    public static final int IMPORT_CONNECTOR_BINDING = 1;
    public static final int IMPORT_CONNECTOR_TYPE    = 2;
    public static final int IMPORT_FUNCTION_MOD      = 3;
    public static final int IMPORT_CONNECTOR_MOD     = 4;

    public static final String[][] IMPORT_FILE_NAMES = new String[][] {
        {"Configurations (*.cfg)", "Configurations (*.xml)"},           //$NON-NLS-1$ //$NON-NLS-2$
        {"Connector Bindings (*.cdk)", "Connector Bindings (*.xml)"},   ////$NON-NLS-1$ //$NON-NLS-2$
        {"Connector Types Archive (*.caf)", "Connector Types (*.cdk)"}, //$NON-NLS-1$ //$NON-NLS-2$
        {"Function Modules (*.jar)" , "Function Modules (*.zip)"},      //$NON-NLS-1$ //$NON-NLS-2$
        {"Connector Modules (*.jar)"}      //$NON-NLS-1$
    };

    public static final String[][] IMPORT_FILE_EXTENSIONS = new String[][] {
        {"*.cfg", "*.xml"}, //$NON-NLS-1$ //$NON-NLS-2$
        {"*.cdk", "*.xml"}, //$NON-NLS-1$ //$NON-NLS-2$
        {"*.caf", "*.cdk"}, //$NON-NLS-1$ //$NON-NLS-2$
        {"*.jar", "*.zip"}, //$NON-NLS-1$ //$NON-NLS-2$
        {"*.jar"}  //$NON-NLS-1$
    };

    public static final String[][] IMPORT_FILE_NAMES_NEW = new String[][] {
        {"Configurations (*.cfg)", "Configurations (*.xml)"},           //$NON-NLS-1$ //$NON-NLS-2$
        {"Connector Bindings (*.cdk)"},   ////$NON-NLS-1$
        {"Connector Types Archive (*.caf)", "Connector Types (*.cdk)"}, //$NON-NLS-1$ //$NON-NLS-2$
        {"Function Modules (*.jar)" , "Function Modules (*.zip)"},      //$NON-NLS-1$ //$NON-NLS-2$
        {"Extension Modules (*.jar)"}      //$NON-NLS-1$
    };

    public static final String[][] IMPORT_FILE_EXTENSIONS_NEW = new String[][] {
        {"*.cfg", "*.xml"}, //$NON-NLS-1$ //$NON-NLS-2$
        {"*.cdk"}, //$NON-NLS-1$
        {"*.caf"}, //$NON-NLS-1$ 
        {"*.jar", "*.zip"}, //$NON-NLS-1$ //$NON-NLS-2$
        {"*.jar"}  //$NON-NLS-1$
    };

    /**
     * Private constants used by other constants within this class.
     * @since 4.3
     */
    class PC {
        public static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$

        public static final String ICON_PATH = "icons/full/"; //$NON-NLS-1$

        public static final String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$

        public static final String CTOOL16 = ICON_PATH + "ctool16/"; //$NON-NLS-1$

        public static final String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$

        public static final String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
    }

    interface Images {
        public static final String PHYSICAL_MODEL_ICON = PC.OBJ16 + "physicalModel.gif"; //$NON-NLS-1$

        public static final String OPEN_PHYSICAL_MODEL_ICON = PC.OBJ16 + "openPhysicalModel.gif"; //$NON-NLS-1$

        public static final String CONNECTOR_BINDING_ICON = PC.OBJ16 + "connectorBinding.gif"; //$NON-NLS-1$

        public static final String CONNECTOR_TYPE_ICON = PC.OBJ16 + "connectorType.gif"; //$NON-NLS-1$

        public static final String IMPORT_ICON = PC.CTOOL16 + "import.gif"; //$NON-NLS-1$

        public static final String REFRESH_ICON = PC.CTOOL16 + "refresh.gif"; //$NON-NLS-1$

        String SHOW_DEBUG_LOG_ICON = PC.CTOOL16 + "debug_log.gif"; //$NON-NLS-1$
        String SHOW_DEBUG_LOG_DISABLED_ICON = PC.CTOOL16 + "debug_log_disabled.gif"; //$NON-NLS-1$
        String SHOW_SQL_RESULTS_ICON = PC.CTOOL16 + "sql_results.gif"; //$NON-NLS-1$
        String SHOW_SQL_RESULTS_DISABLED_ICON = PC.CTOOL16 + "sql_results_disabled.gif"; //$NON-NLS-1$
        String SHOW_PLAN_DOCUMENT_ICON = PC.CTOOL16 + "plan_document.gif"; //$NON-NLS-1$
        String SHOW_PLAN_DOCUMENT_DISABLED_ICON = PC.CTOOL16 + "plan_document_disabled.gif"; //$NON-NLS-1$
        String SHOW_PLAN_TREE_DISABLED_ICON = PC.CTOOL16 + "plan_tree_disabled.gif"; //$NON-NLS-1$
        String SHOW_PLAN_TREE_ICON = PC.CTOOL16 + "plan_tree.gif"; //$NON-NLS-1$

        public static final String SEPARATOR_ICON = PC.CTOOL16 + "verticalseparator.gif"; //$NON-NLS-1$
        public static final String NEW_FOLDER_ICON = PC.CTOOL16 + "newfolder.gif"; //$NON-NLS-1$
        public static final String DELETE_ICON = PC.CTOOL16 + "deletefile.gif"; //$NON-NLS-1$
        public static final String SAVE_TO_FILE_ICON = PC.CTOOL16 + "save_to_file.gif"; //$NON-NLS-1$
        public static final String IMPORT_JAR_ICON = PC.CTOOL16 + "importjarorzip.gif"; //$NON-NLS-1$
        public static final String IMPORT_CAF_ICON = PC.CTOOL16 + "import_caf.gif"; //$NON-NLS-1$

        public static final String CONNECTOR_JAR_ICON = PC.OBJ16 + "connectorJar.gif"; //$NON-NLS-1$
        public static final String CONNECTOR_ZIP_ICON = PC.OBJ16 + "connectorZip.gif"; //$NON-NLS-1$

        public static final String CONNECTOR_ICON              = PC.OBJ16 + "connector.gif"; //$NON-NLS-1$
        public static final String SOURCE_BINDING_ICON         = PC.OBJ16 + "sourceBinding.gif"; //$NON-NLS-1$
        public static final String SOURCE_UNBINDING_ICON       = PC.OBJ16 + "sourceBinding.gif"; //$NON-NLS-1$
        public static final String SOURCE_MODEL_ICON           = PC.OBJ16 + "Model.gif"; //$NON-NLS-1$
        public static final String SOURCE_CONNECTOR_BINDING_ICON   = PC.OBJ16 + "sourceModelBinding.png"; //$NON-NLS-1$
        public static final String IMPORT_WIZ_ICON             = PC.CTOOL16 + "import_wiz.gif"; //$NON-NLS-1$
        public static final String IMPORT_CONNECTORS_ICON      = PC.CTOOL16 + "import_connectors.png"; //$NON-NLS-1$
        public static final String EXPORT_CONNECTORS_ICON      = PC.CTOOL16 + "export_connectors.png"; //$NON-NLS-1$
        public static final String NEW_BINDING_ICON            = PC.CTOOL16 + "new_binding.png"; //$NON-NLS-1$

        public static final String PREVIEW_DATA_ICON      = PC.CTOOL16 + "previewData.gif"; //$NON-NLS-1$
        public static final String SHOW_HIDE_CONNECTORS_ICON   = PC.CTOOL16 + "showHideConnectors.png"; //$NON-NLS-1$
        public static final String COLLAPSE_ALL_ICON           = PC.CTOOL16 + "collapseall.gif"; //$NON-NLS-1$
        public static final String UDF_JAR_ICON                = PC.OBJ16 + "UdfJar.png"; //$NON-NLS-1$
        public static final String UDF_JAR_FOLDER_ICON         = PC.OBJ16 + "UdfJarsFolder.png"; //$NON-NLS-1$
        public static final String ADD_UDF_JAR_ICON            = PC.CTOOL16 + "add_udf_jar.png"; //$NON-NLS-1$
        
        public static final String IMPORT_CONNECTORS_WIZBAN    = PC.WIZBAN + "import_connectors.jpg"; //$NON-NLS-1$
        public static final String EXPORT_CONNECTORS_WIZBAN    = PC.WIZBAN + "export_connectors.jpg"; //$NON-NLS-1$
    }

    interface Extensions {
        // Connector Bindings VDB Editor Page
        String CONNECTOR_BINDINGS_VDB_EDITOR_PAGE_ID = PLUGIN_ID + DOT + "connectorbindingseditorpart"; //$NON-NLS-1$;

        String XML_DOC_SQL_RESULTS_VIEW = PLUGIN_ID + DOT + "views.XmlDocumentSqlResultsView"; //$NON-NLS-1$

        String XML_DOC_RESULTSET_PROCESSOR = PLUGIN_ID + DOT + "xmlDocumentResultSetProcessor"; //$NON-NLS-1$

        String SQL_RESULTS_VIEW = PLUGIN_ID + DOT + "views.SqlResultsView"; //$NON-NLS-1$

        String SQL_RESULTSET_PROCESSOR = PLUGIN_ID + DOT + "sqlDocumentResultSetProcessor"; //$NON-NLS-1$

        String CONFIGURATION_MANAGER_EDITOR_ID = "com.metamatrix.modeler.internal.dqp.ui.views.ConfigurationManagerEditor"; //$NON-NLS-1$

        String CONNECTORS_VIEW_ID = PLUGIN_ID + DOT + "workspace.connectorsView"; //$NON-NLS-1$

        String PREVIEW_DATA_VIEW = PLUGIN_ID + DOT + "views.PreviewDataView"; //$NON-NLS-1$

        String UDF_MODEL_VIEW = PLUGIN_ID + DOT + "workspace.udf.udfModelView"; //$NON-NLS-1$
    }

    interface ExtensionPoints {

        interface VdbEditorUtil {
            String ID = "vdbEditorUtil"; //$NON-NLS-1$;
            String INSTANCE_ELEMENT = "instance"; //$NON-NLS-1$;
            String CLASSNAME = "classname"; //$NON-NLS-1$;
        }

    }

    /**
     * Constants used to access plugin preference values.
     * @since 5.0
     */
    interface Preferences {
        /**
         * General preference that will limit the number of rows returned on a Preview Query
         * @since 5.0
         */
        String ID_PREVIEW_ROW_LIMIT = "generalPreference.previewRowLimit"; //$NON-NLS-1$

        /**
         * General preference that will limit the number of preview results shown.
         * @since 5.5.3
         */
        String ID_PREVIEW_RESULTS_LIMIT = "generalPreference.previewResultsLimit"; //$NON-NLS-1$
    }
}
