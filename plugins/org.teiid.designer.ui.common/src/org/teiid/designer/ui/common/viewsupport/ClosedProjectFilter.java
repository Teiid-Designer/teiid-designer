/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.viewsupport;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.common.UiConstants;

/**
 * ClosedProjectFilter
 *
 * @since 8.0
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
        
        // Only find and check projects that open, are Modeler-based AND not hidden projects
        if (theElement instanceof IResource) {
        	try {
        		IResource res = (IResource)theElement;
	            result = res.getProject().isOpen() &&
	            		 (res.getProject().getNature(ModelerCore.NATURE_ID) != null) &&
	            		 (res.getProject().getNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID) == null);
	        } catch (final CoreException err) {
	            UiConstants.Util.log(err);
	            return false;
	        }
        }

        return result;
    }

}
