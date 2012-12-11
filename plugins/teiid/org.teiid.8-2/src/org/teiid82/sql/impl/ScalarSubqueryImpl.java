/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.designer.query.sql.symbol.IScalarSubquery;
import org.teiid.query.sql.lang.QueryCommand;
import org.teiid.query.sql.symbol.ScalarSubquery;

/**
 *
 */
public class ScalarSubqueryImpl extends ExpressionImpl implements IScalarSubquery {

    /**
     * @param scalarSubquery
     */
    public ScalarSubqueryImpl(ScalarSubquery scalarSubquery) {
        super(scalarSubquery);
    }

    @Override
    public ScalarSubquery getDelegate() {
        return (ScalarSubquery) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ScalarSubqueryImpl clone() {
        return new ScalarSubqueryImpl((ScalarSubquery) getDelegate().clone());
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
}