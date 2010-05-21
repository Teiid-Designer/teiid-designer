/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.modeler.core.metadata.runtime;

/**
 * ProcedureParameterRecord
 */
public interface ProcedureParameterRecord extends MetadataRecord {

    /**
     * Get the runtime type name of the parameter
     * @return column's runtime type
     */    
    String getRuntimeType();

    /**
     * Get the UUID of the datatype associated with the column
     * @return the UUID of the datatype
     */
    String getDatatypeUUID();

    /**
     * Get the default value of the parameter
     * @return parameter's default value
     */
    Object getDefaultValue();
    
    /**
     * Get the length of the parameter
     * @return parameter's length
     */
    int getLength();

    /**
     * Get the nullability of the parameter
     * @return parameter's nullability
     */
    int getNullType();

    /**
     * Get the precision of the parameter
     * @return parameter's precision
     */
    int getPrecision();

    /**
     * Get the position of the parameter
     * @return parameter's position
     */
    int getPosition();

    /**
     * Get the scale of the parameter
     * @return parameter's scale
     */
    int getScale();

    /**
     * Get the radix of the parameter
     * @return parameter's radix
     */
    int getRadix();

    /**
     * Check if the parameter is optional
     * @return true if this parameter is optional
     */
    boolean isOptional();

    /**
     * Return short indicating the type of KEY it is. 
     * @return short
     *
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.PARAMETER_TYPES
     */
    short getType();
}