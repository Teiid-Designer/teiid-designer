/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.ui.refactoring.RedoRefactoringAction;
import org.eclipse.ltk.ui.refactoring.UndoRefactoringAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.PluginUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.association.AssociationDescriptor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.jdbc.JdbcImportSettings;
import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelImport;
import org.teiid.designer.metamodels.diagram.Diagram;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.AbstractActionService;
import org.teiid.designer.ui.common.actions.GlobalActionsMap;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.ui.editors.IEditorActionExporter;
import org.teiid.designer.ui.editors.ModelObjectEditorPage;
import org.teiid.designer.ui.editors.MultiPageModelEditor;
import org.teiid.designer.ui.product.IModelerProductContexts;
import org.teiid.designer.ui.refactor.RefactorUndoManager;
import org.teiid.designer.ui.refactor.actions.NamespaceUriRefactorAction;
import org.teiid.designer.ui.refactor.actions.RenameRefactorAction;
import org.teiid.designer.ui.refactor.move.MoveRefactorAction;
import org.teiid.designer.ui.viewsupport.ModelObjectUtilities;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * The <code>ModelerActionService</code> class is the Modeler Plugin's action service. It is responsible for managing all actions
 * for this plugin.
 *
 * @since 8.0
 */
public final class ModelerActionService extends AbstractActionService
    implements IModelerActionConstants, UiConstants.ExtensionPoints.ModelObjectActionContributor, IModelerRcpActionIds,
    IModelerProductContexts {

    /** The logging prefix. */
    private static final String PREFIX = "ModelerActionService."; //$NON-NLS-1$
    
    /**
     * 
     */
    public static final String MODELING_LABEL = UiConstants.Util.getString("ModelerSpecialActionManager.specialLabel"); //$NON-NLS-1$

    /** A list version of <code>IModelerActionConstants.EclipseGlobalActions.ALL_ACTIONS</code>. */
    private static final List ECLIPSE_GLOBAL_ACTION_IDS;

    /**
     * The action identifiers for the eclipse global actions. Used in the <code>getAction</code> method.
     * <p>
     * <strong>Must be in the same order as in <code>IModelerActionConstants.EclipseGlobalActions.ALL_ACTIONS</code>.</strong>
     */
    private static final List MAPPED_ACTION_IDS;

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

    /** Utilities used for logging and localization. */
    private PluginUtil utils;

    /** Array of all extensions to the NewChildAction extension point */
    private INewChildAction[] newChildExtensions;

    /** Array of all extensions to the NewSiblingAction extension point */
    private INewSiblingAction[] newSiblingExtensions;

    /** The undo manager for this window's refactor actions */
    private RefactorUndoManager refactorUndoManager;

    /**
     * Constructs a <code>ModelerActionService</code> associated with the given <code>IWorkbenchWindow</code>.
     * 
     * @param page the associated workbench page
     */
    public ModelerActionService( IWorkbenchPage page ) {
        this(UiPlugin.getDefault(), UiPlugin.getDefault().getPluginUtil(), page);
    }

    /**
     * @param plugin the plugin requesting action service
     * @param utility the plugin logging and i18n utility class
     * @param page the workbench page
     */
    public ModelerActionService( AbstractUiPlugin plugin,
                                 PluginUtil utility,
                                 IWorkbenchPage page ) {
        super(plugin, page);

        utils = utility;

        // Need to wire any Special Actions to selection listener
        ModelerSpecialActionManager.wireActionsForSelection(this);
    }

    /**
     * Adds the given listener to the list receiving workspace container {@link org.eclipse.emf.common.notify.Notification}s. This
     * is a pass-through helper method to the {@link org.teiid.designer.ui.viewsupport.ModelUtilities} method.
     * 
     * @param theListener the listener being added
     */
    public void addNotifyChangedListener( INotifyChangedListener theListener ) {
        ModelUtilities.addNotifyChangedListener(theListener);
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
        // if current page has global actions put them in here
        // if not, use default action
        GlobalActionsMap actionsMap = (theActionsMap == null) ? new ModelerGlobalActionsMap() : theActionsMap;

        //
        // Menu item group for insert child and sibling
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.INSERT_START));
        theMenuMgr.add(getInsertChildMenu(theSelection));

        theMenuMgr.add(getInsertSiblingMenu(theSelection));

        theMenuMgr.add(getCreateAssociationMenu(theSelection));

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

        IAction undoAction = getAction(EclipseGlobalActions.UNDO, actionsMap);
        theMenuMgr.add(undoAction);

        if (undoAction instanceof IMenuListener) {
            ((IMenuListener)undoAction).menuAboutToShow(theMenuMgr);
        }

        IAction redoAction = getAction(EclipseGlobalActions.REDO, actionsMap);
        theMenuMgr.add(redoAction);

        if (redoAction instanceof IMenuListener) {
            ((IMenuListener)redoAction).menuAboutToShow(theMenuMgr);
        }

        theMenuMgr.add(new GroupMarker(ContextMenu.UNDO_END));

        theMenuMgr.add(new Separator());

        //
        // Menu item group for cut, copy, paste, clone, copyFullName, and copyName
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.CUT_START));
        theMenuMgr.add(getAction(EclipseGlobalActions.CUT, actionsMap));
        theMenuMgr.add(getAction(EclipseGlobalActions.COPY, actionsMap));
        theMenuMgr.add(getAction(EclipseGlobalActions.PASTE, actionsMap));
        theMenuMgr.add(getAction(ModelerGlobalActions.CLONE, actionsMap));
            
        MenuManager copyNameMenu = getCopyNameSubMenu(theSelection);

        if (!copyNameMenu.isEmpty()) {
            theMenuMgr.add(copyNameMenu);
        }

        theMenuMgr.add(new GroupMarker(ContextMenu.CUT_END));
        theMenuMgr.add(new Separator());

        //
        // Menu item group for delete, rename
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.DELETE_START));
        theMenuMgr.add(getAction(EclipseGlobalActions.DELETE, actionsMap));
        theMenuMgr.add(getAction(EclipseGlobalActions.RENAME, actionsMap));
        theMenuMgr.add(new GroupMarker(ContextMenu.DELETE_END));
        theMenuMgr.add(new Separator());

        //
        // Menu item group for open, edit
        //
        theMenuMgr.add(new GroupMarker(ContextMenu.OPEN_START));
        theMenuMgr.add(getAction(ModelerGlobalActions.OPEN, actionsMap));
        theMenuMgr.add(getAction(ModelerGlobalActions.EDIT, actionsMap));
        theMenuMgr.add(new GroupMarker(ContextMenu.OPEN_END));
        theMenuMgr.add(new Separator());

        // Let any model object actions contribute at this time

        contributePermanentActionsToContextMenu(theMenuMgr, theSelection);

        //
        // Group to allow adding to the context menu
        //
        theMenuMgr.add(new Separator(ContextMenu.ADDITIONS));

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
        IMenuManager editMenu = getEditMenu();

        // only allow services from this plugin to be a menu listener
        if (getPlugin().getBundle().getSymbolicName().equals(UiConstants.PLUGIN_ID)) {
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

    /**
     * @see org.teiid.designer.ui.common.actions.AbstractActionService#shutdown()
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
        if (!result) {
            Object obj = SelectionUtilities.getSelectedObject(theSelection);

            // sometimes an ItemProvider gets displayed; defer to its parent
            if (obj instanceof ItemProvider) {
                obj = ((ItemProvider)obj).getParent();
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
        if (!result) {
            List objects = SelectionUtilities.getSelectedObjects(theSelection);
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

        // fail if null, empty
        if (theSelection == null || theSelection.isEmpty())
            return true;

        List resourceObjects = SelectionUtilities.getSelectedIResourceObjects(theSelection);

        IProject project = null;
        for (Object obj : resourceObjects) {
            // fail if NOT an IResource
            if (!(obj instanceof IResource)) {
                return true;
            }

            IResource resource = (IResource) obj;
            if (project == null) {
                project = resource.getProject();
            } else if (! project.equals(resource.getProject())) {
                // 2 resources are not part of the same project
                return true;
            }
        }

        return false;
    }

    /**
     * @see org.teiid.designer.ui.common.actions.AbstractActionService#getAction(String)
     */
    @Override
    public IAction getAction( String theActionId ) throws CoreException {
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
        String result = theKey;

        if (GlobalActionsMap.isEclipseGlobalAction(theKey)) {
            int index = ECLIPSE_GLOBAL_ACTION_IDS.indexOf(theKey);
            result = (String)MAPPED_ACTION_IDS.get(index);
        }

        return result;
    }

    /**
     * Gets the default action for the given identifier. The set of default action identifiers can be found by combining
     * {@link org.teiid.designer.ui.actions.IModelerActionConstants.ModelerGlobalActions} and
     * 
     * @param theActionId the identifier of the default action being requested
     * @return the default action or <code>null</code> if not found
     */
    @Override
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
                            action.setEnabled(!isReadOnly);
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
                                action.setEnabled(!isReadOnly);
                            }
                        }
                    }
                } else if (obj instanceof IFile) {
                    try {
                        // models are files
                        ModelResource modelResource = ModelUtil.getModelResource((IFile)obj, false);
                        if (modelResource != null && !modelResource.hasErrors()) {
                            Resource resource = modelResource.getEmfResource();
                            descriptors = ModelerCore.getModelEditor().getNewRootObjectCommands(resource);
                            boolean isReadOnly = ModelUtil.isIResourceReadOnly((IResource)obj);
                            boolean addedExtension = false;

                            // we need to sort the actions alphabetically, so put them all in a HashMap
                            HashMap actionMap = new HashMap();
                            Iterator iter = descriptors.iterator();
                            while (iter.hasNext()) {
                            	Command nextCommand = (Command)iter.next();
                            	if( !nextCommand.getLabel().equalsIgnoreCase("schema")  //$NON-NLS-1$
                            			&& !nextCommand.getLabel().equalsIgnoreCase("catalog") ) { //$NON-NLS-1$
                                    NewChildAction action = new NewChildAction(resource, nextCommand);
                                    actionMap.put(action.getText(), action);
                                    action.selectionChanged(getWorkbenchWindow().getPartService().getActivePart(), theSelection);

                                    // disable if read-only. obj should be EObject or an IResource.
                                    action.setEnabled(!isReadOnly);
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
                                if( i == 0 ) {
                                	menu.add(new Separator());
                                }
                                if (action.canCreateChild((IFile)obj)) {
                                	addedExtension = true;
                                    menu.add(action);
                                    action.setEnabled(!isReadOnly);
                                }
                            }

                            if( (descriptors.isEmpty()) && !addedExtension ) {
                                menu.add(new NewChildAction());
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
                boolean addedExtension = false;

                // we need to sort the actions alphabetically, so put them all in a HashMap
                if( commands != null ) {
	                HashMap actionMap = new HashMap();
	                Iterator iter = commands.iterator();
	
	                while (iter.hasNext()) {
	                    Command cmd = (Command)iter.next();
	                    NewSiblingAction action = new NewSiblingAction(eObj, cmd);
	                    actionMap.put(action.getText(), action);
	                    action.selectionChanged(getWorkbenchWindow().getPartService().getActivePart(), theSelection);
	                    // disable if read-only
	                    action.setEnabled(!isReadOnly);
	                }
	
	                // sort the keys of the actionMap to put them in alphabetical order
	                TreeSet set = new TreeSet(actionMap.keySet());
	                iter = set.iterator();
	                while (iter.hasNext()) {
	                    // add each action to the menu
	                    menu.add((IAction)actionMap.get(iter.next()));
	                }
                }

                // get the NewSiblingAction extensions and populate with any actions
                // Note: these should not be sorted - they always go on the bottom of the menu
                for (int i = 0; i < getNewSiblingExtensions().length; ++i) {
                    INewSiblingAction action = getNewSiblingExtensions()[i];
                    if (action.canCreateSibling(eObj)) {
                        if( i == 0 ) {
                        	menu.add(new Separator());
                        }
                        menu.add(action);
                        addedExtension = true;
                        action.setEnabled(!isReadOnly);
                    }
                }

                if( commands.isEmpty() && !addedExtension ) {
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

                            action.setEnabled(!isReadOnly);

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
     * @param theSelection the selection object
     * @return the menu manager
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
        
        if (menu.isEmpty()) {
            // just add a disabled "none allowed" item so menu is not empty
            menu.add(new NewChildAction());
        }

        return menu;
    }

    /**
     * Gets an refactor menu appropriate for the given input parameter.
     * 
     * @param theSelection the selection whose insert child menu is being requested
     * @param partSite the part's site where the menu is to be added
     * @return the insert child menu
     */
    public MenuManager getRefactorMenu( ISelection theSelection, IWorkbenchPartSite partSite ) {
        //        System.out.println("[ModelerActionService.getRefactorMenu] top");  //$NON-NLS-1$

        MenuManager menu = new MenuManager(utils.getString(PREFIX + "RefactorMenu.title"), //$NON-NLS-1$
                                           ModelerActionBarIdManager.getRefactorMenuId());
        
        IUndoContext undoContext = (IUndoContext) ResourcesPlugin.getWorkspace().getAdapter(IUndoContext.class);

        if (failedRefactorPreconditions(theSelection)) {
            // will result in returning a disabled 'Refactor' submenu

        } else {
            // object must be an IResource since it passed preconditions
            IWorkbenchWindow window = UiPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();

            String sRenameLabel = utils.getString("org.teiid.designer.ui.refactor.actions.RenameRefactorAction.text"); //$NON-NLS-1$
            String sRenameTooltip = utils.getString("org.teiid.designer.ui.refactor.actions.RenameRefactorAction.tooltip"); //$NON-NLS-1$

            String uriLabel = utils.getString("org.teiid.designer.ui.refactor.actions.NamespaceUriRefactorAction.text"); //$NON-NLS-1$
            String uriToolTip = utils.getString("org.teiid.designer.ui.refactor.actions.NamespaceUriRefactorAction.toolTip"); //$NON-NLS-1$

            String sMoveLabel = utils.getString("org.teiid.designer.ui.refactor.actions.MoveRefactorAction.text"); //$NON-NLS-1$
            String sMoveTooltip = utils.getString("org.teiid.designer.ui.refactor.actions.MoveRefactorAction.tooltip"); //$NON-NLS-1$

            try {
                // undo
                IAction actUndo = new UndoActionHandler(partSite, undoContext);
                menu.add(actUndo);

                // redo
                IAction actRedo = new RedoActionHandler(partSite, undoContext);
                menu.add(actRedo);

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
     * @param selection the selected object
     * 
     * @return the Copy Name submenu
     */
    public MenuManager getCopyNameSubMenu( Object selection) {
        MenuManager menu = new MenuManager(utils.getString(PREFIX + "copyNameSubMenu.title")); //$NON-NLS-1$
        
        CopyFullNameAction action1 = new CopyFullNameAction();
        action1.getActionWorker().selectionChanged(selection);
        menu.add(action1);
        
        CopyNameAction action2 = new CopyNameAction();
        action1.getActionWorker().selectionChanged(selection);
        menu.add(action2);

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
            } catch (CoreException e) {
                String message = UiConstants.Util.getString(PREFIX + "getActionErrorMessage", actionId); //$NON-NLS-1$
                UiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

        // get global actions installed in edit menu
        contributeToEditMenu();
    }

    /**
     * @see org.teiid.designer.ui.common.actions.AbstractActionService#registerEventHandler(org.eclipse.jface.action.IAction)
     */
    @Override
    protected void registerEventHandler( IAction theAction ) {
        // first let super register
        super.registerEventHandler(theAction);

        // EObject notifier events
        if (theAction instanceof INotifyChangedListener) {
            addNotifyChangedListener((INotifyChangedListener)theAction);
        }
    }

    /**
     * Registers the Modeler's default global actions for the given <code>IPageSite</code>'s <code>IActionBars</code>.
     * 
     * @param bars the action bars to register global actions
     */
    public void registerDefaultGlobalActions( IActionBars bars ) {
        
        // TODO this replaces the eclipse Undo action with the designer version
        
        Iterator itr = defaultActionsMap.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry)itr.next();
            bars.setGlobalActionHandler((String)entry.getKey(), (IAction)entry.getValue());
        }
    }

    /**
     * Removes the given listener from receiving workspace container {@link org.eclipse.emf.common.notify.Notification}s. This is
     * a pass-through helper method to the {@link org.teiid.designer.ui.viewsupport.ModelUtilities} method.
     * 
     * @param theListener the listener being removed
     */
    public void removeNotifyChangedListener( INotifyChangedListener theListener ) {
        ModelUtilities.removeNotifyChangedListener(theListener);
    }

    /**
     * @see org.teiid.designer.ui.common.actions.AbstractActionService#unregisterEventHandler(org.eclipse.jface.action.IAction)
     */
    @Override
    protected void unregisterEventHandler( IAction theAction ) {
        // first let super unregister
        super.unregisterEventHandler(theAction);

        // unregister EObject notifier events
        if (theAction instanceof INotifyChangedListener) {
            removeNotifyChangedListener((INotifyChangedListener)theAction);
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

    /**
     * @return the refactor undo manager
     */
    public RefactorUndoManager getRefactorUndoManager() {
        if (this.refactorUndoManager == null) {
            this.refactorUndoManager = RefactorUndoManager.getInstance();
        }
        return this.refactorUndoManager;
    }

}
