/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.symbol.ISymbol;
import org.teiid.query.sql.symbol.Symbol;

/**
 *
 */
public abstract class SymbolImpl extends LanguageObjectImpl implements ISymbol {

    /**
     * @param symbol
     */
    public SymbolImpl(Symbol symbol) {
        super(symbol);
    }

    @Override
    public Symbol getDelegate() {
        return (Symbol) delegate;
    }
    
    @Override
    public String getName() {
        return getDelegate().getName();
    }

    @Override
    public String getShortName() {
        return getDelegate().getShortName();
    }

    @Override
    public void setShortName(String name) {
        getDelegate().setShortName(name);
    }

    @Override
    public String getOutputName() {
        return getDelegate().getOutputName();
    }

    @Override
    public void setOutputName(String outputName) {
        getDelegate().setOutputName(outputName);
    }
}
