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

package com.metamatrix.query.internal.ui.builder.model;

import java.util.EventObject;


/**
 * LanguageObjectModelEvent
 */
public class LanguageObjectEditorModelEvent extends EventObject {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Type indicating the saved <code>LanguageObject</code> has changed.
     */
    public static final String SAVED = "SAVED"; //$NON-NLS-1$
    
    /**
     * Type indicating the current state of an editor has changed. This should not be used when the
     * saved state of the model changed.
     */
    public static final String STATE_CHANGE = "STATE_CHANGE"; //$NON-NLS-1$

     ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * The event type.
     */
    private String type = null;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a <code>LanguageObjectEditorModelEvent</code> with the given type. If the type parameter
     * is <code>null</code> the type is set to {@link #SAVED}.
     * @param theModel the model source of the event
     * @param theType the event type
     * @throws IllegalArgumentException if model is <code>null</code>
     */
    public LanguageObjectEditorModelEvent(ILanguageObjectEditorModel theModel,
                                          String theType) {
        super(theModel);
        type = (theType == null) ? SAVED : theType;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Gets the event type.
     * @return the event type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Indicates if the current model has been saved. This should not be <code>true</code> if the
     * current state of a model has just changed without being saved.
     * @return <code>true</code> if the current model has been saved; <code>false</code> otherwise.
     */
    public boolean isSaved() {
        return (type.equals(SAVED));
    }

    /**
     * Indicates if the current model state has changed. This should not be <code>true</code> if the
     * saved state of a model has just changed.
     * @return <code>true</code> if the current model state has changed; <code>false</code> otherwise.
     */
    public boolean isStateChange() {
        return (type.equals(STATE_CHANGE));
    }

}
