/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;

import java.util.List;

/**
 * <p>Instances of this interface represent Procedures in a Model.  The values of a Procedure are analogous to a Stored Procedure or Function in a database.</p> 
 */
public interface Procedure extends MetadataObject {
/**
 * Return the path to the procedure.
 *  @return String 
 */
    String getPath();
/**
 * Return the procedure description.
 * @return String 
 */
    String getDescription();
/**
 * Return the alias.
 *  @return String alias
 */
    String getAlias();
/**
 * Returns an ordered list  of type <code>ProcedureParameter</code> that represent all the parameters the procedure has.
 * @return List of ProcedureParameters
 * @see ProcedureParameter
 */
    List getParameters();
/**
 * Returns a boolean indicating if this procedure returns a result set.
 * @return boolean is true if a result will be returned
 */
    boolean returnsResults();
    
/**
 * Returns the queryPlan.
 * @return String
 */
public String getQueryPlan();
        
/**
 * Return short indicating the type of procedure.
 * @return short
 *   
 * @see com.metamatrix.metadata.runtime.api.MetadataConstants.PROCEDURE_TYPES
 */
    short getProcedureType();
}

