/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.lang.ICriteria;
import org.teiid.query.sql.lang.Criteria;

/**
 *
 */
public abstract class CriteriaImpl extends ExpressionImpl implements ICriteria {

    /**
     * @param criteria
     */
    public CriteriaImpl(Criteria criteria) {
        super(criteria);
    }
    
    @Override
    public Criteria getDelegate() {
        return (Criteria) delegate;
    }
}
