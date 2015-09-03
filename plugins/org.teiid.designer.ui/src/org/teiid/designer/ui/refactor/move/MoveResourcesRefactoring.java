/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.move;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
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
import org.eclipse.ltk.core.refactoring.resource.MoveResourceChange;
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
 *
 */
public class MoveResourcesRefactoring extends AbstractResourcesRefactoring {

    private class RelatedResourceCallback extends VdbResourceCallback {

        private final Set<PathPair> pathPairs;

        private final Set<IResource> history = new HashSet<IResource>();

        /**
         * @param pathPairs
         */
        public RelatedResourceCallback(Set<PathPair> pathPairs) {
            this.pathPairs = pathPairs;
        }

        @Override
        public void checkValidFile(IFile relatedFile, RefactoringStatus status) {
            checkResource(relatedFile, new NullProgressMonitor(), status);
        }

        @Override
        public void indexFile(IResource resource, IFile relatedFile, RefactoringStatus status) throws Exception {
            RefactorResourcesUtils.unloadModelResource(relatedFile);

            if (history.contains(relatedFile)) {
                /*
                 * This file has been processed before implying that it is related
                 * to more than one target resource.
                 *
                 * Since pathpairs are global to all resources there is
                 * no point in processing the related file more than once.
                 */
                return;
            }

            // Add the related file to the history to avoid processing it again
            history.add(relatedFile);

            if (getResourcesAndChildren(status).contains(relatedFile)) {
                /*
                 * Changes are not applicable as the related file is moving
                 * to the new destination as well
                 */
                return;
            }

            if (pathPairs == null || pathPairs.isEmpty())
                return;

            IPath relatedFilePath = ModelUtil.getLocation(relatedFile).makeAbsolute();
            IPath relatedParentPath = relatedFilePath.removeLastSegments(1);

            // Convert the path pair to a pair of relative paths
            Set<PathPair> relativePathPairs = new HashSet<PathPair>();
            for (PathPair absPathPair : pathPairs) {
                relativePathPairs.add(RefactorResourcesUtils.getRelativePath(relatedParentPath.toOSString(), absPathPair));
            }

            TextFileChange textFileChange = RefactorResourcesUtils.calculateTextChanges(relatedFile, relativePathPairs);
            for( PathPair pair : relativePathPairs ) {
                RefactorResourcesUtils.calculateModelImportsElementLChanges(relatedFile, pair, textFileChange);
            }
            if (addTextChange(relatedFile, textFileChange)) {
                // Calculate the effect on any vdbs containing this modified related file
                RefactorResourcesUtils.calculateRelatedVdbResources(relatedFile, status, this);
            }
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
    protected void checkResource(IResource resource, IProgressMonitor progressMonitor, RefactoringStatus status) {
        String readOnlyStatusMsg;

        if (getResources().contains(resource)) {
            readOnlyStatusMsg = RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyResourceError",  //$NON-NLS-1$
                                                                 resource.getName());
        } else {
            readOnlyStatusMsg = RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyRelatedResourceError",  //$NON-NLS-1$
                                                                 resource.getName());
        }

        RefactorResourcesUtils.checkResourceExists(resource, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkResourceSynched(resource, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkResourceWritable(resource, status, IStatus.ERROR, readOnlyStatusMsg);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkResourceIsNotProject(resource, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkExtensionManager(resource, RefactorType.MOVE, progressMonitor, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkModelResourceWritable(resource, status, IStatus.ERROR, readOnlyStatusMsg);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkSavedResource(resource, status);
        if (!status.isOK()) return;

        RefactorResourcesUtils.checkOpenEditors(resource, status);
    }

    @Override
    public RefactoringStatus checkInitialConditions(final IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();

        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("MoveRefactoring.initialConditions"), 1); //$NON-NLS-1$

            for (IResource resource : getResources()) {
                checkResource(resource, progressMonitor, status);
                if (! status.isOK()) break;

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
        /* Clear changes in case we are going back then forward in the wizard */
        clearChanges();

        RefactoringStatus status = new RefactoringStatus();
        try {
            String destinationPath = ModelUtil.getLocation(destination).makeAbsolute().toOSString();
            progressMonitor.beginTask(RefactorResourcesUtils.getString("MoveRefactoring.finalConditions"), 2); //$NON-NLS-1$

            Set<PathPair> pathPairs = RefactorResourcesUtils.calculateResourceMoves(getResources(), destinationPath, RefactorResourcesUtils.Option.EXCLUDE_FOLDERS);
            RelatedResourceCallback relatedResourceCallback = new RelatedResourceCallback(pathPairs);

            for (IResource resource : getResources()) {
                // Find the resource's related resources and determine their changes
                RefactorResourcesUtils.calculateRelatedResources(resource, status, relatedResourceCallback, Relationship.DEPENDENT);

                // Find the resource's imports as they need to be updated due to the resource move
                if (ModelUtil.isModelFile(resource)) {
                    IFile file = (IFile) resource;
                    Set<PathPair> importPathPairs = RefactorResourcesUtils.calculateImportChanges(file, destinationPath, getResourcesAndChildren(status));
                    TextFileChange textFileChange = RefactorResourcesUtils.calculateTextChanges(file, importPathPairs);
                    for( PathPair pair : importPathPairs ) {
                        RefactorResourcesUtils.calculateModelImportsElementLChanges(file, pair, textFileChange);
                    }
                    if (addTextChange(file, textFileChange)) {
                        // Calculate the effect on any vdbs containing this modified related file
                        RefactorResourcesUtils.calculateRelatedVdbResources(file, status, new VdbResourceCallback());
                    }
                }

                addChange(resource, new MoveResourceChange(resource, destination));
            }

        } catch (Exception ex) {
            UiConstants.Util.log(ex);
            status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
            return status;
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
