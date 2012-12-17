/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IBetweenCriteria;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.query.sql.lang.BetweenCriteria;

/**
 *
 */
public class BetweenCriteriaImpl extends PredicateCriteriaImpl implements IBetweenCriteria {

    /**
     * @param betweenCriteria
     */
    public BetweenCriteriaImpl(BetweenCriteria betweenCriteria) {
        super(betweenCriteria);
    }

    @Override
    public BetweenCriteria getDelegate() {
        return (BetweenCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public BetweenCriteriaImpl clone() {
        return new BetweenCriteriaImpl((BetweenCriteria) getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
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
    public IExpression getExpression() {
        return getFactory().convert(getDelegate().getExpression());
    }

    @Override
    public IExpression getLowerExpression() {
        return getFactory().convert(getDelegate().getLowerExpression());
    }

    @Override
    public IExpression getUpperExpression() {
        return getFactory().convert(getDelegate().getUpperExpression());
    }
}