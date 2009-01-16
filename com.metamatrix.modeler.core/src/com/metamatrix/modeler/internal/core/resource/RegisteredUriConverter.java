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

package com.metamatrix.modeler.internal.core.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ContentHandler;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import com.metamatrix.modeler.core.ModelerCore;

/**
 * The RegisteredUriConverter allows one to register {@link InputStream} instances (each with
 * a {@link URI}) that are returned when the input stream contains the resource contents
 * for the supplied URI.  This class can be assigned to a {@link ResourceSet} when the model
 * that is only accessible as an InputStream must be loaded into a resource set.
 */
public class RegisteredUriConverter implements URIConverter {

    private Map<URI, InputStream> istreamByURI = new HashMap<URI, InputStream>();
    private final URIConverter delegate;

    /**
     * Construct an instance of RegisteredUriConverter.
     */
    public RegisteredUriConverter( final URIConverter delegate ) {
        super();
        if (delegate == null) {
            final String msg = ModelerCore.Util.getString("RegisteredUriConverter.The_URIConverter_may_not_be_null"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        this.delegate = delegate;
    }

    public void register( final URI uri, final InputStream stream ) {
        this.istreamByURI.put(uri,stream);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.impl.URIConverterImpl#createInputStream(org.eclipse.emf.common.util.URI)
     */
    public InputStream createInputStream(final URI uri) throws IOException {
        final InputStream result = this.istreamByURI.get(uri);
        if ( result != null ) {
            return result;
        }
        // Delegate back up to the
        return this.delegate.createInputStream(uri);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.URIConverter#normalize(org.eclipse.emf.common.util.URI)
     */
    public URI normalize(URI uri) {
        return this.delegate.normalize(uri);
    }

    /**
     * @see org.eclipse.emf.ecore.resource.URIConverter#getURIMap()
     */
    public Map<URI, URI> getURIMap() {
        return this.delegate.getURIMap();
    }

    /**
     * @see org.eclipse.emf.ecore.resource.URIConverter#createOutputStream(org.eclipse.emf.common.util.URI)
     */
    public OutputStream createOutputStream(URI uri) throws IOException {
        return this.delegate.createOutputStream(uri);
    }

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#contentDescription(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	public Map<String, ?> contentDescription( URI uri,
	                                          Map<?, ?> options ) throws IOException {
		return this.delegate.contentDescription(uri, options);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#createInputStream(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	public InputStream createInputStream( URI uri,
	                                      Map<?, ?> options ) throws IOException {
		return this.delegate.createInputStream(uri, options);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#createOutputStream(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	public OutputStream createOutputStream( URI uri,
	                                        Map<?, ?> options ) throws IOException {
        return this.delegate.createOutputStream(uri);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#delete(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	public void delete( URI uri,
	                    Map<?, ?> options ) throws IOException {
        this.delegate.delete(uri, options);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#exists(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	public boolean exists( URI uri,
	                       Map<?, ?> options ) {
        return this.delegate.exists(uri, options);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#getAttributes(org.eclipse.emf.common.util.URI, java.util.Map)
	 */
	public Map<String, ?> getAttributes( URI uri,
	                                     Map<?, ?> options ) {
		return this.delegate.getAttributes(uri, options);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#getContentHandlers()
	 */
	public EList<ContentHandler> getContentHandlers() {
		return this.delegate.getContentHandlers();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#getURIHandler(org.eclipse.emf.common.util.URI)
	 */
	public URIHandler getURIHandler( URI uri ) {
		return this.delegate.getURIHandler(uri);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#getURIHandlers()
	 */
	public EList<URIHandler> getURIHandlers() {
		return this.delegate.getURIHandlers();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see org.eclipse.emf.ecore.resource.URIConverter#setAttributes(org.eclipse.emf.common.util.URI, java.util.Map,
	 *      java.util.Map)
	 */
	public void setAttributes( URI uri,
	                           Map<String, ?> attributes,
	                           Map<?, ?> options ) throws IOException {
		this.delegate.setAttributes(uri, attributes, options);
	}
}
