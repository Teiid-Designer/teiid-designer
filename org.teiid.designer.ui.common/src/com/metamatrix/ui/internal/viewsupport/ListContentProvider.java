/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.viewsupport;

import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;


/** 
 * A simple implementation of IStructuredContentProvider that supports any Collection input
 * @since 4.2
 */
public class ListContentProvider implements IStructuredContentProvider {

    /** 
     * Construct a ListContentProvider 
     * @since 4.2
     */
    public ListContentProvider() {
    }

    /** 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    public Object[] getElements(Object inputElement) {
        if ( inputElement instanceof Collection ) {
            return ((Collection) inputElement).toArray();
        }
        return null;
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     * @since 4.2
     */
    public void dispose() {
    }

    /** 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     * @since 4.2
     */
    public void inputChanged(Viewer viewer,
                             Object oldInput,
                             Object newInput) {
    }

}
