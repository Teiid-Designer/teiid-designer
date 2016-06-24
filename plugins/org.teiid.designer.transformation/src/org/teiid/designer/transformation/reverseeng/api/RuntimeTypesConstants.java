/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.reverseeng.api;

import static java.sql.Types.BIGINT;
import static java.sql.Types.BINARY;
import static java.sql.Types.BIT;
import static java.sql.Types.DATE;
import static java.sql.Types.DECIMAL;
import static java.sql.Types.DOUBLE;
import static java.sql.Types.FLOAT;
import static java.sql.Types.INTEGER;
import static java.sql.Types.SMALLINT;
import static java.sql.Types.TIME;
import static java.sql.Types.TIMESTAMP;
import static java.sql.Types.VARCHAR;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeAliases;
import org.teiid.designer.type.IDataTypeManagerService.DataTypeName;

public class RuntimeTypesConstants {
    /**
     * Keys: SQL int type definitions from java.sql.Types, Values: java class names
     */
    private static final Map<String, String> SQL_TO_JAVA_MAP = new HashMap<String, String>();
    
    private static final Map<String, DefaultDataTypes> SQL_TO_DTYPE_MAP = new HashMap<String, DefaultDataTypes>();
    
    /**
     * The names of the primitive built-in datatypes (19 entries).  A primitive built-in type
     * is one in which its basetype is the UR-type of "anySimpleType"
     * 
      string, time, date, char, biginteger, bigdecimal, float, double, integer, timestamp, object, clob, blob, geometry, varbinary
     * 
     PrimitiveBuiltInNames
     */
        public static final String STRING               = "string"; //$NON-NLS-1$
        public static final String BOOLEAN              = "boolean";//$NON-NLS-1$
        public static final String FLOAT                = "float";//$NON-NLS-1$
        public static final String DOUBLE               = "double";//$NON-NLS-1$
        public static final String DATE                 = "date";//$NON-NLS-1$
        public static final String TIME                 = "time";//$NON-NLS-1$
        public static final String CHAR                 = "char";//$NON-NLS-1$
        public static final String BIGDECIMAL           = "bigdecimal";//$NON-NLS-1$
        public static final String INTEGER              = "integer";//$NON-NLS-1$
        public static final String TIMESTAMP            = "timestamp";//$NON-NLS-1$
        public static final String OBJECT               = "object";//$NON-NLS-1$
        public static final String CLOB                 = "clob";//$NON-NLS-1$
        public static final String BLOB                 = "blob";//$NON-NLS-1$
        public static final String GEOMETRY             = "geometry";//$NON-NLS-1$
        public static final String VARBINARY            = "varbinary";//$NON-NLS-1$
        public static final String BYTE               	= "byte"; //$NON-NLS-1$
        public static final String LONG                 = "long"; //$NON-NLS-1$

    
    /**
     * JavaTypeClassNames
     */
        // char constants for Java data types
//        public static final String JAVA_LONG = "java.lang.Long";
//        public static final String JAVA_BYTES = "byte[]";
//        public static final String JAVA_BOOLEAN = "java.lang.Boolean";
//        public static final String JAVA_STRING = "java.lang.String";
//        public static final String JAVA_SQLDATE = "java.sql.Date";
//        public static final String JAVA_UTILDATE = "java.util.Date";
//        public static final String JAVA_BIGDECIMAL = "java.math.BigDecimal";
//        public static final String JAVA_DOUBLE = "java.lang.Double";
//        public static final String JAVA_FLOAT = "java.lang.Float";
//        public static final String JAVA_INTEGER = "java.lang.Integer";
//        public static final String JAVA_SHORT = "java.lang.Short";
//        public static final String JAVA_BYTE = "java.lang.Byte";
//        public static final String JAVA_TIME = "java.sql.Time";
//        public static final String JAVA_TIMESTAMP = "java.sql.Timestamp";
//        public static final String JAVA_BLOB = "java.sql.Blob";
//        public static final String JAVA_OBJECT = "java.lang.Object";
//        public static final String JAVA_CHARACTER = "java.lang.Character";
    
//    static {
//    	SQL_TO_JAVA_MAP.put(STRING, JAVA_STRING);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_BOOLEAN);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_BOOLEAN);
//    	SQL_TO_JAVA_MAP.put(FLOAT, JAVA_FLOAT);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_BIGDECIMAL);
//    	SQL_TO_JAVA_MAP.put(BLOB, JAVA_BLOB);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_BYTE);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_BYTES);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_DOUBLE);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_INTEGER);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_LONG);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_OBJECT);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_SHORT);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_SQLDATE);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_TIME);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_TIMESTAMP);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_UTILDATE);
//    	SQL_TO_JAVA_MAP.put(BOOLEAN, JAVA_UTILDATE);
//    }
    
