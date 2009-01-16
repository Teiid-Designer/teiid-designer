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

package com.metamatrix.modeler.internal.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.actions.workers.DeleteWorker;
import com.metamatrix.modeler.internal.ui.refactor.RefactorCommandProcessorDialog;
import com.metamatrix.modeler.internal.ui.refactor.SaveModifiedResourcesDialog;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.TransactionSettings;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

/**
 * The <code>DeleteAction</code> class is the action that handles the global delete.
 * @since 4.0
 */
public class DeleteAction extends ModelObjectAction {

    //============================================================================================================================
    // Constants
    
    //============================================================================================================================
    // Fields
    
    /** The child type descriptor. */

    private DeleteWorker worker;
    private static final String CANNOT_UNDO_TITLE               = "DeleteAction.cannotUndoTitle"; //$NON-NLS-1$
    private static final String CANNOT_UNDO_MSG                 = "DeleteAction.cannotUndoMsg"; //$NON-NLS-1$
    private static final String READ_ONLY_DEPENDENCIES_TITLE    = "DeleteAction.readOnlyDependenciesTitle"; //$NON-NLS-1$
    private static final String READ_ONLY_DEPENDENCIES_MSG      = "DeleteAction.readOnlyDependenciesMsg"; //$NON-NLS-1$
    private static final String MODIFY_DEPENDENCIES_TITLE       = "DeleteAction.modifyDependenciesTitle"; //$NON-NLS-1$
    private static final String MODIFY_DEPENDENCIES_MSG         = "DeleteAction.modifyDependenciesMsg"; //$NON-NLS-1$
    private static final String FIND_RESOURCE_ERROR_MSG         = "DeleteAction.findResourceErrorMsg"; //$NON-NLS-1$

    //============================================================================================================================
    // Constructors
    
    public DeleteAction() {
        super(UiPlugin.getDefault());

        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
        worker = new DeleteWorker(true);
        setActionWorker(worker);
    }
    

    
    @Override
    public void doRun() {
        worker.createObjectDeleteCommand();
        // cleanup modified files before starting this operation
        boolean bContinue = doResourceCleanup();
 
        if ( bContinue ) {
            super.doRun();

            // if there are problems, use the common error dialog to report them
            if (   worker.getObjectDeleteCommand().getPostExecuteMessages() != null 
                && worker.getObjectDeleteCommand().getPostExecuteMessages().size() > 0 ) {

                RefactorCommandProcessorDialog rcpdDialog
                    = new RefactorCommandProcessorDialog( getShell(), worker.getObjectDeleteCommand() );
                rcpdDialog.open();                           
            }  
        }                      

//        worker.selectionChanged(getSelection());
        }
        
