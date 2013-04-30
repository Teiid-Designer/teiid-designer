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
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.refactor.AbstractResourcesRefactoring;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils.IRelatedResourceCallback;
import org.teiid.designer.vdb.refactor.VdbResourceChange;

/**
 *
 */
public class DeleteResourcesRefactoring extends AbstractResourcesRefactoring {

    private class RelatedResourcesCallback implements IRelatedResourceCallback {

        private final RefactoringStatus status;

        private final Set<IResource> indexedResources = new HashSet<IResource>();

        /**
         * @param status
         */
        public RelatedResourcesCallback(RefactoringStatus status) {
            this.status = status;
        }

        @Override
        public void mergeStatus(RefactoringStatus status) {
            this.status.merge(status);
        }

        @Override
        public void indexFile(IResource resource, IFile relatedFile) throws Exception {
            /*
             * The related file will be deleted so we don't want to try deleting it twice
             */
            if (indexedResources.contains(relatedFile))
                return;

            indexedResources.add(relatedFile);

            RefactorResourcesUtils.unloadModelResource(relatedFile);

            if (! resourcesAndChildren.contains(relatedFile)) {
                // file is NOT already being deleted
                DeleteResourceChange change = new DeleteResourceChange(relatedFile.getFullPath(), true, isDeleteContents());
                addChange(relatedFile, change);
            }

            RefactorResourcesUtils.calculateRelatedVdbResources(relatedFile, this);
        }

        @Override
        public void indexVdb(IResource resource, IFile vdbFile) {
            addChange(vdbFile, new VdbResourceChange(vdbFile));
        }
    }

    private boolean deleteContents;
    
    private Set<IResource> resourcesAndChildren = new HashSet<IResource>();

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
     * @return
     */
    private boolean checkProjectReadOnly(IProject project, RefactoringStatus status) {
        if (isDeleteContents() && ModelUtil.isIResourceReadOnly(project)) {
            status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyResourceError", project.getName()))); //$NON-NLS-1$
            return false;
        }

        return true;
    }
    
    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();

        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("DeleteRefactoring.initialConditions"), 1); //$NON-NLS-1$

            checkResourcesNotEmpty(status);
            closeDirtyEditors(status);
            
            // allow only projects or only non-projects to be selected;
            // note that the selection may contain multiple types of resource
            if (!(RefactorResourcesUtils.containsOnlyProjects(getResources()) || RefactorResourcesUtils.containsOnlyNonProjects(getResources()))) {
                return RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("DeleteRefactoring.containsProjectsAndResourcesError")); //$NON-NLS-1$
            }

            boolean result = true;
            for (IResource resource : getResources()) {
                result = checkResourceExists(resource, status);
                if (!result) break;

                if (resource instanceof IProject) {
                    result = checkProjectReadOnly((IProject) resource, status);
                }
                else {
                    result = checkResourceReadOnly(resource, status);
                }                
                if (!result) break;

                result = checkExtensionManager(resource, RefactorType.DELETE, progressMonitor, status);
                if (!result) break;
                
                // Accumulate all the resources that will be deleted so that scheduleRemovedRelatedFile()
                // does not add a delete change for a resource already being deleted
                try {
                    resource.accept(new IResourceVisitor() {
                        @Override
                        public boolean visit(IResource visitedResource) {
                            resourcesAndChildren.add(visitedResource);
                            return true;
                        }
                    }, IResource.DEPTH_INFINITE, false);
                } catch (Exception err) {
                    ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                    status.merge(RefactoringStatus.createFatalErrorStatus(err.getMessage()));
                }
            }

            if (result) {
                // Only if the resources passed the tests above do we bother with related resources
                RefactorResourcesUtils.checkReadOnlyResources(getResources(), status);
            }
        } finally {
            progressMonitor.done();
        }

        return status;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();

        try {
            for (IResource resource : getResources()) {
                if (!resource.isSynchronized(IResource.DEPTH_INFINITE)) {
                    status.addInfo(RefactorResourcesUtils.getString("DeleteRectoring.warningOutOfSync", resource.getFullPath())); //$NON-NLS-1$
                }
            }

            checkDirtyResources(status);
            
            progressMonitor.beginTask(RefactorResourcesUtils.getString("MoveRefactoring.finalConditions"), 2); //$NON-NLS-1$

            RelatedResourcesCallback callback = new RelatedResourcesCallback(status);
            for (IResource resource : getResources()) {
                addChange(resource, new DeleteResourceChange(resource.getFullPath(), true, isDeleteContents()));
                RefactorResourcesUtils.calculateRelatedResources(resource, callback);
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
