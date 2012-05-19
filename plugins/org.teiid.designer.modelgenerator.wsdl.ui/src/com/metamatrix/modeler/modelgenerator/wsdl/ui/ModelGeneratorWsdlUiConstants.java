/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
    String PLUGIN_ID = "org.teiid.designer.modelgenerator.wsdl.ui"; //$NON-NLS-1$
    
    String PACKAGE_ID = ModelGeneratorWsdlUiConstants.class.getPackage().getName();
     
    /** The dialog settings section to use for any settings saved. */
    String DIALOG_SETTINGS_SECTION = "ModelGeneratorWsdlUi"; //$NON-NLS-1$

    /**
     * Contains private constants used by other constants within this class.
     * @since 4.0
     */  
    class PC {
        protected static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    }
    
    /**
     * Provides access to the plugin's log and to it's resources.
     * @since 4.0
     */
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));
  
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
        String COLUMN_ICON = OBJ16   + "column.gif"; //$NON-NLS-1$
        
        String XSD_COMPLEX_ELEMENT_ICON = OBJ16 + "xsd-complex-element.gif"; //$NON-NLS-1$
        String XSD_ELEMENT_ICON = OBJ16 + "xsd-element.gif"; //$NON-NLS-1$
        String XSD_ATTRIBUTE_ICON = OBJ16 + "xsd-attribute.gif"; //$NON-NLS-1$
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


