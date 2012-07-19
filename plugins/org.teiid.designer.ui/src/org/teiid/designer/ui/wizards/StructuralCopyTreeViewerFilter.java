/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.wizards;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;

/**
 * StructuralCopyTreeViewerFilter
 *
 * @since 8.0
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
        } // endif

		return okay;
	}	
}
