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
import org.teiid.designer.query.sql.lang.IFrom;
import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.sql.lang.From;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 *
 */
public class FromImpl extends LanguageObjectImpl implements IFrom {

    /**
     * @param from
     */
    public FromImpl(From from) {
        super(from);
    }

    @Override
    public From getDelegate() {
        return (From) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public FromImpl clone() {
        return new FromImpl((From) getDelegate().clone());
    }

    @Override
    public List<IFromClause> getClauses() {
        return getFactory().wrap(getDelegate().getClauses());
    }

    @Override
    public void setClauses(List<IFromClause> clauses) {
        List<FromClause> fromClausesImpl = getFactory().unwrap(clauses);
        getDelegate().setClauses(fromClausesImpl);
    }

    @Override
    public void addClause(IFromClause clause) {
        FromClause fromClauseImpl = getFactory().convert(clause);
        getDelegate().addClause(fromClauseImpl);
    }

    @Override
    public void addGroup(IGroupSymbol group) {
        GroupSymbol groupSymbolImpl = getFactory().convert(group);
        getDelegate().addGroup(groupSymbolImpl);
    }

    @Override
    public List<? extends IGroupSymbol> getGroups() {
        return getFactory().wrap(getDelegate().getGroups());
    }

    @Override
    public boolean containsGroup(IGroupSymbol group) {
        GroupSymbol groupSymbolImpl = getFactory().convert(group);
        return getDelegate().containsGroup(groupSymbolImpl);
    }
}