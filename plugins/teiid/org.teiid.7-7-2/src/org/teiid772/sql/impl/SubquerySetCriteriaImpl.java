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
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.lang.ISubquerySetCriteria;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SubquerySetCriteria;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class SubquerySetCriteriaImpl extends PredicateCriteriaImpl implements ISubquerySetCriteria {

    /**
     * @param subquerySetCriteria
     */
    public SubquerySetCriteriaImpl(SubquerySetCriteria subquerySetCriteria) {
        super(subquerySetCriteria);
    }

    @Override
    public SubquerySetCriteria getDelegate() {
        return (SubquerySetCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public SubquerySetCriteriaImpl clone() {
        return new SubquerySetCriteriaImpl(getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
    }

    @Override
    public IQueryCommand getCommand() {
        return getFactory().convert(getDelegate().getCommand());
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
    public void setCommand(IQueryCommand command) {
        QueryCommand commandImpl = getFactory().convert(command);
        getDelegate().setCommand(commandImpl);
    }

    @Override
    public void setNegated(boolean negationFlag) {
        getDelegate().setNegated(negationFlag);
    }
}