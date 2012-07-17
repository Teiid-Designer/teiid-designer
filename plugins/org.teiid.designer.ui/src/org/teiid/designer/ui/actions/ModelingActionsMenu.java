package org.teiid.designer.ui.actions;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.viewers.ISelection;

public final class ModelingActionsMenu extends NewMenuContributionsManager {

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.actions.NewMenuContributionsManager#getContributionManager(org.teiid.designer.ui.actions.ModelerActionService,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
	protected IContributionManager getContributionManager( ModelerActionService actionService,
                                                           ISelection selection ) {
        return actionService.getModelingActionMenu(selection);
    }

}
