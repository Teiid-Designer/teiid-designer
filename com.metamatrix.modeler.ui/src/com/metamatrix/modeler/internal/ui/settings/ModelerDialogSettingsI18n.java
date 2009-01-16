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
