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
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.designer.query.sql.lang.IInto;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IQuery;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.query.sql.lang.CompareCriteria;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.lang.Into;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.lang.Query;
import org.teiid.query.sql.lang.Select;

/**
 *
 */
public class QueryImpl extends QueryCommandImpl implements IQuery {

    /**
     * @param query
     */
    public QueryImpl(Query query) {
        super(query);
    }

    @Override
    public Query getDelegate() {
        return (Query) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public QueryImpl clone() {
        return new QueryImpl((Query) getDelegate().clone());
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
    public ISelect getSelect() {
        return getFactory().convert(getDelegate().getSelect());
    }

    @Override
    public void setSelect(ISelect select) {
        Select selectImpl = getFactory().convert(select);
        getDelegate().setSelect(selectImpl);
    }

    @Override
    public IFrom getFrom() {
        return getFactory().convert(getDelegate().getFrom());
    }

    @Override
    public void setFrom(IFrom from) {
        From fromImpl = getFactory().convert(from);
        getDelegate().setFrom(fromImpl);
    }

    @Override
    public IInto getInto() {
        return getFactory().convert(getDelegate().getInto());
    }

    @Override
    public void setInto(IInto into) {
        Into intoImpl = getFactory().convert(into);
        getDelegate().setInto(intoImpl);
    }

    @Override
    public ICriteria getCriteria() {
        return getFactory().convert(getDelegate().getCriteria());
    }

    @Override
    public void setCriteria(ICriteria where) {
        Criteria whereImpl = getFactory().convert(where);
        getDelegate().setCriteria(whereImpl);
    }

    @Override
    public ICriteria getHaving() {
        return getFactory().convert(getDelegate().getHaving());
    }

    @Override
    public void setHaving(ICompareCriteria having) {
        CompareCriteria havingImpl = getFactory().convert(having);
        getDelegate().setHaving(havingImpl);
    }

    @Override
    public IGroupBy getGroupBy() {
        return getFactory().convert(getDelegate().getGroupBy());
    }

    @Override
    public void setGroupBy(IGroupBy groupBy) {
        GroupBy groupByImpl = getFactory().convert(groupBy);
        getDelegate().setGroupBy(groupByImpl);
    }
}