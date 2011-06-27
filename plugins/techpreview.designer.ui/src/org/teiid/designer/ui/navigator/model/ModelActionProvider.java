/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.navigator.model;

import static com.metamatrix.modeler.internal.ui.PluginConstants.Prefs.General.SHOW_IMPORTS_IN_MODEL_EXPLORER;
import static com.metamatrix.modeler.internal.ui.PluginConstants.Prefs.General.SHOW_NON_MODELS_IN_MODEL_EXPLORER;
import static com.metamatrix.modeler.internal.ui.PluginConstants.Prefs.General.SORT_MODEL_CONTENTS;
import static com.metamatrix.modeler.ui.UiConstants.Util;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.createRenameActionErrorMessage;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.defaultCopyActionNotFoundMessage;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.refreshActionText;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.refreshActionToolTip;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.showImportsActionText;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.showNonModelsActionText;
import static org.teiid.designer.ui.navigator.model.ModelNavigatorMessages.sortModelContentsActionText;

import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import com.metamatrix.core.event.EventObjectListener;
import com.metamatrix.core.event.EventSourceException;
import com.metamatrix.modeler.internal.core.workspace.DotProjectUtils;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.actions.CloneProjectAction2;
import com.metamatrix.modeler.internal.ui.actions.DeleteResourceAction;
import com.metamatrix.modeler.internal.ui.actions.PasteInResourceAction;
import com.metamatrix.modeler.internal.ui.actions.PasteSpecialAction;
import com.metamatrix.modeler.internal.ui.actions.PropertyDialogAction;
import com.metamatrix.modeler.internal.ui.actions.RemoveProjectAction;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.refactor.actions.RenameRefactorAction;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants.Extensions;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.DelegatableAction;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants.ContextMenu;
import com.metamatrix.modeler.ui.actions.ModelResourceActionManager;
import com.metamatrix.modeler.ui.actions.ModelerActionBarIdManager;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.modeler.ui.actions.ModelerGlobalActionsMap;
import com.metamatrix.modeler.ui.actions.ModelerSpecialActionManager;
import com.metamatrix.modeler.ui.event.ModelResourceEvent;
import com.metamatrix.modeler.ui.viewsupport.IExtendedModelObject;
import com.metamatrix.modeler.ui.viewsupport.NonModelViewerFilter;
import com.metamatrix.ui.actions.ActionService;
import com.metamatrix.ui.actions.GlobalActionsMap;
import com.metamatrix.ui.actions.IActionConstants.EclipseGlobalActions;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * 
 */
public class ModelActionProvider extends CommonActionProvider {

    private static final String ECLIPSE_DELETE_ID = "org.eclipse.ui.DeleteResourceAction"; //$NON-NLS-1$
    private static final String ECLIPSE_MOVE_ID = "org.eclipse.ui.MoveResourceAction"; //$NON-NLS-1$
    private static final String ECLIPSE_PASTE_ID = "org.eclipse.ui.PasteAction"; //$NON-NLS-1$
    private static final String ECLIPSE_RENAME_ID = "org.eclipse.ui.RenameResourceAction"; //$NON-NLS-1$
    private static final String MENU_GROUP = "modelExplorerMenuGroup"; //$NON-NLS-1$
    private static final String MODELING_LABEL = Util.getString("ModelerSpecialActionManager.specialLabel"); //$NON-NLS-1$
    private static final String TOOLBAR_GROUP = "modelExplorerToolBarGroup"; //$NON-NLS-1$

    private ModelerGlobalActionsMap actionsMap;
    private CloneProjectAction2 cloneProjectAction;
    private ModelExplorerCopyAction copyAction;
    private IResourceChangeListener markerListener;
    private EventObjectListener modelResourceListener;
    private ModelNavigatorMoveAction moveAction;
    private INotifyChangedListener notificationHandler;
    private IPartListener partListener;
    private PropertyDialogAction propertyAction;
    private RemoveProjectAction removeProjectAction;
    private ModelNavigatorRenameAction renameAction;
    private ISelectionListener selectionListener;

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.ActionGroup#dispose()
     */
    @Override
    public void dispose() {
        if (this.selectionListener != null) {
            getWorkbenchSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this.selectionListener);
        }

