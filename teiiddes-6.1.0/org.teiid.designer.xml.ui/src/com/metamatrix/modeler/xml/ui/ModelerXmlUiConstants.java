/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
    String PLUGIN_ID = "org.teiid.designer.xml.ui"; //$NON-NLS-1$
    
    String PACKAGE_ID = ModelerXmlUiConstants.class.getPackage().getName();

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
    PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, PC.I18N_NAME, ResourceBundle.getBundle(PC.I18N_NAME));

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
