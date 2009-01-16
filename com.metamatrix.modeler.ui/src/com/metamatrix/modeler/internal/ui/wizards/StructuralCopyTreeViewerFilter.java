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

package com.metamatrix.modeler.internal.ui.wizards;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.ui.tree.TreeSplitter;

/**
 * StructuralCopyTreeViewerFilter
 */
public class StructuralCopyTreeViewerFilter extends ViewerFilter {
	public StructuralCopyTreeViewerFilter() {
		super();
	}
	
	@Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean okay = false;
		if (element instanceof EObject) {
			EObject node = (EObject)element;
			if (ModelObjectUtilities.isPrimaryMetamodelObject(node)) {
				okay = true;
            }

        // support treeSplitter objects, if they come through:
        } else if (element instanceof TreeSplitter) {
            okay = true;
        } // endif

		return okay;
	}	
}
