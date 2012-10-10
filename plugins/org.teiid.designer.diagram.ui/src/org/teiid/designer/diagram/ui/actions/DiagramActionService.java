/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.diagram.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.diagram.ui.DiagramUiConstants;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.diagram.ui.editor.DiagramEditor;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.IModelerActionConstants;
import org.teiid.designer.ui.actions.ModelerActionService;
import org.teiid.designer.ui.actions.ModelerGlobalActionsMap;
import org.teiid.designer.ui.common.actions.AbstractActionService;
import org.teiid.designer.ui.common.actions.GlobalActionsMap;
import org.teiid.designer.ui.editors.ModelEditor;
import org.teiid.designer.ui.editors.ModelEditorPage;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


/**
 * The <code>DiagramActionService</code> class is the Modeler Plugin's action service. It is responsible for managing all actions
 * for this plugin.
 *
 * @since 8.0
 */
public final class DiagramActionService extends AbstractActionService implements DiagramUiConstants, IDiagramActionConstants {

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /** Delimeter used when constructing keys when registering/removing actions from the ActionService. */
    private static final char DELIMITER = '|';

    // ======================================================
    // CONSTANTS
    // ======================================================

    /** The logging prefix. */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(DiagramActionService.class);

    // ======================================================
    // FIELDS
    // ======================================================

    /**
     * Constructs the key that should be used when registering/removing an action in the <code>DiagramActionService</code>.
     * 
     * @param theAction the action whose key is being requested
     * @param theEditorPage the editor page where the action is being installed
     * @return the key
     */
    public static String constructKey( final IAction theAction,
                                       final ModelEditorPage theEditorPage ) {
        return new StringBuffer().append(Extensions.DIAGRAM_EDITOR).append(DELIMITER).append(((IFileEditorInput)theEditorPage.getEditorInput()).getFile().getFullPath()).append(DELIMITER).append(theAction.getId()).toString();

    }

    /**
     * Constructs the key that should be used when registering/removing an action in the <code>DiagramActionService</code>.
     * 
     * @param theActionId the actionId to construct the key (class name)
     * @param theEditorPage the editor page where the action is being installed
     * @return the key
     */
    public static String constructKey( final String theActionId,
                                       final ModelEditorPage theEditorPage ) {
        return new StringBuffer().append(Extensions.DIAGRAM_EDITOR).append(DELIMITER).append(((IFileEditorInput)theEditorPage.getEditorInput()).getFile().getFullPath()).append(DELIMITER).append(theActionId).toString();

    }

    // Variables

    // ================================================
    // STATIC METHODS
    // ================================================

    /** Map of default global actions. This map actually contains action for each action identifier. */
    private DiagramGlobalActionsMap defaultActionsMap;

    /** The modeler plugin. */
    // private AbstractUiPlugin plugin;

    private boolean bHasBeenInitialized;

    // ================================================
    // CONSTRUCTORS
    // ================================================

    /**
     * Constructs a <code>DiagramActionService</code> associated with the given <code>IWorkbenchWindow</code>.
     * 
     * @param theWindow the associated workbench window
     */
    public DiagramActionService( final IWorkbenchPage page ) {
        super(DiagramUiPlugin.getDefault(), page);

        // plugin = getPlugin();

        // MOVED TO A LATER MOMENT: initializeGlobalActions();
    }

    protected void addNotifyChangedListener( final INotifyChangedListener theListener ) {
        ModelUtilities.addNotifyChangedListener(theListener);
    }

