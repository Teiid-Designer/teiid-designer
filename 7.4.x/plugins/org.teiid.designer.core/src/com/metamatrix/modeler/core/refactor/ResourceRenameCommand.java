/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.validation.ValidationProblem;
import com.metamatrix.modeler.core.validation.rules.CoreValidationRulesUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.validation.ValidationResultImpl;

/**
 * ResourceRenameCommand is a specialization of ResourceRefactorCommand for renaming files
 * and folders.
 */
public class ResourceRenameCommand extends ResourceRefactorCommand {

    public static final int ERROR_MISSING_NAME = 1201;
    public static final int ERROR_RENAME_PROJECT = 1202;
    public static final int ERROR_READONLY_RESOURCES = 1206;
    public static final int EXCEPTION_DURING_RENAME = 1207;
    public static final int ERROR_SIBLING_NAME = 1208;
    public static final int ERROR_SAME_NAME = 1208;
    private static final String UNDO_RENAME = ModelerCore.Util.getString("ResourceRenameCommand.label"); //$NON-NLS-1$
    
    private IStatus currentStatus;
    private String nameBeforeRename;
    private String extension;
    private HashMap pathMap;
    private HashMap undoMap;
    private String undoLabel = UNDO_RENAME;
    private String undoDescription = UNDO_RENAME;

    /**
     * Construct an instance of ResourceRenameCommand.
     */
    public ResourceRenameCommand() {
        super(UNDO_RENAME);
    }
    
