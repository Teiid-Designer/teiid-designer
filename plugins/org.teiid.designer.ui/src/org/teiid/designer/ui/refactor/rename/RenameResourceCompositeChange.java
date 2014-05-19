/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.rename;

import org.eclipse.core.resources.IResource;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;
import org.teiid.designer.core.refactor.RefactorModelExtensionManager;
import org.teiid.designer.ui.refactor.AbstractResourcesCompositeChange;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;
import org.teiid.designer.ui.util.ErrorHandler;

/**
 *
 */
public class RenameResourceCompositeChange extends AbstractResourcesCompositeChange {

    private final String newName;
    
    private final IResource resource;

    /**
     * @param name
     * @param changes
     * @param newName 
     * @param resource 
     */
    public RenameResourceCompositeChange(String name, Change[] changes, String newName, IResource resource) {
        super(name, changes);
        this.newName = newName;
        this.resource = resource;
    }

    @Override
    public ChangeDescriptor getDescriptor() {
        String project = resource.getProject().getName();
        String description = RefactorResourcesUtils.getString("RenameRefactoring.moveDescriptorDescription"); //$NON-NLS-1$
        String comment = RefactorResourcesUtils.getString("RenameRefactoring.moveDescriptorComment"); //$NON-NLS-1$

        RenameResourceDescriptor descriptor = new RenameResourceDescriptor(project, description, comment);
        descriptor.setResourceToRename(resource);
        descriptor.setNewName(newName);

        return new RefactoringChangeDescriptor(descriptor);
    }

    @Override
    protected Change createUndoChange(Change[] childUndos) {
        return new RenameResourceCompositeChange(getName(), childUndos, newName, resource);
    }
    
    @Override
    protected void postPerform(Change change) {
        try {
            Object[] objects = change.getAffectedObjects();
            if (objects != null) {
                for (Object object : objects) {
                    if (object instanceof IResource) {
                        RefactorModelExtensionManager.postProcess(RefactorType.RENAME, (IResource) object);
                    }
                }
            }

            if (change instanceof ResourceChange) {
                ResourceChange resourceChange = (ResourceChange)change;
                Object object = resourceChange.getModifiedElement();
                if (object instanceof IResource) {
                    RefactorModelExtensionManager.postProcess(RefactorType.RENAME, (IResource) object);
                }
            }
        } catch (Exception ex) {
            ErrorHandler.toExceptionDialog(ex);
        }
    }
}
