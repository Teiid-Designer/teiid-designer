/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.delete;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;
import org.teiid.designer.core.refactor.RelatedResourceFinder.Relationship;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.refactor.AbstractResourcesRefactoring;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils.AbstractResourceCallback;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils.IResourceCallback;

/**
 *
 */
public class DeleteResourcesRefactoring extends AbstractResourcesRefactoring {

    private class RelatedResourceCallback extends VdbResourceCallback {

        private final Set<IResource> indexedResources = new HashSet<IResource>();

        @Override
        public void indexFile(IResource resource, IFile relatedFile, RefactoringStatus status) throws Exception {
            /*
             * The related file will be deleted so we don't want to try deleting it twice
             */
            if (indexedResources.contains(relatedFile))
                return;

            indexedResources.add(relatedFile);

            RefactorResourcesUtils.unloadModelResource(relatedFile);

            if (! getResourcesAndChildren(status).contains(relatedFile)) {
                // file is NOT already being deleted
                DeleteResourceChange change = new DeleteResourceChange(relatedFile.getFullPath(), true, isDeleteContents());
                addChange(relatedFile, change);
            }

            RefactorResourcesUtils.calculateRelatedVdbResources(relatedFile, status, this);
        }

        @Override
        public void indexVdb(IResource resource, IFile vdbFile, RefactoringStatus status) {
            if (! getResourcesAndChildren(status).contains(vdbFile)) {
                // vdb is NOT already being deleted
                super.indexVdb(resource, vdbFile, status);
            }
        }
    }

    private boolean deleteContents;

    /**
     * @param selectedResources
     */
    public DeleteResourcesRefactoring(final List<IResource> selectedResources) {
        super(RefactorResourcesUtils.getString("DeleteRefactoring.title"), selectedResources); //$NON-NLS-1$
    }

    /**
     * Delete projects contents.
     * @return <code>true</code> if this will delete the project contents.  The content delete is not undoable.
     */
    public boolean isDeleteContents() {
        return deleteContents;
    }

    /**
     * Set to delete the projects content.
     *
     * @param deleteContents <code>true</code> if this will delete the project contents.  The content delete is not undoable.
     */
    public void setDeleteContents(boolean deleteContents) {
        this.deleteContents= deleteContents;
    }

    /**
     * Takes into account whether isDeleteContents has been set. If it has not then
     * the project is just removed from eclipse and the directory being read-only is
     * irrelevant
     * 
     * @param project
     * @param status
     */
    private void checkProjectReadOnly(IProject project, RefactoringStatus status) {
        if (isDeleteContents() && ModelUtil.isIResourceReadOnly(project)) {
            status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyResourceError", project.getName()))); //$NON-NLS-1$
        }
    }
    
    private boolean isSelectedOrChildResource(final IResource child) {
        if (getResources().contains(child))
            return true;

        final boolean status[] = new boolean[1];

        IResourceVisitor childVisitor = new IResourceVisitor() {

            @Override
            public boolean visit(IResource resource) {
                if (resource.equals(child)) {
                    status[0] = true;
                    return false; // found the child so exit the visitor stat!
                }

                return true;
            }
        };

        for (IResource selected : getResources()) {
            try {
                selected.accept(childVisitor);
                if (status[0]) {
                    // child is a descendant of one of the selected
                    return true;
                }
            } catch (CoreException ex) {
                // do nothing
            }
        }

        return false;
    }
    	

    @Override
    protected void checkResource(IResource resource, IProgressMonitor progressMonitor, RefactoringStatus status) {
        int readOnlyStatusLevel;
        String readOnlyStatusMsg;

        if (isSelectedOrChildResource(resource)) {
            readOnlyStatusLevel = IStatus.WARNING;
            readOnlyStatusMsg = RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyResourceError",  //$NON-NLS-1$
                                                                 resource.getName());
        } else {
            readOnlyStatusLevel = IStatus.ERROR;
            readOnlyStatusMsg = RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyRelatedResourceError",  //$NON-NLS-1$
                                                                 resource.getName());
        }

        RefactorResourcesUtils.checkResourceExists(resource, status);
        if (status.getSeverity() > IStatus.WARNING) return;

        RefactorResourcesUtils.checkResourceSynched(resource, status);
        if (status.getSeverity() > IStatus.WARNING) return;

        if (resource instanceof IProject)
            checkProjectReadOnly((IProject) resource, status);
        else
            RefactorResourcesUtils.checkResourceWritable(resource, status, readOnlyStatusLevel, readOnlyStatusMsg);

        if (status.getSeverity() > IStatus.WARNING) return;

        RefactorResourcesUtils.checkExtensionManager(resource, RefactorType.DELETE, progressMonitor, status);
        if (status.getSeverity() > IStatus.WARNING) return;

        RefactorResourcesUtils.checkSavedResource(resource, status);
        if (status.getSeverity() > IStatus.WARNING) return;

        if (! getResources().contains(resource)) {
          /*
           * Its okay to delete a resource if an editor for it is open but can cause
           * problems for related resources, especially if the user previews then
           * cancels the delete operation.
           */
          RefactorResourcesUtils.checkOpenEditors(resource, status);
      }
    }
    
    @Override
    public RefactoringStatus checkInitialConditions(final IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();

        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("DeleteRefactoring.initialConditions"), 1); //$NON-NLS-1$

            if (! checkResourcesNotEmpty(status))
                return status;
            
            // allow only projects or only non-projects to be selected;
            // note that the selection may contain multiple types of resource
            if (!(RefactorResourcesUtils.containsOnlyProjects(getResources()) || RefactorResourcesUtils.containsOnlyNonProjects(getResources()))) {
                return RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("DeleteRefactoring.containsProjectsAndResourcesError")); //$NON-NLS-1$
            }

            for (IResource resource : getResources()) {
                checkResource(resource, progressMonitor, status);
                if (status.getSeverity() > IStatus.WARNING) break;

                // Check validity of related resources
                IResourceCallback callback = new AbstractResourceCallback() {
                    @Override
                    public void checkValidFile(IFile relatedFile, RefactoringStatus validityStatus) {
                        checkResource(relatedFile, progressMonitor, validityStatus);
                    }
                };

                RefactorResourcesUtils.calculateRelatedResources(resource, status, callback, Relationship.DEPENDENT);
            }
        } finally {
            progressMonitor.done();
        }

        return status;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();
        clearChanges();

        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("DeleteRefactoring.finalConditions"), 2); //$NON-NLS-1$

            RelatedResourceCallback callback = new RelatedResourceCallback();
            for (IResource resource : getResources()) {
                addChange(resource, new DeleteResourceChange(resource.getFullPath(), true, isDeleteContents()));
                RefactorResourcesUtils.calculateRelatedResources(resource, status, callback, Relationship.DEPENDENT);
            }
        }
        finally {
            progressMonitor.done();
        }
        
        return status;
    }

    @Override
    public Change createChange(IProgressMonitor progressMonitor) throws OperationCanceledException, CoreException {
        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("DeleteRefactoring.creatingChange"), 1); //$NON-NLS-1$
            CompositeChange change = new DeleteResourcesCompositeChange( getName(), 
                                                                                                                      getChanges().toArray(new Change[0]),
                                                                                                                      isDeleteContents(),
                                                                                                                      getResources());
            return change;
        } catch (Exception ex) {
            throw new CoreException(new Status(IStatus.ERROR, DeleteResourcesDescriptor.REFACTORING_ID, ex.getMessage()));
        } finally {
            progressMonitor.done();
        }
    }
}
