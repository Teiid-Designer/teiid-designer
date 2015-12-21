package org.teiid.designer.relational.ui.extension;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;


public class RelationalExtensionMarkerResolutionGenerator implements IMarkerResolutionGenerator {
	private static final String IS_OLD_RELATIONAL_MED = "isOldRelationalMed"; //$NON-NLS-1$
	    @Override
	    public IMarkerResolution[] getResolutions(IMarker marker) {
	    	Collection<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();
	    	
	    	if( marker.getAttribute(IS_OLD_RELATIONAL_MED, false) ) {
	    		resolutions.add(new RelationalExtensionVersionResolution("relational"));
	    	}
	    	
	    	return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
	    }
	}