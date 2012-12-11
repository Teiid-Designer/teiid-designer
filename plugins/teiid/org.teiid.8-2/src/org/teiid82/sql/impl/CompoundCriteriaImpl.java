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
import org.teiid.designer.query.sql.lang.ICompoundCriteria;
import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.query.sql.lang.CompoundCriteria;
import org.teiid.query.sql.lang.Criteria;

/**
 *
 */
public class CompoundCriteriaImpl extends LogicalCriteriaImpl implements ICompoundCriteria {

    /**
     * @param compoundcriteria
     */
    public CompoundCriteriaImpl(CompoundCriteria compoundcriteria) {
        super(compoundcriteria);
    }

    @Override
    public CompoundCriteria getDelegate() {
        return (CompoundCriteria) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public CompoundCriteriaImpl clone() {
        return new CompoundCriteriaImpl((CompoundCriteria) getDelegate().clone());
    }

    @Override
    public Class<?> getType() {
        return getDelegate().getType();
    }

    @Override
    public List<ICriteria> getCriteria() {
        return getFactory().wrap(getDelegate().getCriteria());
    }

    @Override
    public int getCriteriaCount() {
        return getDelegate().getCriteriaCount();
    }

    @Override
    public ICriteria getCriteria(int index) {
        return getFactory().convert(getDelegate().getCriteria(index));
    }

    @Override
    public void addCriteria(ICriteria criteria) {
        Criteria criteriaImpl = (Criteria) getFactory().convert(criteria);
        getDelegate().addCriteria(criteriaImpl);
    }

    @Override
    public LogicalOperator getOperator() {
        int operator = getDelegate().getOperator();
        for (LogicalOperator logicalOperator : LogicalOperator.values()) {
            if (logicalOperator.index() == operator)
                return logicalOperator;
        }
        
        throw new RuntimeException();
    }
}