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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.metamatrix.modeler.internal.ui.refactor.RefactorUndoListener;
import com.metamatrix.modeler.internal.ui.refactor.RefactorUndoManager;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ModelerActionService;

/**
 * UndoRefactoringAction
 */
public class UndoRefactoringAction extends ActionDelegate 
    implements IWorkbenchWindowActionDelegate, IViewActionDelegate, RefactorUndoListener {

    private ModelerActionService actionService;
    private IAction action;

    /**
     * Construct an instance of UndoRefactoringAction.
     */
    public UndoRefactoringAction() {
        super();
        RefactorUndoManager.getInstance().addListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {
        if ( actionService == null ) {
            this.actionService = (ModelerActionService) UiPlugin.getDefault().getActionService(view.getSite().getPage());
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
        action.setEnabled(getUndoManager().canUndo());
        action.setText(getUndoManager().getUndoLabel());
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.internal.ui.refactor.RefactorUndoListener#stateChanged()
     */
    public void stateChanged() {
        action.setEnabled(getUndoManager().canUndo());
        action.setText(getUndoManager().getUndoLabel());
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
		getUndoManager().undo();
	}

}
