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
