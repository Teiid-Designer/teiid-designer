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
package com.metamatrix.modeler.internal.diagram.ui.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.actions.ActionWrapper;
import com.metamatrix.modeler.diagram.ui.actions.AutoLayoutWrapper;
import com.metamatrix.modeler.diagram.ui.actions.DiagramActionService;
import com.metamatrix.modeler.diagram.ui.actions.DiagramGlobalActionsMap;
import com.metamatrix.modeler.diagram.ui.actions.FontDownWrapper;
import com.metamatrix.modeler.diagram.ui.actions.FontUpWrapper;
import com.metamatrix.modeler.diagram.ui.actions.IDiagramActionConstants;
import com.metamatrix.modeler.diagram.ui.actions.PrintWrapper;
import com.metamatrix.modeler.diagram.ui.actions.ZoomComboActionContributeItem;
import com.metamatrix.modeler.diagram.ui.actions.ZoomComboWrapper;
import com.metamatrix.modeler.diagram.ui.actions.ZoomInWrapper;
import com.metamatrix.modeler.diagram.ui.actions.ZoomOutWrapper;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.diagram.ui.editor.IDiagramActionAdapter;
import com.metamatrix.modeler.internal.ui.editors.ModelEditorSite;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.actions.ControlledPopupMenuExtender;
import com.metamatrix.ui.actions.GlobalActionsMap;

/**
 * @since 4.0
 */
