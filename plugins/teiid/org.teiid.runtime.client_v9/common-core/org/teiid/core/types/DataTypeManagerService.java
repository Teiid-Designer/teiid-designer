/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.core.types;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.teiid.core.types.basic.AnyToObjectTransform;
import org.teiid.core.types.basic.AnyToStringTransform;
import org.teiid.core.types.basic.BooleanToNumberTransform;
import org.teiid.core.types.basic.FixedNumberToBigDecimalTransform;
import org.teiid.core.types.basic.FixedNumberToBigIntegerTransform;
import org.teiid.core.types.basic.FloatingNumberToBigDecimalTransform;
import org.teiid.core.types.basic.FloatingNumberToBigIntegerTransform;
import org.teiid.core.types.basic.NullToAnyTransform;
import org.teiid.core.types.basic.NumberToBooleanTransform;
import org.teiid.core.types.basic.NumberToByteTransform;
import org.teiid.core.types.basic.NumberToDoubleTransform;
import org.teiid.core.types.basic.NumberToFloatTransform;
import org.teiid.core.types.basic.NumberToIntegerTransform;
import org.teiid.core.types.basic.NumberToLongTransform;
import org.teiid.core.types.basic.NumberToShortTransform;
import org.teiid.core.types.basic.ObjectToAnyTransform;
import org.teiid.core.util.ArgCheck;
import org.teiid.core.util.PropertiesUtils;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.designer.type.IDataTypeManagerService;
import org.teiid.query.function.FunctionLibrary;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;

/**
 *
 */
public class DataTypeManagerService implements IDataTypeManagerService {

    public static final int MAX_STRING_LENGTH = PropertiesUtils.getIntProperty(System.getProperties(), "org.teiid.maxStringLength", 4000); //$NON-NLS-1$

    private static final boolean COMPARABLE_LOBS = PropertiesUtils.getBooleanProperty(System.getProperties(), "org.teiid.comparableLobs", false); //$NON-NLS-1$

    private static final boolean COMPARABLE_OBJECT = PropertiesUtils.getBooleanProperty(System.getProperties(), "org.teiid.comparableObject", false); //$NON-NLS-1$

    public static final int MAX_VARBINARY_BYTES = Math.max(nextPowOf2(2*MAX_STRING_LENGTH), 1<<13);

    public static final int MAX_LOB_MEMORY_BYTES = Math.max(nextPowOf2(8*MAX_STRING_LENGTH), 1<<15);
    
    private static final String ARRAY_SUFFIX = "[]"; //$NON-NLS-1$

    /**
     * The ordering of this list is important since it affects the iteration
     * of functionMethods when determining the most appropriate
     * typed function in
     * {@link FunctionLibrary#determineNecessaryConversions(String, Class, org.teiid.query.sql.symbol.Expression[], Class[], boolean)}
     * Since String and Object end up with the same score, its only because
     * String appears first in the list will it be chosen above Object.
     *
     * @see <a href="https://issues.jboss.org/browse/TEIID-2876"/>
     */
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

        OBJECT ("object", DataTypeName.OBJECT, Object.class), //$NON-NLS-1$

        NULL ("null", DataTypeName.NULL, NullType.class), //$NON-NLS-1$

        BLOB ("blob", DataTypeName.BLOB, BlobType.class), //$NON-NLS-1$

        CLOB ("clob", DataTypeName.CLOB, ClobType.class), //$NON-NLS-1$

        XML ("xml", DataTypeName.XML, XMLType.class), //$NON-NLS-1$

        VARBINARY ("varbinary", DataTypeName.VARBINARY, BinaryType.class), //$NON-NLS-1$

        GEOMETRY ("geometry", DataTypeName.GEOMETRY, GeometryType.class); //$NON-NLS-1$

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

        /**
         * Is the supplied class type a LOB based data type?
         *
         * @param type
         * @return true if yes; false otherwise
         */
        public static boolean isLOB(Class<?> type) {
            return BLOB.getTypeClass().equals(type)
                    || CLOB.getTypeClass().equals(type)
                    || XML.getTypeClass().equals(type)
                    || GEOMETRY.getTypeClass().equals(type);
        }

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

