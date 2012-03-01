/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.views.status;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * 
 */
public class ModelProjectViewFilter extends ViewerFilter {

    /**
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select( Viewer theViewer,
                           Object theParentElement,
                           Object theElement ) {
        boolean result = true;

        if (theElement instanceof IProject) {
            IProject proj = (IProject)theElement;
            return proj.isOpen() && ModelerCore.hasModelNature(proj);
        }

        return result;
    }

}
