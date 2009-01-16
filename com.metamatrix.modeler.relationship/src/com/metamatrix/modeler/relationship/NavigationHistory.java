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

package com.metamatrix.modeler.relationship;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

/**
 * NavigationHistory
 */
public interface NavigationHistory {
    
    /**
     * Obtain the current NavigationContext.
     * @return the current NavigationContext, or null if there is none.
     */
    public NavigationContext getCurrent();
    
    /**
     * Obtain the NavigationContext that was immediately before the {@link #getCurrent() current context}.
     * @return the previous NavigationContext, or null if there is none.
     * @see #hasPrevious()
     * @throws NavigationContextException if there is a problem reconstituting the requested navigation context.
     * This may happen if the focused object were moved or deleted.
     */
    public NavigationContext getPrevious() throws NavigationContextException;
    
	/**
	 * Obtain the NavigationContext that was immediately before the {@link #getCurrent() current context}.
	 * @return the previous NavigationContext, or null if there is none.
	 * @see #hasPrevious()
	 * @throws NavigationContextException if there is a problem reconstituting the requested navigation context.
	 * This may happen if the focused object were moved or deleted.
	 */
	public NavigationContext peakAtPrevious() throws NavigationContextException;
    
    /**
     * Obtain the NavigationContext that was immediately after the {@link #getCurrent() current context}.
     * @return the next NavigationContext, or null if there is none.
     * @see #hasNext()
     * @throws NavigationContextException if there is a problem reconstituting the requested navigation context.
     * This may happen if the focused object were moved or deleted.
     */
    public NavigationContext getNext() throws NavigationContextException;
    
	/**
	 * Obtain the NavigationContext that was immediately after the {@link #getCurrent() current context}.
	 * @return the next NavigationContext, or null if there is none.
	 * @see #getNext()
	 * @throws NavigationContextException if there is a problem reconstituting the requested navigation context.
	 * This may happen if the focused object were moved or deleted.
	 */
	public NavigationContext peakAtNext() throws NavigationContextException;
    
    /**
     * Determine whether there is a NavigationContext immediately before the {@link #getCurrent() current context}.
     * @return true if there is a previous NavigationContext, or false otherwise.
     * @see #getPrevious()
     */
    public boolean hasPrevious();
    
    /**
     * Determine whether there is a NavigationContext immediately after the {@link #getCurrent() current context}.
     * @return true if there is a next NavigationContext, or false otherwise.
     * @see #getNext()
     */
    public boolean hasNext();
    
    /**
     * Clear this history of all but the current context.
     */
    public void clearHistory();
    
    /**
     * Get the context descriptors for the NavigationContext instances that are before the 
     * {@link #getCurrent() current context}.
     * @return the list of {@link NavigationContextInfo context information}; may be empty, but never null
     */
    public List getBackInfos();

    /**
     * Get the context descriptors for the NavigationContext instances that are after the 
     * {@link #getCurrent() current context}.
     * @return the list of {@link NavigationContextInfo context information}; may be empty, but never null
     */
    public List getForwardInfos();
    
    /**
     * Set the {@link #getCurrent() current NavigationContext} to one that was once the current.  This method
     * is called with one of the values in the {@link #getBackInfos() back} or {@link #getForwardInfos() forward}
     * descriptor lists.
     * @param info the {@link NavigationContextInfo context information} for the context that is to be made
     * current; may not be null
     * @return the NavigationContext that was selected; never null
     * @see #getBackInfos()
     * @see #getForwardInfos()
     * @throws NavigationContextException if there is a problem reconstituting the requested navigation context.
     * This may happen if the focused object were moved or deleted, or if the supplied info was not actually
     * from the history.
     */
    public NavigationContext selectFromHistory( final NavigationContextInfo info ) throws NavigationContextException;
    
    /**
     * Set the {@link #getCurrent() current NavigationContext} for the supplied object.  The behavior
     * is the same whether or not the supplied object is in the history list.
     * @param focusObject the object for which the NavigationContext is to be computed; may not be null
     * @return the current NavigationContext; never null
     * @throws NavigationContextException if there is a problem reconstituting the navigation context.
     * This may happen if the focused object were moved or deleted.
     */
    public NavigationContext navigateTo( final EObject focusObject ) throws NavigationContextException;
    
    /**
     * Refresh the navigation contexts in the history by recomputing the relations in each.  This
     * will cause the {@link #getCurrent() current NavigationContext} to be invalidated, so after this
     * method is called, clients must call {@link #getCurrent()} to obtain the latest (refreshed)
     * NavigationContext.
     * @throws NavigationContextException if there is a problem reconstituting the navigation context.
     * This may happen if the focused object were moved or deleted.
     */
    public void refresh() throws NavigationContextException;
    
    /**
    * Set the {@link #getCurrent() current NavigationContext} for the supplied object.  The behavior
    * is the same whether or not the supplied object is in the history list.
    * @param node the object for which the NavigationContext is to be computed; may not be null
    * @return the current NavigationContext; never null
    * @throws NavigationContextException if there is a problem reconstituting the navigation context.
    * This may happen if the focused object were moved or deleted.
    */
   public NavigationContext navigateTo( final NavigationNode node ) throws NavigationContextException;
    
    /**
     * Set the {@link #getCurrent() current NavigationContext} for the supplied object URI.  The behavior
     * is the same whether or not the supplied object URI is in the history list.
     * @param focusObjectUri the URI for the object for which the NavigationContext is to be computed; may not be null
     * @param focusObject the EObject for the object for which the NavigationContext is to be computed; may not be null
     * @return the current NavigationContext; never null
     * @throws NavigationContextException if there is a problem reconstituting the navigation context.
     * This may happen if the focused object were moved or deleted.
     */
    public NavigationContext navigateTo( final URI focusObjectUri, final EObject focusObject ) throws NavigationContextException;
    
    /**
     * Set the {@link #getCurrent() current NavigationContext} for the supplied object URI.  The behavior
     * is the same whether or not the supplied object URI is in the history list.
     * @param focusObjectUri the URI for the object for which the NavigationContext is to be computed; may not be null
     * @return the current NavigationContext; never null
     * @throws NavigationContextException if there is a problem reconstituting the navigation context.
     * This may happen if the focused object were moved or deleted.
     */
    public NavigationContext navigateTo( final URI focusObjectUri ) throws NavigationContextException;

}
