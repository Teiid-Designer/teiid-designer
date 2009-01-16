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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.INotifyChangedListener;

/**
 * The <code>CompositeNotifyChangeListener</code> class contains a collection of
 * <code>INotifyChangedListener</code>s. This collection of listeners is notified by this class
 * every time it receives a {@link org.eclipse.emf.common.notify.Notification}.
 */
public class CompositeNotifyChangeListener implements INotifyChangedListener {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** Collection of <code>INotifyChangedListener</code>s. */
    private List<INotifyChangedListener> subListeners;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>CompositeNotifyChangeListener</code>.
     */
    public CompositeNotifyChangeListener() {
        subListeners = new ArrayList<INotifyChangedListener>();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Adds an <code>INotifyChangedListener</code> to the collection of listeners notified when a
     * {@link org.eclipse.emf.common.notify.Notification} is received.
     * @param theListener the listener being added
     */
    public void addNotifyChangeListener(INotifyChangedListener theListener) {
        subListeners.add(theListener);
    }

    /* (non-Javadoc)
     * @see org.eclipse.emf.edit.provider.INotifyChangedListener#notifyChanged(org.eclipse.emf.common.notify.Notification)
     */
    public void notifyChanged(Notification theNotification) {
        for (int size = subListeners.size(), i = 0; i < size; i++) {
            INotifyChangedListener l = subListeners.get(i);
            l.notifyChanged(theNotification);
        }
    }

    /**
     * Removes an <code>INotifyChangedListener</code> from the collection of listeners notified when a
     * {@link org.eclipse.emf.common.notify.Notification} is received.
     * @param theListener the listener being removed
     */
    public void removeNotifyChangeListener(INotifyChangedListener theListener) {
        subListeners.add(theListener);
    }

}
