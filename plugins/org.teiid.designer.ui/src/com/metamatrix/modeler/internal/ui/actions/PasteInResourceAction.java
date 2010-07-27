/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.CopyProjectOperation;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eclipse.ui.part.ResourceTransfer;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.SystemClipboardUtilities;
import com.metamatrix.ui.internal.util.UiUtil;

/**
 * PasteInResourceAction
 */
public class PasteInResourceAction extends ModelObjectAction implements UiConstants {

    /** Delegate for EObject and model paste. */
    private AbstractAction eObjDelegateAction;

    /** Delegate for resource paste. */
    private ResourcePasteAction resourceDelegateAction;

    /**
     * Constructs a <code>DeleteResourceAction</code>.
     */
    public PasteInResourceAction() {
        super(UiPlugin.getDefault());

        resourceDelegateAction = new ResourcePasteAction();

        // get EObject paste action
        ActionService actionService = UiPlugin.getDefault().getActionService(UiUtil.getWorkbenchWindowOnlyIfUiThread().getActivePage());

        try {
            eObjDelegateAction = (AbstractAction)actionService.getAction(PasteAction.class.getName());

            setHoverImageDescriptor(eObjDelegateAction.getHoverImageDescriptor());
            setImageDescriptor(eObjDelegateAction.getImageDescriptor());
            setDisabledImageDescriptor(eObjDelegateAction.getDisabledImageDescriptor());
            setText(eObjDelegateAction.getText());
            setToolTipText(eObjDelegateAction.getToolTipText());
        } catch (CoreException theException) {
            Util.log(theException);

            // create an always disabled action
            eObjDelegateAction = new AbstractAction(UiPlugin.getDefault()) {
                @Override
                protected void doRun() {
                }
            };
            eObjDelegateAction.setEnabled(false);
        }
    }

    /**
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        if (this.eObjDelegateAction.isEnabled()) {
            this.eObjDelegateAction.run();
        } else if (this.resourceDelegateAction.isEnabled()) {
            this.resourceDelegateAction.run();
        }

        setEnabledState();
    }

    Shell getShell() {
        return getPart().getSite().getShell();
    }

    /**
     * Check if resource already exists in workspace
     * 
     * @param theResource
     * @return
     * @since 5.0
     */
    private boolean resourceAlreadyInWorkspace( IResource theResource ) {
        boolean result = false;

        try {
            result = ModelWorkspaceManager.getModelWorkspaceManager().findModelWorkspaceItem(theResource) != null;
        } catch (ModelWorkspaceException theException) {
            Util.log(theException);
        }

        return result;
    }

    private boolean isValidPasteResource( IResource theResource ) {
        boolean result = true;

        if (theResource.getProject().isOpen()) {

            if (theResource instanceof IFile) {
                if (!ModelUtilities.isModelFile(theResource) && !ModelUtilities.isVdbFile(theResource)) result = true;
                else if (ModelUtilities.isVdbFile(theResource)) {
                    // Allow if in model project file container
                    result = selectedIsInModelProject();
                }
            } else if (theResource instanceof IContainer) {
                // make sure no models contained
                try {
                    IResource[] members = ((IContainer)theResource).members();

                    if (members.length > 0) {
                        for (int i = 0; i < members.length; i++) {
                            result = isValidPasteResource(members[i]);

                            if (!result) {
                                break;
                            }
                        }
                    }
                } catch (CoreException theException) {
                    result = false;
                }
            } else {
                result = false;
            }
        } else {
            // can't paste into a closed project
            result = false;
        }

        return result;
    }

