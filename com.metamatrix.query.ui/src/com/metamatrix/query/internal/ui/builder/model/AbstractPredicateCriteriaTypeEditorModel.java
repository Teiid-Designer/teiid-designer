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
