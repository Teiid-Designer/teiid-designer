/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.komodo.common.i18n.I18n;
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.teiid.TeiidI18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base class for Teiid model object.
 */
public abstract class ModelObject {

    protected List<PropertyChangeListener> listeners;

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Registers a listener to receive property change events. If already registered, it does nothing.
     * @param newListener the listener being added (cannot be <code>null</code>)
     * @return <code>true</code> if the listener was added
     */
    public boolean addListener(final PropertyChangeListener newListener) {
        Precondition.notNull(newListener, "newListener"); //$NON-NLS-1$

        if (this.listeners == null) {
            this.listeners = new ArrayList<PropertyChangeListener>();
        }

        if (!this.listeners.contains(newListener)) {
            return this.listeners.add(newListener);
        }

        return false;
    }

    /**
     * @param name the name of the property that has changed (cannot be <code>null</code> or empty)
     * @param oldValue the previous property value (can be <code>null</code>)
     * @param newValue the new property value (can be <code>null</code>)
     * @return the new property change event (never <code>null</code>)
     */
    protected PropertyChangeEvent createPropertyChangeEvent(final String name,
                                                            final Object oldValue,
                                                            final Object newValue) {
        assert !StringUtil.isEmpty(name) : "property name is empty"; //$NON-NLS-1$
        return new PropertyChangeEvent(this, name, oldValue, newValue);
    }

    /**
     * Broadcasts a property change event to registered listeners.
     * 
     * @param name the name of the property that has changed (cannot be <code>null</code> or empty)
     * @param oldValue the previous property value (can be <code>null</code>)
     * @param newValue the new property value (can be <code>null</code>)
     */
    protected void firePropertyChangeEvent(final String name,
                                           final Object oldValue,
                                           final Object newValue) {
        Precondition.notEmpty(name, "name"); //$NON-NLS-1$

        if (!CollectionUtil.isEmpty(this.listeners)) {
            final PropertyChangeEvent event = createPropertyChangeEvent(name, oldValue, newValue);

            for (final PropertyChangeListener l : this.listeners) {
                try {
                    l.propertyChange(event);
                } catch (final Exception e) {
                    this.logger.error(I18n.bind(TeiidI18n.propertyChangeListenerProblem, l.getClass().getName()), e);
                }
            }
        }
    }

    /**
     * Unregisters a listener from receiving property change events.
     * 
     * @param registeredListener the listener being removed (cannot be <code>null</code>)
     * @return <code>true</code> if the listener was removed
     */
    public boolean removeListener(final PropertyChangeListener registeredListener) {
        Precondition.notNull(registeredListener, "registeredListener"); //$NON-NLS-1$

        if (this.listeners == null) {
            return false;
        }

        return this.listeners.remove(registeredListener);
    }
}