    /**
     * This method is called in the run() method of AbstractAction to give the actions a hook into canceling the run at the last
     * minute. This overrides the AbstractAction preRun() method.
     */
    @Override
    protected boolean preRun() {
        boolean result = true;

        if (this.eObjDelegateAction.isEnabled()) {
            // result = super.preRun();
            result = activateEditor();
        }

        return result;
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.actions.ModelObjectAction#requiresEditorForRun()
     */
    @Override
    protected boolean requiresEditorForRun() {
        return true;
    }

    /**
     * @see com.metamatrix.ui.actions.AbstractAction#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
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

        // pass selection to delegates. don't need to for the EObject actions since it was retrieved
        // from the action service which automatically hooks up the action to receive selection events

        // this.eObjDelegateAction.selectionChanged(thePart, selection);
        this.resourceDelegateAction.selectionChanged(selection);

        setEnabledState();
    }

    /**
     * @see com.metamatrix.ui.actions.AbstractAction#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    @Override
    public void selectionChanged( SelectionChangedEvent theEvent ) {
        super.selectionChanged(theEvent);

        // pass selection to delegates
        this.eObjDelegateAction.selectionChanged(theEvent);
        this.resourceDelegateAction.selectionChanged(theEvent);

        setEnabledState();
    }

    private boolean selectedIsInModelProject() {
        boolean result = false;

        IResource targetResource = this.resourceDelegateAction.getContainer();

        // targetResource is null if no valid target is selected or
        // selection is empty
        if (targetResource != null) result = ModelUtilities.isModelProjectResource(targetResource);

        return result;
    }

    private void setEnabledState() {
        boolean result = this.eObjDelegateAction.isEnabled();

        if (!result && this.resourceDelegateAction.isEnabled()) {
            // make sure no models are being copied
            IResource[] resources = (IResource[])SystemClipboardUtilities.getContents(ResourceTransfer.getInstance());

            if ((resources != null) && (resources.length > 0)) {
                result = true;

                for (int i = 0; i < resources.length; i++) {
                    if (resourceAlreadyInWorkspace(resources[i]) || !isValidPasteResource(resources[i])) {
                        result = false;
                        break;
                    }
                }
            }
        }

        setEnabled(result);
    }

    private boolean activateEditor() {
        if (requiresEditorForRun()) {
            List allSelectedEObjects = SelectionUtilities.getSelectedEObjects(getSelection());
            if (allSelectedEObjects != null && !allSelectedEObjects.isEmpty()) {
                EObject eObject = (EObject)allSelectedEObjects.get(0);
                ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObject);
                if (mr != null) {
                    ModelEditorManager.activate(mr, true);
                }
            } else {
                // Check selection for resource
                if (SelectionUtilities.isSingleSelection(getSelection())) {
                    Object selectedObject = SelectionUtilities.getSelectedObject(getSelection());
                    if (selectedObject instanceof IFile && ModelUtilities.isModelFile((IFile)selectedObject)) {
                        ModelResource mr = null;

                        try {
                            mr = ModelUtil.getModelResource((IFile)selectedObject, false);
                        } catch (ModelWorkspaceException err) {
                            Util.log(err);
                        } finally {
                            if (mr != null) {
                                ModelEditorManager.activate(mr, true);
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Handles resource paste. Majority of code copied from org.eclipse.ui.views.navigator.PasteAction.
     */
    private class ResourcePasteAction extends SelectionListenerAction {
        public ResourcePasteAction() {
            // text is not used since action is never installed in any menu
            super("not used"); //$NON-NLS-1$
        }

        IContainer getContainer() {
            List selection = getSelectedResources();

            // defect 16350 - prevent ArrayIndexOutOfBoundsExceptions
            if (selection == null || selection.isEmpty()) {
                return null;
            } // endif

            if (selection.get(0) instanceof IFile) {
                return ((IFile)selection.get(0)).getParent();
            }
            return (IContainer)selection.get(0);
        }

        private IResource getTarget() {
            List selectedResources = getSelectedResources();

            for (int i = 0; i < selectedResources.size(); i++) {
                IResource resource = (IResource)selectedResources.get(i);

                if (resource instanceof IProject && !((IProject)resource).isOpen()) return null;
                if (resource.getType() == IResource.FILE) resource = resource.getParent();
                if (resource != null) return resource;
            }
            return null;
        }

        private boolean isLinked( IResource[] resources ) {
            if (resources != null) {
                for (int i = 0; i < resources.length; i++) {
                    if (resources[i].isLinked()) {
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * @see org.eclipse.jface.action.Action#run()
         */
        @Override
        public void run() {
            // try a resource transfer
            ResourceTransfer resTransfer = ResourceTransfer.getInstance();
            IResource[] resourceData = (IResource[])SystemClipboardUtilities.getContents(resTransfer);

            if (resourceData != null && resourceData.length > 0) {
                if (resourceData[0].getType() == IResource.PROJECT) {
                    // enablement checks for all projects
                    for (int i = 0; i < resourceData.length; i++) {
                        CopyProjectOperation operation = new CopyProjectOperation(getShell());
                        operation.copyProject((IProject)resourceData[i]);
                    }
                } else {
                    // enablement should ensure that we always have access to a container
                    IContainer container = getContainer();

                    CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(getShell());
                    operation.copyResources(resourceData, container);
                }
                return;
            }

            // try a file transfer
            FileTransfer fileTransfer = FileTransfer.getInstance();
            String[] fileData = (String[])SystemClipboardUtilities.getContents(fileTransfer);

            if (fileData != null) {
                // enablement should ensure that we always have access to a container
                IContainer container = getContainer();

                CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(getShell());
                operation.copyFiles(fileData, container);
            }
        }

        /**
         * @see org.eclipse.ui.actions.SelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
         */
        @Override
        protected boolean updateSelection( IStructuredSelection selection ) {
            if (!super.updateSelection(selection)) return false;

            try {
                // clipboard must have resources or files
                ResourceTransfer resTransfer = ResourceTransfer.getInstance();
                IResource[] resourceData = (IResource[])SystemClipboardUtilities.getContents(resTransfer);
                FileTransfer fileTransfer = FileTransfer.getInstance();
                String[] fileData = (String[])SystemClipboardUtilities.getContents(fileTransfer);
                if (resourceData == null && fileData == null) return false;

                // can paste open projects regardless of selection
                boolean isProjectRes = resourceData != null && resourceData.length > 0
                                       && resourceData[0].getType() == IResource.PROJECT;
                if (isProjectRes) {
                    for (int i = 1; i < resourceData.length; i++) {
                        // make sure all resource data are projects
                        if (resourceData[i].getType() != IResource.PROJECT) return false;
                    }
                    return true;
                }

                // can paste files and folders to a single selection (project must be open)
                // or multiple file selection with the same parent
                if (getSelectedNonResources().size() > 0) return false;
                List selectedResources = getSelectedResources();
                IResource targetResource = getTarget();

                // targetResource is null if no valid target is selected or
                // selection is empty
                if (targetResource == null) return false;

                // linked resources can only be pasted into projects
                if (isLinked(resourceData) && targetResource.getType() != IResource.PROJECT) return false;

                if (selectedResources.size() > 1) {
                    // if more than one resource is selected the selection has
                    // to be all files with the same parent
                    for (int i = 0; i < selectedResources.size(); i++) {
                        IResource resource = (IResource)selectedResources.get(i);
                        if (resource.getType() != IResource.FILE) return false;
                        if (!targetResource.equals(resource.getParent())) return false;
                    }
                }

                if (targetResource.getType() == IResource.FOLDER && resourceData != null) {
                    // don't try to copy folder to self
                    for (int i = 0; i < resourceData.length; i++) {
                        if (targetResource.equals(resourceData[i])) return false;
                    }
                }

                return true;
            } catch (Exception e) {
                Util.log(e);
                return false;
            }
        }
    }
}
