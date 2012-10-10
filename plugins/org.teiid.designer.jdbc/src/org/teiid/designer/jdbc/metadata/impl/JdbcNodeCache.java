/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IPath;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.jdbc.metadata.JdbcNode;


/**
 * A cache of JdbcNode instances.
 *
 * @since 8.0
 */
public class JdbcNodeCache {

    private final Map nodesByPath;

    /**
     * Construct an instance of JdbcNodeCache.
     */
    public JdbcNodeCache() {
        super();
        this.nodesByPath = new HashMap();
    }

    public void put( final JdbcNode node ) {
        CoreArgCheck.isNotNull(node);
        nodesByPath.put(node.getPath(), node);
    }

    public JdbcNode get( final IPath path ) {
        CoreArgCheck.isNotNull(path);
        return (JdbcNode)nodesByPath.get(path);
    }

    public void remove( final JdbcNode node ) {
        CoreArgCheck.isNotNull(node);
        nodesByPath.remove(node.getPath());
    }

}
