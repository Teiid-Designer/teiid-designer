/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.model;

import org.teiid.designer.query.sql.lang.ILanguageObject;
import org.teiid.designer.query.sql.symbol.IFunction;

/**
 * The <code>FunctionDisplayEditorModel</code> class is used as a model for the
 *  org.teiid.designer.transformation.ui.builder.expression.FunctionDisplayEditor.
 *
 * @since 8.0
 */
public class FunctionDisplayEditorModel extends AbstractLanguageObjectEditorModel {

    /**
     * The currently selected <code>Function</code>.
     */
    private IFunction selectedFunction;

    /**
     * Constructs an <code>FunctionDisplayEditorModel</code> with an incomplete state.
     */
    public FunctionDisplayEditorModel() {
        super(IFunction.class);
    }

    /**
     * Gets the current value.
     * 
     * @return the currently selected <code>Function</code>
     * @throws IllegalStateException if the current value is not complete
     */
    public IFunction getFunction() {
        IFunction function = (IFunction)getLanguageObject();
        return function;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.AbstractLanguageObjectEditorModel#getLanguageObject()
     */
    @Override
    public ILanguageObject getLanguageObject() {
        return selectedFunction;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.AbstractLanguageObjectEditorModel#isComplete()
     */
    @Override
    public boolean isComplete() {
        return (selectedFunction != null);
    }

    /**
     * Sets the selected or current value.
     * 
     * @param theFunction the function becoming the current value
     */
    public void selectFunction( IFunction theFunction ) {
        boolean changed = (!sameAsSelectedFunction(theFunction));
        if (changed) {
            selectedFunction = theFunction;
            fireModelChanged(LanguageObjectEditorModelEvent.STATE_CHANGE);
        }
    }

    /**
     * Sets the saved value.
     * 
     * @param theFunction the function being saved
     */
    private void setFunction( IFunction theFunction ) {
        notifyListeners = false;
        if (theFunction == null) {
            selectedFunction = null;
        } else {
            selectFunction(theFunction);
        }
        notifyListeners = true;

        fireModelChanged(LanguageObjectEditorModelEvent.SAVED);
    }

    private boolean sameAsSelectedFunction( IFunction newFunction ) {
        boolean same;
        if (selectedFunction == null) {
            same = (newFunction == null);
        } else {
            same = selectedFunction.equals(newFunction);
        }
        return same;
    }

    /* (non-Javadoc)
     * @see org.teiid.query.ui.builder.model.AbstractLanguageObjectEditorModel#setLanguageObject(org.teiid.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( ILanguageObject theLangObj ) {
        if (!sameAsSelectedFunction((IFunction)theLangObj)) {
            super.setLanguageObject(theLangObj);
            setFunction((IFunction)theLangObj);
        }
    }

    @Override
    public void clear() {
        selectedFunction = null;
        super.clear();
    }
}
