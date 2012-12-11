/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.lang.ISetQuery;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.lang.SetQuery;

/**
 *
 */
public class SetQueryImpl extends QueryCommandImpl implements ISetQuery {

    /**
     * @param setquery
     */
    public SetQueryImpl(SetQuery setquery) {
        super(setquery);
    }

    @Override
    public SetQuery getDelegate() {
        return (SetQuery) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public SetQueryImpl clone() {
        return new SetQueryImpl((SetQuery) getDelegate().clone());
    }

    @Override
    public IOrderBy getOrderBy() {
        return getFactory().convert(getDelegate().getOrderBy());
    }

    @Override
    public void setOrderBy(IOrderBy orderBy) {
        OrderBy orderByImpl = getFactory().convert(orderBy);
        getDelegate().setOrderBy(orderByImpl);
    }

    @Override
    public IQuery getProjectedQuery() {
        return getFactory().convert(getDelegate().getProjectedQuery());
    }

    @Override
    public boolean isAll() {
        return getDelegate().isAll();
    }

    @Override
    public void setAll(boolean value) {
        getDelegate().setAll(value);
    }

    @Override
    public Operation getOperation() {
        String enumName = getDelegate().getOperation().name();
        return Operation.valueOf(enumName);
    }

    @Override
    public IQueryCommand getLeftQuery() {
        return getFactory().convert(getDelegate().getLeftQuery());
    }

    @Override
    public void setLeftQuery(IQueryCommand query) {
        QueryCommand queryImpl = getFactory().convert(query);
        getDelegate().setLeftQuery(queryImpl);
    }

    @Override
    public IQueryCommand getRightQuery() {
        return getFactory().convert(getDelegate().getRightQuery());
    }

    @Override
    public void setRightQuery(IQueryCommand query) {
        QueryCommand queryImpl = getFactory().convert(query);
        getDelegate().setRightQuery(queryImpl);
    }

    @Override
    public List<IQueryCommand> getQueryCommands() {
        return getFactory().wrap(getDelegate().getQueryCommands());
    }
}