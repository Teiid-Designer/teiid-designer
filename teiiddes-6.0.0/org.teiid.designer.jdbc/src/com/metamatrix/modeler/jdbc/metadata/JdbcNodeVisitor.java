/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

import com.metamatrix.modeler.jdbc.JdbcException;

/** 
 * This interface is implemented by objects that visit {@link JdbcNode JDBC nodes}.
 * <p> 
 * Usage:
 * <pre>
 * class Visitor implements JdbcNodeVisitor {
 *    public boolean visit(JdbcNode node) {
 *       // your code here
 *       return true;
 *    }
 * }
 * JdbcNode root = ...;
 * root.accept(new Visitor());
 * </pre>
 * </p> 
 * <p>
 * Clients may implement this interface.
 * </p>
 *
 * @see JdbcNode#accept
 */
public interface JdbcNodeVisitor {

    /**
     * Visit the supplied node. 
     * <p>
     * The default implementation of this method simply returns true.
     * </p>
     * @param node the node to visit; never null
     * @return true if the children of <code>node</code> should be visited, or false if they should not.
     * @throws JdbcException if the visit fails for some reason
     */
    public boolean visit(JdbcNode node) throws JdbcException;
}
