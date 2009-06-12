/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.tree;

import org.eclipse.jface.viewers.ITreeContentProvider;

import com.metamatrix.ui.internal.widget.DefaultContentProvider;

/**
 * @since 4.0
 */
public abstract class AbstractTreeContentProvider extends DefaultContentProvider implements
                                                                                ITreeContentProvider {

    // ===========================================================================================================================
    // Methods

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 5.0.1
     */
    public Object[] getChildren(Object parent) {
        return super.getElements(parent);
    }

    /** 
     * @see com.metamatrix.ui.internal.widget.DefaultContentProvider#getElements(java.lang.Object)
     * @since 5.0.1
     */
    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @since 4.0
     */
    public boolean hasChildren(Object element) {
        return (getChildren(element).length > 0);
    }
}
