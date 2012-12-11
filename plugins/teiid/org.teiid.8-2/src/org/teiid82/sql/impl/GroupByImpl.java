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
import org.teiid.designer.query.sql.lang.IGroupBy;
import org.teiid.query.sql.lang.GroupBy;
import org.teiid.query.sql.symbol.Expression;

/**
 *
 */
public class GroupByImpl extends LanguageObjectImpl implements IGroupBy {

    /**
     * @param groupBy
     */
    public GroupByImpl(GroupBy groupBy) {
        super(groupBy);
    }

    @Override
    public GroupBy getDelegate() {
        return (GroupBy) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public GroupByImpl clone() {
        return new GroupByImpl((GroupBy) getDelegate().clone());
    }

    @Override
    public int getCount() {
        return getDelegate().getCount();
    }

    @Override
    public List<IExpression> getSymbols() {
        return getFactory().wrap(getDelegate().getSymbols());
    }

    @Override
    public void addSymbol(IExpression symbol) {
        Expression symbolImpl = getFactory().convert(symbol);
        getDelegate().addSymbol(symbolImpl);
    }
    
}