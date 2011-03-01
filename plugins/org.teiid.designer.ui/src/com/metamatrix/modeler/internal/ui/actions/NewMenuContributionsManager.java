package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.CompoundContributionItem;

import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ModelerActionService;

public abstract class NewMenuContributionsManager extends CompoundContributionItem {

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
     */
    @Override
    protected final IContributionItem[] getContributionItems() {
        UiPlugin plugin = UiPlugin.getDefault();
        IWorkbench workbench = plugin.getWorkbench();
        IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

        // the action service creates the new child menu in the context menu so have it create one for edit menu
        ModelerActionService actionService = (ModelerActionService)plugin.getActionService(page);

        // pass current selection and action service to obtain correct items
        if (page.getActivePart().getSite().getSelectionProvider() != null) {
            ISelection selection = page.getActivePart().getSite().getSelectionProvider().getSelection();
            IContributionManager contributionMgr = getContributionManager(actionService, selection);
            return contributionMgr.getItems();
        }
        
        return new IContributionItem[0];
    }

    protected abstract IContributionManager getContributionManager( ModelerActionService actionService,
                                                                    ISelection selection );

}
