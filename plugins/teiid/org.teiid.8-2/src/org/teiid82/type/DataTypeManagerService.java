/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid82.type;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.teiid.core.types.DataTypeManager;
import org.teiid.core.types.DataTypeManager.DefaultDataClasses;
import org.teiid.core.types.DataTypeManager.DefaultDataTypes;
import org.teiid.core.util.ArgCheck;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 *
 */
public class DataTypeManagerService implements IDataTypeManagerService {

    public static final String[] STRING_TYPES = new String[] {DefaultDataTypes.BIG_DECIMAL, DefaultDataTypes.BIG_INTEGER,
                DefaultDataTypes.BYTE, DefaultDataTypes.CHAR, DefaultDataTypes.DOUBLE, DefaultDataTypes.FLOAT, DefaultDataTypes.INTEGER,
                DefaultDataTypes.LONG, DefaultDataTypes.SHORT, DefaultDataTypes.STRING};
    
    private static final Map<String, Integer> TEXT_LIMITS;

    private static final Map<String, String> VALID_CHARS;
    
    // Data Source Type names supported by the server
    private static final String JDBC = "connector-jdbc"; //$NON-NLS-1$
    private static final String SALESFORCE = "teiid-connector-salesforce.rar"; //$NON-NLS-1$ 
    private static final String LDAP = "teiid-connector-ldap.rar"; //$NON-NLS-1$ 
    private static final String FILE = "teiid-connector-file.rar"; //$NON-NLS-1$ 
    private static final String JDBC_XA = "connector-jdbc-xa"; //$NON-NLS-1$
    private static final String WS = "teiid-connector-ws.rar"; //$NON-NLS-1$
    private static final String UNKNOWN = "connector-unknown"; //$NON-NLS-1$

    static {
        TEXT_LIMITS = new HashMap<String, Integer>();
        TEXT_LIMITS.put(DefaultDataTypes.BIG_DECIMAL, new Integer(30)); // BIG_DECIMAL
        TEXT_LIMITS.put(DefaultDataTypes.BIG_INTEGER, new Integer(30)); // BIG_INTEGER
        TEXT_LIMITS.put(DefaultDataTypes.BYTE, new Integer(3)); // BYTE
        TEXT_LIMITS.put(DefaultDataTypes.CHAR, new Integer(1)); // CHAR
        TEXT_LIMITS.put(DefaultDataTypes.DOUBLE, new Integer(30)); // DOUBLE
        TEXT_LIMITS.put(DefaultDataTypes.FLOAT, new Integer(30)); // FLOAT
        TEXT_LIMITS.put(DefaultDataTypes.INTEGER, new Integer(10)); // INTEGER
        TEXT_LIMITS.put(DefaultDataTypes.LONG, new Integer(19)); // LONG
        TEXT_LIMITS.put(DefaultDataTypes.SHORT, new Integer(5)); // SHORT
        TEXT_LIMITS.put(DefaultDataTypes.STRING, new Integer(256)); // STRING

        VALID_CHARS = new HashMap<String, String>();
        VALID_CHARS.put(DefaultDataTypes.BIG_DECIMAL, "0123456789-.eE"); // BIG_DECIMAL //$NON-NLS-1$
        VALID_CHARS.put(DefaultDataTypes.BIG_INTEGER, "0123456789-"); // BIG_INTEGER //$NON-NLS-1$
        VALID_CHARS.put(DefaultDataTypes.BYTE, "0123456789-"); // BYTE //$NON-NLS-1$
        VALID_CHARS.put(DefaultDataTypes.DOUBLE, "0123456789-+.eE"); // DOUBLE //$NON-NLS-1$
        VALID_CHARS.put(DefaultDataTypes.FLOAT, "0123456789-+.eE"); // FLOAT //$NON-NLS-1$
        VALID_CHARS.put(DefaultDataTypes.INTEGER, "0123456789-"); // INTEGER //$NON-NLS-1$
        VALID_CHARS.put(DefaultDataTypes.LONG, "0123456789-"); // LONG //$NON-NLS-1$
        VALID_CHARS.put(DefaultDataTypes.SHORT, "0123456789-"); // SHORT //$NON-NLS-1$
    }

    @Override
    public Class<?> getDataTypeClass(String name) {
        ArgCheck.isNotNull(name);

        Class<?> dataTypeClass = DataTypeManager.getDataTypeClass(name);
        return dataTypeClass != null ? dataTypeClass : null;
    }

    @Override
    public String getDataTypeName(Class<?> typeClass) {
        ArgCheck.isNotNull(typeClass);

        return DataTypeManager.getDataTypeName(typeClass);
    }

    @Override
    public Set<String> getAllDataTypeNames() {
        return DataTypeManager.getAllDataTypeNames();
    }
    
    private <T> T getStaticValue(DataTypeName dataTypeName, Class<?> klazz, Class<T> returnClassType) {
        for (Field field : klazz.getDeclaredFields()) {
            if (field.getName().equals(dataTypeName.name())) {
                field.setAccessible(true);
                try {
                    return (T) field.get(null);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        
        throw new RuntimeException();
    }

    @Override
    public String getDefaultDataType(DataTypeName dataTypeName) {
        ArgCheck.isNotNull(dataTypeName);

        return getStaticValue(dataTypeName, DefaultDataTypes.class, String.class);
    }
    
    @Override
    public Integer getDataTypeLimit(String dataType) {
        ArgCheck.isNotNull(dataType);
        
        Integer limit = TEXT_LIMITS.get(dataType);
        return limit != null ? limit : -1;
    }
    
    @Override
    public String getDataTypeValidChars(String dataType) {
        ArgCheck.isNotNull(dataType);
        
        return VALID_CHARS.get(dataType);
    }
    
    @Override
    public Class<?> getDefaultDataClass(DataTypeName dataTypeName) {
        ArgCheck.isNotNull(dataTypeName);

        return getStaticValue(dataTypeName, DefaultDataClasses.class, Class.class);
    }
    
    @Override
    public boolean isExplicitConversion(String srcType, String tgtType) {
        return DataTypeManager.isExplicitConversion(srcType, tgtType);
    }
    
    @Override
    public boolean isImplicitConversion(String srcType, String tgtType) {
        return DataTypeManager.isImplicitConversion(srcType, tgtType);
    }
    
    @Override
    public String getCanonicalString(String name) {
        return DataTypeManager.getCanonicalString(name);
    }

    @Override
    public boolean canTransform(String sourceTypeName, String targetTypeName) {
        return DataTypeManager.getTransform(sourceTypeName, targetTypeName) != null;
    }
    
    @Override
    public String getDataSourceType(DataSourceTypes dataSourceType) {
        switch (dataSourceType) {
            case FILE:
                return FILE;
            case JDBC:
                return JDBC;
            case JDBC_XA:
                return JDBC_XA;
            case LDAP:
                return LDAP;
            case SALESFORCE:
                return SALESFORCE;
            case WS:
                return WS;
            default:
                return UNKNOWN;
        }
    }
}