    private void checkStatus() {
        
        if (this.getNewName() == null || this.getNewName().length() == 0 ) { 
            final String msg = ModelerCore.Util.getString("ResourceRenameCommand.No_name_has_been_selected"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_MISSING_NAME, msg, null);
            return;
        }
        
        if (getResource() instanceof IProject) {
            final String msg = ModelerCore.Util.getString("ResourceRenameCommand.Cannot_rename_project"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_RENAME_PROJECT, msg, null);
            return;
        }
                
        // check the name
        final ValidationResultImpl result = new ValidationResultImpl(this.getNewName());
        CoreValidationRulesUtil.validateStringNameChars(result, this.getNewName(), null);
        if (result.hasProblems()) {
            ValidationProblem problem = result.getProblems()[0];
            currentStatus = new Status(problem.getSeverity(), PID, problem.getCode(), problem.getMessage(), null);
            return;
        }

        // check for meaningless operation
        if ( this.getNewName().equals(this.nameBeforeRename) ) {
            final String msg = ModelerCore.Util.getString("ResourceRenameCommand.Same_as_old_name"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_SAME_NAME, msg, null);
            return;
        }
        
        // check for siblings
        if ( siblingNameClash(this.getNewName()) ) {
            final String msg = ModelerCore.Util.getString("ResourceRenameCommand.Name_already_exists_in_container"); //$NON-NLS-1$
            currentStatus = new Status(IStatus.ERROR, PID, ERROR_SIBLING_NAME, msg, null);
            return;
        }

        final Object[] params = new Object[] { this.nameBeforeRename, this.getNewName() };
        final String msg = ModelerCore.Util.getString("ResourceRenameCommand.Ready_to_rename", params); //$NON-NLS-1$
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

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#modifyResource(org.eclipse.core.resources.IResource, org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    protected IStatus modifyResource(final IResource resource, final IProgressMonitor monitor) {
        
        this.pathMap = new HashMap();
        this.undoMap = new HashMap();
        return this.modifyResource(resource, this.getNewName(), this.pathMap, this.undoMap, monitor);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#execute(org.eclipse.core.runtime.IProgressMonitor)
     */
    private IStatus modifyResource(final IResource resource, 
                                   final String newResourceName, 
                                   final Map preToPostPathMap, 
                                   final Map postToPrePathMap,
                                   final IProgressMonitor monitor) {
        
        IPath oldResourcePath = null;
        
       try {
           IResource resourceToRename = resource;
           this.nameBeforeRename = resourceToRename.getName();
           oldResourcePath = resourceToRename.getFullPath();
           if ( resource instanceof IFile ) {
               extension = ((IFile) resourceToRename).getFileExtension();
               if ( extension != null && extension.length() > 0 ) {
                   this.nameBeforeRename = nameBeforeRename.substring(0, nameBeforeRename.lastIndexOf(extension) - 1);
               }
           }

           ModelResourceCollectorVisitor preMoveVisitor = new ModelResourceCollectorVisitor();    
           resource.accept(preMoveVisitor);
           ArrayList preMoveList = new ArrayList(preMoveVisitor.getResources().size());
           for ( Iterator iter = preMoveVisitor.getResources().iterator() ; iter.hasNext() ; ) {
               preMoveList.add(((IResource) iter.next()).getFullPath().makeAbsolute().toString());
           }

           // unload all resources beneath the resource that will be renamed
           unloadModelResources(resource);

           // rename the resource        
           setResourceName(resource, newResourceName, monitor);

           // Get the refactored resources using the new resource
           String newPathString = getModifiedPathString(newResourceName);
           IWorkspaceRoot workspaceRoot = getResource().getWorkspace().getRoot();
           
           
           IResource newResource = workspaceRoot.findMember(newPathString);
           
           if(newResource == null) {
               final String msg = ModelerCore.Util.getString("ResourceRenameCommand.Rename_failed_resource_not_found", newPathString); //$NON-NLS-1$
               return new Status(IStatus.ERROR, PID, EXCEPTION_DURING_RENAME, msg, null);
           }
           
           super.setModifiedResource(newResource);

           ModelResourceCollectorVisitor postMoveVisitor = new ModelResourceCollectorVisitor();               
           newResource.accept(postMoveVisitor);
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
           
           // Refactor the renamed files
           IStatus result = refactorModifiedResources(monitor, this.getModifiedResource(), preToPostPathMap);
           if ( result != null && result.getSeverity() == IStatus.ERROR ) {
               return result;
           }
         
           final String msg = ModelerCore.Util.getString("ResourceRenameCommand.Rename_succeeded"); //$NON-NLS-1$
           
           notifyRenamed(oldResourcePath, newResource);
           
           return new Status(IStatus.OK, PID, EXECUTE_SUCCEEDED, msg, null);
       } catch (CoreException e) {
           final String msg = ModelerCore.Util.getString("ResourceRenameCommand.Rename_failed"); //$NON-NLS-1$
           return new Status(IStatus.ERROR, PID, EXCEPTION_DURING_RENAME, msg, e);
       }
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
                    super.buildIndexes(monitor, iFile);
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
                IFile iFile = (IFile) iter.next();
                ModelResource mResource = ModelerCore.getModelEditor().findModelResource(iFile);
                if ( mResource != null ) {
                    super.rebuildImports(mResource, monitor, refactoredPaths);
                    mResource.save(null, false);
                    // Close the resource so that when it is opened any new hrefs will be resolved.
                    mResource.close();
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
        
	/**
     * Determine the path for the target resource if the name were to change to the proposed name.
     * Takes into account the extension, if a file and the extension exists.
     * @param proposedName
     * @return
     */
    private String getModifiedPathString(String proposedName) {
        final String parentPath = getResource().getParent().getFullPath().toString();
        String newPath = parentPath + '/' + proposedName;
        if ( this.extension != null && this.extension.length() > 0 ) { 
            newPath += ('.' + this.extension );
        }
        return newPath;
    }

    /**
     * Determine if the target resource were changed to the proposed name, is there another resource
     * in the same container already named the proposed name.
     * @param proposedName
     * @return true if the proposed name clashes with a sibling; otherwise, false.
     */
    private boolean siblingNameClash(String proposedName) {
        final String newPath = getModifiedPathString(proposedName);
        final IWorkspaceRoot workspaceRoot = getResource().getWorkspace().getRoot();
        final boolean result = workspaceRoot.findMember(newPath) != null;
        return result;
    }

    /**
     * Set the name of this object's target resource to the specified name.
     * @param newName
     * @param monitor
     */
    private void setResourceName(final IResource oldResource, final String newName, final IProgressMonitor monitor) {
        monitor.beginTask(ModelerCore.Util.getString("ResourceRenameCommand.Monitor_begin_rename"), 100); //$NON-NLS-1$

        String newPathString = getModifiedPathString(newName);
        IWorkspaceRoot workspaceRoot = oldResource.getWorkspace().getRoot();
        try {
            IPath newPath = new Path(newPathString);
            IResource newResource = workspaceRoot.findMember(newPathString);
            if (newResource != null) {
                if (oldResource.getType() == IResource.FILE && newResource.getType() == IResource.FILE) {
                    IFile file = (IFile) oldResource;
                    IFile newFile = (IFile) newResource;
                    setUndo(file, newFile);                    
                    
                    IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 50);
                    newFile.setContents(file.getContents(), IResource.KEEP_HISTORY, subMonitor);
                    file.delete(IResource.KEEP_HISTORY, subMonitor); 
                    monitor.worked(100);
                    return;
                }
                newResource.delete(IResource.KEEP_HISTORY, new SubProgressMonitor(monitor, 50));
            }
            if (oldResource.getType() == IResource.PROJECT) {
                IProject project = (IProject) oldResource;
                IProjectDescription description = project.getDescription();
                description.setName(newPath.segment(0));
                project.move(description, IResource.FORCE | IResource.SHALLOW, monitor);
            } else {
                // jh Defect 19539: set the FORCE flag here to fix the 'hosed up' problem...
                oldResource.move(newPath, IResource.KEEP_HISTORY | IResource.SHALLOW | IResource.FORCE, new SubProgressMonitor(monitor, 50));                
            }
        } catch (Exception e) {
            final String msg = ModelerCore.Util.getString("ResourceRenameCommand.Exception_during_rename", newName ); //$NON-NLS-1$
            super.addProblem( new Status(IStatus.ERROR, PID, EXCEPTION_DURING_RENAME, msg, e) );
        }
        
    }
    
    protected IStatus refactorModelContents(IProgressMonitor monitor, final Map refactoredPaths ) {
        Collection errorList = new ArrayList();

        IResource modifiedRes = this.getModifiedResource();
        
        // Do a check so we don't process folders or projects
        if( modifiedRes instanceof IFile ) {
	        final ModelEditor editor = ModelerCore.getModelEditor();
	        try {
	            ModelResource modelResource = editor.findModelResource((IFile)this.getModifiedResource());
	            if (modelResource != null) {
	                RefactorModelExtensionManager.helpUpdateModelContents(IRefactorModelHandler.RENAME, modelResource, refactoredPaths, monitor);
	                    
	                modelResource.save(null, false);
	            }
	
	        } catch (ModelWorkspaceException e) {
	            final String msg = ModelerCore.Util.getString("ResourceRefactorCommand.Exception_finding_model_resource", this.getModifiedResource().getName()); //$NON-NLS-1$
	            errorList.add(new Status(IStatus.ERROR, PID, REFACTOR_MODIFIED_RESOURCE_ERROR, msg, e));
	        }
        }
        
        // defect 16076 - display the correct text on completion, and display all errors
        String msg = ModelerCore.Util.getString("ResourceRefactorCommand.update_model_contents_complete"); //$NON-NLS-1$
        MultiStatus multiStatus = new MultiStatus(PID, REFACTOR_MODIFIED_RESOURCE_COMPLETE, (IStatus[])errorList.toArray(EMPTY_ISTATUS),
                                                  msg, null);
        if (!multiStatus.isOK()) {
            msg = ModelerCore.Util.getString("ResourceRefactorCommand.update_model_contents_error"); //$NON-NLS-1$
            multiStatus = new MultiStatus(PID, REFACTOR_MODIFIED_RESOURCE_ERROR, (IStatus[])errorList.toArray(EMPTY_ISTATUS), msg, null);
        }
        return multiStatus;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#undo()
     */
    @Override
    public IStatus undoResourceModification(IProgressMonitor monitor) {
//        setResourceName(this.nameBeforeRename, monitor);
//        return null;

        return this.modifyResource(super.getModifiedResource(), this.nameBeforeRename, this.pathMap, this.undoMap, monitor);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#redo()
     */
    @Override
    public IStatus redoResourceModification(IProgressMonitor monitor) {
//        setResourceName(this.nameAfterRename, monitor);
//        return null;

        return this.modifyResource(super.getModifiedResource(), this.getNewName(), this.pathMap, this.undoMap, monitor);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#getLabel()
     */
    @Override
    public String getLabel() {
        return undoLabel;
    }

    /**
     * Return the renamed IResource as the result 
     * @see com.metamatrix.modeler.core.refactor.RefactorCommand#getResult()
     * @since 4.3
     */
    @Override
    public Collection getResult() {
        return Collections.singletonList(getModifiedResource());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ModelRefactorCommand#getDescription()
     */
    @Override
    public String getDescription() {
        return undoDescription;
    }

    /* (non-Javadoc)
     * Overridden to capture the file extension.
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#setResource(org.eclipse.core.resources.IResource)
     */
    @Override
    public void setResource(IResource resource) {
        super.setResource(resource);
        this.extension = resource.getFileExtension();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#getMovedResourcePaths(boolean)
     */
    @Override
    protected Map getMovedResourcePathMap(boolean isUndo) {
        return this.pathMap;
    }

    private void setUndo(IFile oldFile, IFile newFile) {
        final Object[] params = new Object[] { oldFile.getName(), newFile.getName() };
        undoLabel = ModelerCore.Util.getString("ResourceRenameCommand.rename_label", params ); //$NON-NLS-1$
        undoDescription = ModelerCore.Util.getString("ResourceRenameCommand.rename_description", params ); //$NON-NLS-1$
    }
    
    /*
     * Notify with RefactorRenameEvent.TYPE_RENAME
     */
    private void notifyRenamed( IPath oldPath,
                                IResource renamedResource ) {
        ((ModelerCore)ModelerCore.getPlugin()).notifyRefactored(new RefactorResourceEvent(renamedResource,
                                                                                          RefactorResourceEvent.TYPE_RENAME,
                                                                                          this, oldPath));
    }
}
