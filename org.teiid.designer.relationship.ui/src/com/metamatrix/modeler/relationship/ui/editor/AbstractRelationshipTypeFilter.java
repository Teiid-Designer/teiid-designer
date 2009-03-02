/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui.editor;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.metamatrix.metamodels.relationship.RelationshipType;

/**
 * AbstractRelationshipTypeFilter
 *      This filter will filter out abstract RelationshipType instances.
 */
public class AbstractRelationshipTypeFilter extends ViewerFilter {

    /**
     * Construct an instance of AbstractRelationshipTypeFilter.
     * 
     */
    public AbstractRelationshipTypeFilter() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean select(Viewer theViewer,
                          Object theParentElement,
                          Object theElement) {
        boolean result = true;
        
        if ( theElement instanceof RelationshipType ) {
            result = !((RelationshipType)theElement).isAbstract(); 
        }

        return result;
    }

}
