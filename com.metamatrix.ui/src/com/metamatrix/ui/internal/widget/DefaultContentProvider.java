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

package com.metamatrix.ui.internal.widget;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @since 4.0
 */
public class DefaultContentProvider implements
                                    IStructuredContentProvider {

    // ============================================================================================================================
    // Implemented Methods

    /**
     * Does nothing.
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @since 4.0
     */
    public void dispose() {
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 5.0.1
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof Map) {
            inputElement = ((Map)inputElement).entrySet();
        }
        if (inputElement instanceof Object[]) {
            return (Object[])inputElement;
        } else if (inputElement instanceof Collection) {
            return ((Collection)inputElement).toArray();
        }
        return new Object[] {
            inputElement
        };
    }

    /**
     * Does nothing.
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     * @since 4.0
     */
    public void inputChanged(final Viewer viewer,
                             final Object oldInput,
                             final Object newInput) {
    }
}
