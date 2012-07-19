/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.jdbc.JdbcPlugin;


/**
 * ResultsImpl
 *
 * @since 8.0
 */
public class ResultsImpl implements Results {

    private final Response response;
    private final List rowList;
    private Object[] rowArray;
    private Results next;
    private Results previous;

    /**
     * Construct an instance of ResultsImpl that presents the whole Response.
     */
    public ResultsImpl( final Response response ) {
        this(response, response.getRecords());
    }

    /**
     * Construct an instance of ResultsImpl.
     */
    public ResultsImpl( final Response response,
                        final List sublist ) {
        this.response = response;
        this.rowList = sublist;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getMetadata()
     */
    @Override
	public ResultsMetadata getMetadata() {
        return response.getMetadata();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.Results#getRowCount()
     */
    @Override
	public int getRowCount() {
        return this.rowList.size();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.Results#getTotalRowCount()
     */
    @Override
	public int getTotalRowCount() {
        return this.response.getRecords().size();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.Results#getAllRows()
     */
    @Override
	public Object[] getRows() {
        if (this.rowArray == null) {
            this.rowArray = this.rowList.toArray();
        }
        return this.rowArray;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.Results#getCell(java.lang.Object, int)
     */
    @Override
	public Object getObject( Object row,
                             int columnIndex ) {
        CoreArgCheck.isNotNull(row);
        final List record = (List)row;
        if (columnIndex >= record.size()) {
            return null;
        }
        return record.get(columnIndex);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getBoolean(java.lang.Object, int)
     */
    @Override
	public String getString( Object row,
                             int columnIndex ) {
        final Object value = getObject(row, columnIndex);
        return (value == null ? null : value.toString());
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getBoolean(java.lang.Object, int)
     */
    @Override
	public boolean getBoolean( Object row,
                               int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return false;
        } else if (value instanceof Boolean) {
            return ((Boolean)value).booleanValue();
        } else if (value instanceof String) {
            return Boolean.valueOf((String)value).booleanValue();
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_boolean", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getShort(java.lang.Object, int)
     */
    @Override
	public short getShort( Object row,
                           int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return 0;
        } else if (value instanceof Integer) {
            return ((Integer)value).shortValue();
        } else if (value instanceof Short) {
            return ((Short)value).shortValue();
        } else if (value instanceof Long) {
            return ((Long)value).shortValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal)value).shortValue();
        } else if (value instanceof BigInteger) {
            return ((BigInteger)value).shortValue();
        } else if (value instanceof String) {
            try {
                return Short.parseShort((String)value);
            } catch (NumberFormatException e) {
                final Object[] params = new Object[] {value, value.getClass()};
                final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_short", params); //$NON-NLS-1$
                throw new JdbcConversionException(e, msg);
            }
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_short", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getInt(java.lang.Object, int)
     */
    @Override
	public int getInt( Object row,
                       int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return 0;
        } else if (value instanceof Integer) {
            return ((Integer)value).intValue();
        } else if (value instanceof Short) {
            return ((Short)value).intValue();
        } else if (value instanceof Long) {
            return ((Long)value).intValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal)value).intValue();
        } else if (value instanceof BigInteger) {
            return ((BigInteger)value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String)value);
            } catch (NumberFormatException e) {
                final Object[] params = new Object[] {value, value.getClass()};
                final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_int", params); //$NON-NLS-1$
                throw new JdbcConversionException(e, msg);
            }
        } else if (value instanceof Float) {
            return ((Float)value).intValue();
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_int", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getLong(java.lang.Object, int)
     */
    @Override
	public long getLong( Object row,
                         int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return 0;
        } else if (value instanceof Long) {
            return ((Long)value).longValue();
        } else if (value instanceof Integer) {
            return ((Integer)value).longValue();
        } else if (value instanceof Short) {
            return ((Short)value).longValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal)value).longValue();
        } else if (value instanceof BigInteger) {
            return ((BigInteger)value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String)value);
            } catch (NumberFormatException e) {
                final Object[] params = new Object[] {value, value.getClass()};
                final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_long", params); //$NON-NLS-1$
                throw new JdbcConversionException(e, msg);
            }
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_long", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getFloat(java.lang.Object, int)
     */
    @Override
	public float getFloat( Object row,
                           int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return 0;
        } else if (value instanceof Float) {
            return ((Float)value).floatValue();
        } else if (value instanceof Double) {
            return ((Double)value).floatValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal)value).floatValue();
        } else if (value instanceof String) {
            try {
                return Float.parseFloat((String)value);
            } catch (NumberFormatException e) {
                final Object[] params = new Object[] {value, value.getClass()};
                final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_float", params); //$NON-NLS-1$
                throw new JdbcConversionException(e, msg);
            }
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_float", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getDouble(java.lang.Object, int)
     */
    @Override
	public double getDouble( Object row,
                             int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return 0;
        } else if (value instanceof Float) {
            return ((Float)value).doubleValue();
        } else if (value instanceof Double) {
            return ((Double)value).doubleValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal)value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String)value);
            } catch (NumberFormatException e) {
                final Object[] params = new Object[] {value, value.getClass()};
                final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_double", params); //$NON-NLS-1$
                throw new JdbcConversionException(e, msg);
            }
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_double", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getByte(java.lang.Object, int)
     */
    @Override
	public byte getByte( Object row,
                         int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return 0;
        } else if (value instanceof Byte) {
            return ((Byte)value).byteValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal)value).byteValue();
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal)value).byteValue();
        } else if (value instanceof BigInteger) {
            return ((BigInteger)value).byteValue();
        } else if (value instanceof String) {
            try {
                return Byte.parseByte((String)value);
            } catch (NumberFormatException e) {
                final Object[] params = new Object[] {value, value.getClass()};
                final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_byte", params); //$NON-NLS-1$
                throw new JdbcConversionException(e, msg);
            }
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_byte", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getBytes(java.lang.Object, int)
     */
    @Override
	public byte[] getBytes( Object row,
                            int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return null;
        } else if (value instanceof BigInteger) {
            return ((BigInteger)value).toByteArray();
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_byte_array", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getDate(java.lang.Object, int)
     */
    @Override
	public Date getDate( Object row,
                         int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            return (Date)value;
        } else if (value instanceof java.util.Date) {
            final long longvalue = ((java.util.Date)value).getTime();
            return new Date(longvalue);
        } else if (value instanceof Long) {
            final long longValue = ((Long)value).longValue();
            return new Date(longValue);
        } else if (value instanceof BigDecimal) {
            final long longValue = ((BigDecimal)value).longValue();
            return new Date(longValue);
        } else if (value instanceof BigInteger) {
            final long longValue = ((BigInteger)value).longValue();
            return new Date(longValue);
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_Date", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.data.Results#getTimestamp(java.lang.Object, int)
     */
    @Override
	public Timestamp getTimestamp( Object row,
                                   int columnIndex ) throws JdbcConversionException {
        final Object value = getObject(row, columnIndex);
        if (value == null) {
            return null;
        } else if (value instanceof Timestamp) {
            return (Timestamp)value;
        } else if (value instanceof java.util.Date) {
            final long longvalue = ((java.util.Date)value).getTime();
            return new Timestamp(longvalue);
        } else if (value instanceof Long) {
            final long longValue = ((Long)value).longValue();
            return new Timestamp(longValue);
        } else if (value instanceof BigDecimal) {
            final long longValue = ((BigDecimal)value).longValue();
            return new Timestamp(longValue);
        } else if (value instanceof BigInteger) {
            final long longValue = ((BigInteger)value).longValue();
            return new Timestamp(longValue);
        }
        final Object[] params = new Object[] {value, value.getClass()};
        final String msg = JdbcPlugin.Util.getString("ResultsImpl.Unable_to_convert_to_Timestamp", params); //$NON-NLS-1$
        throw new JdbcConversionException(msg);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.Results#setCell(java.lang.Object, int, java.lang.Object)
     */
    @Override
	public void setObject( Object row,
                           int columnIndex,
                           Object newValue ) {
        if (row == null) {
            return;
        }
        final List record = (List)row;
        record.set(columnIndex, newValue);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.Results#hasNextResults()
     */
    @Override
	public boolean hasNextResults() {
        return this.next != null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.Results#getNextResults()
     */
    @Override
	public Results getNextResults() {
        return this.next;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.Results#hasPreviousResults()
     */
    @Override
	public boolean hasPreviousResults() {
        return this.previous != null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.Results#getPreviousResults()
     */
    @Override
	public Results getPreviousResults() {
        return this.previous;
    }

    public void setPrevious( final Results prev ) {
        this.previous = prev;
    }

    public void setNext( final Results next ) {
        this.next = next;
    }

}
