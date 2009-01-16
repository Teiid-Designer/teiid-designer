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

package com.metamatrix.modeler.internal.xml.aspects.sql;

import org.eclipse.emf.ecore.resource.ResourceSet;


/** 
 * @since 4.2
 */
public class MappingContext {
    
    private final ResourceSet resourceSet;

    /** 
     * 
     * @since 4.2
     */
    public MappingContext(final ResourceSet resourceSet) {
        super();
        this.resourceSet = resourceSet;
    }

    /** 
     * @return Returns the ResourceSet instance defining the set of models for mapping.
     * @since 4.2
     */
    public ResourceSet getResourceSet() {
        return this.resourceSet;
    }
}
