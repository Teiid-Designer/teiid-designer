/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.actions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.WorkbenchPage;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.InternalUiConstants;

/**
 * The <code>AbstractActionService</code> class is a base implementation of the <code>ActionService</code> interface.
 *
 * @since 8.0
 */
public abstract class AbstractActionService implements ActionService, InternalUiConstants {

    /** The logging prefix. */
    private static final String PREFIX = "AbstractActionService."; //$NON-NLS-1$

    /** The workbench window action bars. */
    private IActionBars actionBars;

    /** Map of registered actions. Key type is action <code>Class</code> name and value type is <code>IAction</code>. */
    private Map<String, IAction> actionMap = new HashMap<String, IAction>();

    /** The plugin associated with this service. */
    private AbstractUiPlugin plugin;

    /** The window associated with this action service. */
    private IWorkbenchWindow window;

    private IWorkbenchPage myPage;

    /**
     * Constructs and empty action registry associated with the given plugin.
     * 
     * @param thePlugin the plugin
     */
    public AbstractActionService( AbstractUiPlugin thePlugin,
                                  IWorkbenchPage page ) {
        plugin = thePlugin;
        CoreArgCheck.isNotNull(page); // page should never be null
        myPage = page;
        setWorkbenchWindow(page.getWorkbenchWindow());

        // Assumption/Fact: It is OK to cast IWorkbenchPage to WorkbenchPage since that is the only impl
        // Bad assumption and definitely NOT a fact; this is an internal class that we may not have access to in the future (via
        // access restrictions)
        actionBars = ((WorkbenchPage)page).getActionBars();
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#addResourceChangeListener(org.eclipse.core.resources.IResourceChangeListener)
     */
    @Override
    public void addResourceChangeListener( IResourceChangeListener theListener ) {
        IWorkspace workspace = ModelerCore.getWorkspace();
        workspace.addResourceChangeListener(theListener);
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#addWorkbenchSelectionListener(ISelectionListener)
     */
    @Override
	  public void addWorkbenchSelectionListener( ISelectionListener theListener ) {
        if (window == null) {
            throw new IllegalStateException(Util.getString(PREFIX + "WorkbenchWindowIsNullMessage")); //$NON-NLS-1$
        }

        ISelectionService service = window.getSelectionService();
        IWorkbenchPart activePart = window.getPartService().getActivePart();

        service.addSelectionListener(theListener);
        theListener.selectionChanged(activePart, service.getSelection());
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#addWorkbenchSelectionListener(ISelectionListener)
     */
    public void addPartListener( IPartListener theListener ) {

        if (window == null) {
            throw new IllegalStateException(Util.getString(PREFIX + "WorkbenchWindowIsNullMessage")); //$NON-NLS-1$
        }

        window.getPartService().addPartListener(theListener);
        theListener.partActivated(window.getPartService().getActivePart());
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#contributeToContextMenu(org.eclipse.jface.action.IMenuManager,
     *      org.teiid.designer.ui.common.actions.GlobalActionsMap, org.eclipse.jface.viewers.ISelection)
     */
    @Override
	  public void contributeToContextMenu( IMenuManager theMenuMgr,
                                         GlobalActionsMap theActionsMap,
                                         ISelection theSelection ) {
        // must be overriden in order to contribute
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#contributePermanentActionsToContextMenu(org.eclipse.jface.action.IMenuManager,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
	  public void contributePermanentActionsToContextMenu( IMenuManager theMenuMgr,
                                                         ISelection theSelection ) {
        // must be overriden in order to contribute
    }

    /**
     * Returns the action.Adds this action to the registry if it is not already there.
     * 
     * @param theActionId the fully qualified classname of the action
     * @see org.teiid.designer.ui.common.actions.ActionService#getAction(String)
     */
    @Override
	  public IAction getAction( String theActionId ) throws CoreException {
        CoreArgCheck.isNotNull(theActionId);

        IAction result = actionMap.get(theActionId);

        if (result == null) {
            Class actionClass = null;

            try {
                // must use the plugin's classloader since this class's loader might be different
                // this means that plugins can only load actions found in their class loader
                actionClass = plugin.getClass().getClassLoader().loadClass(theActionId);
                result = getAction(actionClass);
            } catch (ClassNotFoundException theException) {
                String msg = Util.getString(PREFIX + "ProblemFindingActionClassMessage", //$NON-NLS-1$
                                            new Object[] {theActionId});

                throw new CoreException(new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(), 0, // special status code
                                                   // (not used)
                                                   msg, theException));
            }
        }

        return result;
    }

    /**
     * Retrieves the action associated with the specified action class. The class name is used as the action identifier. If the
     * action is not already in the service, the class name is used to construct one. to construct the action.
     * 
     * @param theActionClass the action class associated with the action to retrieve
     * @return the requested action
     * @throws CoreException if action class can't be instantiated
     */
    public IAction getAction( Class<? extends IAction> theActionClass ) throws CoreException {
        CoreArgCheck.isNotNull(theActionClass);
        //        System.out.println("[AbstractActionService.getAction] Action is: " + theActionClass.getName() ); //$NON-NLS-1$

        String actionName = theActionClass.getName();
        IAction result = actionMap.get(actionName);

        if (result == null) {

            try {
                result = theActionClass.newInstance();
                registerEventHandler(result);
                registerAction(actionName, result);
            } catch (Exception theException) {
                System.out.println("[AbstractActionService.getAction] Exception is: " + theException.getClass().getName()); //$NON-NLS-1$

                if ((theException instanceof IllegalAccessException) || (theException instanceof InstantiationException)
                    || (theException instanceof NullPointerException)) {

                    String msg = Util.getString(PREFIX + "FailureConstructingActionMessage", //$NON-NLS-1$
                                                new Object[] {theActionClass});

                    throw new CoreException(new Status(IStatus.ERROR, plugin.getBundle().getSymbolicName(), 0, // special status
                                                       // code (not used)
                                                       msg, theException));
                }
                // unexpected exception
                // MAKE THIS BETTER!!!!!!!
                // Util.
                throw new RuntimeException(theException.getMessage());
            }
        }

        return result;
    }

    /**
     * Gets the action to use for the given action identifier. Will either return the default action or the action supplied by the
     * given action map.
     * 
     * @param theActionId the action identifier
     * @param theActionMap the action map used to store actions
     * @return the appropriate action to use
     */
    protected IAction getAction( String theActionId,
                                 GlobalActionsMap theActionMap ) {
        IAction result = null;

        if (theActionMap.isUnsupportedAction(theActionId)) {
            result = theActionMap.getAction(theActionId);

            // need to set text, tooltip, icons to match default action
            IAction defaultAction = getDefaultAction(theActionId);
            result.setText(defaultAction.getText());
            result.setToolTipText(defaultAction.getToolTipText());
            result.setImageDescriptor(defaultAction.getImageDescriptor());
            result.setHoverImageDescriptor(defaultAction.getHoverImageDescriptor());
            result.setDisabledImageDescriptor(defaultAction.getDisabledImageDescriptor());
        } else {
            result = (theActionMap.isDefaultAction(theActionId)) ? getDefaultAction(theActionId) : theActionMap.getAction(theActionId);
        }

        return result;
    }

    /**
     * Gets the <code>IActionBars</code> associated with the window of this service.
     * 
     * @return the action bars
     */
    protected IActionBars getActionBars() {
        return actionBars;
    }

    /**
     * Gets the plugin associated with this service.
     * 
     * @return the plugin
     */
    @Override
	public AbstractUiPlugin getPlugin() {
        return plugin;
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#getWorkbenchWindow()
     */
    @Override
	public IWorkbenchWindow getWorkbenchWindow() {
        if (window == null) {
            throw new IllegalStateException(Util.getString(PREFIX + "WorkbenchWindowIsNullMessage")); //$NON-NLS-1$
        }

        return window;
    }

    protected IWorkbenchPage getPage() {
        return myPage;
    }

    /**
     * Adds this action to the registry if it is not already there.
     * 
     * @param sActionKey the action's key
     * @param theAction the action to add
     */
    @Override
	public boolean registerAction( String sActionKey,
                                   IAction theAction ) {

        if (actionMap.containsKey(sActionKey)) {
            return false;
        }
        if (!IAction.class.isAssignableFrom(theAction.getClass())) {
            CoreArgCheck.isTrue(IAction.class.isAssignableFrom(theAction.getClass()),
                                Util.getString(PREFIX + "ClassNotAssignableToIActionMessage", //$NON-NLS-1$
                                               new Object[] {theAction}));
        }

        actionMap.put(sActionKey, theAction);
        return true;
    }

    /**
     * Indicates if and action with the given identifier has been registered.
     * 
     * @param theActionId the action identifier
     * @return <code>true</code> if the action is registered; <code>false</code> otherwise.
     */
    @Override
	public boolean isRegistered( String theActionId ) {
        return actionMap.containsKey(theActionId);
    }

    /**
     * Registers the given action to receive {@link org.eclipse.jface.viewers.ISelection} events and
     * {@link org.eclipse.core.resources.IResourceChangeEvent}s if the action implements the appropriate interfaces. Subclasses
     * may add more event types. Also will call <code>actionBars.setGlobalActionHandler(IAction)</code> if the action implements
     * {@link org.eclipse.ui.ISelectionListener}.
     * 
     * @param theAction the action being registered to receive events
     */
    protected void registerEventHandler( IAction theAction ) {
        // workbench-level selection events
        if (theAction instanceof ISelectionListener) {
            addWorkbenchSelectionListener((ISelectionListener)theAction);
            actionBars.setGlobalActionHandler(theAction.getId(), theAction);
        }

        // resource changed events
        if (theAction instanceof IResourceChangeListener) {
            addResourceChangeListener((IResourceChangeListener)theAction);
        }

        // resource changed events
        if (theAction instanceof IPartListener) {
            addPartListener((IPartListener)theAction);
        }

    }

    /**
     * Removes the action with the given identifier from the action service.
     * 
     * @param theActionId the identifier of the action being removed
     * @throws IllegalArgumentException if input is null
     */
    @Override
	public void removeAction( String theActionId ) {
        CoreArgCheck.isNotNull(theActionId);
        actionMap.remove(theActionId);
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#removeResourceChangeListener(org.eclipse.core.resources.IResourceChangeListener)
     */
    @Override
    public void removeResourceChangeListener( IResourceChangeListener theListener ) {
        IWorkspace workspace = ModelerCore.getWorkspace();
        workspace.removeResourceChangeListener(theListener);
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#removeWorkbenchSelectionListener(ISelectionListener)
     */
    @Override
	public void removeWorkbenchSelectionListener( ISelectionListener theListener ) {
        if (window == null) {
            throw new IllegalStateException(Util.getString(PREFIX + "WorkbenchWindowIsNullMessage")); //$NON-NLS-1$
        }

        ISelectionService service = window.getSelectionService();

        service.removeSelectionListener(theListener);
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#removeWorkbenchSelectionListener(ISelectionListener)
     */
    public void removePartListener( IPartListener theListener ) {
        if (window == null) {
            throw new IllegalStateException(Util.getString(PREFIX + "WorkbenchWindowIsNullMessage")); //$NON-NLS-1$
        }

        window.getPartService().removePartListener(theListener);
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#setWorkbenchWindow(IWorkbenchWindow)
     */
    @Override
	public void setWorkbenchWindow( IWorkbenchWindow theWindow ) {
        window = theWindow;
    }

    /**
     * Performs shutdown by doing the following for each registered action:
     * <ol>
     * <li>unregisters the action if it is a {@link ISelectionListener},
     * <li>unregisters the action if it is a {@link IResourceChangeListener}, and
     * <li>calls dispose on the action if it is an {@link AbstractAction}.
     * </ol>
     * 
     * @see org.teiid.designer.ui.common.actions.ActionService#shutdown()
     */
    @Override
	public void shutdown() {
        Iterator<IAction> itr = actionMap.values().iterator();

        while (itr.hasNext()) {
            IAction action = itr.next();
            unregisterEventHandler(action);

            if (action instanceof AbstractAction) {
                ((AbstractAction)action).dispose();
            }
        }
    }

    /**
     * Unregisters the given action from receiving {@link org.eclipse.jface.viewers.ISelection} events and
     * {@link org.eclipse.core.resources.IResourceChangeEvent}s if the action implements the appropriate interfaces. Subclasses
     * may unregister other event types.
     * 
     * @param theAction the action being unregistered to receive events
     */
    protected void unregisterEventHandler( IAction theAction ) {
        // workbench-level selection events
        if (theAction instanceof ISelectionListener) {
            removeWorkbenchSelectionListener((ISelectionListener)theAction);
        }

        // resource changed events
        if (theAction instanceof IResourceChangeListener) {
            removeResourceChangeListener((IResourceChangeListener)theAction);
        }
    }

}
