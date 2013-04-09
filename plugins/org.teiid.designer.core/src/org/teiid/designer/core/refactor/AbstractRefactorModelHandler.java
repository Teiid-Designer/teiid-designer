/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.refactor;

import java.util.Collection;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 *
 */
public abstract class AbstractRefactorModelHandler implements IRefactorModelHandler {

    @Override
    public void helpUpdateModelContentsForDelete(Collection<IResource> deletedResourcePaths,
                                                 Collection<IResource> directDependentResources,
                                                 IProgressMonitor monitor) {
        // Nothing to do
    }

    @Override
    public boolean preProcess(RefactorType refactorType, IResource refactoredResource, IProgressMonitor monitor) {
        return true;
    }

    @Override
    public void postProcess(RefactorType refactorType, IResource refactoredResource) {
        // Nothing to do
    }

}
