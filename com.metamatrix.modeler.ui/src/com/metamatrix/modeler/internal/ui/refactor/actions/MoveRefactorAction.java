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

import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.window.Window;

import com.metamatrix.modeler.core.ModelEditor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.RefactorCommand;
import com.metamatrix.modeler.core.refactor.ResourceMoveCommand;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.explorer.ModelExplorerContentProvider;
import com.metamatrix.modeler.internal.ui.refactor.FileFolderMoveDialog;
import com.metamatrix.modeler.internal.ui.refactor.RefactorCommandProcessorDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.OrganizeImportHandlerDialog;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.viewsupport.JobUtils;

/**
 * MoveRefactorAction
 */
public class MoveRefactorAction extends RefactorAction {


    private IContainer dest;

    private static final String UNDO_LABEL
        = UiConstants.Util.getString("MoveRefactorAction.undoTitle"); //$NON-NLS-1$
    
    /**
     * Construct an instance of MoveRefactorAction.
     */
    public MoveRefactorAction() {
        super();
    }

    @Override
    public void run( IAction action ) {
        /*
         *  0. instantiate a move command
         *  1. instantiate the FileFolderMoveDialog
         *  2. 'init' it with: 
         *      - the selection
         *      - the rename command
         *      - a ModelContainerSelectionValidator 
         * 
         *  3. if dlg is ok,
         *      a) set the destination (from the dialog) on the command 
         *      a) execute the move command
         *      b) add the command to the UndoManager
         */           
                              
        // create the move command, set the resource on it                              
        ResourceMoveCommand rmcCommand = new ResourceMoveCommand();
        rmcCommand.setResource( resSelectedResource );
        rmcCommand.setImportHandler(new OrganizeImportHandlerDialog());
        
        // cleanup modified files before starting this operation
        boolean bContinue = doResourceCleanup();
        
        if ( !bContinue ) { return; }

        // check if anything wrong with dependents
        if (!checkDependentStatus(rmcCommand, resSelectedResource)) {
            return;
        }

        // create the dialog
        FileFolderMoveDialog ffmdDialog 
            = new FileFolderMoveDialog( UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(), 
                                        rmcCommand,
                                        resSelectedResource,
                                        new ModelExplorerContentProvider() );
            
        // launch the dialog
        ffmdDialog.open();
                
        // if result is ok, finish the command and execute it                
        if ( ffmdDialog.getReturnCode() == Window.OK ) {
            Object[] oSelectedObjects = ffmdDialog.getResult();             

            // add the user's selected destination to the command
            this.dest = (IContainer)oSelectedObjects[0];
            rmcCommand.setDestination(this.dest);
            
            // Let's cache the auto-build and reset after.  We don't want auto-building before the refactoring is complete
            boolean autoBuildOn = ResourcesPlugin.getWorkspace().isAutoBuilding();
            if( autoBuildOn ) {
                JobUtils.setAutoBuild(false);
            }
            
            // run it
            executeCommand( rmcCommand );
            
            if( autoBuildOn ) {
                JobUtils.setAutoBuild(true);
            }
            
            // add the command to the Undo Manager (the manager will deal with whether 
            //  the command can be undone or not)            
            if ( getStatus() != null && getStatus().getSeverity() < IStatus.ERROR ) {             
                getRefactorUndoManager().addCommand( rmcCommand );
            }
            
            // if there are problems, use the common error dialog to report them
            if ( rmcCommand.getPostExecuteMessages() != null 
              && rmcCommand.getPostExecuteMessages().size() > 0 ) {
                 
//                System.out.println( "[MoveRefactorAction.run] command has messages: " + rmcCommand.getPostExecuteMessages().size() );  //$NON-NLS-1$

                RefactorCommandProcessorDialog rcpdDialog
                    = new RefactorCommandProcessorDialog( UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
                                                          rmcCommand );
                rcpdDialog.open();                
           
            } else {
//                 System.out.println( "[MoveRefactorAction.run] command has NO messages" );   //$NON-NLS-1$
            }
        }                        
                   
    }

    @Override
    protected IProgressMonitor executeCommand( final RefactorCommand command ) {
        IProgressMonitor monitor = super.executeCommand(command);
        
        try {
            // Save refactored resource
            final ModelEditor editor = ModelerCore.getModelEditor();

            // defect 16527 - check that a resource is a file before casting
            IResource res = this.dest.findMember(MoveRefactorAction.this.resSelectedResource.getName());
            if (res instanceof IFile) {
                final IFile file = (IFile) res;
                ModelResource model = editor.findModelResource(file);
                if (model != null) {
                    if (model.getEmfResource().isModified()) {
                    	// If an editor is open, call doSave on it, else tell the model to save
                    	com.metamatrix.modeler.internal.ui.editors.ModelEditor openEditor = ModelEditorManager.getModelEditorForFile(file, false);
                    	if(openEditor != null ) {
                    		openEditor.doSave(monitor);
                    	} else {
                    		model.save(monitor, true);
                    	}
                    }
                }
            } // endif -- move was on a file

            // Save modified dependent resources
            for (final Iterator iter = ((ResourceMoveCommand)command).getDependentResources().iterator();
                 iter.hasNext();) {
            	IFile file = (IFile)iter.next();
                ModelResource model = editor.findModelResource(file);
                if (model != null) {
                    if (model.getEmfResource().isModified()
                     && !model.isReadOnly()) {
                    	// If an editor is open, call doSave on it, else tell the model to save
                    	com.metamatrix.modeler.internal.ui.editors.ModelEditor openEditor = ModelEditorManager.getModelEditorForFile(file, false);
                    	if(openEditor != null ) {
                    		openEditor.doSave(monitor);
                    	} else {
                    		model.save(monitor, true);
                    	}
                    }
                }
            }
        } catch (final ModelWorkspaceException err) {
            ModelerCore.Util.log(err);
        }
        
        return monitor;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.refactor.actions.RefactorAction#getUndoLabel()
     */
    @Override
    protected String getUndoLabel() {
        return UNDO_LABEL;
    }

}
