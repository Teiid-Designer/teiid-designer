/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;

/**
 * StructuralCopyFileViewerFilter is a ViewerFilter that displays items other than IFiles, and
 * IFiles that are model files and whose MetamodelDescriptor matches that passed in.
 */
public class StructuralCopyFileViewerFilter extends ViewerFilter implements UiConstants {
	private MetamodelDescriptor metamodelDescriptor;
	
    /**
     * Construct an instance of StructuralCopyFileViewerFilter.
     */
    public StructuralCopyFileViewerFilter(MetamodelDescriptor descriptor) {
        super();
        this.metamodelDescriptor = descriptor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        boolean result = false;
        if ( element instanceof IProject || element instanceof IWorkspaceRoot ) {
            result = true;
        } else if ( element instanceof IFile ) {
        	IFile file = (IFile)element;
			if (ModelUtilities.isModelFile(file)) {
        		boolean exceptionOccurred = false;
        		ModelResource modelResource = null;
        		try {
        			modelResource= ModelUtilities.getModelResource(file, true);
        		} catch (Exception ex) {
        			Util.log(ex);
        			result = false;
        			exceptionOccurred = true;
        		}
        		if (!exceptionOccurred) {
        			MetamodelDescriptor descriptor = null;
        			try {
        				descriptor = modelResource.getPrimaryMetamodelDescriptor();
        			} catch (Exception ex) {
        				Util.log(ex);
        				exceptionOccurred = true;
        				result = false;
        			}
        			if (!exceptionOccurred) {
						result = (this.metamodelDescriptor.equals(descriptor));
        			}
        		}
        	}
        } else {
        	result = true;
        }
        return result;
    }
}
