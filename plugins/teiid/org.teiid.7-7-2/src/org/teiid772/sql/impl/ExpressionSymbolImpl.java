/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.symbol.IExpressionSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.ExpressionSymbol;

/**
 *
 */
public class ExpressionSymbolImpl extends SymbolImpl implements IExpressionSymbol {

    /**
     * @param expressionSymbol
     */
    public ExpressionSymbolImpl(ExpressionSymbol expressionSymbol) {
        super(expressionSymbol);
    }

    @Override
    public ExpressionSymbol getDelegate() {
        return (ExpressionSymbol) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ExpressionSymbolImpl clone() {
        return new ExpressionSymbolImpl((ExpressionSymbol) getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
    }

    @Override
    public IExpression getExpression() {
        return getFactory().convert(getDelegate().getExpression());
    }

    @Override
    public void setExpression(IExpression expression) {
        Expression expressionImpl = getFactory().convert(expression);
        getDelegate().setExpression(expressionImpl);
    }
}