    private Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }
    
    private boolean doResourceCleanup() {
        boolean bResult = true;
        
        if ( ModelEditorManager.getDirtyResources().size() > 0 ) {
            
            // create the dialog, passing in the List of selected resources so it can ignore them
            /*
             * fix for defect 12375: the 'ignore' set must also include the resources of the selected objects 
             */
            SaveModifiedResourcesDialog pnlSave 
                = new SaveModifiedResourcesDialog( getShell(), getResourcesToIgnore() );
            
            if ( !pnlSave.getResourcesToDisplay().isEmpty() ) {
                pnlSave.open();
            
                bResult = ( pnlSave.getReturnCode() == Window.OK );                    
            } 
        }                        
        
        return bResult;
    }

    private Collection getResourcesToIgnore() {
        Collection colIgnore = null;
        
        // first get the dependent resources
        colIgnore = worker.getObjectDeleteCommand().getDependentResources();
        
        // then get the resources that own the selected objects.
        EObject[] eoArray = worker.getSelectedEObjects();
        ModelResource mrTemp = null;
        
        for( int i = 0; i < eoArray.length; i++ ) {
            mrTemp = ModelUtilities.getModelResourceForModelObject( eoArray[ i ] );
            
            // (also, do NOT allow duplicates)
            if ( !colIgnore.contains( mrTemp ) ) {
                colIgnore.add( mrTemp );
            }
        }
        
        return colIgnore;
    }
    
    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling
     * the run at the last minute.
     * This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        
        /*
         *      1. create the transactionsettings object
         *      2. call canDelete and canUndoDelete methods in the helper
         *          -- put result of these calls in the TranSettings object
         *      3. if canUndo == false,
         *          present warning dialog
         *          if user wants to continue,
         *              make sure we use the value of TxnSettings.canUndo in the
         *              canUndo arg of the startTxn method
         *      4.  if canUndo was true, return true
         *          if canUndo was false, return true if user wishes to continue
         *                                return false if user does NOT wish to continue 
         */
        
        if( ! resourceDependencyCheckOK() ) {
            return false;
        }
        
        // create the settings object
        TransactionSettings ts = getTransactionSettings();
        worker.setTransactionSettings( ts );
        worker.initTransactionSettings();
        
        
        // if not undoable, give user a chance to bail out
        if ( !ts.isUndoable() ) {
            String sTitle = UiConstants.Util.getString( CANNOT_UNDO_TITLE );
            String sMsg = UiConstants.Util.getString( CANNOT_UNDO_MSG );
            
            
            boolean bDoAnyway 
                = MessageDialog.openQuestion( getShell(), sTitle, sMsg );
            
            if ( !bDoAnyway ) {
                return false;
            }
        }
                
        // if we are to continue, pass the settings object to the worker
        
        if( requiresEditorForRun() ) {
            
            boolean okToContinue = false; 
            
            
            try {
                okToContinue = openEditorsForDependentModels();
            } catch (ModelWorkspaceException theException) {
                UiConstants.Util.log(IStatus.ERROR, theException.getMessage());
            }
            
            if( !okToContinue ) {
                return false;
            }
            // Need to cache the active part if we open/activate a model editor. Opening an editor
            // will grab focus. Need to return it to the original active part.
            IWorkbenchPart activePart = null;
            
            Object cachedSelection = worker.getSelection();
            if( worker.getFocusedObject() != null ) { 
                if( !ModelEditorManager.isOpen(worker.getFocusedObject()) ) {
                    activePart = UiPlugin.getDefault().getCurrentWorkbenchWindow().getPartService().getActivePart();
                    if( worker.getModelResource() != null ) {
                        ModelEditorManager.activate(worker.getModelResource(), true);
                    } else {
                        ModelEditorManager.open(worker.getFocusedObject(), true);
                    }
                }
            } else if( worker.getModelResource() != null ) {
                activePart = UiPlugin.getDefault().getCurrentWorkbenchWindow().getPartService().getActivePart();
                ModelEditorManager.activate(worker.getModelResource(), true);
            }

            // Reset the selection on the worker
            if( activePart != null ) {
                activePart.setFocus();
            }
            worker.selectionChanged(cachedSelection);
        }
        return true;
    }


    @Override
    protected void postRun() {

        worker.selectionChanged( getSelection() );
        worker.setTransactionSettings( null );
        super.postRun();
    }

    
    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }
    
    private boolean resourceDependencyCheckOK() {
        boolean isOkToDelete = true;
        EObject[] eoArray = worker.getSelectedEObjects();

        List readOnlyDependencies = new ArrayList(getReadOnlyDependentResources(eoArray));
        
        if( !readOnlyDependencies.isEmpty() ) {
            String sTitle = UiConstants.Util.getString( READ_ONLY_DEPENDENCIES_TITLE );
            String sMsg = UiConstants.Util.getString( READ_ONLY_DEPENDENCIES_MSG );
            List resourceList = new ArrayList(readOnlyDependencies.size());
            for(Iterator iter = readOnlyDependencies.iterator(); iter.hasNext(); ) {
                IPath shortPath = ((IResource)iter.next()).getFullPath().makeRelative();
                resourceList.add(shortPath);
            }
            ListMessageDialog.openWarning( getShell(), sTitle, null,  sMsg, resourceList, null );
            isOkToDelete = false;
        }
        
        return isOkToDelete;
    }
    
    
    /* (non-Javadoc)
     * Overridden to collect up only the models that actually reference the object to be deleted.
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#getDependentResources()
     */
    private Collection getReadOnlyDependentResources(EObject[] objectsToDelete) {

        Collection allDependentModelFiles = getAllDependentResources(objectsToDelete);
        Collection visitedResources = new HashSet();
        Collection readOnlyResources = new HashSet();
        for ( Iterator iter2 = allDependentModelFiles.iterator() ; iter2.hasNext() ; ) {
            IResource resource = (IResource) iter2.next();
            
            if ( !ModelUtilities.isVdbFile(resource) && ! visitedResources.contains(resource) ) {
                visitedResources.add(resource);
                if( resource.getResourceAttributes().isReadOnly() )
                    readOnlyResources.add(resource);
            }
        }

        
        return readOnlyResources;
    }
    /*
     * Filters modified resources to check for already existing in list, or in the list to be deleted.
     */
    private void appendDependentModelFiles(Collection affectedResources, Collection allAffectedResources, Collection targetedResources) {
        // Walk through affectedResources list and add to allAffectedResources only if it doesn't contain the model resourse
        Iterator iter = affectedResources.iterator();
        IResource iResource = null;
    
        while( iter.hasNext() ) {
            iResource = (IResource)iter.next();
            if( !ModelUtilities.isVdbFile(iResource) && !targetedResources.contains(iResource) && !allAffectedResources.contains(iResource) ) {
                allAffectedResources.add(iResource);
            }
        }
    }
    
    /* (non-Javadoc)
     * Overridden to collect up only the models that actually reference the object to be deleted.
     * @see com.metamatrix.modeler.core.refactor.ResourceRefactorCommand#getDependentResources()
     */
    private Collection getAllDependentResources(EObject[] objectsToDelete) {
        Collection resourceList = new HashSet();
        Collection allDepResources = new HashSet();
        ModelResource mr = null;
        
        try {
            for ( int i=0 ; i< objectsToDelete.length ; ++i ) {
                mr = ModelUtilities.getModelResourceForModelObject(objectsToDelete[i]);
                if( mr != null )
                    resourceList.add(mr.getCorrespondingResource());
            }
        } catch (ModelWorkspaceException err) {
            UiConstants.Util.log(IStatus.ERROR, err, UiConstants.Util.getString(FIND_RESOURCE_ERROR_MSG, mr) );
        }
        
        /* Temp dependant model list */            
        Collection depModelFiles = Collections.EMPTY_LIST;  
        /*Cached list of all dependent models for all contained resources */
        final Collection allDependantModelFiles = new ArrayList(); 
        
        Iterator iter = resourceList.iterator();
        // Loop through all contained/objects targeted for deletion
        while( iter.hasNext() ) {
            // Obtain all model file IResources for each model targeted for deletion
            depModelFiles = WorkspaceResourceFinderUtil.getResourcesThatUse((IResource)iter.next());
            // Append these to the big list using the private appendXXXX method below
            if( !depModelFiles.isEmpty() )
                appendDependentModelFiles(depModelFiles, allDependantModelFiles, resourceList);
        }
        
        
        for ( Iterator iter2 = allDependantModelFiles.iterator() ; iter2.hasNext() ; ) {
            IResource resource = (IResource) iter2.next();
            
            if (  ! allDepResources.contains(resource) ) {
                allDepResources.add(resource);
            }
        }

        
        return allDepResources;
    }
    
    private boolean openEditorsForDependentModels() throws ModelWorkspaceException {
        boolean okToDelete = true;
        
        EObject[] eoArray = worker.getSelectedEObjects();
        
        Collection allDepResources = getAllDependentResources(eoArray);
        
        if( !allDepResources.isEmpty()) {
            String sTitle = UiConstants.Util.getString( MODIFY_DEPENDENCIES_TITLE );
            String sMsg = UiConstants.Util.getString( MODIFY_DEPENDENCIES_MSG );
            List resourceList = new ArrayList(allDepResources.size());
            for(Iterator iter = allDepResources.iterator(); iter.hasNext(); ) {
                IPath shortPath = ((IResource)iter.next()).getFullPath().makeRelative();
                resourceList.add(shortPath);
            }
            
            okToDelete = ListMessageDialog.openWarningQuestion( getShell(), sTitle, null,  sMsg, resourceList, null );
            
            if( okToDelete ) {
                for (Iterator iter = allDepResources.iterator(); iter.hasNext(); ) {
                    IFile nextRes = (IFile)iter.next();
                    if( !ModelEditorManager.isOpen(nextRes) )
                        ModelEditorManager.activate(nextRes, true);
                }   
            }
        }
        
        if( okToDelete ) {
            // Now open editors for all eObject's resources
            ModelResource mr = null;
            
            // Create a unique list of ModelResources that will be losing objects
            Collection eObjectResources = new HashSet(eoArray.length);
            for ( int i=0 ; i< eoArray.length ; ++i ) {
                mr = ModelUtilities.getModelResourceForModelObject(eoArray[i]);
                eObjectResources.add(mr);

            }
            
            // Insure the affected resources are open in editors
            for( Iterator iter = eObjectResources.iterator(); iter.hasNext(); ) {
                mr = (ModelResource)iter.next();
                if( mr != null && ! ModelEditorManager.isOpen((IFile)mr.getCorrespondingResource()) ) {
                    ModelEditorManager.activate(mr, true, true);
                }
            }
        }
        
        return okToDelete;
    }
    
}

