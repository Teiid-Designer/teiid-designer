/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.rename;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.core.refactor.RelatedResourceFinder;
import org.teiid.designer.core.refactor.RelatedResourceFinder.Relationship;
import org.teiid.designer.core.refactor.ResourceStatusList;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.refactor.AbstractResourcesRefactoring;
import org.teiid.designer.ui.refactor.RefactorResourcesUtils;

/**
 * Refactoring for a rename operation
 */
public class RenameResourceRefactoring extends AbstractResourcesRefactoring {

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
    public RefactoringStatus checkInitialConditions(IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();

        try {
            progressMonitor.beginTask(RefactorResourcesUtils.getString("RenameRefactoring.initialConditions"), 1); //$NON-NLS-1$

            checkResourcesNotEmpty(status);
            closeDirtyEditors(status);

            boolean result = true;
            for (IResource resource : getResources()) {
                result = checkResourceExists(resource, status);
                if (!result) break;

                result = checkResourceReadOnly(resource, status);
                if (!result) break;

                result = checkExtensionManager(resource, RefactorType.RENAME, progressMonitor, status);
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

    /**
     * @param resource
     */
    private void calculateRelatedVdbResources(IResource resource) {
        IResource[] vdbResources = WorkspaceResourceFinderUtil.getVdbResourcesThatContain(resource);
        for (IResource vdb : vdbResources) {
            if (! (vdb instanceof IFile))
                continue;
            
            IPath oldResourcePath = resource.getFullPath();
            IPath newResourcePath = oldResourcePath.removeLastSegments(1).append(getNewResourceName());
            addVdbChange((IFile) vdb, oldResourcePath, newResourcePath);
        }
    }
    
    private void analyseRelatedFile(IFile relatedFile, PathPair pathPair) throws Exception {
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
            calculateRelatedVdbResources(relatedFile);
        }
    }

    private void calculateRelatedResources(RefactoringStatus status, PathPair pathPair) {
        IResource resource = getResource();
        
        RelatedResourceFinder finder = new RelatedResourceFinder(resource);
            
        // Determine dependent resources
        Collection<IFile> searchResults = finder.findRelatedResources(Relationship.DEPENDENT);
        ResourceStatusList statusList = new ResourceStatusList(searchResults);

        for (IStatus problem : statusList.getProblems()) {
            status.merge(RefactoringStatus.create(problem));
        }

        for (IFile file : statusList.getResourceList()) {
            try {
                analyseRelatedFile(file, pathPair);
            } catch (Exception ex) {
                UiConstants.Util.log(ex);
                status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                return;
            }
        }

        // Determine dependencies
        searchResults = finder.findRelatedResources(Relationship.DEPENDENCY);
        statusList = new ResourceStatusList(searchResults, IStatus.OK);

        for (IStatus problem : statusList.getProblems()) {
            status.merge(RefactoringStatus.create(problem));
        }

        for (IFile file : statusList.getResourceList()) {
            try {
                analyseRelatedFile(file, pathPair);
            } catch (Exception ex) {
                UiConstants.Util.log(ex);
                status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                return;
            }
        }
        
        calculateRelatedVdbResources(resource);
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

                if (ModelUtil.isModelFile(resource)) {
                    // Add change for updating of any SQL contained in the resource
                    try {
                        TextFileChange textFileChange = new TextFileChange(resource.getName(), (IFile) resource);
                        RefactorResourcesUtils.calculateSQLChanges((IFile) resource, pathPair, textFileChange);
                        addChange(resource, textFileChange);

                        calculateRelatedVdbResources(resource);
                    } catch (Exception ex) {
                        UiConstants.Util.log(ex);
                        status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                    }
                }          

                // Add changes for related resources
                calculateRelatedResources(status, pathPair);
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
