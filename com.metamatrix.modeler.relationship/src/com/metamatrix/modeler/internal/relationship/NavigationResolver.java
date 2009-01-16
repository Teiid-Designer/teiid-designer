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