    /**
     * Doubly-nested map of String srcType --> Map of String targetType -->
     * Transform
     */
    private static Map<DefaultDataTypes, Map<DefaultDataTypes, Transform>> transforms = new HashMap<DefaultDataTypes, Map<DefaultDataTypes, Transform>>(128);

    private static Map<ITeiidServerVersion, DataTypeManagerService> instances = new HashMap<ITeiidServerVersion, DataTypeManagerService>();

    private final ITeiidServerVersion teiidVersion;

    /**
     * @param teiidVersion 
     * @return the singleton instance
     */
    public static DataTypeManagerService getInstance(ITeiidServerVersion teiidVersion) {
        DataTypeManagerService instance = instances.get(teiidVersion);
        if (instance == null) {
            instance = new DataTypeManagerService(teiidVersion);
            instances.put(teiidVersion, instance);
        }

        return instance;
    }

    public static int nextPowOf2(int val) {
        int result = 1;
        while (result < val) {
            result <<= 1;
        }
        return result;
    }

    /**
     * @param teiidVersion
     */
    private DataTypeManagerService(ITeiidServerVersion teiidVersion) {
        this.teiidVersion = teiidVersion;
        loadBasicTransforms();
    }

    /**
     * Load all basic {@link Transform}s into the  This standard
     * set is always installed but may be overridden.
     */
    private void loadBasicTransforms() {
        Class<?> byteClass = DataTypeManagerService.DefaultDataTypes.BYTE.getTypeClass();
        Class<?> longClass = DataTypeManagerService.DefaultDataTypes.LONG.getTypeClass();
        Class<?> shortClass = DataTypeManagerService.DefaultDataTypes.SHORT.getTypeClass();
        Class<?> integerClass = DataTypeManagerService.DefaultDataTypes.INTEGER.getTypeClass();
        Class<?> doubleClass = DataTypeManagerService.DefaultDataTypes.DOUBLE.getTypeClass();
        Class<?> bigDecimalClass = DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL.getTypeClass();
        Class<?> bigIntegerClass = DataTypeManagerService.DefaultDataTypes.BIG_INTEGER.getTypeClass();
        Class<?> floatClass = DataTypeManagerService.DefaultDataTypes.FLOAT.getTypeClass();

        addTransform(new BooleanToNumberTransform(this, Byte.valueOf((byte)1), Byte.valueOf((byte)0)));
        addTransform(new BooleanToNumberTransform(this, Short.valueOf((short)1), Short.valueOf((short)0)));
        addTransform(new BooleanToNumberTransform(this, Integer.valueOf(1), Integer.valueOf(0)));
        addTransform(new BooleanToNumberTransform(this, Long.valueOf(1), Long.valueOf(0)));
        addTransform(new BooleanToNumberTransform(this, BigInteger.valueOf(1), BigInteger.valueOf(0)));
        addTransform(new BooleanToNumberTransform(this, Float.valueOf(1), Float.valueOf(0)));
        addTransform(new BooleanToNumberTransform(this, Double.valueOf(1), Double.valueOf(0)));
        addTransform(new BooleanToNumberTransform(this, BigDecimal.valueOf(1), BigDecimal.valueOf(0)));
        addTransform(new AnyToStringTransform(this, DefaultDataTypes.BOOLEAN.getTypeClass()));

        addTransform(new NumberToBooleanTransform(this, Byte.valueOf((byte)0)));
        addTransform(new NumberToShortTransform(this, byteClass, false));
        addTransform(new NumberToIntegerTransform(this, byteClass, false));
        addTransform(new NumberToLongTransform(this, byteClass, false, false));
        addTransform(new FixedNumberToBigIntegerTransform(this, byteClass));
        addTransform(new NumberToFloatTransform(this, byteClass, false, false));
        addTransform(new NumberToDoubleTransform(this, byteClass, false, false));
        addTransform(new FixedNumberToBigDecimalTransform(this, byteClass));
        addTransform(new AnyToStringTransform(this, byteClass));

        addTransform(new AnyToStringTransform(this, DefaultDataTypes.CHAR.getTypeClass()));

        addTransform(new NumberToBooleanTransform(this, Short.valueOf((short)0)));
        addTransform(new NumberToByteTransform(this, shortClass));
        addTransform(new NumberToIntegerTransform(this, shortClass, false));
        addTransform(new NumberToLongTransform(this, shortClass, false, false));
        addTransform(new FixedNumberToBigIntegerTransform(this, shortClass));
        addTransform(new NumberToFloatTransform(this, shortClass, false, false));
        addTransform(new NumberToDoubleTransform(this, shortClass, false, false));
        addTransform(new FixedNumberToBigDecimalTransform(this, shortClass));
        addTransform(new AnyToStringTransform(this, shortClass));

        addTransform(new NumberToBooleanTransform(this, Integer.valueOf(0)));
        addTransform(new NumberToByteTransform(this, integerClass));
        addTransform(new NumberToShortTransform(this, integerClass, true));
        addTransform(new NumberToLongTransform(this, integerClass, false, false));
        addTransform(new FixedNumberToBigIntegerTransform(this, integerClass));
        addTransform(new NumberToFloatTransform(this, integerClass, false, true)); //lossy, but not narrowing
        addTransform(new NumberToDoubleTransform(this, integerClass, false, false));
        addTransform(new FixedNumberToBigDecimalTransform(this, integerClass));
        addTransform(new AnyToStringTransform(this, integerClass));

        addTransform(new NumberToBooleanTransform(this, Long.valueOf(0)));
        addTransform(new NumberToByteTransform(this, longClass));
        addTransform(new NumberToShortTransform(this, longClass, true));
        addTransform(new NumberToIntegerTransform(this, longClass, true));
        addTransform(new FixedNumberToBigIntegerTransform(this, longClass));
        addTransform(new NumberToFloatTransform(this, longClass, false, true)); //lossy, but not narrowing
        addTransform(new NumberToDoubleTransform(this, longClass, false, true)); //lossy, but not narrowing
        addTransform(new FixedNumberToBigDecimalTransform(this, longClass));
        addTransform(new AnyToStringTransform(this, longClass));

        addTransform(new NumberToBooleanTransform(this, BigInteger.valueOf(0)));
        addTransform(new NumberToByteTransform(this, bigIntegerClass));
        addTransform(new NumberToShortTransform(this, bigIntegerClass, true));
        addTransform(new NumberToIntegerTransform(this, bigIntegerClass, true));
        addTransform(new NumberToLongTransform(this, bigIntegerClass, true, false));
        addTransform(new NumberToFloatTransform(this, bigIntegerClass, true, false));
        addTransform(new NumberToDoubleTransform(this, bigIntegerClass, true, false));
        addTransform(new org.teiid.core.types.basic.BigIntegerToBigDecimalTransform(this));
        addTransform(new AnyToStringTransform(this, bigIntegerClass));

        addTransform(new NumberToBooleanTransform(this, BigDecimal.valueOf(0)));
        addTransform(new NumberToByteTransform(this, bigDecimalClass));
        addTransform(new NumberToShortTransform(this, bigDecimalClass, true));
        addTransform(new NumberToIntegerTransform(this, bigDecimalClass, true));
        addTransform(new NumberToLongTransform(this, bigDecimalClass, true, false));
        addTransform(new org.teiid.core.types.basic.BigDecimalToBigIntegerTransform(this));
        addTransform(new NumberToFloatTransform(this, bigDecimalClass, true, false));
        addTransform(new NumberToDoubleTransform(this, bigDecimalClass, true, false));
        addTransform(new AnyToStringTransform(this, bigDecimalClass));

        addTransform(new NumberToBooleanTransform(this, Float.valueOf(0)));
        addTransform(new NumberToByteTransform(this, floatClass));
        addTransform(new NumberToShortTransform(this, floatClass, true));
        addTransform(new NumberToIntegerTransform(this, floatClass, true));
        addTransform(new NumberToLongTransform(this, floatClass, false, true)); //lossy, but not narrowing
        addTransform(new FloatingNumberToBigIntegerTransform(this, floatClass));
        addTransform(new NumberToDoubleTransform(this, floatClass, false, false));
        addTransform(new FloatingNumberToBigDecimalTransform(this, floatClass));
        addTransform(new AnyToStringTransform(this, floatClass));

        addTransform(new NumberToBooleanTransform(this, Double.valueOf(0)));
        addTransform(new NumberToByteTransform(this, doubleClass));
        addTransform(new NumberToShortTransform(this, doubleClass, true));
        addTransform(new NumberToIntegerTransform(this, doubleClass, true));
        addTransform(new NumberToLongTransform(this, doubleClass, false, true)); //lossy, but not narrowing
        addTransform(new FloatingNumberToBigIntegerTransform(this, doubleClass));
        addTransform(new NumberToFloatTransform(this, doubleClass, true, false));
        addTransform(new FloatingNumberToBigDecimalTransform(this, doubleClass));
        addTransform(new AnyToStringTransform(this, doubleClass));

        addTransform(new org.teiid.core.types.basic.DateToTimestampTransform(this));
        addTransform(new AnyToStringTransform(this, DefaultDataTypes.DATE.getTypeClass()));

        addTransform(new org.teiid.core.types.basic.TimeToTimestampTransform(this));
        addTransform(new AnyToStringTransform(this, DefaultDataTypes.TIME.getTypeClass()));

        addTransform(new org.teiid.core.types.basic.TimestampToTimeTransform(this));
        addTransform(new org.teiid.core.types.basic.TimestampToDateTransform(this));
        addTransform(new AnyToStringTransform(this, DefaultDataTypes.TIMESTAMP.getTypeClass()));

        addTransform(new org.teiid.core.types.basic.StringToBooleanTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToByteTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToShortTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToIntegerTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToLongTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToBigIntegerTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToFloatTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToDoubleTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToBigDecimalTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToTimeTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToDateTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToTimestampTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToCharacterTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToClobTransform(this));
        addTransform(new org.teiid.core.types.basic.StringToSQLXMLTransform(this));

        addTransform(new org.teiid.core.types.basic.BinaryToBlobTransform(this));

        addTransform(new org.teiid.core.types.basic.ClobToStringTransform(this));

        addTransform(new org.teiid.core.types.basic.BlobToBinaryTransform(this));

        addTransform(new org.teiid.core.types.basic.SQLXMLToStringTransform(this));

        addTransform(new AnyToStringTransform(this, DefaultDataTypes.OBJECT.getTypeClass()) {
            @Override
            public boolean isExplicit() {
                return true;
            }
        });
    }

