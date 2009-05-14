/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational;

import org.eclipse.emf.ecore.EClass;

import com.metamatrix.modeler.jdbc.metadata.JdbcNode;

/**
 * JdbcNodeToRelationalMapping
 */
public interface JdbcNodeToRelationalMapping {
    
    /**
     * Obtain the {@link RelationalPackage Relational} {@link EClass} that is used to represent the
     * supplied {@link JdbcNode database object}.
     * @param jdbcNode the database object for which the Relational 
     * metaclass should be found; may not be null
     * @return the Relational metaclass that should be used to represent 
     * the database object; may be null only if there is no known 
     * metaclass for the supplied type of node
     */
    public EClass getRelationalClassForJdbcNode( JdbcNode jdbcNode );
    
    /**
     * Register a specific {@link RelationalPackage Relational} {@link EClass} for the supplied
     * {@link java.sql.DatabaseMetaData#getTableTypes() JDBC table type}.  The default for all
     * {@link com.metamatrix.modeler.jdbc.metadata.JdbcTable JdbcTable} instances is
     * {@link Table}.
     * @param tableType the JDBC table type; may not be null
     * @param eClassName the name of the {@link EClass} that should be used to represent
     * {@link JdbcTable database table objects}; may not be null or zero length
     */
    public void setRelationalClassForJdbcTableType( String tableType, String eClassName );

    /**
     * Get the {@link RelationalPackage Relational} {@link EClass} that is
     * currently registered for the supplied
     * {@link java.sql.DatabaseMetaData#getTableTypes() JDBC table type}.  The default for all
     * {@link com.metamatrix.modeler.jdbc.metadata.JdbcTable JdbcTable} instances is
     * {@link Table}.
     * <p>
     * If there is no EClass name registered for the supplied table type, this method
     * then checks for an EClass name registered for the {@link String#toUpperCase() uppercase}
     * form of the table type.
     * </p>
     * @param tableType the JDBC table type; may not be null
     * @return the name of the {@link EClass} that should be used to represent
     * {@link JdbcTable database table objects}; null if no EClass is registered
     * for the supplied table type or the uppercase form of the table type
     */
    public String getRelationalClassForJdbcTableType( String tableType );
}
