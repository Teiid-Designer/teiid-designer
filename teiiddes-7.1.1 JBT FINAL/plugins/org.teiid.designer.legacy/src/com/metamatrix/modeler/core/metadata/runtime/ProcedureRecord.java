/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

import java.util.List;

/**
 * ProcedureRecord
 */
public interface ProcedureRecord extends MetadataRecord {

    /**
     * Constants for perperties stored on a ProcedureRecord 
     * @since 4.3
     */
    public interface ProcedureRecordProperties {

        String STORED_PROC_INFO_FOR_RECORD  = "storedProcInfoForRecord";  //$NON-NLS-1$
        
    }

    /**
     * Get a list of identifiers for the parameters in the procedure
     * @return a list of identifiers
     */
    List getParameterIDs();
    
    /**
     * Check if this record is for a procedure that is a function.
     * @return true if the procedure is a function
     */
    boolean isFunction();    

    /**
     * Check if this record is for a procedure that is a virtual.
     * @return true if the procedure is a virtual
     */
    boolean isVirtual();

    /**
     * Get the identifier for a resultSet in the procedure
     * @return an identifier for the resultSet.
     */    
    Object getResultSetID();
    
    /**
     * Return short indicating of PROCEDURE it is. 
     * @return short
     *
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.PROCEDURE_TYPES
     */
    short getType();
    
    int getUpdateCount();
    
}