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


/**
 * PluginConstants
 * This class is intended for use within this plugin only.
 * @since 4.0
 */
public interface PluginConstants {
    //============================================================================================================================
    // Constants


    //============================================================================================================================
    // Image constants

    /**
     * Keys for images and image descriptors stored in the image registry.
     * @since 4.0
     */
    interface Images
        extends UiConstants.Images {
        String DIFF_CHANGED_AND_CHANGED_BELOW_DECORATOR = OVR16 + "chg_and_chg_below_ov.gif"; //$NON-NLS-1$
		String DIFF_CHANGED_BELOW_DECORATOR = OVR16 + "chg_below_ov.gif"; //$NON-NLS-1$
        String DIFF_CHANGED_DECORATOR = OVR16 + "chg_ov.gif"; //$NON-NLS-1$
        String DIFF_FIRST_DECORATOR = OVR16 + "first_ov.gif"; //$NON-NLS-1$
		String DIFF_NEW_DECORATOR = OVR16 + "new_ov.gif"; //$NON-NLS-1$
		String DIFF_OLD_DECORATOR = OVR16 + "old_ov.gif"; //$NON-NLS-1$
		String DIFF_SECOND_DECORATOR = OVR16 + "second_ov.gif"; //$NON-NLS-1$
        String NEXT_NAV = CTOOL16 + "next_nav.gif"; //$NON-NLS-1$
        String PREV_NAV = CTOOL16 + "prev_nav.gif"; //$NON-NLS-1$
    }

    interface Prefs {

        // Appearance Preferences
        interface Appearance {
//            class PC {
//                private static final String PREFIX = "modeler.preference.diagram."; //$NON-NLS-1$
//            }
//
//            public static final String MAPPING_BKGD_COLOR = PC.PREFIX + "mapping.backgroundcolor"; //$NON-NLS-1$
//
//            public static final PreferenceKeyAndDefaultValue[] PREFERENCES =
//                    new PreferenceKeyAndDefaultValue[] {
//                        new PreferenceKeyAndDefaultValue(MAPPING_BKGD_COLOR,
//                                new RGB(175, 220, 250)),
//                    };
        }

    }
}