    private void checkDataType(DefaultDataTypes dataType, String dataTypeName) {
        ArgCheck.isNotNull(dataType, Messages.getString(Messages.ERR.ERR_100_001_0001, teiidVersion, dataTypeName));
    }

    /** Utility to get Transform given srcType and targetType */
    private Transform getTransformFromMaps(DefaultDataTypes srcType, DefaultDataTypes targetType) {
        Map<DefaultDataTypes, Transform> innerMap = transforms.get(srcType);
        boolean found = false;
        if (innerMap != null) {
            Transform result = innerMap.get(targetType);
            if (result != null) {
                return result;
            }
            found = true;
        }
        if (srcType.equals(targetType)) {
            return null;
        }
        if (DefaultDataTypes.OBJECT.equals(targetType)) {
            return new AnyToObjectTransform(this, srcType.getTypeClass());
        }
        if (srcType.equals(DefaultDataTypes.NULL)) {
            return new NullToAnyTransform(this, targetType.getTypeClass());
        }
        if (srcType.equals(DefaultDataTypes.OBJECT)) {
            return new ObjectToAnyTransform(this, targetType.getTypeClass());
        }
        if (found) {
            //built-in type
            return null;
        }

        //TODO: will eventually allow integer[] to long[], etc.
        return null;
    }

