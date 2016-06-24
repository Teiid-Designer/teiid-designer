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

import org.eclipse.core.runtime.IStatus;
import org.teiid.core.designer.TeiidDesignerRuntimeException;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.metamodels.transformation.TransformationPlugin;
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
        public static final String ANYURI               = "anyURI"; //$NON-NLS-1$
        public static final String BASE64BINARY         = "base64Binary"; //$NON-NLS-1$
        public static final String BIGINTEGER           = "biginteger"; //$NON-NLS-1$
        public static final String DATETIME             = "dateTime"; //$NON-NLS-1$
        public static final String DECIMAL              = "decimal"; //$NON-NLS-1$
        public static final String DURATION             = "duration"; //$NON-NLS-1$
        public static final String ENTTIES              = "ENTITIES"; //$NON-NLS-1$
        public static final String ENTITY               = "ENTITY"; //$NON-NLS-1$
        public static final String GDAY                 = "gDay"; //$NON-NLS-1$
        public static final String GMONTH               = "gMonth"; //$NON-NLS-1$
        public static final String GMONTYDAY            = "gMonthDay"; //$NON-NLS-1$
        public static final String GYEAR                = "gYear"; //$NON-NLS-1$
        public static final String GYEARMONTH           = "gYearMonth"; //$NON-NLS-1$
        public static final String HEXBINARY            = "hexBinary"; //$NON-NLS-1$
        public static final String ID                   = "ID"; //$NON-NLS-1$
        public static final String IDREF                = "IDREF"; //$NON-NLS-1$
        public static final String IDREFS               = "IDREFS"; //$NON-NLS-1$
        public static final String INT                  = "int"; //$NON-NLS-1$
        public static final String LANGUAGE             = "language"; //$NON-NLS-1$
        public static final String NAME                 = "Name"; //$NON-NLS-1$
        public static final String NCNAME               = "NCName"; //$NON-NLS-1$
        public static final String NEGATIVE_INTEGER     = "negativeInteger"; //$NON-NLS-1$
        public static final String NMTOKEN              = "NMTOKEN"; //$NON-NLS-1$
        public static final String NMTOKENS             = "NMTOKENS"; //$NON-NLS-1$
        public static final String NON_NEGATIVE_INTEGER = "nonNegativeInteger"; //$NON-NLS-1$
        public static final String NON_POSITIVE_INTEGER = "nonPositiveInteger"; //$NON-NLS-1$
        public static final String NORMALIZED_STRING    = "normalizedString"; //$NON-NLS-1$
        public static final String NOTATION             = "NOTATION"; //$NON-NLS-1$
        public static final String POSITIVE_INTEGER     = "positiveInteger"; //$NON-NLS-1$
        public static final String QNAME                = "Qname"; //$NON-NLS-1$
        public static final String TOKEN                = "unsignedByte"; //$NON-NLS-1$
        public static final String UNSIGNED_BYTE        = "unsignedByte"; //$NON-NLS-1$
        public static final String UNSIGNED_INT         = "unsignedInt"; //$NON-NLS-1$
        public static final String UNSIGNED_LONG        = "unsignedLong"; //$NON-NLS-1$
        public static final String UNSIGNED_SHORT       = "unsignedShort"; //$NON-NLS-1$
        public static final String XML_LITERAL          = "XMLLiteral"; //$NON-NLS-1$
    
