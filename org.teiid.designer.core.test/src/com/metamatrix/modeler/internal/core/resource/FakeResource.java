/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;

/**
 * FakeResource
 */
public class FakeResource extends ResourceImpl {

    /**
     * Construct an instance of FakeResource.
     */
    public FakeResource( final String uri ) {
        super(URI.createURI(uri));
    }

}
