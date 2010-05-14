/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.event;

import java.util.EventListener;
import java.util.EventObject;

public interface EventObjectListener extends EventListener {
    void processEvent( EventObject obj );
}
