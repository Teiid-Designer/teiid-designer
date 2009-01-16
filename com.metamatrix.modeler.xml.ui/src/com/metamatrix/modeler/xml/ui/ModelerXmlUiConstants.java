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

package com.metamatrix.modeler.xml.ui;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * ModelerXmlUiConstants
 */
public interface ModelerXmlUiConstants {

    //======================================
    // Constants
    
    /**
     * The ID of the plug-in containing this constants class.
     * @since 4.0
     */
    String PLUGIN_ID = "com.metamatrix.modeler.xml.ui"; //$NON-NLS-1$ 

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

    //======================================
    // Debug constants

    /**
     * Contains debug contexts defined by this plug-in.
     * @since 4.0
     */
    interface Debug {    
    }

    /**<p>
     * Constants common to all classes in this plug-in.
     * </p>
     * @since 4.0
     */    
    interface I18n {
        /**
         * Contains private constants used by other constants within this class.
         * @since 4.0
         */
        class PC {
//            private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(UiConstants.class);
        }
    }

	/**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images {
        class PC {
            private static final String FULL  = "icons/full/"; //$NON-NLS-1$
            private static final String CTOOL16 = FULL + "ctool16/"; //$NON-NLS-1$
            private static final String OVR16 = FULL + "ovr16/"; //$NON-NLS-1$
        }
        
        String BUILD_FROM_XSD           	= PC.CTOOL16 + "buildFromXSD.gif"; //$NON-NLS-1$
        
        String RECURSIVE 					= PC.OVR16 + "recursive.gif"; //$NON-NLS-1$
        String INCOMPLETE 					= PC.OVR16 + "incomplete.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_N 			= PC.OVR16 + "XSDOccurrenceN.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_NToM 			= PC.OVR16 + "XSDOccurrenceNToM.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_NToUnbounded 	= PC.OVR16 + "XSDOccurrenceNToUnbounded.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_One 			= PC.OVR16 + "XSDOccurrenceOne.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_OneToN 		= PC.OVR16 + "XSDOccurrenceOneToN.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_OneToUnbounded = PC.OVR16 + "XSDOccurrenceOneToUnbounded.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_Zero 			= PC.OVR16 + "XSDOccurrenceZero.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_ZeroToN 		= PC.OVR16 + "XSDOccurrenceZeroToN.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_ZeroToOne 	= PC.OVR16 + "XSDOccurrenceZeroToOne.gif"; //$NON-NLS-1$
        String XSD_OCCURRENCE_ZeroToUnbounded = PC.OVR16 + "XSDOccurrenceZeroToUnbounded.gif"; //$NON-NLS-1$
    }
}
