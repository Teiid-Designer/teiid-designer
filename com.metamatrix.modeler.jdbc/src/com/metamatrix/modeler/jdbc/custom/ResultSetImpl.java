/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.custom;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @since 4.3
 */
public class ResultSetImpl implements ResultSet {

    private List results;
    private List columnNames;
    private int currentRow = -1; // 0 based

    protected ResultSetImpl( List results,
                             List columnNames ) {
        this.results = results;
        this.columnNames = columnNames;
    }

    /**
     * @see java.sql.ResultSet#getConcurrency()
     * @since 4.3
     */
    public int getConcurrency() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getFetchDirection()
     * @since 4.3
     */
    public int getFetchDirection() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getFetchSize()
     * @since 4.3
     */
    public int getFetchSize() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getRow()
     * @since 4.3
     */
    public int getRow() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getType()
     * @since 4.3
     */
    public int getType() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#afterLast()
     * @since 4.3
     */
    public void afterLast() {
    }

    /**
     * @see java.sql.ResultSet#beforeFirst()
     * @since 4.3
     */
    public void beforeFirst() {
    }

    /**
     * @see java.sql.ResultSet#cancelRowUpdates()
     * @since 4.3
     */
    public void cancelRowUpdates() {
    }

    /**
     * @see java.sql.ResultSet#clearWarnings()
     * @since 4.3
     */
    public void clearWarnings() {
    }

    /**
     * @see java.sql.ResultSet#close()
     * @since 4.3
     */
    public void close() {
    }

    /**
     * @see java.sql.ResultSet#deleteRow()
     * @since 4.3
     */
    public void deleteRow() {
    }

    /**
     * @see java.sql.ResultSet#insertRow()
     * @since 4.3
     */
    public void insertRow() {
    }

    /**
     * @see java.sql.ResultSet#moveToCurrentRow()
     * @since 4.3
     */
    public void moveToCurrentRow() {
    }

    /**
     * @see java.sql.ResultSet#moveToInsertRow()
     * @since 4.3
     */
    public void moveToInsertRow() {
    }

    /**
     * @see java.sql.ResultSet#refreshRow()
     * @since 4.3
     */
    public void refreshRow() {
    }

    /**
     * @see java.sql.ResultSet#updateRow()
     * @since 4.3
     */
    public void updateRow() {
    }

    /**
     * @see java.sql.ResultSet#first()
     * @since 4.3
     */
    public boolean first() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isAfterLast()
     * @since 4.3
     */
    public boolean isAfterLast() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isBeforeFirst()
     * @since 4.3
     */
    public boolean isBeforeFirst() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isFirst()
     * @since 4.3
     */
    public boolean isFirst() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isLast()
     * @since 4.3
     */
    public boolean isLast() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#last()
     * @since 4.3
     */
    public boolean last() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#next()
     * @since 4.3
     */
    public boolean next() {
        if (currentRow + 1 < this.results.size()) {
            currentRow++;
            return true;
        }
        return false;
    }

