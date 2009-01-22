/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import org.eclipse.emf.ecore.EObject;

/**
 * SqlProcedureParameterAspect
 */
public interface SqlProcedureParameterAspect extends SqlAspect, SqlDatatypeCheckerAspect {

    /**
     * Get the datatype of the parameter
     * @param eObject The <code>EObject</code> for which datatype prop is obtained 
     * @return parameter's datatype
     */    
    EObject getDatatype(EObject eObject);

    /**
     * Get the name of the datatype of the parameter
     * @param eObject The <code>EObject</code> for which datatype prop is obtained 
     * @return name of the parameter's datatype
     */    
    String getDatatypeName(EObject eObject);

    /**
     * Get the string form of the datatype ObjectID of the parameter
     * @param eObject The <code>EObject</code> for which datatype type is obtained 
     * @return ObjectID string of the parameter's datatype
     */    
    String getDatatypeObjectID(EObject eObject);

    /**
     * Get the name of the runtime type of the parameter
     * @param eObject The <code>EObject</code> for which runtime type is obtained 
     * @return name of the parameter's runtime datatype
     */    
    String getRuntimeType(EObject eObject);

    /**
     * Get the default value of the parameter
     * @param eObject The <code>EObject</code> for which defaultValue prop is obtained 
     * @return parameter's default value
     */
    Object getDefaultValue(EObject eObject);

    /**
     * Get the parameter's nulltype
     * @param eObject The <code>EObject</code> for which nulltype prop is obtained 
     * @return true if the parameter's null type
     */
    int getNullType(EObject eObject);

    /**
     * Get the parameter's length
     * @param eObject The <code>EObject</code> for which length prop is obtained 
     * @return length of the parameter
     */
    int getLength(EObject eObject);

    /**
     * Get the parameter's position
     * @param eObject The <code>EObject</code> for which position prop is obtained 
     * @return length of the parameter
     */
    int getPosition(EObject eObject);

    /**
     * Get the parameter's radix
     * @param eObject The <code>EObject</code> for which radix prop is obtained 
     * @return length of the column
     */
    int getRadix(EObject eObject); 

    /**
     * Get the parameter's length
     * @param eObject The <code>EObject</code> for which scale prop is obtained 
     * @return length of the column
     */
    int getScale(EObject eObject);

    /**
     * Get the precision of the parameter
     * @param eObject The <code>EObject</code> for which precision prop is obtained 
     * @return parameter's precision
     */
    int getPrecision(EObject eObject);

    /**
     * Get the type of parameter
     * @return
     */
    int getType(EObject eObject);

    /**
     * Check if the parameter is optional
	 * @param eObject The <code>EObject</code> for which optional prop is obtained 
     * @return true if the parameter is optional
     */
    boolean isOptional(EObject eObject); 

    /**
     * Set the datatype for the parameter
     * @param eObject The <code>EObject</code> for which datatype prop is to be set
     * @param datatype The <code>Datatype</code> value
     */    
    void setDatatype(EObject eObject, EObject datatype);

    /**
     * Indicates if the {@link #setDatatype(EObject, EObject)} is a supported operation. 
     * @return <code>true</code>if supported; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean canSetDatatype();

    /**
     * Set the length for the parameter
     * @param eObject The <code>EObject</code> for which length prop is to be set
     * @param length The length value
     */    
    void setLength(EObject eObject, int length);
    
    /**
     * Indicates if the {@link #setLength(EObject, int)} is a supported operation. 
     * @return <code>true</code>if supported; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean canSetLength();

    /**
     * Set the null type for the parameter
     * @param eObject The <code>EObject</code> for which null type value is to be set
     * @param nullType The null type value
     */    
    void setNullType(EObject eObject, int nullType);
    
    /**
     * Indicates if the {@link #setNullType(EObject, int)} is a supported operation. 
     * @return <code>true</code>if supported; <code>false</code> otherwise.
     * @since 4.2
     */
    boolean canSetNullType();
    
    /**
     * @return true of the param is a either IN or IN OUT.  
     */
    boolean isInputParam(EObject eObject);
    
    /**
     * sets the direction of the parameter
     */
    void setDirection(EObject eObject, int dir);

}
