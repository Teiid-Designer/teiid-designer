/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.jdbc.metadata.Includes;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;

/**
 * IncludesImpl
 */
public class IncludesImpl implements Includes {
    
    private static final String[] NO_TYPES = new String[]{};
    
    private final JdbcDatabase database;
    private String[] includedTableTypes;
    private boolean fks         = DEFAULT_INCLUDE_FOREIGN_KEYS;
    private boolean indexes     = DEFAULT_INCLUDE_INDEXES;
    private boolean procedures  = DEFAULT_INCLUDE_PROCEDURES;
    private boolean approximateIndexes = DEFAULT_APPROXIMATE_INDEXES;
    private boolean uniqueIndexes = DEFAULT_UNIQUE_INDEXES;

    /**
     * Construct an instance of IncludesImpl.
     * 
     */
    public IncludesImpl( final JdbcDatabase database ) {
        super();
        ArgCheck.isNotNull(database);
        this.database = database;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#getJdbcDatabase()
     */
    public JdbcDatabase getJdbcDatabase() {
        return this.database;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#getIncludedTableTypes()
     */
    public String[] getIncludedTableTypes() {
        return includedTableTypes;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#getIncludedTableTypes(java.lang.String[])
     */
    public void setIncludedTableTypes(String[] typesToInclude) {
        includedTableTypes = typesToInclude != null ? typesToInclude : NO_TYPES;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#includeProcedures()
     */
    public boolean includeProcedures() {
        return procedures;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#setIncludeProcedures(boolean)
     */
    public void setIncludeProcedures(boolean include) {
        procedures = include;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#includeForeignKeys()
     */
    public boolean includeForeignKeys() {
        return fks;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#setIncludeForeignKeys(boolean)
     */
    public void setIncludeForeignKeys(boolean include) {
        fks = include;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#includeIndexes()
     */
    public boolean includeIndexes() {
        return indexes;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#setIncludeIndexes(boolean)
     */
    public void setIncludeIndexes(boolean include) {
        indexes = include;
    }

    /**
     * @return
     */
    public boolean getApproximateIndexes() {
        return approximateIndexes;
    }

    /**
     * @return
     */
    public boolean getUniqueIndexesOnly() {
        return uniqueIndexes;
    }

    /**
     * @param b
     */
    public void setApproximateIndexes(boolean b) {
        approximateIndexes = b;
    }

    /**
     * @param b
     */
    public void setUniqueIndexesOnly(boolean b) {
        uniqueIndexes = b;
    }

}
