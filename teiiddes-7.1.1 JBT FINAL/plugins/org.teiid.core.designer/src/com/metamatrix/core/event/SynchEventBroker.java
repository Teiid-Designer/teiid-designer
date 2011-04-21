/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.event;

import java.util.EventObject;
import org.teiid.core.CorePlugin;

public class SynchEventBroker extends AbstractEventBroker {

    private static final String DEFAULT_NAME = CorePlugin.Util.getString("SynchEventBroker.DefaultName"); //$NON-NLS-1$

    public SynchEventBroker() {
        this(null);
    }

    public SynchEventBroker( String name ) {
        super();
        if (name == null) {
            name = DEFAULT_NAME;
        }
        super.setName(name);
    }

    @Override
    protected void process( EventObject obj ) {
        super.notifyListeners(obj);
        if (super.isShutdownRequested()) {
            super.setShutdownComplete(true);
        }
    }

    @Override
    protected void waitToCompleteShutdown() {
        super.setShutdownComplete(true);
    }
}
