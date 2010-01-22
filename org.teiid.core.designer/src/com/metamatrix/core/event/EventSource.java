/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.event;

import java.util.List;

public interface EventSource {

    void addListener( Class eventClass,
                      EventObjectListener listener ) throws EventSourceException;

    void addListener( EventObjectListener listener ) throws EventSourceException;

    void removeListener( Class eventClass,
                         EventObjectListener listener ) throws EventSourceException;

    void removeListener( EventObjectListener listener ) throws EventSourceException;

    void removeAllListeners() throws EventSourceException;

    /**
     * Obtain the listeners for the specified event class
     */
    List getListeners( Class eventClass );

    /**
     * Obtain those listeners that are registered for <i>all</i> event classes (i.e., those that use the
     * <code>addListener(EventObjectListener)</code> method).
     */
    List getListeners();

    /**
     * Obtain the complete list of listeners that are registered. The resulting list contains no duplicates.
     */
    List getAllListeners();

}
