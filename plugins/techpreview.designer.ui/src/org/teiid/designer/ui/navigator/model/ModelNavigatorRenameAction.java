/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.renameNotSupportedMessage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.RenameResourceAction;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.actions.TreeViewerRenameAction;
import com.metamatrix.modeler.internal.ui.refactor.actions.RenameRefactorAction;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.DelegatableAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * ModelNavigatorRenameAction is a specialization of ResourceNavigatorRenameAction that also handles in-line renaming of EObjects.
 * In addition, it prevents renaming of ModelResources that are open in a ModelEditor.
 */
public class ModelNavigatorRenameAction extends RenameResourceAction implements ISelectionProvider, UiConstants {

    private final IAction actRefactorRename;
    private final IActionDelegate delRefactorRename;
    private final TreeViewerRenameAction inlineRenameAction;
    private final TreeViewer viewer;

    /**
     * Construct an instance of ModelExplorerRenameAction.
     * 
     * @param shell
     * @param viewer
     */
    public ModelNavigatorRenameAction( IWorkbenchWindow window,
                                       TreeViewer viewer ) {
        super(window);
        this.viewer = viewer;

        // create the TreeViewerRenameAction for inline editing of EObject names
        this.inlineRenameAction = new TreeViewerRenameAction();
        this.inlineRenameAction.setTreeViewer(this.viewer, (ILabelProvider)viewer.getLabelProvider());

        this.delRefactorRename = new RenameRefactorAction();
        this.actRefactorRename = new DelegatableAction(this.delRefactorRename, window);
        this.actRefactorRename.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    @Override
    public void addSelectionChangedListener( ISelectionChangedListener listener ) {
        throw new RuntimeException("ModelExplorerRenameAction.addSelectionChangedListener is not supported"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    @Override
    public ISelection getSelection() {
        return getStructuredSelection();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    @Override
    public void removeSelectionChangedListener( ISelectionChangedListener listener ) {
        throw new RuntimeException("ModelExplorerRenameAction.removeSelectionChangedListener is not supported"); //$NON-NLS-1$
    }

    private void renameModelResource( IResource theResource ) {
        this.delRefactorRename.selectionChanged(this.actRefactorRename, getSelection());

        if ((this.actRefactorRename != null) && this.actRefactorRename.isEnabled()) {
            this.actRefactorRename.run();
        }

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.RenameResourceAction#run()
     */
    @Override
    public void run() {
        Object selection = getStructuredSelection().getFirstElement();

        if (selection instanceof EObject) {
            // set the selection on the TreeViewerRenameAction and run it
            this.inlineRenameAction.selectionChanged(new SelectionChangedEvent(this, getStructuredSelection()));
            this.inlineRenameAction.run();

        } else if ((selection instanceof IResource) && ModelerCore.hasModelNature(((IResource)selection).getProject())) {
            if (selection instanceof IFile) {
                final IFile file = (IFile)selection;

                if (ModelUtilities.isModelFile(file)) {
                    renameModelResource(file);
                } else if (file.getFileExtension() != null
                        && ModelerCore.VDB_FILE_EXTENSION.endsWith(file.getFileExtension().toLowerCase())) {
                    WidgetUtil.showError(renameNotSupportedMessage);
                } else {
                    super.run();
                }
            } else if (selection instanceof IProject) {
                renameModelResource((IResource)selection);
            } else if (selection instanceof IFolder) {
                renameModelResource((IResource)selection);
            } else {
                super.run();
            }
        } else {
            super.run();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.RenameResourceAction#runWithNewPath(org.eclipse.core.runtime.IPath,
     *      org.eclipse.core.resources.IResource)
     */
    @Override
    protected void runWithNewPath( IPath path,
                                   IResource resource ) {
        IWorkspaceRoot root = resource.getProject().getWorkspace().getRoot();
        super.runWithNewPath(path, resource);

        if (this.viewer != null) {
            IResource newResource = root.findMember(path);

            if (newResource != null) {
                this.viewer.setSelection(new StructuredSelection(newResource), true);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void setSelection( ISelection selection ) {
        throw new RuntimeException("ModelExplorerRenameAction.setSelection is not supported"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.RenameResourceAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection ) {
        boolean result = SelectionUtilities.isSingleSelection(selection);

        if (result) {
            EObject selectedEObject = SelectionUtilities.getSelectedEObject(selection);

            if (selectedEObject != null) {
                this.inlineRenameAction.selectionChanged(new SelectionChangedEvent(this, selection));
                result = this.inlineRenameAction.isEnabled();
            } else {
                this.delRefactorRename.selectionChanged(this.actRefactorRename, selection);
                result = this.actRefactorRename.isEnabled();
            }
        }

        return result;
    }

}
