/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.vdb.ui;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.UiConstants;

/**
 * "Global" Constants with respect to this plug-in that may be used by other plug-ins.
 * 
 * @since 4.2
 */
public interface VdbUiConstants {

    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.2
     */
    String PLUGIN_ID = "org.teiid.designer.vdb.ui"; //$NON-NLS-1$

    /**
     */
    String PACKAGE_ID = VdbUiConstants.class.getPackage().getName();

    /**
     */
    String VDB_EXPLORER_VIEW_ID = "com.metamatrix.modeler.internal.vdb.ui.views.vdbView"; //$NON-NLS-1$

    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * 
     * @since 4.2
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

    /**
     * 
     */
    interface Extensions {
        String VDB_EDITOR_ID = "com.metamatrix.modeler.internal.vdb.ui.editor.vdbEditor"; //$NON-NLS-1$
        String PROBLEMS_TAB_ID = Util.getString("VdbEditorProblemPage.title"); //$NON-NLS-1$
    }

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 4.0
     */
    interface Images extends UiConstants.Images {
        String IMPORT_VDB_ICON = WIZBAN + "import_vdb.gif"; //$NON-NLS-1$
        String SYNCRONIZE_VDB_ICON = FULL + "cview20/" + "synchronizemodels.gif"; //$NON-NLS-1$ //$NON-NLS-2$
        String REBUILD_VDB_ICON = CTOOL16 + "rebuild_vdb.gif"; //$NON-NLS-1$
        String SYNCHRONIZE_MODELS_ICON = CVIEW16 + "synchronizemodels.gif"; //$NON-NLS-1$
        String VISIBLE_ICON = CVIEW16 + "visible.gif"; //$NON-NLS-1$
        
        String ADD_FILE = CTOOL16 + "add-file.png"; //$NON-NLS-1$
        String ADD_MODEL = CTOOL16 + "add-model.png"; //$NON-NLS-1$
        String ADD_ROLE = CTOOL16 + "add-role.png"; //$NON-NLS-1$
        String ADD_TRANSLATOR = CTOOL16 + "add-translator.png"; //$NON-NLS-1$
        String EDIT_ROLE = CTOOL16 + "edit-role.png"; //$NON-NLS-1$
        String EDIT_TRANSLATOR = CTOOL16 + "edit-translator.png"; //$NON-NLS-1$
        String REMOVE_FILE = CTOOL16 + "remove-file.png"; //$NON-NLS-1$
        String REMOVE_MODEL = CTOOL16 + "remove-model.png"; //$NON-NLS-1$
        String REMOVE_ROLE = CTOOL16 + "remove-role.png"; //$NON-NLS-1$
        String REMOVE_TRANSLATOR = CTOOL16 + "remove-translator.png"; //$NON-NLS-1$
        String RESTORE_DEFAULT_VALUE = CTOOL16 + "restore-default-value.png"; //$NON-NLS-1$
    }

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.1
     */
    class PC {
        public static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$

        public static final String ICON_PATH = "icons/full/"; //$NON-NLS-1$

        public static final String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$

        public static final String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$

        public static final String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
    }
}
