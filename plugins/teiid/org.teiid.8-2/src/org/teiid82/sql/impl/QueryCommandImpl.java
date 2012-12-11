/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.lang.IQueryCommand;
import org.teiid.query.sql.lang.QueryCommand;

/**
 *
 */
public abstract class QueryCommandImpl extends CommandImpl implements IQueryCommand {

    /**
     * @param queryCommand
     */
    public QueryCommandImpl(QueryCommand queryCommand) {
        super(queryCommand);
    }

    @Override
    public QueryCommand getDelegate() {
        return (QueryCommand) delegate;
    }
}