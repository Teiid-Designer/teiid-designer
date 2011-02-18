/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.event;

/**
 * The <code>IChangeNotifier</code> interface provides a means for registering for change notification.
 */
public interface IChangeNotifier {

    /**
     * Adds the given listener to this notifier. Has no effect if an identical listener is already registered.
     * @param theListener the listener being registered
     */
    void addChangeListener(IChangeListener theListener);
    
    /**
     * Removes the given listener from this notifier. Has no effect if the listener is not registered.
     * @param theListener the listener being unregistered
     */
    void removeChangeListener(IChangeListener theListener);

}
