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

package com.metamatrix.ui.internal.viewsupport;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;


/**
 * A <code>ViewerFilter</code> that can contain other filters.
 * @since 5.0.2
 */
public class CompositeViewerFilter extends ViewerFilter {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private Set<ViewerFilter> filters = new HashSet<ViewerFilter>(5);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Adds the specified filter.
     * @param theFilter the filter being added
     * @since 5.0.2
     */
    public void addFilter(ViewerFilter theFilter) {
        this.filters.add(theFilter);
    }

    /**
     * Removes the specified filter.
     * @param theFilter the filter being removed
     * @return <code>true</code> if the filter was successfully removed; <code>false</code> otherwise.
     * @since 5.0.2
     */
    public boolean removeFilter(ViewerFilter theFilter) {
        return this.filters.remove(theFilter);
    }

    /** 
     * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 5.0.2
     */
    @Override
    public boolean select(Viewer theViewer,
                          Object theParentElement,
                          Object theElement) {
        boolean result = true;
        Iterator<ViewerFilter> itr = this.filters.iterator();
        
        while (itr.hasNext()) {
            ViewerFilter filter = itr.next();
            
            if (!filter.select(theViewer, theParentElement, theElement)) {
                result = false;
                break;
            }
        }
        
        return result;
    }
    
}
