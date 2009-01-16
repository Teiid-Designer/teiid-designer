/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.core.metamodel.aspect.sql;

import org.eclipse.emf.ecore.EObject;


/**
 * SqlColumnAspect is used to get the different properties on a column for runtime metadata.
 */
public interface SqlColumnAspect extends SqlAspect, SqlDatatypeCheckerAspect {
    
    /**
     * Check if the column is selectable
     * @param eObject The <code>EObject</code> for which selectable prop is obtained 
     * @return true if the column is selectable elese false
     */
    boolean isSelectable(EObject eObject);

    /**
     * Check if the column is updatable
     * @param eObject The <code>EObject</code> for which updatable prop is obtained 
     * @return true if the column is updatable else false
     */
    boolean isUpdatable(EObject eObject);    

    /**
     * Get the column's nulltype
     * @param eObject The <code>EObject</code> for which nulltype prop is obtained 
     * @return true if the column's null type
     */
    int getNullType(EObject eObject);

    /**
     * Check if the column is autoincrementable
     * @param eObject The <code>EObject</code> for which autoincrementable prop is obtained 
     * @return true if the column is autoincrementable else false
     */    
    boolean isAutoIncrementable(EObject eObject);

    /**
     * Check if the column is casesensitive
     * @param eObject The <code>EObject</code> for which caseSensitive prop is obtained 
     * @return true if the column is casesensitive else false
     */    
    boolean isCaseSensitive(EObject eObject);
    
    /**
     * Check if the column is signed type.
     * @param eObject The <code>EObject</code> for which signed prop is obtained 
     * @return true if the column is of a fixed length
     */    
    boolean isSigned(EObject eObject);
    
    /**
     * Check if the column is a currency type
     * @param eObject The <code>EObject</code> for which currency type prop is obtained 
     * @return true if the column is of a fixed length
     */    
    boolean isCurrency(EObject eObject); 
    
    /**
     * Check if the column is of a fixed length
     * @param eObject The <code>EObject</code> for which fixedLength prop is obtained 
     * @return true if the column is of a fixed length
     */    
    boolean isFixedLength(EObject eObject);
    
    /**
     * Check if the column represents a column on a virtual group
     * that is mapped to a procedure's input parameter that is a 
     * transformation source.  In this sense, the virtual column
     * is an input parameter to the tranformation.
     * @param eObject The <code>EObject</code>
     * @return true if the column is a tranformation input parameter
     */    
    boolean isTranformationInputParameter(EObject eObject);
    
    /**
     * Check if the column is searcheable in a LIKE clause
     * @param eObject The <code>EObject</code> for which search type is obtained 
     * @return true if the column is searcheable in a LIKE clause
     */    
    int getSearchType(EObject eObject);

    /**
     * Get the default value of the column
     * @param eObject The <code>EObject</code> for which defaultValue prop is obtained 
     * @return column's default value
     */
    Object getDefaultValue(EObject eObject);
    
    /**
     * Get the minimum value of the column
     * @param eObject The <code>EObject</code> for which minimum value prop is obtained 
     * @return column's minimum value
     */
    Object getMinValue(EObject eObject);

    /**
     * Get the maximum value of the column
     * @param eObject The <code>EObject</code> for which maximum value prop is obtained 
     * @return column's maximum value
     */
    Object getMaxValue(EObject eObject);
    
    /**
     * Get the format of the column
     * @param eObject The <code>EObject</code> for which format is obtained 
     * @return column's format
     */
    String getFormat(EObject eObject);    

    /**
     * Get the column's length
     * @param eObject The <code>EObject</code> for which length prop is obtained 
     * @return length of the column
     */
    int getLength(EObject eObject);
    
    /**
     * Get the column's length
     * @param eObject The <code>EObject</code> for which scale prop is obtained 
     * @return length of the column
     */
    int getScale(EObject eObject);

    /**
     * Get the column's radix
     * @param eObject The <code>EObject</code> for which scale prop is obtained 
     * @return length of the column
     */
    int getRadix(EObject eObject);     

    /**
     * Get the number of distinct values this column has in the table. 
     * @param eObject The <code>EObject</code> for which scale prop is obtained 
     * @return distinct values for the column
     * @since 4.3
     */
    int getDistinctValues(EObject eObject);

    /**
     * Get the number of null values this column has in the table. 
     * @param eObject The <code>EObject</code> for which scale prop is obtained
     * @return null values for the column
     * @return
     * @since 4.3
     */
    int getNullValues(EObject eObject);

    /**
     * Get the datatype of the column
     * @param eObject The <code>EObject</code> for which datatype prop is obtained 
     * @return column's datatype
     */    
    EObject getDatatype(EObject eObject);

    /**
     * Set the datatype for the column
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
     * Set the length for the column
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
     * Set the null type for the column
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
     * Get the name of the datatype of the column
     * @param eObject The <code>EObject</code> for which datatype prop is obtained 
     * @return name of the column's datatype
     */    
    String getDatatypeName(EObject eObject);
    
    /**
     * Get the name of the runtime type of the column
     * @param eObject The <code>EObject</code> for which runtime type is obtained 
     * @return name of the column's runtime datatype
     */
    String getRuntimeType(EObject eObject);

    /**
     * Get the name of the native type of the column
     * @param eObject The <code>EObject</code> for which native type is obtained 
     * @return name of the column's native datatype
     */
    String getNativeType(EObject eObject);    

    /**
     * Get the string form of the datatype ObjectID of the column
     * @param eObject The <code>EObject</code> for which datatype type is obtained 
     * @return ObjectID string of the column's datatype
     */    
    String getDatatypeObjectID(EObject eObject);
    
    /**
     * Get the precision of the column
     * @param eObject The <code>EObject</code> for which precision prop is obtained 
     * @return column's precision
     */
    int getPrecision(EObject eObject);
    
    /**
     * Get the position of the column within its container
     * @param eObject The <code>EObject</code> for which position prop is obtained 
     * @return column's position
     */
    int getPosition(EObject eObject);
    
    /**
     * Get the charOctetLength of the column
     * @param eObject The <code>EObject</code> for which charOctetLength prop is obtained 
     * @return column's char octect length
     */
    int getCharOctetLength(EObject eObject);

}
