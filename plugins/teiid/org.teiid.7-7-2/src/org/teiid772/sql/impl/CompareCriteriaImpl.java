/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.ICompareCriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class CompareCriteriaImpl extends PredicateCriteriaImpl implements ICompareCriteria {

    /**
     * @param compareCriteria
     */
    public CompareCriteriaImpl(CompareCriteria compareCriteria) {
        super(compareCriteria);
    }

    @Override
    public CompareCriteria getDelegate() {
        return (CompareCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public CompareCriteriaImpl clone() {
        return new CompareCriteriaImpl((CompareCriteria) getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
    }

    @Override
    public int getOperator() {
        return getDelegate().getOperator();
    }

    @Override
    public void setOperator(int operator) {
        getDelegate().setOperator(operator);
    }

    @Override
    public String getOperatorAsString() {
        return getDelegate().getOperatorAsString();
    }

    @Override
    public IExpression getLeftExpression() {
        return getFactory().convert(getDelegate().getLeftExpression());
    }

    @Override
    public void setLeftExpression(IExpression expression) {
        Expression expressionImpl = getFactory().convert(expression);
        getDelegate().setLeftExpression(expressionImpl);
    }

    @Override
    public IExpression getRightExpression() {
        return getFactory().convert(getDelegate().getRightExpression());
    }

    @Override
    public void setRightExpression(IExpression expression) {
        Expression expressionImpl = getFactory().convert(expression);
        getDelegate().setRightExpression(expressionImpl);
    }
}