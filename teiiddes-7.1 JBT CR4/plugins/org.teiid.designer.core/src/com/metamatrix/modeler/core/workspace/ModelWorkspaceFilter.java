/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

/**
 * The ModelWorkspaceFilter interface defines the operations that allow
 * a {@link ModelWorkspaceView} to determine whether an object should
 * appear in the view.
 */
public interface ModelWorkspaceFilter {
    
    /**
     * Returns whether the given element makes it through this filter.
     *
     * @param parentElement the parent element
     * @param element the element
     * @return <code>true</code> if element is included in the
     *   filtered set, and <code>false</code> if excluded
     */
    public boolean select( Object parentElement, Object element );

}
