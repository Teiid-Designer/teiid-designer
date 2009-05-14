/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.relational.SearchabilityType;

/**
 * FakeRelationalTypeMapping
 */
public class FakeRelationalTypeMapping implements RelationalTypeMapping {

    /**
     * Construct an instance of FakeRelationalTypeMapping.
     */
    public FakeRelationalTypeMapping() {
        super();
    }

    /**
     * @see com.metamatrix.metamodels.relational.util.RelationalTypeMapping#getDatatype(java.lang.String)
     */
    public EObject getDatatype( String jdbcTypeName ) {
        return null;
    }

    /**
     * @see com.metamatrix.metamodels.relational.util.RelationalTypeMapping#getDatatype(int)
     */
    public EObject getDatatype( int jdbcType ) {
        return null;
    }

    /**
     * @see com.metamatrix.metamodels.relational.util.RelationalTypeMapping#getJdbcTypeName(com.metamatrix.metamodels.core.Datatype)
     */
    public String getJdbcTypeName( EObject type ) {
        return null;
    }

    /**
     * @see com.metamatrix.metamodels.relational.util.RelationalTypeMapping#getSearchabilityType(com.metamatrix.metamodels.core.Datatype)
     */
    public SearchabilityType getSearchabilityType( EObject datatype ) {
        return null;
    }

}
