/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.query.ui.sqleditor;

import java.util.EventObject;
import com.metamatrix.query.sql.lang.Command;

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
