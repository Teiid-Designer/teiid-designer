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

import com.metamatrix.query.sql.LanguageObject;

/**
 * The <code>ILanguageObjectEditorModel</code> interface represents a model of an
 * {@link com.metamatrix.query.ui.builder.ILanguageObjectEditor}'s state. It holds a saved value and a current
 * value.
 */
public interface ILanguageObjectEditorModel {
    
    /**
     * Adds the given listener to the listeners receiving model change notifications. If the listener is
     * already registered, they are not added again.
     * @param theListener the listener being added
     * @return <code>true</code> if the listener was added; <code>false</code> otherwise.
     * @throws IllegalArgumentException if listener is <code>null</code>
     */
    boolean addModelListener(ILanguageObjectEditorModelListener theListener);

    /**
     * Clears the model causing it to be placed in it's initial state with no saved <code>LanguageObject</code>.
     */
    void clear();
    
    /**
     * Gets the model's current <code>LanguageObject</code>. May not be equal to it's last saved value.
     * @return the language object or <code>null</code> if not complete and valid
     */
    LanguageObject getLanguageObject();
    
    /**
     * Gets the <code>LanguageObject</code> class associated with this model.
     * @return the type
     */
    Class getModelType();
    
    /**
     * Indicates if the current <code>LanguageObject</code> is different from the saved value.
     * @return <code>true</code> if different; <code>false</code> otherwise.
     */
    boolean hasChanged();
    
    /**
     * Indicates if the current <code>LanguageObject</code> is complete.
     * @return <code>true</code> if complete; <code>false</code> when incomplete.
     */
    boolean isComplete();
    
    /**
     * Removes the given listener from the listeners receiving model change notifications.
     * @param theListener the listener being removed
     * @return <code>true</code> if the listener was removed; <code>false</code> otherwise.
     * @throws IllegalArgumentException if listener is <code>null</code>
     */
    boolean removeModelListener(ILanguageObjectEditorModelListener theListener);
    
    /**
     * Sets the model's current value equal to it's last saved <code>LanguageObject</code> or null.
     */
    void reset();
    
    /**
     * Saves the current <code>LanguageObject</code>.
     * @throws IllegalStateException if current value is not complete
     */
    void save();
    
    /**
     * Sets the model's saved <code>LanguageObject</code> and it's current <code>LanguageObject</code>.
     * @param theLanguageObject the value being used
     * @throws IllegalArgumentException if the object is not of the proper type
     */
    void setLanguageObject(LanguageObject theLangObj);
    
    /**
     * Gets the <code>LanguageObject</code> class associated with this model.
     * @param the theLanguageObjectClass the type
     */
    void setModelType(Class theLanguageObjectClass);
    
}
