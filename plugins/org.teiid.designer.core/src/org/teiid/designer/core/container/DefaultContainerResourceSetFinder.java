/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.container;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.workspace.ModelWorkspaceException;


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
     * @see org.teiid.designer.core.workspace.ResourceSetFinder#getResourceSet(org.eclipse.core.resources.IResource)
     */
    @Override
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
