/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.transaction;

import java.util.EventListener;

/**
 * UndoableEventListener
 */
public interface UndoableListener extends EventListener {
    void process(Undoable event);
}
