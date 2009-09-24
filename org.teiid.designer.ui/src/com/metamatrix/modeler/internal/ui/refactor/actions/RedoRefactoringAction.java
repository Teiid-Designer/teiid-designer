/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.refactor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.metamatrix.modeler.internal.ui.refactor.RefactorUndoListener;
import com.metamatrix.modeler.internal.ui.refactor.RefactorUndoManager;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ModelerActionService;

/**
 * RedoRefactoringAction
 */
public class RedoRefactoringAction
    extends ActionDelegate
    implements IWorkbenchWindowActionDelegate, IViewActionDelegate, RefactorUndoListener {

    private ModelerActionService actionService;
    private IAction action;

    /**
     * Construct an instance of RedoRefactoringAction.
     */
    public RedoRefactoringAction() {
        super();
        RefactorUndoManager.getInstance().addListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {
        if ( actionService == null ) {
            IViewSite site = view.getViewSite();
            this.actionService = (ModelerActionService) UiPlugin.getDefault().getActionService(site.getPage());
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
        if ( actionService == null ) {
            this.actionService = (ModelerActionService) UiPlugin.getDefault().getActionService(window.getActivePage());
        }
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate2#dispose()
     */
    @Override
    public void dispose() {
        RefactorUndoManager.getInstance().removeListener(this);
        super.dispose();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        super.selectionChanged(action, selection);
        this.action = action;
        action.setEnabled(getUndoManager().canRedo());
        action.setText(getUndoManager().getRedoLabel());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.refactor.RefactorUndoListener#stateChanged()
     */
    public void stateChanged() {
        action.setEnabled(getUndoManager().canRedo());
        action.setText(getUndoManager().getRedoLabel());
    }

    private RefactorUndoManager getUndoManager() {
        if ( this.actionService == null ) {
            actionService = (ModelerActionService) UiPlugin.getDefault().getActionService(UiPlugin.getDefault().getCurrentWorkbenchWindow().getActivePage());
        }
        return actionService.getRefactorUndoManager();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(IAction action) {
        getUndoManager().redo();
    }

}
