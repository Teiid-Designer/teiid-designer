/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid8.sql.impl;

import java.util.Collection;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ISetCriteria;
import org.teiid.query.sql.lang.SetCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class SetCriteriaImpl extends PredicateCriteriaImpl implements ISetCriteria {

    /**
     * @param setCriteria
     */
    public SetCriteriaImpl(SetCriteria setCriteria) {
        super(setCriteria);
    }

    @Override
    public SetCriteria getDelegate() {
        return (SetCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public SetCriteriaImpl clone() {
        return new SetCriteriaImpl((SetCriteria) getDelegate().clone());
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
    public Collection<Object> getValues() {
        return getDelegate().getValues();
    }

    @Override
    public void setValues(Collection<Object> values) {
        getDelegate().setValues(values);
    }

    @Override
    public void setNegated(boolean value) {
        getDelegate().setNegated(value);
    }
}