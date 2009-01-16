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

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * A ResourceSetFinder is used by the ModelBufferManager to identify which {@link ResourceSet} should be used
 * for a particular IResource.
 */
public interface ResourceSetFinder {
    
    /**
     * Find and return the {@link ResourceSet} into which the specified resource file should be or has
     * been loaded.
     * @param resource the resource file, folder or project
     * @return the ResourceSet for the resource
     * @throws CoreException
     */
    public ResourceSet getResourceSet( IResource resource ) throws ModelWorkspaceException;

}