    private DefaultDataTypes findDefaultDataType(String id) {
        for (DefaultDataTypes defaultDataType : DefaultDataTypes.getValues(teiidVersion)) {
            if (defaultDataType.getId().equalsIgnoreCase(id)) {
                return defaultDataType;
            }

            if(defaultDataType.hasAlias(id)) {
                return defaultDataType;
            }
        }

        return null;
    }
    
    private DefaultDataTypes findDefaultDataType(DataTypeName dataTypeName) {
        ArgCheck.isTrue(AnnotationUtils.isApplicable(dataTypeName, teiidVersion),
                                    Messages.getString(Messages.ERR.ERR_100_001_0001, teiidVersion, dataTypeName));

        for (DefaultDataTypes defaultDataType : DefaultDataTypes.getValues(teiidVersion)) {
            if (defaultDataType.getDataTypeName().equals(dataTypeName)) {
                return defaultDataType;
            }

            if(defaultDataType.hasAlias(dataTypeName.name())) {
                return defaultDataType;
            }
        }

        return null;
    }

    private DefaultDataTypes findDefaultDataType(Class<?> typeClass) {
        for (DefaultDataTypes defaultDataType : DefaultDataTypes.getValues(teiidVersion)) {
            if (defaultDataType.getTypeClass().equals(typeClass) || defaultDataType.getTypeArrayClass().equals(typeClass)) {
                return defaultDataType;
            }
        }

        return null;
    }

