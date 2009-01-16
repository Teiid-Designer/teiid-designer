/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.internal.ui.refactor.actions;

import java.text.MessageFormat;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.ResourceRefactorCommand;
import com.metamatrix.modeler.core.refactor.ResourceRenameCommand;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.refactor.FileFolderRenameDialog;
import com.metamatrix.modeler.internal.ui.refactor.RefactorCommandProcessorDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.OrganizeImportHandlerDialog;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.viewsupport.UiBusyIndicator;
import com.metamatrix.vdb.edit.refactor.ResourceRenameVdbCommand;

/**
 * RenameRefactorAction
 */
public class RenameRefactorAction extends RefactorAction {


    private static final String UNDO_LABEL
        = UiConstants.Util.getString("RenameRefactorAction.undoTitle"); //$NON-NLS-1$
    private static final String READ_ONLY_FILE_IN_FOLDER_TITLE 
        = UiConstants.Util.getString("RenameRefactorAction.readOnlyFileInFolderTitle");     //$NON-NLS-1$


    /**
     * Construct an instance of MoveRefactorAction.
     */
    public RenameRefactorAction() {
        super();
    }
    
    @Override
    public void run( IAction action ) {
        // We have to take into account different resource types.  First operate on things we know which are
        // models and folders
        if( isModelOrVdbFile() ) {
            renameFolderOrModel();
        } else {
            // Now we handle generic resource
            renameResource();
        }
    }
    
//  MyDefect : Refactored
    private boolean isModelOrVdbFile() {
        return ModelUtilities.isModelFile(resSelectedResource) 
                || resSelectedResource instanceof IFolder                         
                //MyDefect : added for defect 17255
                || ModelUtilities.isVdbFile(resSelectedResource);
    }
    
    
    private void renameFolderOrModel() {
        // Might have selected a folder, so we need to check that all files in it are writable, else
        // don't run
        
        boolean bFolderCheck = folderReadOnlyCheck();
        
        if( !bFolderCheck ) { return; }
        
        /*
         *  0. instantiate a move command
         *  1. instantiate the FileFolderMoveDialog
         *  2. 'init' it with: 
         *      - the selection
         *      - the rename command
         *      - a ModelContainerSelectionValidator 
         * 
         *  2. if dlg is ok,
         *      a) set the destination (from the dialog) on the command 
         *      a) execute the move command
         *      b) add the command to the UndoManager
         */           
        

        // create the move command, set the resource on it                              
//        ResourceRenameCommand rrcCommand = new ResourceRenameCommand();
        //MyDefect : Added this to use the base class
        ResourceRefactorCommand newRenameCommand = new ResourceRenameCommand();
        
        if(ModelUtilities.isVdbFile(resSelectedResource) ) {
            newRenameCommand = new ResourceRenameVdbCommand();
        }
        
        final ResourceRefactorCommand rrcCommand = newRenameCommand;
        
        rrcCommand.setResource( resSelectedResource );
        rrcCommand.setImportHandler(new OrganizeImportHandlerDialog());
        
        // cleanup modified files before starting this operation
        boolean bContinue = doResourceCleanup();
        
        if ( !bContinue ) { return; }
        
        // check if anything wrong with dependents
        if (!checkDependentStatus(rrcCommand, resSelectedResource)) {
            return;
        }
        
        // create the dialog
        FileFolderRenameDialog ffrdDialog = createFileFolderRenameDialog(rrcCommand);
           
        // launch the dialog
        ffrdDialog.open();
                
        // if result is ok, finish the command and execute it                
        if ( ffrdDialog.getReturnCode() == Window.OK ) {
            try {
                // get the new name from the dialog
                rrcCommand.setLabel( ffrdDialog.getNewName() );
                
                boolean wasOpen = false;
                IWorkbenchPage  iwbp = getIWorkbenchPage();
                
                // Make sure this is not a folder
                if( resSelectedResource instanceof IFile) {
                    // determine if file open:
                    wasOpen = isEditorOpen(iwbp, resSelectedResource);
                }
                
                // run it
                UiBusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
                    public void run() {
                        executeCommand( rrcCommand );
                    }
                });
                
    
                // add the command to the Undo Manager (the manager will deal with whether 
                //  the command can be undone or not)            
                if ( getStatus() != null && getStatus().getSeverity() < IStatus.ERROR ) {             
                    getRefactorUndoManager().addCommand( rrcCommand );
                }
                
                // if there are problems, use the common error dialog to report them
                displayErrorMessage(rrcCommand);
                    
                // reopen if needed (if the rename failed, this will fail silently):                  
                String newName = ffrdDialog.getNewName()+'.'+resSelectedResource.getFileExtension();
                reOpenEditor(wasOpen, newName, iwbp);
            }
            catch (CoreException err) {
                ModelerCore.Util.log( IStatus.ERROR, err, err.getMessage() );
            }
            
        } // endif -- user pressed OK in dialog.
        
        return;
    }
    
