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

package com.metamatrix.modeler.internal.core.container;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.ResourceSetFinder;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * The DefaultContainerResourceSetFinder always returns the one {@link ResourceSet} that is available from
 * the {@link ModelerCore#getModelContainer() default model container}.
 */
public class DefaultContainerResourceSetFinder implements ResourceSetFinder {

    /**
     * Construct an instance of DefaultContainerResourceSetFinder.
     * 
     */
    public DefaultContainerResourceSetFinder() {
        super();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.core.workspace.ResourceSetFinder#getResourceSet(org.eclipse.core.resources.IResource)
     */
    public ResourceSet getResourceSet(IResource resource) throws ModelWorkspaceException {
        try {
            return ModelerCore.getModelContainer();
        } catch (ModelWorkspaceException e) {
            throw e;
        } catch (CoreException e) {
            throw new ModelWorkspaceException(e);
        }
    }

}
