/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.query.ui.builder.model;

import org.teiid.designer.query.sql.lang.IExpression;

/**
 * CriteriaExpressionEditorModel
 *
 * @since 8.0
 */
public class CriteriaExpressionEditorModel extends CompositeLanguageObjectEditorModel {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private ConstantEditorModel constantModel;
    
    private ElementEditorModel elementModel;
    
    private FunctionDisplayEditorModel functionModel;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs an <code>CriteriaExpressionEditorModel</code> by constructing a new {@link ConstantEditorModel},
     * a new {@link ElementEditorModel}, and a new {@link FunctionDisplayEditorModel}.
     */
    public CriteriaExpressionEditorModel() {
        this(new ConstantEditorModel(), new ElementEditorModel(), new FunctionDisplayEditorModel());
    }
    
    /**
     * Constructs an <code>CriteriaExpressionEditorModel</code> by using the given editor models.
     * @param theConstantEditorModel the ConstantEditor model
     * @param theElementEditorModel the ElementEditor model
     * @param theFunctionEditorModel the FunctionDisplayEditor model
     */
    public CriteriaExpressionEditorModel(ConstantEditorModel theConstantEditorModel,
                                         ElementEditorModel theElementEditorModel,
                                         FunctionDisplayEditorModel theFunctionEditorModel) {
        super(IExpression.class);
        
        elementModel = theElementEditorModel;
        addModel(elementModel);
        
        constantModel = theConstantEditorModel;
        addModel(constantModel);
        
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
    
    public FunctionDisplayEditorModel getFunctionEditorModel() {
        return functionModel;
    }
    
    public IExpression getExpression() {
    	IExpression exp = (IExpression)getLanguageObject();
        return exp;
    }
    
}
