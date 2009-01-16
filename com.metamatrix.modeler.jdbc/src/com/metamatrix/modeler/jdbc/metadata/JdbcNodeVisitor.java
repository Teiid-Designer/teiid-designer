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
