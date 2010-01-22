/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.event;

public interface EventBroker extends EventSource, EventObjectListener {
    boolean shutdown() throws EventBrokerException;

    boolean isShutdown();

    /**
     * Return whether this broker has at least one event that has yet to be processed and sent to the appropriate listeners.
     * 
     * @return true if there are events that have yet to be processed, or false otherwise.
     */
    boolean hasUnprocessedEvents();
}
