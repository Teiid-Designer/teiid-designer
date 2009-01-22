/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.model;

import com.metamatrix.query.sql.symbol.Expression;

/**
 * The <code>ExpressionEditorModel</code> is a composite model that contains the following models:
 * <p>
 * <ul>
 * <li> {@link ConstantEditorModel},
 * <li> {@link ElementEditorModel}, and
 * <li> {@link FunctionEditorModel}.
 * </ul>
 */
public class ExpressionEditorModel extends CompositeLanguageObjectEditorModel {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private ConstantEditorModel constantModel;
    
    private ElementEditorModel elementModel;
    
    private FunctionEditorModel functionModel;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs an <code>ExpressionEditorModel</code> by constructing a new {@link ConstantEditorModel},
     * a new {@link ElementEditorModel}, and a new {@link theFunctionEditorModel}.
     */
    public ExpressionEditorModel() {
        this(new ConstantEditorModel(), new ElementEditorModel(), new FunctionEditorModel());
    }
    
    /**
     * Constructs an <code>ExpressionEditorModel</code> by using the given editor models.
     * @param theConstantEditorModel the ConstantEditor model
     * @param theElementEditorModel the ElementEditor model
     * @param theFunctionEditorModel the FunctionEditor model
     */
    public ExpressionEditorModel(ConstantEditorModel theConstantEditorModel,
                                 ElementEditorModel theElementEditorModel,
                                 FunctionEditorModel theFunctionEditorModel) {
        super(Expression.class);
        
        constantModel = theConstantEditorModel;
        addModel(constantModel);
        
        elementModel = theElementEditorModel;
        addModel(elementModel);
        
        functionModel = theFunctionEditorModel;
        addModel(functionModel);        
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public ConstantEditorModel getConstantEditorModel() {
        return constantModel;
    }
    
    public ElementEditorModel getElementEditorModel() {
        return elementModel;
    }
    
    public FunctionEditorModel getFunctionEditorModel() {
        return functionModel;
    }
    
	public Expression getExpression() {
		return (Expression)getLanguageObject();
	}
}
