/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IUnaryFromClause;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.sql.lang.UnaryFromClause;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 *
 */
public class UnaryFromClauseImpl extends FromClauseImpl implements IUnaryFromClause {

    /**
     * @param unaryFromClause
     */
    public UnaryFromClauseImpl(UnaryFromClause unaryFromClause) {
        super(unaryFromClause);
    }

    @Override
    public UnaryFromClause getDelegate() {
        return (UnaryFromClause) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public UnaryFromClauseImpl clone() {
        return new UnaryFromClauseImpl((UnaryFromClause) getDelegate().clone());
    }

    @Override
    public boolean isOptional() {
        return getDelegate().isOptional();
    }

    @Override
    public void setOptional(boolean optional) {
        getDelegate().setOptional(optional);
    }

    @Override
    public boolean isMakeDep() {
        return getDelegate().isMakeDep();
    }

    @Override
    public void setMakeDep(boolean makeDep) {
        getDelegate().setMakeDep(makeDep);
    }

    @Override
    public boolean isMakeNotDep() {
        return getDelegate().isMakeNotDep();
    }

    @Override
    public void setMakeNotDep(boolean makeNotDep) {
        getDelegate().setMakeNotDep(makeNotDep);
    }

    @Override
    public void setGroup(IGroupSymbol group) {
        GroupSymbol groupSymbolImpl = getFactory().convert(group);
        getDelegate().setGroup(groupSymbolImpl);
    }

    @Override
    public IGroupSymbol getGroup() {
        return getFactory().convert(getDelegate().getGroup());
    }
}