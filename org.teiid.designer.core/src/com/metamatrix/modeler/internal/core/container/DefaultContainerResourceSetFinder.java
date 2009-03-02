/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
