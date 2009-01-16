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
