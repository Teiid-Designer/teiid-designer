/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.editors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.UiPlugin;
import org.teiid.designer.ui.actions.IModelerActionConstants;
import org.teiid.designer.ui.actions.ModelerGlobalActionsMap;
import org.teiid.designer.ui.common.actions.ActionService;
import org.teiid.designer.ui.common.actions.GlobalActionsMap;


/**
 * Manages the installation/deinstallation of global actions for multi-page editors. Responsible for the redirection of global
 * actions to the active editor. Multi-page contributor replaces the contributors for the individual editors in the multi-page
 * editor.
 *
 * @since 8.0
 */
public class ModelEditorActionContributor extends MultiPageEditorActionBarContributor
    implements IModelerActionConstants, UiConstants {

    /** Map of default global actions. */
    private static ModelerGlobalActionsMap defaultActionsMap;

    /** StatusBar field for Read Only/Writable state of the model file. */
    private StatusLineContributionItem fileStateItem;

    /** The editor input. Will always be a model file. */
    private IFileEditorInput input;

    /**
     * A collection of editor page action contributors. These page contributors should NOT be disposed from this class. It is
     * their ModelEditorPage that is responsible for their disposing.
     */
    private Map partContributorMap;

    /** Listener to clean up the partContributorMap. */
    private IPartListener partListener;


    /**
     * Keep track of the global action handlers. When we activate them through the IHandlerService, if their is already a handler
     * for that action, Eclipse logs a warning message but the activation still works. This map is being used so that we can
     * deactivate existing handlers before activating the new one (which gets rid of the warning message).
     */
    private Map<String, IHandlerActivation> actionHandlerMap;

    static {
        defaultActionsMap = new ModelerGlobalActionsMap();
    }

    /**
     * 
     */
    public ModelEditorActionContributor() {
        this.actionHandlerMap = new HashMap<String, IHandlerActivation>(ModelerGlobalActionsMap.ALL_GLOBAL_ACTIONS.length);
    }

    /**
     * @param thePart the editor part
     * @param theContributor the action bar contributor
     */
    public void addContributor( IEditorPart thePart,
                                IEditorActionBarContributor theContributor ) {
        if (partContributorMap == null) {
            partContributorMap = new HashMap();
        }

        partContributorMap.put(thePart, theContributor);

        // if not done previously, create listener for part closed events
        if (this.partListener == null) {
            this.partListener = new EditorPartListener();
            getPage().addPartListener(this.partListener);
        }

        theContributor.init(getActionBars(), getPage());
    }

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToStatusLine(org.eclipse.jface.action.IStatusLineManager)
     */
    @Override
    public void contributeToStatusLine( IStatusLineManager theStatusLineManager ) {
        super.contributeToStatusLine(theStatusLineManager);

        fileStateItem = new StatusLineContributionItem(IModelerActionConstants.StatusBar.MODEL_EDITOR_FILE_STATE);
        theStatusLineManager.add(fileStateItem);
    }

    /**
     * @see org.eclipse.ui.part.EditorActionBarContributor#dispose()
     * @since 5.0
     */
    @Override
    public void dispose() {
        if (this.partListener != null) {
            getPage().removePartListener(this.partListener);
        }

        super.dispose();
    }

    /**
     * Gets the action bar contributor for the given page editor.
     * 
     * @param thePart the page editor whose contributor is being requested
     * @return the contributor or <code>null</code> if not found
     */
    private AbstractModelEditorPageActionBarContributor getActionContributor( IEditorPart thePart ) {
        // get the contributor from the map
        AbstractModelEditorPageActionBarContributor result = null;

        if (partContributorMap != null) {
            result = (AbstractModelEditorPageActionBarContributor)partContributorMap.get(thePart);
        }
        return result;
    }

    /**
     * Handler for part closed events. Used to clean up the part contributor map.
     * 
     * @param thePart the part that was closed
     * @since 5.0
     */
    void handlePartClosed( IWorkbenchPart thePart ) {
        if (thePart instanceof ModelEditor) {
            // just remove editor pages
            List editors = ((ModelEditor)thePart).getAllEditors();

            for (int size = editors.size(), i = 0; i < size; ++i) {
                // could have checked first to see if contained in but remove also does that
                this.partContributorMap.remove(editors.get(i));
            }
        }
    }

    /**
     * @see org.eclipse.ui.IEditorActionBarContributor#setActiveEditor(org.eclipse.ui.IEditorPart)
     */
    @Override
    public void setActiveEditor( IEditorPart thePart ) {
        setActivePage(thePart);
    }

    /* (non-JavaDoc)
     * Method declared in AbstractMultiPageEditorActionBarContributor.
     */
    @Override
    public void setActivePage( IEditorPart thePart ) {
        // redirect to the pages contributor
        AbstractModelEditorPageActionBarContributor contributor = null;
        IEditorPart part = thePart;

        // when first starting eclipse with a ModelEditor as the active editor, and when switching
        // from another editor to the ModelEditor, the part passed in is the editor itself.
        // so find the current page and use it's contributor
        if (thePart instanceof ModelEditor) {
            part = ((ModelEditor)thePart).getCurrentPage();
        }

        //
        // deactivate other page contributors by setting the active editor. if not their editor they will deactivate.
        //

        if (this.partContributorMap != null) {
            Iterator itrContributor = this.partContributorMap.values().iterator();
            while (itrContributor.hasNext()) {
                IEditorActionBarContributor pageContributor = (IEditorActionBarContributor)itrContributor.next();
                if (pageContributor != null) {
                    pageContributor.setActiveEditor(part);
                }
            }
        }

        //
        // activate current page contributor
        //
        // even if activeEditor is the part we still need to setActiveEditor on the contributor
        // to let the contributor re-contribute. if we don't and user selects off of the editor
        // and on to a view and back to the editor the contribution state may not be valid
        contributor = getActionContributor(part);

        // editor parts don't have to have a contributor
        if (contributor != null) {
            contributor.setActiveEditor(part);
        }

        //
        // install global actions
        // (ModelerGlobalActionsMap)
        ModelerGlobalActionsMap globalActions = (contributor == null) ? (ModelerGlobalActionsMap)defaultActionsMap : (ModelerGlobalActionsMap)contributor.getGlobalActions();

        // if contributor does not return a map then use all default actions
        if (globalActions == null) {
            globalActions = new ModelerGlobalActionsMap();
        }

        IWorkbenchWindow window = getPage().getWorkbenchWindow();
        ActionService actionService = (contributor == null) ? UiPlugin.getDefault().getActionService(window.getActivePage()) : contributor.getActionService();
        IHandlerService svc = (IHandlerService)part.getSite().getService(IHandlerService.class);

        Iterator itr = globalActions.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry)itr.next();
            String actionId = (String)entry.getKey();

            // if the actions map indicates that a default action should be used, the map will not contain that
            // action. need to get it from the service
            IAction action = null;
            if( globalActions.isDefaultAction(actionId) && actionService != null ) {
            	action = actionService.getDefaultAction(actionId);
            } else {
            	action = (IAction)entry.getValue();
            }

            if (action == null) {
                action = GlobalActionsMap.UNSUPPORTED_ACTION;
            }

            // must deactive if already one activated in order to get rid of an Eclipse warning message
            if (this.actionHandlerMap.containsKey(actionId)) {
                IHandlerActivation activation = this.actionHandlerMap.get(actionId);
                if( activation.getHandlerService() != null ) {
                	activation.getHandlerService().deactivateHandler(activation);
                }
            }

            getActionBars().setGlobalActionHandler(actionId, action);

            this.actionHandlerMap.put(actionId, svc.activateHandler(actionId, new ActionHandler(action)));
        }

        // must call this if action bars have been changed
        getActionBars().updateActionBars();
    }

    /**
     * Sets the model file being edited by the <code>ModelEditor</code>. Updates the status bar with the readonly state of the
     * file.
     * 
     * @param theInput the model file 
     */
    public void setEditorInput( IFileEditorInput theInput ) {
        input = theInput;
        setReadOnlyState();
    }

    /** Sets the Read-only/Writable state of the editor resource into the status bar. */
    public void setReadOnlyState() {

        if (input != null && fileStateItem != null) {
            // update status bar file state field
            fileStateItem.setText(getReadOnlyState() ? Util.getString("ModelerEditorActionContributor.modelIsReadOnly") //$NON-NLS-1$
            : Util.getString("ModelerEditorActionContributor.modelIsWritable")); //$NON-NLS-1$
        }
    }

    /**
     * @return true if model is read-only
     */
    public boolean getReadOnlyState() {
        if (input != null) {
            return ModelUtil.isIResourceReadOnly(input.getFile());
        }

        return true;
    }

    /**
     * Listener to clean up the part contributor map.
     * 
     * @since 5.0
     */
    class EditorPartListener implements IPartListener {

        @Override
		public void partActivated( IWorkbenchPart thePart ) {
        }

        @Override
		public void partBroughtToTop( IWorkbenchPart thePart ) {
        }

        @Override
		public void partDeactivated( IWorkbenchPart thePart ) {
        }

        @Override
		public void partOpened( IWorkbenchPart thePart ) {
        }

        @Override
		public void partClosed( IWorkbenchPart thePart ) {
            handlePartClosed(thePart);
        }
    }
}
