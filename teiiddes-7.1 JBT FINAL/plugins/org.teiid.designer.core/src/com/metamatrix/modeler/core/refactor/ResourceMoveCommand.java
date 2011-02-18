/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * ResourceMoveCommand is the ModelRefactorCommand for moving a resource to a new destination.
 */
public class ResourceMoveCommand extends ResourceRefactorCommand {

    public static final int ERROR_MISSING_CONTAINER = 1101;
    public static final int ERROR_MOVING_PROJECT = 1102;
    public static final int ERROR_PROJECT_NATURE = 1103;
    public static final int EXCEPTION_PROJECT_NATURE = 1104;
    public static final int ERROR_SUBFOLDER_MOVE = 1105;
    public static final int ERROR_READONLY_RESOURCES = 1106;
    public static final int EXCEPTION_DURING_MOVE = 1107;
    public static final int EXCEPTION_REBUILDING_MODEL_IMPORTS = 1108;
    public static final int ERROR_SAME_CONTAINER = 1109;
    public static final int ERROR_ALREADY_EXISTS_IN_CONTAINER = 1110;
    public static final int ERROR_PROJECT_CLOSED = 1111;

    private IContainer destination;
    private IStatus currentStatus;
    private IPath pathAfterMove;
    private Map pathMap;
    private Map undoMap;

    /**
     * Construct an instance of ResourceMoveCommand.
     */
    public ResourceMoveCommand() {
        super(ModelerCore.Util.getString("ResourceMoveCommand.label")); //$NON-NLS-1$
    }

    public void setDestination(IContainer destination) {
        this.destination = destination;
    }

