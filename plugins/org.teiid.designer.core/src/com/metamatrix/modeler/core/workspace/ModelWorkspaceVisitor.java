/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

/** 
 * This interface is implemented by objects that visit {@link JdbcNode JDBC nodes}.
 * <p> 
 * Usage:
 * <pre>
 * class Visitor implements ModelWorkspaceVisitor {
 *    public boolean visit(ModelWorkspaceItem item) {
 *       // your code here
 *       return true;
 *    }
 * }
 * ModelWorkspaceItem item = ...;
 * item.accept(new Visitor());
 * </pre>
 * </p> 
 * <p>
 * Clients may implement this interface.
 * </p>
 *
 * @see ModelWorkspaceItem#accept
 */
public interface ModelWorkspaceVisitor {

    /**
     * Visit the supplied {@link ModelWorkspaceItem item}. 
     * <p>
     * The default implementation of this method simply returns true.
     * </p>
     * @param item the item to visit; never null
     * @return true if the children of <code>item</code> should be visited, or false if they should not.
     * @throws ModelWorkspaceException if the visit fails for some reason
     */
    public boolean visit(ModelWorkspaceItem item) throws ModelWorkspaceException;
}