    public static boolean isArrayType(String name) {
        return name.endsWith(ARRAY_SUFFIX);
    }

    private String getComponentType(String name) {
        return name.substring(0, name.lastIndexOf(ARRAY_SUFFIX));
    }
    
    @Override
    public String getDefaultDataType(DataTypeName dataTypeName) {
        ArgCheck.isNotNull(dataTypeName);
        ArgCheck.isTrue(AnnotationUtils.isApplicable(dataTypeName, teiidVersion),
                        Messages.getString(Messages.ERR.ERR_100_001_0001, teiidVersion, dataTypeName));

        DefaultDataTypes dataType = findDefaultDataType(dataTypeName);
        checkDataType(dataType, dataTypeName.toString());

        return dataType.getId();
    }

    /**
     * Get the data type with the given name.
     * 
     * @param name
     *      Data type name
     *      
     * @return Data type class
     */
    public DefaultDataTypes getDataType(String name) {
        if (name == null) {
            return DefaultDataTypes.NULL;
        }

        DefaultDataTypes dataType = null;
        if (isArrayType(name)) {
            String compName = getComponentType(name);
            dataType = findDefaultDataType(compName);
        } else {
            dataType = findDefaultDataType(name);
        }

        if (dataType == null)
            dataType = DefaultDataTypes.OBJECT;

        return dataType;
    }

    @Override
    public Class<?> getDataTypeClass(String name) {
        if (name == null) {
            return DefaultDataTypes.NULL.getTypeClass();
        }

        DefaultDataTypes dataType = getDataType(name);
        if (isArrayType(name)) {
            return dataType.getTypeArrayClass();
        }

        return dataType.getTypeClass();
    }

    @Override
    public Class<?> getDefaultDataClass(DataTypeName dataTypeName) {
        ArgCheck.isNotNull(dataTypeName);
        ArgCheck.isTrue(AnnotationUtils.isApplicable(dataTypeName, teiidVersion),
                        Messages.getString(Messages.ERR.ERR_100_001_0001, teiidVersion, dataTypeName));

        DefaultDataTypes dataType = findDefaultDataType(dataTypeName);
        checkDataType(dataType, dataTypeName.toString());

        return dataType.getTypeClass();
    }

    public DefaultDataTypes getDataType(Class<?> typeClass) {
        ArgCheck.isNotNull(typeClass);

        DefaultDataTypes dataType = findDefaultDataType(typeClass);
        if (dataType != null)
            return dataType;

        return DefaultDataTypes.OBJECT;
    }

