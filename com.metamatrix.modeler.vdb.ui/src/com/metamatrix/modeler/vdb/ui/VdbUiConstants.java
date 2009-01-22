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
    //============================================================================================================================
    // Constants

    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.2
     */
    String PLUGIN_ID = VdbUiConstants.class.getPackage().getName();
    
    String VDB_EXPLORER_VIEW_ID = "com.metamatrix.modeler.internal.vdb.ui.views.vdbView"; //$NON-NLS-1$
    
    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.1
     */
    class PC {
        public static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$

        public static final String ICON_PATH = "icons/full/"; //$NON-NLS-1$

        public static final String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$

        public static final String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$

        public static final String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
    }

    
    
    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * 
     * @since 4.2
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));


    interface Extensions {
        String VDB_EDITOR_ID = "com.metamatrix.modeler.internal.vdb.ui.editor.vdbEditor"; //$NON-NLS-1$
        String OVERVIEW_TAB_ID = Util.getString("VdbEditorOverviewPage.title"); //$NON-NLS-1$
        String PROBLEMS_TAB_ID = Util.getString("VdbEditorProblemPage.title"); //$NON-NLS-1$
        String WEB_SERVICES_TAB_ID = Util.getString("VdbEditorWsdlPage.title");  //$NON-NLS-1$
        String INDEXES_TAB_ID = Util.getString("VdbEditorWsdlPage.title");  //$NON-NLS-1$
    }
    
    /**
     * Extension points defined by the VDB UI Plugin. 
     * @since 4.3
     */
    interface ExtensionPoints {

        /**
         * Constants for the VDB Editor Page extension point.
         */
        interface VdbEditorPage {
            String ID = "vdbEditorPage"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class";  //$NON-NLS-1$
            String CLASS_NAME = "name"; //$NON-NLS-1$
            String DISPLAY_NAME = "displayName"; //$NON-NLS-1$
            String ORDER = "order"; //$NON-NLS-1$
        }

    }
    
    //============================================================================================================================
	// Image constants
    
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images
    extends UiConstants.Images {
        String IMPORT_VDB_ICON           = WIZBAN   + "import_vdb.gif"; //$NON-NLS-1$
        String SYNCRONIZE_VDB_ICON       = FULL + "cview20/"   + "synchronizemodels.gif"; //$NON-NLS-1$ //$NON-NLS-2$
        String REBUILD_VDB_ICON          = FULL + "ctool16/"   + "rebuild_vdb.gif"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
