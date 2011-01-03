/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
     * 
     * @since 4.3
     */

    String PLUGIN_ID = "org.teiid.designer.dqp.ui"; //$NON-NLS-1$

    String PACKAGE_ID = DqpUiConstants.class.getPackage().getName();

    //String EXT_PREFIX = "com.metamatrix.modeler.dqp.ui";  //$NON-NLS-1$

    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * 
     * @since 4.3
     */
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

    /**
     * Extension ID of the jdbc client perspective
     */
    String JDBC_CLIENT_PERSPECTIVE_ID = /*EXT_PREFIX + DOT + */"jdbcClientPluginPerspective"; //$NON-NLS-1$

    /**
     * Extension ID of the jdbc cconnection view
     */
    String JDBC_CONNECTION_VIEW_ID = "net.sourceforge.sqlexplorer.plugin.views.ConnectionsView"; //$NON-NLS-1$

    String RECONNECT_SERVER_FAMILY = "reconnectServer"; //$NON-NLS-1$

    /**
     * import types
     */

    interface ExtensionPoints {

        interface VdbEditorUtil {
            String ID = "vdbEditorUtil"; //$NON-NLS-1$;
            String INSTANCE_ELEMENT = "instance"; //$NON-NLS-1$;
            String CLASSNAME = "classname"; //$NON-NLS-1$;
        }

    }

    interface Extensions {
        String XML_DOC_SQL_RESULTS_VIEW = /*EXT_PREFIX + DOT + */"xmlDocumentSqlResultsView"; //$NON-NLS-1$

        String XML_DOC_RESULTSET_PROCESSOR = /*EXT_PREFIX + DOT + */"xmlDocumentResultSetProcessor"; //$NON-NLS-1$

        String SQL_RESULTS_VIEW = /*EXT_PREFIX + DOT + */"sqlResultsView"; //$NON-NLS-1$

        String SQL_RESULTSET_PROCESSOR = /*EXT_PREFIX + DOT + */"sqlResultSetProcessor"; //$NON-NLS-1$

        String CONNECTORS_VIEW_ID = /*PLUGIN_ID + DOT + */"connectorsView"; //$NON-NLS-1$

        String PREVIEW_DATA_VIEW = /*PLUGIN_ID + DOT + */"previewDataView"; //$NON-NLS-1$
    }

    interface Images {
        public static final String PHYSICAL_MODEL_ICON = PC.OBJ16 + "physicalModel.gif"; //$NON-NLS-1$

        public static final String OPEN_PHYSICAL_MODEL_ICON = PC.OBJ16 + "openPhysicalModel.gif"; //$NON-NLS-1$

        public static final String CONNECTOR_BINDING_ICON = PC.OBJ16 + "connectorBinding.gif"; //$NON-NLS-1$

        public static final String CONNECTOR_TYPE_ICON = PC.OBJ16 + "connectorType.gif"; //$NON-NLS-1$

        public static final String FOLDER_OBJ = PC.OBJ16 + "fldr_obj.gif"; //$NON-NLS-1$

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

        public static final String DELETE_ICON = PC.CTOOL16 + "deletefile.gif"; //$NON-NLS-1$
        public static final String SAVE_TO_FILE_ICON = PC.CTOOL16 + "save_to_file.gif"; //$NON-NLS-1$

        public static final String CONNECTOR_ICON = PC.OBJ16 + "connector.gif"; //$NON-NLS-1$
        public static final String SOURCE_BINDING_ICON = PC.OBJ16 + "sourceBinding.gif"; //$NON-NLS-1$
        public static final String SOURCE_UNBINDING_ICON = PC.OBJ16 + "sourceBinding.gif"; //$NON-NLS-1$
        public static final String SOURCE_MODEL_ICON = PC.OBJ16 + "Model.gif"; //$NON-NLS-1$
        public static final String SOURCE_CONNECTOR_BINDING_ICON = PC.OBJ16 + "sourceModelBinding.png"; //$NON-NLS-1$
        public static final String IMPORT_WIZ_ICON = PC.CTOOL16 + "import_wiz.gif"; //$NON-NLS-1$
        // TODO create SERVER images
        public static final String SERVER_ICON = PC.CTOOL16 + "teiid-server.png"; //$NON-NLS-1$
        public static final String SERVER_ERROR_ICON = PC.CTOOL16 + "teiid-server-error.png"; //$NON-NLS-1$
        public static final String SET_DEFAULT_SERVER_ICON = PC.CTOOL16 + "teiid-server-default.png"; //$NON-NLS-1$
        public static final String SET_DEFAULT_SERVER_ERROR_ICON = PC.CTOOL16 + "teiid-server-disconnected.png"; //$NON-NLS-1$
        public static final String NEW_SERVER_ICON = PC.CTOOL16 + "new-teiid-server.png"; //$NON-NLS-1$
        public static final String DELETE_SERVER_ICON = PC.CTOOL16 + "delete-teiid-server.png"; //$NON-NLS-1$
        public static final String EDIT_SERVER_ICON = PC.CTOOL16 + "edit-teiid-server.png"; //$NON-NLS-1$
        public static final String NEW_BINDING_ICON = PC.CTOOL16 + "new_binding.png"; //$NON-NLS-1$
        public static final String CONNECTION_SOURCE_ICON = PC.CTOOL16 + "connection_source.gif"; //$NON-NLS-1$
        
        public static final String PREVIEW_DATA_ICON = PC.CTOOL16 + "previewData.gif"; //$NON-NLS-1$
        public static final String SHOW_HIDE_CONNECTORS_ICON = PC.CTOOL16 + "showHideConnectors.png"; //$NON-NLS-1$
        public static final String COLLAPSE_ALL_ICON = PC.CTOOL16 + "collapseall.gif"; //$NON-NLS-1$

        public static final String IMPORT_CONNECTORS_WIZBAN = PC.WIZBAN + "import_connectors.jpg"; //$NON-NLS-1$
        public static final String EXPORT_CONNECTORS_WIZBAN = PC.WIZBAN + "export_connectors.jpg"; //$NON-NLS-1$
        public static final String SERVER_WIZBAN = PC.WIZBAN + "export_connectors.jpg"; //"server_wizard_banner.jpg"; //$NON-NLS-1$
        public static final String CREATE_WAR = PC.CTOOL16 + "deployVdb.png"; //$NON-NLS-1$
        public static final String DEPLOY_VDB = PC.CTOOL16 + "deployVdb.png"; //$NON-NLS-1$
        public static final String EXECUTE_VDB = PC.CTOOL16 + "run_vdb.gif"; //$NON-NLS-1$
        public static final String INACTIVE_DEPLOYED_VDB = PC.CTOOL16 + "inactiveDeployedVdb.png"; //$NON-NLS-1$

    }

    /**
     * Private constants used by other constants within this class.
     * 
     * @since 4.3
     */
    class PC {
        public static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$

        public static final String ICON_PATH = "icons/full/"; //$NON-NLS-1$

        public static final String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$

        public static final String CTOOL16 = ICON_PATH + "ctool16/"; //$NON-NLS-1$

        public static final String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$

        public static final String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
    }

}
