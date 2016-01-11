package org.teiid.designer.type;

import java.util.Set;
import org.teiid.designer.annotation.Since;
/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
import org.teiid.designer.annotation.Updated;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

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
        BIGINTEGER,
        FLOAT,
        DOUBLE,
        BIGDECIMAL,
        DATE,
        TIME,
        TIMESTAMP,
        OBJECT,
        NULL,
        BLOB,
        CLOB,
        XML,
        VARCHAR,
        TINYINT,
        SMALLINT,
        BIGINT,
        REAL,
        DECIMAL,
        
        @Since(Version.TEIID_8_0)
        VARBINARY,

        @Since(Version.TEIID_8_10)
        GEOMETRY
    }

    public enum DataTypeAliases {
        VARCHAR("varchar"), //$NON-NLS-1$

        TINYINT("tinyint"), //$NON-NLS-1$

        SMALLINT("smallint"), //$NON-NLS-1$

        BIGINT("bigint"), //$NON-NLS-1$

        REAL("real"), //$NON-NLS-1$

        DECIMAL("decimal"); //$NON-NLS-1$
        
        private String id;

        /**
         * 
         */
        private DataTypeAliases(String id) {
            this.id = id;
        }

        /**
         * @return the id
         */
        public String getId() {
            return this.id;
        }
    }

    /**
     * Types of data source supported by teiid instances
     */
    enum DataSourceTypes {
        JDBC("connector-jdbc"), //$NON-NLS-1$

        @Updated(version=Version.TEIID_8_0, replaces="connector-salesforce")
        SALESFORCE("salesforce"), //$NON-NLS-1$

        @Updated(version=Version.TEIID_8_0, replaces="connector-ldap")
        LDAP("ldap"), //$NON-NLS-1$ 

        @Updated(version=Version.TEIID_8_0, replaces="connector-file")
        FILE("file"), //$NON-NLS-1$ 

        MONGODB("mongodb"), //$NON-NLS-1$ 

        JDBC_XA("connector-jdbc-xa"), //$NON-NLS-1$

        @Updated(version=Version.TEIID_8_0, replaces="connector-ws")
        WS("webservice"), //$NON-NLS-1$

        UNKNOWN("connector-unknown"); //$NON-NLS-1$

        private String id;

        DataSourceTypes(String id) {
            this.id = id;
        }

        public String id() {
            return this.id;
        }
    }
    
	/**
	 * see <code>org.teiid.core.types.JDBCSQLTypeInfo.java</code> for details
	 * FLOAT, DOUBLE, BIG_DECIMAL all show 20 as the default precision
	 */
	int DEFAULT_PRECISION = 0; 

    /**
     * Get the teiid instance specific name of the data source type
     *  
     * @param dataSourceType
     * 
     * @return data source type name
     */
    String getDataSourceType(DataSourceTypes dataSourceType);

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
    Integer getDataTypeLimit(String dataTypeName);
    
    /**
     * Get the valid characters of the data type
     * 
     * @param dataTypeName
     * 
     * @return string of valid characters or null if all characters are valid
     */
    String getDataTypeValidChars(String dataTypeName);
    
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
     * @param sourceTypeName
     * @param targetTypeName
     *
     * @return true if the conversion is explicit
     */
    boolean isExplicitConversion(String sourceTypeName, String targetTypeName);
    
    /**
     * Is the given source an implicit conversion of the target
     *
     * @param sourceTypeName
     * @param targetTypeName
     * 
     * @return true if the conversion is implicit;
     */
    boolean isImplicitConversion(String sourceTypeName, String targetTypeName);

    /**
     * Is the given data type variable length
     * @param dataTypeName
     * 
     * @return true if the data type supports length value
     */
    boolean isLengthDataType(String dataTypeName);
    
    /**
     * Is the given data type support precision and scale
     * @param dataTypeName
     * 
     * @return true if the data type supports precision and scale
     */
    boolean isPrecisionDataType(String dataTypeName);
    
    /**
     * Is the given data type support scale
     * @param dataTypeName
     * 
     * @return true if the data type supports scale
     */

	public boolean isScaleDataType(String dataTypeName);
    
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
    boolean isTransformable(String sourceTypeName, String targetTypeName);
    
}
