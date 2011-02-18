/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import org.teiid.query.sql.LanguageObject;
import org.teiid.query.sql.symbol.Function;

/**
 * The <code>FunctionDisplayEditorModel</code> class is used as a model for the
 * {@link com.metamatrix.query.internal.ui.builder.expression.FunctionDisplayEditor}.
 */
public class FunctionDisplayEditorModel extends AbstractLanguageObjectEditorModel {

    /**
     * The currently selected <code>Function</code>.
     */
    private Function selectedFunction;

    /**
     * Constructs an <code>FunctionDisplayEditorModel</code> with an incomplete state.
     */
    public FunctionDisplayEditorModel() {
        super(Function.class);
    }

    /**
     * Gets the current value.
     * 
     * @return the currently selected <code>Function</code>
     * @throws IllegalStateException if the current value is not complete
     */
    public Function getFunction() {
        Function function = (Function)getLanguageObject();
        return function;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#getLanguageObject()
     */
    @Override
    public LanguageObject getLanguageObject() {
        return selectedFunction;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#isComplete()
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
    public void selectFunction( Function theFunction ) {
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
    private void setFunction( Function theFunction ) {
        notifyListeners = false;
        if (theFunction == null) {
            selectedFunction = null;
        } else {
            selectFunction(theFunction);
        }
        notifyListeners = true;

        fireModelChanged(LanguageObjectEditorModelEvent.SAVED);
    }

    private boolean sameAsSelectedFunction( Function newFunction ) {
        boolean same;
        if (selectedFunction == null) {
            same = (newFunction == null);
        } else {
            same = selectedFunction.equals(newFunction);
        }
        return same;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.query.internal.ui.builder.model.AbstractLanguageObjectEditorModel#setLanguageObject(com.metamatrix.query.sql.LanguageObject)
     */
    @Override
    public void setLanguageObject( LanguageObject theLangObj ) {
        if (!sameAsSelectedFunction((Function)theLangObj)) {
            super.setLanguageObject(theLangObj);
            setFunction((Function)theLangObj);
        }
    }

    @Override
    public void clear() {
        selectedFunction = null;
        super.clear();
    }
}