public final class DiagramActionContributor extends AbstractModelEditorPageActionBarContributor
    implements DiagramUiConstants, IDiagramActionConstants {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(DiagramActionContributor.class);

    private ActionContributionItem zoomIn;
    private ActionContributionItem zoomOut;
    private ActionContributionItem zoomCombo;
    private ActionContributionItem fontUp;
    private ActionContributionItem fontDown;
    private ActionContributionItem autoLayout;
    private GroupMarker diagramGroupStart;
    private GroupMarker diagramGroupEnd;

    // actions map is needed since we want to override the default print action
    private DiagramGlobalActionsMap actionsMap;

    // collection of ActionContributionItems and GroupMarkers
    private List<ContributionItem> contributionItems;

    // Hashmap of registered actions that can be used to cleanup during dispose method
    private HashMap<AbstractAction, String> allRegisteredActions = new HashMap<AbstractAction, String>();

    private String CONTEXT_MENU_ID = ContextMenu.DIAGRAM_EDITOR_PAGE;

    // cached to know if this adapter should be deactivated
    private IDiagramActionAdapter cachedAdapter;

    private boolean firstTime = true;

    private ControlledPopupMenuExtender popupMenuExtender = null;

    private boolean contributed = false;

    private IMenuManager editMenu;

    private List<IDiagramActionAdapter> contributors = new ArrayList<IDiagramActionAdapter>();

    /**
     * @since 4.0
     */
    public DiagramActionContributor( final ModelEditorPage page ) {
        super(page);

        actionsMap = new DiagramGlobalActionsMap();
        actionsMap.reset();

        contributionItems = new ArrayList<ContributionItem>();
        initActions();
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#getGlobalActions()
     */
    @Override
    public GlobalActionsMap getGlobalActions() {
        /*
         * 1. get map from 'adapter' contributors (ex: TransformationActionsAdapter)
         * 2. merge our map and that map (?)
         * 3. return it in this method
         * 
         */

        DiagramGlobalActionsMap gamAdapterMap = null;
        IDiagramActionAdapter diagramActionAdapter = getCurrentDiagramActionAdapter();

        if (diagramActionAdapter != null) {
            gamAdapterMap = (DiagramGlobalActionsMap)diagramActionAdapter.getGlobalActions();
            initDiagramGLobalActionDetails();
        }

        if (gamAdapterMap == null) {
            return actionsMap;
        }
        gamAdapterMap = provideMissingActions(gamAdapterMap);

        return gamAdapterMap;
    }

    private DiagramGlobalActionsMap provideMissingActions( DiagramGlobalActionsMap dgaMap ) {
        /*
         * any actions that are 'global' to eclipse and also global (occuring once) across all
         * diagram types must be added here to the current action adapter's global actions map.
         * 'Print' is the only one currently treated this way.
         */

        actionsMap.put(IModelerActionConstants.EclipseGlobalActions.PRINT,
                       getAction(IModelerActionConstants.EclipseGlobalActions.PRINT));

        if (dgaMap.get(IModelerActionConstants.EclipseGlobalActions.PRINT) == null) {
            IAction actPrint = getAction(IModelerActionConstants.EclipseGlobalActions.PRINT);
            dgaMap.put(IModelerActionConstants.EclipseGlobalActions.PRINT, actPrint);
        }
        return dgaMap;
    }

    @Override
    public void init( IActionBars bars,
                      IWorkbenchPage page ) {
        super.init(bars, page);
    }

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
     * @since 5.0
     */
    @Override
    public void contributeToMenu( IMenuManager theMenuManager ) {
        // this menu is called once by the Eclipse framework
        super.contributeToMenu(theMenuManager);

        IContributionItem item = theMenuManager.find(ModelerActionBarIdManager.getEditMenuId());

        // cache the edit menu so that we can pass it to the adapters
        if ((item != null) && (item instanceof IMenuManager)) {
            this.editMenu = (IMenuManager)item;
        }
    }

    /**
     * Retrieves the requested action from the action service.
     * 
     * @param theActionId the action identifier
     * @return the action or <code>null</code> if not found in the service
     */
    private IAction getRegisteredAction( String theActionId ) {
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
    private void registerAction( AbstractAction theAction ) {
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
        String actionKey = allRegisteredActions.get(theAction);
        getActionService().removeAction(actionKey);
    }

    /**
     * @since 4.0
     */
    private void initActions() {
        // Each DiagramActionContributor will have their own actions so don't them from the DiagramActionService
        // create these in order that they willl appear

        // construct start group marker
        diagramGroupStart = new GroupMarker(Toolbar.DIAGRAM_START);
        contributionItems.add(diagramGroupStart);

        // ----- ZoomInWrapper -----//

        AbstractAction action = (AbstractAction)getRegisteredAction(ZoomInWrapper.class.getName());

        if (action == null) {
            action = new ZoomInWrapper();
            registerAction(action);
        }

        zoomIn = createActionContributionItem(action);

        // ----- ZoomOutWrapper -----//

        action = (AbstractAction)getRegisteredAction(ZoomOutWrapper.class.getName());

        if (action == null) {
            action = new ZoomOutWrapper();
            registerAction(action);
        }

        zoomOut = createActionContributionItem(action);

        // ----- ZoomComboWrapper -----//

        action = (AbstractAction)getRegisteredAction(ZoomComboWrapper.class.getName());

        if (action == null) {
            action = new ZoomComboWrapper();
            registerAction(action);
        }

        zoomCombo = new ZoomComboActionContributeItem(
                                                      action,
                                                      DiagramUiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getPartService());
        contributionItems.add(zoomCombo);

        // ----- FontUpWrapper -----//

        action = (AbstractAction)getRegisteredAction(FontUpWrapper.class.getName());

        if (action == null) {
            action = new FontUpWrapper((DiagramEditor)getEditorPage());
            registerAction(action);
        }

        fontUp = createActionContributionItem(action);

        // ----- FontDownWrapper -----//

        action = (AbstractAction)getRegisteredAction(FontDownWrapper.class.getName());

        if (action == null) {
            action = new FontDownWrapper((DiagramEditor)getEditorPage());
            registerAction(action);
        }

        fontDown = createActionContributionItem(action);

        // ----- AutoLayoutWrapper -----//

        action = (AbstractAction)getRegisteredAction(AutoLayoutWrapper.class.getName());

        if (action == null) {
            action = new AutoLayoutWrapper();
            registerAction(action);
        }

        autoLayout = createActionContributionItem(action);

        // ----- PrintWrapper -----//

        action = (AbstractAction)getRegisteredAction(PrintWrapper.class.getName());

        if (action == null) {
            action = new PrintWrapper();
            registerAction(action);
        }

        createActionContributionItem(action);

        // construct end group marker
        diagramGroupEnd = new GroupMarker(Toolbar.DIAGRAM_END);
        contributionItems.add(diagramGroupEnd);

        // override default action so that Print in the menu and toolbar is the diagram print
        actionsMap.put(IModelerActionConstants.EclipseGlobalActions.PRINT,
                       getAction(IModelerActionConstants.EclipseGlobalActions.PRINT));

    }

    private IAction getAction( String sActionId ) {
        try {
            return getActionService().getAction(sActionId);
        } catch (CoreException ce) {
            String message = this.getClass().getName() + ":  getAction() error  "; //$NON-NLS-1$
            DiagramUiConstants.Util.log(IStatus.ERROR, ce, message);
        }
        return null;
    }

    /**
     * Creates an <code>ActionContributionItem</code> using the given action. Also hides the item and registers the action in the
     * service.
     * 
     * @param theAction the action whose <code>ActionContributionItem</code> is being created
     * @since 4.0
     */
    private ActionContributionItem createActionContributionItem( final IAction theAction ) {
        ActionContributionItem item = new ActionContributionItem(theAction);
        item.setVisible(false);
        contributionItems.add(item);
        return item;
    }

    /**
     * @since 4.0
     */
    @Override
    public ActionService getActionService() {
        return DiagramUiPlugin.getDefault().getActionService(getEditorPage().getSite().getPage());
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.AbstractModelEditorPageActionBarContributor#createContextMenu()
     */
    @Override
    public void createContextMenu() {
        // create menu
        // jh fix: 2 arg ctor for MenuManager is: MenuManager(text, id)
        // 1 arg ctor only sets the text, not the id
        MenuManager mgr = new MenuManager(CONTEXT_MENU_ID, CONTEXT_MENU_ID);

        mgr.setRemoveAllWhenShown(true);
        Menu contextMenu = mgr.createContextMenu(getEditorPage().getControl());

        getEditorPage().getControl().setMenu(contextMenu);

        // wire up the listening
        mgr.addMenuListener(this);
    }

    /**
     * @see org.eclipse.jface.action.IMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void menuAboutToShow( IMenuManager theMenuMgr ) {
        ISelectionProvider selProvider = getEditorPage().getModelObjectSelectionProvider();
        boolean override = false;
        IDiagramActionAdapter diagramActionAdapter = getCurrentDiagramActionAdapter();
        if (diagramActionAdapter != null) {
            override = diagramActionAdapter.shouldOverrideMenu(selProvider.getSelection());
        }

        if (!override) {
            getActionService().contributeToContextMenu(theMenuMgr, getGlobalActions(), null);
        }

        if (diagramActionAdapter != null) {
            diagramActionAdapter.contributeToMenuManager(theMenuMgr, selProvider.getSelection());
        }

        if (!override) {
            if (theMenuMgr.find(IModelerActionConstants.ContextMenu.ADDITIONS) == null) {
                theMenuMgr.add(new Separator(IModelerActionConstants.ContextMenu.ADDITIONS));
            }
            if (popupMenuExtender == null) {
                // Need to create a PopupMenuExtender to include any external actions here
                IEditorPart editor = ((ModelEditorSite)getEditorPage().getEditorSite()).getEditor();
                popupMenuExtender = new ControlledPopupMenuExtender(CONTEXT_MENU_ID, (MenuManager)theMenuMgr, selProvider, editor);
            }
            popupMenuExtender.menuAboutToShow(theMenuMgr);
        }
    }

    private IDiagramActionAdapter getCurrentDiagramActionAdapter() {
        ModelEditorPage mep = getEditorPage();
        IDiagramActionAdapter idapDiagramActionAdapter = (IDiagramActionAdapter)mep.getAdapter(IDiagramActionAdapter.class);
        return idapDiagramActionAdapter;
    }

    private void initDiagramGLobalActionDetails() {

        if (firstTime) {
            IAction action = null;

            for (int i = 0; i < DiagramGlobalActionsMap.ALL_DIAGRAM_GLOBAL_ACTIONS.length; i++) {
                String actionId = DiagramGlobalActionsMap.ALL_DIAGRAM_GLOBAL_ACTIONS[i];

                try {
                    action = getActionService().getAction(actionId);
                } catch (CoreException ce) {
                    System.out.println("[DiagramActionContributor] actionService.getAction failed"); //$NON-NLS-1$
                }
                if (action instanceof ActionWrapper) {
                    ((ActionWrapper)action).initialize();
                }
            }

            firstTime = false;
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPageActionBarContributor#pageActivated()
     * @since 4.0
     */
    @Override
    public void pageActivated() {
        /*
         * 1. show global actions on the toolbar          
         * 2. call pageActivated on the contributor
         */
        IDiagramActionAdapter diagramActionAdapter = getCurrentDiagramActionAdapter();

        // deactivate if necessary
        // deactivate does NOT get called if a new diagram type in the same model is displayed.
        // for instance, a model's package diagram is displayed and the user double-clicks the same model's
        // transformation diagram in the model explorer tree. this will display the transformation diagram.
        // however since the editor page stayed the same deactivate did not get called. so need to do here.
        if ((diagramActionAdapter != cachedAdapter) && (cachedAdapter != null)) {

            // deactivate page contributor
            if (cachedAdapter instanceof AbstractModelEditorPageActionBarContributor) {
                ((AbstractModelEditorPageActionBarContributor)cachedAdapter).pageDeactivated();
            }
        }

        // activate current adapter
        if ((diagramActionAdapter != null) && (diagramActionAdapter instanceof AbstractModelEditorPageActionBarContributor)) {
            cachedAdapter = diagramActionAdapter;
            initDiagramGLobalActionDetails();

            // have adapter contribute if it hasn't already
            if (!this.contributors.contains(cachedAdapter)) {
                this.contributors.add(cachedAdapter);
                cachedAdapter.contributeExportedActions(this.editMenu);
            }

            ((AbstractModelEditorPageActionBarContributor)diagramActionAdapter).pageActivated();
            this.editMenu.update(true);
        }

        // set the merged actions visible, subject to what the contributor's map says to do
        contributeToToolBar(actionBars.getToolBarManager());
        setActionsVisible(true);
        getActionBars().updateActionBars();

        // Need to tell the zoomCombo to update it's text
        if (zoomCombo != null) {
            ((ZoomComboActionContributeItem)zoomCombo).refreshText();
        }
    }

    /**
     * @see com.metamatrix.modeler.ui.editors.ModelEditorPageActionBarContributor#pageDeactivated()
     * @since 4.0
     */
    @Override
    public void pageDeactivated() {
        IDiagramActionAdapter diagramActionAdapter = getCurrentDiagramActionAdapter();
        if (diagramActionAdapter != null) {

            if (diagramActionAdapter instanceof AbstractModelEditorPageActionBarContributor) {
                ((AbstractModelEditorPageActionBarContributor)diagramActionAdapter).pageDeactivated();
            }
        }

        // set the merged actions hidden, subject to what the contributor's map says to do
        setActionsVisible(false);
        decontributeToToolBar(actionBars.getToolBarManager());
        getActionBars().updateActionBars();
    }

    /**
     * Helper method to show/hide action contributions.
     * 
     * @param theShowFlag indicates if the actions should be visible or not
     */
    private void setActionsVisible( boolean theShowFlag ) {
        // includes ActionContributionItems and GroupMarkers
        for (int size = contributionItems.size(), i = 0; i < size; i++) {
            ContributionItem item = contributionItems.get(i);
            item.setVisible(theShowFlag);
        }

        if (getActionBars() != null && getActionBars().getToolBarManager() != null) getActionBars().getToolBarManager().update(true);
    }

    public void decontributeToToolBar( final IToolBarManager toolBarManager ) {
        toolBarManager.remove(diagramGroupStart);

        toolBarManager.remove(zoomIn);
        toolBarManager.remove(zoomCombo);
        toolBarManager.remove(zoomOut);
        toolBarManager.remove(fontUp);
        toolBarManager.remove(fontDown);
        toolBarManager.remove(autoLayout);

        toolBarManager.remove(diagramGroupEnd);

        contributed = false;
    }

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
     * @since 4.0
     */
    @Override
    public void contributeToToolBar( final IToolBarManager toolBarManager ) {
        if (!contributed) {
            super.contributeToToolBar(toolBarManager);

            // add diagram custom actions
            if (toolBarManager.find(diagramGroupStart.getId()) == null) toolBarManager.add(diagramGroupStart);

            toolBarManager.appendToGroup(Toolbar.DIAGRAM_START, zoomIn);
            toolBarManager.appendToGroup(Toolbar.DIAGRAM_START, zoomCombo);
            toolBarManager.appendToGroup(Toolbar.DIAGRAM_START, zoomOut);
            toolBarManager.appendToGroup(Toolbar.DIAGRAM_START, fontUp);
            toolBarManager.appendToGroup(Toolbar.DIAGRAM_START, fontDown);
            toolBarManager.appendToGroup(Toolbar.DIAGRAM_START, autoLayout);

            if (toolBarManager.find(diagramGroupEnd.getId()) == null) toolBarManager.add(diagramGroupEnd);

            contributed = true;
        }
    }

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#dispose()
     * @since 4.0
     */
    @Override
    public void dispose() {

        for (Iterator<AbstractAction> iter = allRegisteredActions.keySet().iterator(); iter.hasNext();) {
            AbstractAction theAction = iter.next();
            unregisterAction(theAction);
        }

        ActionService service = getActionService();
        IToolBarManager tbMgr = getActionBars().getToolBarManager();

        for (int size = contributionItems.size(), i = 0; i < size; i++) {
            ContributionItem item = contributionItems.get(i);
            tbMgr.remove(item);

            if (item instanceof ActionContributionItem) {
                service.removeAction(DiagramActionService.constructKey(((ActionContributionItem)item).getAction(),
                                                                       getEditorPage()));
            }
        }

        tbMgr.update(true);

    }

    @Override
    public List<IAction> getAdditionalModelingActions( ISelection theSelection ) {
        MenuManager menu = new MenuManager("TempMenu", "TempMenuID"); //$NON-NLS-1$ //$NON-NLS-2$

        List theActions = new ArrayList();

        contributeExportedActions(menu);

        Object[] theItems = menu.getItems();
        if (theItems != null && theItems.length > 0) {
            for (int i = 0; i < theItems.length; i++) {
                theActions.add(theItems[i]);
            }
        }

        return theActions;
    }

    @Override
    public void contributeExportedActions( IMenuManager theMenuMgr ) {
        /*
         * Get the current 'diagram type adapter' and call the same method 
         * on it.  Example: TransformationActionAdapter.
         */
        // do not export to our own menu
        if (CONTEXT_MENU_ID.equals(theMenuMgr.getId())) {
            return;
        }

        IDiagramActionAdapter diagramActionAdapter = getCurrentDiagramActionAdapter();
        if (diagramActionAdapter != null) {
            diagramActionAdapter.contributeExportedActions(theMenuMgr);
            theMenuMgr.update();
        }
    }

    public void tellZoomWrappersToClose() {
        ((ZoomInWrapper)zoomIn.getAction()).closeZoomManager();
        ((ZoomOutWrapper)zoomOut.getAction()).closeZoomManager();
    }
}
