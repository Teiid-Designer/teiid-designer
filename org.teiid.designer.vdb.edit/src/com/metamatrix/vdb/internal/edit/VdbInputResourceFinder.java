/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
