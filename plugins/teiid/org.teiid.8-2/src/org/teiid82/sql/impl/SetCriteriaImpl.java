/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ILanguageObject;
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
        Collection values = getDelegate().getValues();
        if (values == null)
            return Collections.emptyList();
        else
            return values;
    }

    @Override
    public void setValues(Collection<Object> values) {
        if (values == null || values.isEmpty()) {
            getDelegate().setValues(Collections.emptyList());
            return;
        }
        
        List<Object> unwrapValues = new ArrayList<Object>();
        for (Object v : values) {
            if (v instanceof ILanguageObject) {
                unwrapValues.add(getFactory().convert((ILanguageObject) v));
            } else {
                unwrapValues.add(v);
            }
        }
        
        getDelegate().setValues(unwrapValues);
    }

    @Override
    public void setNegated(boolean value) {
        getDelegate().setNegated(value);
    }
}