/*
 * 
        Design-time	Run-time	Java
        anyURI	string	String
        base64Binary	string	String
        bigdecimal	bigdecimal	BigDecimal
        biginteger	biginteger	BigInteger
        blob	blob	Object
        boolean	boolean	Boolean
        byte	byte	Byte
        char	char	Character
        clob	clob	String
        date	date	Date
        dateTime	timestamp	Timestamp
        decimal	bigdecimal	BigDecimal
        double	double	Double
        duration	string	String
        ENTITIES	string	String
        ENTITY	string	String
        float	float	Float
        gDay	biginteger	BigInteger
        geometry	geometry	Object
        gMonth	biginteger	BigInteger
        gMonthDay	timestamp	Timestamp
        gYear	biginteger	BigInteger
        gYearMonth	timestamp	Timestamp
        hexBinary	string	String
        ID	string	String
        IDREF	string	String
        IDREFS	string	String
        int	integer	Integer
        language	string	String
        long	long	Long
        Name	string	String
        NCName	string	String
        negativeInteger	biginteger	BigInteger
        NMTOKEN	string	String
		NMTOKENS	string	String
		nonNegativeInteger	biginteger	BigInteger
		nonPositiveInteger	biginteger	BigInteger
		normalizedString	string	String
		NOTATION	string	String
		object	object	Object
		positiveInteger	biginteger	BigInteger
		Qname	string	String
		short	short	Short
		string	string	String
		time	time	Time
		timestamp	timestamp	Timestamp
		token	string	String
		unsignedByte	short	Short
		unsignedInt	long	Long
		unsignedLong	biginteger	BigInteger
		unsignedShort	integer	Integer
		varbinary	varbinary	String
		XMLLiteral	xml	String


     */
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
        SQL_TO_DTYPE_MAP.put(ANYURI, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(BASE64BINARY, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(BIGINTEGER, DefaultDataTypes.BIG_INTEGER);
        SQL_TO_DTYPE_MAP.put(DATETIME, DefaultDataTypes.TIMESTAMP);
        SQL_TO_DTYPE_MAP.put(DECIMAL, DefaultDataTypes.BIG_DECIMAL);
        SQL_TO_DTYPE_MAP.put(DURATION, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(ENTTIES, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(ENTITY, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(GDAY, DefaultDataTypes.BIG_INTEGER);
        SQL_TO_DTYPE_MAP.put(GMONTH, DefaultDataTypes.BIG_INTEGER);
        SQL_TO_DTYPE_MAP.put(GMONTYDAY, DefaultDataTypes.TIMESTAMP);
        SQL_TO_DTYPE_MAP.put(GYEAR, DefaultDataTypes.BIG_INTEGER);
        SQL_TO_DTYPE_MAP.put(GYEARMONTH, DefaultDataTypes.TIMESTAMP);
        SQL_TO_DTYPE_MAP.put(HEXBINARY, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(ID, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(IDREF, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(IDREFS, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(INT, DefaultDataTypes.INTEGER);
        SQL_TO_DTYPE_MAP.put(LANGUAGE, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(NAME, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(NCNAME, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(NEGATIVE_INTEGER, DefaultDataTypes.BIG_INTEGER);
        SQL_TO_DTYPE_MAP.put(NMTOKEN, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(NMTOKENS, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(NON_NEGATIVE_INTEGER, DefaultDataTypes.BIG_INTEGER);
        SQL_TO_DTYPE_MAP.put(NON_POSITIVE_INTEGER, DefaultDataTypes.BIG_INTEGER);
        SQL_TO_DTYPE_MAP.put(NORMALIZED_STRING, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(NOTATION, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(POSITIVE_INTEGER, DefaultDataTypes.BIG_INTEGER);
        SQL_TO_DTYPE_MAP.put(QNAME, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(TOKEN, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(UNSIGNED_BYTE, DefaultDataTypes.SHORT);
        SQL_TO_DTYPE_MAP.put(UNSIGNED_INT, DefaultDataTypes.LONG);
        SQL_TO_DTYPE_MAP.put(UNSIGNED_LONG, DefaultDataTypes.BIG_INTEGER);
        SQL_TO_DTYPE_MAP.put(UNSIGNED_SHORT, DefaultDataTypes.INTEGER);
        SQL_TO_DTYPE_MAP.put(VARBINARY, DefaultDataTypes.STRING);
        SQL_TO_DTYPE_MAP.put(XML_LITERAL, DefaultDataTypes.STRING);
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
    	DefaultDataTypes type = SQL_TO_DTYPE_MAP.get(runtimeTypeName);
    	if( type == null ) {
    		TransformationPlugin.Util.log(IStatus.WARNING, "Runtime Type: " + runtimeTypeName + 
    				" is not supported. Java type Object will be used.");
    		return DefaultDataTypes.OBJECT.getTypeClass().toString();
    	}
    	String typeString = type.getTypeClass().toString();
    	if( typeString.startsWith("class ") ) {
    		typeString = typeString.substring(6);
    	}
    	return typeString;
    }
}
