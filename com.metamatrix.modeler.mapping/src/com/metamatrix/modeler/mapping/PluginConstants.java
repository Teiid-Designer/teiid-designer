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
