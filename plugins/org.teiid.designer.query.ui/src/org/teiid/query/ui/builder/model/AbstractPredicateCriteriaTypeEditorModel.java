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
 * AbstractPredicateCriteriaTypeEditorModel
 *
 * @since 8.0
 */
public abstract class AbstractPredicateCriteriaTypeEditorModel extends AbstractLanguageObjectEditorModel {
	public AbstractPredicateCriteriaTypeEditorModel(Class theType) {
		super(theType);
	}
	
	public abstract String[] getOperators();
	
	public abstract String getCurrentOperator();
	
	public abstract void setCurrentOperator(String op);
    
    public abstract IExpression getLeftExpression();
    
    public abstract void setLeftExpression(IExpression exp);

	public abstract IExpression getRightExpression();
	
	public abstract void setRightExpression(IExpression exp);
}
