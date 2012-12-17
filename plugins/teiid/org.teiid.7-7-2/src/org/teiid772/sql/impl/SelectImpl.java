/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.ISelect;
import org.teiid.query.sql.lang.Select;
import org.teiid.query.sql.symbol.SelectSymbol;

/**
 *
 */
public class SelectImpl extends LanguageObjectImpl implements ISelect {

    /**
     * @param select
     */
    public SelectImpl(Select select) {
        super(select);
    }

    @Override
    public Select getDelegate() {
        return (Select) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public SelectImpl clone() {
        return new SelectImpl(getDelegate().clone());
    }

    @Override
    public List<IExpression> getSymbols() {
        return getFactory().wrap(getDelegate().getSymbols());
    }

    @Override
    public void setSymbols(List<? extends IExpression> symbols) {
        List<SelectSymbol> symbolImpls = getFactory().unwrap(symbols);
        getDelegate().setSymbols(symbolImpls);
    }

    @Override
    public void addSymbol(IExpression expression) {
        SelectSymbol selectSymbol = getFactory().convert(expression);
        getDelegate().addSymbol(selectSymbol);
    }

    @Override
    public boolean isStar() {
        return getDelegate().isStar();
    }

    @Override
    public boolean isDistinct() {
        return getDelegate().isDistinct();
    }

    @Override
    public void setDistinct(boolean isDistinct) {
        getDelegate().setDistinct(isDistinct);
    }
}