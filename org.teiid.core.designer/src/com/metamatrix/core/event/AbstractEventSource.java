/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.metamatrix.core.CorePlugin;
import com.metamatrix.core.util.CoreArgCheck;

abstract public class AbstractEventSource implements EventSource {

    private static final String LISTENER_MAY_NOT_BE_NULL = CorePlugin.Util.getString("AbstractEventSource.The_event_listener_may_not_be_null"); //$NON-NLS-1$
    private static final String EVENT_CLASS_MAY_NOT_BE_NULL = CorePlugin.Util.getString("AbstractEventSource.The_event_class_may_not_be_null"); //$NON-NLS-1$

    Map eventClassListeners = new HashMap(5);
    List eventListeners = new ArrayList(5);

    public synchronized void addListener( Class eventClass,
                                          EventObjectListener listener ) {
        CoreArgCheck.isNotNull(listener, LISTENER_MAY_NOT_BE_NULL);
        CoreArgCheck.isNotNull(eventClass, EVENT_CLASS_MAY_NOT_BE_NULL);

        if (!eventClassListeners.containsKey(eventClass)) {
            List listenerList = new ArrayList(1);
            listenerList.add(listener);
            eventClassListeners.put(eventClass, listenerList);
        } else {
            List listenerList = (List)eventClassListeners.get(eventClass);
            if (!listenerList.contains(listener)) {
                listenerList.add(listener);
            }
        }
    }

    public synchronized void addListener( EventObjectListener listener ) {
        CoreArgCheck.isNotNull(listener, LISTENER_MAY_NOT_BE_NULL);
        if (!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }

    public synchronized void removeListener( Class eventClass,
                                             EventObjectListener listener ) {
        CoreArgCheck.isNotNull(listener, LISTENER_MAY_NOT_BE_NULL);
        CoreArgCheck.isNotNull(eventClass, EVENT_CLASS_MAY_NOT_BE_NULL);

        if (eventClassListeners.containsKey(eventClass)) {
            List listenerList = (List)eventClassListeners.get(eventClass);
            listenerList.remove(listener);
        }
    }

    public synchronized void removeListener( EventObjectListener listener ) {
        CoreArgCheck.isNotNull(listener, LISTENER_MAY_NOT_BE_NULL);

        eventListeners.remove(listener);

        // Remove the listener from all of the event class listeners ...
        Iterator iter = eventClassListeners.values().iterator();
        while (iter.hasNext()) {
            List listenerList = (List)iter.next();
            listenerList.remove(listener);
        }
    }

    public synchronized void removeAllListeners() {
        eventClassListeners.clear();
        eventListeners.clear();
    }

    public synchronized List getListeners() {

        // Return those listeners that listen to all events ...
        return new ArrayList(eventListeners);
    }

    public synchronized List getAllListeners() {
        Set result = new HashSet();
        result.addAll(eventListeners);
        Iterator iter = eventClassListeners.values().iterator();
        while (iter.hasNext()) {
            List listenerList = (List)iter.next();
            result.addAll(listenerList);
        }
        return new ArrayList(result);
    }

    public synchronized List getListeners( Class eventClass ) {
        CoreArgCheck.isNotNull(eventClass, EVENT_CLASS_MAY_NOT_BE_NULL);

        // Always include those listeners that listen to all events ...
        List returnList = new ArrayList(eventListeners);

        // Add any those that listen to the specific event class ...
        List listeners = (List)eventClassListeners.get(eventClass);
        if (listeners != null) {

            // Add them one-by-one, so there are no duplicates ...
            Object listener = null;
            Iterator itr = listeners.iterator();
            while (itr.hasNext()) {
                listener = itr.next();
                if (!returnList.contains(listener)) {
                    returnList.add(listener);
                }
            }
        }

        return returnList;
    }
}
