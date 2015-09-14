/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.core.types;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLXML;
import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.designer.annotation.AnnotationUtils;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 * <p> This is a helper class used to obtain SQL type information for java types.
 * The SQL type information is obtained from java.sql.Types class. The integers and
 * strings returned by methods in this class are based on constants in java.sql.Types.
 */

public final class JDBCSQLTypeInfo {
	
	public static class TypeInfo {
		String name;
		int maxDisplaySize;
		int defaultPrecision;
		String javaClassName;
		int[] jdbcTypes;
        private final ITeiidServerVersion minTeiidVersion;
		
		public TypeInfo(ITeiidServerVersion minTeiidVersion, int maxDisplaySize, int precision, String name,
				String javaClassName, int[] jdbcTypes) {
			super();
            this.minTeiidVersion = minTeiidVersion;
			this.maxDisplaySize = maxDisplaySize;
			this.defaultPrecision = precision;
			this.name = name;
			this.javaClassName = javaClassName;
			this.jdbcTypes = jdbcTypes;
		}

        /**
         * @return minimum teiid version for this type info
         */
        public ITeiidServerVersion getMinimumTeiidVersion() {
            return minTeiidVersion;
        }

        /**
         * @param teiidVersion
         * @return false if given version is less than min required version, true otherwise
         */
        public boolean isApplicable(ITeiidServerVersion teiidVersion) {
            return ! minTeiidVersion.isGreaterThan(teiidVersion);
        }
		
	}
	
    // Prevent instantiation
    private JDBCSQLTypeInfo() {}

    public static final Integer DEFAULT_RADIX = 10;
    public static final Integer DEFAULT_SCALE = 0;

    // XML column constants
    public final static Integer XML_COLUMN_LENGTH = Integer.MAX_VALUE;

    private static Map<String, TypeInfo> NAME_TO_TYPEINFO = new LinkedHashMap<String, TypeInfo>();
    private static Map<Integer, TypeInfo> TYPE_TO_TYPEINFO = new HashMap<Integer, TypeInfo>();
    private static Map<String, TypeInfo> CLASSNAME_TO_TYPEINFO = new HashMap<String, TypeInfo>();

