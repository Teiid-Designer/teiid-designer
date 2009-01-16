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

package com.metamatrix.ui.internal.eventsupport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * The <code>CompositeSelectionChangedListener</code> class contains a collection of
 * <code>ISelectionChangedListener</code>s. This collection of listeners is notified by this class
 * every time it receives a {@link org.eclipse.jface.viewers.SelectionChangedEvent}.
 */
public class CompositeSelectionChangedListener implements ISelectionChangedListener {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Collection of <code>ISelectionChangedListener</code>s. */
    private List<ISelectionChangedListener> subListeners;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>CompositeSelectionChangedListener</code>.
     */
    public CompositeSelectionChangedListener() {
        subListeners = new ArrayList<ISelectionChangedListener>();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Adds an <code>ISelectionChangedListener</code> to the collection of listeners notified when a
     * {@link org.eclipse.jface.viewers.SelectionChangedEvent} is received.
     * @param theListener the listener being added
     */
    public void addSelectionChangedListener(ISelectionChangedListener theListener) {
        subListeners.add(theListener);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
     */
    public void selectionChanged(SelectionChangedEvent theEvent) {
        for (int size = subListeners.size(), i = 0; i < size; i++) {
            ISelectionChangedListener l = subListeners.get(i);
            l.selectionChanged(theEvent);
        }
    }

    /**
     * Removes an <code>ISelectionChangedListener</code> from the collection of listeners notified when a
     * {@link org.eclipse.jface.viewers.SelectionChangedEvent} is received.
     * @param theListener the listener being removed
     */
    public void removeSelectionChangedListener(ISelectionChangedListener theListener) {
        subListeners.add(theListener);
    }

}
