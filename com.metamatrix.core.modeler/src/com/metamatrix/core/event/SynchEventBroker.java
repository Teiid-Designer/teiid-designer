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

