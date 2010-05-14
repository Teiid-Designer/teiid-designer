/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;
import org.eclipse.xsd.impl.XSDSchemaImpl;

/**
 * This class acts as a holder for an xsd that could not be resolved. This will allow us 
 * to process a schema with an invalid path just as we would process a resolvable schema. We need
 * this additional layer since we are unable to create a XSDSchemaImpl object with an un-resolvanle resource.
 * 
 * @since 5.0
 *
 */
public class NonResolvableXSDSchema extends XSDSchemaImpl {

	/**
	 * The URI of the unresolvable schema.
	 */
	private URI uri = null;

	/**
	 * Constructor to initialize the URI
	 * @param uri
	 */
	public NonResolvableXSDSchema(URI uri) {
		this.uri = uri;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.emf.ecore.EObject#eResource()
	 */
	@Override
    public Resource eResource() {
		ResourceFactoryImpl eResource = new ResourceFactoryImpl();
		return eResource.createResource(uri);
	}
    
    /**
     * Obtains the unresolvable <code>URI</code>. 
     * @return the URI
     * @since 5.0.2
     */
    public URI getUri() {
        return this.uri;
    }

}