//  MyDefect : Refactored
    private FileFolderRenameDialog createFileFolderRenameDialog(ResourceRefactorCommand rrcCommand) {
        return new FileFolderRenameDialog( UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                    rrcCommand,
                                    resSelectedResource  );
    }
    
//  MyDefect : Refactored
    private void displayErrorMessage(ResourceRefactorCommand rrcCommand) {
        // if there are problems, use the common error dialog to report them
        if ( rrcCommand.getPostExecuteMessages() != null 
          && rrcCommand.getPostExecuteMessages().size() > 0 ) {   
            RefactorCommandProcessorDialog rcpdDialog
                = new RefactorCommandProcessorDialog( UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                                          rrcCommand );
            
            rcpdDialog.open();                       
        }
    }
    
    private void renameResource() {

        try {
            String newName = queryNewResourceName(resSelectedResource);
            if (newName == null || newName.equals(""))//$NON-NLS-1$
                return;
            
            IPath newPath = resSelectedResource.getFullPath().removeLastSegments(1).append(newName);
            
            IWorkspaceRoot workspaceRoot = resSelectedResource.getWorkspace().getRoot();
            IProgressMonitor monitor = new NullProgressMonitor();
            IResource newResource = workspaceRoot.findMember(newPath);

            IWorkbenchPage  iwbp = getIWorkbenchPage();
            
            // determine if file open in workspace...
            boolean wasOpen = isEditorOpen(iwbp, resSelectedResource);
            
            // do the move:
            if (newResource != null) {
                if (checkOverwrite(getShell(), newResource)) {
                    if (resSelectedResource.getType() == IResource.FILE && newResource.getType() == IResource.FILE) {
                        IFile file = (IFile) resSelectedResource;
                        IFile newFile = (IFile) newResource;
                        
                        // need to check for the case that a file we are overwriting is open,
                        //  and re-open it if it is, regardless of whether the old file was open
                        wasOpen = isEditorOpen(iwbp, newResource);
                        
                        if (validateEdit(file, newFile, getShell())) {
                            newFile.setContents(file.getContents(), IResource.KEEP_HISTORY, monitor);
                            file.delete(IResource.KEEP_HISTORY, monitor); 
                        }
                    }                    
                }
            } else {
                resSelectedResource.move(newPath, IResource.KEEP_HISTORY | IResource.SHALLOW, new SubProgressMonitor(monitor, 50));
            }
           
            reOpenEditor(wasOpen, newName, iwbp);
            
        }
        catch (CoreException err) {
            ModelerCore.Util.log( IStatus.ERROR, err, err.getMessage() );
        }
    }

//  MyDefect : Refactored
    private IWorkbenchPage getIWorkbenchPage() {
        // defect 16103 - rename partially fails (copy works, delete fails) while editor open.
        IWorkbench iwb = UiPlugin.getDefault().getWorkbench();
        return iwb.getActiveWorkbenchWindow().getActivePage();
    }
    
//  MyDefect : Refactored
    private boolean isEditorOpen(IWorkbenchPage iwbp, IResource currentResource) {
        boolean wasOpen = false;
        
        IEditorPart iep = getResourceEditorPart(iwbp, currentResource);
        wasOpen = iep != null;
        if (wasOpen) {
            iwbp.closeEditor(iep, true);
        }
        
        return wasOpen; 
    }
    
