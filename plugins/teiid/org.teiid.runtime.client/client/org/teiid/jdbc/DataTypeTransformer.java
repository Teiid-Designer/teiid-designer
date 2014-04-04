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

package org.teiid.jdbc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import org.teiid.core.types.BinaryType;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.util.ReaderInputStream;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.runtime.client.Messages;


/**
 * <p>This class is used to transform objects into desired data types. The static
 * method on this class are used by Metadatresults, ResultsWrapper and
 * MMCallableStatement classes.</p>
 */
final class DataTypeTransformer {

    // Prevent instantiation
    private DataTypeTransformer() {}

    private static DataTypeManagerService getDataTypeManager(ITeiidServerVersion teiidVersion) {
        DataTypeManagerService dataTypeManager = DataTypeManagerService.getInstance(teiidVersion);
        return dataTypeManager;
    }

    /**
     * Gets an object value and transforms it into a java.math.BigDecimal object.
     * @param value the object to be transformed
     * @return a BigDecimal object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final BigDecimal getBigDecimal(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, BigDecimal.class);
    }
    
    static final <T> T transform(ITeiidServerVersion teiidVersion, Object value, Class<T> targetType) throws SQLException {
    	return transform(teiidVersion, value, targetType, getRuntimeType(teiidVersion, targetType));
    }
    
    static final <T> T transform(ITeiidServerVersion teiidVersion, Object value, Class<T> targetType, Class<?> runtimeType) throws SQLException {
    	if (value == null || targetType.isAssignableFrom(value.getClass())) {
    		return targetType.cast(value);
    	}
    	if (targetType == byte[].class) {
    		if (value instanceof Blob) {
                Blob blob = (Blob)value;
                long length = blob.length();
                if (length > Integer.MAX_VALUE) {
                    throw new SQLException(Messages.getString(Messages.JDBC.DataTypeTransformer_blob_too_big));
                }
                return targetType.cast(blob.getBytes(1, (int)length));
            } else if (value instanceof String) {
            	return targetType.cast(((String)value).getBytes());
            } else if (value instanceof BinaryType) {
            	return targetType.cast(((BinaryType)value).getBytesDirect());
            }
    	} else if (targetType == String.class) {
    		if (value instanceof SQLXML) {
        		return targetType.cast(((SQLXML)value).getString());
        	} else if (value instanceof Clob) {
        		Clob c = (Clob)value;
        		long length = c.length();
        		if (length == 0) {
        			//there is a bug in SerialClob with 0 length
        			return targetType.cast(""); //$NON-NLS-1$ 
        		}
        		return targetType.cast(c.getSubString(1, length>Integer.MAX_VALUE?Integer.MAX_VALUE:(int)length));
        	}
    	}
    	try {
    		DataTypeManagerService dataTypeManager = getDataTypeManager(teiidVersion);
            return (T)dataTypeManager.transformValue(dataTypeManager.convertToRuntimeType(value, true), runtimeType);
    	} catch (Exception e) {
    		String valueStr = value.toString();
    		if (valueStr.length() > 20) {
    			valueStr = valueStr.substring(0, 20) + "..."; //$NON-NLS-1$
    		}
    		String msg = Messages.getString(Messages.JDBC.DataTypeTransformer_Err_converting, valueStr, targetType.getSimpleName());
            throw new SQLException(msg, e);
    	} 
    }
    
	static final <T> Class<?> getRuntimeType(ITeiidServerVersion teiidVersion, Class<T> type) {
		Class<?> runtimeType = type;
		if (!getDataTypeManager(teiidVersion).getAllDataTypeClasses().contains(type)) {
			if (type == Clob.class) {
				runtimeType = DataTypeManagerService.DefaultDataTypes.CLOB.getTypeClass();
			} else if (type == Blob.class) {
				runtimeType = DataTypeManagerService.DefaultDataTypes.BLOB.getTypeClass();
			} else if (type == SQLXML.class) {
				runtimeType = DataTypeManagerService.DefaultDataTypes.XML.getTypeClass();
			} else if (type == byte[].class) {
				runtimeType = DataTypeManagerService.DefaultDataTypes.VARBINARY.getTypeClass();
			} else {
				runtimeType = DataTypeManagerService.DefaultDataTypes.OBJECT.getTypeClass();
			}
		}
		return runtimeType;
	}

    /**
     * Gets an object value and transforms it into a boolean
     * @param value the object to be transformed
     * @return a Boolean object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final boolean getBoolean(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	if (value == null) {
    		return false;
    	}
    	return transform(teiidVersion, value, Boolean.class); 
    }

    /**
     * Gets an object value and transforms it into a byte
     * @param value the object to be transformed
     * @return a Byte object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final byte getByte(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	if (value == null) {
    		return 0;
    	}
    	return transform(teiidVersion, value, Byte.class); 
    }
    
    static final byte[] getBytes(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, byte[].class);
    }
    
    static final Character getCharacter(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, Character.class); 
    }

    /**
     * Gets an object value and transforms it into a java.sql.Date object.
     * @param value the object to be transformed
     * @param Calendar object to be used to construct the Date object.
     * @return a Date object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final Date getDate(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, Date.class); 
    }

    /**
     * Gets an object value and transforms it into a double
     * @param value the object to be transformed
     * @return a Double object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final double getDouble(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	if (value == null) {
    		return 0;
    	}
    	return transform(teiidVersion, value, Double.class); 
    }

    /**
     * Gets an object value and transforms it into a float
     * @param value the object to be transformed
     * @return a Float object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final float getFloat(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	if (value == null) {
    		return 0;
    	}
    	return transform(teiidVersion, value, Float.class); 
    }

    /**
     * Gets an object value and transforms it into a integer
     * @param value the object to be transformed
     * @return a Integer object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final int getInteger(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	if (value == null) {
    		return 0;
    	}
    	return transform(teiidVersion, value, Integer.class); 
    }

    /**
     * Gets an object value and transforms it into a long
     * @param value the object to be transformed
     * @return a Long object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final long getLong(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	if (value == null) {
    		return 0;
    	}
    	return transform(teiidVersion, value, Long.class); 
    }

    /**
     * Gets an object value and transforms it into a short
     * @param value the object to be transformed
     * @return a Short object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final short getShort(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	if (value == null) {
    		return 0;
    	}
    	return transform(teiidVersion, value, Short.class); 
    }

    /**
     * Gets an object value and transforms it into a java.sql.Time object.
     * @param value the object to be transformed
     * @param Calendar object to be used to construct the Time object.
     * @return a Time object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final Time getTime(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, Time.class); 
    }

    /**
     * Gets an object value and transforms it into a java.sql.Timestamp object.
     * @param value the object to be transformed
     * @param Calendar object to be used to construct the Timestamp object.
     * @return a Timestamp object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final Timestamp getTimestamp(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, Timestamp.class); 
    }
    
    static final String getString(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, String.class); 
    }

    /**
     * Gets an object value and transforms it into a java.sql.Timestamp object.
     * @param value the object to be transformed
     * @param Calendar object to be used to construct the Timestamp object.
     * @return a Timestamp object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final Blob getBlob(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, Blob.class); 
    }

    /**
     * Gets an object value and transforms it into a java.sql.Timestamp object.
     * @param value the object to be transformed
     * @param Calendar object to be used to construct the Timestamp object.
     * @return a Timestamp object
     * @throws SQLException if failed to transform to the desired datatype
     */
    static final Clob getClob(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, Clob.class); 
    }

    /**
     * Gets an object value and transforms it into a SQLXML object.
     * @param value the object to be transformed
     * @return a SQLXML object
     * @throws SQLException if failed to transform to the desired datatype
     */    
    static final SQLXML getSQLXML(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	return transform(teiidVersion, value, SQLXML.class); 
    }
    
    static final Reader getCharacterStream(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	if (value == null) {
			return null;
		}

		if (value instanceof Clob) {
			return ((Clob) value).getCharacterStream();
		}
		
		if (value instanceof SQLXML) {
			return ((SQLXML)value).getCharacterStream();
		}
		
		return new StringReader(getString(teiidVersion, value));
    }
    
    static final InputStream getAsciiStream(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
    	if (value == null) {
			return null;
		}

		if (value instanceof Clob) {
			return ((Clob) value).getAsciiStream();
		}
		
		if (value instanceof SQLXML) {
			//TODO: could check the SQLXML encoding
			return new ReaderInputStream(((SQLXML)value).getCharacterStream(), Charset.forName("ASCII")); //$NON-NLS-1$
		}
		
		return new ByteArrayInputStream(getString(teiidVersion, value).getBytes(Charset.forName("ASCII"))); //$NON-NLS-1$
    }

    static final NClob getNClob(ITeiidServerVersion teiidVersion, Object value) throws SQLException {
		final Clob clob = getClob(teiidVersion, value);
		if (clob == null) {
			return null;
		}
		if (clob instanceof NClob) {
			return (NClob)clob;
		}
		return (NClob) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] {NClob.class}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				try {
					return method.invoke(clob, args);
				} catch (InvocationTargetException e) {
					throw e.getCause();
				}
			}
		});
    }
    
    static final Array getArray(ITeiidServerVersion teiidVersion, Object obj) throws SQLException {
    	//TODO: type primitive arrays more closely
    	return transform(teiidVersion, obj, Array.class, Object[].class); 
    }

}