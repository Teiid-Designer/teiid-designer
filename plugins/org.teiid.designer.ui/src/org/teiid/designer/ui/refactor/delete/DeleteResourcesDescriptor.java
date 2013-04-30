/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.delete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;

/**
 * Refactoring descriptor for the delete resource refactoring.
 */
public class DeleteResourcesDescriptor extends RefactoringDescriptor {
    
    static final String REFACTORING_ID = DeleteResourcesDescriptor.class.getPackage().getName();

    private static final String UNAMED_DESCRIPTOR = "Unamed_Descriptor"; //$NON-NLS-1$
    
    /** The resources to move */
    private Collection<IPath> resourcePaths;

    private boolean deleteContents;

    /**
     * Create new default instance
     */
    public DeleteResourcesDescriptor() {
        this(UNAMED_DESCRIPTOR, null);
    }
    
    /**
     * Create new instance
     *
     * @param description
     * @param comment
     */
    public DeleteResourcesDescriptor(String description, String comment) {
        super(REFACTORING_ID, null, description, comment, RefactoringDescriptor.STRUCTURAL_CHANGE | RefactoringDescriptor.MULTI_CHANGE);
        resourcePaths = Collections.emptyList();
    }
    
    /**
     * @return the resourcePaths
     */
    public Collection<IPath> getResourcePaths() {
        return this.resourcePaths;
    }
    
    /**
     * @param resourcePaths
     */
    public void setResourcePathsToDelete(Collection<IPath> resourcePaths) {
        this.resourcePaths = resourcePaths;
    }

    /**
     * @param resources
     */
    public void setResourcesToDelete(Collection<IResource> resources) {
        CoreArgCheck.isNotNull(resources);
        
        resourcePaths = new ArrayList<IPath>();
        for (IResource resource : resources) {
            resourcePaths.add(resource.getFullPath());
        }
    }

    /**
     * <code>true</code> is returned if projects contents are also deleted.
     *
     * @return <code>true</code> if this will delete the project contents.  The content delete is not undoable.
     */
    public boolean isDeleteContents() {
        return deleteContents;
    }

    /**
     * If set to <code>true</code>, delete will also delete project contents.
     *
     * @param deleteContents <code>true</code> if this will delete the project contents.  The content delete is not undoable.
     */
    public void setDeleteContents(boolean deleteContents) {
        this.deleteContents = deleteContents;
    }

    public Refactoring createRefactoring(RefactoringStatus status) throws CoreException {
        IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
        List<IResource> resources = new ArrayList<IResource>();

        for (IPath resourcePath : resourcePaths) {
            IResource resource = root.findMember(resourcePath);
            if (resource == null || !resource.exists()) {
                status.addFatalError(RefactorResourcesUtils.getString("RefactorResourceDescriptor.resourceNoExistError", resourcePath)); //$NON-NLS-1$
                return null;
            }

            resources.add(resource);
        }

        DeleteResourcesRefactoring refactoring = new DeleteResourcesRefactoring(resources);
        refactoring.setDeleteContents(deleteContents);
        
        return refactoring;
    }

}

