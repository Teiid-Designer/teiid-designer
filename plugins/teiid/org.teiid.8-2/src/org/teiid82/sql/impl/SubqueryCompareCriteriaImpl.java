/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.lang.ISubqueryCompareCriteria;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SubqueryCompareCriteria;

/**
 *
 */
public class SubqueryCompareCriteriaImpl extends PredicateCriteriaImpl implements ISubqueryCompareCriteria {

    /**
     * @param subqueryCompareCriteria
     */
    public SubqueryCompareCriteriaImpl(SubqueryCompareCriteria subqueryCompareCriteria) {
        super(subqueryCompareCriteria);
    }

    @Override
    public SubqueryCompareCriteria getDelegate() {
        return (SubqueryCompareCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public SubqueryCompareCriteriaImpl clone() {
        return new SubqueryCompareCriteriaImpl((SubqueryCompareCriteria) getDelegate().clone());
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
    public void setCommand(IQueryCommand command) {
        QueryCommand commandImpl = getFactory().convert(command);
        getDelegate().setCommand(commandImpl);
    }
}