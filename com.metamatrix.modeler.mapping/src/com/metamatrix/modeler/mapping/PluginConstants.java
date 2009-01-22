/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * PluginConstants  
 * This class is intended for use within this plugin only.
 * @since 4.0
 */
public interface PluginConstants {

    //===========================================
    // Constants

    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = "com.metamatrix.modeler.mapping"; //$NON-NLS-1$
     
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

    /**
     * Constants related to extension points, including all extension point ID's and extension point schema component names.
     * @since 4.0
     */
    interface ExtensionPoints {

        /**
         * The <code>ModelMapper</code> extension point defines the requirements for an extension
         * that wants to be able to be displayed in a mapping diagram.
         */
        interface ModelMapper {
            String ID = "modelMapper"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class";  //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
            String METAMODEL_URI = "metamodelUri"; //$NON-NLS-1$
        }


        /**
         * The <code>ModelMapper</code> extension point defines the requirements for an extension
         * that wants to be able to be displayed in a mapping diagram.
         */
        interface ChoiceObjectHandler {
            String ID = "choiceObjectHandler"; //$NON-NLS-1$
            String CLASS_ELEMENT = "class";  //$NON-NLS-1$
            String CLASSNAME = "name"; //$NON-NLS-1$
            String FACTORY_CLASS = "factoryClass"; //$NON-NLS-1$
        }

    }
    
}
