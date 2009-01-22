/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