    /**
     * @param alias
     * @return the data type that is aliased by the given alias
     */
    public DefaultDataTypes getDataType(DataTypeAliases alias) {
        ArgCheck.isNotNull(alias);

        for (DefaultDataTypes defaultDataType : DefaultDataTypes.getValues(teiidVersion)) {
            if (defaultDataType.hasAlias(alias)) {
                return defaultDataType;
            }
        }

        throw new IllegalArgumentException("No data type for the alias " + alias.getId() + " for teiid version " + teiidVersion); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public String getDataTypeName(Class<?> typeClass) {
        if (typeClass == null) {
            return DefaultDataTypes.NULL.getId();
        }

        DefaultDataTypes dataType = findDefaultDataType(typeClass);
        if (dataType == null)
            dataType = DefaultDataTypes.OBJECT;
        
        if (typeClass.isArray())
            return dataType.getId() + ARRAY_SUFFIX;
        
        return dataType.getId();
    }

    @Override
    public Set<String> getAllDataTypeNames() {
        Set<String> dataTypeNames = new LinkedHashSet<String>();
        for (DefaultDataTypes defaultDataType : DefaultDataTypes.getValues(teiidVersion)) {
            dataTypeNames.add(defaultDataType.getId());
        }

        return dataTypeNames;
    }


    /**
     * Get a set of all data type classes.
     *
     * @return Set of data type classes (Class)
     */
    public Set<Class<?>> getAllDataTypeClasses() {
        Set<Class<?>> dataTypeNames = new HashSet<Class<?>>();
        for (DefaultDataTypes defaultDataType : DefaultDataTypes.getValues(teiidVersion)) {
            dataTypeNames.add(defaultDataType.getTypeClass());
        }

        return dataTypeNames;
    }

    @Override
    public Integer getDataTypeLimit(String dataType) {
        ArgCheck.isNotNull(dataType);

        for (DefaultDataTypes defaultDataType : DefaultDataTypes.getValues(teiidVersion)) {
            if (defaultDataType.getId().equals(dataType)) {
                return defaultDataType.getLimit();
            }
        }

        return -1;
    }

    @Override
    public String getDataTypeValidChars(String dataType) {
        ArgCheck.isNotNull(dataType);
        
        for (DefaultDataTypes defaultDataType : DefaultDataTypes.getValues(teiidVersion)) {
            if (defaultDataType.getId().equals(dataType)) {
                return defaultDataType.getValidChars();
            }
        }

        return null;
    }

    @Override
    public String getDataSourceType(DataSourceTypes dataSourceType) {
        if (dataSourceType == null)
            return DataSourceTypes.UNKNOWN.id();

        ArgCheck.isTrue(AnnotationUtils.isApplicable(dataSourceType, teiidVersion),
                        Messages.getString(Messages.ERR.ERR_100_001_0001, teiidVersion, dataSourceType.id()));

        return AnnotationUtils.getUpdatedName(dataSourceType, dataSourceType.id(), teiidVersion);
    }

    /**
     * Is the data type represented by the given type id comparable
     *
     * @param type
     * @return true if type is comparable, false otherwise
     */
    public boolean isNonComparable(String type) {
        DefaultDataTypes dataType = findDefaultDataType(type);
        checkDataType(dataType, type);
        return isNonComparable(dataType.getTypeClass());
    }

    /**
     * Is the data type represented by the given type class comparable
     *
     * @param type
     * @return true if type is comparable, false otherwise
     */
    public boolean isNonComparable(Class<?> type) {
        return (!COMPARABLE_OBJECT && DefaultDataTypes.OBJECT.getTypeClass().equals(type))
            || (!COMPARABLE_LOBS && DefaultDataTypes.BLOB.getTypeClass().equals(type))
            || (!COMPARABLE_LOBS && DefaultDataTypes.CLOB.getTypeClass().equals(type))
            || DefaultDataTypes.XML.getTypeClass().equals(type);
    }

    /**
     * @param sourceTypeName
     * @param targetTypeName
     * @return applicable transform for converting source to target
     */
    public Transform getTransform(String sourceTypeName, String targetTypeName) {
        if (sourceTypeName == null || targetTypeName == null) {
            throw new IllegalArgumentException(Messages.getString(Messages.ERR.ERR_003_029_0002, sourceTypeName, targetTypeName));
        }

        DefaultDataTypes sourceType = findDefaultDataType(sourceTypeName);
        DefaultDataTypes targetType = findDefaultDataType(targetTypeName);

        if (sourceType == null || targetType == null)
            return null;

        return getTransformFromMaps(sourceType, targetType);
    }

    /**
     * @param sourceType
     * @param targetType
     * @return applicable transform for converting source to target
     */
    public Transform getTransform(Class<?> sourceType, Class<?> targetType) {
        if (sourceType == null || targetType == null) {
            throw new IllegalArgumentException(Messages.getString(Messages.ERR.ERR_003_029_0002, sourceType, targetType));
        }

        DefaultDataTypes sourceDataType = findDefaultDataType(sourceType);
        DefaultDataTypes targetDataType = findDefaultDataType(targetType);

        if (sourceDataType == null || targetDataType == null)
            return null;

        return getTransformFromMaps(sourceDataType, targetDataType);
    }

    /**
     * @param sourceDataType
     * @param targetDataType
     * @return applicable transform for converting source to target
     */
    public Transform getTransform(DefaultDataTypes sourceDataType, DefaultDataTypes targetDataType) {
        return getTransformFromMaps(sourceDataType, targetDataType);
    }

    @Override
    public boolean isTransformable(String sourceTypeName, String targetTypeName) {
        if (sourceTypeName == null || targetTypeName == null)
            throw new IllegalArgumentException(Messages.getString(Messages.ERR.ERR_003_029_0002, sourceTypeName, targetTypeName));

        return getTransform(sourceTypeName, targetTypeName) != null;
    }

    /**
     * @param srcTypeClass
     * @param targetTypeClass
     * @return true if source type is transformable into target type
     */
    public boolean isTransformable(Class<?> srcTypeClass, Class<?> targetTypeClass) {
        if (srcTypeClass == null || targetTypeClass == null)
            throw new IllegalArgumentException(Messages.getString(Messages.ERR.ERR_003_029_0002, srcTypeClass, targetTypeClass));

        return getTransform(srcTypeClass, targetTypeClass) != null;
    }

    @Override
    public boolean isExplicitConversion(String sourceTypeName, String targetTypeName) {
        Transform t = getTransform(sourceTypeName, targetTypeName);
        if (t != null) {
            return t.isExplicit();
        }
        return false;
    }

    public void getImplicitConversions(String sourceTypeName, Collection<String> result) {
        if (sourceTypeName == null || result == null) {
            throw new IllegalArgumentException();
        }

        DefaultDataTypes sourceType = findDefaultDataType(sourceTypeName);
        if (sourceType != null) {
            Map<DefaultDataTypes, Transform> innerMap = transforms.get(sourceType);
            if (innerMap != null) {
                for (Entry<DefaultDataTypes, Transform> entry : innerMap.entrySet()) {
                    if (!entry.getValue().isExplicit()) {
                        result.add(entry.getKey().getId());
                    }
                }
                result.add(DefaultDataTypes.OBJECT.getId());
                return;
            }
        }

        String previous = DefaultDataTypes.OBJECT.getId();
        result.add(previous);
        while (isArrayType(sourceTypeName)) {
            previous += ARRAY_SUFFIX;
            result.add(previous);
            sourceTypeName = getComponentType(sourceTypeName);
        }
    }
   
    @Override
    public boolean isImplicitConversion(String sourceTypeName, String targetTypeName) {
        Transform t = getTransform(sourceTypeName, targetTypeName);
        if (t != null) {
            return !t.isExplicit();
        }

        if (DefaultDataTypes.NULL.getId().equals(sourceTypeName) && !DefaultDataTypes.NULL.getId().equals(targetTypeName)) {
            return true;
        }

        if (DefaultDataTypes.OBJECT.getId().equals(targetTypeName) && !DefaultDataTypes.OBJECT.getId().equals(sourceTypeName)) {
            return true;
        }

        if (isArrayType(sourceTypeName) && isArrayType(targetTypeName)) {
            return isImplicitConversion(getComponentType(sourceTypeName), getComponentType(targetTypeName));
        }

        return false;
    }

    /**
     * Add a new transform to the known transform types.
     * 
     * @param transform
     *      New transform to add
     */
    public void addTransform(Transform transform) {
        ArgCheck.isNotNull(transform);
        String sourceName = transform.getSourceTypeName();
        String targetName = transform.getTargetTypeName();

        DefaultDataTypes sourceDataType = findDefaultDataType(sourceName);
        checkDataType(sourceDataType, sourceName);
        DefaultDataTypes targetDataType = findDefaultDataType(targetName);
        checkDataType(targetDataType, targetName);

        Map<DefaultDataTypes, Transform> innerMap = transforms.get(sourceDataType);
        if (innerMap == null) {
            innerMap = new LinkedHashMap<DefaultDataTypes, Transform>();
            transforms.put(sourceDataType, innerMap);
        }
        innerMap.put(targetDataType, transform);
    }

    @SuppressWarnings("unchecked")
    public <T> T transformValue(Object value, DefaultDataTypes defaultDataType) throws Exception {
        if (value == null) {
            return (T)value;
        }
        return transformValue(value, value.getClass(), defaultDataType);
    }

    @SuppressWarnings("unchecked")
    public <T> T transformValue(Object value, Class<?> sourceType, DefaultDataTypes targetDataType) throws Exception {
        if (value == null || sourceType == targetDataType.getTypeClass() || DefaultDataTypes.OBJECT == targetDataType) {
            return (T) value;
        }

        Transform transform = null;
        DefaultDataTypes sourceDataType = findDefaultDataType(sourceType);
        if (sourceDataType != null)
            transform = getTransformFromMaps(sourceDataType, targetDataType);

        if (transform == null) {
            Object[] params = new Object[] { sourceType, targetDataType, value};
            throw new TeiidClientException(Messages.gs(Messages.TEIID.TEIID10076, params));
        }

        T result = (T) transform.transform(value);
        return result;
    }

    public <T> T transformValue(Object value, Class<T> targetClass) throws Exception {
        if (value == null) {
            return (T)value;
        }
        
        return transformValue(value, value.getClass(), getDataType(targetClass));
    }

    public boolean isDecimalAsDouble() {
        return PropertiesUtils.getBooleanProperty(System.getProperties(), "org.teiid.decimalAsDouble", false); //$NON-NLS-1$
    }
    
    /**
     * Is the given data type variable length
     * @param dataTypeName
     * 
     * @return true if the data type supports length value
     */
    @Override
	public boolean isLengthDataType(String dataTypeName) {

		if (dataTypeName.equalsIgnoreCase(DataTypeName.CHAR.name())
				|| dataTypeName.equalsIgnoreCase(DataTypeName.CLOB.name())
				|| dataTypeName.equalsIgnoreCase(DataTypeName.BLOB.name())
				|| dataTypeName.equalsIgnoreCase(DataTypeName.OBJECT.name())
				|| dataTypeName.equalsIgnoreCase(DataTypeName.STRING.name())
				|| dataTypeName.equalsIgnoreCase(DataTypeName.VARBINARY.name())
				|| dataTypeName.equalsIgnoreCase(DataTypeName.BIGINTEGER.name()) ) {
			return true;
		}
		
		return false;
	}

    /**
     * Is the given data type support precision
     * @param dataTypeName
     * 
     * @return true if the data type supports precision
     */
    @Override
	public boolean isPrecisionDataType(String dataTypeName) {

		if (dataTypeName.equalsIgnoreCase(DataTypeName.BIGDECIMAL.name())
				|| dataTypeName.equalsIgnoreCase(DataTypeName.DECIMAL.name()) ) {
			return true;
		}
		
		return false;
	}
    
    /**
     * Is the given data type support scale
     * @param dataTypeName
     * 
     * @return true if the data type supports scale
     */
    @Override
	public boolean isScaleDataType(String dataTypeName) {

		if (dataTypeName.equalsIgnoreCase(DataTypeName.BIGDECIMAL.name())
				|| dataTypeName.equalsIgnoreCase(DataTypeName.DECIMAL.name()) ) {
			return true;
		}
		
		return false;
	}

    /**
     * Convert the value to the probable runtime type.
     * @param allConversions if false only lob conversions will be used
     */
    public Object convertToRuntimeType(Object value, boolean allConversions) {
        if (value == null) {
            return null;
        }
        Class<?> c = value.getClass();
        if (findDefaultDataType(c) != null) {
            return value;
        }

        if (allConversions) {
            if (c == char[].class) {
                return new ClobType(ClobImpl.createClob((char[])value));
            }
            if (c == byte[].class) {
                return new BinaryType((byte[])value);
            }
            if (java.util.Date.class.isAssignableFrom(c)) {
                return new Timestamp(((java.util.Date)value).getTime());                
            }
            if (Object[].class.isAssignableFrom(c)) {
                return new ArrayImpl(teiidVersion, (Object[])value);
            }
        }
        if (Clob.class.isAssignableFrom(c)) {
            return new ClobType((Clob)value);
        } 
        if (Blob.class.isAssignableFrom(c)) {
            return new BlobType((Blob)value);
        } 
        if (SQLXML.class.isAssignableFrom(c)) {
            return new XMLType((SQLXML)value);
        }
        if (c == ArrayImpl.class) {
            return DefaultDataTypes.OBJECT.getTypeArrayClass();
        }
        if (c.isArray()) {
            return getDataTypeClass(getDataTypeName(c));
        }
        return value; // "object type"
    }
}
