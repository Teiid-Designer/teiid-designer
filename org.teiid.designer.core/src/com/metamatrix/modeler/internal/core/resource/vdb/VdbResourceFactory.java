/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource.vdb;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

/** 
 * VdbResourceFactory
 * @since 4.2
 */
public class VdbResourceFactory extends ResourceFactoryImpl {

    /**
     * Returns a newly allocated default resource {@link ResourceImpl#ResourceImpl(URI) implementation}.
     * @param uri the URI.
     * @return a new resource for the URI.
     */
    @Override
    public Resource createResource(URI uri) {
    	VdbResourceImpl resource = new VdbResourceImpl(uri);
    	return resource;
    }    
}
