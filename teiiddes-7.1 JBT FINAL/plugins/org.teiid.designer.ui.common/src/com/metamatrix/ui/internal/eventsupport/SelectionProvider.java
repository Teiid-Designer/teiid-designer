/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.internal.eventsupport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * SelectionProvider is a simple implementation of ISelectionProvider that handles the listener list
 * and firing events to it.  Can be either extended or used directly.  
 * Now why didn't Eclipse think of this? :-)
 */
public class SelectionProvider implements ISelectionProvider {

    // ==================================================
    // Variables
        
    private ArrayList<ISelectionChangedListener> listenerList = new ArrayList<ISelectionChangedListener>();
    private ISelection currentSelection;
    
    // ==================================================
    // Constructors
    
    /**
     * Create a SelectionProvider.
     */
    public SelectionProvider() {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public synchronized void addSelectionChangedListener(ISelectionChangedListener listener) {
        if ( listener != null && ! listenerList.contains(listener) ) {
            listenerList.add(listener);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
     */
    public ISelection getSelection() {
        return currentSelection;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
     */
    public synchronized void removeSelectionChangedListener(ISelectionChangedListener listener) {
        listenerList.remove(listener);
    }

    /**
     * Convenience method, allowing users to set selection directly from another 
     * SelectionChangedEvent.  Will re-fire the event to all listeners.
     * @param event an event to set as this object's current selection and re-fire
     * to all listeners registered with this object.
     */
    public void setSelection(SelectionChangedEvent event) {
        if ( event == null ) {
            currentSelection = null;
        } else {
            currentSelection = event.getSelection();
            fireSelectionChangedEvent(event);
        }
    }

    /**
     * Set this provider's selection and fire a selection event to all listeners, where this object
     * will be the source of the event.
     * @param selectedObjects the intended content of the selection
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(List selectedObjects) {
        if ( selectedObjects != null ) {
            this.setSelection(new StructuredSelection(selectedObjects), true, null);
        }
    }

    /**
     * Set this provider's selection and fire a selection event to all listeners, where this object
     * will be the source of the event.
     * @param selection the content of the selection
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection) {
        this.setSelection(selection, true, null);
    }

    /**
     * Set this provider's selection and optionally fire a selection event to all listeners, where 
     * this object will be the source of the event.
     * @param selection the content of the selection
     * @param fireEvent controls whether or not listeners to this SelectionProvider will be notified
     * of the selection via a SelectionChangedEvent.
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection, boolean fireEvent) {
        this.setSelection(selection, fireEvent, null);
    }

    /**
     * Set this provider's selection and optionally fire a selection event to all listeners, where 
     * the specified ISelectionProvider will be the source of the event.
     * @param selection the content of the selection
     * @param fireEvent controls whether or not listeners to this SelectionProvider will be notified
     * of the selection via a SelectionChangedEvent.
     * @param source an optional ISelectionProvider for the source of the event.  Ignored if fireEvent
     * is false; if source is null, this object will be the source of the fired event.
     * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
     */
    public void setSelection(ISelection selection, boolean fireEvent, ISelectionProvider source) {
        currentSelection = selection;
        if ( fireEvent ) {
            if ( source == null ) {
                source = this;
            }
            fireSelectionChangedEvent(new SelectionChangedEvent(source, selection));
        }
    }

    /**
     * fires the specified SelectionChangedEvent to all SelectionChangedListeners registered with
     * this object.
     * @param event
     */
    protected synchronized void fireSelectionChangedEvent(SelectionChangedEvent event) {
        // Prevents concurrent modification of listener list
        List<ISelectionChangedListener> copyOfListenerList = new ArrayList<ISelectionChangedListener>(listenerList);
        for ( Iterator<ISelectionChangedListener> iter = copyOfListenerList.iterator() ; iter.hasNext() ; ) {
            iter.next().selectionChanged(event);
        }
    }
    
    protected List<ISelectionChangedListener> getListenerList() {
        return listenerList;
    }
    
}
