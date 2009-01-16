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

package com.metamatrix.vdb.internal.edit;

import java.io.InputStream;

import org.eclipse.core.runtime.IPath;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.vdb.edit.VdbEditException;
import com.metamatrix.vdb.edit.manifest.ModelReference;

/**
 * VdbInputResourceFinder
 */
public interface VdbInputResourceFinder {
    
    /**
     * Find the EMF resource given by the supplied model path.
     * @param modelPath the path to the model
     * @return the EMF {@link Resource} for the supplied model
     * @throws VdbEditException if there is an error retrieving the resource
     */
    Resource getEmfResource( final IPath modelPath ) throws VdbEditException;
    
    /**
     * Find the EMF resource given by the supplied model reference.
     * @param modelRef the ModelReference to use
     * @return the EMF {@link Resource} for the supplied model
     * @throws VdbEditException if there is an error retrieving the resource
     */
    Resource getEmfResource( final ModelReference modelRef ) throws VdbEditException;
    
    /**
     * Return an InputStream to the EMF resource.
     * @param emfResource the resource
     * @return the InputStream to the resource or null if one cannot be created
     * @throws VdbEditException if there is an error retrieving the stream
     */
    InputStream getEmfResourceStream( final Resource emfResource ) throws VdbEditException;
    
    /**
     * Return the relative path to the EMF resource.
     * @param emfResource the resource
     * @return the path to the supplied {@link Resource}
     * @throws VdbEditException if there is an error retrieving the path
     */
    IPath getEmfResourcePath( final Resource emfResource ) throws VdbEditException;

}
