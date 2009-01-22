/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.settings;

import org.eclipse.osgi.util.NLS;

public final class ModelerDialogSettingsI18n extends NLS {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
	public static String ProblemsViewFilter_Enabled; 				// org.eclipse.ui.views.problem.filter.enabled
	public static String ProblemsViewFilter_FilterOnMarkerLimit; 	// org.eclipse.ui.views.problem.filter.filterOnMarkerLimit
	public static String ProblemsViewFilter_MarkerLimit; 			// org.eclipse.ui.views.problem.filter.markerLimit
	public static String ProblemsViewFilter_OnResource; 			// org.eclipse.ui.views.problem.filter.onResource
	

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // INITIALIZER
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    static {
        final String BUNDLE_NAME = "com.metamatrix.modeler.internal.ui.settings.modelerDialogSettingsI18n";//$NON-NLS-1$
        
        // load message values from bundle file
        NLS.initializeMessages(BUNDLE_NAME, ModelerDialogSettingsI18n.class);
    }

	
}
