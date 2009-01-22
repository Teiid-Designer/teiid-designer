/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.relationship.RelationshipPlugin;

/**
 * NavigationResolver
 */
public class NavigationResolver {

    /**
     * Construct an instance of NavigationResolver.
     * 
     */
    public NavigationResolver() {
        super();
    }
    
    public URI getUri( final EObject object ) {
        final URI uri = EcoreUtil.getURI(object);
        if ( uri == null ) {
            final Object[] params = new Object[]{object};
            final String msg = RelationshipPlugin.Util.getString("NavigationResolver.Unable_to_obtain_URI_for_object",params); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        return uri;
    }

    public EObject resolve( final String uri ) throws CoreException {
        final Container container = ModelerCore.getModelContainer();
        final URI theUri = URI.createURI(uri);
        return container.getEObject(theUri,true);
    }

}
