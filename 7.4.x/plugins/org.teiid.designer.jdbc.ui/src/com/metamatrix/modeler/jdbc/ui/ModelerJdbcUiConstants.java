/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.ui;

import java.util.ResourceBundle;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.UiConstants;

/**
 * @since 4.0
 */
public interface ModelerJdbcUiConstants {

    /**
     * The ID of the plug-in containing this constants class.
     * 
     * @since 4.0
     */
    String PLUGIN_ID = "org.teiid.designer.jdbc.ui"; //$NON-NLS-1$
    
    String PACKAGE_ID = ModelerJdbcUiConstants.class.getPackage().getName();

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    }

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

    /**
     * Keys for images and image descriptors stored in the image registry.
     * 
     * @since 4.0
     */
    interface Images extends UiConstants.Images {
        String DATABASE = OBJ16 + "database.gif"; //$NON-NLS-1$
        String IMPORT_DATABASE_ICON = WIZBAN + "importDatabase.gif"; //$NON-NLS-1$
        String COST_ANALYSIS = CTOOL16 + "costing.gif"; //$NON-NLS-1$ 
    }

    /**
     * Collection of extension points defined by this plugin.
     * 
     * @since 5.0
     */
    interface ExtensionPoints {
        /**
         * The extension point that allows post processing of a JDBC import.
         * 
         * @since 5.0
         */
        interface JdbcImportPostProcessor {
            String ID = "jdbcImportPostProcessor"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class"; //$NON-NLS-1$
            String CLASS_NAME = "name"; //$NON-NLS-1$
        }
    }
}
