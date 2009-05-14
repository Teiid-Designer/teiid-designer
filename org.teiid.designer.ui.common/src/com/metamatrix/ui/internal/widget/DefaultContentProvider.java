/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
