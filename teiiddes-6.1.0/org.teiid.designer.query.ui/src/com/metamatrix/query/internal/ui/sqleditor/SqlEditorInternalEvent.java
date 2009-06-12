/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.sqleditor;

import java.util.EventObject;

/**
 * The <code>SqlEditorInternalEvent</code> class is the event that the
 * SqlEditor uses internally to notify interested EventListeners that a change 
 * has occurred in the SqlEditorPanel.
 */
public class SqlEditorInternalEvent extends EventObject {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////

    /**
     */
    private static final long serialVersionUID = 1L;
    /**
     * Different Types of Change Events
     * TEXT_RESET - panel text was reset
     * TEXT_CHANGED - existing sql text changed
     * READONLY_CHANGED - panel readonly status changed
     * MESSAGE_VISIBILITY_CHANGED - message visibility status changed
     * FONT_SIZE_CHANGED - font size changed
     * CARET_CHANGED - caret offset changed
     * OPTIMIZER_STATE_CHANGED - optimizer state changed
     */
    public static final int TEXT_RESET = 0;
    public static final int TEXT_CHANGED = 1;
    public static final int READONLY_CHANGED = 2;
    public static final int MESSAGE_VISIBILITY_CHANGED = 3;
    public static final int FONT_SIZE_CHANGED = 4;
    public static final int CARET_CHANGED = 5;
    public static final int OPTIMIZER_STATE_CHANGED = 6;

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    private int type;

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////

    public SqlEditorInternalEvent(Object source, int type) {
        super(source);
        if ( type != TEXT_RESET 
          && type != TEXT_CHANGED
          && type != READONLY_CHANGED
          && type != MESSAGE_VISIBILITY_CHANGED
          && type != FONT_SIZE_CHANGED
          && type != CARET_CHANGED
          && type != OPTIMIZER_STATE_CHANGED ) {
            throw new AssertionError(type + " is not a valid for this SqlEditorInternalEvent type"); //$NON-NLS-1$ 
        }
        this.type = type;
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    public int getType() {
        return this.type;
    }
}
