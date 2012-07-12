/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relationship.ui.properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.relationship.RelationshipFolder;
import org.teiid.designer.metamodels.relationship.RelationshipPackage;
import org.teiid.designer.metamodels.relationship.RelationshipType;
import org.teiid.designer.relationship.ui.UiConstants;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * RelationshipTypeViewerFilter
 */
public class RelationshipTypeViewerFilter extends ViewerFilter {

    /**
     * Construct an instance of RelationshipModelViewerFilter.
     * 
     */
    public RelationshipTypeViewerFilter() {
        super(); 
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if ( element instanceof IContainer ) {
            return true;
        }
        
        if ( element instanceof IFile ) {
            if ( ModelUtilities.isModelFile((IFile) element) ) {
                try {
                    ModelResource resource = ModelUtil.getModelResource((IFile) element, false);
                    if ( resource != null && resource.getPrimaryMetamodelDescriptor() != null ) {
                        if ( RelationshipPackage.eNS_URI.equals(resource.getPrimaryMetamodelDescriptor().getNamespaceURI()) ) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    UiConstants.Util.log(e);
                }
            }
        }
        
        if ( element instanceof RelationshipType ) {
            return true;
        }
        
        if ( element instanceof RelationshipFolder ) {
            return true;
        }

        if ( element instanceof RelationshipTypeFolder ) {
            return true;
        }

        return false;
    }

}
