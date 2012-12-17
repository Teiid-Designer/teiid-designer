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
import org.teiid.designer.query.sql.lang.IIsNullCriteria;
import org.teiid.query.sql.lang.IsNullCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class IsNullCriteriaImpl extends PredicateCriteriaImpl implements IIsNullCriteria {

    /**
     * @param isNullCriteria
     */
    public IsNullCriteriaImpl(IsNullCriteria isNullCriteria) {
        super(isNullCriteria);
    }

    @Override
    public IsNullCriteria getDelegate() {
        return (IsNullCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public IsNullCriteriaImpl clone() {
        return new IsNullCriteriaImpl((IsNullCriteria) getDelegate().clone());
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

    @Override
    public boolean isNegated() {
        return getDelegate().isNegated();
    }

    @Override
    public void setNegated(boolean value) {
        getDelegate().setNegated(value);
    }
}