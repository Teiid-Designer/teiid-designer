/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.event;

import java.util.EventObject;
import com.metamatrix.core.CorePlugin;

public class SynchEventBroker extends AbstractEventBroker {

    private static final String DEFAULT_NAME = CorePlugin.Util.getString("SynchEventBroker.DefaultName"); //$NON-NLS-1$

    public SynchEventBroker() {
        this(null);
    }
    public SynchEventBroker( String name ) {
        super();
        if ( name == null ) {
            name = DEFAULT_NAME;
        }
        super.setName(name);
    }

    @Override
    protected void process(EventObject obj) {
        super.notifyListeners(obj);
        if (super.isShutdownRequested()) {
            super.setShutdownComplete(true);
        }
    }

    @Override
    protected void waitToCompleteShutdown() {
        super.setShutdownComplete(true);
    }

    /**
     * Return whether this broker has at least one event that has yet
     * to be processed and sent to the appropriate listeners.  This
     * implementation always returns false, since this broker always
     * notifies all listeners about an event before another one can be
     * processed.
     * @return false in all cases for this implementation
     */
    public boolean hasUnprocessedEvents() {
        return false;    
    }

}

