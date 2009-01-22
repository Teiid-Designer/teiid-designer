/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.genericimport.ui;

import com.metamatrix.ui.UiConstants;


/**
 * PluginConstants for the GenericImportUiPlugin.
 */
public interface PluginConstants {
    //============================================================================================================================
    // Constants

    public static final String EMPTY_STRING = "";  //$NON-NLS-1$
    
    //============================================================================================================================
    // Image constants
    
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images
    extends UiConstants.Images {
        String IMPORT_PROJECT_ICON  = WIZBAN   + "import_project.gif"; //$NON-NLS-1$
    }

}
