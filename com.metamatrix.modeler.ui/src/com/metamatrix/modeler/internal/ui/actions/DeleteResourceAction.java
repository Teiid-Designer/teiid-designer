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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.notification.util.DefaultIgnorableNotificationSource;
import com.metamatrix.modeler.core.refactor.ModelResourceCollectorVisitor;
import com.metamatrix.modeler.core.refactor.RefactorResourceEvent;
import com.metamatrix.modeler.core.refactor.RefactorResourceUtil;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.builder.ModelBuildUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;
import com.metamatrix.modeler.internal.ui.refactor.RefactorUndoManager;
import com.metamatrix.modeler.internal.ui.undo.ModelerUndoManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.actions.IActionConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

/**
 * The <code>DeleteResourceAction</code> is used to close models before deletion. If the model is open in an editor, the editor is
 * closed. It delegates to Eclipse's delete resource action for the actual delete. Also it deletes EObjects by delegating to
 * {@link com.metamatrix.modeler.internal.ui.actions.DeleteAction}.
 */
public class DeleteResourceAction extends AbstractAction implements UiConstants {

    /** Delegate action to delete resources. */
    private SelectionListenerAction deleteResourceAction;

    /** Delegate action to delete EObjects. */
    private AbstractAction deleteEObjectAction;

    /** The current delegate. */
    private IAction delegateAction;

    /**
     * Constructs a <code>DeleteResourceAction</code>.
     */
    public DeleteResourceAction() {
        super(UiPlugin.getDefault());
        deleteResourceAction = initDelegateAction();
        this.delegateAction = this.deleteResourceAction;

        setHoverImageDescriptor(deleteResourceAction.getHoverImageDescriptor());
        setImageDescriptor(deleteResourceAction.getImageDescriptor());
        setDisabledImageDescriptor(deleteResourceAction.getDisabledImageDescriptor());
        setText(deleteResourceAction.getText());
        setToolTipText(deleteResourceAction.getToolTipText());

        // get EObject delete action
        ActionService actionService = UiPlugin.getDefault().getActionService(UiUtil.getWorkbenchWindowOnlyIfUiThread().getActivePage());

        try {
            deleteEObjectAction = (AbstractAction)actionService.getAction(IActionConstants.EclipseGlobalActions.DELETE);
        } catch (CoreException theException) {
            Util.log(theException);
        }
    }

    protected SelectionListenerAction initDelegateAction() {
        return new org.eclipse.ui.actions.DeleteResourceAction(UiUtil.getWorkbenchWindowOnlyIfUiThread()) {
            @Override
            public void run() {
                // setting this to testing mode in essence brings back the functionality of 5.5.3. Allowing the execution to
                // use the DeleteResourceAction code that called LTKLauncher was causing problems with a Shell being disposed
                this.fTestingMode = true;
                super.run();
            }
        };
    }

    /**
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        List<IPath> deletedModelPaths = new ArrayList<IPath>();
        try {
            if (this.delegateAction == this.deleteResourceAction) {
                // this should never be called when there is one or more selected objects that are NOT an IResource.
                // the delegate action enablement should assure this.
                List resources = getSelectedObjects();

                /* Temp dependant model list */
                Collection depModelFiles = Collections.EMPTY_LIST;
                /*Cached list of all dependent models for all contained resources */
                final Collection allDependantModelFiles = new ArrayList();
                /* All models contained in select and under objects selected (i.e. projects and folders */
                List allSelectedAndContainedModelFiles = allSelectedAndContainedModelFiles(resources);

                for (Iterator iter2 = allSelectedAndContainedModelFiles.iterator(); iter2.hasNext();) {
                    deletedModelPaths.add(((IResource)iter2.next()).getFullPath());
                }

