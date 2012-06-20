/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.actions;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.teiid.designer.ui.common.AbstractUiPlugin;

/**
 * @since 4.0
 */
public class FakeActionService implements ActionService {

    private IWorkbenchWindow window;

    /**
     * @since 4.0
     */
    public FakeActionService( IWorkbenchWindow theWindow ) {
        super();
        setWorkbenchWindow(theWindow);
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#contributeToContextMenu(org.eclipse.jface.action.IMenuManager,
     *      org.teiid.designer.ui.common.actions.GlobalActionsMap, org.eclipse.jface.viewers.ISelection)
     */
    @Override
	public void contributeToContextMenu( IMenuManager theMenuMgr,
                                         GlobalActionsMap theActionsMap,
                                         ISelection theSelection ) {
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#contributePermanentActionsToContextMenu(org.eclipse.jface.action.IMenuManager,
     *      org.eclipse.jface.viewers.ISelection)
     */
    @Override
	public void contributePermanentActionsToContextMenu( IMenuManager theMenuMgr,
                                                         ISelection theSelection ) {
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#getAction(java.lang.String)
     * @since 4.0
     */
    @Override
	public IAction getAction( String theActionId ) {
        return null;
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#getDefaultAction(java.lang.String)
     */
    @Override
	public IAction getDefaultAction( String theActionId ) {
        return null;
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#addResourceChangeListener(IResourceChangeListener)
     */
    @Override
	public void addResourceChangeListener( IResourceChangeListener theListener ) {
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#addWorkbenchSelectionListener(org.eclipse.ui.ISelectionListener)
     * @since 4.0
     */
    @Override
	public void addWorkbenchSelectionListener( ISelectionListener theListener ) {
        if (window == null) {
            throw new IllegalStateException("Workbench window has not been set."); //$NON-NLS-1$
        }
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#removeResourceChangeListener(IResourceChangeListener)
     */
    @Override
	public void removeResourceChangeListener( IResourceChangeListener theListener ) {
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#removeWorkbenchSelectionListener(org.eclipse.ui.ISelectionListener)
     * @since 4.0
     */
    @Override
	public void removeWorkbenchSelectionListener( ISelectionListener theListener ) {
        if (window == null) {
            throw new IllegalStateException("Workbench window has not been set."); //$NON-NLS-1$
        }
    }

    /**
     * Gets the plugin associated with this service.
     * 
     * @return the plugin
     */
    @Override
	public AbstractUiPlugin getPlugin() {
        return null;
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#getWorkbenchWindow()
     */
    @Override
	public IWorkbenchWindow getWorkbenchWindow() {
        if (window == null) {
            throw new IllegalStateException("Workbench window has not been set."); //$NON-NLS-1$
        }

        return window;
    }

    /**
     * Registers an action.
     * 
     * @param theActionId the action identifier
     * @param theAction the action
     * @return true if successful
     */
    @Override
	public boolean registerAction( String sActionId,
                                   IAction theAction ) {
        return false;
    }

    /**
     * Indicates if and action with the given identifier has been registered.
     * 
     * @param theActionId the action identifier
     * @return <code>true</code> if the action is registered; <code>false</code> otherwise.
     */
    @Override
	public boolean isRegistered( String theActionId ) {
        return false;
    }

    /**
     * Removes the action with the given identifier from the action service.
     * 
     * @param theActionId the identifier of the action being removed
     * @throws AssertionError if input is null
     */
    @Override
	public void removeAction( String theActionId ) {
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#setWorkbenchWindow(IWorkbenchWindow)
     */
    @Override
	public void setWorkbenchWindow( IWorkbenchWindow theWindow ) {
        window = theWindow;
    }

    /**
     * @see org.teiid.designer.ui.common.actions.ActionService#shutdown()
     */
    @Override
	public void shutdown() {
    }

}
