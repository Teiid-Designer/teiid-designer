/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.container;

import org.eclipse.core.resources.IResource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * A ResourceSetFinder is used by the ModelBufferManager to identify which {@link ResourceSet} should be used
 * for a particular IResource.
 */
public interface ResourceSetFinder {
    
    /**
     * Find and return the {@link ResourceSet} into which the specified resource file should be or has
     * been loaded.
     * @param resource the resource file, folder or project
     * @return the ResourceSet for the resource
     * @throws CoreException
     */
    public ResourceSet getResourceSet( IResource resource ) throws ModelWorkspaceException;

}
