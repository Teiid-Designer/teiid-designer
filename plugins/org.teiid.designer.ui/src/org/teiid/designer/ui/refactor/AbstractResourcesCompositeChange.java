/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;
import org.teiid.designer.ui.refactor.move.MoveResourcesUtils;

/**
 * Abstract implementation of composite change that ensure all
 * affected resources (that are EMF model resources) are unloaded
 * prior to the change.
 */
public abstract class AbstractResourcesCompositeChange extends CompositeChange {

    /**
     * @param name
     * @param changes
     */
    public AbstractResourcesCompositeChange(String name, Change[] changes) {
        super(name,  changes);
    }

    @Override
    public abstract ChangeDescriptor getDescriptor();

    @Override
    protected abstract Change createUndoChange(Change[] childUndos);

    private void unloadChangeResource(Change change) throws CoreException {
        Object[] objects = change.getAffectedObjects();
        if (objects != null) {
            for (Object object : objects) {
                if (object instanceof IResource) {
                    MoveResourcesUtils.unloadModelResource((IResource) object);
                }
            }
        }

        if (change instanceof ResourceChange) {
            ResourceChange resourceChange = (ResourceChange) change;
            Object object = resourceChange.getModifiedElement();
            if (object instanceof IResource) {
                MoveResourcesUtils.unloadModelResource((IResource) object);
            }
        }
    }

    @Override
    public Change perform(IProgressMonitor pm) throws CoreException {
        for (Change childChange : getChildren()) {
            unloadChangeResource(childChange);
        }

        return super.perform(pm);
    }
}