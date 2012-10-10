/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.ui;

import java.util.ResourceBundle;

import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.ui.common.UiConstants;


/**
 * ModelerXsdUiConstants
 *
 * @since 8.0
 */
public interface ModelerXsdUiConstants {

    // ======================================
    // Constants

    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.1
     */
    String PLUGIN_ID = "org.teiid.designer.xsd.ui"; //$NON-NLS-1$ 

    String PACKAGE_ID = ModelerXsdUiConstants.class.getPackage().getName();

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.1
     */
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    }

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.1
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 4.1
     */
    interface Images extends UiConstants.Images {
        String IMPORT_XSD_ICON = WIZBAN + "import_xsd.gif"; //$NON-NLS-1$
    }
}
