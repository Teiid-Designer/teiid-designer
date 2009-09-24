/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
