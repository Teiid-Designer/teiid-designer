/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.designer.query.sql.lang.INotCriteria;
import org.teiid.query.sql.lang.NotCriteria;

/**
 *
 */
public class NotCriteriaImpl extends AtomicCriteriaImpl implements INotCriteria {

    /**
     * @param notCriteria
     */
    public NotCriteriaImpl(NotCriteria notCriteria) {
        super(notCriteria);
    }

    @Override
    public NotCriteria getDelegate() {
        return (NotCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public NotCriteriaImpl clone() {
        return new NotCriteriaImpl((NotCriteria) getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
    }

    @Override
    public ICriteria getCriteria() {
        return getFactory().convert(getDelegate().getCriteria());
    }
}