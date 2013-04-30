/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.move;

import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.resource.MoveResourceChange;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.refactor.AbstractResourcesRefactoring;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils.IRelatedResourceCallback;
import org.teiid.designer.vdb.refactor.VdbResourceChange;

/**
 *
 */
public class MoveResourcesRefactoring extends AbstractResourcesRefactoring {

    private class RelatedResourcesCallback implements IRelatedResourceCallback {

        private final Set<PathPair> pathPairs;

        private final RefactoringStatus status;

        /**
         * @param pathPairs
         * @param status
         */
        public RelatedResourcesCallback(Set<PathPair> pathPairs, RefactoringStatus status) {
            this.pathPairs = pathPairs;
            this.status = status;
        }

        @Override
        public void mergeStatus(RefactoringStatus status) {
            this.status.merge(status);
        }

        @Override
        public void indexFile(IResource resource, IFile relatedFile) throws Exception {
            RefactorResourcesUtils.unloadModelResource(relatedFile);

            TextFileChange textFileChange = new TextFileChange(relatedFile.getName(), relatedFile);
            RefactorResourcesUtils.calculatePathChanges(relatedFile, pathPairs, textFileChange);

            if (textFileChange.getEdit().hasChildren()) {
                // Only if the related file is actually being changed do we add the text change
                // and calculate the effect on any vdbs containing this related file
                addChange(relatedFile, textFileChange);
                RefactorResourcesUtils.calculateRelatedVdbResources(relatedFile, this);
            }
        }

        @Override
        public void indexVdb(IResource resource, IFile vdbFile) {
            addChange(vdbFile, new VdbResourceChange(vdbFile));
        }
    }

    private IContainer destination;
    
    /**
     * @param selectedResources 
     */
    public MoveResourcesRefactoring(final List<IResource> selectedResources) {
        super(RefactorResourcesUtils.getString("MoveRefactoring.title"), selectedResources); //$NON-NLS-1$
    }

    /**
     * @return the destination container
     */
    public IContainer getDestination() {
        return destination;
    }
    
    /**
     * Set the destination
     * 
     * @param destination
     */
    public void setDestination(IContainer destination) {
        this.destination = destination;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();

        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("MoveRefactoring.initialConditions"), 1); //$NON-NLS-1$

            checkResourcesNotEmpty(status);
            closeDirtyEditors(status);

            boolean result = true;
            for (IResource resource : getResources()) {
                result = checkResourceExists(resource, status);
                if (!result) break;

                result = checkResourceReadOnly(resource, status);
                if (!result) break;

                result = checkForProject(resource, status);
                if (!result) break;

                result = checkExtensionManager(resource, RefactorType.MOVE, progressMonitor, status);
                if (!result) break;
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
        /* Clear changes in case we are going back then forward in the wizard */
        clearChanges();

        RefactoringStatus status = new RefactoringStatus();
        String destinationPath = destination.getRawLocation().makeAbsolute().toOSString();
        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("MoveRefactoring.finalConditions"), 2); //$NON-NLS-1$
            
            for (IResource resource : getResources()) {
                addChange(resource, new MoveResourceChange(resource, destination));

                try {
                    Set<PathPair> pathPairs = RefactorResourcesUtils.calculateResourceMoves(getResources(), destinationPath, RefactorResourcesUtils.Option.EXCLUDE_FOLDERS);

                    if (pathPairs == null || pathPairs.isEmpty()) {
                        status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("MoveRefactoring.emptyResourcePairsError"))); //$NON-NLS-1$
                        continue;
                    }

                    RefactorResourcesUtils.calculateRelatedResources(resource, new RelatedResourcesCallback(pathPairs, status));

                } catch (Exception ex) {
                    UiConstants.Util.log(ex);
                    status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                }
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
            CompositeChange change = new MoveResourcesCompositeChange( getName(), 
                                                                                                                      getChanges().toArray(new Change[0]),
                                                                                                                      destination,
                                                                                                                      getResources());
            return change;
        } catch (Exception ex) {
            throw new CoreException(new Status(IStatus.ERROR, MoveResourcesDescriptor.REFACTORING_ID, ex.getMessage()));
        } finally {
            progressMonitor.done();
        }
    }
}
