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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import org.teiid.client.RequestMessage;
import org.teiid.client.RequestMessage.ResultsMode;
import org.teiid.client.RequestMessage.StatementType;
import org.teiid.core.util.TimestampWithTimezone;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.runtime.client.Messages;



public class CallableStatementImpl extends PreparedStatementImpl implements CallableStatement {

    // object representing parameter value
    private Object parameterValue;

    /**
     * <p>MMCallableStatement constructor that sets the procedureName, IN parameters
     * and OUT parameters on this object.
     * @param Driver's connection object which creates this object.
     * @param procedureCall string
     * @throws SQLException if there is an error parsing the call
     */
    CallableStatementImpl(ConnectionImpl connection, String procedureCall, int resultSetType, int resultSetConcurrency) throws SQLException {
        // set the connection on the super class
        super(connection, procedureCall, resultSetType, resultSetConcurrency);
        this.prepareSql = procedureCall;
    }

    @Override
    protected void resetExecutionState() throws SQLException {
    	super.resetExecutionState();
    	parameterValue = null;
    }
    
    @Override
    protected RequestMessage createRequestMessage(String[] commands,
    		boolean isBatchedCommand, ResultsMode resultsMode) {
    	RequestMessage message = super.createRequestMessage(commands, isBatchedCommand, resultsMode);
    	message.setStatementType(StatementType.CALLABLE);
    	return message;
    }
    
    public void close() throws SQLException {
        this.prepareSql = null;
        super.close();
    }

    public java.math.BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        BigDecimal bigDecimalParam = DataTypeTransformer.getBigDecimal(getTeiidVersion(), getObject(parameterIndex));

