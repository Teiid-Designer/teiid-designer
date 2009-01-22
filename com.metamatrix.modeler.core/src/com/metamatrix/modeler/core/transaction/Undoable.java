/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.transaction;

import java.io.Serializable;
import java.util.Collection;

import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * Undoable
 */
public interface Undoable extends Serializable {
    /**
     * Undo the edit that was made.
     */
    public void undo() throws ModelerCoreException;

    /**
     * True if it is still possible to undo this operation
     */
    public boolean canUndo();

    /**
     * Re-apply the edit, assuming that it has been undone.
     */
    public void redo() throws ModelerCoreException;

    /**
     * True if it is still possible to redo this operation
     */
    public boolean canRedo();

    /**
     * May be sent to inform an edit that it should no longer be
     * used. This is a useful hook for cleaning up state no longer
     * needed once undoing or redoing is impossible--for example,
     * deleting file resources used by objects that can no longer be
     * undeleted. UndoManager calls this before it dequeues edits.
     *
     * Note that this is a one-way operation. There is no "undie"
     * method.
     *
     * @see CompoundEdit#die
     */
    public void die();

    /**
     * Provide a localized, human readable description of this edit
     * suitable for use in, say, a change log.
     */
    public String getPresentationName();

    /**
     * Provide a localized, human readable description of the undoable
     * form of this edit, e.g. for use as an Undo menu item. Typically
     * derived from getDescription();
     */
    public String getUndoPresentationName();

    /**
     * Provide a localized, human readable description of the redoable
     * form of this edit, e.g. for use as a Redo menu item. Typically
     * derived from getPresentationName();
     */
    public String getRedoPresentationName();
    
    /**
     * @return the event ID
     */
    Object getId();

    /**
     * @return the collection of changed resources
     */
    Collection getResources();     

    /**
     * Return false if this edit is insignificant--for example one
     * that maintains the user's selection, but does not change any
     * model state. This status can be used by an UndoableListener
     * when deciding which UndoableEdits to present
     * to the user as Undo/Redo options, and which to perform as side
     * effects of undoing or redoing other events.
     */
    public boolean isSignificant();
    
    /**
     * Set the significant flag for this undoable
     * @param isSignificant
     */
    public void setSignificant(boolean isSignificant);
    
    /**
     * Setter for descrption which is used for presentation strings
     * @param string
     */
    public void setDescription(String string);
    
    /**
     *  
     * @return the source of the Undoable's transaction (may be NULL)
     * @since 4.3
     */
    public Object getSource();
}
