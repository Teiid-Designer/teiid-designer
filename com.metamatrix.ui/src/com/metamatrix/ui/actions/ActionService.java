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

package com.metamatrix.ui.actions;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.ui.AbstractUiPlugin;

/**
 * The <code>ActionService</code> interface defines how to create and obtain actions registered in the
 * service. Since each workbench window has it's own selection service, each window must have their own
 * action service.
 */
public interface ActionService {
    
    /**
     * Fills the given menu with the standard items appropriate for a context menu of a part in
     * the Modeler. 
     * @param theMenuMgr the context menu being contributed to
     * @param theActionsMap the map of global action handlers
     * @param theSelection the current selection
     */
    void contributeToContextMenu(IMenuManager theMenuMgr, GlobalActionsMap theActionsMap, ISelection theSelection);
    
    /**
     * Gives the <code>IModelObjectActionContributor</code>s a chance to contribute to the context menu
     * @param theMenuMgr the context menu being contributed to
     * @param theSelection the current selection
     */
    void contributePermanentActionsToContextMenu(IMenuManager theMenuMgr, ISelection theSelection);

    /**
     * Gives the <code>IModelObjectActionContributor</code>s a chance to contribute to the Edit menu
     * @param theMenuMgr the Edit menu
     * @param theSelection the current selection
     */
    void contributePermanentActionsToEditMenu(IMenuManager theMenuMgr, ISelection theSelection);
    
    /**
     * Notifies the service that it is no longer needed. The service should shutdown and dispose of any
     * system resources.
     */
    public void shutdown();

    /**
     * Gets the action with the given identifier from the registry. Action identifier's are their class name.
     * Only one action for each class will be managed by this service. If an action with the given identifier
     * is not found, the service will construct one.
     * @param theActionId the identifier (class name) of the requested action
     * @return the action or <code>null</code> if action is not found
     * @throws CoreException if identifier is null or if action cannot be constructed
     */
    IAction getAction(String theActionId) throws CoreException;
    
    /**
     * Gets a default action for the given identifier.
     * @param theActionId the action identifier
     * @return the default action
     */
    IAction getDefaultAction(String theActionId);
    
    /**
     * Adds the given listener to the list receiving editor selection events.
     * @param theEditor the editor whose events will be listened to
     * @param theListener the listener being added
     */
    void addEditorSelectionListener(IEditorPart theEditor, ISelectionChangedListener theListener);
    
    /** 
     * Adds the given listener to the workspace to receive {@link org.eclipse.core.resources.IResourceChangeEvent}s.
     * @param listener the listener being added
     */
    void addResourceChangeListener(IResourceChangeListener theListener);
    
    /**
     * Adds the given listener to the list receiving view selection events.
     * @param theView the view whose events will be listened to
     * @param theListener the listener being added
     */
    void addViewSelectionListener(IViewPart theView, ISelectionChangedListener theListener);
    
    /**
     * Adds the given listener to the list receiving workbench selection events.
     * @param theListener the listener being added
     * @throws IllegalStateException if the window has not been set
     */
    void addWorkbenchSelectionListener(ISelectionListener theListener);
    
    /**
     * Removes the given listener from the list receiving editor selection events.
     * @param theEditor the editor whose events are no longer listened to
     * @param theListener the listener being removed
     */
    void removeEditorSelectionListener(IEditorPart theEditor, ISelectionChangedListener theListener);
    
    /** 
     * Removes the given listener from the workspace from receiving {@link org.eclipse.core.resources.IResourceChangeEvent}s.
     * @param listener the listener being removed
     */
    void removeResourceChangeListener(IResourceChangeListener theListener);
    
    /**
     * Removes the given listener from the list receiving view selection events.
     * @param theView the view whose events are no longer listened to
     * @param theListener the listener being removed
     */
    void removeViewSelectionListener(IViewPart theView, ISelectionChangedListener theListener);
    
    /**
     * Removes the given listener from the list receiving workbench selection events.
     * @param theListener the listener being removed
     * @throws IllegalStateException if the window has not been set
     */
    void removeWorkbenchSelectionListener(ISelectionListener theListener);
    
    /**
     * Gets the plugin associated with this service.
     * @return the plugin
     */
    AbstractUiPlugin getPlugin();
     
    
    /**   
     * Convenience version of registerAction.  Key defaults to getId of the action.  
     * @param theActionId the action identifier
     * @return true if successful
     */
    boolean registerAction( IAction theAction );
    
    /**   
     * Registers an action.  
     * @param theActionId the action identifier
     * @param theAction the action  
     * @return true if successful
     */
    boolean registerAction( String sActionId, IAction theAction );

    
    /**   
     * Registers an action.  
     * @param theActionId the action identifier
     * @param theAction the action  
     * @param bForce true if this action should replace an exising one with the same key  
     * @return true if successful
     */
    boolean registerAction( String sActionId, IAction theAction, boolean bForce );
    
    /**
     * Indicates if and action with the given identifier has been registered.
     * @param theActionId the action identifier
     * @return <code>true</code> if the action is registered; <code>false</code> otherwise.
     */
    boolean isRegistered(String theActionId);
    
    /**
     * Indicates if and action with the given <code>Class</code> has been registered.
     * @param theActionClass the action class
     * @return <code>true</code> if the action is registered; <code>false</code> otherwise.
     */
    boolean isRegistered(Class<IAction> theActionClass);
    
    /**
     * Removes the action with the given identifier from the action service.
     * @param theActionId the identifier of the action being removed
     * @throws com.metamatrix.core.util.AssertionError if input is null
     */
    void removeAction(String theActionId);
    
    /**
     * Removes the action with the given from the action service.
     * @param theActionClass the class of the action being removed
     * @throws com.metamatrix.core.util.AssertionError if input is null
     */
    void removeAction(Class<IAction> theActionClass);
    
    /**
     * Sets the <code>IWorkbenchWindow</code> associated with this action service. <strong>Must</strong> be 
     * called during initialization.
     * @param theWindow the window
     */
    void setWorkbenchWindow(IWorkbenchWindow theWindow);
    
    /**
     * Gets the <code>IWorkbenchWindow</code> associated with this action service. Each action service
     * <strong>MUST</code> be associated to a window.
     * @return the associated window
     * @throws IllegalStateException if the window has not been set
     */
    IWorkbenchWindow getWorkbenchWindow();

}
