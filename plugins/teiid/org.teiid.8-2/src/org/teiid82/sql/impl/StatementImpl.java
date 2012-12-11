/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.proc.IStatement;
import org.teiid.query.sql.proc.Statement;

/**
 *
 */
public abstract class StatementImpl extends LanguageObjectImpl implements IStatement {

    /**
     * @param statement
     */
    public StatementImpl(Statement statement) {
        super(statement);
    }

    @Override
    public Statement getDelegate() {
        return (Statement) delegate;
    }

}