                Iterator iter = allSelectedAndContainedModelFiles.iterator();
                // Loop through all contained/objects targeted for deletion
                while (iter.hasNext()) {
                    // Obtain all model file IResources for each model targeted for deletion
                    depModelFiles = WorkspaceResourceFinderUtil.getResourcesThatUse((IResource)iter.next());
                    // Append these to the big list using the private appendXXXX method below
                    if (!depModelFiles.isEmpty()) appendDependentModelFiles(depModelFiles,
                                                                            allDependantModelFiles,
                                                                            allSelectedAndContainedModelFiles,
                                                                            resources);
                }

                if (isOkToCloseResources(resources)) {
                    boolean okToDelete = true;
                    // If we find dependent models, we need to warn the user
                    if (!allDependantModelFiles.isEmpty()) okToDelete = warnUserAboutDependants(allDependantModelFiles);

                    if (okToDelete) {
                        closeResources(resources);
                        deleteResourceAction.selectionChanged(new StructuredSelection(resources));
                        // If we make this call we can keep additional confirm dialogs from popping up
                        deleteResourceAction.run();

                        // We need to check whether or not the resources were deleted. Only way to do that
                        // is to see if they "exist()". User may have said "No" to the "Do you wish to Delete xxxxx" dialog.
                        if (resourcesRemoved(resources)) {

                            // make a call to validate the dependent models so the appropriate problem markers are generated and
                            // displayed to user.

                            final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
                                @Override
                                public void execute( IProgressMonitor theMonitor ) {
                                    validateDependentResources(allDependantModelFiles, theMonitor);
                                    theMonitor.done();
                                }
                            };
                            try {
                                new ProgressMonitorDialog(Display.getCurrent().getActiveShell()).run(true, true, op);
                            } catch (InterruptedException e) {
                            } catch (InvocationTargetException e) {
                                UiConstants.Util.log(e.getTargetException());
                            }
                        }
                    }
                }

            } else if (this.delegateAction == this.deleteEObjectAction) {
                this.delegateAction.run();
            }
        } finally {
            ModelerUndoManager.getInstance().clearAllEdits();
            RefactorUndoManager.getInstance().clear();

            for (IPath path : deletedModelPaths) {
                notifyDeleted(path);
            }
        }

    }

    /*
     * This method collects all model files either contained in the selected resources or
     * contained in a selected folder.
     */
    private List allSelectedAndContainedModelFiles( List selectedResources ) {
        List allSelectedAndContainedModelFiles = Collections.EMPTY_LIST;

        // Iterator over the selected resources
        for (int size = selectedResources.size(), i = 0; i < size; i++) {
            Object obj = selectedResources.get(i);
            if (obj instanceof IResource) {
                if (allSelectedAndContainedModelFiles.isEmpty()) {
                    allSelectedAndContainedModelFiles = new ArrayList();
                }

                IResource iRes = (IResource)obj;

                if (!(obj instanceof IFolder) && ModelUtilities.isModelFile(iRes)) {
                    // if the resource is not a folder and is a model file
                    // Add to the selected for deletion list
                    if (!allSelectedAndContainedModelFiles.contains(iRes)) {
                        allSelectedAndContainedModelFiles.add(iRes);
                    }
                } else if (obj instanceof IFolder || obj instanceof IProject) {
                    // If the resource is a folder (and maybe a project) get all models
                    // contained under that folder
                    Collection folderModels = getContainedModelFiles(obj);
                    Iterator iter = folderModels.iterator();
                    IResource nextRes = null;

                    // Iterator over the contained models and check for duplicates.
                    while (iter.hasNext()) {
                        nextRes = (IResource)iter.next();
                        if (!allSelectedAndContainedModelFiles.contains(nextRes)) {
                            allSelectedAndContainedModelFiles.add(nextRes);
                        }
                    }
                }
            }
        }
        return allSelectedAndContainedModelFiles;
    }

    /*
     * This method calls the appropriate validate method to create the "Missing" import problem markers (and others) for the
     * resources just deleted.
     */
    void validateDependentResources( Collection modelIResources,
                                     IProgressMonitor theMontitor ) {
        if (!modelIResources.isEmpty()) {
            // In order for the notifications caused by "opening models" for validation, to be swallowed, the validation
            // call needs to be wrapped in a transaction. This was discovered and relayed by Goutam on 2/14/05.
            boolean started = ModelerCore.startTxn(false, false, "Validate Dependent Resources", //$NON-NLS-1$
                                                   new DefaultIgnorableNotificationSource(DeleteResourceAction.this));
            boolean succeeded = false;
            try {
                ModelBuildUtil.validateResources(theMontitor, modelIResources, doGetContainer(), false);

                succeeded = true;

            } catch (final Exception err) {
                final String msg = Util.getString("DeleteResourceAction.validateDependentResources"); //$NON-NLS-1$
                getPluginUtils().log(IStatus.ERROR, err, msg);
            } finally {
                if (started) {
                    if (succeeded) {
                        ModelerCore.commitTxn();
                    } else {
                        ModelerCore.rollbackTxn();
                    }
                }
            }

        }
    }

    /*
     * Private method used to assess whether or not the user actually deleted the resources or said "NO". This action 
     * doesn't know about the "Do you want to delete" dialog, so we needed an alternate way to check.
     */
    private boolean resourcesRemoved( List iResources ) {
        if (!iResources.isEmpty()) {
            IResource firstResource = (IResource)iResources.iterator().next();
            if (!firstResource.exists()) {
                return true;
            }
        }
        return false;
    }

    /*
     * Filters modified resources to check for already existing in list, or in the list to be deleted.
     */
    private void appendDependentModelFiles( Collection affectedResources,
                                            Collection allAffectedResources,
                                            Collection targetedResources,
                                            Collection selectedObjects ) {
        // Walk through affectedResources list and add to allAffectedResources only if it doesn't contain the model resourse
        Iterator iter = affectedResources.iterator();
        IResource mr = null;

        while (iter.hasNext()) {
            mr = (IResource)iter.next();
            if (!targetedResources.contains(mr) && !allAffectedResources.contains(mr)
                && !isUnderSelectedObjects(mr, selectedObjects)) {
                allAffectedResources.add(mr);
            }
        }
    }

    private boolean warnUserAboutDependants( Collection dependentResources ) {
        String title = Util.getString("DeleteResourceAction.confirmDependenciesTitle"); //$NON-NLS-1$
        String msg = Util.getString("DeleteResourceAction.confirmDependenciesMessage"); //$NON-NLS-1$
        List resourceList = new ArrayList(dependentResources.size());
        for (Iterator iter = dependentResources.iterator(); iter.hasNext();) {
            IPath shortPath = ((IResource)iter.next()).getFullPath().makeRelative();
            resourceList.add(shortPath);
        }
        return ListMessageDialog.openWarningQuestion(getShell(), title, null, msg, resourceList, null);
    }

    private boolean isUnderSelectedObjects( IResource dependentResource,
                                            Collection selectedObjects ) {
        Iterator iter = selectedObjects.iterator();
        while (iter.hasNext()) {
            if (dependentResource.getProject().equals(iter.next())) {
                return true;
            }
        }
        return false;
    }

    protected Container doGetContainer() {
        try {
            return ModelerCore.getModelContainer();
        } catch (CoreException err) {
            String message = Util.getString("DeleteResourceAction.doGetContainerProblemMessage"); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, err, message);
        }
        return null;
    }

    /*
     * Private method used to obtain all Model file IResources contained in an IFolder
     * or IProject IResource.  This is required because if these containers are being deleted, we need to gather
     * up all objects that will go along with it and check for dependencies so we can validate all affected
     * resources.
     */
    private Collection getContainedModelFiles( Object folderOrProject ) {
        Collection containedModelFiles = Collections.EMPTY_LIST;

        ModelResourceCollectorVisitor visitor = new ModelResourceCollectorVisitor();
        if (folderOrProject instanceof IFolder) {

            // get the folder's project
            IProject project = ((IFolder)folderOrProject).getProject();
            if (project != null) {
                // Insure that the project is open and is a Modeler Project
                if (project.isOpen() && ModelerCore.hasModelNature(project)) {
                    try {
                        ((IFolder)folderOrProject).accept(visitor);
                    } catch (CoreException e) {
                        UiConstants.Util.log(e);
                    }
                }

                // Get the resources and weed out non-model files
                List pResources = visitor.getResources();
                containedModelFiles = new ArrayList(pResources.size());
                Iterator iter = pResources.iterator();
                IResource nextRes = null;
                while (iter.hasNext()) {
                    nextRes = (IResource)iter.next();
                    if (ModelUtilities.isModelFile(nextRes)) containedModelFiles.add(nextRes);
                }
            }
        } else if (folderOrProject instanceof IProject) {
            // get the folder's project
            IProject project = (IProject)folderOrProject;
            // Insure that the project is open and is a Modeler Project
            if (project.isOpen() && ModelerCore.hasModelNature(project)) {
                try {
                    project.accept(visitor);
                } catch (CoreException e) {
                    UiConstants.Util.log(e);
                }
            }

            // Get the resources and weed out non-model files
            List pResources = visitor.getResources();
            containedModelFiles = new ArrayList(pResources.size());
            Iterator iter = pResources.iterator();
            IResource nextRes = null;
            while (iter.hasNext()) {
                nextRes = (IResource)iter.next();
                if (ModelUtilities.isModelFile(nextRes)) containedModelFiles.add(nextRes);
            }
        }

        // return on project model files.
        return containedModelFiles;
    }

    /**
     * Closes all models under the specified <code>IResource</code>. If a model is open in an editor, the editor is closed.
     * 
     * @param theResource the <code>IResource</code> whose models are being closed
     * @return <code>true</code> if all models closed successfully; <code>false</code> otherwise.
     */
    private boolean closeResource( IResource theResource ) {
        boolean result = true;

        if (ModelerCore.hasModelNature(theResource.getProject())) {
            if (theResource instanceof IProject) {
                result = closeProject((IProject)theResource);
            } else if (theResource instanceof IFolder) {
                result = closeFolder((IFolder)theResource);
            } else if (theResource instanceof IFile) {
                result = closeFile((IFile)theResource);
            }
        }

        return result;
    }

    /**
     * If the specified <code>IFile</code> is a model resource it is closed. If the model is open in an editor, the editor is
     * closed.
     * 
     * @param theFile the <code>IFile</code> being closed
     * @return <code>true</code> if the model closed successfully or if not a model file; <code>false</code> otherwise.
     */
    private boolean closeFile( IFile theFile ) {
        boolean result = true;

        ModelResource model = null;
        if (ModelUtilities.isModelFile(theFile)) {
            try {
                model = ModelUtilities.getModelResource(theFile, false);

                if (model != null) {
                    if (model.isLoaded()) {
                        // see if the model is open, in an initialized ModelEditor
                        if (ModelEditorManager.isOpen(theFile)) {
                            if (!ModelEditorManager.isOpenAndInitialized(theFile)) {
                                // System.out.println("[DeleteResourceAction.closeFile] model is open; about to close: " +
                                // theFile.getName() );
                                ModelEditorManager.close(theFile, false);
                            }
                        } else {
                            // jh Defect 19139:
                            // if the model is not open it might be in an Editor Reference. If so, clean that up.
                            IEditorReference editorRef = ModelEditorManager.getEditorReferenceForFile(theFile);
                            if (editorRef != null) {
                                // System.out.println("[DeleteResourceAction.closeFile] Found an EditorReference; about to remove: "
                                // + editorRef.getName() );
                                ModelEditorManager.removeEditorReference(editorRef);
                            }
                        }
                        // }
                    } else {
                        // jh Defect 19139:
                        // if the model is not loaded it might be in an Editor Reference. If so, clean that up.
                        IEditorReference editorRef = ModelEditorManager.getEditorReferenceForFile(theFile);
                        if (editorRef != null) {
                            // System.out.println("[DeleteResourceAction.closeFile] Found an EditorReference; about to remove: " +
                            // editorRef.getName() );
                            ModelEditorManager.removeEditorReference(editorRef);
                        }
                    }
                }

            } catch (ModelWorkspaceException theException) {
                Util.log(theException);
                result = false;
            }

            // don't close model if editor wasn't closed. user aborted close.
            if (result) {
                try {
                    if (model != null) {
                        // Need to close the model in the Explorer?
                        // System.out.println("[DeleteResourceAction.closeFile] result is true; about to call closeModel(model): "
                        // + model.getItemName() );
                        closeModel(model);
                    }
                } catch (ModelWorkspaceException theException) {
                    Util.log(theException);
                    result = false;
                }
            }
        } else if (ModelUtil.isVdbArchiveFile(theFile)) {
            // IResource vbdResource = (IResource)theFile;

            IEditorPart editor = UiUtil.getEditorForFile(theFile, false);
            if (editor != null) {
                if (editor.isDirty()) {
                    String title = UiConstants.Util.getString("DeleteResourceAction.pendingChangesTitle"); //$NON-NLS-1$
                    String message = UiConstants.Util.getString("DeleteResourceAction.pendingChangesMessage", theFile.getName()); //$NON-NLS-1$
                    result = MessageDialog.openQuestion(getShell(), title, message);
                }
                if (result) {
                    UiUtil.close(theFile, false);
                }
            }

        }

        return result;
    }

    private void closeResources( List resources ) {

        boolean userOK = true;
        final List modifiedResources = ModelBuildUtil.getModifiedResources();

        boolean started = ModelerCore.startTxn(false, false, "Closing Resources for Delete", //$NON-NLS-1$
                                               new DefaultIgnorableNotificationSource(DeleteResourceAction.this));
        boolean succeeded = false;
        try {
            for (int size = resources.size(), i = 0; i < size && userOK; i++) {
                Object obj = resources.get(i);

                if (obj instanceof IResource) {
                    userOK = closeResource((IResource)obj);
                }
            }
            succeeded = true;

        } catch (final Exception err) {
            final String msg = Util.getString("DeleteResourceAction.closeResources"); //$NON-NLS-1$
            getPluginUtils().log(IStatus.ERROR, err, msg);
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        ModelBuildUtil.setModifiedResources(modifiedResources);
    }

    private boolean isOkToCloseResources( List resources ) {
        boolean userOK = true;

        // We need to wrap this in a transaction
        boolean started = ModelerCore.startTxn(false, false, "Confirming Close Resources", //$NON-NLS-1$
                                               new DefaultIgnorableNotificationSource(DeleteResourceAction.this));
        boolean succeeded = false;
        try {
            for (int size = resources.size(), i = 0; i < size && userOK; i++) {
                Object obj = resources.get(i);

                if (obj instanceof IResource) {
                    userOK = isOkToCloseResource((IResource)obj);
                }
            }
            succeeded = true;
        } catch (final Exception err) {
            final String msg = UiConstants.Util.getString("DeleteResourceAction.confirmingCloseResources"); //$NON-NLS-1$
            UiConstants.Util.log(IStatus.ERROR, err, msg);
        } finally {
            if (started) {
                if (succeeded) {
                    ModelerCore.commitTxn();
                } else {
                    ModelerCore.rollbackTxn();
                }
            }
        }

        return userOK;
    }

    private boolean isOkToCloseResource( IResource theResource ) {
        boolean result = true;

        if (ModelerCore.hasModelNature(theResource.getProject())) {
            if (theResource instanceof IProject) {
                result = isOkToCloseProject((IProject)theResource);
            } else if (theResource instanceof IFolder) {
                result = isOkToCloseFolder((IFolder)theResource);
            } else if (theResource instanceof IFile) {
                result = isOkToCloseFile((IFile)theResource);
            }
        }

        return result;
    }

    private boolean isOkToCloseProject( IProject theProject ) {
        boolean result = true;

        try {
            IResource[] kids = theProject.members();

            if (kids.length > 0) {
                for (int i = 0; i < kids.length; i++) {
                    if (!isOkToCloseResource(kids[i])) {
                        result = false;
                    }
                }
            }
        } catch (CoreException theException) {
            Util.log(theException);
            result = false;
        }

        return result;
    }

    private boolean isOkToCloseFolder( IFolder theFolder ) {
        boolean result = true;

        try {
            IResource[] kids = theFolder.members();

            if (kids.length > 0) {
                for (int i = 0; i < kids.length; i++) {
                    if (!isOkToCloseResource(kids[i])) {
                        result = false;
                    }
                }
            }
        } catch (CoreException theException) {
            Util.log(theException);
            result = false;
        }

        return result;
    }

    private boolean isOkToCloseFile( IFile theFile ) {
        boolean result = true;

        ModelResource model = null;
        if (ModelUtilities.isModelFile(theFile)) {
            try {
                model = ModelUtilities.getModelResource(theFile, false);
                if (model != null && model.isLoaded() && model.getEmfResource().isModified()) {
                    // first, see if the model has pending changes that need to be saved.
                    String title = UiConstants.Util.getString("DeleteResourceAction.pendingChangesTitle"); //$NON-NLS-1$
                    String message = UiConstants.Util.getString("DeleteResourceAction.pendingChangesMessage", theFile.getName()); //$NON-NLS-1$
                    result = MessageDialog.openQuestion(getShell(), title, message);
                }

            } catch (ModelWorkspaceException theException) {
                Util.log(theException);
                result = false;
            }
        } else if (ModelUtil.isVdbArchiveFile(theFile)) {

            IEditorPart editor = UiUtil.getEditorForFile(theFile, false);
            if (editor != null) {
                if (editor.isDirty()) {
                    String title = UiConstants.Util.getString("DeleteResourceAction.pendingChangesTitle"); //$NON-NLS-1$
                    String message = UiConstants.Util.getString("DeleteResourceAction.pendingChangesMessage", theFile.getName()); //$NON-NLS-1$
                    result = MessageDialog.openQuestion(getShell(), title, message);
                }
            }

        }

        return result;
    }

    private void closeModel( ModelResource modelResource ) throws ModelWorkspaceException {
        ModelResourceEvent event = new ModelResourceEvent(modelResource, ModelResourceEvent.CLOSING, this);
        UiPlugin.getDefault().getEventBroker().processEvent(event);
        if (modelResource.isOpen() && modelResource.isLoaded()) {
            modelResource.getEmfResource().setModified(false);
            modelResource.close();
            event = new ModelResourceEvent(modelResource, ModelResourceEvent.CLOSED, this);
            UiPlugin.getDefault().getEventBroker().processEvent(event);
        }
    }

    /**
     * Closes all models under the specified <code>IFolder</code>. If a model is open in an editor, the editor is closed.
     * 
     * @param theFolder the <code>IFolder</code> whose models are being closed
     * @return <code>true</code> if all models closed successfully; <code>false</code> otherwise.
     */
    private boolean closeFolder( IFolder theFolder ) {
        boolean result = true;

        try {
            IResource[] kids = theFolder.members();

            if (kids.length > 0) {
                for (int i = 0; i < kids.length; i++) {
                    if (!closeResource(kids[i])) {
                        result = false;
                    }
                }
            }
        } catch (CoreException theException) {
            Util.log(theException);
            result = false;
        }

        return result;
    }

    /**
     * Closes all models under the specified <code>IProject</code>. If a model is open in an editor, the editor is closed.
     * 
     * @param theProject the <code>IProject</code> whose models are being closed
     * @return <code>true</code> if all models closed successfully; <code>false</code> otherwise.
     */
    private boolean closeProject( IProject theProject ) {
        boolean result = true;

        try {
            IResource[] kids = theProject.members();

            if (kids.length > 0) {
                for (int i = 0; i < kids.length; i++) {
                    if (!closeResource(kids[i])) {
                        result = false;
                    }
                }
            }
        } catch (CoreException theException) {
            Util.log(theException);
            result = false;
        }

        return result;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractAction#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged( IWorkbenchPart thePart,
                                  ISelection theSelection ) {
        super.selectionChanged(thePart, theSelection);

        // make the delegate actions aware of the new selection and set enablement
        IStructuredSelection selection = null;

        if (theSelection instanceof IStructuredSelection) {
            selection = (IStructuredSelection)theSelection;
        } else {
            selection = StructuredSelection.EMPTY;
        }

        this.deleteResourceAction.selectionChanged(selection);

        if (this.deleteEObjectAction != null) {
            this.deleteEObjectAction.selectionChanged(thePart, selection);
        }

        // set enablement
        boolean enable = false;

        if (this.deleteResourceAction.isEnabled()) {
            this.delegateAction = this.deleteResourceAction;
            enable = true;
        } else if ((this.deleteResourceAction != null) && this.deleteEObjectAction.isEnabled()) {
            this.delegateAction = this.deleteEObjectAction;
            enable = true;
        }
        // Now make sure we aren't deleting a .project, FunctionDefinitions.xmi, or Configuration project
        if (enable) {
            enable = isValidSelection();
        }
        setEnabled(enable);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractAction#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    @Override
    public void selectionChanged( SelectionChangedEvent theEvent ) {
        super.selectionChanged(theEvent);

        // make the delegate actions aware of the new selection and set enablement
        this.deleteResourceAction.selectionChanged(theEvent);

        if (this.deleteResourceAction != null) {
            this.deleteEObjectAction.selectionChanged(theEvent);
        }

        // set enablement
        boolean enable = false;

        if (deleteResourceAction.isEnabled()) {
            this.delegateAction = this.deleteResourceAction;
            enable = true;
        } else if ((this.deleteResourceAction != null) && this.deleteEObjectAction.isEnabled()) {
            this.delegateAction = this.deleteEObjectAction;
            enable = true;
        }

        // Now make sure we aren't deleting a .project, FunctionDefinitions.xmi, or Configuration project
        if (enable) {
            enable = isValidSelection();
        }
        setEnabled(enable);
    }

    protected Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

    private boolean isValidSelection() {
        // Check for mixed Selection
        // Can't delete EObjects & IResources together
        if (SelectionUtilities.isMixedObjectTypes(getSelection())) {
            return false;
        }

        boolean isValid = true;
        // 1) Can't delete a .project file
        // 2) Can't delete the "Configuration" project
        // 3) Can't delete the "FunctionDefinitions" model
        List selectedObjects = getSelectedObjects();
        for (Iterator iter = selectedObjects.iterator(); iter.hasNext();) {
            Object nextObj = iter.next();
            if (nextObj instanceof IProject) {
                String projName = ((IProject)nextObj).getName();
                if (ModelerCore.isReservedProjectName(projName)) {
                    return false;
                }
            } else if (nextObj instanceof IFile) {
                IFile file = (IFile)nextObj;
                if (file.getFileExtension().equalsIgnoreCase(ModelerCore.DOT_PROJECT_EXTENSION)) return false;
                else if (file.getName().equalsIgnoreCase(ModelerCore.UDF_MODEL_NAME)) return false;
            }
        }

        return isValid;
    }

    /*
     * Notify with RefactorRenameEvent.TYPE_DELETE
     */
    private void notifyDeleted( IPath deletedResourcePath ) {
        RefactorResourceUtil.notifyRefactored(new RefactorResourceEvent(null, RefactorResourceEvent.TYPE_DELETE, this,
                                                                        deletedResourcePath));
    }
}
