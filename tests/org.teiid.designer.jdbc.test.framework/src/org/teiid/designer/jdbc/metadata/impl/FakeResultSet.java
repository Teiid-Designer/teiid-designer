/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import java.io.InputStream;
import java.io.Reader;
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
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * FakeResultSet
 */
public class FakeResultSet implements ResultSet {

    /**
     * Construct an instance of FakeResultSet.
     */
    public FakeResultSet() {
        super();
    }

    /**
     * @see java.sql.ResultSet#next()
     */
    @Override
	public boolean next() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#close()
     */
    @Override
	public void close() {

    }

    /**
     * @see java.sql.ResultSet#wasNull()
     */
    @Override
	public boolean wasNull() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getString(int)
     */
    @Override
	public String getString( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBoolean(int)
     */
    @Override
	public boolean getBoolean( int columnIndex ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getByte(int)
     */
    @Override
	public byte getByte( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getShort(int)
     */
    @Override
	public short getShort( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getInt(int)
     */
    @Override
	public int getInt( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getLong(int)
     */
    @Override
	public long getLong( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getFloat(int)
     */
    @Override
	public float getFloat( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getDouble(int)
     */
    @Override
	public double getDouble( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(int, int)
     * @deprecated
     */
    @Override
	@Deprecated
    public BigDecimal getBigDecimal( int columnIndex,
                                     int scale ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBytes(int)
     */
    @Override
	public byte[] getBytes( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(int)
     */
    @Override
	public Date getDate( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(int)
     */
    @Override
	public Time getTime( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int)
     */
    @Override
	public Timestamp getTimestamp( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getAsciiStream(int)
     */
    @Override
	public InputStream getAsciiStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getUnicodeStream(int)
     * @deprecated
     */
    @Override
	@Deprecated
    public InputStream getUnicodeStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBinaryStream(int)
     */
    @Override
	public InputStream getBinaryStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getString(java.lang.String)
     */
    @Override
	public String getString( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBoolean(java.lang.String)
     */
    @Override
	public boolean getBoolean( String columnName ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getByte(java.lang.String)
     */
    @Override
	public byte getByte( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getShort(java.lang.String)
     */
    @Override
	public short getShort( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getInt(java.lang.String)
     */
    @Override
	public int getInt( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getLong(java.lang.String)
     */
    @Override
	public long getLong( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getFloat(java.lang.String)
     */
    @Override
	public float getFloat( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getDouble(java.lang.String)
     */
    @Override
	public double getDouble( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
     * @deprecated
     */
    @Override
	@Deprecated
    public BigDecimal getBigDecimal( String columnName,
                                     int scale ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBytes(java.lang.String)
     */
    @Override
	public byte[] getBytes( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(java.lang.String)
     */
    @Override
	public Date getDate( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(java.lang.String)
     */
    @Override
	public Time getTime( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String)
     */
    @Override
	public Timestamp getTimestamp( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
     */
    @Override
	public InputStream getAsciiStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
     * @deprecated
     */
    @Override
	@Deprecated
    public InputStream getUnicodeStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
     */
    @Override
	public InputStream getBinaryStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getWarnings()
     */
    @Override
	public SQLWarning getWarnings() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#clearWarnings()
     */
    @Override
	public void clearWarnings() {

    }

    /**
     * @see java.sql.ResultSet#getCursorName()
     */
    @Override
	public String getCursorName() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getMetaData()
     */
    @Override
	public ResultSetMetaData getMetaData() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getObject(int)
     */
    @Override
	public Object getObject( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getObject(java.lang.String)
     */
    @Override
	public Object getObject( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#findColumn(java.lang.String)
     */
    @Override
	public int findColumn( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(int)
     */
    @Override
	public Reader getCharacterStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
     */
    @Override
	public Reader getCharacterStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(int)
     */
    @Override
	public BigDecimal getBigDecimal( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
     */
    @Override
	public BigDecimal getBigDecimal( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#isBeforeFirst()
     */
    @Override
	public boolean isBeforeFirst() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isAfterLast()
     */
    @Override
	public boolean isAfterLast() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isFirst()
     */
    @Override
	public boolean isFirst() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isLast()
     */
    @Override
	public boolean isLast() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#beforeFirst()
     */
    @Override
	public void beforeFirst() {

    }

    /**
     * @see java.sql.ResultSet#afterLast()
     */
    @Override
	public void afterLast() {

    }

    /**
     * @see java.sql.ResultSet#first()
     */
    @Override
	public boolean first() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#last()
     */
    @Override
	public boolean last() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getRow()
     */
    @Override
	public int getRow() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#absolute(int)
     */
    @Override
	public boolean absolute( int row ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#relative(int)
     */
    @Override
	public boolean relative( int rows ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#previous()
     */
    @Override
	public boolean previous() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#setFetchDirection(int)
     */
    @Override
	public void setFetchDirection( int direction ) {

    }

    /**
     * @see java.sql.ResultSet#getFetchDirection()
     */
    @Override
	public int getFetchDirection() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#setFetchSize(int)
     */
    @Override
	public void setFetchSize( int rows ) {

    }

    /**
     * @see java.sql.ResultSet#getFetchSize()
     */
    @Override
	public int getFetchSize() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getType()
     */
    @Override
	public int getType() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getConcurrency()
     */
    @Override
	public int getConcurrency() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#rowUpdated()
     */
    @Override
	public boolean rowUpdated() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#rowInserted()
     */
    @Override
	public boolean rowInserted() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#rowDeleted()
     */
    @Override
	public boolean rowDeleted() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#updateNull(int)
     */
    @Override
	public void updateNull( int columnIndex ) {

    }

    /**
     * @see java.sql.ResultSet#updateBoolean(int, boolean)
     */
    @Override
	public void updateBoolean( int columnIndex,
                               boolean x ) {

    }

    /**
     * @see java.sql.ResultSet#updateByte(int, byte)
     */
    @Override
	public void updateByte( int columnIndex,
                            byte x ) {

    }

    /**
     * @see java.sql.ResultSet#updateShort(int, short)
     */
    @Override
	public void updateShort( int columnIndex,
                             short x ) {

    }

    /**
     * @see java.sql.ResultSet#updateInt(int, int)
     */
    @Override
	public void updateInt( int columnIndex,
                           int x ) {

    }

    /**
     * @see java.sql.ResultSet#updateLong(int, long)
     */
    @Override
	public void updateLong( int columnIndex,
                            long x ) {

    }

    /**
     * @see java.sql.ResultSet#updateFloat(int, float)
     */
    @Override
	public void updateFloat( int columnIndex,
                             float x ) {

    }

    /**
     * @see java.sql.ResultSet#updateDouble(int, double)
     */
    @Override
	public void updateDouble( int columnIndex,
                              double x ) {

    }

    /**
     * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
     */
    @Override
	public void updateBigDecimal( int columnIndex,
                                  BigDecimal x ) {

    }

    /**
     * @see java.sql.ResultSet#updateString(int, java.lang.String)
     */
    @Override
	public void updateString( int columnIndex,
                              String x ) {

    }

    /**
     * @see java.sql.ResultSet#updateBytes(int, byte[])
     */
    @Override
	public void updateBytes( int columnIndex,
                             byte[] x ) {

    }

    /**
     * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
     */
    @Override
	public void updateDate( int columnIndex,
                            Date x ) {

    }

    /**
     * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
     */
    @Override
	public void updateTime( int columnIndex,
                            Time x ) {

    }

    /**
     * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
     */
    @Override
	public void updateTimestamp( int columnIndex,
                                 Timestamp x ) {

    }

    /**
     * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
     */
    @Override
	public void updateAsciiStream( int columnIndex,
                                   InputStream x,
                                   int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
     */
    @Override
	public void updateBinaryStream( int columnIndex,
                                    InputStream x,
                                    int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
     */
    @Override
	public void updateCharacterStream( int columnIndex,
                                       Reader x,
                                       int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
     */
    @Override
	public void updateObject( int columnIndex,
                              Object x,
                              int scale ) {

    }

    /**
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
     */
    @Override
	public void updateObject( int columnIndex,
                              Object x ) {

    }

    /**
     * @see java.sql.ResultSet#updateNull(java.lang.String)
     */
    @Override
	public void updateNull( String columnName ) {

    }

    /**
     * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
     */
    @Override
	public void updateBoolean( String columnName,
                               boolean x ) {

    }

    /**
     * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
     */
    @Override
	public void updateByte( String columnName,
                            byte x ) {

    }

    /**
     * @see java.sql.ResultSet#updateShort(java.lang.String, short)
     */
    @Override
	public void updateShort( String columnName,
                             short x ) {

    }

    /**
     * @see java.sql.ResultSet#updateInt(java.lang.String, int)
     */
    @Override
	public void updateInt( String columnName,
                           int x ) {

    }

    /**
     * @see java.sql.ResultSet#updateLong(java.lang.String, long)
     */
    @Override
	public void updateLong( String columnName,
                            long x ) {

    }

    /**
     * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
     */
    @Override
	public void updateFloat( String columnName,
                             float x ) {

    }

    /**
     * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
     */
    @Override
	public void updateDouble( String columnName,
                              double x ) {

    }

    /**
     * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
     */
    @Override
	public void updateBigDecimal( String columnName,
                                  BigDecimal x ) {

    }

    /**
     * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
     */
    @Override
	public void updateString( String columnName,
                              String x ) {

    }

    /**
     * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
     */
    @Override
	public void updateBytes( String columnName,
                             byte[] x ) {

    }

    /**
     * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
     */
    @Override
	public void updateDate( String columnName,
                            Date x ) {

    }

    /**
     * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
     */
    @Override
	public void updateTime( String columnName,
                            Time x ) {

    }

    /**
     * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
     */
    @Override
	public void updateTimestamp( String columnName,
                                 Timestamp x ) {

    }

    /**
     * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
     */
    @Override
	public void updateAsciiStream( String columnName,
                                   InputStream x,
                                   int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
     */
    @Override
	public void updateBinaryStream( String columnName,
                                    InputStream x,
                                    int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
     */
    @Override
	public void updateCharacterStream( String columnName,
                                       Reader reader,
                                       int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
     */
    @Override
	public void updateObject( String columnName,
                              Object x,
                              int scale ) {

    }

    /**
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
     */
    @Override
	public void updateObject( String columnName,
                              Object x ) {

    }

    /**
     * @see java.sql.ResultSet#insertRow()
     */
    @Override
	public void insertRow() {

    }

    /**
     * @see java.sql.ResultSet#updateRow()
     */
    @Override
	public void updateRow() {

    }

    /**
     * @see java.sql.ResultSet#deleteRow()
     */
    @Override
	public void deleteRow() {

    }

    /**
     * @see java.sql.ResultSet#refreshRow()
     */
    @Override
	public void refreshRow() {

    }

    /**
     * @see java.sql.ResultSet#cancelRowUpdates()
     */
    @Override
	public void cancelRowUpdates() {

    }

    /**
     * @see java.sql.ResultSet#moveToInsertRow()
     */
    @Override
	public void moveToInsertRow() {

    }

    /**
     * @see java.sql.ResultSet#moveToCurrentRow()
     */
    @Override
	public void moveToCurrentRow() {

    }

    /**
     * @see java.sql.ResultSet#getStatement()
     */
    @Override
	public Statement getStatement() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getRef(int)
     */
    @Override
	public Ref getRef( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBlob(int)
     */
    @Override
	public Blob getBlob( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getClob(int)
     */
    @Override
	public Clob getClob( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getArray(int)
     */
    @Override
	public Array getArray( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getRef(java.lang.String)
     */
    @Override
	public Ref getRef( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBlob(java.lang.String)
     */
    @Override
	public Blob getBlob( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getClob(java.lang.String)
     */
    @Override
	public Clob getClob( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getArray(java.lang.String)
     */
    @Override
	public Array getArray( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
     */
    @Override
	public Date getDate( int columnIndex,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
     */
    @Override
	public Date getDate( String columnName,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
     */
    @Override
	public Time getTime( int columnIndex,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
     */
    @Override
	public Time getTime( String columnName,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
     */
    @Override
	public Timestamp getTimestamp( int columnIndex,
                                   Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
     */
    @Override
	public Timestamp getTimestamp( String columnName,
                                   Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getURL(int)
     */
    @Override
	public URL getURL( int columnIndex ) {
        /* Implement for JDBC 3.0 */
        return null;
    }

    /**
     * @see java.sql.ResultSet#getURL(java.lang.String)
     */
    @Override
	public URL getURL( String columnName ) {
        /* Implement for JDBC 3.0 */
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
     */
    @Override
	public void updateArray( int columnIndex,
                             Array x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
     */
    @Override
	public void updateArray( String columnName,
                             Array x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
     */
    @Override
	public void updateBlob( int columnIndex,
                            Blob x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
     */
    @Override
	public void updateBlob( String columnName,
                            Blob x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
     */
    @Override
	public void updateClob( int columnIndex,
                            Clob x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
     */
    @Override
	public void updateClob( String columnName,
                            Clob x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
     */
    @Override
	public void updateRef( int columnIndex,
                           Ref x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
     */
    @Override
	public void updateRef( String columnName,
                           Ref x ) {
        /* Implement for JDBC 3.0 */

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
    
    public <T> T getObject(int columnIndex,
                           Class<T> type) throws SQLException {
        return null;
    }
    
    public <T> T getObject(String columnLabel,
                           Class<T> type) throws SQLException {
        return null;
    }

}
