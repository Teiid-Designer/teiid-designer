/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.ui.builder;

import org.eclipse.swt.widgets.Control;
import com.metamatrix.query.internal.ui.builder.model.ILanguageObjectEditorModel;
import org.teiid.query.sql.LanguageObject;

/**
 * The <code>ILanguageObjectEditor</code> interface represents an editor capable of editing a
 * {@link com.metamatrix.query.sql.LanguageObject}. Editor's may have a saved value and may
 * have a current value.
 */
public interface ILanguageObjectEditor {

    /**
     * Places focus on the appropriate control in the editor.
     */
    void acceptFocus();
    
    /**
     * Clears the editor causing it to be placed in it's initial state with no saved value.
     */
    void clear();
    
    /**
     * Gets the type of <code>LanguageObject</code> being edited.
     * @return the <code>Class</code> being edited
     */
    Class getEditorType();
    
    /**
     * Gets the editor's currently displayed value. May not be equal to it's last saved value.
     * @return the language object
     * @throws IllegalStateException if the current value is not complete
     */
    LanguageObject getLanguageObject();

    /**
     * Gets the editor's model.
     * @return the requested model
     */
    ILanguageObjectEditorModel getModel();
    
    /**
     * Gets the title of the editor.
     * @return the title
     */
    String getTitle();
    
    /**
     * Gets the tool tip of the editor.
     * @return the tool tip
     */
    String getToolTipText();
    
    /**
     * Gets the user interface control of the editor
     * @return the UI control
     */
    Control getUi();
    
    /**
     * Indicates if the editor has changed since the last save.
     * @return <code>true</code> if the editor has changed; <code>false</code> otherwise.
     */
    boolean hasChanged();
    
    /**
     * Indicates if the editor has a complete state.
     * @return <code>true</code> if the editor is complete; <code>false</code> otherwise.
     */
    boolean isComplete();
    
    /**
     * Indicates if the editor is enabled.
     * @return <code>true</code> if enabled; <code>false</code> when disabled.
     */
    boolean isEnabled();

    /**
     * Causes the editor to change it's displayed value to the last saved value.
     */
    void reset();
    
    /**
     * Saves the current <code>LanguageObject</code> value.
     * @throws IllegalStateException if current value is not complete
     */
    void save();
    
    /**
     * Gets the type of <code>LanguageObject</code> being edited.
     * @return the <code>Class</code> being edited
     */
    void setEditorType(Class theEditorType);
    
    /**
     * Sets the enabled state of the editor.
     * @param theEnableFlag the enabled state the editor should be set to
     */
    void setEnabled(boolean theEnableFlag);

    /**
     * Sets the editor's display value and saved value.
     * @param theLanguageObject the value being set in the editor
     * @throws IllegalArgumentException if the object is not of the proper type
     */
    void setLanguageObject(LanguageObject theLanguageObject);
    
    /**
     * Sets the editor's model.
     * @param the model
     * @throws IllegalArgumentException if the model is null or of the wrong type
     */
    void setModel(ILanguageObjectEditorModel theModel);
    
}
