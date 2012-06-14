/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.advisor.ui.actions;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.teiid.designer.core.ModelerCore;

public class ToggleAutoBuildAction extends Action implements
		ActionFactory.IWorkbenchAction {
	private IWorkbenchWindow window;

	/**
	 * Creates a new ToggleAutoBuildAction
	 * 
	 * @param window
	 *            The window for parenting dialogs associated with this action
	 */
	public ToggleAutoBuildAction(IWorkbenchWindow window) {
		super("Build Automatically");
		this.window = window;
		setChecked(ModelerCore.getWorkspace().isAutoBuilding());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.ActionFactory.IWorkbenchAction#dispose()
	 */
	public void dispose() {
		// nothing to dispose
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		IWorkspace workspace = ModelerCore.getWorkspace();
		IWorkspaceDescription description = workspace.getDescription();
		description.setAutoBuilding(!description.isAutoBuilding());
		try {
			workspace.setDescription(description);
		} catch (CoreException e) {
			ErrorDialog.openError(window.getShell(), null, null, e.getStatus());
		}
	}
}
