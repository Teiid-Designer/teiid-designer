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
    String PLUGIN_ID = ModelerJdbcUiConstants.class.getPackage().getName();

    /**
     * Contains private constants used by other constants within this class.
     * 
     * @since 4.0
     */
    class PC {
        static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    }

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));

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
