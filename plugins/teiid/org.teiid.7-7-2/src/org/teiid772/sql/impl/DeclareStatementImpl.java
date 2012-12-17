/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.proc.IDeclareStatement;
import org.teiid.query.sql.proc.DeclareStatement;

/**
 *
 */
public class DeclareStatementImpl extends AssignmentStatementImpl implements IDeclareStatement {

    /**
     * @param declareStatement
     */
    public DeclareStatementImpl(DeclareStatement declareStatement) {
        super(declareStatement);
    }

    @Override
    public DeclareStatement getDelegate() {
        return (DeclareStatement) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DeclareStatementImpl clone() {
        return new DeclareStatementImpl((DeclareStatement) getDelegate().clone());
    }

    @Override
    public String getVariableType() {
        return getDelegate().getVariableType();
    }
}