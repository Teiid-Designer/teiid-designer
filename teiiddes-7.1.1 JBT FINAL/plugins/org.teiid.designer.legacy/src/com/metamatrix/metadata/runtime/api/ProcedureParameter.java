/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;


/**
 * <p>Instances of this interface represent Parameters for a Procedure.  A Procedure can have various types of parameters.  The types are as follows:
 *  <ul>
 *  <li>IN - Input parameter</li>
 *  <li>OUT - Output parameter</li>
 *  <li>INOUT - Input-Output parameter</li>
 *  <li>RETURN VALUE - a return value</li>
 *  <li>RESULT SET(S) - one or more nested result sets</li>
 *  </ul>
 * </p> 
 */
public interface ProcedureParameter  {
/**
 * Returns the <code>DataType</code> this parameter will be represented as.
 * @return DataType 
 */
    DataType getDataType();
/**
 * Return short indicating the type of parameter.
 * @return short
 *
 * @see com.metamatrix.metadata.runtime.api.MetadataConstants.PARAMETER_TYPES
 */
    short getParameterType();
/**
 * Returns a boolean indicating if this parameter is optional.
 * @return boolean true when the parameter is optional 
 */
    boolean isOptional();
/**
 * Returns the order of parameter in relation to the other parameters that are of the same type for its procedure.
 *  @return int postion
 */
    int getPosition();
    int getResultSetPosition();
    String getName();
/**
 * Returns the default value of parameter if it is optional.
 *  @return int postion
 */
    String getDefaultValue();
/**
 * Returns the procID.
 * @return ProcedureID
 */
    ProcedureID getProcID();
    
    
}

