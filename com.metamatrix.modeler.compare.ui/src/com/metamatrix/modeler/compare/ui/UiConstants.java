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

package com.metamatrix.modeler.compare.ui;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * DiagramUiConstants
 * @since 4.0
 */
public interface UiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = "com.metamatrix.modeler.compare.ui"; //$NON-NLS-1$
     
    /**
     * Contains private constants used by other constants within this class.
     * @since 4.0
     */  
    class PC {
        private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    }
    
    /**
     * Provides access to the plugin's log and to it's resources.
     * @since 4.0
     */
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));
  
    //============================================================================================================================
    // Extension constants
    
    /**
     * Constants related to extensions, including all extension ID's.
     * @since 4.0
     */
    interface Extensions {
//        String DIAGRAM_EDITOR           = "diagramEditorPage"; //$NON-NLS-1$
    }
    
    /**
     * Constants related to extension points, including all extension point ID's and extension point schema component names.
     * @since 4.0
     */
    interface ExtensionPoints {

    }
        
    /**
     * Constants related to properties on a Diagram Model Node
     * @since 4.0
     */
    interface Errors {
        String SOME_MAPPING_FAILURE = "MappingErrors.someError"; //$NON-NLS-1$
     }
    
    /**
     * Constants related to styles of drawing diagram connections ('routers')
     * @since 4.0
     */
    interface Colors {
//        Color SELECTION                 = ColorConstants.lightBlue;
//        Color INPUT_SET_HEADER          = new Color(null, 250, 160, 210);
    } 
    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images {
         String FULL  = "icons/full/"; //$NON-NLS-1$
         String OVR16  = FULL + "ovr16/"; //$NON-NLS-1$
         String CTOOL16  = FULL + "ctool16/"; //$NON-NLS-1$
    }
}


