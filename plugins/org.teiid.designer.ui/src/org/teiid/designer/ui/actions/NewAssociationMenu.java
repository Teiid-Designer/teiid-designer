package org.teiid.designer.ui.actions;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.viewers.ISelection;

public final class NewAssociationMenu extends NewMenuContributionsManager {

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.actions.NewMenuContributionsManager#getContributionManager(org.teiid.designer.ui.actions.ModelerActionService,
     *      org.eclipse.jface.viewers.ISelection)
     */
    protected IContributionManager getContributionManager( ModelerActionService actionService,
                                                           ISelection selection ) {
        return actionService.getCreateAssociationMenu(selection);
    }

}
