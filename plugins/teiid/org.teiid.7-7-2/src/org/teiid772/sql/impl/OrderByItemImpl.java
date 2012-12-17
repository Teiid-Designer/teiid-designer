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
import org.teiid.designer.query.sql.lang.IOrderByItem;
import org.teiid.query.sql.lang.OrderByItem;
import org.teiid.query.sql.symbol.SingleElementSymbol;

/**
 *
 */
public class OrderByItemImpl extends LanguageObjectImpl implements IOrderByItem {

    /**
     * @param orderByItem
     */
    public OrderByItemImpl(OrderByItem orderByItem) {
        super(orderByItem);
    }

    @Override
    public OrderByItem getDelegate() {
        return (OrderByItem) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public OrderByItemImpl clone() {
        return new OrderByItemImpl(getDelegate().clone());
    }

    @Override
    public IExpression getSymbol() {
        return getFactory().convert(getDelegate().getSymbol());
    }

    @Override
    public void setSymbol(IExpression symbol) {
        SingleElementSymbol symbolImpl = getFactory().convert(symbol);
        getDelegate().setSymbol(symbolImpl);
    }

    @Override
    public boolean isAscending() {
        return getDelegate().isAscending();
    }
}