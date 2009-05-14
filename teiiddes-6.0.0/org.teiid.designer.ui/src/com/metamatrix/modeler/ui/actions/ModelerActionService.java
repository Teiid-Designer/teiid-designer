/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.ItemProvider;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelImport;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.actions.BookmarkAction;
import com.metamatrix.modeler.internal.ui.actions.CopyAction;
import com.metamatrix.modeler.internal.ui.actions.CutAction;
import com.metamatrix.modeler.internal.ui.actions.DeleteAction;
import com.metamatrix.modeler.internal.ui.actions.FindAction;
import com.metamatrix.modeler.internal.ui.actions.NewAssociationAction;
import com.metamatrix.modeler.internal.ui.actions.NewChildAction;
import com.metamatrix.modeler.internal.ui.actions.NewSiblingAction;
import com.metamatrix.modeler.internal.ui.actions.PasteAction;
import com.metamatrix.modeler.internal.ui.actions.PrintAction;
import com.metamatrix.modeler.internal.ui.actions.RedoAction;
import com.metamatrix.modeler.internal.ui.actions.RenameAction;
import com.metamatrix.modeler.internal.ui.actions.SelectAllAction;
import com.metamatrix.modeler.internal.ui.actions.UndoAction;
import com.metamatrix.modeler.internal.ui.editors.MultiPageModelEditor;
import com.metamatrix.modeler.internal.ui.refactor.RefactorUndoManager;
import com.metamatrix.modeler.internal.ui.refactor.actions.MoveRefactorAction;
import com.metamatrix.modeler.internal.ui.refactor.actions.NamespaceUriRefactorAction;
import com.metamatrix.modeler.internal.ui.refactor.actions.RedoRefactoringAction;
import com.metamatrix.modeler.internal.ui.refactor.actions.RenameRefactorAction;
import com.metamatrix.modeler.internal.ui.refactor.actions.UndoRefactoringAction;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.IEditorActionExporter;
import com.metamatrix.modeler.ui.editors.ModelObjectEditorPage;
import com.metamatrix.modeler.ui.product.IModelerProductContexts;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.AbstractActionService;
import com.metamatrix.ui.actions.GlobalActionsMap;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * The <code>ModelerActionService</code> class is the Modeler Plugin's action service. It is responsible for managing all actions
 * for this plugin.
 */