    static {
    	//note the order in which these are added matters.  if there are multiple sql type mappings (e.g. biginteger and bigdecimal to numeric), the latter will be the primary
    	addType(DataTypeManagerService.DefaultDataTypes.BIG_INTEGER, 20, 19, DataTypeManagerService.DefaultDataTypes.BIG_INTEGER.getId(), Types.NUMERIC);
    	addType(new String[] {DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL.getId(), "decimal"}, 22, 20, DataTypeManagerService.DefaultDataTypes.BIG_DECIMAL.getId(), Types.NUMERIC, Types.DECIMAL); //$NON-NLS-1$
    	addType(DataTypeManagerService.DefaultDataTypes.GEOMETRY, Integer.MAX_VALUE, Integer.MAX_VALUE, GeometryType.class.getName(), Types.BLOB, Types.LONGVARBINARY);
    	addType(DataTypeManagerService.DefaultDataTypes.BLOB, Integer.MAX_VALUE, Integer.MAX_VALUE, Blob.class.getName(), Types.BLOB, Types.LONGVARBINARY);
    	addType(DataTypeManagerService.DefaultDataTypes.BOOLEAN, 5, 1, DataTypeManagerService.DefaultDataTypes.BOOLEAN.getId(), Types.BIT, Types.BOOLEAN);
    	addType(new String[] {DataTypeManagerService.DefaultDataTypes.BYTE.getId(), "tinyint"}, 4, 3, DataTypeManagerService.DefaultDataTypes.BYTE.getId(), Types.TINYINT); //$NON-NLS-1$
    	addType(DataTypeManagerService.DefaultDataTypes.CHAR, 1, 1, DataTypeManagerService.DefaultDataTypes.CHAR.getId(), Types.CHAR);
    	addType(DataTypeManagerService.DefaultDataTypes.CLOB, Integer.MAX_VALUE, Integer.MAX_VALUE, Clob.class.getName(), Types.CLOB, Types.NCLOB, Types.LONGNVARCHAR, Types.LONGVARCHAR);
    	addType(DataTypeManagerService.DefaultDataTypes.DATE, 10, 10, DataTypeManagerService.DefaultDataTypes.DATE.getId(), Types.DATE);
    	addType(DataTypeManagerService.DefaultDataTypes.DOUBLE, 22, 20, DataTypeManagerService.DefaultDataTypes.DOUBLE.getId(), Types.DOUBLE, Types.FLOAT);
    	addType(new String[] {DataTypeManagerService.DefaultDataTypes.FLOAT.getId(), "real"}, 22, 20, DataTypeManagerService.DefaultDataTypes.FLOAT.getId(), Types.REAL); //$NON-NLS-1$
    	addType(DataTypeManagerService.DefaultDataTypes.INTEGER, 11, 10, DataTypeManagerService.DefaultDataTypes.INTEGER.getId(), Types.INTEGER);
    	addType(new String[] {DataTypeManagerService.DefaultDataTypes.LONG.getId(), "bigint"}, 20, 19, DataTypeManagerService.DefaultDataTypes.LONG.getId(), Types.BIGINT); //$NON-NLS-1$
    	addType(DataTypeManagerService.DefaultDataTypes.OBJECT, Integer.MAX_VALUE, Integer.MAX_VALUE, DataTypeManagerService.DefaultDataTypes.OBJECT.getId(), Types.JAVA_OBJECT);
    	addType(new String[] {DataTypeManagerService.DefaultDataTypes.SHORT.getId(), "smallint"}, 6, 5, DataTypeManagerService.DefaultDataTypes.SHORT.getId(), Types.SMALLINT); //$NON-NLS-1$
    	addType(new String[] {DataTypeManagerService.DefaultDataTypes.STRING.getId(), "varchar"}, DataTypeManagerService.MAX_STRING_LENGTH, DataTypeManagerService.MAX_STRING_LENGTH, DataTypeManagerService.DefaultDataTypes.STRING.getId(), Types.VARCHAR, Types.NVARCHAR, Types.CHAR, Types.NCHAR); //$NON-NLS-1$
    	addType(DataTypeManagerService.DefaultDataTypes.TIME, 8, 8, DataTypeManagerService.DefaultDataTypes.TIME.getId(), Types.TIME);
    	addType(DataTypeManagerService.DefaultDataTypes.TIMESTAMP, 29, 29, DataTypeManagerService.DefaultDataTypes.TIMESTAMP.getId(), Types.TIMESTAMP);
    	addType(DataTypeManagerService.DefaultDataTypes.XML, Integer.MAX_VALUE, Integer.MAX_VALUE, SQLXML.class.getName(), Types.SQLXML);
    	addType(DataTypeManagerService.DefaultDataTypes.NULL, 4, 1, null, Types.NULL);
    	addType(DataTypeManagerService.DefaultDataTypes.VARBINARY, DataTypeManagerService.MAX_LOB_MEMORY_BYTES, DataTypeManagerService.MAX_LOB_MEMORY_BYTES, byte[].class.getName(), Types.VARBINARY, Types.BINARY);
    	
    	TypeInfo typeInfo = new TypeInfo(Version.TEIID_7_7.get(), Integer.MAX_VALUE, 0, "ARRAY", Array.class.getName(), new int[Types.ARRAY]); //$NON-NLS-1$
		CLASSNAME_TO_TYPEINFO.put(Array.class.getName(), typeInfo); 
    	TYPE_TO_TYPEINFO.put(Types.ARRAY, typeInfo);
    }

    private static TypeInfo addType(DefaultDataTypes type, int maxDisplaySize, int precision, String javaClassName, int... sqlTypes) {
        ITeiidServerVersion minTeiidVersion = TeiidServerVersion.Version.TEIID_7_7.get();
        if (AnnotationUtils.hasAnnotation(type, Since.class)) {
            Since since = AnnotationUtils.getAnnotation(type, Since.class);
            minTeiidVersion = since.value().get();
        }

        return addType(minTeiidVersion, type.getId(), maxDisplaySize, precision, javaClassName, sqlTypes);
    }
    
	private static TypeInfo addType(ITeiidServerVersion minTeiidVersion, String typeName, int maxDisplaySize, int precision, String javaClassName, int... sqlTypes) {
		TypeInfo ti = new TypeInfo(minTeiidVersion, maxDisplaySize, precision, typeName, javaClassName, sqlTypes);
		NAME_TO_TYPEINFO.put(typeName, ti);
		if (javaClassName != null) {
			CLASSNAME_TO_TYPEINFO.put(javaClassName, ti);
		}
		for (int i : sqlTypes) {
			TYPE_TO_TYPEINFO.put(i, ti);
		}
		return ti;
	}
	
