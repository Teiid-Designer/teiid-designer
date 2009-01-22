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
 * AbstractPredicateCriteriaTypeEditorModel
 */
public abstract class AbstractPredicateCriteriaTypeEditorModel extends AbstractLanguageObjectEditorModel {
	public AbstractPredicateCriteriaTypeEditorModel(Class theType) {
		super(theType);
	}
	
	public abstract String[] getOperators();
	
	public abstract String getCurrentOperator();
	
	public abstract void setCurrentOperator(String op);
    
    public abstract Expression getLeftExpression();
    
    public abstract void setLeftExpression(Expression exp);

	public abstract Expression getRightExpression();
	
	public abstract void setRightExpression(Expression exp);
}