public final class ModelerActionService extends AbstractActionService
    implements IModelerActionConstants, UiConstants.ExtensionPoints.ModelObjectActionContributor, IModelerRcpActionIds,
    IModelerProductContexts {

    /** The logging prefix. */
    private static final String PREFIX = "ModelerActionService."; //$NON-NLS-1$
    public static final String MODELING_LABEL = UiConstants.Util.getString("ModelerSpecialActionManager.specialLabel"); //$NON-NLS-1$

    /** A list version of <code>IModelerActionConstants.EclipseGlobalActions.ALL_ACTIONS</code>. */
    private static final List ECLIPSE_GLOBAL_ACTION_IDS;

    /**
     * The action identifiers for the eclipse global actions. Used in the <code>getAction</code> method.
     * <p>
     * <strong>Must be in the same order as in <code>IModelerActionConstants.EclipseGlobalActions.ALL_ACTIONS</code>.</strong>
     */
    private static final List MAPPED_ACTION_IDS;

    private static final String DELETE_ID = ActionFactory.DELETE.getId();

    static {
        ECLIPSE_GLOBAL_ACTION_IDS = Arrays.asList(EclipseGlobalActions.ALL_ACTIONS);

        //
        // must keep these in order with IModelerActionConstants.EclipseGlobalActions.ALL_ACTIONS
        //
        String[] ACTION_CLASSES = new String[] {UndoAction.class.getName(), RedoAction.class.getName(),
            CutAction.class.getName(), CopyAction.class.getName(), PasteAction.class.getName(), PrintAction.class.getName(),
            DeleteAction.class.getName(), FindAction.class.getName(), SelectAllAction.class.getName(),
            BookmarkAction.class.getName(), RenameAction.class.getName()};

        MAPPED_ACTION_IDS = Arrays.asList(ACTION_CLASSES);
    }

    /** Collection of <code>IModelObjectActionContributors</code>. */
    private List modelObjectContributors;

    /** Map of default global actions. This map actually contains action for each action identifier. */
    private ModelerGlobalActionsMap defaultActionsMap;

    /** The modeler plugin. */
    private AbstractUiPlugin plugin;

    /** Utilities used for logging and localization. */
    private PluginUtil utils;

    /** Array of all extensions to the NewChildAction extension point */
    private INewChildAction[] newChildExtensions;

    /** Array of all extensions to the NewSiblingAction extension point */
    private INewSiblingAction[] newSiblingExtensions;

    /** The undo manager for this window's refactor actions */
    private RefactorUndoManager refactorUndoManager;

    private IContributionItem[] cachedEditMenuItems;

    /**
     * Constructs a <code>ModelerActionService</code> associated with the given <code>IWorkbenchWindow</code>.
     * 
     * @param theWindow the associated workbench window
     */
    public ModelerActionService( IWorkbenchPage page ) {
        this(UiPlugin.getDefault(), UiPlugin.getDefault().getPluginUtil(), page);
    }

    public ModelerActionService( AbstractUiPlugin plugin,
                                 PluginUtil utility,
                                 IWorkbenchPage page ) {
        super(plugin, page);

        this.plugin = plugin;
        utils = utility;

        // Need to wire any Special Actions to selection listener
        ModelerSpecialActionManager.wireActionsForSelection(this);
    }

    /**
     * Adds the given listener to the list receiving workspace container {@link org.eclipse.emf.common.notify.Notification}s. This
     * is a pass-through helper method to the {@link com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities} method.
     * 
     * @param listener the listener being added
     */
    public void addNotifyChangedListener( INotifyChangedListener theListener ) {
        if (Util.isTraceEnabled(this)) {
            Util.printEntered(this, new StringBuffer().append("addNotifyChangedListener(INotifyChangedListener):Listener=") //$NON-NLS-1$
            .append(theListener).append(", listener identity=") //$NON-NLS-1$
            .append(System.identityHashCode(theListener)).toString());
        }

        ModelUtilities.addNotifyChangedListener(theListener);

        if (Util.isTraceEnabled(this)) {
            Util.printExited(this, new StringBuffer().append("addNotifyChangedListener(INotifyChangedListener):Listener=") //$NON-NLS-1$
            .append(theListener).append(", listener identity=") //$NON-NLS-1$
            .append(System.identityHashCode(theListener)).toString());
        }
    }

    /**
     * Gives the <code>IModelObjectActionContributor</code>s a chance to contribute to the context menu
     * 
     * @param theMenuMgr the context menu being contributed to
     * @param theSelection the current selection
     */
    @Override
    public void contributePermanentActionsToContextMenu( IMenuManager theMenuMgr,
                                                         ISelection theSelection ) {
        List contributors = getModelObjectActionContributors();

        for (int size = contributors.size(), i = 0; i < size; i++) {
            IModelObjectActionContributor contributor = (IModelObjectActionContributor)modelObjectContributors.get(i);
            contributor.contributeToContextMenu(theMenuMgr, theSelection);
        }
    }

    /**
     * Gives the <code>IModelObjectActionContributor</code>s a chance to contribute to the Edit menu
     * 
     * @param theMenuMgr the Edit menu
     * @param theSelection the current selection
     */
    @Override
    public void contributePermanentActionsToEditMenu( IMenuManager theMenuMgr,
                                                      ISelection theSelection ) {
        List contributors = getModelObjectActionContributors();

        for (int size = contributors.size(), i = 0; i < size; i++) {
            IModelObjectActionContributor contributor = (IModelObjectActionContributor)modelObjectContributors.get(i);
            contributor.contributeToEditMenu(theMenuMgr, theSelection);
        }
    }

    /**
     * Fills the given menu with the standard items appropriate for a context menu of a part in the Modeler.
     * 
     * @param theMenuMgr the menu being contributed to (should be a context menu)
     * @param theActionsMap the map of global action handlers
     * @param theSelection the selection used to determine how to build the new child and new sibling menus
     */
    @Override
    public void contributeToContextMenu( IMenuManager theMenuMgr,
                                         GlobalActionsMap theActionsMap,
                                         ISelection theSelection ) {
        if (utils.isTraceEnabled(this)) {
            utils.print(this,
                        new StringBuffer().append("contributeToContextMenu(theMenuMgr, ModelerGlobalActionsMap, ISelection):") //$NON-NLS-1$
                        .append("Menu id") //$NON-NLS-1$
                        .append(theMenuMgr.getId()).append(", selected obj=") //$NON-NLS-1$
                        .append(SelectionUtilities.getSelectedObject(theSelection)).toString());
        }

        // if current page has global actions put them in here
        // if not, use default action
        GlobalActionsMap actionsMap = (theActionsMap == null) ? new ModelerGlobalActionsMap() : theActionsMap;

        //
        // Menu item group for insert child and sibling
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.INSERT_START));

        if (isSubmenuSupported(ID_INSERT_CHILD_MENU)) {
            theMenuMgr.add(getInsertChildMenu(theSelection));
        }

        if (isSubmenuSupported(ID_INSERT_SIBLING_MENU)) {
            theMenuMgr.add(getInsertSiblingMenu(theSelection));
        }

        if (isSubmenuSupported(ID_INSERT_ASSOCIATION_MENU)) {
            theMenuMgr.add(getCreateAssociationMenu(theSelection));
        }

        MenuManager modelingActionMenu = getModelingActionMenu(theSelection);
        if (modelingActionMenu != null && modelingActionMenu.getItems().length > 0) {
            theMenuMgr.add(modelingActionMenu);
        }

        theMenuMgr.add(new GroupMarker(ContextMenu.INSERT_END));
        theMenuMgr.add(new Separator());

        //
        // Menu item group for undo and redo
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.UNDO_START));

        if (isActionSupported(ActionValues.ID_UNDO_ACTION)) {
            IAction undoAction = getAction(EclipseGlobalActions.UNDO, actionsMap);
            theMenuMgr.add(undoAction);

            if (undoAction instanceof IMenuListener) {
                ((IMenuListener)undoAction).menuAboutToShow(theMenuMgr);
            }
        }

        if (isActionSupported(ActionValues.ID_REDO_ACTION)) {
            IAction redoAction = getAction(EclipseGlobalActions.REDO, actionsMap);
            theMenuMgr.add(redoAction);

            if (redoAction instanceof IMenuListener) {
                ((IMenuListener)redoAction).menuAboutToShow(theMenuMgr);
            }
        }

        theMenuMgr.add(new GroupMarker(ContextMenu.UNDO_END));

        theMenuMgr.add(new Separator());

        //
        // Menu item group for cut, copy, paste, clone, copyFullName, and copyName
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.CUT_START));

        if (isActionSupported(ActionValues.ID_CUT_ACTION)) {
            theMenuMgr.add(getAction(EclipseGlobalActions.CUT, actionsMap));
        }

        if (isActionSupported(ActionValues.ID_COPY_ACTION)) {
            theMenuMgr.add(getAction(EclipseGlobalActions.COPY, actionsMap));
        }

        if (isActionSupported(ActionValues.ID_PASTE_ACTION)) {
            theMenuMgr.add(getAction(EclipseGlobalActions.PASTE, actionsMap));
        }

        if (isActionSupported(ActionValues.ID_CLONE_ACTION)) {
            theMenuMgr.add(getAction(ModelerGlobalActions.CLONE, actionsMap));
        }

        MenuManager copyNameMenu = getCopyNameSubMenu(actionsMap);

        if (!copyNameMenu.isEmpty()) {
            theMenuMgr.add(copyNameMenu);
        }

        theMenuMgr.add(new GroupMarker(ContextMenu.CUT_END));
        theMenuMgr.add(new Separator());

        //
        // Menu item group for delete, rename
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.DELETE_START));

        if (isActionSupported(ActionValues.ID_DELETE_ACTION)) {
            theMenuMgr.add(getAction(EclipseGlobalActions.DELETE, actionsMap));
        }

        if (isActionSupported(ActionValues.ID_RENAME_ACTION)) {
            theMenuMgr.add(getAction(EclipseGlobalActions.RENAME, actionsMap));
        }

        theMenuMgr.add(new GroupMarker(ContextMenu.DELETE_END));
        theMenuMgr.add(new Separator());

        //
        // Menu item group for open, edit
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.OPEN_START));

        if (isActionSupported(ActionValues.ID_OPEN_ACTION)) {
            theMenuMgr.add(getAction(ModelerGlobalActions.OPEN, actionsMap));
        }

        if (isActionSupported(ActionValues.ID_EDIT_ACTION)) {
            theMenuMgr.add(getAction(ModelerGlobalActions.EDIT, actionsMap));
        }

        theMenuMgr.add(new GroupMarker(ContextMenu.OPEN_END));
        theMenuMgr.add(new Separator());

        // Let any model object actions contribute at this time

        contributePermanentActionsToContextMenu(theMenuMgr, theSelection);

        //
        // Group to allow adding to the context menu
        //
        theMenuMgr.add(new Separator(ContextMenu.ADDITIONS));

    }

    void cleanupEditMenu( IMenuManager theEditMenu ) {
        String id = ModelerActionBarIdManager.getInsertStartMarkerId();

        if (theEditMenu.find(id) != null) {
            theEditMenu.remove(id);
        }

        id = ModelerActionBarIdManager.getInsertChildMenuId();

        if (theEditMenu.find(id) != null) {
            theEditMenu.remove(id);
        }

        id = ModelerActionBarIdManager.getInsertSiblingMenuId();

        if (theEditMenu.find(id) != null) {
            theEditMenu.remove(id);
        }

        id = ModelerActionBarIdManager.getInsertAssociationMenuId();

        if (theEditMenu.find(id) != null) {
            theEditMenu.remove(id);
        }

        id = ModelerActionBarIdManager.getModelingMenuId();

        if (theEditMenu.find(id) != null) {
            theEditMenu.remove(id);
        }

        id = ModelerActionBarIdManager.getInsertEndMarkerId();

        if (theEditMenu.find(id) != null) {
            theEditMenu.remove(id);
        }

        id = EclipseGlobalActions.RENAME;

        if (theEditMenu.find(id) != null) {
            theEditMenu.remove(id);
        }
    }

    void restoreEditInsertGroup( IMenuManager theEditMenu,
                                 ISelection theSelection ) {
        String insertStartId = ModelerActionBarIdManager.getInsertStartMarkerId();

        theEditMenu.insertBefore(ModelerActionBarIdManager.getEditMenuStartMarkerId(), new GroupMarker(insertStartId));
        theEditMenu.appendToGroup(insertStartId, getInsertChildMenu(theSelection));
        theEditMenu.appendToGroup(insertStartId, getInsertSiblingMenu(theSelection));
        theEditMenu.appendToGroup(insertStartId, getCreateAssociationMenu(theSelection));

        MenuManager modelingActionMenu = getModelingActionMenu(theSelection);
        if (modelingActionMenu != null && modelingActionMenu.getItems().length > 0) {
            theEditMenu.appendToGroup(insertStartId, modelingActionMenu);
        }

        theEditMenu.appendToGroup(insertStartId, new GroupMarker(ModelerActionBarIdManager.getInsertEndMarkerId()));
    }

    /**
     * Obtains the Edit Menu.
     * 
     * @return the edit menu
     * @since 4.4
     */
    public IMenuManager getEditMenu() {
        IMenuManager menuMgr = getActionBars().getMenuManager();
        return menuMgr.findMenuUsingPath(ModelerActionBarIdManager.getEditMenuId());
    }

    /**
     * Obtains the Validate Menu.
     * 
     * @return the menu or <code>null</code> if not found
     * @since 5.0
     */
    public IMenuManager getValidateMenu() {
        IMenuManager menuMgr = getActionBars().getMenuManager();
        return menuMgr.findMenuUsingPath(ModelerActionBarIdManager.getValidateMenuId());
    }

    /**
     * Obtains the File Menu.
     * 
     * @return the menu or <code>null</code> if not found
     * @since 5.0
     */
    public IMenuManager getFileMenu() {
        IMenuManager menuMgr = getActionBars().getMenuManager();
        return menuMgr.findMenuUsingPath(ModelerActionBarIdManager.getFileMenuId());
    }

    /**
     * Called once to add items to the edit menu.
     */
    private void contributeToEditMenu() {
        IMenuManager tempEditMenu = getEditMenu();
        cachedEditMenuItems = new IContributionItem[tempEditMenu.getItems().length];
        for (int i = 0; i < tempEditMenu.getItems().length; i++) {
            cachedEditMenuItems[i] = tempEditMenu.getItems()[i];
        }
        final IMenuManager editMenu = tempEditMenu;

        // only allow services from this plugin to be a menu listener
        if (getPlugin().getBundle().getSymbolicName().equals(UiConstants.PLUGIN_ID)) {
            // add the insert child and insert sibling menu item group
            editMenu.addMenuListener(new IMenuListener() {
                public void menuAboutToShow( IMenuManager manager ) {
                    createEditMenu(editMenu);
                }
            });

            // hook up undo/redo actions to be edit menu listeners
            IMenuListener action;

            try {
                action = (IMenuListener)getAction(UndoAction.class.getName());
                editMenu.addMenuListener(action);
            } catch (CoreException e) {
                utils.log(IStatus.ERROR, e, utils.getString(PREFIX + "actionProblem")); //$NON-NLS-1$
            }

            try {
                action = (IMenuListener)getAction(RedoAction.class.getName());
                editMenu.addMenuListener(action);
            } catch (CoreException e) {
                utils.log(IStatus.ERROR, e, utils.getString(PREFIX + "actionProblem")); //$NON-NLS-1$
            }
        }
    }

    void createEditMenu( IMenuManager editMenu ) {
        cleanupEditMenu(editMenu);

        // get current selection
        restoreEditInsertGroup(editMenu, getWorkbenchWindow().getSelectionService().getSelection());

        editMenu.insertAfter(ModelerActionBarIdManager.getInsertEndMarkerId(), new Separator());

        // Eclipse considers the global actions within IWorkbenchActionConstants.GLOBAL_ACTIONS to be dynamic
        // based upon
        // the active part. Any additional global actions that the Modeler wants to treat as dynamic in the same
        // manner
        // must be replaced below by the respective global action handler set on the current part's action bars.

        // Get the action bars for the active part
        final IWorkbenchPart part = getWorkbenchWindow().getActivePage().getActivePart();
        IActionBars bars = null;
        if (part instanceof IViewPart) {
            bars = ((IViewPart)part).getViewSite().getActionBars();
        } else if (part instanceof IEditorPart) {
            bars = ((IEditorPart)part).getEditorSite().getActionBars();
        }

        if (isActionSupported(ActionValues.ID_RENAME_ACTION)) {
            // Add the current rename action if necessary
            IAction action = bars.getGlobalActionHandler(EclipseGlobalActions.RENAME);

            if (action != null) {
                // make sure ID is EclipseGlobalActions.RENAME so it can be removed in cleanup section above
                action.setId(EclipseGlobalActions.RENAME);
                editMenu.insertAfter(DELETE_ID, action);
            }
        }

        try {
            // if supported by the product, insert the paste special and clones action after the paste
            String pasteId = ActionFactory.PASTE.getId();

            if (isActionSupported(ActionValues.ID_CLONE_ACTION) && (editMenu.find(ModelerGlobalActions.CLONE) == null)) {
                editMenu.insertAfter(pasteId, getAction(ModelerGlobalActions.CLONE));
            }

            if (isActionSupported(ActionValues.ID_PASTE_SPECIAL_ACTION)
                && (editMenu.find(ModelerGlobalActions.PASTE_SPECIAL) == null)) {
                editMenu.insertAfter(pasteId, getAction(ModelerGlobalActions.PASTE_SPECIAL));
            }

            // create the open, edit menu items
            String openId = ModelerActionBarIdManager.getOpenGroupStartMarkerId();

            if (editMenu.find(ModelerGlobalActions.PASTE_SPECIAL) == null) {
                editMenu.insertAfter(DELETE_ID, new GroupMarker(openId));
            }
            // create the open, edit menu items
            if (UiPlugin.getDefault().isProductContextSupported(Product.IDE_APPLICATION)) {
                if (editMenu.find(openId) == null) {
                    // Add the open group id
                    editMenu.insertAfter(DELETE_ID, new GroupMarker(openId));
                }
            }

            if (isActionSupported(ActionValues.ID_OPEN_ACTION) && (editMenu.find(ModelerGlobalActions.OPEN) == null)) {
                editMenu.appendToGroup(openId, getAction(ModelerGlobalActions.OPEN));
            }

            if (isActionSupported(ActionValues.ID_EDIT_ACTION) && (editMenu.find(ModelerGlobalActions.EDIT) == null)) {
                editMenu.appendToGroup(openId, getAction(ModelerGlobalActions.EDIT));
            }

            if (editMenu.find(ModelerActionBarIdManager.getOpenGroupExtrasMarkerId()) == null) {
                editMenu.appendToGroup(openId, new Separator(ModelerActionBarIdManager.getOpenGroupExtrasMarkerId()));
            }
        } catch (CoreException theException) {
            utils.log(IStatus.ERROR, theException, utils.getString(PREFIX + "actionProblem")); //$NON-NLS-1$
        }

        getActionBars().updateActionBars();
    }

    /**
     * @see com.metamatrix.ui.actions.AbstractActionService#shutdown()
     * @since 5.5.3
     */
    @Override
    public void shutdown() {
        // unhook undo/redo
        IMenuManager editMenu = getEditMenu();

        if (editMenu != null) {
            IMenuListener action;

            try {
                action = (IMenuListener)getAction(UndoAction.class.getName());
                editMenu.removeMenuListener(action);
            } catch (CoreException e) {
                utils.log(IStatus.ERROR, e, utils.getString(PREFIX + "actionProblem")); //$NON-NLS-1$
            }

            try {
                action = (IMenuListener)getAction(RedoAction.class.getName());
                editMenu.removeMenuListener(action);
            } catch (CoreException e) {
                utils.log(IStatus.ERROR, e, utils.getString(PREFIX + "actionProblem")); //$NON-NLS-1$
            }
        }

        super.shutdown();
    }

    /**
     * Indicates if the specified {@link IModelerProductContexts.ActionValues} is supported by the current product.
     * 
     * @param theProductContextActionValue the value being checked
     * @return <code>true</code> if supported; <code>false</code> otherwise.
     * @since 4.4
     */
    boolean isActionSupported( String theProductContextActionValue ) {
        return this.plugin.isProductContextValueSupported(IModelerProductContexts.Actions.ACTION, theProductContextActionValue);
    }

    private boolean isSubmenuSupported( String theProductContextSubmenuValue ) {
        return this.plugin.isProductContextValueSupported(IModelerProductContexts.Actions.SUB_MENU, theProductContextSubmenuValue);
    }

    /**
     * Indicates if the preconditions needed to insert a child have not been met. If one of the following is <code>true</code>,
     * the preconditions have not been met:
     * <ul>
     * <li>Selection is null,
     * <li>Selection is empty,
     * <li>Selection is a multi-selection, or
     * <li>Selected object is not an EObject or not a model.
     * </ul>
     * 
     * @param theSelection the selection being checked
     * @return <code>true</code> if insert preconditions have not been met; <code>false</code> otherwise.
     */
    private boolean failedInsertChildPreconditions( ISelection theSelection ) {
        boolean result = (theSelection == null) || theSelection.isEmpty() || SelectionUtilities.isMultiSelection(theSelection);
        if (utils.isTraceEnabled(this)) {
            utils.print(this,
                        new StringBuffer().append("failedInsertChildPreconditions(ISelection):Null selection=") //$NON-NLS-1$
                        .append((theSelection == null)).append(", empty selection=") //$NON-NLS-1$
                        .append(theSelection.isEmpty()).append(", multi-selection=") //$NON-NLS-1$
                        .append(SelectionUtilities.isMultiSelection(theSelection)).append(", selected obj=" + SelectionUtilities.getSelectedObject(theSelection)) //$NON-NLS-1$
                        .toString());
        }

        if (!result) {
            Object obj = SelectionUtilities.getSelectedObject(theSelection);

            // sometimes an ItemProvider gets displayed; defer to its parent
            if (obj instanceof ItemProvider) {
                obj = ((ItemProvider)obj).getParent();
            }

            if (utils.isTraceEnabled(this)) {
                StringBuffer msg = new StringBuffer().append("failedInsertChildPreconditions(ISelection):Is Resource=") //$NON-NLS-1$
                .append((obj instanceof IResource)).append(", is EObject=") //$NON-NLS-1$
                .append((obj instanceof EObject)).append(", is IResource=") //$NON-NLS-1$
                .append((obj instanceof IResource)).append(", selected obj=") //$NON-NLS-1$
                .append(obj);
                if (obj instanceof IResource) {
                    msg.append("is model=").append(ModelUtilities.isModelFile((IResource)obj)); //$NON-NLS-1$
                }

                utils.print(this, msg.toString());
            }

            if ((obj instanceof IResource) && ModelUtilities.isModelFile((IResource)obj)) {
                result = false;
            } else if (obj instanceof Diagram) {
                result = true;
            } else if (obj instanceof EObject) {
                result = false;
                if (obj instanceof ModelImport || obj instanceof JdbcSource || obj instanceof JdbcImportSettings) {
                    result = true;
                }
            } else {
                result = true;
            }
        }

        return result;
    }

    /**
     * Indicates if the preconditions needed to insert a sibling have not been met. If one of the following is <code>true</code>,
     * the preconditions have not been met:
     * <ul>
     * <li>Selection is null,
     * <li>Selection is empty,
     * <li>Selection is a multi-selection, or
     * <li>Selected object is not an EObject.
     * </ul>
     * 
     * @param theSelection the selection being checked
     * @return <code>true</code> if insert preconditions have not been met; <code>false</code> otherwise.
     */
    private boolean failedInsertSiblingPreconditions( ISelection theSelection ) {
        boolean failed = false;
        if (utils.isTraceEnabled(this)) {
            utils.print(this,
                        new StringBuffer().append("failedInsertSiblingPreconditions(ISelection):Null selection=") //$NON-NLS-1$
                        .append((theSelection == null)).append(", empty selection=") //$NON-NLS-1$
                        .append(theSelection.isEmpty()).append(", multi-selection=") //$NON-NLS-1$
                        .append(SelectionUtilities.isMultiSelection(theSelection)).append(", selected EObj=" + SelectionUtilities.getSelectedEObject(theSelection)) //$NON-NLS-1$
                        .toString());
        }
        failed = ((theSelection == null) || theSelection.isEmpty() || SelectionUtilities.isMultiSelection(theSelection) || (SelectionUtilities.getSelectedEObject(theSelection) == null));

        if (!failed) {
            EObject eObj = SelectionUtilities.getSelectedEObject(theSelection);
            if (eObj != null) {
                if (eObj instanceof Diagram) {
                    Diagram diagram = (Diagram)SelectionUtilities.getSelectedEObject(theSelection);
                    EObject target = diagram.getTarget();
                    // disallow for root level diagrams, ModelImport, and JDBC Import objects
                    if (target == null || target instanceof ModelAnnotation) {
                        failed = true;
                    }
                } else if (eObj instanceof ModelImport || eObj instanceof JdbcSource || eObj instanceof JdbcImportSettings) {
                    failed = true;
                }
            }
        }

        return failed;
    }

    /**
     * Indicates if the preconditions needed to create an Association have not been met. If one of the following is
     * <code>true</code>, the preconditions have not been met:
     * <ul>
     * <li>Selection is null,
     * <li>Selection is empty,
     * <li>Selection is not multi-selection, or
     * <li>Selected objects are not all EObjects or not a model. , or
     * <li>Selected objects all have the same parent, or
     * <li>Selected objects have more than 2 parents.
     * </ul>
     * 
     * @param theSelection the selection being checked
     * @return <code>true</code> if insert preconditions have not been met; <code>false</code> otherwise.
     */
    private boolean failedCreateAssociationPreconditions( ISelection theSelection ) {
        boolean result = (theSelection == null) || theSelection.isEmpty() || SelectionUtilities.isSingleSelection(theSelection);
        if (utils.isTraceEnabled(this)) {
            utils.print(this, new StringBuffer().append("failedCreateAssociationPreconditions(ISelection):Null selection=") //$NON-NLS-1$
            .append((theSelection == null)).append(", empty selection=") //$NON-NLS-1$
            .append(theSelection.isEmpty()).append(", multi-selection=") //$NON-NLS-1$
            .append(SelectionUtilities.isMultiSelection(theSelection)).toString());
        }

        if (!result) {
            List objects = SelectionUtilities.getSelectedObjects(theSelection);

            if (utils.isTraceEnabled(this)) {
                StringBuffer msg = new StringBuffer();
                for (Iterator iter = objects.iterator(); iter.hasNext();) {
                    Object obj = iter.next();
                    msg.append("failedCreateAssociationPreconditions(ISelection): selection = " + obj.toString()) //$NON-NLS-1$
                    .append("Is Resource=") //$NON-NLS-1$
                    .append((obj instanceof IResource)).append(", is EObject=") //$NON-NLS-1$
                    .append((obj instanceof EObject)).append(", is IResource=") //$NON-NLS-1$
                    .append((obj instanceof IResource)).append(", selected obj=") //$NON-NLS-1$
                    .append(obj);
                    if (obj instanceof IResource) {
                        msg.append("is model=").append(ModelUtilities.isModelFile((IResource)obj)); //$NON-NLS-1$
                    }
                }

                utils.print(this, msg.toString());
            }

            for (Iterator iter = objects.iterator(); iter.hasNext();) {
                Object obj = iter.next();
                if ((obj instanceof IResource) && !ModelUtilities.isModelFile((IResource)obj)) {
                    result = true;
                    break;
                } else if (obj instanceof Diagram) {
                    result = true;
                } else if (obj instanceof EObject) {
                    result = false;
                } else {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Indicates if the preconditions needed to refactor have not been met. If one of the following is <code>true</code>, the
     * preconditions have not been met:
     * <ul>
     * <li>Selection is null,
     * <li>Selection is empty,
     * <li>Selection is a multi-selection, or
     * <li>Selected object is not an IResource
     * </ul>
     * 
     * @param theSelection the selection being checked
     * @return <code>true</code> if refactor preconditions have not been met; <code>false</code> otherwise.
     */
    private boolean failedRefactorPreconditions( ISelection theSelection ) {

        // fail if null, empty or multi
        boolean result = (theSelection == null) || theSelection.isEmpty() || SelectionUtilities.isMultiSelection(theSelection);

        if (utils.isTraceEnabled(this)) {
            utils.print(this,
                        new StringBuffer().append("failedRefactorPreconditions(ISelection):Null selection=") //$NON-NLS-1$
                        .append((theSelection == null)).append(", empty selection=") //$NON-NLS-1$
                        .append(theSelection.isEmpty()).append(", multi-selection=") //$NON-NLS-1$
                        .append(SelectionUtilities.isMultiSelection(theSelection)).append(", selected obj=" + SelectionUtilities.getSelectedObject(theSelection)) //$NON-NLS-1$
                        .toString());
        }

        if (!result) {
            Object obj = SelectionUtilities.getSelectedObject(theSelection);

            if (utils.isTraceEnabled(this)) {
                StringBuffer msg = new StringBuffer().append("failedRefactorPreconditions(ISelection):Is Resource=") //$NON-NLS-1$
                .append((obj instanceof IResource)).append(", is EObject=") //$NON-NLS-1$
                .append((obj instanceof EObject)).append(", is IResource=") //$NON-NLS-1$
                .append((obj instanceof IResource)).append(", selected obj=") //$NON-NLS-1$
                .append(obj);
                if (obj instanceof IResource) {
                    msg.append("is model=").append(ModelUtilities.isModelFile((IResource)obj)); //$NON-NLS-1$
                }

                utils.print(this, msg.toString());
            }

            // fail if NOT an IResource
            if (!(obj instanceof IResource)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * @see com.metamatrix.ui.actions.AbstractActionService#getAction(String)
     */
    @Override
    public IAction getAction( String theActionId ) throws CoreException {
        if (utils.isTraceEnabled(this)) {
            utils.print(this, new StringBuffer().append("getAction(String):Before action id=") //$NON-NLS-1$
            .append(theActionId).append(", after action id=") //$NON-NLS-1$
            .append(getActionId(theActionId)).toString());
        }

        // registering actions to receive events is done by the getAction(Class) method
        return super.getAction(getActionId(theActionId));
    }

    /**
     * If the key represents an Eclipse global action a second lookup is needed to get the actual action class name which is
     * needed by the action service to construct the action. For non-Eclipse global action, the key is already the action class
     * name.
     * 
     * @param theKey the action identifier whose "real" action identifier is being requested
     * @return the action identifier used by the action service
     */
    public String getActionId( String theKey ) {
        if (utils.isTraceEnabled(this)) {
            utils.print(this, new StringBuffer().append("getActionId(String):Key=") //$NON-NLS-1$
            .append(theKey).append(", is Eclipse action=") //$NON-NLS-1$
            .append(GlobalActionsMap.isEclipseGlobalAction(theKey)).toString());
        }

        String result = theKey;

        if (GlobalActionsMap.isEclipseGlobalAction(theKey)) {
            int index = ECLIPSE_GLOBAL_ACTION_IDS.indexOf(theKey);
            result = (String)MAPPED_ACTION_IDS.get(index);
        }

        return result;
    }

    /**
     * Gets the default action for the given identifier. The set of default action identifiers can be found by combining
     * {@link com.metamatrix.modeler.ui.actions.IModelerActionConstants.ModelerGlobalActions} and
     * {@link com.metamatrix.modeler.ui.actions.IModelerActionConstants.EclipseGlobalActions}.
     * 
     * @param theActionId the identifier of the default action being requested
     * @return the default action or <code>null</code> if not found
     */
    public IAction getDefaultAction( String theActionId ) {
        return defaultActionsMap.getAction(theActionId);
    }

    /**
     * Gets an insert child menu appropriate for the given input parameter.
     * 
     * @param theSelection the selection whose insert child menu is being requested
     * @return the insert child menu
     */
    public MenuManager getInsertChildMenu( ISelection theSelection ) {
        MenuManager menu = new MenuManager(utils.getString(PREFIX + "NewChildMenu.title"), //$NON-NLS-1$
                                           ModelerActionBarIdManager.getInsertChildMenuId());

        if (failedInsertChildPreconditions(theSelection)) {
            menu.add(new NewChildAction());
        } else {
            // object will either be an EObject or a model since it passed preconditions
            Object obj = SelectionUtilities.getSelectedObject(theSelection);

            try {
                // descriptors are Commands
                Collection descriptors = null;

                // sometimes an ItemProvider gets displayed; defer to its parent
                if (obj instanceof ItemProvider) {
                    obj = ((ItemProvider)obj).getParent();
                }

                if (obj instanceof EObject) {
                    boolean isReadOnly = ModelObjectUtilities.isReadOnly((EObject)obj);
                    // get the allowable child types
                    // Defect 19197 - If obj instanceof Import Container, don't get descriptors
                    descriptors = ModelerCore.getModelEditor().getNewChildCommands((EObject)obj);
                    if ((descriptors == null) || (descriptors.isEmpty())) {
                        // there are none - make an empty NewChildAction
                        menu.add(new NewChildAction());
                    } else {
                        // we need to sort the actions alphabetically, so put them all in a HashMap
                        HashMap actionMap = new HashMap();
                        // create a NewChildAction for every new child type
                        Iterator iter = descriptors.iterator();
                        while (iter.hasNext()) {
                            NewChildAction action = new NewChildAction((EObject)obj, (Command)iter.next());
                            actionMap.put(action.getText(), action);
                            action.selectionChanged(getWorkbenchWindow().getPartService().getActivePart(), theSelection);

                            // disable if read-only. obj should be EObject or an IResource.
                            if (isReadOnly) {
                                action.setEnabled(false);
                            }
                        }

                        // sort the keys of the actionMap to put them in alphabetical order
                        TreeSet set = new TreeSet(actionMap.keySet());
                        iter = set.iterator();
                        while (iter.hasNext()) {
                            // add each action to the menu
                            menu.add((IAction)actionMap.get(iter.next()));
                        }

                        // get the NewChildAction extensions and populate with any actions
                        // Note: these should not be sorted - they always go on the bottom of the menu
                        for (int i = 0; i < getNewChildExtensions().length; ++i) {
                            INewChildAction action = getNewChildExtensions()[i];
                            if (action.canCreateChild((EObject)obj)) {
                                menu.add(action);
                                if (isReadOnly) {
                                    action.setEnabled(false);
                                }
                            }
                        }
                    }
                } else if (obj instanceof IFile) {
                    try {
                        // models are files
                        ModelResource modelResource = ModelUtilities.getModelResource((IFile)obj, false);
                        if (modelResource != null && !modelResource.hasErrors()) {
                            Resource resource = modelResource.getEmfResource();
                            descriptors = ModelerCore.getModelEditor().getNewRootObjectCommands(resource);
                            boolean isReadOnly = ModelUtil.isIResourceReadOnly((IResource)obj);

                            if ((descriptors == null) || (descriptors.isEmpty())) {
                                menu.add(new NewChildAction());
                            } else {
                                // we need to sort the actions alphabetically, so put them all in a HashMap
                                HashMap actionMap = new HashMap();
                                Iterator iter = descriptors.iterator();
                                while (iter.hasNext()) {
                                    NewChildAction action = new NewChildAction(resource, (Command)iter.next());
                                    actionMap.put(action.getText(), action);
                                    action.selectionChanged(getWorkbenchWindow().getPartService().getActivePart(), theSelection);

                                    // disable if read-only. obj should be EObject or an IResource.
                                    if (isReadOnly) {
                                        action.setEnabled(false);
                                    }
                                }

                                // sort the keys of the actionMap to put them in alphabetical order
                                TreeSet set = new TreeSet(actionMap.keySet());
                                iter = set.iterator();
                                while (iter.hasNext()) {
                                    // add each action to the menu
                                    menu.add((IAction)actionMap.get(iter.next()));
                                }

                            }
                        } else {
                            menu.add(new NewChildAction());
                        }
                    } catch (ModelWorkspaceException theException) {
                        utils.log(IStatus.ERROR, theException, utils.getString(PREFIX + "NewChildMenu.problem", //$NON-NLS-1$
                                                                               new Object[] {theSelection}));
                    }
                }

            } catch (ModelerCoreException theException) {
                utils.log(IStatus.ERROR, theException, utils.getString(PREFIX + "NewChildMenu.problem", //$NON-NLS-1$
                                                                       new Object[] {theSelection}));
            }
        }

        return menu;
    }

    /**
     * Gets an insert sibling menu appropriate for the given input parameter.
     * 
     * @param theSelection the selection whose insert sibling menu is being requested
     * @return the insert sibling menu
     */
    public MenuManager getInsertSiblingMenu( ISelection theSelection ) {
        MenuManager menu = new MenuManager(utils.getString(PREFIX + "NewSiblingMenu.title"), //$NON-NLS-1$
                                           ModelerActionBarIdManager.getInsertSiblingMenuId());

        if (failedInsertSiblingPreconditions(theSelection)) {
            menu.add(new NewSiblingAction());
        } else {
            // should be guaranteed to have an EObject at this point
            EObject eObj = SelectionUtilities.getSelectedEObject(theSelection);
            if (eObj instanceof Diagram) {
                eObj = ((Diagram)eObj).getTarget();
            }

            boolean isReadOnly = ModelObjectUtilities.isReadOnly(eObj);

            try {
                // descriptors are Commands
                Collection commands = ModelerCore.getModelEditor().getNewSiblingCommands(eObj);

                if ((commands != null) && (!commands.isEmpty())) {

                    // we need to sort the actions alphabetically, so put them all in a HashMap
                    HashMap actionMap = new HashMap();
                    Iterator iter = commands.iterator();

                    while (iter.hasNext()) {
                        Command cmd = (Command)iter.next();
                        NewSiblingAction action = new NewSiblingAction(eObj, cmd);
                        actionMap.put(action.getText(), action);
                        action.selectionChanged(getWorkbenchWindow().getPartService().getActivePart(), theSelection);
                        // disable if read-only
                        if (isReadOnly) {
                            action.setEnabled(false);
                        }
                    }

                    // sort the keys of the actionMap to put them in alphabetical order
                    TreeSet set = new TreeSet(actionMap.keySet());
                    iter = set.iterator();
                    while (iter.hasNext()) {
                        // add each action to the menu
                        menu.add((IAction)actionMap.get(iter.next()));
                    }

                    // get the NewSiblingAction extensions and populate with any actions
                    // Note: these should not be sorted - they always go on the bottom of the menu
                    for (int i = 0; i < getNewSiblingExtensions().length; ++i) {
                        INewSiblingAction action = getNewSiblingExtensions()[i];
                        if (action.canCreateSibling(eObj)) {
                            menu.add(action);
                            if (isReadOnly) {
                                action.setEnabled(false);
                            }
                        }
                    }

                } else {
                    menu.add(new NewSiblingAction());
                }
            } catch (ModelerCoreException theException) {
                utils.log(IStatus.ERROR, theException, utils.getString(PREFIX + "NewSiblingMenu.problem", //$NON-NLS-1$
                                                                       new Object[] {theSelection}));
            }
        }

        return menu;
    }

    /**
     * Gets a create association menu appropriate for the given input parameter.
     * 
     * @param theSelection the selection whose insert sibling menu is being requested
     * @return the insert sibling menu
     */
    public MenuManager getCreateAssociationMenu( ISelection theSelection ) {
        MenuManager menu = new MenuManager(utils.getString(PREFIX + "NewAssociationMenu.title"), //$NON-NLS-1$
                                           ModelerActionBarIdManager.getInsertAssociationMenuId());

        if (failedCreateAssociationPreconditions(theSelection)) {
            menu.add(new NewAssociationAction());
        } else {

            try {
                // association descriptors are AssociationDescriptor objects

                List selectedEObjects = SelectionUtilities.getSelectedEObjects(theSelection);
                Collection descriptors = ModelerCore.getModelEditor().getNewAssociationDescriptors(selectedEObjects);

                if ((descriptors != null) && (!descriptors.isEmpty())) {

                    // disable if read-only
                    boolean isReadOnly = false;
                    for (Iterator iter = selectedEObjects.iterator(); iter.hasNext();) {
                        if (ModelObjectUtilities.isReadOnly((EObject)iter.next())) {
                            isReadOnly = true;
                            break;
                        }
                    }

                    int index = 0;
                    for (Iterator iter = descriptors.iterator(); iter.hasNext();) {
                        AssociationDescriptor ad = (AssociationDescriptor)iter.next();

                        if (ad.isAmbiguous()) {
                            MenuManager submenu = new MenuManager(ad.getText());
                            String groupName = "associationSubmenuGroup" + (index++); //$NON-NLS-1$
                            menu.insert(0, new GroupMarker(groupName));
                            menu.appendToGroup(groupName, submenu);
                            AssociationDescriptor[] children = ad.getChildren();
                            for (int i = 0; i < children.length; ++i) {
                                NewAssociationAction action = new NewAssociationAction(children[i]);
                                submenu.add(action);
                                action.selectionChanged(getWorkbenchWindow().getPartService().getActivePart(), theSelection);
                            }
                        } else {
                            IAction action = new NewAssociationAction(ad);
                            menu.add(action);

                            if (isReadOnly) {
                                action.setEnabled(false);
                            }

                        }

                    }

                } else {
                    menu.add(new NewAssociationAction());
                }
            } catch (ModelerCoreException theException) {
                utils.log(IStatus.ERROR, theException, utils.getString(PREFIX + "NewAssociationMenu.problem", //$NON-NLS-1$
                                                                       new Object[] {theSelection}));
            }
        }

        return menu;
    }

    // convenience method to get exported modeling actions from ModelEditor and multi-page editors.
    private List getExportedActions( ISelection selection ) {
        List expActions = new ArrayList();
        //
        // If current editor is a IEditorActionExporter, get it's exported actions
        //
        IEditorPart editor = getWorkbenchWindow().getActivePage().getActiveEditor();
        if (editor instanceof IEditorActionExporter) {
            List newActions = ((IEditorActionExporter)editor).getAdditionalModelingActions(selection);
            if (!newActions.isEmpty()) {
                expActions.addAll(newActions);
            }
        }

        //
        // Check for ModelObject Editor and get it's exported actions
        //
        if (editor instanceof MultiPageModelEditor) {
            ModelObjectEditorPage moep = ((MultiPageModelEditor)editor).getActiveObjectEditor();
            if (moep != null) {
                List newActions = moep.getAdditionalModelingActions(selection);
                if (!newActions.isEmpty()) {
                    expActions.addAll(newActions);
                }
            }
        }

        return expActions;
    }

    /**
     * Allows access to a full modeling action menu based on a supplied selection
     * 
     * @param theSelection
     * @return
     * @since 5.0
     */
    public MenuManager getModelingActionMenu( ISelection theSelection ) {
        MenuManager menu = new MenuManager(MODELING_LABEL, ModelerActionBarIdManager.getModelingMenuId()); 

        MenuManager mosaMenu = ModelerSpecialActionManager.getModeObjectSpecialActionMenu(theSelection);
        if (mosaMenu != null && mosaMenu.getItems().length > 0) {
            Object[] items = mosaMenu.getItems();
            for (int i = 0; i < items.length; i++) {
                menu.add(mosaMenu.getItems()[i]);
            }
            menu.add(new Separator());
        }

        List exportedActions = getExportedActions(theSelection);
        if (!exportedActions.isEmpty()) {
            for (int j = 0; j < exportedActions.size(); j++) {
                Object nextItem = exportedActions.get(j);
                if (nextItem instanceof IAction) {
                    menu.add((IAction)nextItem);
                } else if (nextItem instanceof ActionContributionItem) {
                    menu.add((ActionContributionItem)nextItem);
                }
            }
            menu.add(new Separator());
        }

        MenuManager mraMenu = ModelResourceActionManager.getModelResourceActionMenu(theSelection);

        if (mraMenu != null && mraMenu.getItems().length > 0) {
            Object[] items = mraMenu.getItems();
            for (int i = 0; i < items.length; i++) {
                menu.add(mraMenu.getItems()[i]);
            }
            menu.add(new Separator());
        }

        // get permanent action contributors

        List contributors = getModelObjectActionContributors();
        boolean addedActions = false;
        for (int size = contributors.size(), i = 0; i < size; i++) {
            IModelObjectActionContributor contributor = (IModelObjectActionContributor)modelObjectContributors.get(i);
            List additionalActions = contributor.getAdditionalModelingActions(theSelection);
            if (!additionalActions.isEmpty()) {
                for (int j = 0; j < additionalActions.size(); j++) {
                    if (!addedActions) {
                        addedActions = true;
                    }
                    menu.add((IAction)additionalActions.get(j));
                }
            }
        }
        if (addedActions) {
            menu.add(new Separator());
        }

        return menu;
    }

    /**
     * Gets an refactor menu appropriate for the given input parameter.
     * 
     * @param theSelection the selection whose insert child menu is being requested
     * @return the insert child menu
     */
    public MenuManager getRefactorMenu( ISelection theSelection ) {
        //        System.out.println("[ModelerActionService.getRefactorMenu] top");  //$NON-NLS-1$

        MenuManager menu = new MenuManager(utils.getString(PREFIX + "RefactorMenu.title"), //$NON-NLS-1$
                                           ModelerActionBarIdManager.getRefactorMenuId());

        if (failedRefactorPreconditions(theSelection)) {
            // will result in returning a disabled 'Refactor' submenu

        } else {
            // object must be an IResource since it passed preconditions
            IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

            String sRenameLabel = utils.getString("com.metamatrix.modeler.internal.ui.refactor.actions.RenameRefactorAction.text"); //$NON-NLS-1$
            String sRenameTooltip = utils.getString("com.metamatrix.modeler.internal.ui.refactor.actions.RenameRefactorAction.tooltip"); //$NON-NLS-1$

            String uriLabel = utils.getString("com.metamatrix.modeler.internal.ui.refactor.actions.NamespaceUriRefactorAction.text"); //$NON-NLS-1$
            String uriToolTip = utils.getString("com.metamatrix.modeler.internal.ui.refactor.actions.NamespaceUriRefactorAction.toolTip"); //$NON-NLS-1$

            String sMoveLabel = utils.getString("com.metamatrix.modeler.internal.ui.refactor.actions.MoveRefactorAction.text"); //$NON-NLS-1$
            String sMoveTooltip = utils.getString("com.metamatrix.modeler.internal.ui.refactor.actions.MoveRefactorAction.tooltip"); //$NON-NLS-1$

            try {
                // undo
                if (isActionSupported(ActionValues.ID_REFACTOR_UNDO_ACTION)) {
                    IActionDelegate delUndo = new UndoRefactoringAction();
                    IAction actUndo = new DelegatableAction(delUndo, window);
                    delUndo.selectionChanged(actUndo, theSelection);
                    menu.add(actUndo);
                }

                // redo
                if (isActionSupported(ActionValues.ID_REFACTOR_REDO_ACTION)) {
                    IActionDelegate delRedo = new RedoRefactoringAction();
                    IAction actRedo = new DelegatableAction(delRedo, window);
                    delRedo.selectionChanged(actRedo, theSelection);
                    menu.add(actRedo);
                }

                // separator
                menu.add(new GroupMarker(ContextMenu.UNDO_END));
                menu.add(new Separator());
                menu.add(new GroupMarker("reorgStart")); //$NON-NLS-1$

                // rename
                IActionDelegate delRename = new RenameRefactorAction();
                IAction actRename = new DelegatableAction(delRename, window);
                actRename.setText(sRenameLabel);
                actRename.setToolTipText(sRenameTooltip);
                delRename.selectionChanged(actRename, theSelection);
                menu.add(actRename);

                // rename Namespace URI
                NamespaceUriRefactorAction delNamespace = new NamespaceUriRefactorAction();
                IAction namespaceRenameAction = new DelegatableAction(delNamespace, window);
                namespaceRenameAction.setText(uriLabel);
                namespaceRenameAction.setToolTipText(uriToolTip);
                delNamespace.selectionChanged(namespaceRenameAction, theSelection);
                menu.add(namespaceRenameAction);

                // move
                IActionDelegate delMove = new MoveRefactorAction();
                IAction actMove = new DelegatableAction(delMove, window);
                actMove.setText(sMoveLabel);
                actMove.setToolTipText(sMoveTooltip);
                delMove.selectionChanged(actMove, theSelection);
                actMove.setId(MoveRefactorAction.class.getName());
                menu.add(actMove);

                menu.add(new GroupMarker("reorgEnd")); //$NON-NLS-1$

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return menu;
    }

    /**
     * Gets the Copy Name menu.
     * 
     * @return the Copy Name submenu
     */
    public MenuManager getCopyNameSubMenu( GlobalActionsMap actionsMap ) {
        //        System.out.println("[ModelerActionService.getCopyNameMenu] top");  //$NON-NLS-1$

        MenuManager menu = new MenuManager(utils.getString(PREFIX + "copyNameSubMenu.title")); //$NON-NLS-1$

        if (isActionSupported(ActionValues.ID_COPY_FULL_NAME_ACTION)) {
            menu.add(getAction(ModelerGlobalActions.COPY_FULL_NAME, actionsMap));
        }

        if (isActionSupported(ActionValues.ID_COPY_NAME_ACTION)) {
            menu.add(getAction(ModelerGlobalActions.COPY_NAME, actionsMap));
        }

        return menu;
    }

    /**
     * Gets a list of the extension for the ModelObjectActionContributor extension point.
     * 
     * @return the list of <code>IModelObjectActionContributor</code> implementations
     */
    public List getModelObjectActionContributors() {
        if (modelObjectContributors == null) {
            // get the ModelObjectActionContributor extension point from the plugin class
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, ID);

            // get the all extensions to the ModelObjectActionContributor extension point
            IExtension[] extensions = extensionPoint.getExtensions();

            if (extensions.length > 0) {
                modelObjectContributors = new ArrayList(extensions.length);

                // for each extension get their contributor
                for (int i = 0; i < extensions.length; i++) {
                    IConfigurationElement[] elements = extensions[i].getConfigurationElements();
                    Object extension = null;

                    for (int j = 0; j < elements.length; j++) {
                        try {
                            extension = elements[j].createExecutableExtension(CLASSNAME);

                            if (extension instanceof IModelObjectActionContributor) {
                                modelObjectContributors.add(extension);
                            } else {
                                utils.log(IStatus.ERROR, utils.getString(PREFIX + "wrongContributorClass", //$NON-NLS-1$
                                                                         new Object[] {extension.getClass().getName()}));
                            }
                        } catch (Exception theException) {
                            utils.log(IStatus.ERROR,
                                      theException,
                                      utils.getString(PREFIX + "contributorProblem", //$NON-NLS-1$
                                                      new Object[] {elements[j].getAttribute(CLASSNAME)}));
                        }
                    }
                }
            } else {
                modelObjectContributors = Collections.EMPTY_LIST;
            }
        }

        return modelObjectContributors;
    }

    /**
     * Initializes the <code>Map</code> of all global actions. Must be called after construction to get actions to take the place
     * of the default eclipse actions.
     */
    public void initializeGlobalActions() {
        if (Util.isTraceEnabled(this)) {
            Util.printEntered(this, "initializeGlobalActions()"); //$NON-NLS-1$
        }

        // need to put the real actions into the map as the new constructed map only
        // contains identifiers which stand for "use default" action
        defaultActionsMap = new ModelerGlobalActionsMap();

        // load all default global actions since the map doesn't really have the default actions
        // as values. it just has a marker.
        for (int i = 0; i < ModelerGlobalActionsMap.ALL_GLOBAL_ACTIONS.length; i++) {
            String actionId = ModelerGlobalActionsMap.ALL_GLOBAL_ACTIONS[i];

            try {
                // the call to getAction(String) throws a CoreException if the action cannot be constructed
                // also the call to getAction(String) registers the action to receive appropriate events
                IAction action = getAction(actionId);
                defaultActionsMap.put(actionId, action);

                if (utils.isTraceEnabled(this)) {
                    utils.print(this, new StringBuffer().append("initializeGlobalActions():Action id=") //$NON-NLS-1$
                    .append(action.getId()).append(", action class=") //$NON-NLS-1$
                    .append(action.getClass().getName()) 
                    .toString());
                }
            } catch (CoreException e) {
                String message = UiConstants.Util.getString(PREFIX + "getActionErrorMessage", actionId); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

        // get global actions installed in edit menu
        contributeToEditMenu();

        if (Util.isTraceEnabled(this)) {
            Util.printExited(this, new StringBuffer().append("initializeGlobalActions():Global action count=") //$NON-NLS-1$
            .append(defaultActionsMap.size()).toString());
        }
    }

    /**
     * @see com.metamatrix.ui.actions.AbstractActionService#registerEventHandler(org.eclipse.jface.action.IAction)
     */
    @Override
    protected void registerEventHandler( IAction theAction ) {
        if (Util.isTraceEnabled(this)) {
            Util.printEntered(this, new StringBuffer().append("registerEventHandler(IAction):Action id=") //$NON-NLS-1$
            .append(theAction.getId()).append(", action identity=") //$NON-NLS-1$
            .append(System.identityHashCode(theAction)).append(", is INotifyChangedListener=") //$NON-NLS-1$
            .append(theAction instanceof INotifyChangedListener).toString());
        }

        // first let super register
        super.registerEventHandler(theAction);

        // EObject notifier events
        if (theAction instanceof INotifyChangedListener) {
            addNotifyChangedListener((INotifyChangedListener)theAction);
        }

        if (Util.isTraceEnabled(this)) {
            Util.printExited(this, "registerEventHandler(IAction)"); //$NON-NLS-1$
        }
    }

    /**
     * Registers the Modeler's default global actions for the given <code>IPageSite</code>'s <code>IActionBars</code>.
     * 
     * @param thePageSite the site where the global actions are being registered
     */
    public void registerDefaultGlobalActions( IActionBars bars ) {
        Iterator itr = defaultActionsMap.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry)itr.next();
            bars.setGlobalActionHandler((String)entry.getKey(), (IAction)entry.getValue());
        }
    }

    /**
     * Removes the given listener from receiving workspace container {@link org.eclipse.emf.common.notify.Notification}s. This is
     * a pass-through helper method to the {@link com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities} method.
     * 
     * @param listener the listener being removed
     */
    public void removeNotifyChangedListener( INotifyChangedListener theListener ) {
        if (Util.isTraceEnabled(this)) {
            Util.printEntered(this, new StringBuffer().append("removeNotifyChangedListener(INotifyChangedListener):Listener=") //$NON-NLS-1$
            .append(theListener).append(", listener identity=") //$NON-NLS-1$
            .append(System.identityHashCode(theListener)).toString());
        }

        ModelUtilities.removeNotifyChangedListener(theListener);

        if (Util.isTraceEnabled(this)) {
            Util.printExited(this, new StringBuffer().append("removeNotifyChangedListener(INotifyChangedListener):Listener=") //$NON-NLS-1$
            .append(theListener).append(", listener identity=") //$NON-NLS-1$
            .append(System.identityHashCode(theListener)).toString());
        }
    }

    /**
     * @see com.metamatrix.ui.actions.AbstractActionService#unregisterEventHandler(org.eclipse.jface.action.IAction)
     */
    @Override
    protected void unregisterEventHandler( IAction theAction ) {
        if (Util.isTraceEnabled(this)) {
            Util.printEntered(this, new StringBuffer().append("unregisterEventHandler(IAction):Action id=") //$NON-NLS-1$
            .append(theAction.getId()).append(", action identity=") //$NON-NLS-1$
            .append(System.identityHashCode(theAction)).append(", is INotifyChangedListener=") //$NON-NLS-1$
            .append(theAction instanceof INotifyChangedListener).toString());
        }

        // first let super unregister
        super.unregisterEventHandler(theAction);

        // unregister EObject notifier events
        if (theAction instanceof INotifyChangedListener) {
            removeNotifyChangedListener((INotifyChangedListener)theAction);
        }

        if (Util.isTraceEnabled(this)) {
            Util.printExited(this, "unregisterEventHandler(IAction)"); //$NON-NLS-1$
        }
    }

    private INewChildAction[] getNewChildExtensions() {
        if (newChildExtensions == null) {
            // get the NewChildAction extension point from the plugin class
            String id = UiConstants.ExtensionPoints.NewChildExtension.ID;
            String classTag = UiConstants.ExtensionPoints.NewChildExtension.CLASS;
            String className = UiConstants.ExtensionPoints.NewChildExtension.CLASSNAME;
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, id);
            // get the all extensions to the NewChildAction extension point
            IExtension[] extensions = extensionPoint.getExtensions();

            ArrayList actionList = new ArrayList();
            // walk through the extensions and find all INewChildAction implementations
            for (int i = 0; i < extensions.length; ++i) {
                IConfigurationElement[] elements = extensions[i].getConfigurationElements();
                try {

                    // first, find the content provider instance and add it to the instance list
                    for (int j = 0; j < elements.length; ++j) {
                        if (elements[j].getName().equals(classTag)) {
                            Object action = elements[j].createExecutableExtension(className);
                            if (action instanceof INewChildAction) {
                                actionList.add(action);
                            }
                        }
                    }

                } catch (Exception e) {
                    // catch any Exception that occurred obtaining the configuration and log it
                    String message = UiConstants.Util.getString("ModelerActionService.configurationErrorMessage", //$NON-NLS-1$
                                                                extensions[i].getUniqueIdentifier());
                    UiConstants.Util.log(IStatus.ERROR, e, message);
                }
            }

            newChildExtensions = new INewChildAction[actionList.size()];
            for (int i = 0; i < actionList.size(); ++i) {
                newChildExtensions[i] = (INewChildAction)actionList.get(i);
            }

        }
        return newChildExtensions;
    }

    private INewSiblingAction[] getNewSiblingExtensions() {
        if (newSiblingExtensions == null) {
            // get the NewSiblingAction extension point from the plugin class
            String id = UiConstants.ExtensionPoints.NewSiblingExtension.ID;
            String classTag = UiConstants.ExtensionPoints.NewSiblingExtension.CLASS;
            String className = UiConstants.ExtensionPoints.NewSiblingExtension.CLASSNAME;
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(UiConstants.PLUGIN_ID, id);
            // get the all extensions to the NewSiblingAction extension point
            IExtension[] extensions = extensionPoint.getExtensions();

            ArrayList actionList = new ArrayList();
            // walk through the extensions and find all INewSiblingAction implementations
            for (int i = 0; i < extensions.length; ++i) {
                IConfigurationElement[] elements = extensions[i].getConfigurationElements();
                try {

                    // first, find the content provider instance and add it to the instance list
                    for (int j = 0; j < elements.length; ++j) {
                        if (elements[j].getName().equals(classTag)) {
                            Object action = elements[j].createExecutableExtension(className);
                            if (action instanceof INewSiblingAction) {
                                actionList.add(action);
                            }
                        }
                    }

                } catch (Exception e) {
                    // catch any Exception that occurred obtaining the configuration and log it
                    String message = UiConstants.Util.getString("ModelerActionService.configurationErrorMessage", //$NON-NLS-1$
                                                                extensions[i].getUniqueIdentifier());
                    UiConstants.Util.log(IStatus.ERROR, e, message);
                }
            }

            newSiblingExtensions = new INewSiblingAction[actionList.size()];
            for (int i = 0; i < actionList.size(); ++i) {
                newSiblingExtensions[i] = (INewSiblingAction)actionList.get(i);
            }

        }
        return newSiblingExtensions;
    }

    public RefactorUndoManager getRefactorUndoManager() {
        if (this.refactorUndoManager == null) {
            this.refactorUndoManager = RefactorUndoManager.getInstance();
        }
        return this.refactorUndoManager;
    }

}
