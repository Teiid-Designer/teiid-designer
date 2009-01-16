/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
