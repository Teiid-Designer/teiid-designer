/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.reconciler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlColumnAspect;

/**
 * BindingList class - for maintaining the list of attribute - SQL Symbol bindings.
 * 
 */
public class BindingList {

	private final int COUNT = 10;
	private List<Binding> bindings = new ArrayList<Binding>(COUNT);
    private List removedBindings = new ArrayList();
	private Set<IBindingListViewer> changeListeners = new HashSet<IBindingListViewer>();

	/**
	 * Constructor
	 */
	public BindingList() {
	}
	
    /**
     * Return the Binding at the supplied index
     * @param index the list index
     * @return the binding at the supplied index
     */
    public Binding get(int index) {
        if(index>=0 && index<bindings.size()) {
            return bindings.get(index);
        } 
        return null;
    }
    
    /**
     * Return the List of all bindings
     * @return the list of Binding objects
     */
    public List getAll() {
        return bindings;
    }
    
    /**
     * Return the List of bindings which have been removed from the main list, but
     * saved for reuse.
     * @return the list of removed Bindings
     */
    public List getRemovedList() {
        return removedBindings;
    }
    
    /**
     * Return the number of bindings
     */
    public int size() {
        return bindings.size();
    }
    
    /**
     * Return whether any of the bindings is bound and has a type conflict.
     * @return 'true' if isBound and the types conflict.
     */
    public boolean hasTypeConflict() {
        boolean hasConflict = false;
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            if( binding.hasTypeConflict() ) {
                hasConflict = true;
                break;
            }
        }
        return hasConflict;
    }
	    
    /**
     * Add a new Binding to the list
     * @param binding the binding to add
     */
    public void add(Binding binding) {
        bindings.add(bindings.size(), binding);
        Iterator<IBindingListViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            iterator.next().addBinding(binding);
    }
    
    /**
     * Add a List of bindings to the binding list.
     * @param bindings the list of Binding object to add
     */
    public void addAll(List bindings) {
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            add((Binding)iter.next());
        }
    }

    /**
     * Insert a new Binding at the specified index
     * @param binding the binding to insert.
     * @param index the index location for insertion
     */
    public void insert(Binding binding,int index) {
        bindings.add(index, binding);
        Iterator<IBindingListViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            iterator.next().insertBinding(binding,index);
    }
    
    /**
     * Remove the supplied binding from the binding list
     * @param binding the binding to remove
     */
    public void remove(Binding binding) {
        bindings.remove(binding);
        if(!removedBindings.contains(binding)) {
            removedBindings.add(binding);
        }
        Iterator<IBindingListViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            iterator.next().removeBinding(binding);
    }

    /**
     * Remove the supplied list of bindings from the binding list
     * @param bindings the bindings to remove
     */
    public void removeAll(List bindings) {
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            remove((Binding)iter.next());
        }
    }
    
    /**
     * Method to determine if there is a removed binding whose attribute name matches the supplied name.
     * @param attrName the supplied attribute name
     * @return 'true' if the removeList has a matching Binding, 'false' if not.
     */
    public boolean hasRemovedBindingMatch(String attrName) {
        boolean hasMatch = false;
        Iterator iter = removedBindings.iterator();
        while(iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            Object attr = binding.getAttribute();
            if( attr!=null && (attr instanceof EObject) && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn((EObject)attr) ) {
                SqlColumnAspect columnAspect = (SqlColumnAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)attr);
                String currentName = columnAspect.getName((EObject)attr);
                if(currentName!=null && currentName.equalsIgnoreCase(attrName)) {
                    hasMatch = true;
                    break;
                }
            }
        }
        return hasMatch;
    }

    /**
     * Remove the binding in the removed list whose attribute name matches the supplied name.
     * @param attrName the supplied attribute name
     * @return the removed Binding, null if not found.
     */
    public Binding getRemovedBindingMatch(String attrName) {
        int bindingIndex = -1;
        Binding bindingMatch = null;
        for(int i=0; i<removedBindings.size(); i++) {
            Binding binding = (Binding)removedBindings.get(i);
            Object attr = binding.getAttribute();
            if( attr!=null && (attr instanceof EObject) && com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.isColumn((EObject)attr) ) {
                SqlColumnAspect columnAspect = (SqlColumnAspect)com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspectHelper.getSqlAspect((EObject)attr);
                String currentName = columnAspect.getName((EObject)attr);
                if(currentName!=null && currentName.equalsIgnoreCase(attrName)) {
                    bindingIndex = i;
                    break;
                }
            }
        }
        if(bindingIndex>=0) {
            bindingMatch = (Binding)removedBindings.remove(bindingIndex);
        }
        return bindingMatch;
    }

	/**
     * Method to notify that the supplied binding has changed
	 * @param binding that changed.
	 */
	public void bindingChanged(Binding binding) {
		Iterator<IBindingListViewer> iterator = changeListeners.iterator();
		while (iterator.hasNext())
			iterator.next().updateBinding(binding);
	}

    /**
     * Method to notify that a refresh is required
     * @param updateLabels 'true' if label update is required, 'false' if not.
     */
    public void refresh(boolean updateLabels) {
        Iterator<IBindingListViewer> iterator = changeListeners.iterator();
        while (iterator.hasNext())
            iterator.next().refresh(updateLabels);
    }

    /**
     * Get the index of a binding in the list
     * @param binding the Binding to find the index for
     * @return the index of the binding, -1 if not found.
     */
    public int indexOf(Binding binding) {
        return bindings.indexOf(binding);
    }

    /**
     * Get the first Unbound binding in the list
     * @return the first unbound binding in the List, null if none exist
     */
    public Binding getFirstUnbound() {
        Binding result = null;
        // Iterate and find the first unbound
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            Binding binding = (Binding)iter.next();
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
    public Binding getFirstBound() {
        Binding result = null;
        // Iterate and find the first bound
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            if(binding.isBound()) {
                result = binding;
                break;
            }
        }
        return result;
    }
    
    /**
     * Get the first binding in the list with a type conflict
     * @return the first binding in the List with a type conflict, null if none exist
     */
    public Binding getFirstTypeConflict() {
        Binding result = null;
        // Iterate and find the first bound
        Iterator iter = bindings.iterator();
        while(iter.hasNext()) {
            Binding binding = (Binding)iter.next();
            if(binding.hasTypeConflict()) {
                result = binding;
                break;
            }
        }
        return result;
    }

    /**
     * Get the next Unbound binding in the list, after the supplied Binding
     * @param the supplied binding
     * @return the next unbound binding in the List, null if none exist
     */
    public Binding getNextUnbound(Binding binding) {
        Binding result = null;
        // Index of the supplied binding
        int index = indexOf(binding);
        
        // Look from the current index to the end of the list
        for(int i=index+1; i<size(); i++) {
            Binding nextBinding = bindings.get(i);
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
     * Get the next Bound binding in the list, after the supplied Binding
     * @param the supplied binding
     * @return the next bound binding in the List, null if none exist
     */
    public Binding getNextBound(Binding binding) {
        Binding result = null;
        // Index of the supplied binding
        int index = indexOf(binding);
        
        // Look from the current index to the end of the list
        for(int i=index+1; i<size(); i++) {
            Binding nextBinding = bindings.get(i);
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
     * Get the next binding with a type conflict in the list, after the supplied Binding
     * @param the supplied binding
     * @return the next binding with a type Conflict in the List, null if none exist
     */
    public Binding getNextTypeConflict(Binding binding) {
        Binding result = null;
        // Index of the supplied binding
        int index = indexOf(binding);
        
        // Look from the current index to the end of the list
        for(int i=index+1; i<size(); i++) {
            Binding nextBinding = bindings.get(i);
            if(nextBinding.hasTypeConflict()) {
                result = nextBinding;
                break;
            }
        }
        
        // If not found so far, start at the top
        if(result==null) {
            return getFirstTypeConflict();
        }

        return result;
    }

    /**
     * Move a Binding up in the list
     * @param binding the binding to move down
     */
    public void moveUp(Binding binding) {
        int currentIndex = indexOf(binding);
        if(currentIndex>0) {
            Binding removedBinding = bindings.remove(currentIndex);
            bindings.add(--currentIndex,removedBinding);
            refresh(true);
        }
    }
    
    /**
     * Move a Binding up to top of the list
     * @param binding the binding to move to top
     */
    public void moveTop(Binding binding) {
        int currentIndex = indexOf(binding);
        if(currentIndex>0) {
            Binding removedBinding = bindings.remove(currentIndex);
            bindings.add(0,removedBinding);
            refresh(true);
        }
    }
    
    /**
     * Move a Binding up to top of the list
     * @param binding the binding to move to top
     */
    public void moveBottom(Binding binding) {
        int currentIndex = indexOf(binding);
        if(currentIndex>-1 && currentIndex<bindings.size()-1) {
            Binding removedBinding = bindings.remove(currentIndex);
            bindings.add(removedBinding);
            refresh(true);
        }
    }
    
    /**
     * Move a Binding down in the list
     * @param binding the binding to move down
     */
    public void moveDown(Binding binding) {
        int index = indexOf(binding);
        if(index>-1 && index<bindings.size()-1) {
            Binding removedBinding = bindings.remove(index);
            bindings.add(++index,removedBinding);
            refresh(true);
        }
    }
    
    /**
     * Swap Bindings in the list
     * @param binding1 the binding to swap with the second binding
     * @param binding2 the binding to swap with the first binding
     */
    public void swap(Binding binding1, Binding binding2) {
        int index1 = indexOf(binding1);
        int index2 = indexOf(binding2);
        if(index1>-1 && index2>-1) {
            Binding obj1 = bindings.get(index1);
            Binding obj2 = bindings.get(index2);
            bindings.set(index2,obj1);
            bindings.set(index1,obj2);
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