	private static void addType(ITeiidServerVersion minTeiidVersion, String[] typeNames, int maxDisplaySize, int precision, String javaClassName, int... sqlTypes) {
		TypeInfo ti = addType(minTeiidVersion, typeNames[0], maxDisplaySize, precision, javaClassName, sqlTypes);
		for (int i = 1; i < typeNames.length; i++) {
			NAME_TO_TYPEINFO.put(typeNames[i], ti);
		}
	}

	private static void addType(String[] typeNames, int maxDisplaySize, int precision, String javaClassName, int... sqlTypes) {
	    addType(Version.TEIID_7_7.get(), typeNames, maxDisplaySize, precision, javaClassName, sqlTypes);
	}

    /**
     * This method is used to obtain a short indicating JDBC SQL type for any object.
     * The short values that give the type info are from java.sql.Types.
     * @param Name of the teiid type.
     * @return A short value representing SQL Type for the given java type.
     */
    public static final int getSQLType(ITeiidServerVersion teiidVersion, String typeName) {

        if (typeName == null) {
            return Types.NULL;
        }
        
        TypeInfo sqlType = NAME_TO_TYPEINFO.get(typeName);
        
        if (sqlType == null) {
            if (DataTypeManagerService.isArrayType(typeName)) {
        		return Types.ARRAY;
        	}
            return Types.JAVA_OBJECT;
        }
        if (! sqlType.isApplicable(teiidVersion))
            return Types.NULL;

        return sqlType.jdbcTypes[0];
    }    

    /**
     * Get sql Type from java class type name.  This should not be called with runtime types
     * as Clob and Blob are represented by ClobType and BlobType respectively.
     * @param typeName
     * @return int
     */
    public static final int getSQLTypeFromClass(ITeiidServerVersion teiidVersion, String className) {

        if (className == null) {
            return Types.NULL;
        }
        
        TypeInfo sqlType = CLASSNAME_TO_TYPEINFO.get(className);
        
        if (sqlType == null) {
            return Types.JAVA_OBJECT;
        }
        if (! sqlType.isApplicable(teiidVersion))
            return Types.NULL;

        return sqlType.jdbcTypes[0];
    }
    
    /**
     * This method is used to obtain a the java class name given an int value
     * indicating JDBC SQL type. The int values that give the type info are from
     * java.sql.Types.
     * @param int value giving the SQL type code.
     * @return A String representing the java class name for the given SQL Type.
     */
    public static final String getJavaClassName(ITeiidServerVersion teiidVersion, int jdbcSQLType) {
    	TypeInfo typeInfo = TYPE_TO_TYPEINFO.get(jdbcSQLType);
    	
    	if (typeInfo == null) {
    		return DataTypeManagerService.DefaultDataTypes.OBJECT.getId();
    	}
    	if (! typeInfo.isApplicable(teiidVersion))
            return null;
    	
    	return typeInfo.javaClassName;
    }
    
    public static final String getTypeName(ITeiidServerVersion teiidVersion, int sqlType) {
    	TypeInfo typeInfo = TYPE_TO_TYPEINFO.get(sqlType);
    	
    	if (typeInfo == null) {
    		return DataTypeManagerService.DefaultDataTypes.OBJECT.getId();
    	}
    	if (! typeInfo.isApplicable(teiidVersion))
            return null;

    	return typeInfo.name;
    }

    public static Set<String> getMMTypeNames() {
    	return NAME_TO_TYPEINFO.keySet();
    }

	public static Integer getMaxDisplaySize(ITeiidServerVersion teiidVersion, Class<?> dataTypeClass) {
	    return getMaxDisplaySize(teiidVersion, DataTypeManagerService.getInstance(teiidVersion).getDataTypeName(dataTypeClass));
	}

	public static Integer getMaxDisplaySize(ITeiidServerVersion teiidVersion, String typeName) {
		TypeInfo ti = NAME_TO_TYPEINFO.get(typeName);
		if (ti == null) {
			return null;
		}
		if (! ti.isApplicable(teiidVersion))
            return null;

	    return ti.maxDisplaySize;
	}

	public static Integer getDefaultPrecision(ITeiidServerVersion teiidVersion, Class<?> dataTypeClass) {
	    return getDefaultPrecision(teiidVersion, DataTypeManagerService.getInstance(teiidVersion).getDataTypeName(dataTypeClass));
	}

	public static Integer getDefaultPrecision(ITeiidServerVersion teiidVersion, String typeName) {
		TypeInfo ti = NAME_TO_TYPEINFO.get(typeName);
		if (ti == null) {
			return null;
		}
		if (! ti.isApplicable(teiidVersion))
		    return null;

	    return ti.defaultPrecision;
	}

}
