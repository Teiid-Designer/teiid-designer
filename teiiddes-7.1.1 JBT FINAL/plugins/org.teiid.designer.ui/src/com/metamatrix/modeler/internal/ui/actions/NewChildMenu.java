package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.viewers.ISelection;
import com.metamatrix.modeler.ui.actions.ModelerActionService;

public final class NewChildMenu extends NewMenuContributionsManager {

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.internal.ui.actions.NewMenuContributionsManager#getContributionManager(com.metamatrix.modeler.ui.actions.ModelerActionService,
     *      org.eclipse.jface.viewers.ISelection)
     */
    protected IContributionManager getContributionManager( ModelerActionService actionService,
                                                           ISelection selection ) {
        return actionService.getInsertChildMenu(selection);
    }

}
