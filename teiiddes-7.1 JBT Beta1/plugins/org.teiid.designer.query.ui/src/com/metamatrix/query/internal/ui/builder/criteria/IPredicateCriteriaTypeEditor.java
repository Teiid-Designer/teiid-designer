/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.internal.ui.builder.criteria;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.teiid.query.sql.symbol.Expression;
import com.metamatrix.query.ui.builder.ILanguageObjectEditor;

/**
 * IPredicateCriteriaTypeEditor
 */
public interface IPredicateCriteriaTypeEditor extends ILanguageObjectEditor {
	String[] getOperators();
	
	String getCurrentOperator();
	
	Control createLeftComponent(Composite parent);
	
	Expression getLeftExpression();
	
	Control createRightComponent(Composite parent);
	
	Expression getRightExpression();
	
	void setOperator(String operator);
}