    static {
    	SQL_TO_DTYPE_MAP.put(STRING, DefaultDataTypes.STRING);
    	SQL_TO_DTYPE_MAP.put(BOOLEAN, DefaultDataTypes.BOOLEAN);
    	SQL_TO_DTYPE_MAP.put(BYTE, DefaultDataTypes.BYTE);
    	SQL_TO_DTYPE_MAP.put(FLOAT, DefaultDataTypes.FLOAT);
    	SQL_TO_DTYPE_MAP.put(CHAR, DefaultDataTypes.CHAR);
    	SQL_TO_DTYPE_MAP.put(INTEGER, DefaultDataTypes.INTEGER);
    	SQL_TO_DTYPE_MAP.put(DOUBLE, DefaultDataTypes.DOUBLE);
    	SQL_TO_DTYPE_MAP.put(LONG, DefaultDataTypes.LONG);
    	SQL_TO_DTYPE_MAP.put(BIGDECIMAL, DefaultDataTypes.BIG_DECIMAL);
    	SQL_TO_DTYPE_MAP.put(DATE, DefaultDataTypes.DATE);
    	SQL_TO_DTYPE_MAP.put(TIME, DefaultDataTypes.TIME);
    	SQL_TO_DTYPE_MAP.put(TIMESTAMP, DefaultDataTypes.TIMESTAMP);
    	SQL_TO_DTYPE_MAP.put(OBJECT, DefaultDataTypes.OBJECT);
    	SQL_TO_DTYPE_MAP.put(GEOMETRY, DefaultDataTypes.OBJECT);
    	SQL_TO_DTYPE_MAP.put(CLOB, DefaultDataTypes.STRING);
    	SQL_TO_DTYPE_MAP.put(BLOB, DefaultDataTypes.OBJECT);
    }
    
    public enum DefaultDataTypes {

        STRING ("string", DataTypeName.STRING, String.class, 256, DataTypeAliases.VARCHAR), //$NON-NLS-1$
        
        BOOLEAN ("boolean", DataTypeName.BOOLEAN, Boolean.class), //$NON-NLS-1$

        BYTE ("byte", DataTypeName.BYTE, Byte.class, 3, "0123456789-", DataTypeAliases.TINYINT), //$NON-NLS-1$ //$NON-NLS-2$
        
        SHORT ("short", DataTypeName.SHORT, Short.class, 5, "0123456789-", DataTypeAliases.SMALLINT), //$NON-NLS-1$ //$NON-NLS-2$

        CHAR ("char", DataTypeName.CHAR, Character.class, 1), //$NON-NLS-1$

        INTEGER ("integer", DataTypeName.INTEGER, Integer.class, 10, "0123456789-"), //$NON-NLS-1$ //$NON-NLS-2$

        LONG ("long", DataTypeName.LONG, Long.class, 19, "0123456789-", DataTypeAliases.BIGINT), //$NON-NLS-1$ //$NON-NLS-2$

        BIG_INTEGER ("biginteger", DataTypeName.BIGINTEGER, BigInteger.class, 30, "0123456789-"), //$NON-NLS-1$ //$NON-NLS-2$

        FLOAT ("float", DataTypeName.FLOAT, Float.class, 30, "0123456789-+.eE", DataTypeAliases.REAL), //$NON-NLS-1$ //$NON-NLS-2$

        DOUBLE ("double", DataTypeName.DOUBLE, Double.class, 30, "0123456789-+.eE"), //$NON-NLS-1$ //$NON-NLS-2$

        BIG_DECIMAL ("bigdecimal", DataTypeName.BIGDECIMAL, BigDecimal.class, 30, "0123456789-.eE", DataTypeAliases.DECIMAL), //$NON-NLS-1$ //$NON-NLS-2$

        DATE ("date", DataTypeName.DATE, Date.class), //$NON-NLS-1$

        TIME ("time", DataTypeName.TIME, Time.class), //$NON-NLS-1$

        TIMESTAMP ("timestamp", DataTypeName.TIMESTAMP, Timestamp.class), //$NON-NLS-1$

        OBJECT ("object", DataTypeName.OBJECT, Object.class); //$NON-NLS-1$

//        NULL ("null", DataTypeName.NULL, NullType.class), //$NON-NLS-1$
//
//        BLOB ("blob", DataTypeName.BLOB, BlobType.class), //$NON-NLS-1$
//
//        CLOB ("clob", DataTypeName.CLOB, ClobType.class), //$NON-NLS-1$
//
//        XML ("xml", DataTypeName.XML, XMLType.class), //$NON-NLS-1$
//
//        @Since(Version.TEIID_8_0)
//        VARBINARY ("varbinary", DataTypeName.VARBINARY, BinaryType.class), //$NON-NLS-1$
//
//        @Since(Version.TEIID_8_10)
//        GEOMETRY ("geometry", DataTypeName.GEOMETRY, GeometryType.class); //$NON-NLS-1$

