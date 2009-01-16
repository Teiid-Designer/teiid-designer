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

package com.metamatrix.modeler.transformation.ui.editors;

import java.util.EventObject;

import com.metamatrix.modeler.transformation.ui.UiConstants;


/**
 * QueryEditorStatusEvent is fired each time a the status of the Query being edited changes
 */
public class QueryEditorStatusEvent extends EventObject 
		implements UiConstants {

    ///////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////

    /**
     */
    private static final long serialVersionUID = 1L;
    /**
     * Different Types of QueryEditorStatus Events
     * QUERY_PARSABLE - query is parsable
     * QUERY_RESOLVABLE - query is parsable and resolvable
     * QUERY_VALIDATABLE - query is parsable,resolvable, and validatable
     * QUERY_NOT_PARSABLE - query is not parsable
     * QUERY_HAS_PENDING_CHANGES - query has pending changes which have not been validated
     * QUERY_TAB_CHANGE - query panel tab selection was changed
     */
    public static final int QUERY_PARSABLE = 0;
    public static final int QUERY_RESOLVABLE = 1;
    public static final int QUERY_VALIDATABLE = 2;
    public static final int QUERY_NOT_PARSABLE = 3;
    public static final int QUERY_HAS_PENDING_CHANGES = 4;
    public static final int QUERY_TAB_CHANGE = 5;

    ///////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////

    private int eventType;

    ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////

    public QueryEditorStatusEvent(Object source, int eventType) {
        super(source);
        if ( eventType != QUERY_PARSABLE
          && eventType != QUERY_RESOLVABLE
          && eventType != QUERY_VALIDATABLE
          && eventType != QUERY_NOT_PARSABLE 
          && eventType != QUERY_HAS_PENDING_CHANGES
          && eventType != QUERY_TAB_CHANGE) {
            throw new AssertionError(invalidEventTypeString(eventType));
        }
        this.eventType = eventType;
    }

    ///////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////

    public int getType() {
        return this.eventType;
    }
    
    public boolean isParsable() {
        return (eventType == QUERY_PARSABLE || 
                eventType == QUERY_RESOLVABLE ||
                eventType == QUERY_VALIDATABLE);
    }
    
    public boolean isResolvable() {
        return (eventType == QUERY_RESOLVABLE ||
                eventType == QUERY_VALIDATABLE);
    }
    
    public boolean isValid() {
        return (eventType == QUERY_VALIDATABLE);
    }

    public boolean isNotParsable() {
        return eventType == QUERY_NOT_PARSABLE;
    }

    public boolean hasPendingChanges() {
        return eventType == QUERY_HAS_PENDING_CHANGES;
    }

    public boolean isTabChange() {
        return eventType == QUERY_TAB_CHANGE;
    }
    
    @Override
    public String toString() {
    	StringBuffer msg = new StringBuffer(getClass().getName());
    	msg.append(':');
    	String typeStr = Util.getString("QueryEditorStatusEvent.type"); //$NON-NLS-1$
    	msg.append(typeStr);
    	msg.append('=');
    	
    	switch (eventType) {
    		case QUERY_VALIDATABLE:
    			String valid = Util.getString("QueryEditorStatusEvent.valid"); //$NON-NLS-1$
    			msg.append(valid);
    			break;
    		case QUERY_PARSABLE:
    			String parsable = Util.getString("QueryEditorStatusEvent.parsable"); //$NON-NLS-1$
    			msg.append(parsable);
    			break;
    		case QUERY_NOT_PARSABLE:
    			String notParsable = Util.getString("QueryEditorStatusEvent.notParsable"); //$NON-NLS-1$
    			msg.append(notParsable);
    			break;
    		case QUERY_HAS_PENDING_CHANGES:
    			String pendingChanges = Util.getString("QueryEditorStatusEvent.pendingChanges"); //$NON-NLS-1$
    			msg.append(pendingChanges);
    			break;
    		case QUERY_RESOLVABLE:
    			String resolvable = Util.getString("QueryEditorStatusEvent.resolvable"); //$NON-NLS-1$
    			msg.append(resolvable);
    			break;
    		case QUERY_TAB_CHANGE:
    			String tabChange = Util.getString("QueryEditorStatusEvent.tabChange"); //$NON-NLS-1$
    			msg.append(tabChange);
    			break;
    		default:
    			String unknownType = Util.getString("QueryEditorStatusEvent.unknownType"); //$NON-NLS-1$
    			msg.append(unknownType);
    			break;
    	}
    	
    	return msg.toString();
    }
    
    private String invalidEventTypeString(int eventType) {
    	StringBuffer buf = new StringBuffer();
    	buf.append(Util.getString("QueryEditorStatusEvent.error")); //$NON-NLS-1$
    	buf.append(": "); //$NON-NLS-1$
    	buf.append(eventType);
    	buf.append(' ');
    	buf.append(Util.getString("QueryEditorStatusEvent.isNotValid")); //$NON-NLS-1$
    	String result = buf.toString();
    	return result;
    }
}
