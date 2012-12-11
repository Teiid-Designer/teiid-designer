/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.symbol.IAggregateSymbol;
import org.teiid.query.sql.symbol.AggregateSymbol;
import org.teiid.query.sql.symbol.AggregateSymbol.Type;

/**
 *
 */
public class AggregateSymbolImpl extends FunctionImpl implements IAggregateSymbol {

    /**
     * @param aggregatesymbol
     */
    public AggregateSymbolImpl(AggregateSymbol aggregatesymbol) {
        super(aggregatesymbol);
    }

    @Override
    public AggregateSymbol getDelegate() {
        return (AggregateSymbol) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AggregateSymbolImpl clone() {
        return new AggregateSymbolImpl((AggregateSymbol) getDelegate().clone());
    }
    
    @Override
    public AggregateType getAggregateFunction() {
        String enumName = getDelegate().getAggregateFunction().name();
        return AggregateType.valueOf(enumName);
    }

    @Override
    public void setAggregateFunction(AggregateType aggregateType) {
        String enumName = aggregateType.name();
        getDelegate().setAggregateFunction(Type.valueOf(enumName));
    }

    
}