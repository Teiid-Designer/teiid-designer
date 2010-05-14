/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata;

import java.sql.DatabaseMetaData;

/**
 * JdbcProcedure
 */
public interface JdbcProcedure extends JdbcNode {
    
    public static final short RESULT_UNKNOWN    = DatabaseMetaData.procedureResultUnknown;
    public static final short NO_RESULT         = DatabaseMetaData.procedureNoResult;
    public static final short RETURNS_RESULT    = DatabaseMetaData.procedureReturnsResult;

    /**
     * Get the remarks for this table.
     * @return the remarks; may be null or empty
     */
    public String getRemarks();
    
    /**
     * Get the type of this procedure.  One of {@link #RESULT_UNKNOWN}, {@link #NO_RESULT} or
     * {@link #RETURNS_RESULT}.
     * @return the procedure type
     */
    public short getProcedureType();
}
