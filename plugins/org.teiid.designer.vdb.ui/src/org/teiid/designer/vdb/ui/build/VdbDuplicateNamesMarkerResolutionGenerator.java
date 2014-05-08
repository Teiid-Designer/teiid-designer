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
 * Quick fix to move xsd files from the VDB model collection into its
 * other files collection
 */
public class VdbDuplicateNamesMarkerResolutionGenerator extends VdbMarkerResolutionGenerator {

    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        Collection<IMarkerResolution> resolutions = new ArrayList<IMarkerResolution>();

        if( marker.getAttribute(VdbBuilder.DUPLICATE_MODEL_NAMES, false)) {
            resolutions.add(new VdbMigrateXsdFilesMarkerResolution());
        }

        return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
    }
}
