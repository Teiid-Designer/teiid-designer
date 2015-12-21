package org.teiid.designer.relational.ui.extension;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

public class RestExtensionMarkerResolutionGenerator implements IMarkerResolutionGenerator {
	private static final String IS_OLD_REST_VERSION_MED = "isOldRestVersionMed"; //$NON-NLS-1$
	    @Override
	    public IMarkerResolution[] getResolutions(IMarker marker) {
	    	Collection<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
	    	
	    	if( marker.getAttribute(IS_OLD_REST_VERSION_MED, false) ) {
	    		resolutions.add(new RelationalExtensionVersionResolution("rest"));
	    	} 
	    	
	    	return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
	    }
	}