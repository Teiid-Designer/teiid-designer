/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui.builder.criteria;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.transformation.ui.builder.ILanguageObjectEditor;


/**
 * IPredicateCriteriaTypeEditor
 *
 * @since 8.0
 */
public interface IPredicateCriteriaTypeEditor extends ILanguageObjectEditor {
	String[] getOperators();
	
	String getCurrentOperator();
	
	Control createLeftComponent(Composite parent);
	
	IExpression getLeftExpression();
	
	Control createRightComponent(Composite parent);
	
	IExpression getRightExpression();
	
	void setOperator(String operator);
}
