/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui;

import com.metamatrix.ui.UiConstants;


/**
 * This class is intended for use within this plugin only.
 * @since 4.0
 */
public interface PluginConstants {

    static final String XSD_EXTENSION = ".xsd"; //$NON-NLS-1$
    
    //============================================================================================================================
    // Image constants
    
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.1
     */
    interface Images extends UiConstants.Images {
        String SEMANTICS_ICON = CVIEW16 + "semantics.gif"; //$NON-NLS-1$
    }

}
