/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.viewsupport;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/** 
 * @since 8.0
 */
public class NonModelViewerFilter extends ViewerFilter {

    /** 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 5.0.1
     */
    @Override
    public boolean select(Viewer theViewer,
                          Object theParentElement,
                          Object theElement) {
        boolean result = true;
        
        if ((theElement instanceof IFile) && !ModelUtilities.isModelingRelatedFile((IResource)theElement)) {
            result = false;
        }
        
        return result;
    }

}
