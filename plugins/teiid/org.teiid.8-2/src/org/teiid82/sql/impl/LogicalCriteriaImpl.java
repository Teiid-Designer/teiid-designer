/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid82.sql.impl;

import org.teiid.designer.query.sql.lang.ILogicalCriteria;
import org.teiid.query.sql.lang.LogicalCriteria;

/**
 *
 */
public abstract class LogicalCriteriaImpl extends LanguageObjectImpl implements ILogicalCriteria {

    /**
     * @param logicalcriteria
     */
    public LogicalCriteriaImpl(LogicalCriteria logicalcriteria) {
        super(logicalcriteria);
    }

    @Override
    public LogicalCriteria getDelegate() {
        return (LogicalCriteria) delegate;
    }
}