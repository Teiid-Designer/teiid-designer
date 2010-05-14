/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.refactor;

/**
 * RefactorUndoListener is an interface for events that occur in the RefactorUndoManager.
 */
public interface RefactorUndoListener {

    /**
     * notify listeners that the state of the RefactorUndoManager has changed.  Listeners
     * should respond by checking with the RefactorUndoManager to refresh their state. 
     * This method is called when the RefactorUndoManager performs an undo, or redo, or
     * clears, or receives a new command.
     */
    public void stateChanged();

}
