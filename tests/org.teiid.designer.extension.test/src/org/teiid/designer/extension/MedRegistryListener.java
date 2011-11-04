/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension;

import java.util.ArrayList;
import java.util.List;

import org.teiid.designer.extension.registry.RegistryEvent;
import org.teiid.designer.extension.registry.RegistryListener;

/**
 * 
 */
public final class MedRegistryListener implements RegistryListener {

    private List<RegistryEvent> events = new ArrayList<RegistryEvent>();

    /**
     * Clears the stored events.
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
    public RegistryEvent getEvent() {
        if (this.events.isEmpty()) {
            return null;
        }

        return this.events.get(0);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.extension.registry.RegistryListener#process(org.teiid.designer.extension.registry.RegistryEvent)
     */
    @Override
    public void process( RegistryEvent e ) {
        this.events.add(e);
    }

}
