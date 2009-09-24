/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

/**
 * The ModelWorkspaceSelectionFilter interface defines the operations that allow
 * a {@link ModelWorkspaceSelections} to determine whether the selection state 
 * of an object can be explicitly set, or whether the selection state of an object
 * is determined from other objects.
 */
public interface ModelWorkspaceSelectionFilter {
    
    /**
     * Returns whether the given element makes it through this filter.
     *
     * @param element the element
     * @return <code>true</code> if element is selectable, 
     * and <code>false</code> otherwise.
     */
    public boolean isSelectable( Object element );

}
