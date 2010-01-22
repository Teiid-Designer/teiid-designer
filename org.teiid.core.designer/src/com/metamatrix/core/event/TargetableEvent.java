/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.event;

import java.util.EventObject;
import com.metamatrix.core.id.ObjectID;

public class TargetableEvent extends EventObject {
    /**
     */
    private static final long serialVersionUID = 1L;

    private Object target;
    private ObjectID eventID;

    public TargetableEvent( Object source,
                            Object target ) {
        this(source, target, null);
    }

    public TargetableEvent( Object source,
                            Object target,
                            ObjectID eventID ) {
        super(source);
        this.target = target;
        this.eventID = eventID;
    }

    /**
     * Return the target of the event.
     * 
     * @return the target object, which may be null
     */
    public Object getTarget() {
        return this.target;
    }

    /**
     * Return the identifier associted with this event.
     * 
     * @return the identifier, which may be null
     */
    public ObjectID getEventID() {
        return this.eventID;
    }
}
