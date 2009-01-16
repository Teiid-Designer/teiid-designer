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

package com.metamatrix.modeler.mapping.ui.editor;

public interface IBindingListViewer {
	
    /**
     * Update the view to reflect the fact that a binding was added 
     * to the binding list
     * 
     * @param binding
     */
    public void addBinding(BindingAdapter binding);
    
    /**
     * Update the view to reflect the fact that a binding was added 
     * to the binding list
     * 
     * @param binding
     */
    public void insertBinding(BindingAdapter binding, int index);
    
    /**
     * Update the view to reflect the fact that bindings were added 
     * to the binding list
     * 
     * @param bindings
     */
    public void addBindings(Object[] bindings);
	
    /**
     * Update the view to reflect the fact that a binding was removed 
     * from the binding list
     * 
     * @param binding
     */
    public void removeBinding(BindingAdapter binding);
    
    /**
     * Update the view to reflect the fact that bindings were removed 
     * from the binding list
     * 
     * @param binding
     */
    public void removeBindings(Object[] bindings);
	
    /**
     * Update the view to reflect the fact that one of the bindings
     * was modified 
     * 
     * @param binding
     */
    public void updateBinding(BindingAdapter binding);
    
    /**
     * Update the view to reflect the fact that one of the bindings
     * was modified 
     * 
     * @param updateLabels
     */
    public void refresh(boolean updateLabels);
}
