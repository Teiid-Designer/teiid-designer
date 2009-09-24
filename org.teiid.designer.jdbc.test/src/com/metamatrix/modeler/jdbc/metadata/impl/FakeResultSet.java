/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

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
    public boolean next() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#close()
     */
    public void close() {

    }

    /**
     * @see java.sql.ResultSet#wasNull()
     */
    public boolean wasNull() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getString(int)
     */
    public String getString( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBoolean(int)
     */
    public boolean getBoolean( int columnIndex ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getByte(int)
     */
    public byte getByte( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getShort(int)
     */
    public short getShort( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getInt(int)
     */
    public int getInt( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getLong(int)
     */
    public long getLong( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getFloat(int)
     */
    public float getFloat( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getDouble(int)
     */
    public double getDouble( int columnIndex ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(int, int)
     * @deprecated
     */
    @Deprecated
    public BigDecimal getBigDecimal( int columnIndex,
                                     int scale ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBytes(int)
     */
    public byte[] getBytes( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(int)
     */
    public Date getDate( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(int)
     */
    public Time getTime( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int)
     */
    public Timestamp getTimestamp( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getAsciiStream(int)
     */
    public InputStream getAsciiStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getUnicodeStream(int)
     * @deprecated
     */
    @Deprecated
    public InputStream getUnicodeStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBinaryStream(int)
     */
    public InputStream getBinaryStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getString(java.lang.String)
     */
    public String getString( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBoolean(java.lang.String)
     */
    public boolean getBoolean( String columnName ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getByte(java.lang.String)
     */
    public byte getByte( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getShort(java.lang.String)
     */
    public short getShort( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getInt(java.lang.String)
     */
    public int getInt( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getLong(java.lang.String)
     */
    public long getLong( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getFloat(java.lang.String)
     */
    public float getFloat( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getDouble(java.lang.String)
     */
    public double getDouble( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
     * @deprecated
     */
    @Deprecated
    public BigDecimal getBigDecimal( String columnName,
                                     int scale ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBytes(java.lang.String)
     */
    public byte[] getBytes( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(java.lang.String)
     */
    public Date getDate( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(java.lang.String)
     */
    public Time getTime( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String)
     */
    public Timestamp getTimestamp( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
     */
    public InputStream getAsciiStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
     * @deprecated
     */
    @Deprecated
    public InputStream getUnicodeStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
     */
    public InputStream getBinaryStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getWarnings()
     */
    public SQLWarning getWarnings() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#clearWarnings()
     */
    public void clearWarnings() {

    }

    /**
     * @see java.sql.ResultSet#getCursorName()
     */
    public String getCursorName() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getMetaData()
     */
    public ResultSetMetaData getMetaData() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getObject(int)
     */
    public Object getObject( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getObject(java.lang.String)
     */
    public Object getObject( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#findColumn(java.lang.String)
     */
    public int findColumn( String columnName ) {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(int)
     */
    public Reader getCharacterStream( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
     */
    public Reader getCharacterStream( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(int)
     */
    public BigDecimal getBigDecimal( int columnIndex ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
     */
    public BigDecimal getBigDecimal( String columnName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#isBeforeFirst()
     */
    public boolean isBeforeFirst() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isAfterLast()
     */
    public boolean isAfterLast() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isFirst()
     */
    public boolean isFirst() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#isLast()
     */
    public boolean isLast() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#beforeFirst()
     */
    public void beforeFirst() {

    }

    /**
     * @see java.sql.ResultSet#afterLast()
     */
    public void afterLast() {

    }

    /**
     * @see java.sql.ResultSet#first()
     */
    public boolean first() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#last()
     */
    public boolean last() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#getRow()
     */
    public int getRow() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#absolute(int)
     */
    public boolean absolute( int row ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#relative(int)
     */
    public boolean relative( int rows ) {
        return false;
    }

    /**
     * @see java.sql.ResultSet#previous()
     */
    public boolean previous() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#setFetchDirection(int)
     */
    public void setFetchDirection( int direction ) {

    }

    /**
     * @see java.sql.ResultSet#getFetchDirection()
     */
    public int getFetchDirection() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#setFetchSize(int)
     */
    public void setFetchSize( int rows ) {

    }

    /**
     * @see java.sql.ResultSet#getFetchSize()
     */
    public int getFetchSize() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getType()
     */
    public int getType() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#getConcurrency()
     */
    public int getConcurrency() {
        return 0;
    }

    /**
     * @see java.sql.ResultSet#rowUpdated()
     */
    public boolean rowUpdated() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#rowInserted()
     */
    public boolean rowInserted() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#rowDeleted()
     */
    public boolean rowDeleted() {
        return false;
    }

    /**
     * @see java.sql.ResultSet#updateNull(int)
     */
    public void updateNull( int columnIndex ) {

    }

    /**
     * @see java.sql.ResultSet#updateBoolean(int, boolean)
     */
    public void updateBoolean( int columnIndex,
                               boolean x ) {

    }

    /**
     * @see java.sql.ResultSet#updateByte(int, byte)
     */
    public void updateByte( int columnIndex,
                            byte x ) {

    }

    /**
     * @see java.sql.ResultSet#updateShort(int, short)
     */
    public void updateShort( int columnIndex,
                             short x ) {

    }

    /**
     * @see java.sql.ResultSet#updateInt(int, int)
     */
    public void updateInt( int columnIndex,
                           int x ) {

    }

    /**
     * @see java.sql.ResultSet#updateLong(int, long)
     */
    public void updateLong( int columnIndex,
                            long x ) {

    }

    /**
     * @see java.sql.ResultSet#updateFloat(int, float)
     */
    public void updateFloat( int columnIndex,
                             float x ) {

    }

    /**
     * @see java.sql.ResultSet#updateDouble(int, double)
     */
    public void updateDouble( int columnIndex,
                              double x ) {

    }

    /**
     * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
     */
    public void updateBigDecimal( int columnIndex,
                                  BigDecimal x ) {

    }

    /**
     * @see java.sql.ResultSet#updateString(int, java.lang.String)
     */
    public void updateString( int columnIndex,
                              String x ) {

    }

    /**
     * @see java.sql.ResultSet#updateBytes(int, byte[])
     */
    public void updateBytes( int columnIndex,
                             byte[] x ) {

    }

    /**
     * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
     */
    public void updateDate( int columnIndex,
                            Date x ) {

    }

    /**
     * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
     */
    public void updateTime( int columnIndex,
                            Time x ) {

    }

    /**
     * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
     */
    public void updateTimestamp( int columnIndex,
                                 Timestamp x ) {

    }

    /**
     * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream, int)
     */
    public void updateAsciiStream( int columnIndex,
                                   InputStream x,
                                   int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream, int)
     */
    public void updateBinaryStream( int columnIndex,
                                    InputStream x,
                                    int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader, int)
     */
    public void updateCharacterStream( int columnIndex,
                                       Reader x,
                                       int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
     */
    public void updateObject( int columnIndex,
                              Object x,
                              int scale ) {

    }

    /**
     * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
     */
    public void updateObject( int columnIndex,
                              Object x ) {

    }

    /**
     * @see java.sql.ResultSet#updateNull(java.lang.String)
     */
    public void updateNull( String columnName ) {

    }

    /**
     * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
     */
    public void updateBoolean( String columnName,
                               boolean x ) {

    }

    /**
     * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
     */
    public void updateByte( String columnName,
                            byte x ) {

    }

    /**
     * @see java.sql.ResultSet#updateShort(java.lang.String, short)
     */
    public void updateShort( String columnName,
                             short x ) {

    }

    /**
     * @see java.sql.ResultSet#updateInt(java.lang.String, int)
     */
    public void updateInt( String columnName,
                           int x ) {

    }

    /**
     * @see java.sql.ResultSet#updateLong(java.lang.String, long)
     */
    public void updateLong( String columnName,
                            long x ) {

    }

    /**
     * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
     */
    public void updateFloat( String columnName,
                             float x ) {

    }

    /**
     * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
     */
    public void updateDouble( String columnName,
                              double x ) {

    }

    /**
     * @see java.sql.ResultSet#updateBigDecimal(java.lang.String, java.math.BigDecimal)
     */
    public void updateBigDecimal( String columnName,
                                  BigDecimal x ) {

    }

    /**
     * @see java.sql.ResultSet#updateString(java.lang.String, java.lang.String)
     */
    public void updateString( String columnName,
                              String x ) {

    }

    /**
     * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
     */
    public void updateBytes( String columnName,
                             byte[] x ) {

    }

    /**
     * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
     */
    public void updateDate( String columnName,
                            Date x ) {

    }

    /**
     * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
     */
    public void updateTime( String columnName,
                            Time x ) {

    }

    /**
     * @see java.sql.ResultSet#updateTimestamp(java.lang.String, java.sql.Timestamp)
     */
    public void updateTimestamp( String columnName,
                                 Timestamp x ) {

    }

    /**
     * @see java.sql.ResultSet#updateAsciiStream(java.lang.String, java.io.InputStream, int)
     */
    public void updateAsciiStream( String columnName,
                                   InputStream x,
                                   int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateBinaryStream(java.lang.String, java.io.InputStream, int)
     */
    public void updateBinaryStream( String columnName,
                                    InputStream x,
                                    int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateCharacterStream(java.lang.String, java.io.Reader, int)
     */
    public void updateCharacterStream( String columnName,
                                       Reader reader,
                                       int length ) {

    }

    /**
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object, int)
     */
    public void updateObject( String columnName,
                              Object x,
                              int scale ) {

    }

    /**
     * @see java.sql.ResultSet#updateObject(java.lang.String, java.lang.Object)
     */
    public void updateObject( String columnName,
                              Object x ) {

    }

    /**
     * @see java.sql.ResultSet#insertRow()
     */
    public void insertRow() {

    }

    /**
     * @see java.sql.ResultSet#updateRow()
     */
    public void updateRow() {

    }

    /**
     * @see java.sql.ResultSet#deleteRow()
     */
    public void deleteRow() {

    }

    /**
     * @see java.sql.ResultSet#refreshRow()
     */
    public void refreshRow() {

    }

    /**
     * @see java.sql.ResultSet#cancelRowUpdates()
     */
    public void cancelRowUpdates() {

    }

    /**
     * @see java.sql.ResultSet#moveToInsertRow()
     */
    public void moveToInsertRow() {

    }

    /**
     * @see java.sql.ResultSet#moveToCurrentRow()
     */
    public void moveToCurrentRow() {

    }

    /**
     * @see java.sql.ResultSet#getStatement()
     */
    public Statement getStatement() {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getRef(int)
     */
    public Ref getRef( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBlob(int)
     */
    public Blob getBlob( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getClob(int)
     */
    public Clob getClob( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getArray(int)
     */
    public Array getArray( int i ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getRef(java.lang.String)
     */
    public Ref getRef( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getBlob(java.lang.String)
     */
    public Blob getBlob( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getClob(java.lang.String)
     */
    public Clob getClob( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getArray(java.lang.String)
     */
    public Array getArray( String colName ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
     */
    public Date getDate( int columnIndex,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
     */
    public Date getDate( String columnName,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
     */
    public Time getTime( int columnIndex,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
     */
    public Time getTime( String columnName,
                         Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
     */
    public Timestamp getTimestamp( int columnIndex,
                                   Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar)
     */
    public Timestamp getTimestamp( String columnName,
                                   Calendar cal ) {
        return null;
    }

    /**
     * @see java.sql.ResultSet#getURL(int)
     */
    public URL getURL( int columnIndex ) {
        /* Implement for JDBC 3.0 */
        return null;
    }

    /**
     * @see java.sql.ResultSet#getURL(java.lang.String)
     */
    public URL getURL( String columnName ) {
        /* Implement for JDBC 3.0 */
        return null;
    }

    /**
     * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
     */
    public void updateArray( int columnIndex,
                             Array x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
     */
    public void updateArray( String columnName,
                             Array x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
     */
    public void updateBlob( int columnIndex,
                            Blob x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
     */
    public void updateBlob( String columnName,
                            Blob x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
     */
    public void updateClob( int columnIndex,
                            Clob x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
     */
    public void updateClob( String columnName,
                            Clob x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
     */
    public void updateRef( int columnIndex,
                           Ref x ) {
        /* Implement for JDBC 3.0 */

    }

    /**
     * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
     */
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

}