        private static Map<ITeiidServerVersion, List<DefaultDataTypes>> valueCache = new HashMap<ITeiidServerVersion, List<DefaultDataTypes>>();

        private String id;

        private DataTypeName dataTypeName;

        private Class<?> klazz;

        private int limit = -1;

        private String validChars = null;

        private Class<?> arrayKlazz;

        private Set<DataTypeAliases> aliases = Collections.emptySet();

        DefaultDataTypes(String id, DataTypeName dataTypeName, Class<?> klazz, DataTypeAliases... aliases) {
            this.id = id;
            this.dataTypeName = dataTypeName;
            this.klazz = klazz;
            if (aliases != null) {
                this.aliases = new HashSet<DataTypeAliases>();
                for (DataTypeAliases alias : aliases) {
                    this.aliases.add(alias);
                }
            }
            this.arrayKlazz = Array.newInstance(klazz, 0).getClass();
        }

        DefaultDataTypes(String id, DataTypeName dataTypeName, Class<?> klazz, int limit, DataTypeAliases... aliases) {
            this(id, dataTypeName, klazz, aliases);
            this.limit = limit;
        }

        DefaultDataTypes(String id, DataTypeName dataTypeName, Class<?> klazz, int limit, String validChars, DataTypeAliases... aliases) {
            this(id, dataTypeName, klazz, limit, aliases);
            this.validChars = validChars;
        }

        public String getId() {
            return this.id;
        }

        public DataTypeName getDataTypeName() {
            return dataTypeName;
        }

        public Class<?> getTypeClass() {
            return klazz;
        }

        public Class<?> getTypeArrayClass() {
            return arrayKlazz;
        }

        public int getLimit() {
            return this.limit;
        }

        public String getValidChars() {
            return validChars;
        }

        /**
         * @param alias
         * @return true if this type contains the given alias, false otherwise
         */
        public boolean hasAlias(DataTypeAliases alias) {
            return aliases.contains(alias);
        }

        /**
         * @param alias
         * @return true if this type contains the given alias, false otherwise
         */
        public boolean hasAlias(String aliasId) {
            for (DataTypeAliases alias : aliases) {
                if(alias.getId().equalsIgnoreCase(aliasId))
                    return true;
            }

            return false;
        }

//        /**
//         * Is the supplied class type a LOB based data type?
//         *
//         * @param type
//         * @return true if yes; false otherwise
//         */
//        public static boolean isLOB(Class<?> type) {
//            return BLOB.getTypeClass().equals(type)
//                    || CLOB.getTypeClass().equals(type)
//                    || XML.getTypeClass().equals(type)
//                    || GEOMETRY.getTypeClass().equals(type);
//        }

        /**
         * Use instead of values() since it will only return the enumerated values
         * that conform to the given teiid version.
         *
         * This is going to be used an awful lot so reduce the need to call on
         * {@link AnnotationUtils} which contains reflection code which is slow
         * by caching the results.
         *
         * @param teiidVersion
         *
         * @return set of values for teiid version
         */
        public static List<DefaultDataTypes> getValues(ITeiidServerVersion teiidVersion) {
            List<DefaultDataTypes> appDataTypes = valueCache.get(teiidVersion);

            if (appDataTypes == null) {
                appDataTypes = new ArrayList<DefaultDataTypes>();
                for (DefaultDataTypes dataType : DefaultDataTypes.values()) {
                    if (! AnnotationUtils.isApplicable(dataType, teiidVersion))
                        continue;

                    appDataTypes.add(dataType);
                }

                valueCache.put(teiidVersion, appDataTypes);
            }

            return appDataTypes;
        }

        /**
         * Return enum value for code but only if available for given teiid version
         *
         * @param teiidVersion
         * @param ordinal
         * @return enum value for ordinal
         */
        public static DefaultDataTypes valueOf(ITeiidServerVersion teiidVersion, int ordinal) {
            for (DefaultDataTypes dataType : DefaultDataTypes.values()) {
                if (! AnnotationUtils.isApplicable(dataType, teiidVersion))
                    continue;

                if (dataType.ordinal() == ordinal)
                    return dataType;
            }

            return null;
        }
    }
    
    
    public static String getJavaType(String runtimeTypeName) {
    	String typeString = SQL_TO_DTYPE_MAP.get(runtimeTypeName).getTypeClass().toString();
    	if( typeString.startsWith("class ") ) {
    		typeString = typeString.substring(6);
    	}
    	return typeString;
    }
}
