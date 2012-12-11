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
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.IDelete;
import org.teiid.designer.query.sql.lang.IExpression;
import org.teiid.designer.query.sql.lang.IOption;
import org.teiid.designer.query.sql.symbol.IGroupSymbol;
import org.teiid.query.sql.lang.Criteria;
import org.teiid.query.sql.lang.Delete;
import org.teiid.query.sql.symbol.GroupSymbol;

/**
 *
 */
public class DeleteImpl extends LanguageObjectImpl implements IDelete {

    /**
     * @param delete
     */
    public DeleteImpl(Delete delete) {
        super(delete);
    }

    @Override
    public Delete getDelegate() {
        return (Delete) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DeleteImpl clone() {
        return new DeleteImpl((Delete) getDelegate().clone());
    }

    @Override
    public int getType() {
        return getDelegate().getType();
    }

    @Override
    public IOption getOption() {
        return getFactory().convert(getDelegate().getOption());
    }

    @Override
    public List<IExpression> getProjectedSymbols() {
        return getFactory().wrap(getDelegate().getProjectedSymbols());
    }

    @Override
    public boolean isResolved() {
        return getDelegate().isResolved();
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
    public ICriteria getCriteria() {
        return getFactory().convert(getDelegate().getCriteria());
    }

    @Override
    public void setCriteria(ICriteria criteria) {
        Criteria criteriaImpl = getFactory().convert(criteria);
        getDelegate().setCriteria(criteriaImpl);
    }
}