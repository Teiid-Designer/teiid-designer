/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.ICommand;
import org.teiid.designer.query.sql.lang.ISubqueryFromClause;
import org.teiid.query.sql.lang.Command;
import org.teiid.query.sql.lang.SubqueryFromClause;

/**
 *
 */
public class SubqueryFromClauseImpl extends FromClauseImpl implements ISubqueryFromClause {

    /**
     * @param subqueryFromClause
     */
    public SubqueryFromClauseImpl(SubqueryFromClause subqueryFromClause) {
        super(subqueryFromClause);
    }

    @Override
    public SubqueryFromClause getDelegate() {
        return (SubqueryFromClause) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public SubqueryFromClauseImpl clone() {
        return new SubqueryFromClauseImpl((SubqueryFromClause) getDelegate().clone());
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
    public ICommand getCommand() {
        return getFactory().convert(getDelegate().getCommand());
    }

    @Override
    public void setCommand(ICommand command) {
        Command commandImpl = getFactory().convert(command);
        getDelegate().setCommand(commandImpl);
    }

    @Override
    public String getName() {
        return getDelegate().getName();
    }

    @Override
    public void setName(String name) {
        getDelegate().setName(name);
    }
}