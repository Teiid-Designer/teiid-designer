/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class ExpressionImpl extends LanguageObjectImpl implements IExpression {

    /**
     * @param expression
     */
    protected ExpressionImpl(Expression expression) {
        super(expression);
    }
    
    @Override
    public Expression getDelegate() {
        return (Expression) super.getDelegate();
    }

    @Override
    public IExpression clone() {
        return new ExpressionImpl((Expression) getDelegate().clone());
    }
    
    @Override
    public Class<?> getType() {
        return getDelegate().getType();
    }

}
