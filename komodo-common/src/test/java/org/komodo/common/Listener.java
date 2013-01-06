package org.komodo.common;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/

/**
 * A property change listener for testing.
 */
public class Listener implements PropertyChangeListener {

    private List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();

    /**
     * Empties the received events collection.
     */
    public void clear() {
        this.events.clear();
    }

    /**
     * @return the number of events received
     */
    public int getCount() {
        return this.events.size();
    }

    /**
     * @return the first event received or <code>null</code>
     */
    public PropertyChangeEvent getEvent() {
        if (this.events.isEmpty()) {
            return null;
        }

        return this.events.get(0);
    }

    /**
     * @return the new value of the last received event or <code>null</code> if no events
     */
    public Object getNewValue() {
        if (this.events.isEmpty()) {
            return null;
        }

        return getEvent().getNewValue();
    }

    /**
     * @return the old value of the last received event or <code>null</code> if no events
     */
    public Object getOldValue() {
        if (this.events.isEmpty()) {
            return null;
        }

        return getEvent().getOldValue();
    }

    /**
     * @return the property name of the last received event or <code>null</code> if no events
     */
    public String getPropertyName() {
        if (this.events.isEmpty()) {
            return null;
        }

        return getEvent().getPropertyName();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    @Override
    public void propertyChange( PropertyChangeEvent e ) {
        this.events.add(e);
    }

}
