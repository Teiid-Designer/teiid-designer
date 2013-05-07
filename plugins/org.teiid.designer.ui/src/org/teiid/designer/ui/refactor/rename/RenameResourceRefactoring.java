/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.rename;

import java.util.Collections;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.core.refactor.RelatedResourceFinder.Relationship;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.refactor.AbstractResourcesRefactoring;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils.AbstractResourceCallback;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils.IResourceCallback;

/**
 * Refactoring for a rename operation
 */
public class RenameResourceRefactoring extends AbstractResourcesRefactoring {

    private class RelatedResourceCallback extends AbstractResourceCallback {

        private final PathPair pathPair;

        /**
         * @param pathPair
         */
        public RelatedResourceCallback(PathPair pathPair) {
            this.pathPair = pathPair;
        }

        @Override
        public void checkValidFile(IFile relatedFile, RefactoringStatus status) {
            checkResource(relatedFile, new NullProgressMonitor(), status);
        }

        @Override
        public void indexFile(IResource resource, IFile relatedFile, RefactoringStatus status) throws Exception {
            RefactorResourcesUtils.unloadModelResource(relatedFile);

            TextFileChange textFileChange = new TextFileChange(relatedFile.getName(), relatedFile);
            RefactorResourcesUtils.calculatePathChanges(relatedFile, Collections.singleton(pathPair), textFileChange);

            if (ModelUtil.isModelFile(getResource())) {
                // It is reasonable that only if a model file is being renamed will it impact the SQL of related files.
                // There is no reason to expect a project or folder to even be include in an SQL statement.
                RefactorResourcesUtils.calculateSQLChanges(relatedFile, pathPair, textFileChange);
            }

            if (textFileChange.getEdit() != null && textFileChange.getEdit().hasChildren()) {
                // Only if the related file is actually being changed do we add the text change
                // and calculate the effect on any vdbs containing this related file
                addChange(relatedFile, textFileChange);
                RefactorResourcesUtils.calculateRelatedVdbResources(relatedFile, status, this);
            }
        }

        @Override
        public void indexVdb(IResource resource, IFile vdbFile, RefactoringStatus status) {
            IPath oldResourcePath = resource.getFullPath();
            IPath newResourcePath = oldResourcePath.removeLastSegments(1).append(getNewResourceName());
            addVdbChange(vdbFile, oldResourcePath, newResourcePath);
        }
    }

    private String newName;

    /**
     * Create a new instance
     * 
     * @param selectedResource
     */
    public RenameResourceRefactoring(final IResource selectedResource) {
        super(RefactorResourcesUtils.getString("RenameRefactoring.title"), Collections.singletonList(selectedResource)); //$NON-NLS-1$
    }
    
    IResource getResource() {
        return getResources().get(0);
    }
    
    /**
     * Get the new name for the resource
     * 
     * @return name
     */
    public String getNewResourceName() {
        return newName;
    }
    
    /**
     * Set the new name for the resource
     * 
     * @param newName
     */
    public void setNewResourceName(String newName) {
        this.newName = newName;
    }

    @Override
    protected void checkResource(IResource resource, IProgressMonitor progressMonitor, RefactoringStatus status) {
        RefactorResourcesUtils.checkResourceExists(resource, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkResourceSynched(resource, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkResourceWritable(resource, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkExtensionManager(resource, RefactorType.MOVE, progressMonitor, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkModelResourceWritable(resource, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkSavedResource(resource, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkOpenEditors(resource, status);
    }

    @Override
    public RefactoringStatus checkInitialConditions(final IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();

        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("RenameRefactoring.initialConditions"), 1); //$NON-NLS-1$

            checkResourcesNotEmpty(status);

            for (IResource resource : getResources()) {
                checkResource(resource, progressMonitor, status);
                if (!status.isOK()) break;

                // Check validity of related resources
                IResourceCallback callback = new AbstractResourceCallback() {
                    @Override
                    public void checkValidFile(IFile relatedFile, RefactoringStatus validityStatus) {
                        checkResource(relatedFile, progressMonitor, validityStatus);
                    }
                };

                RefactorResourcesUtils.calculateRelatedResources(resource, status, callback, Relationship.DEPENDENCY);
            }
        } finally {
            progressMonitor.done();
        }

        return status;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor progressMonitor) throws OperationCanceledException {
        /* Clear changes in case we are going back then forward in the wizard */
        clearChanges();

        RefactoringStatus status = new RefactoringStatus();
        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("RenameRefactoring.finalConditions"), 2); //$NON-NLS-1$

            for (IResource resource : getResources()) {
                // Add move change for the resource
                addChange(resource, new RenameResourceChange(resource.getFullPath(), getNewResourceName()));

                IPath absPath = resource.getRawLocation().makeAbsolute();
                IPath destination = absPath.removeLastSegments(1).append(getNewResourceName());
                String destinationPath = destination.toOSString();
                PathPair pathPair = new PathPair(absPath.toOSString(), destinationPath);
                RelatedResourceCallback callback = new RelatedResourceCallback(pathPair);

                if (ModelUtil.isModelFile(resource)) {
                    // Add change for updating of any SQL contained in the resource
                    try {
                        TextFileChange textFileChange = new TextFileChange(resource.getName(), (IFile) resource);
                        RefactorResourcesUtils.calculateSQLChanges((IFile) resource, pathPair, textFileChange);
                        addChange(resource, textFileChange);

                        RefactorResourcesUtils.calculateRelatedVdbResources(resource, status, callback);
                    } catch (Exception ex) {
                        UiConstants.Util.log(ex);
                        status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                    }
                }          

                // Add changes for related resources
                RefactorResourcesUtils.calculateRelatedResources(resource, status, callback, Relationship.DEPENDENCY);
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
            progressMonitor.beginTask(RefactorResourcesUtils.getString("MoveRefactoring.creatingChange"), 1); //$NON-NLS-1$
            CompositeChange change = new RenameResourceCompositeChange( getName(), 
                                                                                                                      getChanges().toArray(new Change[0]),
                                                                                                                      newName,
                                                                                                                      getResource());
            return change;
        } catch (Exception ex) {
            throw new CoreException(new Status(IStatus.ERROR, RenameResourceDescriptor.REFACTORING_ID, ex.getMessage()));
        } finally {
            progressMonitor.done();
        }
    }

}
