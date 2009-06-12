/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.mapping.ui.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.metamatrix.metamodels.transformation.InputParameter;

/**
 * BindingList class - for maintaining the list of attribute - attribute bindings.
 * 
 */
public class BindingList {

	private final int COUNT = 10;
	private List bindings = new ArrayList(COUNT);
	private Set changeListeners = new HashSet();

	/**
	 * Constructor
	 */
	public BindingList() {
	}
	
    /**
     * Return the BindingAdapter at the supplied index
     * @param index the list index
     * @return the binding at the supplied index
     */
    public BindingAdapter get(int index) {
        if(index>=0 && index<bindings.size()) {
            return (BindingAdapter)bindings.get(index);
        } 
        return null;
    }
    
    /**
     * Return the List of all bindings
     * @return the list of BindingAdapter objects
     */
    public List getAll() {
        return bindings;
    }
    
    public boolean contains( InputParameter inputParm ) {
        boolean hasMatch = false;
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            BindingAdapter binding = (BindingAdapter)iter.next();
            Object item = binding.getItem();
            if( item!=null && item.equals(inputParm) ) {
                hasMatch = true;
                break;
            }
        }
        return hasMatch;    
    }
    
    public BindingAdapter getBindingFor( InputParameter inputParm ) {
        BindingAdapter result = null;
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            BindingAdapter binding = (BindingAdapter)iter.next();
            Object item = binding.getItem();
            if( item!=null && item.equals(inputParm) ) {
                result = binding;
                break;
            }
        }
        return result;    
    }

    /**
     * Return the number of bindings
     */
    public int size() {
        return bindings.size();
    }
	    
    /**
     * Add a new BindingAdapter to the list
     * @param binding the binding to add
     */
    public void add(BindingAdapter binding) {
        bindings.add(bindings.size(), binding);
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext())
            ((IBindingListViewer) iterator.next()).addBinding(binding);
    }
    
    /**
     * Add a List of bindings to the binding list.
     * @param bindings the list of BindingAdapter object to add
     */
    public void addAll(List bindings) {
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            add((BindingAdapter)iter.next());
        }
    }

    /**
     * Insert a new BindingAdapter at the specified index
     * @param binding the binding to insert.
     * @param index the index location for insertion
     */
    public void insert(BindingAdapter binding,int index) {
        bindings.add(index, binding);
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext())
            ((IBindingListViewer) iterator.next()).insertBinding(binding,index);
    }
    
    /**
     * Remove the supplied binding from the binding list
     * @param binding the binding to remove
     */
    public void remove(BindingAdapter binding) {
        bindings.remove(binding);
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext())
            ((IBindingListViewer) iterator.next()).removeBinding(binding);
    }

    /**
     * Remove the supplied list of bindings from the binding list
     * @param bindings the bindings to remove
     */
    public void removeAll(List bindings) {
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            remove((BindingAdapter)iter.next());
        }
    }
    
	/**
     * Method to notify that the supplied binding has changed
	 * @param binding that changed.
	 */
	public void bindingChanged(BindingAdapter binding) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((IBindingListViewer) iterator.next()).updateBinding(binding);
	}

    /**
     * Method to notify that a refresh is required
     * @param updateLabels 'true' if label update is required, 'false' if not.
     */
    public void refresh(boolean updateLabels) {
        Iterator iterator = changeListeners.iterator();
        while (iterator.hasNext())
            ((IBindingListViewer) iterator.next()).refresh(updateLabels);
    }

    /**
     * Get the index of a binding in the list
     * @param binding the BindingAdapter to find the index for
     * @return the index of the binding, -1 if not found.
     */
    public int indexOf(BindingAdapter binding) {
        return bindings.indexOf(binding);
    }

    /**
     * Get the first Unbound binding in the list
     * @return the first unbound binding in the List, null if none exist
     */
    public BindingAdapter getFirstUnbound() {
        BindingAdapter result = null;
        // Iterate and find the first unbound
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            BindingAdapter binding = (BindingAdapter)iter.next();
            if(!binding.isBound()) {
                result = binding;
                break;
            }
        }
        return result;
    }
    
    /**
     * Get the first Bound binding in the list
     * @return the first bound binding in the List, null if none exist
     */
    public BindingAdapter getFirstBound() {
        BindingAdapter result = null;
        // Iterate and find the first bound
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            BindingAdapter binding = (BindingAdapter)iter.next();
            if(binding.isBound()) {
                result = binding;
                break;
            }
        }
        return result;
    }

    /**
     * Get the next Unbound binding in the list, after the supplied BindingAdapter
     * @param the supplied binding
     * @return the next unbound binding in the List, null if none exist
     */
    public BindingAdapter getNextUnbound(BindingAdapter binding) {
        BindingAdapter result = null;
        // Index of the supplied binding
        int index = indexOf(binding);
        
        // Look from the current index to the end of the list
        for(int i=index+1; i<size(); i++) {
            BindingAdapter nextBinding = (BindingAdapter)bindings.get(i);
            if(!nextBinding.isBound()) {
                result = nextBinding;
                break;
            }
        }
        
        // If not found so far, start at the top
        if(result==null) {
            return getFirstUnbound();
        }

        return result;
    }
    
    /**
     * Get the next Bound binding in the list, after the supplied BindingAdapter
     * @param the supplied binding
     * @return the next bound binding in the List, null if none exist
     */
    public BindingAdapter getNextBound(BindingAdapter binding) {
        BindingAdapter result = null;
        // Index of the supplied binding
        int index = indexOf(binding);
        
        // Look from the current index to the end of the list
        for(int i=index+1; i<size(); i++) {
            BindingAdapter nextBinding = (BindingAdapter)bindings.get(i);
            if(nextBinding.isBound()) {
                result = nextBinding;
                break;
            }
        }
        
        // If not found so far, start at the top
        if(result==null) {
            return getFirstBound();
        }

        return result;
    }
    
    /**
     * Move a BindingAdapter up in the list
     * @param binding the binding to move down
     */
    public void moveUp(BindingAdapter binding) {
        int currentIndex = indexOf(binding);
        if(currentIndex>0) {
            BindingAdapter removedBinding = (BindingAdapter)bindings.remove(currentIndex);
            bindings.add(--currentIndex,removedBinding);
            refresh(true);
        }
    }
    
    /**
     * Move a BindingAdapter down in the list
     * @param binding the binding to move down
     */
    public void moveDown(BindingAdapter binding) {
        int index = indexOf(binding);
        if(index>-1 && index<bindings.size()-1) {
            BindingAdapter removedBinding = (BindingAdapter)bindings.remove(index);
            bindings.add(++index,removedBinding);
            refresh(true);
        }
    }
    
	/**
     * Remove the ChangeListener
	 * @param viewer the change listener to remove
	 */
	public void removeChangeListener(IBindingListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
     * Add the supplied ChangeListener
	 * @param viewer the change listener to add
	 */
	public void addChangeListener(IBindingListViewer viewer) {
		changeListeners.add(viewer);
	}

}
