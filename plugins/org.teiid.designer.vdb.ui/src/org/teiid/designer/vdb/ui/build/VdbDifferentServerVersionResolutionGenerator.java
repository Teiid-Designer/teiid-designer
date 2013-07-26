/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.vdb.ui.build;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;

/**
 *
 */
public class VdbDifferentServerVersionResolutionGenerator extends VdbMarkerResolutionGenerator {
	/**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IMarkerResolutionGenerator#getResolutions(org.eclipse.core.resources.IMarker)
     */
    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
    	Collection<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
    	
    	if( marker.getAttribute(VdbBuilder.DIFFERENT_VALIDATION_VERSION, false) ) {
    		resolutions.add(new VdbModelPathResolution());
    	} 
    	
        return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
    }
}
