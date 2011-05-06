/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.editors.sqleditor;

import java.util.EventObject;
import org.teiid.query.sql.lang.Command;

/**
 * The <code>SqlEditorEvent</code> class notifies interested
 * EventListeners that a change in the status of SqlEditorPanel
 * has occured.
 */
public class SqlEditorEvent extends EventObject {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////

    /**
     */
    private static final long serialVersionUID = 1L;
    /**
     * Different Types of Change Events
     * CHANGES_PENDING - panel has pending changes which have not been validated
     * CHANGED - changed but is not parsable or resolvable
     * PARSABLE - sql is parsable, but not resolvable or validatable
     * RESOLVABLE - sql is parsable and resolvable, but not validatable
     * VALIDATABLE - sql is parsable, resolvable, and validatable
     */
    public static final int CHANGES_PENDING = 0;
    public static final int CHANGED = 1;
    public static final int PARSABLE = 2;
    public static final int RESOLVABLE = 3;
    public static final int VALIDATABLE = 4;
    public static final int CARET_CHANGED = 5;

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    private Command command = null;
    private String SQLString = null;
    private int type;

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////

    public SqlEditorEvent(Command command, int type) {
        this(null,command,type);
    }
    
    public SqlEditorEvent(Object source, Command query, String SQLString, int type) {
        super(source);
        if ( type != PARSABLE
          && type != RESOLVABLE
          && type != VALIDATABLE) {
            throw new AssertionError(type + " is not a valid for this SqlChangeEvent type"); //$NON-NLS-1$ 
        }
        this.command = query;
        this.SQLString = SQLString;
        this.type = type;
    }

    public SqlEditorEvent(Object source, Command query, int type) {
        super(source);
        if ( type != PARSABLE
          && type != RESOLVABLE
          && type != VALIDATABLE) {
            throw new AssertionError(type + " is not a valid for this SqlChangeEvent type"); //$NON-NLS-1$ 
        }
        this.command = query;
        this.type = type;
    }

    public SqlEditorEvent(Object source, String SQLString, int type) {
        super(source);
        if ( type != CHANGED ) {
            throw new AssertionError(type + " is not a valid for this SqlChangeEvent type"); //$NON-NLS-1$ 
        }
        this.SQLString = SQLString;
        this.type = type;
    }

    public SqlEditorEvent(Object source, int type) {
        super(source);
        if ( type != CHANGES_PENDING && type != CARET_CHANGED) {
            throw new AssertionError(type + " is not a valid for this SqlChangeEvent type"); //$NON-NLS-1$ 
        }
        this.type = type;
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    public Command getCommand() {
        return this.command;
    }

    public String getSQLString() {
        return this.SQLString;
    }

    public int getType() {
        return this.type;
    }
}
