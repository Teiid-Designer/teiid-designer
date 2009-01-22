/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.undo;

import java.util.EventObject;

/**
 * ModelerUndoListener is an interface for notification that some sort of change
 * occurred in the ModelerUndoManager.  Events are fired to all listeners whenever
 * the manager's undo or redo stacks are modified.
 */
public interface ModelerUndoListener {

    public void processEvent(EventObject event);

}
