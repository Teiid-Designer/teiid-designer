package org.teiid.designer.type;

import java.util.Set;
/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/

/**
 *
 */
public interface IDataTypeManagerService {

    /**
     * Enumerator of data type names supported by the
     * teiid DataTypeManager
     */
    enum DataTypeName {
        STRING,
        BOOLEAN,
        BYTE,
        SHORT,
        CHAR,
        INTEGER,
        LONG,
        BIG_INTEGER,
        FLOAT,
        DOUBLE,
        BIG_DECIMAL,
        DATE,
        TIME,
        TIMESTAMP,
        OBJECT,
        NULL,
        BLOB,
        CLOB,
        XML,
        VARBINARY
    }
    
    /**
     * Get the data type class with the given name.
     * 
     * @param name
     *      Data type name
     *      
     * @return Data type class
     */
    Class<?> getDataTypeClass(String name);
    
    /**
     * Get the runtime type for the given class
     *  
     * @param typeClass
     * 
     * @return runtime type
     */
    String getDataTypeName(Class<?> typeClass);
    
    /**
     * Get a set of all data type names.
     * 
     * @return Set of data type names (String)
     */
    Set<String> getAllDataTypeNames();
    
    /**
     * Get the default data type represented by the 
     * given {@link DataTypeName} enumerator
     * 
     * @param dataTypeName
     * 
     * @return name of data type or will throw a runtime exception
     *                if there is no data type.
     */
    String getDefaultDataType(DataTypeName dataTypeName);
    
    /**
     * Get the length of the data type
     * 
     * @param dataTypeName
     * 
     * @return integer indicating data type limit
     */
    Integer getDataTypeLimit(String dataType);
    
    /**
     * Get the valid characters of the data type
     * 
     * @param dataTypeName
     * 
     * @return string of valid characters or null if all characters are valid
     */
    String getDataTypeValidChars(String dataType);
    
    /**
     * Get the default data class represented by the 
     * given {@link DataTypeName} enumerator
     * 
     * @param dataTypeName
     * 
     * @return class of data type or will throw a runtime exception
     *                if there is no data type.
     */
    Class<?> getDefaultDataClass(DataTypeName dataTypeName);
    
    /**
     * Is the given source an explicit conversion of the target
     * 
     * @param srcType
     * @param tgtType
     * 
     * @return true if the conversion is explicit
     */
    boolean isExplicitConversion(String srcType, String tgtType);
    
    /**
     * Is the given source an implicit conversion of the target
     * 
     * @param srcType
     * @param tgtType
     * 
     * @return true if the conversion is implicit;
     */
    boolean isImplicitConversion(String srcType, String tgtType);

    /**
     *  Cache the given name if not already cached.
     * 
     * @param name
     */
    String getCanonicalString(String name);
    
    /**
     * Can a value transformation between the sourceType with given name
     * and the targetType of given name be attained. The Class for source and target type
     * are not needed to do this lookup.
     * 
     * @param sourceTypeName
     * @param targetTypeName
     * 
     * @return true if a transform is possible between the types
     */
    boolean canTransform(String sourceTypeName, String targetTypeName);
}
