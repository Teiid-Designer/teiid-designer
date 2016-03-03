/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime;


/**
 * ColumnRecord
 *
 * @since 8.0
 */
public interface ColumnRecord extends MetadataRecord {
    
    /**
     * Check if the column is selectable
     * @return true if the column is selectable elese false
     */
    boolean isSelectable();

    /**
     * Check if the column is updatable
     * @return true if the column is updatable else false
     */
    boolean isUpdatable();    

    /**
     * Check if the column is nullable
     * @return nulltype of the column
     */
    int getNullType();

    /**
     * Check if the column is autoincrementable
     * @return true if the column is autoincrementable else false
     */    
    boolean isAutoIncrementable();

    /**
     * Check if the column is casesensitive
     * @return true if the column is casesensitive else false
     */    
    boolean isCaseSensitive();
    
    /**
     * Check if the column is signed type.
     * @return true if the column is of a fixed length
     */    
    boolean isSigned();
    
    /**
     * Check if the column is a currency type
     * @return true if the column is of a fixed length
     */    
    boolean isCurrency(); 
    
    /**
     * Check if the column is of a fixed length
     * @return true if the column is of a fixed length
     */    
    boolean isFixedLength();
    
    /**
     * Check if the column represents a column on a virtual group
     * that is mapped to a procedure's input parameter that is a 
     * transformation source.  In this sense, the virtual column
     * is an input parameter to the tranformation.
     * @return true if the column is a tranformation input parameter
     */    
    boolean isTranformationInputParameter();
    
    /**
     * Check if the column is searcheable in a LIKE clause
     * @return true if the column is searcheable in a LIKE clause
     */    
    int getSearchType();

    /**
     * Get the default value of the column
     * @return column's default value
     */
    String getDefaultValue();

    /**
     * Get the minimum value of the column
     * @return column's minimum value
     */
    Object getMinValue();

    /**
     * Get the maximum value of the column
     * @return column's maximum value
     */
    Object getMaxValue();
    
    /**
     * Get the format of the column
     * @return column's format
     */
    String getFormat();    

    /**
     * Get the column's length
     * @return length of the column
     */
    int getLength();
    
    /**
     * Get the column's length
     * @return length of the column
     */
    int getScale();     

    /**
     * Get the runtime type name of the column
     * @return column's runtime type
     */    
    String getRuntimeType();

    /**
     * Get the native type name of the column
     * @return column's native type
     */    
    String getNativeType();    

    /**
     * Get the UUID of the datatype associated with the column
     * @return the UUID of the datatype
     */
    String getDatatypeUUID();
    
    /**
     * Get the precision of the column
     * @return column's precision
     */
    int getPrecision();
    
    /**
     * Get the position of the column within its parent
     * @return column's position
     */
    int getPosition();
    
    /**
     * Get the charOctetLength of the column
     * @return column's char octet length
     */
    int getCharOctetLength();

    /**
     * Get the column's radix
     * @return the column's radix
     */
    int getRadix();

    /**
     * Get the number of distinct values this column has in the table. 
     * @return distinct values for the column
     * @since 4.3
     */
    int getDistinctValues();

    /**
     * Get the number of null values this column has in the table. 
     * @return null values for the column
     * @since 4.3
     */
    int getNullValues();

}