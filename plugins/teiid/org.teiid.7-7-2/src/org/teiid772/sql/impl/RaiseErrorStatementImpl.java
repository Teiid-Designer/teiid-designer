/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.ILanguageVisitor;
import org.teiid.designer.query.sql.proc.IRaiseStatement;
import org.teiid.query.sql.proc.RaiseErrorStatement;

/**
 *
 */
public class RaiseErrorStatementImpl extends StatementImpl implements IRaiseStatement {

    /**
     * @param raiseErrorStatement
     */
    public RaiseErrorStatementImpl(RaiseErrorStatement raiseErrorStatement) {
        super(raiseErrorStatement);
    }

    @Override
    public RaiseErrorStatement getDelegate() {
        return (RaiseErrorStatement) delegate;
    }

    @Override
    public void acceptVisitor(ILanguageVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RaiseErrorStatementImpl clone() {
        return new RaiseErrorStatementImpl(getDelegate().clone());
    }
}