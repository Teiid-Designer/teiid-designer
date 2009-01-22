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
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * This class provides a mapping between the built-in types and the {@link JDBC types}.
 */
public interface RelationalTypeMapping {
    
    /** Constant denoting that the int type should not be registered */
    final int NO_INT_TYPE = -754888;
        
    public static class SQL_TYPE_NAMES {
        public static final String ARRAY            = "ARRAY";  //$NON-NLS-1$
        public static final String BIGINT           = "BIGINT";   //$NON-NLS-1$
        public static final String BINARY           = "BINARY";   //$NON-NLS-1$
        public static final String BIT              = "BIT";   //$NON-NLS-1$
        public static final String BLOB             = "BLOB";   //$NON-NLS-1$
        public static final String CHAR             = "CHAR";   //$NON-NLS-1$
        public static final String CLOB             = "CLOB";   //$NON-NLS-1$
        public static final String DATE             = "DATE";   //$NON-NLS-1$
        public static final String DECIMAL          = "DECIMAL";   //$NON-NLS-1$
        public static final String DISTINCT         = "DISTINCT";   //$NON-NLS-1$
        public static final String DOUBLE           = "DOUBLE";   //$NON-NLS-1$
        public static final String FLOAT            = "FLOAT";   //$NON-NLS-1$
        public static final String INTEGER          = "INTEGER";   //$NON-NLS-1$
        public static final String JAVA_OBJECT      = "JAVA_OBJECT";   //$NON-NLS-1$
        public static final String LONGVARBINARY    = "LONGVARBINARY";   //$NON-NLS-1$
        public static final String LONGVARCHAR      = "LONGVARCHAR";   //$NON-NLS-1$
        public static final String NCHAR            = "NCHAR";  //$NON-NLS-1$
        public static final String NTEXT            = "NTEXT";  //$NON-NLS-1$
        public static final String NULL             = "NULL";   //$NON-NLS-1$
        public static final String NUMERIC          = "NUMERIC";   //$NON-NLS-1$
        public static final String OTHER            = "OTHER";   //$NON-NLS-1$
        public static final String REAL             = "REAL";   //$NON-NLS-1$
        public static final String REF              = "REF";   //$NON-NLS-1$
        public static final String SMALLINT         = "SMALLINT";   //$NON-NLS-1$
        public static final String STRUCT           = "STRUCT";   //$NON-NLS-1$
        public static final String TIME             = "TIME";   //$NON-NLS-1$
        public static final String TIMESTAMP        = "TIMESTAMP";   //$NON-NLS-1$
        public static final String TINYINT          = "TINYINT";   //$NON-NLS-1$
        public static final String VARBINARY        = "VARBINARY";   //$NON-NLS-1$
        public static final String VARCHAR          = "VARCHAR";  //$NON-NLS-1$
    }
     
    /**
     * Find the {@link Datatype} that corresponds to the supplied type name from a JDBC data source.
     * @param jdbcTypeName the name of the JDBC type
     * @return the Datatype that best corresponds to the JDBC type name
     * @throws ModelerCoreException if there is a problem with the datatype manager
     */
    public EObject getDatatype( final String jdbcTypeName ) throws ModelerCoreException;
    
    /**
     * Find the {@link Datatype} that corresponds to the supplied {@link java.sql.Types JDBC type}.
     * @param jdbcType the {@link Type JDBC type}
     * @return the Datatype that best corresponds to the JDBC type, or null if no 
     * Datatype could be found or if the type is ambiguous (such as {@link Types#OTHER}).
     * @throws ModelerCoreException if there is a problem with the datatype manager
     */
    public EObject getDatatype( final int jdbcType ) throws ModelerCoreException;
    
    /**
     * Find the name of the JDBC type that corresponds to the supplied datatype.  If there is not
     * corresponding JDBC type, this method obtains the base type for the supplied type and looks
     * for its corresponding JDBC type.  This process continues until either the 
     * @param type the datatype for which the corresponding JDBC type is to be found
     * @return the name of the JDBC type that best corresponds to the supplied type; never null
     * @throws ModelerCoreException if there is a problem with the datatype manager
     */
    public String getJdbcTypeName( final EObject type ) throws ModelerCoreException;
    
    /**
     * Find the searchability type for the supplied Datatype.
     * @param datatype the datatype for which the searchability is to be found; may not be null
     * @return the SearchabilityType value
     */
    public SearchabilityType getSearchabilityType( final EObject datatype );

}
