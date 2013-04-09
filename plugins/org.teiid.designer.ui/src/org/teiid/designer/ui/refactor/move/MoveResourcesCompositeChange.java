/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.move;

import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.teiid.designer.ui.refactor.AbstractResourcesCompositeChange;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;

/**
 *
 */
public class MoveResourcesCompositeChange extends AbstractResourcesCompositeChange {

    private final IContainer destination;
    
    private final List<IResource> resources;

    /**
     * @param name
     * @param changes
     * @param destination 
     * @param resources 
     */
    public MoveResourcesCompositeChange(String name, Change[] changes, IContainer destination, List<IResource> resources) {
        super(name, changes);
        this.destination = destination;
        this.resources = resources;
    }

    @Override
    public ChangeDescriptor getDescriptor() {
        String project = destination.getProject().getName();
        String description = RefactorResourcesUtils.getString("MoveRefactoring.moveDescriptorDescription"); //$NON-NLS-1$
        String comment = RefactorResourcesUtils.getString("MoveRefactoring.moveDescriptorComment"); //$NON-NLS-1$

        MoveResourcesDescriptor descriptor = new MoveResourcesDescriptor(project, description, comment);
        descriptor.setResourcesToMove(resources);
        descriptor.setDestinationPath(destination.getFullPath());

        return new RefactoringChangeDescriptor(descriptor);
    }

    @Override
    protected Change createUndoChange(Change[] childUndos) {
        return new MoveResourcesCompositeChange(getName(), childUndos, destination, resources);
    }
}
