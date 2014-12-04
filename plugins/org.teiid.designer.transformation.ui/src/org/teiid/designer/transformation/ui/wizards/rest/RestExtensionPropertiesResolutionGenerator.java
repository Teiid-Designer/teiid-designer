/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.wizards.rest;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

public class RestExtensionPropertiesResolutionGenerator implements IMarkerResolutionGenerator {
	 public static final String HAS_OLD_REST_PREFIX = "hasOldRestPrefix"; //$NON-NLS-1$

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {
    	Collection<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
    	
    	if( marker.getAttribute(HAS_OLD_REST_PREFIX, false) ) {
    		resolutions.add(new RestExtensionPropertiesMarkerResolution());
    	} 
    	
        return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
	}

}
