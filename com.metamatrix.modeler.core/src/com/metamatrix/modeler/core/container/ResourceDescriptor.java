/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.container;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * ResourceDescriptor
 */
public interface ResourceDescriptor {
    
    /**
     * Return the unique identifier of the resource type.
     * @return the unique identifier
     */
    String getUniqueIdentifier();

    /**
     * Get the file extensions that this resource
     * @return
     */
    List getExtensions();

    /**
     * @return
     */
    List getProtocols();
    
    /**
     * Return the {@link org.eclipse.emf.ecore.resource.Resource.Factory Resource.Factory} that
     * can be used to create {@link org.eclipse.emf.ecore.resource.Resource} instances.
     * @return
     */
    Resource.Factory getResourceFactory() throws ModelerCoreException;

}
