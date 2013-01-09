/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.sql.impl;

import org.teiid.designer.query.metadata.IQueryNode;
import org.teiid.query.mapping.relational.QueryNode;

/**
 *
 */
public class QueryNodeImpl implements IQueryNode {

    private QueryNode delegate;

    /**
     * @param queryNode
     */
    public QueryNodeImpl(QueryNode queryNode) {
        this.delegate = queryNode;
    }

    /**
     * @return delegate
     */
    public QueryNode getDelegate() {
        return delegate;
    }

    @Override
    public void addBinding(String binding) {
        delegate.addBinding(binding);
    }

}
