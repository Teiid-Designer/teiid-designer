/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IExistsCriteria;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.query.sql.lang.ExistsCriteria;
import org.teiid.query.sql.lang.QueryCommand;

/**
 *
 */
public class ExistsCriteriaImpl extends LanguageObjectImpl implements IExistsCriteria {

    /**
     * @param existsCriteria
     */
    public ExistsCriteriaImpl(ExistsCriteria existsCriteria) {
        super(existsCriteria);
    }

    @Override
    public ExistsCriteria getDelegate() {
        return (ExistsCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ExistsCriteriaImpl clone() {
        return new ExistsCriteriaImpl((ExistsCriteria) getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
    }

    @Override
    public IQueryCommand getCommand() {
        return getFactory().convert(getDelegate().getCommand());
    }

    @Override
    public void setCommand(IQueryCommand command) {
        QueryCommand commandImpl = getFactory().convert(command);
        getDelegate().setCommand(commandImpl);
    }

    @Override
    public boolean isNegated() {
        return getDelegate().isNegated();
    }

    @Override
    public void setNegated(boolean value) {
        getDelegate().setNegated(value);
    }
}