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

package com.metamatrix.rose.internal.impl;

import java.util.List;

import org.eclipse.emf.importer.rose.parser.RoseNode;

import com.metamatrix.rose.internal.IAmbiguousReference;

/**
 * @since 4.1
 */
public final class AmbiguousReference implements
                                     IAmbiguousReference {

    //============================================================================================================================
    // Variables

    private Object referencer;
    private RoseNode node;
    private String name;
    private List availObjs;
    private Object refdObj;
    private boolean resolved = false;
    private String type;

    //============================================================================================================================
    // Constructors

    /**
     * @param referencer
     * @param type
     * @param node
     * @param name
     * @param objects
     * @since 4.1
     */
    public AmbiguousReference(final Object referencer,
                              final String type,
                              final RoseNode node,
                              final String name,
                              final List objects) {
        this.referencer = referencer;
        this.type = type;
        this.node = node;
        this.name = name;
        this.availObjs = objects;
    }

    //============================================================================================================================
    // Implemented Methods

    /**
     * @see com.metamatrix.rose.internal.IAmbiguousReference#getAvailableObjects()
     * @since 4.1
     */
    public List getAvailableObjects() {
        return this.availObjs;
    }

    /**
     * @see com.metamatrix.rose.internal.IAmbiguousReference#getName()
     * @since 4.1
     */
    public String getName() {
        return this.name;
    }

    /**
     * @see com.metamatrix.rose.internal.IAmbiguousReference#getReferencedObject()
     * @since 4.1
     */
    public Object getReferencedObject() {
        return this.refdObj;
    }

    /**
     * @see com.metamatrix.rose.internal.IAmbiguousReference#getReferencer()
     * @since 4.1
     */
    public Object getReferencer() {
        return this.referencer;
    }

    /**
     * @see com.metamatrix.rose.internal.IAmbiguousReference#getType()
     * @since 4.1
     */
    public String getType() {
        return this.type;
    }

    /**
     * @see com.metamatrix.rose.internal.IAmbiguousReference#isResolved()
     * @since 4.1
     */
    public boolean isResolved() {
        return this.resolved;
    }

    //============================================================================================================================
    // Property Methods

    /**
     * @return The Rose QUID of the object referenced.
     * @since 4.1
     */
    public String getQuid() {
        return this.node.getRoseRefId();
    }

    /**
     * @return The referencer's Rose QUID.
     * @since 4.1
     */
    public String getReferencerQuid() {
        return this.node.getRoseId();
    }

    /**
     * @param object
     * @since 4.1
     */
    public void setReferencedObject(final Object object) {
        this.refdObj = object;
        this.resolved = true;
    }
}
