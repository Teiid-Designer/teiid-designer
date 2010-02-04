/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;

/**
 * A cache of JdbcNode instances.
 */
public class JdbcNodeCache {
    
    private final Map nodesByPath;

    /**
     * Construct an instance of JdbcNodeCache.
     * 
     */
    public JdbcNodeCache() {
        super();
        this.nodesByPath = new HashMap();
    }
    
    public void put( final JdbcNode node ) {
        Assertion.isNotNull(node);
        nodesByPath.put(node.getPath(),node);
    }
    
    public JdbcNode get( final IPath path ) {
        Assertion.isNotNull(path);
        return (JdbcNode)nodesByPath.get(path);
    }
    
    public void remove( final JdbcNode node ) {
        Assertion.isNotNull(node);
        nodesByPath.remove(node.getPath());
    }

}
