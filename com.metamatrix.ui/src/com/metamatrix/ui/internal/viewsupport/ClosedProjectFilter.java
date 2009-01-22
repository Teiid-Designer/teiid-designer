/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.viewsupport;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * ClosedProjectFilter
 */
public class ClosedProjectFilter extends ViewerFilter {

    /**
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer theViewer,
                          Object theParentElement,
                          Object theElement) {
        boolean result = true;
        
        if (theElement instanceof IResource) {
            result = ((IResource)theElement).getProject().isOpen();
        }

        return result;
    }

}
