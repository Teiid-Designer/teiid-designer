/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.model;

import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IElementSymbol;

/**
 * The <code>ElementEditorModel</code> class is used as a model for the
 * org.teiid.designer.transformation.ui.builder.expression.ElementEditor.
 *
 * @since 8.0
 */
public class ElementEditorModel extends AbstractLanguageObjectEditorModel {
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The currently selected <code>ElementSymbol</code>.
     */
    private IElementSymbol selectedElement;

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructs an <code>ElementEditorModel</code> with an incomplete state.
     */
    public ElementEditorModel() {
        super(IElementSymbol.class);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.AbstractLanguageObjectEditorModel#clear()
     */
    @Override
    public void clear() {
        selectedElement = null;
        super.clear();
    }

    /**
     * Gets the current value.
     * 
     * @return the currently selected <code>ElementSymbol</code>
     * @throws IllegalStateException if the current value is not complete
     */
    public IElementSymbol getElementSymbol() {
        return (IElementSymbol)getLanguageObject();
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.AbstractLanguageObjectEditorModel#getLanguageObject()
     */
    @Override
    public ILanguageObject getLanguageObject() {
        return selectedElement;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.AbstractLanguageObjectEditorModel#isComplete()
     */
    @Override
    public boolean isComplete() {
        return (selectedElement != null);
    }

    /**
     * Sets the selected or current value.
     * 
     * @param theElement the element becoming the current value
     * @return <code>true</code> if selected element has changed; <code>false</code> otherwise.
     */
    public boolean selectElementSymbol( IElementSymbol theElement ) {
        boolean changed = false;

        if (selectedElement == null) {
            changed = (theElement != null);
        } else {
            changed = (theElement == null) ? true : !selectedElement.equals(theElement);
        }

        if (changed) {
            selectedElement = theElement;
            fireModelChanged(LanguageObjectEditorModelEvent.STATE_CHANGE);
        }

        return changed;
    }

    /**
     * Sets the saved value.
     * 
     * @param theElement the element being saved
     */
    private void setElementSymbol( IElementSymbol theElement ) {
        if (theElement == null) {
            clear();
        } else {
            // turn firing of event off for the selectElementSymbol method since we want the
            // event type to be SAVED.
            notifyListeners = false;

            if (selectElementSymbol(theElement)) {
                notifyListeners = true;
                fireModelChanged(LanguageObjectEditorModelEvent.SAVED);
            }

            notifyListeners = true;
        }
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.AbstractLanguageObjectEditorModel#setLanguageObject(org.teiid.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( ILanguageObject theLangObj ) {
        super.setLanguageObject(theLangObj);
        setElementSymbol((IElementSymbol)theLangObj);
    }

}
