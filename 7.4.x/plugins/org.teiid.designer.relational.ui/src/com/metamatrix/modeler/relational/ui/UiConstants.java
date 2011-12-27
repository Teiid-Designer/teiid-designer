/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relational.ui;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

public interface UiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.designer.relational.ui"; //$NON-NLS-1$

    String PACKAGE_ID = UiConstants.class.getPackage().getName();

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    }

    // ============================================================================================================================
    // Image constants

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 4.0
     */
    interface Images extends com.metamatrix.ui.UiConstants.Images {
        String MANAGE_EXTENDED_PROPERTIES_ICON = WIZBAN + "manage_properties.gif"; //$NON-NLS-1$
        String MANAGE_EXTENDED_PROPERTIES_ACTION_ICON = CTOOL16 + "edit_extension_properties.png"; //$NON-NLS-1$
    }

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));
}