//  MyDefect : Refactored
    private void reOpenEditor(boolean wasOpen, String newName, IWorkbenchPage  iwbp)throws PartInitException {
        // reopen if needed (if the rename failed, this will fail silently):
        if (wasOpen) {
            IContainer folder = resSelectedResource.getParent();
            IResource newResource = folder.findMember(newName);
            if (newResource != null) {
                IDE.openEditor(iwbp, (IFile)newResource);
            } 
        }
    }
    
    /** Gets the IEditorPart for the resource if open in the workspace.
     * @param iwbp
     * @param res
     * @return
     */
    private IEditorPart getResourceEditorPart(IWorkbenchPage iwbp, IResource res) {
        if( res instanceof IFile) {
            FileEditorInput edIn = new FileEditorInput((IFile) res);
            IEditorPart      iep = iwbp.findEditor(edIn);
            return iep;
        } // endif -- res was IFile

        // not an IFile:
        return null;
    }

    private boolean folderReadOnlyCheck() {
        boolean allOK = true;
        if( resSelectedResource != null && resSelectedResource instanceof IFolder ) {
            // Is Folder, check read-only of contents
            IResource[] contents = null;
            try { 
                contents = ((IFolder) resSelectedResource).members();
            } catch (CoreException e) {
            }
            if( contents != null ) {
                // Check all resources
                boolean allWritable = true;
                for(int i=0; i<contents.length; i++ ) {
                    if(ModelUtil.isIResourceReadOnly(contents[i])) {
                        allWritable = false;
                    }
                    if( !allWritable )
                        break;
                }
                if( !allWritable ) {
                    allOK = false;
                    // tell the user they can't rename:
                    String message = UiConstants.Util.getString("RenameRefactorAction.folderContainsReadOnlyFiles",     //$NON-NLS-1$
                                                             resSelectedResource.getName());
                    MessageDialog.openError(getShell(), READ_ONLY_FILE_IN_FOLDER_TITLE, message);
                }
            }
        }
        
        return allOK;
    }
    
    /**
     * Return the new name to be given to the target resource.
     *
     * @return java.lang.String
     * @param context IVisualPart
     */
    protected String queryNewResourceName(final IResource resource) {
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IPath prefix = resource.getFullPath().removeLastSegments(1);
        IInputValidator validator = new IInputValidator() {
            public String isValid(String string) {
                if (resource.getName().equals(string)) {
                    return UiConstants.Util.getString("RenameResourceAction.nameMustBeDifferent"); //$NON-NLS-1$
                }
                IStatus status = workspace.validateName(string, resource.getType());
                if (!status.isOK()) {
                    return status.getMessage();
                }
                if (workspace.getRoot().exists(prefix.append(string))) {
                    return UiConstants.Util.getString("RenameResourceAction.nameExists"); //$NON-NLS-1$
                }
                return null;
            }
        };
            
        InputDialog dialog = new InputDialog(
            getShell(),
            UiConstants.Util.getString("RenameResourceAction.inputDialogTitle"),  //$NON-NLS-1$
            UiConstants.Util.getString("RenameResourceAction.inputDialogMessage"), //$NON-NLS-1$
            resource.getName(), 
            validator); 
        dialog.setBlockOnOpen(true);
        dialog.open();
        return dialog.getValue();
    }
    /**
     * Check if the user wishes to overwrite the supplied resource
     * @returns true if there is no collision or delete was successful
     * @param shell the shell to create the dialog in 
     * @param destination - the resource to be overwritten
     */
    private boolean checkOverwrite(final Shell shell, final IResource destination) {

        final boolean[] result = new boolean[1];

        //Run it inside of a runnable to make sure we get to parent off of the shell as we are not
        //in the UI thread.

        Runnable query = new Runnable() {
            public void run() {
                String pathName = destination.getFullPath().makeRelative().toString();
                result[0] =
                    MessageDialog.openQuestion(
                        shell,
                        "Resource Already Exists", //RESOURCE_EXISTS_TITLE, //$NON-NLS-1$
                        MessageFormat.format("Overwrite File?", new Object[] {pathName})); //$NON-NLS-1$
            }

        };

        shell.getDisplay().syncExec(query);
        return result[0];
    }
    
    /**
     * Validates the destination file if it is read-only and additionally 
     * the source file if both are read-only.
     * Returns true if both files could be made writeable.
     * 
     * @param source source file
     * @param destination destination file
     * @param shell ui context for the validation
     * @return boolean <code>true</code> both files could be made writeable.
     *  <code>false</code> either one or both files were not made writeable  
     */
    boolean validateEdit(IFile source, IFile destination, Shell shell) {
        if (destination.isReadOnly()) {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IStatus status;
            if (source.isReadOnly())
                status = workspace.validateEdit(new IFile[] {source, destination}, shell);
            else
                status = workspace.validateEdit(new IFile[] {destination}, shell);  
            return status.isOK();
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.refactor.actions.RefactorAction#getUndoLabel()
     */
    @Override
    protected String getUndoLabel() {
        return UNDO_LABEL;
    }
}
