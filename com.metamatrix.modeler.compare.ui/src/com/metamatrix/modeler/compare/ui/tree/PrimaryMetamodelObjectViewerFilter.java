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

package com.metamatrix.modeler.compare.ui.tree;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;


/** 
 * @since 4.2
 */
public class PrimaryMetamodelObjectViewerFilter extends ViewerFilter {

    /** 
     * 
     * @since 4.2
     */
    public PrimaryMetamodelObjectViewerFilter() {
        super();
    }

    /** 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean select(Viewer viewer,
                          Object parentElement,
                          Object element) {
        
        if ( element instanceof DifferenceReport ) {
            return true;
        }
        if ( element instanceof Mapping && passesFilter( (Mapping)element ) ) {
            return true;
        }
        
        return false;
    }
    
    public boolean passesFilter( Mapping mapping ) {
        EObject eo = MappingTreeContentProvider.getEObjectForMapping( mapping );   
        
        if ( eo != null && ModelObjectUtilities.isPrimaryMetamodelObject( eo ) ) {
            return true;
        }
        return false;
    }

}
