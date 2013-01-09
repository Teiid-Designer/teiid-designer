/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.designer.query.sql.lang.IJoinPredicate;
import org.teiid.query.sql.lang.FromClause;
import org.teiid.query.sql.lang.JoinPredicate;

/**
 *
 */
public class JoinPredicateImpl extends FromClauseImpl implements IJoinPredicate {

    /**
     * @param joinPredicate
     */
    public JoinPredicateImpl(JoinPredicate joinPredicate) {
        super(joinPredicate);
    }

    @Override
    public JoinPredicate getDelegate() {
        return (JoinPredicate) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public JoinPredicateImpl clone() {
        return new JoinPredicateImpl((JoinPredicate) getDelegate().clone());
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
    public IFromClause getLeftClause() {
        return getFactory().convert(getDelegate().getLeftClause());
    }

    @Override
    public void setLeftClause(IFromClause fromClause) {
        FromClause fromClauseImpl = getFactory().convert(fromClause);
        getDelegate().setLeftClause(fromClauseImpl);
    }

    @Override
    public IFromClause getRightClause() {
        return getFactory().convert(getDelegate().getRightClause());
    }

    @Override
    public void setRightClause(IFromClause fromClause) {
        FromClause fromClauseImpl = getFactory().convert(fromClause);
        getDelegate().setRightClause(fromClauseImpl);
    }
}