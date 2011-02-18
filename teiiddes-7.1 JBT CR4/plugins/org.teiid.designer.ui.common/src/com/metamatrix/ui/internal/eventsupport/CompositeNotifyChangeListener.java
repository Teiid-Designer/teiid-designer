/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
