/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.lang.IFromClause;
import org.teiid.query.sql.lang.FromClause;

/**
 *
 */
public abstract class FromClauseImpl extends LanguageObjectImpl implements IFromClause {

    /**
     * @param fromClause
     */
    public FromClauseImpl(FromClause fromClause) {
        super(fromClause);
    }

    @Override
    public FromClause getDelegate() {
        return (FromClause) delegate;
    }
}