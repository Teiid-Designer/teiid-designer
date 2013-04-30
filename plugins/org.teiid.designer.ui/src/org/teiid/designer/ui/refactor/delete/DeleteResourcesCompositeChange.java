/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.delete;

import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.teiid.designer.ui.refactor.AbstractResourcesCompositeChange;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;

/**
 *
 */
public class DeleteResourcesCompositeChange extends AbstractResourcesCompositeChange {
    
    private final List<IResource> resources;

    private final boolean deleteContents;

    /**
     * @param name
     * @param changes
     * @param deleteContents
     * @param resources 
     */
    public DeleteResourcesCompositeChange(String name, Change[] changes, boolean deleteContents, List<IResource> resources) {
        super(name, changes);
        this.resources = resources;
        this.deleteContents = deleteContents;
    }

    @Override
    public ChangeDescriptor getDescriptor() {
        String description = RefactorResourcesUtils.getString("DeleteRefactoring.deleteDescriptorDescription"); //$NON-NLS-1$
        String comment = RefactorResourcesUtils.getString("DeleteRefactoring.deleteDescriptorComment"); //$NON-NLS-1$

        DeleteResourcesDescriptor descriptor = new DeleteResourcesDescriptor(description, comment);
        descriptor.setResourcesToDelete(resources);
        descriptor.setDeleteContents(deleteContents);

        return new RefactoringChangeDescriptor(descriptor);
    }

    @Override
    protected Change createUndoChange(Change[] childUndos) {
        return new DeleteResourcesCompositeChange(getName(), childUndos, deleteContents, resources);
    }
}
