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
