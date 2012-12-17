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
import org.teiid.designer.query.sql.symbol.IAliasSymbol;
import org.teiid.query.sql.symbol.AliasSymbol;
import org.teiid.query.sql.symbol.SingleElementSymbol;

/**
 *
 */
public class AliasSymbolImpl extends SymbolImpl implements IAliasSymbol {

    /**
     * @param aliasSymbol
     */
    public AliasSymbolImpl(AliasSymbol aliasSymbol) {
        super(aliasSymbol);
    }

    @Override
    public AliasSymbol getDelegate() {
        return (AliasSymbol) delegate;
    }
    
    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public AliasSymbolImpl clone() {
        return new AliasSymbolImpl((AliasSymbol) getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
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
}