    /**
     * @see java.sql.ResultSet#previous()
     * @since 4.3
     */
    public boolean previous() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#rowDeleted()
     * @since 4.3
     */
    public boolean rowDeleted() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#rowInserted()
     * @since 4.3
     */
    public boolean rowInserted() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#rowUpdated()
     * @since 4.3
     */
    public boolean rowUpdated() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#wasNull()
     * @since 4.3
     */
    public boolean wasNull() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getByte(int)
     * @since 4.3
     */
    public byte getByte( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getDouble(int)
     * @since 4.3
     */
    public double getDouble( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getFloat(int)
     * @since 4.3
     */
    public float getFloat( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getInt(int)
     * @since 4.3
     */
    public int getInt( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getLong(int)
     * @since 4.3
     */
    public long getLong( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getShort(int)
     * @since 4.3
     */
    public short getShort( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#setFetchDirection(int)
     * @since 4.3
     */
    public void setFetchDirection( int direction ) {
    }

    /**
     * @see java.sql.ResultSet#setFetchSize(int)
     * @since 4.3
     */
    public void setFetchSize( int rows ) {
    }

    /**
     * @see java.sql.ResultSet#updateNull(int)
     * @since 4.3
     */
    public void updateNull( int columnIndex ) {
    }

    /**
     * @see java.sql.ResultSet#absolute(int)
     * @since 4.3
     */
    public boolean absolute( int row ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getBoolean(int)
     * @since 4.3
     */
    public boolean getBoolean( int columnIndex ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#relative(int)
     * @since 4.3
     */
    public boolean relative( int rows ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getBytes(int)
     * @since 4.3
     */
    public byte[] getBytes( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateByte(int, byte)
     * @since 4.3
     */
    public void updateByte( int columnIndex,
                            byte x ) {
    }

    /**
     * @see java.sql.ResultSet#updateDouble(int, double)
     * @since 4.3
     */
    public void updateDouble( int columnIndex,
                              double x ) {
    }

    /**
     * @see java.sql.ResultSet#updateFloat(int, float)
     * @since 4.3
     */
    public void updateFloat( int columnIndex,
                             float x ) {
    }

    /**
     * @see java.sql.ResultSet#updateInt(int, int)
     * @since 4.3
     */
    public void updateInt( int columnIndex,
                           int x ) {
    }

    /**
     * @see java.sql.ResultSet#updateLong(int, long)
     * @since 4.3
     */
    public void updateLong( int columnIndex,
                            long x ) {
    }

    /**
     * @see java.sql.ResultSet#updateShort(int, short)
     * @since 4.3
     */
    public void updateShort( int columnIndex,
                             short x ) {
    }

    /**
     * @see java.sql.ResultSet#updateBoolean(int, boolean)
     * @since 4.3
     */
    public void updateBoolean( int columnIndex,
                               boolean x ) {
    }

    /**
     * @see java.sql.ResultSet#updateBytes(int, byte[])
     * @since 4.3
     */
    public void updateBytes( int columnIndex,
                             byte[] x ) {
    }

    /**
     * @see java.sql.ResultSet#getAsciiStream(int)
     * @since 4.3
     */
    public InputStream getAsciiStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBinaryStream(int)
     * @since 4.3
     */
    public InputStream getBinaryStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getUnicodeStream(int)
     * @since 4.3
     */
    public InputStream getUnicodeStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
     * @since 4.3
     */
    public void updateAsciiStream( int columnIndex,
                                   InputStream x,
                                   int length ) {
    }

    /**
     * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
     * @since 4.3
     */
    public void updateBinaryStream( int columnIndex,
                                    InputStream x,
                                    int length ) {
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(int)
     * @since 4.3
     */
    public Reader getCharacterStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
     * @since 4.3
     */
    public void updateCharacterStream( int columnIndex,
                                       Reader x,
                                       int length ) {
    }

    /**
     * @see java.sql.ResultSet#getObject(int)
     * @since 4.3
     */
    public Object getObject( int columnIndex ) {
        return ((List)this.results.get(this.currentRow)).get(columnIndex - 1);
    }

    /**
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
     * @since 4.3
     */
    public void updateObject( int columnIndex,
                              Object x ) {
    }

    /**
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
     * @since 4.3
     */
    public void updateObject( int columnIndex,
                              Object x,
                              int scale ) {
    }

    /**
     * @see java.sql.ResultSet#getCursorName()
     * @since 4.3
     */
    public String getCursorName() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getString(int)
     * @since 4.3
     */
    public String getString( int columnIndex ) {
        return (String)((List)this.results.get(this.currentRow)).get(columnIndex - 1);
    }

    /**
     * @see java.sql.ResultSet#updateString(int, java.lang.String)
     * @since 4.3
     */
    public void updateString( int columnIndex,
                              String x ) {
    }

    /**
     * @see java.sql.ResultSet#getByte(java.lang.String)
     * @since 4.3
     */
    public byte getByte( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getDouble(java.lang.String)
     * @since 4.3
     */
    public double getDouble( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getFloat(java.lang.String)
     * @since 4.3
     */
    public float getFloat( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#findColumn(java.lang.String)
     * @since 4.3
     */
    public int findColumn( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getInt(java.lang.String)
     * @since 4.3
     */
    public int getInt( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getLong(java.lang.String)
     * @since 4.3
     */
    public long getLong( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getShort(java.lang.String)
     * @since 4.3
     */
    public short getShort( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#updateNull(java.lang.String)
     * @since 4.3
     */
    public void updateNull( String columnName ) {
    }

    /**
     * @see java.sql.ResultSet#getBoolean(java.lang.String)
     * @since 4.3
     */
    public boolean getBoolean( String columnName ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getBytes(java.lang.String)
     * @since 4.3
     */
    public byte[] getBytes( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
     * @since 4.3
     */
    public void updateByte( String columnName,
                            byte x ) {
    }

    /**
     * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
     * @since 4.3
     */
    public void updateDouble( String columnName,
                              double x ) {
    }

    /**
     * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
     * @since 4.3
     */
    public void updateFloat( String columnName,
                             float x ) {
    }

    /**
     * @see java.sql.ResultSet#updateInt(java.lang.String, int)
     * @since 4.3
     */
    public void updateInt( String columnName,
                           int x ) {
    }

    /**
     * @see java.sql.ResultSet#updateLong(java.lang.String, long)
     * @since 4.3
     */
    public void updateLong( String columnName,
                            long x ) {
    }

    /**
     * @see java.sql.ResultSet#updateShort(java.lang.String, short)
     * @since 4.3
     */
    public void updateShort( String columnName,
                             short x ) {
    }

    /**
     * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
     * @since 4.3
     */
    public void updateBoolean( String columnName,
                               boolean x ) {
    }

    /**
     * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
     * @since 4.3
     */
    public void updateBytes( String columnName,
                             byte[] x ) {
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(int)
     * @since 4.3
     */
    public BigDecimal getBigDecimal( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(int, int)
     * @since 4.3
     */
    public BigDecimal getBigDecimal( int columnIndex,
                                     int scale ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
     * @since 4.3
     */
    public void updateBigDecimal( int columnIndex,
                                  BigDecimal x ) {
    }

    /**
     * @see java.sql.ResultSet#getURL(int)
     * @since 4.3
     */
    public URL getURL( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getArray(int)
     * @since 4.3
     */
    public Array getArray( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
     * @since 4.3
     */
    public void updateArray( int columnIndex,
                             Array x ) {
    }

    /**
     * @see java.sql.ResultSet#getBlob(int)
     * @since 4.3
     */
    public Blob getBlob( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
     * @since 4.3
     */
    public void updateBlob( int columnIndex,
                            Blob x ) {
    }

    /**
     * @see java.sql.ResultSet#getClob(int)
     * @since 4.3
     */
    public Clob getClob( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
     * @since 4.3
     */
    public void updateClob( int columnIndex,
                            Clob x ) {
    }

    /**
     * @see java.sql.ResultSet#getDate(int)
     * @since 4.3
     */
    public Date getDate( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
     * @since 4.3
     */
    public void updateDate( int columnIndex,
                            Date x ) {
    }

    /**
     * @see java.sql.ResultSet#getRef(int)
     * @since 4.3
     */
    public Ref getRef( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
     * @since 4.3
     */
    public void updateRef( int columnIndex,
                           Ref x ) {
    }

    /**
     * @see java.sql.ResultSet#getMetaData()
     * @since 4.3
     */
    public ResultSetMetaData getMetaData() {
        return (ResultSetMetaData)Proxy.newProxyInstance(this.getClass().getClassLoader(),
                                                         new Class[] {ResultSetMetaData.class},
                                                         new ExcelResultSetMetaDataHandler(this.columnNames));
    }

    /**
     * @see java.sql.ResultSet#getWarnings()
     * @since 4.3
     */
    public SQLWarning getWarnings() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getStatement()
     * @since 4.3
     */
    public Statement getStatement() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(int)
     * @since 4.3
     */
    public Time getTime( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
     * @since 4.3
     */
    public void updateTime( int columnIndex,
                            Time x ) {
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int)
     * @since 4.3
     */
    public Timestamp getTimestamp( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
     * @since 4.3
     */
    public void updateTimestamp( int columnIndex,
                                 Timestamp x ) {
    }

    /**
     * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
     * @since 4.3
     */
    public InputStream getAsciiStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
     * @since 4.3
     */
    public InputStream getBinaryStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
     * @since 4.3
     */
    public InputStream getUnicodeStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
     * @since 4.3
     */
    public void updateAsciiStream( String columnName,
                                   InputStream x,
                                   int length ) {
    }

    /**
     * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
     * @since 4.3
     */
    public void updateBinaryStream( String columnName,
                                    InputStream x,
                                    int length ) {
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
     * @since 4.3
     */
    public Reader getCharacterStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
     * @since 4.3
     */
    public void updateCharacterStream( String columnName,
                                       Reader reader,
                                       int length ) {
    }

    /**
     * @see java.sql.ResultSet#getObject(java.lang.String)
     * @since 4.3
     */
    public Object getObject( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
     * @since 4.3
     */
    public void updateObject( String columnName,
                              Object x ) {
    }

    /**
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
     * @since 4.3
     */
    public void updateObject( String columnName,
                              Object x,
                              int scale ) {
    }

    /**
     * @see java.sql.ResultSet#getString(java.lang.String)
     * @since 4.3
     */
    public String getString( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
     * @since 4.3
     */
    public void updateString( String columnName,
                              String x ) {
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
     * @since 4.3
     */
    public BigDecimal getBigDecimal( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
     * @since 4.3
     */
    public BigDecimal getBigDecimal( String columnName,
                                     int scale ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
     * @since 4.3
     */
    public void updateBigDecimal( String columnName,
                                  BigDecimal x ) {
    }

    /**
     * @see java.sql.ResultSet#getURL(java.lang.String)
     * @since 4.3
     */
    public URL getURL( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getArray(java.lang.String)
     * @since 4.3
     */
    public Array getArray( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
     * @since 4.3
     */
    public void updateArray( String columnName,
                             Array x ) {
    }

    /**
     * @see java.sql.ResultSet#getBlob(java.lang.String)
     * @since 4.3
     */
    public Blob getBlob( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
     * @since 4.3
     */
    public void updateBlob( String columnName,
                            Blob x ) {
    }

    /**
     * @see java.sql.ResultSet#getClob(java.lang.String)
     * @since 4.3
     */
    public Clob getClob( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
     * @since 4.3
     */
    public void updateClob( String columnName,
                            Clob x ) {
    }

    /**
     * @see java.sql.ResultSet#getDate(java.lang.String)
     * @since 4.3
     */
    public Date getDate( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
     * @since 4.3
     */
    public void updateDate( String columnName,
                            Date x ) {
    }

    /**
     * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
     * @since 4.3
     */
    public Date getDate( int columnIndex,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getRef(java.lang.String)
     * @since 4.3
     */
    public Ref getRef( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
     * @since 4.3
     */
    public void updateRef( String columnName,
                           Ref x ) {
    }

    /**
     * @see java.sql.ResultSet#getTime(java.lang.String)
     * @since 4.3
     */
    public Time getTime( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
     * @since 4.3
     */
    public void updateTime( String columnName,
                            Time x ) {
    }

    /**
     * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
     * @since 4.3
     */
    public Time getTime( int columnIndex,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String)
     * @since 4.3
     */
    public Timestamp getTimestamp( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
     * @since 4.3
     */
    public void updateTimestamp( String columnName,
                                 Timestamp x ) {
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
     * @since 4.3
     */
    public Timestamp getTimestamp( int columnIndex,
                                   Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
     * @since 4.3
     */
    public Date getDate( String columnName,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
     * @since 4.3
     */
    public Time getTime( String columnName,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
     * @since 4.3
     */
    public Timestamp getTimestamp( String columnName,
                                   Calendar cal ) {
        return null;
    }

    public void reset() {
        currentRow = -1;
    }

    @Override
    public int getHoldability() {

        return 0;
    }

    @Override
    public Reader getNCharacterStream( int columnIndex ) {

        return null;
    }

    @Override
    public Reader getNCharacterStream( String columnLabel ) {

        return null;
    }

    @Override
    public NClob getNClob( int columnIndex ) {

        return null;
    }

    @Override
    public NClob getNClob( String columnLabel ) {

        return null;
    }

    @Override
    public String getNString( int columnIndex ) {

        return null;
    }

    @Override
    public String getNString( String columnLabel ) {

        return null;
    }

    @Override
    public Object getObject( int columnIndex,
                             Map<String, Class<?>> map ) {

        return null;
    }

    @Override
    public Object getObject( String columnLabel,
                             Map<String, Class<?>> map ) {

        return null;
    }

    @Override
    public RowId getRowId( int columnIndex ) {

        return null;
    }

    @Override
    public RowId getRowId( String columnLabel ) {

        return null;
    }

    @Override
    public SQLXML getSQLXML( int columnIndex ) {

        return null;
    }

    @Override
    public SQLXML getSQLXML( String columnLabel ) {

        return null;
    }

    @Override
    public boolean isClosed() {

        return false;
    }

    @Override
    public void updateAsciiStream( int columnIndex,
                                   InputStream x ) {

    }

    @Override
    public void updateAsciiStream( String columnLabel,
                                   InputStream x ) {

    }

    @Override
    public void updateAsciiStream( int columnIndex,
                                   InputStream x,
                                   long length ) {

    }

    @Override
    public void updateAsciiStream( String columnLabel,
                                   InputStream x,
                                   long length ) {

    }

    @Override
    public void updateBinaryStream( int columnIndex,
                                    InputStream x ) {

    }

    @Override
    public void updateBinaryStream( String columnLabel,
                                    InputStream x ) {

    }

    @Override
    public void updateBinaryStream( int columnIndex,
                                    InputStream x,
                                    long length ) {

    }

    @Override
    public void updateBinaryStream( String columnLabel,
                                    InputStream x,
                                    long length ) {

    }

    @Override
    public void updateBlob( int columnIndex,
                            InputStream inputStream ) {

    }

    @Override
    public void updateBlob( String columnLabel,
                            InputStream inputStream ) {

    }

    @Override
    public void updateBlob( int columnIndex,
                            InputStream inputStream,
                            long length ) {

    }

    @Override
    public void updateBlob( String columnLabel,
                            InputStream inputStream,
                            long length ) {

    }

    @Override
    public void updateCharacterStream( int columnIndex,
                                       Reader x ) {

    }

    @Override
    public void updateCharacterStream( String columnLabel,
                                       Reader reader ) {

    }

    @Override
    public void updateCharacterStream( int columnIndex,
                                       Reader x,
                                       long length ) {

    }

    @Override
    public void updateCharacterStream( String columnLabel,
                                       Reader reader,
                                       long length ) {

    }

    @Override
    public void updateClob( int columnIndex,
                            Reader reader ) {

    }

    @Override
    public void updateClob( String columnLabel,
                            Reader reader ) {

    }

    @Override
    public void updateClob( int columnIndex,
                            Reader reader,
                            long length ) {

    }

    @Override
    public void updateClob( String columnLabel,
                            Reader reader,
                            long length ) {

    }

    @Override
    public void updateNCharacterStream( int columnIndex,
                                        Reader x ) {

    }

    @Override
    public void updateNCharacterStream( String columnLabel,
                                        Reader reader ) {

    }

    @Override
    public void updateNCharacterStream( int columnIndex,
                                        Reader x,
                                        long length ) {

    }

    @Override
    public void updateNCharacterStream( String columnLabel,
                                        Reader reader,
                                        long length ) {

    }

    @Override
    public void updateNClob( int columnIndex,
                             NClob clob ) {

    }

    @Override
    public void updateNClob( String columnLabel,
                             NClob clob ) {

    }

    @Override
    public void updateNClob( int columnIndex,
                             Reader reader ) {

    }

    @Override
    public void updateNClob( String columnLabel,
                             Reader reader ) {

    }

    @Override
    public void updateNClob( int columnIndex,
                             Reader reader,
                             long length ) {

    }

    @Override
    public void updateNClob( String columnLabel,
                             Reader reader,
                             long length ) {

    }

    @Override
    public void updateNString( int columnIndex,
                               String string ) {

    }

    @Override
    public void updateNString( String columnLabel,
                               String string ) {

    }

    @Override
    public void updateRowId( int columnIndex,
                             RowId x ) {

    }

    @Override
    public void updateRowId( String columnLabel,
                             RowId x ) {

    }

    @Override
    public void updateSQLXML( int columnIndex,
                              SQLXML xmlObject ) {

    }

    @Override
    public void updateSQLXML( String columnLabel,
                              SQLXML xmlObject ) {

    }

    @Override
    public boolean isWrapperFor( Class<?> iface ) {

        return false;
    }

    @Override
    public <T> T unwrap( Class<T> iface ) {

        return null;
    }
}
