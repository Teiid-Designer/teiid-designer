/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid772.sql.impl;

import org.teiid.designer.query.sql.lang.IPredicateCriteria;
import org.teiid.query.sql.lang.PredicateCriteria;

/**
 *
 */
public abstract class PredicateCriteriaImpl extends LanguageObjectImpl implements IPredicateCriteria {

    /**
     * @param predicateCriteria
     */
    public PredicateCriteriaImpl(PredicateCriteria predicateCriteria) {
        super(predicateCriteria);
    }

    @Override
    public PredicateCriteria getDelegate() {
        return (PredicateCriteria) delegate;
    }
}