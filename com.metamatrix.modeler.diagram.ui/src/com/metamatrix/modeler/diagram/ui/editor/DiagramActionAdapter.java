/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.DiagramActionService;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.actions.ActionService;

/**
 * @since 5.0
 */
public class DiagramActionAdapter extends AbstractModelEditorPageActionBarContributor
    implements IDiagramActionAdapter, DiagramUiConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////
    private static final String PREFIX = I18nUtil.getPropertyPrefix(DiagramActionAdapter.class);

    // ============================================================================================================================
    // Variables

    // collection of contributions that have been contributed via the contributeExportedActions().
    // these contributions get installed in the Edit Menu and the visibility of these is controlled when
    // editor pages/diagram types are changed. these must be removed from the edit menu when this is disposed.
    private List contributionItems;

    // Hashmap of registered actions that can be used to cleanup during dispose method
    private HashMap allRegisteredActions = new HashMap();

    /** The main menu being contributed to. */
    private IMenuManager editMenu;

    private boolean actionsInitialized = false;

    // ============================================================================================================================
    // Constructors

    /**
     * @since 4.0
     */
    public DiagramActionAdapter( final ModelEditorPage page ) {
        super(page);

        contributionItems = new ArrayList();
        initActions();
    }

    /**
     * @since 5.0
     */
    protected void initActions() {
        actionsInitialized = true;
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#setDiagramEditor(com.metamatrix.modeler.ui.editors.ModelEditorPage)
     * @since 5.0
     */
    public void setDiagramEditor( final ModelEditorPage editor ) {
        super.setActiveEditor(editor);

        if (!actionsInitialized) {
            initActions();
        }
    }

    /**
     * @return
     * @since 5.0
     */
    protected DiagramEditor getDiagramEditor() {
        if (super.getEditorPage() instanceof DiagramEditor) return (DiagramEditor)super.getEditorPage();

        return null;
    }

    /**
     * @return
     * @since 5.0
     */
    protected List getSelectedInDiagram() {
        if (getDiagramEditor() != null) {
            return getDiagramEditor().getDiagramViewer().getSelectedEditParts();
        }

        return Collections.EMPTY_LIST;
    }

    // ============================================================================================================================
    // Methods

    /**
     * @param newItem
     * @since 5.0
     */
    protected void addContributionItem( IContributionItem newItem ) {
        contributionItems.add(newItem);
    }

    /**
     * @return
     * @since 5.0
     */
    protected List getContributionItems() {
        return contributionItems;
    }

    /**
     * @param oldItem
     * @since 5.0
     */
    protected void removeContributionItem( IContributionItem oldItem ) {
        contributionItems.remove(oldItem);
    }

    /**
     * Retrieves the requested action from the action service.
     * 
     * @param theActionId the action identifier
     * @return the action or <code>null</code> if not found in the service
     */
    protected IAction getRegisteredAction( String theActionId ) {
        IAction result = null;
        ActionService actionService = getActionService();
        String key = DiagramActionService.constructKey(theActionId, getEditorPage());

        if (actionService.isRegistered(key)) {
            try {
                result = actionService.getAction(key);
            } catch (CoreException theException) {
                Util.log(IStatus.ERROR, Util.getString(PREFIX + "actionProblem", new Object[] {theActionId})); //$NON-NLS-1$
            }
        }

        return result;
    }

    /**
     * Registers the specified action with the action service and wires it to receive selection events.
     * 
     * @param theAction the action being registered
     */
    protected void registerAction( AbstractAction theAction ) {
        DiagramUiPlugin.registerDiagramActionForSelection(theAction);
        String actionKey = DiagramActionService.constructKey(theAction, getEditorPage());
        getActionService().registerAction(actionKey, theAction);
        allRegisteredActions.put(theAction, actionKey);
    }

    /**
     * Unregisters the specified action with the action service.
     * 
     * @param theAction the action being unregistered
     */
    protected void unregisterAction( AbstractAction theAction ) {
        DiagramUiPlugin.unregisterDiagramActionForSelection(theAction);
        String actionKey = (String)allRegisteredActions.get(theAction);
        getActionService().removeAction(actionKey);
    }

    /**
     * Creates an <code>ActionContributionItem</code> using the given action. Also hides the item and registers the action in the
     * service.
     * 
     * @param theAction the action whose <code>ActionContributionItem</code> is being created
     * @since 4.0
     */
    protected ActionContributionItem createActionContributionItem( final IAction theAction ) {
        ActionContributionItem item = new ActionContributionItem(theAction);
        item.setVisible(false);
        addContributionItem(item);
        return item;
    }

    /**
     * @since 4.0
     */
    @Override
    public ActionService getActionService() {
        // Defect 24615 - Action service may be null. VDB editor will end up Hiding DiagramEditor and calling dispose without
        // initializing the action service.
        ActionService actionService = null;

        if (getEditorPage() != null && getEditorPage().getSite() != null && getEditorPage().getEditorSite().getPage() != null) {
            actionService = DiagramUiPlugin.getDefault().getActionService(getEditorPage().getSite().getPage());
        }

        return actionService;
    }

    // ============================================================================================================================
    // AbstractModelEditorPageActionBarContributor Methods

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPageActionBarContributor#pageActivated()
     * @since 4.0
     */
    @Override
    public void pageActivated() {
        // set main actionbar contributions to be visible
        setActionsVisible(true);
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPageActionBarContributor#pageDeactivated()
     * @since 4.0
     */
    @Override
    public void pageDeactivated() {
        // set main actionbar contributions to be hidden
        setActionsVisible(false);
    }

    /**
     * Helper method to show/hide action contributions.
     * 
     * @param theShowFlag indicates if the actions should be visible or not
     */
    private void setActionsVisible( boolean theShowFlag ) {
        for (int size = contributionItems.size(), i = 0; i < size; i++) {
            IContributionItem item = (IContributionItem)contributionItems.get(i);
            item.setVisible(theShowFlag);
        }

        if (getActionBars() != null && getActionBars().getToolBarManager() != null) {
            getActionBars().getToolBarManager().update(true);
        }
    }

    protected void setEditMenu( IMenuManager theEditMenu ) {
        this.editMenu = theEditMenu;
    }

    // ============================================================================================================================
    // EditorActionBarContributor Methods

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {
        ActionService service = getActionService();

        // Defect 24615 - Action service may be null. VDB editor will end up Hiding DiagramEditor and calling dispose without
        // initializing the action service.
        if (service != null) {
            for (Iterator iter = allRegisteredActions.keySet().iterator(); iter.hasNext();) {
                AbstractAction theAction = (AbstractAction)iter.next();
                unregisterAction(theAction);
            }

            for (int size = contributionItems.size(), i = 0; i < size; i++) {
                IContributionItem item = (IContributionItem)contributionItems.get(i);

                if (item instanceof ActionContributionItem) {
                    ActionContributionItem ai = (ActionContributionItem)item;
                    service.removeAction(DiagramActionService.constructKey(ai.getAction(), getEditorPage()));

                    if (ai.getAction() instanceof ISelectionListener) {
                        DiagramUiPlugin.unregisterDiagramActionForSelection((ISelectionListener)ai.getAction());
                    }
                }

                // remove from edit menu
                if (this.editMenu != null) {
                    this.editMenu.remove(item);
                }
            }
        }
        setEditorPage(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#disposeOfActions()
     */
    public void disposeOfActions() {
        this.dispose();
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#contributeToDiagramToolBar()
     * @since 5.0
     */
    public void contributeToDiagramToolBar() {
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#contributeToMenuManager(org.eclipse.jface.action.IMenuManager,
     *      org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public void contributeToMenuManager( IMenuManager theMenuMgr,
                                         ISelection theSelection ) {
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#enableDiagramToolbarActions()
     * @since 5.0
     */
    public void enableDiagramToolbarActions() {
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#handleNotification(org.eclipse.emf.common.notify.Notification)
     * @since 5.0
     */
    public void handleNotification( Notification theNotification ) {
    }

    /**
     * @see com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter#shouldOverrideMenu(org.eclipse.jface.viewers.ISelection)
     * @since 5.0
     */
    public boolean shouldOverrideMenu( ISelection theSelection ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#createContextMenu()
     * @since 5.0
     */
    @Override
    public void createContextMenu() {
    }

}
