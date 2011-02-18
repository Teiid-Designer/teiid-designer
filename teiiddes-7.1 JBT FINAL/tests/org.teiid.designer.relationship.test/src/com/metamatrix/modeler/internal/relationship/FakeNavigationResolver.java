/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * This is a FakeNavigationResolver that does not resolve the URI for an EObject to a real EObject, but instead creates a fake
 * EObject for each URI. It does, however, maintain the list of fake EObject instances, and returns the same EObject instance for
 * a matching URI.
 */
public class FakeNavigationResolver extends NavigationResolver {

    private final Map uriToEObject;
    private final Map eObjectToUri;

    /**
     * Construct an instance of FakeNavigationResolver.
     */
    public FakeNavigationResolver() {
        super();
        this.uriToEObject = new HashMap();
        this.eObjectToUri = new HashMap();
    }

    /**
     * This implementation simply creates a new EObject for each URI.
     * 
     * @see com.metamatrix.modeler.internal.relationship.NavigationContextCache#doResolve(java.lang.String)
     */
    @Override
    public EObject resolve( String uri ) {
        final EObject existing = (EObject)this.uriToEObject.get(uri);
        if (existing != null) {
            return existing;
        }
        final EObject result = EcoreFactory.eINSTANCE.createEObject();
        this.uriToEObject.put(uri, result);
        this.eObjectToUri.put(result, uri);
        return result;
    }

    /**
     * @see com.metamatrix.modeler.internal.relationship.NavigationResolver#getUri(org.eclipse.emf.ecore.EObject)
     */
    @Override
    public URI getUri( EObject object ) {
        final String uri = (String)this.eObjectToUri.get(object);
        if (uri != null) {
            return URI.createURI(uri);
        }
        return null;
    }

    /**
     * Return the map of URI to EObject instances, populated via calls to
     * {@link NavigationContextCache#getNavigationContext(NavigationContextInfo)}
     * 
     * @return
     */
    public Map getUriToEObjectMap() {
        return this.uriToEObject;
    }

}
