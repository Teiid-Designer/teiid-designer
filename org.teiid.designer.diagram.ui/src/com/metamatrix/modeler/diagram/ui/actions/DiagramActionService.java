/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;


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
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.modeler.diagram.ui.editor.DiagramEditor;
import com.metamatrix.modeler.internal.ui.editors.ModelEditor;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.IModelerActionConstants;
import com.metamatrix.modeler.ui.actions.ModelerActionService;
import com.metamatrix.modeler.ui.actions.ModelerGlobalActionsMap;
import com.metamatrix.modeler.ui.editors.ModelEditorPage;
import com.metamatrix.ui.actions.AbstractActionService;
import com.metamatrix.ui.actions.GlobalActionsMap;

/**
 * The <code>DiagramActionService</code> class is the Modeler Plugin's action service. It is responsible for
 * managing all actions for this plugin.
 */
public final class DiagramActionService extends AbstractActionService 
                                     implements DiagramUiConstants, 
                                                IDiagramActionConstants {
 

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
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

    /** Map of default global actions. This map actually contains action for each action identifier. */
    private DiagramGlobalActionsMap defaultActionsMap;

    /** The modeler plugin. */
//    private AbstractUiPlugin plugin;

    private boolean bHasBeenInitialized;

    // Variables

    // ================================================
    // STATIC METHODS
    // ================================================
    
    /**
     * Constructs the key that should be used when registering/removing an action in the <code>DiagramActionService</code>.
     * @param theAction the action whose key is being requested
     * @param theEditorPage the editor page where the action is being installed
     * @return the key
     */
    public static String constructKey(IAction theAction,
                                      ModelEditorPage theEditorPage) {
        return new StringBuffer().append(Extensions.DIAGRAM_EDITOR)
                                 .append(DELIMITER)
                                 .append(((IFileEditorInput)theEditorPage.getEditorInput()).getFile().getFullPath())
                                 .append(DELIMITER)
                                 .append(theAction.getId())
                                 .toString();

    }
    
    /**
     * Constructs the key that should be used when registering/removing an action in the <code>DiagramActionService</code>.
     * @param theActionId the actionId to construct the key (class name)
     * @param theEditorPage the editor page where the action is being installed
     * @return the key
     */
    public static String constructKey(String theActionId,
                                      ModelEditorPage theEditorPage) {
        return new StringBuffer().append(Extensions.DIAGRAM_EDITOR)
                                 .append(DELIMITER)
                                 .append(((IFileEditorInput)theEditorPage.getEditorInput()).getFile().getFullPath())
                                 .append(DELIMITER)
                                 .append(theActionId)
                                 .toString();

    }

    // ================================================
    // CONSTRUCTORS
    // ================================================
    
    /**
     * Constructs a <code>DiagramActionService</code> associated with the given <code>IWorkbenchWindow</code>.
     * @param theWindow the associated workbench window
     */
    public DiagramActionService(IWorkbenchPage page) {
        super(DiagramUiPlugin.getDefault(), page);

//        plugin = getPlugin();

        // MOVED TO A LATER MOMENT: initializeGlobalActions();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractActionService#getDefaultAction(java.lang.String)
     */
    public IAction getDefaultAction(String theActionId) {
//System.out.println("[DiagramActionService.getDefaultAction]"); //$NON-NLS-1$     
        try {   
            // get the action from the ModelerActionService
            return UiPlugin.getDefault().getActionService( getWorkbenchWindow().getActivePage() ).getAction( theActionId );
        } catch( CoreException ce ) {
            
            //TODO: complete this!
        }
        return null;
    }

    
    // ======================================================
    // METHODS
    // ======================================================

    
    private IEditorPart getActiveEditor() {
        IWorkbenchPage activePage = getPlugin().getCurrentWorkbenchWindow().getActivePage();
        IEditorPart editor;
        if (activePage != null) {
            editor = activePage.getActiveEditor();
        } else {
            editor = getPage().getActiveEditor();
        } // endif

        if (editor instanceof ModelEditor) {
            return editor;                   
        }
        return null;
    }

    /**
     * Initializes the <code>Map</code> of all global actions.
     */
    private void initializeGlobalActions() {
//        System.out.println("[DiagramActionService.initializeGlobalActions]"); //$NON-NLS-1$     

        // need to put the real actions into the map as the new constructed map only
        // contains identifiers which stand for "use default" action
        IAction action  =   null;
        defaultActionsMap = new DiagramGlobalActionsMap();

        // load all default global actions since the map doesn't really have the default actions
        // as values. it just has a marker.
        for (int i = 0; i < DiagramGlobalActionsMap.ALL_DIAGRAM_GLOBAL_ACTIONS.length; i++) {
            String actionId = DiagramGlobalActionsMap.ALL_DIAGRAM_GLOBAL_ACTIONS[i];
                        
            try {
                // the call to getAction(String) throws a CoreException if the action cannot be constructed.
                // also the call to getAction(String) registers the action to receive appropriate events.
                // if the action is registered in the modeler action service use it. if not there then it is
                // not a modeler global action and must be a diagram specific action
      
                /*
                 * jh note: this seems backwards; it will always use the ModelerActionService action, 
                 *          if present, and ignore the 'diagram' one that should be overriding it...
                 */
      
//                if (getModelerActionService().isRegistered(actionId)) {
//                    action = getModelerActionService().getAction(actionId);
//                } else {
//                    action = getAction(actionId);
//                }

                // rewrite:      
                
//                System.out.println("[DiagramActionService.initializeGlobalActions] about to process actionId: " + actionId ); //$NON-NLS-1$
                if ( actionId.equals(IModelerActionConstants.EclipseGlobalActions.PRINT ) ) {
//                    System.out.println("[DiagramActionService.initializeGlobalActions] Adding PrintWrapper to the defaultActionsMap" ); //$NON-NLS-1$
                    PrintWrapper printAction = new PrintWrapper();
                    defaultActionsMap.put(IModelerActionConstants.EclipseGlobalActions.PRINT, 
                                          printAction );
                    registerEventHandler( printAction );
                } else {
                    // get Eclipse and Modeler global actions from the ModelerActionService so that
                    // only one instance is constructed.
                    if (GlobalActionsMap.isEclipseGlobalAction(actionId) ||
                        ModelerGlobalActionsMap.isModelerGlobalAction(actionId)) {
                        action = getModelerActionService().getAction(actionId);
                    } else {
                        // must be a diagram global action
                        action = getAction(actionId);
                    }
    
                    defaultActionsMap.put(actionId, action);
                }
                
            } catch (CoreException e) {
                String message = DiagramUiConstants.Util.getString(PREFIX + "actionErrorMessage", actionId); //$NON-NLS-1$
                DiagramUiConstants.Util.log(IStatus.ERROR, e, message);
            }            
        }

//        System.out.println("[DiagramActionService.initializeGlobalActions] final defaultActionsMap: " + defaultActionsMap ); //$NON-NLS-1$


        // get global actions installed in edit menu
        //OBSOLETE???contributeToEditMenu();
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractActionService#isRegistered(java.lang.String)
     */
    @Override
    public boolean isRegistered(String theActionId) {
        if ( !bHasBeenInitialized ) {
            bHasBeenInitialized = true;
            initializeGlobalActions();                
        }

        return super.isRegistered(theActionId);
    }

    /**
     * Fills the given menu with the standard items appropriate for a context menu of a part in
     * the Modeler. 
     * @param theMenuMgr the menu being contributed to (should be a context menu)
     * @param theActionsMap the map of global action handlers
     * @param theSelection the selection used to determine how to build the new child and new sibling menus
     */
    @Override
    public void contributeToContextMenu(IMenuManager theMenuMgr,
                                        GlobalActionsMap theActionsMap,
                                        ISelection theSelection) {

// System.out.println("[DiagramActionService.contributeToContextMenu] top "); //$NON-NLS-1$

                 
        DiagramEditor deDiagramEditor = getDiagramEditor();
        ISelection selection = null;       
                       
        if (  deDiagramEditor != null ) {
            
            // establish the selection
            if( theSelection == null ) {
                selection = deDiagramEditor.getModelObjectSelectionProvider().getSelection();
            } else {
                selection = theSelection;
            }
            
//            if ( selection == null ) {            
//                System.out.println("[DiagramActionService.contributeToContextMenu] selection is still NULL "); //$NON-NLS-1$
//            }
            
            // init globals
            if ( !bHasBeenInitialized ) {
                bHasBeenInitialized = true;
                initializeGlobalActions();                
            }
            
            // get ModelerActionService's contribution
            getModelerActionService().contributeToContextMenu( theMenuMgr,
                                                               theActionsMap,
                                                               selection );
                
            /*
             * add 'diagram global' actions here
             *         GlobalActionsMap actionsMap = (theActionsMap == null) ? new ModelerGlobalActionsMap() : theActionsMap;
             * 
             */
//            theMenuMgr.add( getAction( DiagramGlobalActions.ZOOM_IN, defaultActionsMap) );
//            theMenuMgr.add( getAction( DiagramGlobalActions.ZOOM_OUT, defaultActionsMap) );
//            theMenuMgr.add( getAction( DiagramGlobalActions.FONT_UP, defaultActionsMap) );
//            theMenuMgr.add( getAction( DiagramGlobalActions.FONT_DOWN, defaultActionsMap) );
//            theMenuMgr.add( getAction( DiagramGlobalActions.AUTOLAYOUT, defaultActionsMap) );

            // add the notation submenu                          
            MenuManager smNotationSubmenu = deDiagramEditor.getNotationActionGroup();
            
            if (smNotationSubmenu != null && !smNotationSubmenu.isEmpty()) {
               theMenuMgr.add(new GroupMarker(ContextMenu.NOTATION_START));
               theMenuMgr.insertAfter(ContextMenu.NOTATION_START, smNotationSubmenu);
               theMenuMgr.add(new GroupMarker(ContextMenu.NOTATION_END));
            }             
        }
         
        // will ModelerActionService already have done this???:

// jhTODO: resolve the issue of how 'contributeExportedActions' is supposed to work.
//// jh try commenting this out to prevent duplicates:
//        if (deDiagramEditor instanceof IEditorActionExporter) {
//            ((IEditorActionExporter)deDiagramEditor).contributeExportedActions(theMenuMgr);
//        }
                         
    }

    private DiagramEditor getDiagramEditor() {
        IEditorPart editor = getActiveEditor();        
        
        ModelEditor meEditor = ((ModelEditor)editor);
       
        if ( meEditor.getCurrentPage() instanceof DiagramEditor ) {
            return (DiagramEditor)meEditor.getCurrentPage();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractActionService#getAction(String)
     */
    @Override
    public IAction getAction(String theActionId) throws CoreException {
//        System.out.println("[DiagramActionService.getAction] looking up: " + theActionId ); //$NON-NLS-1$
        if ( !bHasBeenInitialized ) {
            bHasBeenInitialized = true;
           initializeGlobalActions();
         }
     

        IAction action = defaultActionsMap.getAction( theActionId );
        if ( action != null ) {
//            System.out.println("[DiagramActionService.getAction] returning action from our default map: " + action ); //$NON-NLS-1$     
            return action;
        }
//            System.out.println("[DiagramActionService.getAction] returning action from our superclass' method"); //$NON-NLS-1$     
     
        // registering actions to receive events is done by the getAction(Class) method
        return super.getAction(getActionId(theActionId));
    }

    /**
     * If the key represents an Eclipse global action a second lookup is needed to get the actual
     * action class name which is needed by the action service to construct the action. For non-Eclipse
     * global action, the key is already the action class name.
     * @param theKey the action identifier whose "real" action identifier is being requested
     * @return the action identifier used by the action service
     */
    private String getActionId(String theKey) {
//        System.out.println("[DiagramActionService.getActionId]"); //$NON-NLS-1$     

        String result = theKey;

        if ( getModelerActionService().getActionId( theKey ) != null ) {
            result = getModelerActionService().getActionId( theKey );        
        }

        return result;        
    }


    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractActionService#registerEventHandler(org.eclipse.jface.action.IAction)
     */
    @Override
    protected void registerEventHandler(IAction theAction) {
//        System.out.println("[DiagramActionService.registerEventHandler] defId: " + theAction.getActionDefinitionId() ); //$NON-NLS-1$     

        // first let super register
        super.registerEventHandler(theAction);

        // EObject notifier events
        if (theAction instanceof INotifyChangedListener) {
            addNotifyChangedListener((INotifyChangedListener)theAction);
        }

    }


    protected void addNotifyChangedListener( INotifyChangedListener theListener ) {
        ModelUtilities.addNotifyChangedListener( theListener );                
    }
    
    
    /** 
     * Removes the given listener from receiving workspace container {@link org.eclipse.emf.common.notify.Notification}s.
     * This is a pass-through helper method to the {@link com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities}
     * method.
     * @param listener the listener being removed
     */
    public void removeNotifyChangedListener(INotifyChangedListener theListener) {
        ModelUtilities.removeNotifyChangedListener(theListener);
    }
    
    @Override
    public void removePartListener( IPartListener theListener ) {
    	super.removePartListener(theListener);
    }    


    /* (non-Javadoc)
     * @see com.metamatrix.ui.actions.AbstractActionService#unregisterEventHandler(org.eclipse.jface.action.IAction)
     */
    @Override
    protected void unregisterEventHandler(IAction theAction) {

        // first let super unregister
        super.unregisterEventHandler(theAction);

        // unregister EObject notifier events
        if (theAction instanceof INotifyChangedListener) {
            removeNotifyChangedListener((INotifyChangedListener)theAction);
        }

        if (theAction instanceof IPartListener) {
            removePartListener((IPartListener)theAction);
        }
    }
    
    public ModelerActionService getModelerActionService() { 
//        System.out.println("[DiagramActionService.getModelerActionService]"); //$NON-NLS-1$     
        return (ModelerActionService)UiPlugin.getDefault().getActionService( getPage() );
    }
    /** 
     * @see com.metamatrix.ui.actions.ActionService#contributePermanentActionsToContextMenu(org.eclipse.jface.action.IMenuManager, org.eclipse.jface.viewers.ISelection)
     * @since 4.2
     */
    @Override
    public void contributePermanentActionsToContextMenu(IMenuManager theMenuMgr,
                                                        ISelection theSelection) {
        super.contributePermanentActionsToContextMenu(theMenuMgr, theSelection);
    }
}
