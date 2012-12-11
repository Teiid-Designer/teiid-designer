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
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IOrderBy;
import org.teiid.designer.query.sql.lang.IOrderByItem;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.query.sql.lang.OrderBy;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class OrderByImpl extends LanguageObjectImpl implements IOrderBy {

    /**
     * @param orderBy
     */
    public OrderByImpl(OrderBy orderBy) {
        super(orderBy);
    }

    @Override
    public OrderBy getDelegate() {
        return (OrderBy) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public OrderByImpl clone() {
        return new OrderByImpl(getDelegate().clone());
    }

    @Override
    public int getVariableCount() {
        return getDelegate().getVariableCount();
    }

    @Override
    public List<IOrderByItem> getOrderByItems() {
        return getFactory().wrap(getDelegate().getOrderByItems());
    }

    @Override
    public void addVariable(IExpression expression) {
        Expression expressionImpl = getFactory().convert(expression);
        getDelegate().addVariable(expressionImpl);
    }

    @Override
    public void addVariable(IElementSymbol element, boolean orderType) {
        ElementSymbol symbolImpl = getFactory().convert(element);
        getDelegate().addVariable(symbolImpl, orderType);
    }
}