        // set scale on the param value
        return bigDecimalParam.setScale(scale);
    }
    
    public java.math.BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getBigDecimal(getTeiidVersion(), getObject(parameterIndex));
    }

    public Blob getBlob(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getBlob(getTeiidVersion(), getObject(parameterIndex));
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getBoolean(getTeiidVersion(), getObject(parameterIndex));
    }

    public byte getByte(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getByte(getTeiidVersion(), getObject(parameterIndex));
    }

    public Clob getClob(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getClob(getTeiidVersion(), getObject(parameterIndex));
    }

    public java.sql.Date getDate(int parameterIndex) throws SQLException {
        return getDate(parameterIndex, null);
    }

    public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
    	Object val = getObject(parameterIndex);
        return getDate(cal, val);
    }

	private java.sql.Date getDate(Calendar cal, Object val) throws SQLException {
		Date value = DataTypeTransformer.getDate(getTeiidVersion(), val);

        if (value == null) {
            return null;
        }
        
        if (cal != null) {
            value = TimestampWithTimezone.createDate(value, getDefaultCalendar().getTimeZone(), cal);
        }
        
        return value;
	}

    public double getDouble(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getDouble(getTeiidVersion(), getObject(parameterIndex));
    }

    public float getFloat(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getFloat(getTeiidVersion(), getObject(parameterIndex));
    }

    public int getInt(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getInteger(getTeiidVersion(), getObject(parameterIndex));
    }

    public long getLong(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getLong(getTeiidVersion(), getObject(parameterIndex));
    }

    public Object getObject(int parameterIndex) throws SQLException {
    	return getObject(Integer.valueOf(parameterIndex));
    }
    
    Object getObject(Object parameterIndex) throws SQLException {
    	Integer indexInResults = null;
    	if (parameterIndex instanceof String) {
    		indexInResults = this.outParamByName.get(parameterIndex);
    	} else {
    		indexInResults = this.outParamIndexMap.get(parameterIndex);    		
    	}
        if(indexInResults == null){
            throw new SQLException(Messages.getString(Messages.JDBC.MMCallableStatement_Param_not_found, parameterIndex));
        }
        checkStatement();
        parameterValue = resultSet.getOutputParamValue(indexInResults);
        return parameterValue;
    }

    public short getShort(int parameterIndex) throws SQLException {
        return DataTypeTransformer.getShort(getTeiidVersion(), getObject(parameterIndex));
    }

    public String getString(int parameterIndex) throws SQLException {
       return DataTypeTransformer.getString(getTeiidVersion(), getObject(parameterIndex));
    }

    public Time getTime(int parameterIndex) throws SQLException {
        return getTime(parameterIndex, null);
    }

    public Time getTime(int parameterIndex, java.util.Calendar cal) throws SQLException {
    	Object val = getObject(parameterIndex);
        return getTime(cal, val);
    }

	private Time getTime(java.util.Calendar cal, Object val)
			throws SQLException {
		Time value = DataTypeTransformer.getTime(getTeiidVersion(), val);

        if (value == null) {
            return null;
        }
        
        if (cal != null) {
            value = TimestampWithTimezone.createTime(value, getDefaultCalendar().getTimeZone(), cal);
        }
        
        return value;
	}

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return getTimestamp(parameterIndex, null);
    }

    public Timestamp getTimestamp(int parameterIndex, java.util.Calendar cal) throws SQLException {
    	Object val = getObject(parameterIndex);
        return getTimestamp(cal, val);
    }

	private Timestamp getTimestamp(java.util.Calendar cal, Object val)
			throws SQLException {
		Timestamp value = DataTypeTransformer.getTimestamp(getTeiidVersion(), val);

        if (value == null) {
            return null;
        }
        
        if (cal != null) {
            value = TimestampWithTimezone.createTimestamp(value, getDefaultCalendar().getTimeZone(), cal);
        }
        
        return value;
	}

    public void registerOutParameter(int parameterIndex, int jdbcSqlType) throws SQLException {
        // ignore - we don't care
    }

    public void registerOutParameter(int parameterIndex, int jdbcSqlType, int scale) throws SQLException {
        // ignore - we don't care
    }

    public void registerOutParameter (int parameterIndex, int jdbcSqlType, String typeName) throws SQLException {
        // ignore - we don't care
    }
   
    public boolean wasNull() throws SQLException {
        checkStatement();

        return parameterValue == null;
    }

	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getSQLXML(getTeiidVersion(), getObject(parameterIndex));
	}

	public Array getArray(int parameterIndex) throws SQLException {
		checkSupportedVersion(Version.TEIID_8_5);
		return DataTypeTransformer.getArray(getTeiidVersion(), getObject(parameterIndex));
	}

	public Array getArray(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_8_5);
		return DataTypeTransformer.getArray(getTeiidVersion(), getObject(parameterName));
	}

	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getBigDecimal(getTeiidVersion(), getObject(parameterName));
	}

	public Blob getBlob(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getBlob(getTeiidVersion(), getObject(parameterName));
	}
	
	public boolean getBoolean(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getBoolean(getTeiidVersion(), getObject(parameterName));
	}

	public byte getByte(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getByte(getTeiidVersion(), getObject(parameterName));
	}

	public byte[] getBytes(int parameterIndex) throws SQLException {
		return DataTypeTransformer.getBytes(getTeiidVersion(), getObject(parameterIndex)); 
	}

	public byte[] getBytes(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getBytes(getTeiidVersion(), getObject(parameterName));
	}

	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return DataTypeTransformer.getCharacterStream(getTeiidVersion(), getObject(parameterIndex));
	}

	public Reader getCharacterStream(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getCharacterStream(getTeiidVersion(), getObject(parameterName));
	}

	public Clob getClob(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getClob(getTeiidVersion(), getObject(parameterName));
	}

	public Date getDate(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getDate(getTeiidVersion(), getObject(parameterName));
	}

	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return getDate(cal, getObject(parameterName));
	}

	public double getDouble(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getDouble(getTeiidVersion(), getObject(parameterName));
	}

	public float getFloat(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getFloat(getTeiidVersion(), getObject(parameterName));
	}

	public int getInt(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getInteger(getTeiidVersion(), getObject(parameterName));
	}

	public long getLong(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getLong(getTeiidVersion(), getObject(parameterName));
	}

	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getCharacterStream(getTeiidVersion(), getObject(parameterIndex));
	}

	public Reader getNCharacterStream(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getCharacterStream(getTeiidVersion(), getObject(parameterName));
	}

	public NClob getNClob(int parameterIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public NClob getNClob(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getNClob(getTeiidVersion(), getObject(parameterName));
	}

	public String getNString(int parameterIndex) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getString(getTeiidVersion(), getObject(parameterIndex));
	}

	public String getNString(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getString(getTeiidVersion(), getObject(parameterName));
	}

	public Object getObject(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return getObject((Object)parameterName);
	}

	public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Ref getRef(int parameterIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public Ref getRef(String parameterName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public RowId getRowId(int parameterIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public RowId getRowId(String parameterName) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getSQLXML(getTeiidVersion(), getObject(parameterName));
	}

	public short getShort(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getShort(getTeiidVersion(), getObject(parameterName));
	}

	public String getString(String parameterName) throws SQLException {
		return DataTypeTransformer.getString(getTeiidVersion(), getObject(parameterName));
	}

	public Time getTime(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getTime(getTeiidVersion(), getObject(parameterName));
	}

	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return getTime(cal, getObject(parameterName));
	}

	public Timestamp getTimestamp(String parameterName) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return DataTypeTransformer.getTimestamp(getTeiidVersion(), getObject(parameterName));
	}

	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return getTimestamp(cal, getObject(parameterName));
	}

	public URL getURL(int parameterIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public URL getURL(String parameterName) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void registerOutParameter(String parameterName, int sqlType)	throws SQLException {
	}

	public void registerOutParameter(String parameterName, int sqlType,int scale) throws SQLException {
	}

	public void registerOutParameter(String parameterName, int sqlType,	String typeName) throws SQLException {
	}

	public void setAsciiStream(String parameterName, InputStream x)	throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setAsciiStream((Object)parameterName, x);
	}

	public void setAsciiStream(String parameterName, InputStream x, int length)	throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setAsciiStream((Object)parameterName, x);
	}

	public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setAsciiStream((Object)parameterName, x);
	}

	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject(parameterName, x);
	}

	public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
		setBlob((Object)parameterName, x);
	}

	public void setBinaryStream(String parameterName, InputStream x, int length)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setBinaryStream(parameterName, x);
	}

	public void setBinaryStream(String parameterName, InputStream x, long length)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setBinaryStream(parameterName, x);
	}

	public void setBlob(String parameterName, Blob x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject(parameterName, x);
	}

	public void setBlob(String parameterName, InputStream inputStream)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setBlob((Object)parameterName, inputStream);
	}

	public void setBlob(String parameterName, InputStream inputStream,
			long length) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setBlob((Object)parameterName, inputStream);
	}

	public void setBoolean(String parameterName, boolean x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setByte(String parameterName, byte x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setBytes(String parameterName, byte[] x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setCharacterStream(String parameterName, Reader reader)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setClob(parameterName, reader);
	}

	public void setCharacterStream(String parameterName, Reader reader,
			int length) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setClob(parameterName, reader);
	}

	public void setCharacterStream(String parameterName, Reader reader,
			long length) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setClob(parameterName, reader);
	}

	public void setClob(String parameterName, Clob x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setClob(String parameterName, Reader reader)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setClob((Object)parameterName, reader);
	}

	public void setClob(String parameterName, Reader reader, long length)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setClob((Object)parameterName, reader);
	}

	public void setDate(String parameterName, Date x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setDate(String parameterName, Date x, Calendar cal)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setDate((Object)parameterName, x, cal);
	}

	public void setDouble(String parameterName, double x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setFloat(String parameterName, float x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setInt(String parameterName, int x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setLong(String parameterName, long x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setClob((Object)parameterName, value);
	}

	public void setNCharacterStream(String parameterName, Reader value,
			long length) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setClob((Object)parameterName, value);
	}

	public void setNClob(String parameterName, NClob value) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, value);
	}

	public void setNClob(String parameterName, Reader reader)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setClob((Object)parameterName, reader);
	}

	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setClob((Object)parameterName, reader);
	}

	public void setNString(String parameterName, String value)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, null);
	}

	public void setNull(String parameterName, int sqlType) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, null);
	}		

	public void setNull(String parameterName, int sqlType, String typeName)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, null);
	}

	public void setObject(String parameterName, Object x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setObject(String parameterName, Object x, int targetSqlType)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x, targetSqlType);
	}

	public void setObject(String parameterName, Object x, int targetSqlType,
			int scale) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x, targetSqlType, scale);
	}

	public void setRowId(String parameterName, RowId x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public void setSQLXML(String parameterName, SQLXML xmlObject)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, xmlObject);
	}

	public void setShort(String parameterName, short x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setString(String parameterName, String x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setTime(String parameterName, Time x) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setTime(String parameterName, Time x, Calendar cal)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setTime((Object)parameterName, x, cal);
	}

	public void setTimestamp(String parameterName, Timestamp x)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, x);
	}

	public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setTimestamp((Object)parameterName, x, cal);
	}

	public void setURL(String parameterName, URL val) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		setObject((Object)parameterName, val);
	}

	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		throw new UnsupportedOperationException();
	}

	public <T> T getObject(String columnLabel, Class<T> type)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

}