        if (this.copyAction != null) {
            getWorkbenchSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this.copyAction);
        }

        if (this.notificationHandler != null) {
            ModelUtilities.removeNotifyChangedListener(this.notificationHandler);
        }

        if (this.markerListener != null) {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.markerListener);
        }

        if (this.modelResourceListener != null) {
            try {
                UiPlugin.getDefault().getEventBroker().removeListener(this.modelResourceListener);
            } catch (EventSourceException e) {
                Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }

        if (this.partListener != null) {
            getWorkbenchSite().getPage().removePartListener(this.partListener);
        }

        if (getViewer().getContentProvider() != null) {
            getViewer().getContentProvider().dispose();
        }

        if (getViewer().getLabelProvider() != null) {
            getViewer().getLabelProvider().dispose();
        }

        super.dispose();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.ActionGroup#fillActionBars(org.eclipse.ui.IActionBars)
     */
    @Override
    public void fillActionBars( IActionBars actionBars ) {
        super.fillActionBars(actionBars);

        // Note: this method gets call when view is constructed and each time selection changes

        //
        // customize view menu (add show imports action)
        //
        IMenuManager menuMgr = actionBars.getMenuManager();

        if (menuMgr.find(MENU_GROUP) == null) {
            menuMgr.add(new GroupMarker(MENU_GROUP));
            menuMgr.add(new ShowImportsAction());
            menuMgr.add(new ShowNonModelsAction());
        }

        //
        // customize view toolbar (add preview data, sort model contents, and refresh actions)
        //
        IToolBarManager toolBarMgr = actionBars.getToolBarManager();

        if (toolBarMgr.find(TOOLBAR_GROUP) == null) {
            toolBarMgr.add(new Separator(TOOLBAR_GROUP));

            // preview data action
            IAction previewAction = ModelerSpecialActionManager.getAction(Extensions.PREVIEW_DATA_ACTION_ID);

            if (previewAction != null) {
                toolBarMgr.add(previewAction);
                toolBarMgr.add(new Separator());
            }

            // selection of the sort button sets the preference, which will trigger a refresh
            toolBarMgr.add(new SortModelContentsAction());

            // add refresh tree action
            IAction refreshAction = new RefreshAction(getWorkbenchSite().getSite());
            refreshAction.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.REFRESH_ICON));
            refreshAction.setToolTipText(refreshActionToolTip);
            refreshAction.setText(refreshActionText);
            refreshAction.setId("modelExplorerResourceNavigator.refreshAction"); //$NON-NLS-1$
            toolBarMgr.add(refreshAction);
        }

        if (this.renameAction != null) {
            this.renameAction.selectionChanged(getSelection());
        }

        if (this.moveAction != null) {
            this.moveAction.selectionChanged(getSelection());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
     */
    @Override
    public void fillContextMenu( IMenuManager menu ) {
        super.fillContextMenu(menu);

        IStructuredSelection selection = getSelection();

        if (isAllExtendedModelObjects(selection)) {
            // Get the context menu from the extended model Objects
            boolean didOverride = false;

            for (Object obj : SelectionUtilities.getSelectedObjects(selection)) {
                IExtendedModelObject extendedModelObject = (IExtendedModelObject)obj;

                if (extendedModelObject.overrideContextMenu()) {
                    didOverride = true;
                    extendedModelObject.fillContextMenu(menu);
                }
            }

            if (didOverride) {
                if (menu.find(IModelerActionConstants.ContextMenu.ADDITIONS) == null) {
                    menu.add(new Separator(IModelerActionConstants.ContextMenu.ADDITIONS));
                }

                return;
            }
        }

        // if single or multi selection has only EObjects show our action service context menu
        // else show the ResourceNavigators
        if (SelectionUtilities.isAllEObjects(selection)) {
            getActionService().contributeToContextMenu(menu, actionsMap, selection);
        } else {
            super.fillContextMenu(menu);

            try {
                // need to override the delete in the context menu. the only way i could figure out was by
                // removing and adding. our action makes sure to close the model and close it's editor
                // (if necessary) prior to deleting.

                if (menu.find(ECLIPSE_DELETE_ID) != null) {
                    IAction deleteAction = getActionService().getAction(DeleteResourceAction.class);
                    menu.insertAfter(ECLIPSE_DELETE_ID, deleteAction);
                    menu.remove(ECLIPSE_DELETE_ID);
                }

                // Add PasteSpecialAction after PasteAction
                // (if single selection and model resource)
                if (SelectionUtilities.isSingleSelection(selection)) {
                    Object obj = SelectionUtilities.getSelectedObject(selection);

                    if ((obj instanceof IResource) && ModelUtilities.isModelFile((IResource)obj)) {
                        // Add PasteSpecial Action after Paste
                        if (menu.find(ECLIPSE_PASTE_ID) != null) {
                            IAction pasteSpecialAction = getActionService().getAction(PasteSpecialAction.class);
                            menu.insertAfter(ECLIPSE_PASTE_ID, pasteSpecialAction);
                        }
                    }
                }

                // need to override the paste in the context menu. the only way i could figure out was by
                // removing and adding. our action makes sure to close the model and close it's editor
                // (if necessary) prior to pasting.

                if (menu.find(ECLIPSE_PASTE_ID) != null) {
                    IAction pasteAction = getActionService().getAction(PasteInResourceAction.class);
                    menu.insertAfter(ECLIPSE_PASTE_ID, pasteAction);
                    menu.remove(ECLIPSE_PASTE_ID);
                }

                // override the rename in the context menu same as delete.

                if (menu.find(ECLIPSE_RENAME_ID) != null) {
                    menu.insertAfter(ECLIPSE_RENAME_ID, this.renameAction);
                    menu.remove(ECLIPSE_RENAME_ID);
                    this.renameAction.selectionChanged(getSelection());
                }

                // override the move in the context menu same as delete.

                if (menu.find(ECLIPSE_MOVE_ID) != null) {
                    menu.insertAfter(ECLIPSE_MOVE_ID, this.moveAction);
                    menu.remove(ECLIPSE_MOVE_ID);
                    this.moveAction.selectionChanged(selection);
                }

                // override the properties dialog action in the context menu.
                // unfortunately the default action does not use the ID setup in IWorkbenchConstants:-(
                // so can't use the menu.find(id) method
                // so first time the action is found set it's ID
                if (menu.find(ActionFactory.PROPERTIES.getId()) == null) {
                    IContributionItem[] items = menu.getItems();
                    IContributionItem oldItem = null;

                    // loop backwards since the item we're looking for is always at/near the bottom of the menu
                    for (int i = (items.length - 1); i >= 0; i--) {
                        if (items[i] instanceof ActionContributionItem) {
                            IAction action = ((ActionContributionItem)items[i]).getAction();

                            if (action instanceof org.eclipse.ui.dialogs.PropertyDialogAction) {
                                action.setId(ActionFactory.PROPERTIES.getId());
                                oldItem = items[i];
                                break;
                            }
                        }
                    }

                    // since the contribution id is set the action's id at construction, setting the
                    // action id above does not affect the contribution id. so the find done below will
                    // not work. so have to do a remove and add here.
                    if (oldItem != null) {
                        menu.remove(oldItem);
                        menu.add(this.propertyAction);
                    }
                }

                if (menu.find(ActionFactory.PROPERTIES.getId()) != null) {
                    menu.insertAfter(ActionFactory.PROPERTIES.getId(), this.propertyAction);
                    menu.remove(ActionFactory.PROPERTIES.getId());
                    this.propertyAction.selectionChanged(selection);
                }

            } catch (CoreException theException) {
                Util.log(theException);
            }

            // Let's set up/insert our Insert markers

            if (menu.find(OpenFileAction.ID) != null) {
                menu.insertBefore(OpenFileAction.ID, new GroupMarker(ContextMenu.INSERT_START));
                menu.insertAfter(ContextMenu.INSERT_START, new Separator(ContextMenu.INSERT_END));
            }
            // if single selection and model resource add new child menu
            if (SelectionUtilities.isSingleSelection(selection)) {
                Object obj = SelectionUtilities.getSelectedObject(selection);

                if ((obj instanceof IResource) && ModelUtilities.isModelFile((IResource)obj)) {
                    MenuManager newChildMenu = getActionService().getInsertChildMenu(selection);
                    getActionService().contributePermanentActionsToContextMenu(newChildMenu, selection);

                    // insert menu after New submenu.
                    // the new submenu doesn't have an ID so put it before the open action
                    //
                    // Menu item group for insert child and sibling
                    //
                    menu.insertAfter(ContextMenu.INSERT_START, newChildMenu);
                }
            }

            // add group for model related actions. this group is added to by actions in the manifest
            // Example actions are close model and rebuild imports.
            menu.insertBefore(ActionFactory.IMPORT.getId(), new GroupMarker(ContextMenu.MODEL_START));
            menu.insertBefore(ActionFactory.IMPORT.getId(), new Separator(ContextMenu.MODEL_START));
            menu.insertAfter(ContextMenu.MODEL_START, new GroupMarker(ContextMenu.MODEL_END));

            // Combine ModelResourceActions & Special ModelObject actions into a Modeling Menu
            MenuManager modelingActionMenu = getModelingActionMenu(selection);

            if (modelingActionMenu != null && modelingActionMenu.getItems().length > 0) {
                menu.insertBefore(ContextMenu.INSERT_END, modelingActionMenu);
            }
        }

        // if single selection and ANY resource add refactor menu
        if (SelectionUtilities.isSingleSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);

            if (obj instanceof IResource) {
                MenuManager refactorMenu = getActionService().getRefactorMenu(selection);

                // insert menu at end of the cut/copy/paste group
                if (refactorMenu != null) {
                    // find the location. Default to the end of the whole
                    // context menu.
                    if (menu.find(ECLIPSE_RENAME_ID) != null) {
                        menu.insertAfter(ECLIPSE_RENAME_ID, refactorMenu);
                    } else {
                        menu.insertBefore(ContextMenu.ADDITIONS, refactorMenu);
                    }
                }

                // remove the 'other' rename and move
                if (menu.find(ECLIPSE_RENAME_ID) != null) {
                    menu.remove(ECLIPSE_RENAME_ID);
                }

                // override the move in the context menu same as delete.
                if (menu.find(ECLIPSE_MOVE_ID) != null) {
                    menu.remove(ECLIPSE_MOVE_ID);
                }

            }
            // Add Remove project action if selection is IProject and it is closed
            if (obj instanceof IProject) {
                if (!((IProject)obj).isOpen()) {
                    menu.insertBefore(ContextMenu.ADDITIONS, removeProjectAction);
                } else {
                    if (DotProjectUtils.isModelerProject((IProject)obj)) {
                        menu.insertBefore(ContextMenu.ADDITIONS, cloneProjectAction);
                    }
                }
            }
        }
    }

    /**
     * @return the modeler action service (never <code>null</code>)
     */
    private ModelerActionService getActionService() {
        return (ModelerActionService)UiPlugin.getDefault().getActionService(getWorkbenchSite().getPage());
    }

    /**
     * @param selection the Model Explorer's current selection
     * @return the modeling action sub-menu for a given selection
     */
    private MenuManager getModelingActionMenu( ISelection selection ) {
        MenuManager menu = new MenuManager(MODELING_LABEL, ModelerActionBarIdManager.getModelingMenuId());

        // add special model object actions
        MenuManager actionMenu = ModelerSpecialActionManager.getModeObjectSpecialActionMenu(selection);

        if ((actionMenu != null) && (actionMenu.getItems().length > 0)) {
            for (IContributionItem item : actionMenu.getItems()) {
                menu.add(item);
            }

            menu.add(new Separator());
        }

        // add special model resource actions
        actionMenu = ModelResourceActionManager.getModelResourceActionMenu(selection);

        if ((actionMenu != null) && (actionMenu.getItems().length > 0)) {
            for (IContributionItem item : actionMenu.getItems()) {
                menu.add(item);
            }

            menu.add(new Separator());
        }

        return menu;
    }

    CommonNavigator getNavigator() {
        return (CommonNavigator)getWorkbenchSite().getPart();
    }

    /**
     * @return the preference store
     */
    IPreferenceStore getPreferenceStore() {
        return UiPlugin.getDefault().getPreferenceStore();
    }

    /**
     * @return the current selection in the Model Explorer's viewer
     */
    private IStructuredSelection getSelection() {
        return (IStructuredSelection)getViewer().getSelection();
    }

    TreeViewer getViewer() {
        assert (getActionSite().getStructuredViewer() instanceof TreeViewer) : "This class should only be used in the ModelExplorer"; //$NON-NLS-1$
        return (TreeViewer)getActionSite().getStructuredViewer();
    }

    ICommonViewerWorkbenchSite getWorkbenchSite() {
        ICommonViewerSite site = getActionSite().getViewSite();
        assert (site instanceof ICommonViewerWorkbenchSite) : "This class should only be used in the ModelExplorer"; //$NON-NLS-1$
        return (ICommonViewerWorkbenchSite)site;
    }

    protected void handleKeyPressed( KeyEvent event ) {
        if (event.stateMask != 0) {
            return;
        }

        if (event.keyCode == SWT.F2) {
            // rename action
            /*
             * ModelerActionService fix for defect 12372: if selection is a single IResource, use the refactor rename instead
             */
            ISelection selection = getViewer().getSelection();

            // if single selection see if it is an IResource
            if (SelectionUtilities.isSingleSelection(selection)) {
                Object obj = SelectionUtilities.getSelectedObject(selection);

                // if single selection and ANY resource use refactor rename
                if (obj instanceof IResource) {
                    // rename
                    IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
                    IActionDelegate delRefactorRename = new RenameRefactorAction();
                    IAction refactorRenameAction = new DelegatableAction(delRefactorRename, window);
                    refactorRenameAction.setText(""); //$NON-NLS-1$
                    refactorRenameAction.setToolTipText(""); //$NON-NLS-1$
                    delRefactorRename.selectionChanged(refactorRenameAction, selection);

                    if (refactorRenameAction.isEnabled()) {
                        refactorRenameAction.run();
                    }
                } else if ((this.renameAction != null) && this.renameAction.isEnabled()) {
                    // if single, but not an IResource, use the normal Rename
                    this.renameAction.run();
                }
            } else if ((this.renameAction != null) && this.renameAction.isEnabled()) {
                // if not single, use the normal Rename
                this.renameAction.run();
            }
        } else if (event.character == SWT.DEL) {
            // delete action
            try {
                IAction deleteAction = null;
                ISelection selection = getSelection();

                if (SelectionUtilities.isAllEObjects(selection)) {
                    deleteAction = getActionService().getAction(ActionFactory.DELETE.getId());
                } else {
                    deleteAction = getWorkbenchSite().getActionBars().getGlobalActionHandler(ActionFactory.DELETE.getId());
                }

                if ((deleteAction != null) && deleteAction.isEnabled()) {
                    deleteAction.run();
                    event.doit = false;
                }
            } catch (CoreException e) {
                Util.log(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
     */
    @Override
    public void init( ICommonActionExtensionSite actionSite ) {
        super.init(actionSite);

        // register global actions
        final IWorkbenchWindow wdw = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
        final ModelerActionService svc = getActionService();
        final IActionBars bars = getWorkbenchSite().getActionBars();

        // MUST construct this action before registering default actions in order to cache the default eclipse copy action. if
        // this is done after the registering default actions then there is no way to get the ResourceNavigators default copy
        // action.
        this.copyAction = new ModelExplorerCopyAction(bars, svc);
        // register to receive workspace selection events in order to swap out copy actions
        wdw.getSelectionService().addSelectionListener(this.copyAction);

        this.removeProjectAction = new RemoveProjectAction();
        // register to receive workspace selection events in order to swap out copy actions
        wdw.getSelectionService().addSelectionListener(this.removeProjectAction);

        this.cloneProjectAction = new CloneProjectAction2();
        // register to receive workspace selection events in order to swap out copy actions
        wdw.getSelectionService().addSelectionListener(this.cloneProjectAction);

        svc.registerDefaultGlobalActions(bars);

        this.actionsMap = new ModelerGlobalActionsMap();

        try {
            bars.setGlobalActionHandler(EclipseGlobalActions.COPY, this.copyAction);

            // rename action
            this.renameAction = new ModelNavigatorRenameAction(wdw, getViewer());
            this.renameAction.selectionChanged((IStructuredSelection)getViewer().getSelection()); // initial tree selection
            this.actionsMap.put(EclipseGlobalActions.RENAME, this.renameAction);
            bars.setGlobalActionHandler(EclipseGlobalActions.RENAME, this.renameAction);

            final IAction deleteAction = svc.getAction(DeleteResourceAction.class);
            bars.setGlobalActionHandler(ActionFactory.DELETE.getId(), deleteAction);

            final IAction pasteAction = svc.getAction(PasteInResourceAction.class);
            bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), pasteAction);

            // move action
            this.moveAction = new ModelNavigatorMoveAction(wdw, getViewer());
            bars.setGlobalActionHandler(ActionFactory.MOVE.getId(), this.moveAction);

            bars.updateActionBars();
        } catch (final CoreException e) {
            Util.log(e);
            WidgetUtil.showError(createRenameActionErrorMessage);
        }

        // used in context menu to replace default action
        this.propertyAction = new PropertyDialogAction(getViewer().getControl(), getViewer());
        bars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(), this.propertyAction);

        registerListeners();
    }

    /**
     * @param selection the selection being checked
     * @return <code>true</code> if all selected objects are {@link IExtendedModelObject}s
     */
    private boolean isAllExtendedModelObjects( ISelection selection ) {
        if (SelectionUtilities.isEmptySelection(selection)) {
            return false;
        }

        for (Object obj : SelectionUtilities.getSelectedObjects(selection)) {
            if (!(obj instanceof IExtendedModelObject)) {
                return false;
            }
        }

        return true;
    }

    private void registerListeners() {
        final TreeViewer viewer = getViewer();

        KeyListener keyListener = new KeyListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
             */
            @Override
            public void keyPressed( KeyEvent event ) {
                handleKeyPressed(event);
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
             */
            @Override
            public void keyReleased( KeyEvent event ) {
                // nothing to do
            }
        };
        viewer.getControl().addKeyListener(keyListener);

        // register to listen for Change Notifications
        this.notificationHandler = new ModelNavigatorNotificationHandler(viewer, getNavigator());
        ModelUtilities.addNotifyChangedListener(this.notificationHandler);

        this.selectionListener = new ISelectionListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
             *      org.eclipse.jface.viewers.ISelection)
             */
            @Override
            public void selectionChanged( IWorkbenchPart part,
                                          final ISelection selection ) {
                if ((part != getNavigator()) && getNavigator().isLinkingEnabled()) {
                    if ((selection instanceof IStructuredSelection) && !selection.isEmpty()) {
                        int nObj = ((IStructuredSelection)selection).size();
                        int nEObj = SelectionUtilities.getSelectedEObjects(selection).size();

                        if (nObj == nEObj) {
                            viewer.setSelection(selection, true);
                        } else {
                            // Defect 23541: Newly created models were not getting selected in the tree
                            if ((nObj == 1) && (((IStructuredSelection)selection).getFirstElement() instanceof IResource)) {
                                Runnable runnable = new Runnable() {
                                    /**
                                     * {@inheritDoc}
                                     * 
                                     * @see java.lang.Runnable#run()
                                     */
                                    @Override
                                    public void run() {
                                        if (!viewer.getControl().isDisposed()) {
                                            viewer.setSelection(selection, true);

                                            // do a refresh if the viewer selection is
                                            // empty in order to force
                                            // the treeitems to be created.
                                            if (viewer.getSelection().isEmpty()) {
                                                ITreeContentProvider cp = (ITreeContentProvider)viewer.getContentProvider();
                                                Object parent = cp.getParent(((IStructuredSelection)selection).getFirstElement());

                                                if (parent == null) {
                                                    viewer.refresh(true);
                                                } else {
                                                    viewer.refresh(parent, true);
                                                }

                                                // set selection one more time
                                                viewer.setSelection(selection, true);
                                            }
                                        }
                                    }
                                };

                                // do an async here to ensure the resource treeitem has been created first
                                viewer.getControl().getDisplay().asyncExec(runnable);
                            }
                        }
                    }
                }
            }
        };

        // register a part listener to refresh resource icons when ModelEditors open/close
        // use my page, not the active page:
        this.partListener = new IPartListener() {
            private void checkResource( final IWorkbenchPart part ) {
                if (part instanceof ModelEditor) {
                    Runnable runnable = new Runnable() {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see java.lang.Runnable#run()
                         */
                        @Override
                        public void run() {
                            if (!viewer.getTree().isDisposed()) {
                                viewer.refresh(((ModelEditor)part).getModelFile());
                            }
                        }
                    };

                    Display.getCurrent().asyncExec(runnable);
                }
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.ui.IPartListener#partActivated(org.eclipse.ui.IWorkbenchPart)
             */
            @Override
            public void partActivated( IWorkbenchPart part ) {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.ui.IPartListener#partBroughtToTop(org.eclipse.ui.IWorkbenchPart)
             */
            @Override
            public void partBroughtToTop( IWorkbenchPart part ) {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.ui.IPartListener#partClosed(org.eclipse.ui.IWorkbenchPart)
             */
            @Override
            public void partClosed( IWorkbenchPart part ) {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.ui.IPartListener#partDeactivated(org.eclipse.ui.IWorkbenchPart)
             */
            @Override
            public void partDeactivated( IWorkbenchPart part ) {
                // nothing to do
            }

            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.ui.IPartListener#partOpened(org.eclipse.ui.IWorkbenchPart)
             */
            @Override
            public void partOpened( IWorkbenchPart part ) {
                checkResource(part);
            }
        };

        getWorkbenchSite().getPage().addPartListener(this.partListener);

        this.markerListener = new IResourceChangeListener() {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
             */
            @Override
            public void resourceChanged( IResourceChangeEvent event ) {
                final IMarkerDelta[] deltas = event.findMarkerDeltas(null, true);

                if ((deltas != null) && (deltas.length > 0)) {
                    final Set<IProject> projects = new HashSet<IProject>(deltas.length);

                    for (IMarkerDelta delta : deltas) {
                        projects.add(delta.getResource().getProject());
                    }

                    Runnable runnable = new Runnable() {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see java.lang.Runnable#run()
                         */
                        @Override
                        public void run() {
                            if (!viewer.getTree().isDisposed()) {

                                for (IProject project : projects) {
                                    viewer.refresh(project, true);
                                }
                            }
                        }
                    };

                    Display.getDefault().asyncExec(runnable);
                }
            }
        };

        ResourcesPlugin.getWorkspace().addResourceChangeListener(this.markerListener);

        this.modelResourceListener = new EventObjectListener() {
            /**
             * {@inheritDoc}
             * 
             * @see com.metamatrix.core.event.EventObjectListener#processEvent(java.util.EventObject)
             */
            @Override
            public void processEvent( EventObject obj ) {
                ModelResourceEvent event = (ModelResourceEvent)obj;
                final IResource file = event.getResource();

                if (event.getType() == ModelResourceEvent.CLOSING) {
                    Runnable runnable = new Runnable() {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see java.lang.Runnable#run()
                         */
                        @Override
                        public void run() {
                            if (!viewer.getTree().isDisposed()) {
                                viewer.collapseToLevel(file, AbstractTreeViewer.ALL_LEVELS);
                            }
                        }
                    };

                    Display.getDefault().asyncExec(runnable);
                } else if (event.getType() == ModelResourceEvent.CLOSED) {
                    Runnable runnable = new Runnable() {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see java.lang.Runnable#run()
                         */
                        @Override
                        public void run() {
                            if (!viewer.getTree().isDisposed()) {
                                viewer.remove(file);
                                viewer.refresh(file.getParent(), false);
                            }
                        }
                    };

                    Display.getDefault().asyncExec(runnable);
                } else if (event.getType() == ModelResourceEvent.RELOADED) {
                    Runnable runnable = new Runnable() {
                        /**
                         * {@inheritDoc}
                         * 
                         * @see java.lang.Runnable#run()
                         */
                        @Override
                        public void run() {
                            if (!viewer.getTree().isDisposed()) {
                                viewer.refresh(file.getParent(), false);
                            }
                        }
                    };

                    Display.getDefault().asyncExec(runnable);
                }
            }
        };

        try {
            UiPlugin.getDefault().getEventBroker().addListener(ModelResourceEvent.class, modelResourceListener);
        } catch (EventSourceException e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    /**
     * The <code>ModelExplorerCopyAction</code> delegates the copying to either the default Eclipse ResourceNavigator copy action or
     * to the EObject copy action based on workspace selection. Must be constructed before global actions are overwritten.
     */
    class ModelExplorerCopyAction extends Action implements ISelectionListener {

        private IAction currentAction = null; // current action based on selection
        private IAction defaultAction = null; // default eclipse copy action
        private IAction modelerAction = null; // eobject copy action

        public ModelExplorerCopyAction( IActionBars theActionBars,
                                        ActionService theService ) {
            // cache the eclipse resource copy action
            this.defaultAction = theActionBars.getGlobalActionHandler(EclipseGlobalActions.COPY);

            if (this.defaultAction == null) {
                this.defaultAction = GlobalActionsMap.UNSUPPORTED_ACTION;
                Util.log(IStatus.ERROR, defaultCopyActionNotFoundMessage);
            }

            // cache the EObject copy action
            try {
                this.modelerAction = theService.getAction(EclipseGlobalActions.COPY);
            } catch (CoreException theException) {
                Util.log(theException);
            }

            if (this.modelerAction == null) {
                this.modelerAction = GlobalActionsMap.UNSUPPORTED_ACTION;
            }

            // initialize state
            this.currentAction = defaultAction;
            setEnabled(this.currentAction.isEnabled());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.action.Action#run()
         */
        @Override
        public void run() {
            this.currentAction.runWithEvent(new Event());
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
         *      org.eclipse.jface.viewers.ISelection)
         */
        @Override
        public void selectionChanged( IWorkbenchPart thePart,
                                      ISelection theSelection ) {
            IAction handler = (SelectionUtilities.isAllEObjects(theSelection)) ? this.modelerAction : this.defaultAction;

            // switch handlers if necessary
            if (this.currentAction != handler) {
                this.currentAction = handler;
            }

            setEnabled(this.currentAction.isEnabled());
        }
    }

    class ShowImportsAction extends Action {
        public ShowImportsAction() {
            super(showImportsActionText, IAction.AS_CHECK_BOX);
            this.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.IMPORT_CONTAINER));

            // set initial state based on preference
            IPreferenceStore store = getPreferenceStore();
            boolean prefValue = true;

            if (store.contains(SHOW_IMPORTS_IN_MODEL_EXPLORER)) {
                prefValue = store.getBoolean(SHOW_IMPORTS_IN_MODEL_EXPLORER);
            } else {
                prefValue = store.getDefaultBoolean(SHOW_IMPORTS_IN_MODEL_EXPLORER);
            }

            setChecked(prefValue);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.action.Action#run()
         */
        @Override
        public void run() {
            // update preference value
            IPreferenceStore store = getPreferenceStore();
            store.setValue(SHOW_IMPORTS_IN_MODEL_EXPLORER, isChecked());

            // refresh viewer
            getViewer().refresh();
        }
    }

    class ShowNonModelsAction extends Action {

        private final ViewerFilter filter = new NonModelViewerFilter();

        public ShowNonModelsAction() {
            super(showNonModelsActionText, IAction.AS_CHECK_BOX);

            // set initial state based on preference
            IPreferenceStore store = getPreferenceStore();
            boolean prefValue = true;

            if (store.contains(SHOW_NON_MODELS_IN_MODEL_EXPLORER)) {
                prefValue = store.getBoolean(SHOW_NON_MODELS_IN_MODEL_EXPLORER);
            } else {
                prefValue = store.getDefaultBoolean(SHOW_NON_MODELS_IN_MODEL_EXPLORER);
            }

            setChecked(prefValue);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.action.Action#run()
         */
        @Override
        public void run() {
            // update preference value
            IPreferenceStore store = getPreferenceStore();
            store.setValue(SHOW_NON_MODELS_IN_MODEL_EXPLORER, isChecked());

            // refresh viewer
            if (isChecked()) {
                getViewer().removeFilter(this.filter);
            } else {
                getViewer().addFilter(this.filter);
            }
        }

    }

    class SortModelContentsAction extends Action {
        public SortModelContentsAction() {
            super(sortModelContentsActionText, IAction.AS_CHECK_BOX);
            this.setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.ALPHA_SORT_ICON));

            // set initial state based on preference
            IPreferenceStore store = getPreferenceStore();
            boolean prefValue = true;

            if (store.contains(SORT_MODEL_CONTENTS)) {
                prefValue = store.getBoolean(SORT_MODEL_CONTENTS);
            } else {
                prefValue = store.getDefaultBoolean(SORT_MODEL_CONTENTS);
            }

            setChecked(prefValue);
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.jface.action.Action#run()
         */
        @Override
        public void run() {
            // update preference value
            IPreferenceStore store = getPreferenceStore();
            store.setValue(SORT_MODEL_CONTENTS, isChecked());

            // refresh viewer
            getViewer().refresh();
        }
    }

}
