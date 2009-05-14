/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.selection;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * TreeSelectionProvider
 */
public interface TreeSelectionProvider {

    /**
     * Return the objects that are considered root-level objects.  Root-level objects do 
     * not have parents.
     * @return the List of root objects; never null
     * @throws CoreException if there is a problem obtaining the root-level objects.
     */
    public List getRoots() throws CoreException;
    
    /**
     * Convenience method to determine whether the supplied node is considered a root-level object.
     * @param node the node; may not be null
     * @return true if the node is considered to be a {@link #getRoots() root-level object}, or false
     * otherwise
     */
    public boolean isRoot( final Object node );
    
    /**
     * Return the parent for the supplied node.  Note that if:
     * <p>
     * <code>       Object parent = provider.getParent(child)</code>
     * </p><p>
     * then the following must return true:
     * </p><p>
     * <code>       provider.getChildren(parent).contains(child)</code>
     * </p>
     * @param node the node; may not be null
     * @return the object that is considered to be the parent of the supplied node, or null
     * if the supplied node is also a {@link #isRoot(Object) root node}.
     */
    public Object getParent( final Object node );
    
    /**
     * Return the children for the supplied node.  Note that if:
     * <p>
     * <code>       Object parent = provider.getParent(child)</code>
     * </p><p>
     * then the following must return true:
     * </p><p>
     * <code>       provider.getChildren(parent).contains(child)</code>
     * </p>
     * @param parentNode the parent node; may not be null
     * @return the children for the supplied parent node; never null
     */
    public List getChildren( final Object parentNode );
 
//    public String getMemento( final Object node);
//    
//    public Object getObject( final String memento);
    
}
