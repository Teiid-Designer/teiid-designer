/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xml.ui.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * SchemaFileViewerFilter is a ViewerFilter that only displays projects, folders, and
 * .xsd files within open projects.
 */
public class SchemaFileViewerFilter extends ViewerFilter {

    /**
     * Construct an instance of SchemaFileViewerFilter.
     */
    public SchemaFileViewerFilter() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        boolean result = false;
        
        // determine if project is open first
        boolean projectOpen = false;
        if (element instanceof IResource) {
            projectOpen = ((IResource)element).getProject().isOpen();
        }
        
        // look further if project open
        if(projectOpen) {
            if ( element instanceof IContainer || element instanceof IWorkspaceRoot ) {
                result = true;
            }
            // only show xsd files
            if ( element instanceof IFile ) {
                result = ModelUtil.isXsdFile((IFile) element);
            } 
        }
        return result;
    }

}
