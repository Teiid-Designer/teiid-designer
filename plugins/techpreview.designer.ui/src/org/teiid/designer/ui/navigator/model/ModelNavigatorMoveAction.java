/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.moveRefactorActionText;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.moveRefactorActionToolTip;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.MoveProjectAction;
import org.eclipse.ui.actions.MoveResourceAction;

import com.metamatrix.modeler.internal.ui.refactor.actions.MoveRefactorAction;
import com.metamatrix.modeler.ui.actions.DelegatableAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * 
 */
public class ModelNavigatorMoveAction extends MoveResourceAction {

    private DelegatableAction actMove;
    private IActionDelegate delMove;
    private final MoveProjectAction moveProjectAction;
    private final TreeViewer viewer;

    public ModelNavigatorMoveAction( IWorkbenchWindow window,
                                     TreeViewer viewer ) {
        super(window);
        this.viewer = viewer;
        this.moveProjectAction = new MoveProjectAction(window);
        this.delMove = new MoveRefactorAction();
        this.actMove = new DelegatableAction(delMove, window);

        this.actMove.setText(moveRefactorActionText);
        this.actMove.setToolTipText(moveRefactorActionToolTip);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.MoveResourceAction#run()
     */
    @Override
    public void run() {
        Object selection = getStructuredSelection().getFirstElement();

        if (selection instanceof IResource) {
            this.delMove.selectionChanged(this.actMove, getStructuredSelection());
            this.delMove.run(this.actMove);
        }

        if (this.moveProjectAction.isEnabled()) {
            this.moveProjectAction.run();
            return;
        }

        super.run();

        List destinations = getDestinations();

        if ((destinations != null) && !destinations.isEmpty()) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            List resources = new ArrayList();

            for (Object obj : destinations) {
                IResource newResource = root.findMember((IPath)obj);

                if (newResource != null) {
                    resources.add(newResource);
                }
            }

            this.viewer.setSelection(new StructuredSelection(resources), true);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.CopyResourceAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection ) {
        if (selection.size() == 1) {
            List resources = SelectionUtilities.getSelectedIResourceObjects(selection);

            if (!resources.isEmpty()) {
                this.delMove.selectionChanged(this.actMove, selection);
                return this.actMove.isEnabled();
            }
        }

        return false;
    }

}
