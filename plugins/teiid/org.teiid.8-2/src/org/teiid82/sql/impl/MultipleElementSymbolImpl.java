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
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IMultipleElementSymbol;
import org.teiid.query.sql.symbol.MultipleElementSymbol;

/**
 *
 */
public class MultipleElementSymbolImpl extends ExpressionImpl implements IMultipleElementSymbol {

    /**
     * @param multipleElementSymbol
     */
    public MultipleElementSymbolImpl(MultipleElementSymbol multipleElementSymbol) {
        super(multipleElementSymbol);
    }

    @Override
    public MultipleElementSymbol getDelegate() {
        return (MultipleElementSymbol) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public MultipleElementSymbolImpl clone() {
        return new MultipleElementSymbolImpl((MultipleElementSymbol) getDelegate().clone());
    }

    @Override
    public List<IElementSymbol> getElementSymbols() {
        return getFactory().wrap(getDelegate().getElementSymbols());
    }
}