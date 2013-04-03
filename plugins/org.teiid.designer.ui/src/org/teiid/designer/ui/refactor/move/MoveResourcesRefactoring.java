/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.move;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.resource.MoveResourceChange;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.core.refactor.RefactorModelExtensionManager;
import org.teiid.designer.core.refactor.RefactorResourceEvent;
import org.teiid.designer.core.refactor.RelatedResourceFinder;
import org.teiid.designer.core.refactor.RelatedResourceFinder.Relationship;
import org.teiid.designer.core.refactor.ResourceStatusList;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.util.UiUtil;

/**
 *
 */
public class MoveResourcesRefactoring extends Refactoring {

    private final List<IResource> resources;
    
    private IWorkbenchWindow workbenchWindow;
    
    private IContainer destination;
    
    private Map<IResource, Change> changes;

    /**
     * @param selectedResources 
     */
    public MoveResourcesRefactoring(final List<IResource> selectedResources) {
        this.resources = selectedResources;
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
    

    /**
     * @param window
     */
    public void setWorkbenchWindow(IWorkbenchWindow window) {
        this.workbenchWindow = window;
    }

    @Override
    public String getName() {
        return MoveResourcesUtils.getString("MoveRefactoring.title"); //$NON-NLS-1$
    }
    
    /**
     * Get the resources
     * 
     * @return the resources
     */
    public List<IResource> getResources() {
        return resources;
    }
    
    private RefactoringStatus checkRelatedResourceStatus() {
        RefactoringStatus status = new RefactoringStatus();
        
        for (IResource resource : resources) {
            try {
                ModelResource modelResource = ModelUtil.getModel(resource);

                if (modelResource != null && modelResource.isReadOnly()) {
                    status.merge(RefactoringStatus.createFatalErrorStatus(MoveResourcesUtils.getString("MoveRefactoring.readOnlyResourceError", resource.getName()))); //$NON-NLS-1$
                    return status;
                }

                RelatedResourceFinder finder = new RelatedResourceFinder(resource);
                Collection<IFile> relatedFiles = finder.findRelatedResources(Relationship.ALL);

                for (IFile relatedFile : relatedFiles) {
                    try {
                        modelResource = ModelUtil.getModel(relatedFile);
                        if (modelResource != null && modelResource.isReadOnly()) {
                            status.merge(RefactoringStatus.createWarningStatus(MoveResourcesUtils.getString("MoveRefactoring.readOnlyRelatedResourceError", modelResource.getItemName()))); //$NON-NLS-1$
                        }
                    } catch (ModelWorkspaceException err) {
                        ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                    }
                }
            } catch (Exception err) {
                ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                return RefactoringStatus.createErrorStatus(err.getMessage());
            }
        }
        
        return status;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();
        
        try {
            progressMonitor.beginTask(MoveResourcesUtils.getString("MoveRefactoring.initialConditions"), 1); //$NON-NLS-1$
            
            if (resources == null || resources.isEmpty()) {
                status.merge(RefactoringStatus.createFatalErrorStatus(MoveResourcesUtils.getString("MoveRefactoring.noResourceError"))); //$NON-NLS-1$
            }
            else if (UiUtil.saveDirtyEditors(workbenchWindow, null, true)) {
                status.merge(RefactoringStatus.createFatalErrorStatus(MoveResourcesUtils.getString("MoveRefactoring.saveEditorsError"))); //$NON-NLS-1$
            }
            
            boolean result = true;
            for (IResource resource : resources) {
                if (!resource.exists()) {
                    status.merge(RefactoringStatus.createFatalErrorStatus(MoveResourcesUtils.getString("MoveRefactoring.resourceNoExistError", resource.getName()))); //$NON-NLS-1$
                    result = false;
                }
                else if (ModelUtil.isIResourceReadOnly(resource)) {
                    status.merge(RefactoringStatus.createFatalErrorStatus(MoveResourcesUtils.getString("MoveRefactoring.readOnlyResourceError", resource.getName()))); //$NON-NLS-1$
                    result = false;
                }
                else if (resource instanceof IProject) {
                    status.merge(RefactoringStatus.createFatalErrorStatus(MoveResourcesUtils.getString("MoveRefactoring.moveProjectError", resource.getName()))); //$NON-NLS-1$
                    result = false;
                }  
                else if (! RefactorModelExtensionManager.preProcess(RefactorResourceEvent.TYPE_MOVE, resource, progressMonitor)) {
                    status.merge(RefactoringStatus.createFatalErrorStatus(MoveResourcesUtils.getString("MoveRefactoring.closeVDBError"))); //$NON-NLS-1$
                    result = false;
                }
                
                if (!result) {
                    break;
                }
            }
            
            if (result) {
                // Only if the resources passed the tests above do we bother with related resources
                status.merge(checkRelatedResourceStatus());
            }
            
        } finally {
            progressMonitor.done();
        }
        
        return status;
    }

    private RefactoringStatus calculateRelatedResources() {

        RefactoringStatus status = new RefactoringStatus();
        
        String destinationPath = destination.getRawLocation().makeAbsolute().toOSString();
        Set<PathPair> pathPairs = null;
        
        try {
            pathPairs = MoveResourcesUtils.calculateResourceMoves(resources, destinationPath, MoveResourcesUtils.Option.EXCLUDE_FOLDERS);
        } catch (Exception ex) {
            UiConstants.Util.log(ex);
            status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
            return status;
        }
        
        if (pathPairs == null || pathPairs.isEmpty()) {
            status.merge(RefactoringStatus.createFatalErrorStatus(MoveResourcesUtils.getString("MoveRefactoring.emptyResourcePairsError"))); //$NON-NLS-1$
            return status;
        }
        
        System.out.println("Path being changed by this move: ");
        for (PathPair pair : pathPairs) {
            System.out.println(pair);
        }
        
        for (IResource resource : resources) {
            RelatedResourceFinder finder = new RelatedResourceFinder(resource);
            
            // Determine dependent resources
            Collection<IFile> searchResults = finder.findRelatedResources(Relationship.DEPENDENT);
            ResourceStatusList statusList = new ResourceStatusList(searchResults);

            for (IStatus problem : statusList.getProblems()) {
                status.merge(RefactoringStatus.create(problem));
            }

            for (IFile file : statusList.getResourceList()) {
                System.out.println("Calculated that " + file.getFullPath() + " is a dependent");
                try {
                    MoveResourcesUtils.unloadModelResource(file);
                    TextFileChange textFileChange = MoveResourcesUtils.calculateTextChanges(file, pathPairs);
                    changes.put(file, textFileChange);
                } catch (Exception ex) {
                    UiConstants.Util.log(ex);
                    status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                    return status;
                }
            }

            // Determine dependencies
            searchResults = finder.findRelatedResources(Relationship.DEPENDENCY);
            statusList = new ResourceStatusList(searchResults, IStatus.OK);

            for (IStatus problem : statusList.getProblems()) {
                status.merge(RefactoringStatus.create(problem));
            }

            for (IFile file : statusList.getResourceList()) {
                System.out.println("Calculated that " + file.getFullPath() + " is a dependency");
                try {
                    TextFileChange textFileChange = MoveResourcesUtils.calculateTextChanges(file, pathPairs);
                    changes.put(file, textFileChange);
                } catch (Exception ex) {
                    UiConstants.Util.log(ex);
                    status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                    return status;
                }
            }
        }

        return status;
    }

    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor progressMonitor) throws OperationCanceledException {
        RefactoringStatus status = new RefactoringStatus();
        try {
            progressMonitor.beginTask(MoveResourcesUtils.getString("MoveRefactoring.finalConditions"), 2); //$NON-NLS-1$
            changes = new LinkedHashMap<IResource, Change>();
            
            for (IResource resource : resources) {
                changes.put(resource, new MoveResourceChange(resource, destination));
                status.merge(calculateRelatedResources());
            }
        }
        finally {
            progressMonitor.done();
        }
        
        return status;
    }

    @Override
    public Change createChange(IProgressMonitor progressMonitor) throws OperationCanceledException {
        try {
            progressMonitor.beginTask(MoveResourcesUtils.getString("MoveRefactoring.creatingChange"), 1); //$NON-NLS-1$
            final Collection<Change> changeCollection = changes.values();
            CompositeChange change = new MoveResourcesCompositeChange( getName(), 
                                                                                                                      changeCollection.toArray(new Change[0]),
                                                                                                                      destination,
                                                                                                                      resources);
            return change;
        } finally {
            progressMonitor.done();
        }
    }
}
