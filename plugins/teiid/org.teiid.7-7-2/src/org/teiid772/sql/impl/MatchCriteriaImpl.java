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
import org.teiid.designer.query.sql.lang.IMatchCriteria;
import org.teiid.query.sql.lang.MatchCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class MatchCriteriaImpl extends PredicateCriteriaImpl implements IMatchCriteria {

    /**
     * @param matchCriteria
     */
    public MatchCriteriaImpl(MatchCriteria matchCriteria) {
        super(matchCriteria);
    }

    @Override
    public MatchCriteria getDelegate() {
        return (MatchCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public MatchCriteriaImpl clone() {
        return new MatchCriteriaImpl((MatchCriteria) getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
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

    @Override
    public char getEscapeChar() {
        return getDelegate().getEscapeChar();
    }

    @Override
    public void setEscapeChar(char escapeChar) {
        getDelegate().setEscapeChar(escapeChar);
    }

    @Override
    public boolean isNegated() {
        return getDelegate().isNegated();
    }

    @Override
    public void setNegated(boolean value) {
        getDelegate().setNegated(value);
    }

    @Override
    public Mode getMode() {
        String enumName = getDelegate().getMode().name();
        return Mode.valueOf(enumName);
    }
}