    // ======================================================
    // METHODS
    // ======================================================

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#contributePermanentActionsToContextMenu(org.eclipse.jface.action.IMenuManager,
     *      org.eclipse.jface.viewers.ISelection)
     * @since 4.2
     */
    @Override
    public void contributePermanentActionsToContextMenu( final IMenuManager theMenuMgr,
                                                         final ISelection theSelection ) {
        super.contributePermanentActionsToContextMenu(theMenuMgr, theSelection);
    }

    /**
     * Fills the given menu with the standard items appropriate for a context menu of a part in the Modeler.
     * 
     * @param theMenuMgr the menu being contributed to (should be a context menu)
     * @param theActionsMap the map of global action handlers
     * @param theSelection the selection used to determine how to build the new child and new sibling menus
     */
    @Override
    public void contributeToContextMenu( final IMenuManager theMenuMgr,
                                         final GlobalActionsMap theActionsMap,
                                         final ISelection theSelection ) {

        // System.out.println("[DiagramActionService.contributeToContextMenu] top "); //$NON-NLS-1$

        final DiagramEditor deDiagramEditor = getDiagramEditor();
        ISelection selection = null;

        if (deDiagramEditor != null) {

            // establish the selection
            if (theSelection == null) selection = deDiagramEditor.getModelObjectSelectionProvider().getSelection();
            else selection = theSelection;

            // if ( selection == null ) {
            //                System.out.println("[DiagramActionService.contributeToContextMenu] selection is still NULL "); //$NON-NLS-1$
            // }

            // init globals
            if (!bHasBeenInitialized) {
                bHasBeenInitialized = true;
                initializeGlobalActions();
            }

            // get ModelerActionService's contribution
            getModelerActionService().contributeToContextMenu(theMenuMgr, theActionsMap, selection);

            /*
             * add 'diagram global' actions here
             *         GlobalActionsMap actionsMap = (theActionsMap == null) ? new ModelerGlobalActionsMap() : theActionsMap;
             * 
             */
            // theMenuMgr.add( getAction( DiagramGlobalActions.ZOOM_IN, defaultActionsMap) );
            // theMenuMgr.add( getAction( DiagramGlobalActions.ZOOM_OUT, defaultActionsMap) );
            // theMenuMgr.add( getAction( DiagramGlobalActions.FONT_UP, defaultActionsMap) );
            // theMenuMgr.add( getAction( DiagramGlobalActions.FONT_DOWN, defaultActionsMap) );
            // theMenuMgr.add( getAction( DiagramGlobalActions.AUTOLAYOUT, defaultActionsMap) );

            // add the notation submenu
            final MenuManager smNotationSubmenu = deDiagramEditor.getNotationActionGroup();

            if (smNotationSubmenu != null && !smNotationSubmenu.isEmpty()) {
                theMenuMgr.add(new GroupMarker(ContextMenu.NOTATION_START));
                theMenuMgr.insertAfter(ContextMenu.NOTATION_START, smNotationSubmenu);
                theMenuMgr.add(new GroupMarker(ContextMenu.NOTATION_END));
            }
        }

        // will ModelerActionService already have done this???:

        // jhTODO: resolve the issue of how 'contributeExportedActions' is supposed to work.
        // // jh try commenting this out to prevent duplicates:
        // if (deDiagramEditor instanceof IEditorActionExporter) {
        // ((IEditorActionExporter)deDiagramEditor).contributeExportedActions(theMenuMgr);
        // }

    }

    /* (non-Javadoc)
     * see org.teiid.designer.ui.common.actions.AbstractActionService#getAction(String)
     */
    @Override
    public IAction getAction( final String theActionId ) throws CoreException {
        //        System.out.println("[DiagramActionService.getAction] looking up: " + theActionId ); //$NON-NLS-1$
        if (!bHasBeenInitialized) {
            bHasBeenInitialized = true;
            initializeGlobalActions();
        }

        final IAction action = defaultActionsMap.getAction(theActionId);
        if (action != null) // System.out.println("[DiagramActionService.getAction] returning action from our default map: " +
                            // action );
        return action;

        // registering actions to receive events is done by the getAction(Class) method
        return super.getAction(getActionId(theActionId));
    }

    /**
     * If the key represents an Eclipse global action a second lookup is needed to get the actual action class name which is needed
     * by the action service to construct the action. For non-Eclipse global action, the key is already the action class name.
     * 
     * @param theKey the action identifier whose "real" action identifier is being requested
     * @return the action identifier used by the action service
     */
    private String getActionId( final String theKey ) {
        //        System.out.println("[DiagramActionService.getActionId]"); //$NON-NLS-1$     

        String result = theKey;

        if (getModelerActionService().getActionId(theKey) != null) result = getModelerActionService().getActionId(theKey);

        return result;
    }

    private IEditorPart getActiveEditor() {
        final IWorkbenchPage activePage = getPlugin().getCurrentWorkbenchWindow().getActivePage();
        IEditorPart editor;
        if (activePage != null) editor = activePage.getActiveEditor();
        else editor = getPage().getActiveEditor();

        if (editor instanceof ModelEditor) return editor;
        return null;
    }

    /* (non-Javadoc)
     * see org.teiid.designer.ui.common.actions.AbstractActionService#getDefaultAction(java.lang.String)
     */
    @Override
	public IAction getDefaultAction( final String theActionId ) {
        //System.out.println("[DiagramActionService.getDefaultAction]"); //$NON-NLS-1$     
        try {
            // get the action from the ModelerActionService
            return UiPlugin.getDefault().getActionService(getWorkbenchWindow().getActivePage()).getAction(theActionId);
        } catch (final CoreException ce) {

            // complete this!
        }
        return null;
    }

    private DiagramEditor getDiagramEditor() {
        final IEditorPart editor = getActiveEditor();

        final ModelEditor meEditor = ((ModelEditor)editor);

        if (meEditor.getCurrentPage() instanceof DiagramEditor) return (DiagramEditor)meEditor.getCurrentPage();
        return null;
    }

    public ModelerActionService getModelerActionService() {
        //        System.out.println("[DiagramActionService.getModelerActionService]"); //$NON-NLS-1$     
        return (ModelerActionService)UiPlugin.getDefault().getActionService(getPage());
    }

    /**
     * Initializes the <code>Map</code> of all global actions.
     */
    private void initializeGlobalActions() {
        //        System.out.println("[DiagramActionService.initializeGlobalActions]"); //$NON-NLS-1$     

        // need to put the real actions into the map as the new constructed map only
        // contains identifiers which stand for "use default" action
        IAction action = null;
        defaultActionsMap = new DiagramGlobalActionsMap();

        // load all default global actions since the map doesn't really have the default actions
        // as values. it just has a marker.
        for (final String actionId : DiagramGlobalActionsMap.ALL_DIAGRAM_GLOBAL_ACTIONS) {
            try {
                // the call to getAction(String) throws a CoreException if the action cannot be constructed.
                // also the call to getAction(String) registers the action to receive appropriate events.
                // if the action is registered in the modeler action service use it. if not there then it is
                // not a modeler global action and must be a diagram specific action

                /*
                 * jh note: this seems backwards; it will always use the ModelerActionService action, 
                 *          if present, and ignore the 'diagram' one that should be overriding it...
                 */

                // if (getModelerActionService().isRegistered(actionId)) {
                // action = getModelerActionService().getAction(actionId);
                // } else {
                // action = getAction(actionId);
                // }

                // rewrite:

                //                System.out.println("[DiagramActionService.initializeGlobalActions] about to process actionId: " + actionId ); //$NON-NLS-1$
                if (actionId.equals(IModelerActionConstants.EclipseGlobalActions.PRINT)) {
                    //                    System.out.println("[DiagramActionService.initializeGlobalActions] Adding PrintWrapper to the defaultActionsMap" ); //$NON-NLS-1$
                    final PrintWrapper printAction = new PrintWrapper();
                    defaultActionsMap.put(IModelerActionConstants.EclipseGlobalActions.PRINT, printAction);
                    registerEventHandler(printAction);
                } else {
                    // get Eclipse and Modeler global actions from the ModelerActionService so that
                    // only one instance is constructed.
                    if (GlobalActionsMap.isEclipseGlobalAction(actionId) || ModelerGlobalActionsMap.isModelerGlobalAction(actionId)) action = getModelerActionService().getAction(actionId);
                    else // must be a diagram global action
                    action = getAction(actionId);

                    defaultActionsMap.put(actionId, action);
                }

            } catch (final CoreException e) {
                final String message = DiagramUiConstants.Util.getString(PREFIX + "actionErrorMessage", actionId); //$NON-NLS-1$
                DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
            }
        }

        //        System.out.println("[DiagramActionService.initializeGlobalActions] final defaultActionsMap: " + defaultActionsMap ); //$NON-NLS-1$

        // get global actions installed in edit menu
        // OBSOLETE???contributeToEditMenu();
    }

    /* (non-Javadoc)
     * see org.teiid.designer.ui.common.actions.AbstractActionService#isRegistered(java.lang.String)
     */
    @Override
    public boolean isRegistered( final String theActionId ) {
        if (!bHasBeenInitialized) {
            bHasBeenInitialized = true;
            initializeGlobalActions();
        }

        return super.isRegistered(theActionId);
    }

    /* (non-Javadoc)
     * see org.teiid.designer.ui.common.actions.AbstractActionService#registerEventHandler(org.eclipse.jface.action.IAction)
     */
    @Override
    protected void registerEventHandler( final IAction theAction ) {
        //        System.out.println("[DiagramActionService.registerEventHandler] defId: " + theAction.getActionDefinitionId() ); //$NON-NLS-1$     

        // first let super register
        super.registerEventHandler(theAction);

        // EObject notifier events
        if (theAction instanceof INotifyChangedListener) addNotifyChangedListener((INotifyChangedListener)theAction);

    }

    /**
     * Removes the given listener from receiving workspace container {@link org.eclipse.emf.common.notify.Notification}s. This is a
     * pass-through helper method to the {@link org.teiid.designer.ui.viewsupport.ModelUtilities} method.
     * 
     * @param listener the listener being removed
     */
    public void removeNotifyChangedListener( final INotifyChangedListener theListener ) {
        ModelUtilities.removeNotifyChangedListener(theListener);
    }

    @Override
    public void removePartListener( final IPartListener theListener ) {
        super.removePartListener(theListener);
    }

    /* (non-Javadoc)
     * see org.teiid.designer.ui.common.actions.AbstractActionService#unregisterEventHandler(org.eclipse.jface.action.IAction)
     */
    @Override
    protected void unregisterEventHandler( final IAction theAction ) {

        // first let super unregister
        super.unregisterEventHandler(theAction);

        // unregister EObject notifier events
        if (theAction instanceof INotifyChangedListener) removeNotifyChangedListener((INotifyChangedListener)theAction);

        if (theAction instanceof IPartListener) removePartListener((IPartListener)theAction);
    }
}
