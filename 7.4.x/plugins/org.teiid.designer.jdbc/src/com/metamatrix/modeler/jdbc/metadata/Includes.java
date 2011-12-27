/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

/**
 * This interface defines the information that should be included when
 * accessing the metadata for a {@link JdbcDatabase}.
 */
public interface Includes {
    
    /**
     * The default is to include foreign keys.
     */
    public final boolean DEFAULT_INCLUDE_FOREIGN_KEYS = true;

    /**
     * The default is to include incomplete foreign keys.
     */
    public final boolean DEFAULT_INCLUDE_INCOMPLETE_FKS = true;

    /**
     * The default is to include indexes.
     */
    public final boolean DEFAULT_INCLUDE_INDEXES = true;

    /**
     * The default is to <i>not</i> include procedures.
     */
    public final boolean DEFAULT_INCLUDE_PROCEDURES = false;

    /**
     * The default is to allow approximations.
     */
    public final boolean DEFAULT_APPROXIMATE_INDEXES = true;

    /**
     * The default is to not do only unique indexes.
     */
    public final boolean DEFAULT_UNIQUE_INDEXES = false;

    /**
     * Return the {@link JdbcDatabase} object that contains this node.
     * @return the database; may not be null
     */
    public JdbcDatabase getJdbcDatabase();

    /**
     * Return the table types that are to be included.
     * @return the table types; null if the table types have not been set, or empty
     * if no table types (i.e, no tables) are to be included
     * @see Capabilities#getTableTypes()
     */
    public String[] getIncludedTableTypes();
    
    /**
     * Set the table types that are to be included.
     * @param typesToInclude the table types; may be null if all the table types are to be included,
     * or empty if no table types (i.e, no tables) are to be included
     * @see Capabilities#getTableTypes()
     */
    public void setIncludedTableTypes( String[] typesToInclude );

    /**
     * Return whether procedures should be included.
     * @return true if procedures should be included, or false otherwise.
     */
    public boolean includeProcedures();
    
    /**
     * Set whether procedures should be included.
     * @param include true if procedures should be included, or false otherwise.
     */
    public void setIncludeProcedures( boolean include );

    /**
     * Return whether foreign keys should be included.
     * @return true if foreign keys should be included, or false otherwise.
     */
    public boolean includeForeignKeys();
    
    /**
     * Set whether foreign keys should be included.
     * @param include true if foreign keys should be included, or false otherwise.
     */
    public void setIncludeForeignKeys( boolean include );

    /**
     * Return whether incomplete foreign keys should be included.
     * 
     * @return true if incomplete foreign keys should be included, or false otherwise.
     */
    public boolean includeIncompleteFKs();

    /**
     * Set whether incomplete foreign keys should be included.
     * 
     * @param include true if incomplete foreign keys should be included, or false otherwise.
     */
    public void setIncludeIncompleteFKs( boolean include );

    /**
     * Return whether indexes should be included.
     * @return true if indexes should be included, or false otherwise.
     */
    public boolean includeIndexes();
    
    /**
     * Set whether indexes should be included.
     * @param include true if indexes should be included, or false otherwise.
     */
    public void setIncludeIndexes( boolean include );

    /**
     * Get whether approximate information for indexes is allowed.
     * @return true if approximate index information is allowed, or false if exact information is required.
     */
    public boolean getApproximateIndexes();
    
    /**
     * Set whether approximate information for indexes is allowed.
     * @param approximate true if approximate index information is allowed, or false if exact information is required.
     */
    public void setApproximateIndexes( boolean approximate );
    
    /**
     * get whether only unique indexes should be included.
     * @return true if only unique indexes should be included, or false if all indexes should be included.
     */
    public boolean getUniqueIndexesOnly();

    /**
     * Set whether only unique indexes should be included.
     * @param uniqueOnly true if only unique indexes should be included, or false if all indexes should be included.
     */
    public void setUniqueIndexesOnly( boolean uniqueOnly );

}
