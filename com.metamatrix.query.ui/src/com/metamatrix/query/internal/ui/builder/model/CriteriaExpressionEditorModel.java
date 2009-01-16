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

import com.metamatrix.query.sql.symbol.Expression;

/**
 * CriteriaExpressionEditorModel
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
     * a new {@link ElementEditorModel}, and a new {@link theFunctionDisplayEditorModel}.
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
        super(Expression.class);
        
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
    
    public Expression getExpression() {
    	Expression exp = (Expression)getLanguageObject();
        return exp;
    }
    
}
