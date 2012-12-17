/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import java.util.Collection;
import java.util.List;
import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IInsert;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.symbol.IElementSymbol;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.sql.lang.Insert;
import org.teiid.query.sql.symbol.ElementSymbol;
import org.teiid.query.sql.symbol.Expression;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 *
 */
public class InsertImpl extends CommandImpl implements IInsert {

    /**
     * @param insert
     */
    public InsertImpl(Insert insert) {
        super(insert);
    }

    @Override
    public Insert getDelegate() {
        return (Insert) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public InsertImpl clone() {
        return new InsertImpl((Insert) getDelegate().clone());
    }

    @Override
    public IGroupSymbol getGroup() {
        return getFactory().convert(getDelegate().getGroup());
    }

    @Override
    public void setGroup(IGroupSymbol group) {
        GroupSymbol groupSymbolImpl = getFactory().convert(group);
        getDelegate().setGroup(groupSymbolImpl);
    }

    @Override
    public List<IElementSymbol> getVariables() {
        return getFactory().wrap(getDelegate().getVariables());
    }

    @Override
    public void addVariable(IElementSymbol symbol) {
        ElementSymbol elementSymbolImpl = getFactory().convert(symbol);
        getDelegate().addVariable(elementSymbolImpl);
    }

    @Override
    public void addVariables(Collection<IElementSymbol> symbols) {
        Collection<ElementSymbol> elementSymbols = getFactory().unwrap(symbols);
        getDelegate().addVariables(elementSymbols);
    }

    @Override
    public List<IExpression> getValues() {
        return getFactory().wrap(getDelegate().getValues());
    }

    @Override
    public void setValues(List<IExpression> expressions) {
        List<Expression> expressionImpls = getFactory().unwrap(expressions);
        getDelegate().setValues(expressionImpls);
    }

    @Override
    public void setVariables(Collection<IElementSymbol> symbols) {
        List<ElementSymbol> symbolImpls = getFactory().unwrap(symbols);
        getDelegate().setVariables(symbolImpls);
    }

    @Override
    public IQueryCommand getQueryExpression() {
        return getFactory().convert(getDelegate().getQueryExpression());
    }

    @Override
    public boolean hasTupleSource() {
        return getDelegate().getTupleSource() != null;
    }
}