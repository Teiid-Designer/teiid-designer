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

package com.metamatrix.modeler.modelgenerator.wsdl.ui;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * DiagramUiConstants
 * @since 4.0
 */
public interface ModelGeneratorWsdlUiConstants {
    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = "com.metamatrix.modeler.modelgenerator.wsdl.ui"; //$NON-NLS-1$
     
    /** The dialog settings section to use for any settings saved. */
    String DIALOG_SETTINGS_SECTION = "ModelGeneratorWsdlUi"; //$NON-NLS-1$

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
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, PC.BUNDLE_NAME, ResourceBundle.getBundle(PC.BUNDLE_NAME));
  
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
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images {
        String ICON_PATH = "icons/full/"; //$NON-NLS-1$
        String CVIEW16 = ICON_PATH + "cview16/"; //$NON-NLS-1$
        String CTOOL16 = ICON_PATH + "ctool16/"; //$NON-NLS-1$
        String DTOOL16 = ICON_PATH + "dtool16/"; //$NON-NLS-1$
        String OBJ16 = ICON_PATH + "obj16/"; //$NON-NLS-1$
        String WIZBAN = ICON_PATH + "wizban/"; //$NON-NLS-1$
        
        String NEW_MODEL_BANNER = WIZBAN + "new_wsdl_wiz.gif"; //$NON-NLS-1$

        String IMPORT_WSDL_ICON = CTOOL16 + "import_wsdl.gif"; //$NON-NLS-1$

        String SERVICE_ICON = OBJ16   + "service_obj.gif"; //$NON-NLS-1$
        String OPERATION_ICON = OBJ16   + "operation_obj.gif"; //$NON-NLS-1$
        String PORT_ICON = OBJ16   + "port_obj.gif"; //$NON-NLS-1$
        String BINDING_ICON = OBJ16   + "binding_obj.gif"; //$NON-NLS-1$
    }
    
    /**
     * Contains constants for the available context help identifiers found in the helpContexts.xml file. 
     * @since 4.2
     */
    interface HelpContexts {
        String PREFIX = PLUGIN_ID + '.';
        String WSDL_SELECTION_PAGE = PREFIX + "wsdlSelectionPage"; //$NON-NLS-1$
    }
}


