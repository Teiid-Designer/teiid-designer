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
 * ColumnRecord
 */
public interface DatatypeRecord extends MetadataRecord {

    /**
     * Constants for names of accessor methods that map to fields stored  on the DatatypeRecords.
     * Note the names do not have "get" on them, this is also the nameInsource
     * of the attributes on SystemPhysicalModel.
     * @since 4.3
     */
    public interface MetadataFieldNames {

        String DATA_TYPE_UUID = "DatatypeID";  //$NON-NLS-1$
        String BASE_TYPE_UUID = "BasetypeID";  //$NON-NLS-1$
        String RUN_TYPE_NAME = "RuntimeTypeName";  //$NON-NLS-1$
    }

    /**
     * If the data type is numeric, the length is the total number of 
     * significant digits used to express the number. If it is a string, 
     * character array, or bit array it represents the maximum length of 
     * the value. For time and timestamp data types, the length is the number 
     * of positions that make up the fractional seconds.  The value of length
     * is a non-negative integer.
     * @return int
     */
    int getLength();
    
    /**
     * Returns an int indicating the precision length. Default to 
     * MetadataConstants.NOT_DEFINED_INT if not set.
     * @return int
     */
    int getPrecisionLength();
    
    /**
     * Returns the scale, which is the number of significant digits to the 
     * right of the decimal point. The scale cannot exceed the length, and the 
     * scale defaults to 0 (meaning it is an integer number and the decimal 
     * point is dropped).
     * @return int
     */
    int getScale();
    
    /**
     * Returns an int indicating the radix. Default to 
     * MetadataConstants.NOT_DEFINED_INT if not set.
     * @return int
     */
    int getRadix();
    
    /**
     * Returns a boolean indicating if the element data is signed.
     * @return boolean
     */
    boolean isSigned();
    
    /**
     * Returns a boolean indicating if the element is auto incremented by 
     * the database.  Therefore, this element value should not be provided 
     * on an insert statement.
     * @return boolean
     */
    boolean isAutoIncrement();
    
    /**
     * Returns a boolean indicating if the element data is case sensitive.
     * This value shall be false if the data type is not a character, 
     * character array or string type.
     * @return boolean
     */
    boolean isCaseSensitive();
    
    /**
     * Return short indicating the type.
     * @return short
     *
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.DATATYPE_TYPES
     */
    short getType();
    
    /**
     * Return whether this type represents a built-in.
     * @see #getType
     */
    boolean isBuiltin();
    
    /**
     * Returns a short indicating the serach typr.
     * @return short
     * 
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.SEARCH_TYPES 
     */
    short getSearchType();
    
    /**
     * Returns a short indicating if the element can be set to null.
     * @return short
     * 
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.NULL_TYPES 
     */
    short getNullType();
    
    /**
     * Returns the name of the Java class that represents this datatype
     * @return String is the name of the Java class
     */
    String getJavaClassName();
    
    /**
     * Returns the name of the runtime type that this datatype
     * is mapped to.
     * @return runtime type name
     */
    String getRuntimeTypeName();
    
    /**
     * Return a string that uniquely identifies the datatype. 
     * The string typically defines a URI reference constructed as follows:
     * <p>
     * 1. the base URI of the XML Schema namespace
     * 2. the fragment identifier defining the name of the datatype
     * </p>
     * @return String the identifier
     */
    String getDatatypeID();
    
    /**
     * Return a string that uniquely identifies the datatype for
     * which this datatype is an extension/restriction. If this 
     * datatype has no base or supertype then null is returned. 
     * The string typically defines a URI reference constructed as follows:
     * <p>
     * 1. the base URI of the XML Schema namespace
     * 2. the fragment identifier defining the name of the datatype
     * </p>
     * @return String the identifier
     */
    String getBasetypeID();
    
    /**
     * Return a string that uniquely identifies the built-in primitive 
     * datatype for which this datatype is an extension/restriction. If this 
     * datatype has no base or supertype then null is returned. 
     * The string typically defines a URI reference constructed as follows:
     * <p>
     * 1. the base URI of the XML Schema namespace
     * 2. the fragment identifier defining the name of the datatype
     * </p>
     * @return String the identifier
     */
    String getPrimitiveTypeID();
    
    /**
     * Return the name of the datatype for
     * which this datatype is an extension/restriction. If this 
     * datatype has no base or supertype then null is returned. 
     * <p>
     * The implementation simply computes the fragment from the {@link #getBasetypeID()}.
     * </p>
     * @return String the name
     */
    String getBasetypeName();
    
    /**
     * Returns the variety used to characterize the 
     * @param eObject The <code>EObject</code> to check 
     * @return short
     * 
     * @see com.metamatrix.modeler.core.metadata.runtime.MetadataConstants.DATATYPES_VARIETIES 
     */
    short getVarietyType();
    
    /**
     * Depending on the value of the variety type additional properties
     * may be defined.
     * @param eObject The <code>EObject</code> to check 
     * @return List
     */
    List getVarietyProps();

}
