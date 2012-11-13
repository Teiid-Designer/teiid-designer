/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.ui.actions;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

/**
 * Provides the Quick Fix extension for resolving MED problem marker issues with MED files and models.
 */
public class MedMarkerResolutionGenerator implements IMarkerResolutionGenerator {
    @Override
    public IMarkerResolution[] getResolutions(IMarker marker) {
        return new IMarkerResolution[] {new LegacyClassnameResolution()};
    }
}