    private void checkStatus() {
        
        // ensure destination has been set
        if (this.destination == null) {
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.No_destination_has_been_selected"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_MISSING_CONTAINER, msg, null);
            return;
        }
        
        // can't move projects
        if (getResource() instanceof IProject) {
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Cannot_move_project"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_MOVING_PROJECT, msg, null);
            return;
        }

        // can't move into a closed project
        if ( (this.destination instanceof IProject) && ! ((IProject) this.destination).isOpen() ) {
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Cannot_move_to_closed_project"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_PROJECT_CLOSED, msg, null);
            return;
        }

        // can't move into non-model projects
        try {
            if (this.destination.getProject().getNature(ModelerCore.NATURE_ID) == null) {
                final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Cannot_move_to_non_model_project"); //$NON-NLS-1$
                currentStatus = new Status(IStatus.ERROR, PID, ERROR_PROJECT_NATURE, msg, null);
                return;
            }
        } catch (CoreException e) {
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Cannot_determine_project_nature"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, EXCEPTION_PROJECT_NATURE, msg, e);
            return;
        }

        // shouldn't execute if moving into the same container as current location
        if ( getResource().getParent().equals(this.destination) ) {
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Same_container"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_SAME_CONTAINER, msg, null);
            return;
        }

        // can't move into a folder beneath itself
        if (getResource() instanceof IFolder && isSubfolder()) {
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Cannot_move_folder_inside_itself"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_SUBFOLDER_MOVE, msg, null);
            return;
        }

        // can't execute if the destination contains a resource with the same name as the selection
        if ( destinationChildNameClash() ) {
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Same_name_in_container"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_ALREADY_EXISTS_IN_CONTAINER, msg, null);
            return;
        }

        // ready to move
        final Object[] params = new Object[] { getResource().toString()};
        final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Ready_to_move", params); //$NON-NLS-1$
        currentStatus = new Status(IStatus.OK, PID, CAN_EXECUTE, msg, null);

    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#getCanExecuteStatus()
     */
    @Override
    protected IStatus getCanExecuteStatus() {
        checkStatus();
        return this.currentStatus;
    }

    /**
     * Determine if the destination is located at or beneath the target.  This test is only
     * necessary if the move target is a folder.
     * @return true if the move is illegal; false if the move is okay.
     */
    private boolean isSubfolder() {
        boolean result = false;
        // target cannot equal destination
        final String destinationPath = destination.getFullPath().toString();
        if ( destinationPath.equals(getResource().getFullPath().toString()) ) {
            result = true;
        } else {
            // destination cannot be beneath target
            final String moveTargetPath = getResource().getFullPath().toString() + '/'; 
            result = destinationPath.startsWith(moveTargetPath);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus modifyResource(final IResource resource, final IProgressMonitor monitor) {
        
        this.pathMap = new HashMap();
        this.undoMap = new HashMap();
        return this.modifyResource(resource, this.destination, this.pathMap, this.undoMap, monitor);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    private IStatus modifyResource(final IResource resource, 
                                   final IContainer targetContainer, 
                                   final Map preToPostPathMap, 
                                   final Map postToPrePathMap,
                                   final IProgressMonitor monitor) {
        
        IResource[] resources = new IResource[] { resource };
        try {
            IPath pathBeforeMove = resource.getFullPath();

            ModelResourceCollectorVisitor preMoveVisitor = new ModelResourceCollectorVisitor();    
            resource.accept(preMoveVisitor);
            ArrayList preMoveList = new ArrayList(preMoveVisitor.getResources().size());
            for ( Iterator iter = preMoveVisitor.getResources().iterator() ; iter.hasNext() ; ) {
                preMoveList.add(((IResource) iter.next()).getFullPath().makeAbsolute().toString());
            }
            
            // unload all resources beneath the resource that will be moved
            unloadModelResources(resource);

            // move the resource
            ModelerCore.getWorkspace().move(resources, targetContainer.getFullPath(), true, monitor);

            String newPathName = targetContainer.getFullPath().toString() + '/' + resource.getName();
            pathAfterMove = new Path(newPathName);
            IResource movedResource = resource.getWorkspace().getRoot().findMember(pathAfterMove);  
            super.setModifiedResource(movedResource);

            ModelResourceCollectorVisitor postMoveVisitor = new ModelResourceCollectorVisitor();    
            movedResource.accept(postMoveVisitor);
            ArrayList postMoveList = new ArrayList(postMoveVisitor.getResources().size());
            for ( Iterator iter = postMoveVisitor.getResources().iterator() ; iter.hasNext() ; ) {
                postMoveList.add(((IResource) iter.next()).getFullPath().makeAbsolute().toString());
            }
            
            preToPostPathMap.clear();
            postToPrePathMap.clear();
            if ( preMoveList.size() == postMoveList.size() ) {
                Iterator preIter = preMoveList.iterator();
                Iterator postIter = postMoveList.iterator();
                while ( preIter.hasNext() ) {
                    Object pre = preIter.next();
                    Object post = postIter.next();
                    preToPostPathMap.put(pre, post);
                    postToPrePathMap.put(post, pre);
                }
            }       
                 
            // Refactor the moved files
            IStatus result = refactorModifiedResources(monitor, this.getModifiedResource(), preToPostPathMap);
            if ( result != null && result.getSeverity() == IStatus.ERROR ) {
                return result;
            }
            
            notifyMoved(pathBeforeMove, movedResource);
            
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Move_succeeded"); //$NON-NLS-1$
            return new Status(IStatus.OK, PID, EXECUTE_SUCCEEDED, msg, null);
        } catch (CoreException e) {
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Move_failed"); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PID, EXCEPTION_DURING_MOVE, msg, e);
        }
//        return result;
    }
    
    protected IStatus refactorModifiedResources(IProgressMonitor monitor, IResource resource, final Map refactoredPaths) {

        Collection errorList = new ArrayList();
        int severity = IStatus.OK;

        try {
            // Collect all IResources within all IProjects  
            ModelResourceCollectorVisitor visitor = new ModelResourceCollectorVisitor();    
            resource.accept(visitor);
            
            // Create ModelResources for any refactored IResources ...
            for ( Iterator iter = visitor.getResources().iterator() ; iter.hasNext() ; ) {
                IFile iFile = (IFile) iter.next();
                ModelResource mResource = ModelerCore.getModelEditor().findModelResource(iFile);
                if ( mResource == null ) {
                    mResource = ModelerCore.create(iFile);
                }
                if ( mResource != null ) {
                    mResource.save(null, false);
                } else {
                    if ( severity < IStatus.WARNING ) {
                        severity = IStatus.WARNING;
                    }
                    final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Model_resource_not_in_ws", iFile.getName()); //$NON-NLS-1$
                    errorList.add( new Status(IStatus.WARNING, PID, WARNING_REBUILD_IMPORTS, msg, null) );
                }
            }
            
            // Rebuild the import lists on the refactored resources ...
            for ( Iterator iter = visitor.getResources().iterator() ; iter.hasNext() ; ) {
                IFile nextResourceIFile = (IFile) iter.next();
                ModelResource mResource = ModelerCore.getModelEditor().findModelResource(nextResourceIFile);
                if ( mResource != null ) {
                	
                	// Cache a map of import locations to model UUIDs prior to rebuilding imports.
                	Map<String, String> preImportsUuidMap = new HashMap<String, String>();
                	List<ModelImport> preImports = mResource.getModelImports();
                	for( ModelImport modelImport : preImports ) {
                		preImportsUuidMap.put(modelImport.getUuid(), modelImport.getModelLocation());
                	}
                	
                    super.rebuildImports(mResource, monitor, refactoredPaths);
                    
                    // Cache a map of import locations to model UUIDs after rebuilding imports
                	Map<String, String> postImportsUuidMap = new HashMap<String, String>();
                	List<ModelImport> postImports = mResource.getModelImports();
                	for( ModelImport modelImport : postImports ) {
                		postImportsUuidMap.put(modelImport.getUuid(), modelImport.getModelLocation());
                	}
                	
                	// Build a map of refactored "locations" so the refactor helper can fix HREFs
                	Map<String, String> importLocationChangeMap = new HashMap<String, String>();
                	for( Object nextKey : preImportsUuidMap.keySet()) {
                		String preLoc = preImportsUuidMap.get(nextKey);
                		String postLoc = postImportsUuidMap.get(nextKey);
                		if( ! postLoc.equalsIgnoreCase(preLoc) ) {
                			importLocationChangeMap.put(preLoc, postLoc);
                		}
                	}
                	
                    mResource.save(null, false);
                    // Close the resource so that when it is opened any new hrefs will be resolved.
                    mResource.close();
                    
                    mResource.unload();
                    
                    File file = new File(nextResourceIFile.getLocation().toOSString());
                    
                    ResourceRefactorFileHelper.updateHrefsForFile(file, importLocationChangeMap);
                    
                    mResource.open(new NullProgressMonitor());
                    super.buildIndexes(monitor, nextResourceIFile);
                    
                } else {
                    if ( severity < IStatus.WARNING ) {
                        severity = IStatus.WARNING;
                    }
                    final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Rebuild_model_imports_complete"); //$NON-NLS-1$
                    errorList.add( new Status(IStatus.WARNING, PID, WARNING_REBUILD_IMPORTS, msg, null) );
                }
            }
        } catch (Exception e) {
            severity = IStatus.ERROR;
            final String msg = ModelerCore.Util.getString("ResourceMoveCommand.Exception_refactoring_external_references_for_resource_0_1",resource); //$NON-NLS-1$
            errorList.add( new Status(IStatus.ERROR, PID, ERROR_REBUILD_IMPORTS, msg, e) );
        }
        
        // defect 16076 - display the correct text on completion, and display all errors
        final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Execution_complete"); //$NON-NLS-1$
        return new MultiStatus(PID, REBUILD_IMPORTS_COMPLETE, (IStatus[])errorList.toArray(EMPTY_ISTATUS), msg, null);
    }
    
    protected IStatus refactorModelContents(IProgressMonitor monitor, final Map refactoredPaths ) {
    	return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#undoResourceModification()
     */
    @Override
    protected IStatus undoResourceModification(IProgressMonitor monitor) {
        final IContainer targetContainer = super.getResource().getParent();
        return this.modifyResource(super.getModifiedResource(), targetContainer, this.pathMap, this.undoMap, monitor);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#redoResourceModification()
     */
    @Override
    protected IStatus redoResourceModification(IProgressMonitor monitor) {
        final IContainer targetContainer = this.destination;
        return this.modifyResource(super.getResource(), targetContainer, this.pathMap, this.undoMap, monitor);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#getLabel()
     */
    @Override
    public String getLabel() {
        return ModelerCore.Util.getString("ResourceMoveCommand.move_label", getResource().getName()); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#getDescription()
     */
    @Override
    public String getDescription() {
        final Object[] params = new Object[] { getResource().getName(), this.destination.getFullPath()};
        return ModelerCore.Util.getString("ResourceMoveCommand.move_description", params); //$NON-NLS-1$
    }
    
    /**
     * Determine if the target resource were moved to the proposed destination, is there another resource
     * in the same container with the same name.
     * @return true if the proposed destination would cause a name clash; otherwise, false.
     */
    private boolean destinationChildNameClash() {
        final String destinationPath = this.destination.getFullPath().toString();
        final String proposedPath = destinationPath + '/' + getResource().getName();
        final IWorkspaceRoot workspaceRoot = getResource().getWorkspace().getRoot();
        final boolean result = workspaceRoot.findMember(proposedPath) != null;
        return result;
    }
    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#getMovedResourcePaths(boolean)
     */
    @Override
    protected Map getMovedResourcePathMap(boolean isUndo) {
        if ( isUndo ) {
            return this.undoMap;
        }
        return this.pathMap;
    }
    
    /*
     * Notify with RefactorRenameEvent.TYPE_RENAME
     */
    private void notifyMoved( IPath oldPath,
                              IResource movedResource ) {
        ((ModelerCore)ModelerCore.getPlugin()).notifyRefactored(new RefactorResourceEvent(movedResource,
                                                                                          RefactorResourceEvent.TYPE_MOVE, this,
                                                                                          oldPath));
    }
}
