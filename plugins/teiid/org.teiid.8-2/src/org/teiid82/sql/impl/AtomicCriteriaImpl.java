/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.lang.IAtomicCriteria;
import org.teiid.query.sql.lang.AtomicCriteria;

/**
 *
 */
public abstract class AtomicCriteriaImpl extends LogicalCriteriaImpl implements IAtomicCriteria {

    /**
     * @param atomicCriteria
     */
    public AtomicCriteriaImpl(AtomicCriteria atomicCriteria) {
        super(atomicCriteria);
    }

    @Override
    public AtomicCriteria getDelegate() {
        return (AtomicCriteria) delegate;
    }
}