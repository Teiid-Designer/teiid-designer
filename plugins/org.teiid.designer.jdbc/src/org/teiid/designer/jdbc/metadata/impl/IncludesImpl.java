/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.jdbc.metadata.Includes;
import org.teiid.designer.jdbc.metadata.JdbcDatabase;


/**
 * IncludesImpl
 *
 * @since 8.0
 */
public class IncludesImpl implements Includes {
    
    private static final String[] NO_TYPES = new String[]{};
    
    private final JdbcDatabase database;
    private String[] includedTableTypes;
    private boolean fks         = DEFAULT_INCLUDE_FOREIGN_KEYS;
    private boolean incompleteFKs = DEFAULT_INCLUDE_INCOMPLETE_FKS;
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
        CoreArgCheck.isNotNull(database);
        this.database = database;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#getJdbcDatabase()
     */
    @Override
	public JdbcDatabase getJdbcDatabase() {
        return this.database;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#getIncludedTableTypes()
     */
    @Override
	public String[] getIncludedTableTypes() {
        return includedTableTypes;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#getIncludedTableTypes(java.lang.String[])
     */
    @Override
	public void setIncludedTableTypes(String[] typesToInclude) {
        includedTableTypes = typesToInclude != null ? typesToInclude : NO_TYPES;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#includeProcedures()
     */
    @Override
	public boolean includeProcedures() {
        return procedures;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#setIncludeProcedures(boolean)
     */
    @Override
	public void setIncludeProcedures(boolean include) {
        procedures = include;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#setIncludeForeignKeys(boolean)
     */
    @Override
	public void setIncludeForeignKeys( boolean include ) {
        fks = include;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#includeForeignKeys()
     */
    @Override
	public boolean includeForeignKeys() {
        return fks;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#setIncludeIncompleteFKs(boolean)
     */
    @Override
	public void setIncludeIncompleteFKs( boolean include ) {
        incompleteFKs = include;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#includeIncompleteFKs()
     */
    @Override
	public boolean includeIncompleteFKs() {
        return incompleteFKs;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#includeIndexes()
     */
    @Override
	public boolean includeIndexes() {
        return indexes;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Includes#setIncludeIndexes(boolean)
     */
    @Override
	public void setIncludeIndexes(boolean include) {
        indexes = include;
    }

    /**
     * @return
     */
    @Override
	public boolean getApproximateIndexes() {
        return approximateIndexes;
    }

    /**
     * @return
     */
    @Override
	public boolean getUniqueIndexesOnly() {
        return uniqueIndexes;
    }

    /**
     * @param b
     */
    @Override
	public void setApproximateIndexes(boolean b) {
        approximateIndexes = b;
    }

    /**
     * @param b
     */
    @Override
	public void setUniqueIndexesOnly(boolean b) {
        uniqueIndexes = b;
    }

}
