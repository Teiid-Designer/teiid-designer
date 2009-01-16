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
