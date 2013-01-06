/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.teiid.model.vdb;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.komodo.common.util.CollectionUtil;
import org.komodo.common.util.HashCode;
import org.komodo.common.util.Precondition;
import org.komodo.common.util.StringUtil;
import org.komodo.teiid.model.ModelObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The base class for Teiid model objects.
 */
public abstract class VdbObject implements Comparable<VdbObject>, ModelObject {

    /**
     * Names of the default properties for a VDB object.
     */
    public interface PropertyName {

        /**
         * The Teiid object identifier.
         */
        String ID = VdbObject.class.getSimpleName() + ".id"; //$NON-NLS-1$
    }

    protected String id;

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
     * {@inheritDoc}
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(final VdbObject that) {
        Precondition.notNull(that, "that"); //$NON-NLS-1$

        if (this == that) {
            return 0;
        }

        final String thisClass = getClass().getSimpleName();
        final String thatClass = that.getClass().getSimpleName();
        final int result = thisClass.compareTo(thatClass);

        if (result < 0) {
            return -10;
        }

        if (result > 0) {
            return 10;
        }

        if (StringUtil.isEmpty(this.id)) {
            if (StringUtil.isEmpty(that.id)) {
                return 0;
            }

            return -1;
        }

        if (StringUtil.isEmpty(that.id)) {
            return 1;
        }

        return this.id.compareTo(that.id);
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
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object that) {
        if (this == that) {
            return true;
        }

        if ((that == null) || !getClass().equals(that.getClass())) {
            return false;
        }

        // check ID
        return (StringUtil.matches(this.id, ((VdbObject)that).id));
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
                    this.logger.error("Exception in property change listener. Listener is now unregistered", e);
                }
            }
        }
    }

    /**
     * @return the ID (can be <code>null</code> or empty)
     */
    public String getId() {
        return this.id;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCode.compute(this.id);
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

    /**
     * Generates a property change event if the ID is changed.
     * 
     * @param newId the new ID (can be <code>null</code> or empty)
     */
    public void setId(final String newId) {
        if (!StringUtil.matches(this.id, newId)) {
            final String oldValue = this.id;
            this.id = newId;
            firePropertyChangeEvent(PropertyName.ID, oldValue, newId);

            assert StringUtil.matches(this.id, newId);
            assert !StringUtil.matches(this.id, oldValue);
        }
    }

}
