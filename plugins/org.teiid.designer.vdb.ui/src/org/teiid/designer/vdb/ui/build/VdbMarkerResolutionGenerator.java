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
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * Quick fix to update model path in VDB for local workspace location change
 */
public class VdbMarkerResolutionGenerator implements IMarkerResolutionGenerator {
    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.IMarkerResolutionGenerator#getResolutions(org.eclipse.core.resources.IMarker)
     */
    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
    	Collection<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
    	if( marker.getAttribute(VdbBuilder.WRONG_PATH, false) || 
    		marker.getAttribute(VdbBuilder.OUT_OF_SYNC, false) ||
    	 marker.getAttribute(VdbBuilder.MISSING_UUID, false)) {
    		resolutions.add(new VdbModelPathResolution());
    	}
        return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
    }
}