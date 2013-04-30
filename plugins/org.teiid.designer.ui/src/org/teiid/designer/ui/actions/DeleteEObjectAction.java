/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISaveableFilter;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.Saveable;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.workers.DeleteWorker;
import org.teiid.designer.ui.common.util.UiUtil;
import org.teiid.designer.ui.common.widget.ListMessageDialog;
import org.teiid.designer.ui.editors.ModelEditorManager;
import org.teiid.designer.ui.refactor.RefactorCommandProcessorDialog;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * The <code>DeleteEObjectAction</code> class is the action that handles the global delete.
 * @since 8.0
 */
public class DeleteEObjectAction extends ModelObjectAction {

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
    
    public DeleteEObjectAction() {
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
        boolean bContinue = UiUtil.saveDirtyEditors(null, getResourcesFilter(), true);
 
        if ( bContinue ) {
            super.doRun();

            // if there are problems, use the common error dialog to report them
            if (   worker.getObjectDeleteCommand().getPostExecuteMessages() != null 
                && worker.getObjectDeleteCommand().getPostExecuteMessages().size() > 0 ) {

                RefactorCommandProcessorDialog rcpdDialog
                    = new RefactorCommandProcessorDialog( getShell(), worker.getObjectDeleteCommand() );
                rcpdDialog.open();                           
            }  
        } else {
            MessageDialog.openInformation(getShell(),
                                          UiConstants.Util.getString("DeleteAction.operationCanceledDialogTitle"), //$NON-NLS-1$
                                          UiConstants.Util.getString("DeleteAction.operationCanceledDialogMessage")); //$NON-NLS-1$
        }

//        worker.selectionChanged(getSelection());
        }
        
    private Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

    private ISaveableFilter getResourcesFilter() {
        // first get the dependent resources
        final Collection ignoredResources = this.worker.getObjectDeleteCommand().getDependentResources();

        // then get the resources that own the selected objects
        ModelResource mrTemp = null;

        for (EObject modelObject : this.worker.getSelectedEObjects()) {
            mrTemp = ModelUtilities.getModelResourceForModelObject(modelObject);

            // do not allow duplicates
            if (!ignoredResources.contains(mrTemp)) {
                ignoredResources.add(mrTemp);
            }
        }

        ISaveableFilter filter = new ISaveableFilter() {

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.ui.ISaveableFilter#select(org.eclipse.ui.Saveable, org.eclipse.ui.IWorkbenchPart[])
             */
            @Override
            public boolean select( Saveable saveable,
                                   IWorkbenchPart[] containingParts ) {
                // make sure first part is an editor
                if ((containingParts != null) && (containingParts.length != 0) && (containingParts[0] instanceof IEditorPart)) {
                    IEditorInput input = ((IEditorPart)containingParts[0]).getEditorInput();

                    if (input instanceof IFileEditorInput) {
                        ModelResource model = ModelUtilities.getModelResourceForIFile(((IFileEditorInput)input).getFile(),
                                                                                      false);

                        // don't select if an ignored resource
                        if (model != null) {
                            return !ignoredResources.contains(model);
                        }
                    }
                }

                // if no part or part is not an editor don't force save
                return false;
            }
        };

        return filter;
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
     * @see org.teiid.designer.ui.actions.ModelObjectAction#requiresEditorForRun()
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
     * @See org.teiid.designer.core.refactor.ResourceRefactorCommand#getDependentResources()
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
     * @See org.teiid.designer.core.refactor.ResourceRefactorCommand#getDependentResources()
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

