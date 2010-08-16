/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui;

import java.util.ResourceBundle;
import org.eclipse.ui.PlatformUI;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;


/**
 * UiConstants
 * @since 4.0
 */
public interface UiConstants {
    //============================================================================================================================
    // Constants
    
    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.designer.ui.common"; //$NON-NLS-1$ 
    
    String PACKAGE_ID = UiConstants.class.getPackage().getName();

    /**
     * Contains private constants used by other constants within this class.
     */
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    }
    
    /**
     * Provides access to the plugin's log and to it's resources.
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PACKAGE_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

    //============================================================================================================================
	// Image constants
        
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images {
        String ICONS = "icons/"; //$NON-NLS-1$
        String FULL = ICONS + "full/"; //$NON-NLS-1$
        
        String CTOOL16 = FULL + "ctool16/"; //$NON-NLS-1$
        String CVIEW16 = FULL + "cview16/"; //$NON-NLS-1$
        String DLCL16  = FULL + "dlcl16/"; //$NON-NLS-1$
        String ELCL16  = FULL + "elcl16/"; //$NON-NLS-1$
        String OBJ16   = FULL + "obj16/"; //$NON-NLS-1$
        String OVR16   = FULL + "ovr16/"; //$NON-NLS-1$
        String WIZBAN  = FULL + "wizban/"; //$NON-NLS-1$

        String ALL_LEFT           = CVIEW16 + "allLeft.gif"; //$NON-NLS-1$
        String ALL_RIGHT          = CVIEW16 + "allRight.gif"; //$NON-NLS-1$
        String ASCENDING_ICON     = CVIEW16 + "ascending.gif"; //$NON-NLS-1$
        String DESCENDING_ICON    = CVIEW16 + "descending.gif"; //$NON-NLS-1$
        String DOWN               = CVIEW16 + "down.gif"; //$NON-NLS-1$
        String LEFT               = CVIEW16 + "left.gif"; //$NON-NLS-1$
        String RIGHT              = CVIEW16 + "right.gif"; //$NON-NLS-1$
        String UP                 = CVIEW16 + "up.gif"; //$NON-NLS-1$
        
        /**
		 * This icon exists in org.eclipse.ui.ide rather than org.eclipse.ui
		 */
        String REFRESH = ELCL16 + "refresh_nav.gif"; //$NON-NLS-1$

        String CHECKED_CHECKBOX            = CVIEW16 + "checked_box.png"; //$NON-NLS-1$
        String UNCHECKED_CHECKBOX          = CVIEW16 + "unchecked_box.png"; //$NON-NLS-1$

        // images registered from org.eclipse.ui and used in org.eclipse.ui.views.tasklist.
        // images are registered in UiPlugin.startup()
        String TASK_ERROR   = OBJ16 + "error_tsk.gif"; //$NON-NLS-1$
        String TASK_WARNING = OBJ16 + "warn_tsk.gif"; //$NON-NLS-1$
        String TASK_INFO    = OBJ16 + "info_tsk.gif"; //$NON-NLS-1$

        String FIND_DISABLED = DLCL16 + "find_obj.gif"; //$NON-NLS-1$
        String FIND_ENABLED  = ELCL16 + "find_obj.gif"; //$NON-NLS-1$
    }

    
    interface EclipsePluginIds {
        String UI = PlatformUI.PLUGIN_ID;
        String UI_IDE = "org.eclipse.ui.ide"; //$NON-NLS-1$
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // EXTENSION POINTS INTERFACE
    ///////////////////////////////////////////////////////////////////////////////////////////////

    //============================================================================================================================
    // Extension Point constants

    /**
     * Constants related to extension points, including all extension point ID's and extension point schema component names.
     * @since 4.0
     */
    interface ExtensionPoints {
        /**
         * Constants for the Product Customizer extension point.
         */
        interface ProductCustomizer {
            String ID = "productCustomizer"; //$NON-NLS-1$
            String CLASS_NAME = "name"; //$NON-NLS-1$
        }        
    }
        
}
