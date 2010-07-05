/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.sql.schema;

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

import org.eclipse.datatools.connectivity.sqm.loader.JDBCCatalogLoader;

/**
 * This class is a workaround for https://jira.jboss.org/browse/TEIID-1141 Its
 * only function to to return the name of the VDB as the Catalog name. This
 * class should be removed when the fix for that defect is pulled into Designer.
 * https://jira.jboss.org/browse/
 * 
 */
public class CatalogLoader extends JDBCCatalogLoader {

	/**
	 * @param catalogObject
	 */
	public CatalogLoader() {
		super(null);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.datatools.connectivity.sqm.loader.JDBCCatalogLoader#createResultSet()
	 */
	@Override
	protected ResultSet createResultSet() throws SQLException {
		BugFixResultSet result = new BugFixResultSet();
		ResultSet emptyResult = super.createResultSet();
		result.setMetadata(emptyResult.getMetaData());
		return result;
	}

	public class BugFixResultSet implements ResultSet {

		private ResultSetMetaData metadata;
		private boolean notCalled = true;

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#absolute(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean absolute(int row) throws SQLException {
			return false;
		}

		/**
		 * @param metaData
		 */
		public void setMetadata(ResultSetMetaData metaData) {
			this.metadata = metaData;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#afterLast()
		 */
		@SuppressWarnings("unused")
		@Override
		public void afterLast() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#beforeFirst()
		 */
		@SuppressWarnings("unused")
		@Override
		public void beforeFirst() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#cancelRowUpdates()
		 */
		@SuppressWarnings("unused")
		@Override
		public void cancelRowUpdates() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#clearWarnings()
		 */
		@SuppressWarnings("unused")
		@Override
		public void clearWarnings() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#close()
		 */
		@SuppressWarnings("unused")
		@Override
		public void close() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#deleteRow()
		 */
		@SuppressWarnings("unused")
		@Override
		public void deleteRow() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#findColumn(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public int findColumn(String columnLabel) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#first()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean first() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getArray(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Array getArray(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getArray(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Array getArray(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getAsciiStream(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public InputStream getAsciiStream(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getAsciiStream(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public InputStream getAsciiStream(String columnLabel)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBigDecimal(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBigDecimal(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBigDecimal(int, int)
		 */
		@SuppressWarnings("unused")
		@Override
		public BigDecimal getBigDecimal(int columnIndex, int scale)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBigDecimal(java.lang.String, int)
		 */
		@SuppressWarnings("unused")
		@Override
		public BigDecimal getBigDecimal(String columnLabel, int scale)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBinaryStream(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public InputStream getBinaryStream(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBinaryStream(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public InputStream getBinaryStream(String columnLabel)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBlob(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Blob getBlob(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBlob(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Blob getBlob(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBoolean(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean getBoolean(int columnIndex) throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBoolean(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean getBoolean(String columnLabel) throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getByte(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public byte getByte(int columnIndex) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getByte(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public byte getByte(String columnLabel) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBytes(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public byte[] getBytes(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getBytes(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public byte[] getBytes(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getCharacterStream(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Reader getCharacterStream(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getCharacterStream(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Reader getCharacterStream(String columnLabel)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getClob(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Clob getClob(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getClob(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Clob getClob(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getConcurrency()
		 */
		@SuppressWarnings("unused")
		@Override
		public int getConcurrency() throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getCursorName()
		 */
		@SuppressWarnings("unused")
		@Override
		public String getCursorName() throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getDate(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Date getDate(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getDate(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Date getDate(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getDate(int, java.util.Calendar)
		 */
		@SuppressWarnings("unused")
		@Override
		public Date getDate(int columnIndex, Calendar cal) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar)
		 */
		@SuppressWarnings("unused")
		@Override
		public Date getDate(String columnLabel, Calendar cal)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getDouble(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public double getDouble(int columnIndex) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getDouble(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public double getDouble(String columnLabel) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getFetchDirection()
		 */
		@SuppressWarnings("unused")
		@Override
		public int getFetchDirection() throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getFetchSize()
		 */
		@SuppressWarnings("unused")
		@Override
		public int getFetchSize() throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getFloat(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public float getFloat(int columnIndex) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getFloat(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public float getFloat(String columnLabel) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getHoldability()
		 */
		@SuppressWarnings("unused")
		@Override
		public int getHoldability() throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getInt(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public int getInt(int columnIndex) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getInt(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public int getInt(String columnLabel) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getLong(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public long getLong(int columnIndex) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getLong(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public long getLong(String columnLabel) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getMetaData()
		 */
		@SuppressWarnings("unused")
		@Override
		public ResultSetMetaData getMetaData() throws SQLException {
			return this.metadata;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getNCharacterStream(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Reader getNCharacterStream(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getNCharacterStream(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Reader getNCharacterStream(String columnLabel)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getNClob(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public NClob getNClob(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getNClob(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public NClob getNClob(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getNString(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public String getNString(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getNString(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public String getNString(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getObject(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Object getObject(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getObject(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Object getObject(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getObject(int, java.util.Map)
		 */
		@SuppressWarnings("unused")
		@Override
		public Object getObject(int columnIndex, Map<String, Class<?>> map)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map)
		 */
		@SuppressWarnings("unused")
		@Override
		public Object getObject(String columnLabel, Map<String, Class<?>> map)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getRef(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Ref getRef(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getRef(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Ref getRef(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getRow()
		 */
		@SuppressWarnings("unused")
		@Override
		public int getRow() throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getRowId(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public RowId getRowId(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getRowId(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public RowId getRowId(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getSQLXML(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public SQLXML getSQLXML(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getSQLXML(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public SQLXML getSQLXML(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getShort(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public short getShort(int columnIndex) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getShort(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public short getShort(String columnLabel) throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getStatement()
		 */
		@SuppressWarnings("unused")
		@Override
		public Statement getStatement() throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getString(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public String getString(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getString(java.lang.String)
		 */
		@Override
		public String getString(String columnLabel) throws SQLException {
			if (!columnLabel.equals(JDBCCatalogLoader.COLUMN_TABLE_CAT)) {
				throw new SQLException("columnLabel must be " //$NON-NLS-1$
						+ JDBCCatalogLoader.COLUMN_TABLE_CAT);
			}

			return getCatalogObject().getCatalogDatabase().getName();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getTime(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Time getTime(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getTime(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Time getTime(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getTime(int, java.util.Calendar)
		 */
		@SuppressWarnings("unused")
		@Override
		public Time getTime(int columnIndex, Calendar cal) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar)
		 */
		@SuppressWarnings("unused")
		@Override
		public Time getTime(String columnLabel, Calendar cal)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getTimestamp(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public Timestamp getTimestamp(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getTimestamp(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public Timestamp getTimestamp(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar)
		 */
		@SuppressWarnings("unused")
		@Override
		public Timestamp getTimestamp(int columnIndex, Calendar cal)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getTimestamp(java.lang.String,
		 *      java.util.Calendar)
		 */
		@SuppressWarnings("unused")
		@Override
		public Timestamp getTimestamp(String columnLabel, Calendar cal)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getType()
		 */
		@SuppressWarnings("unused")
		@Override
		public int getType() throws SQLException {
			return 0;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getURL(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public URL getURL(int columnIndex) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getURL(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public URL getURL(String columnLabel) throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getUnicodeStream(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public InputStream getUnicodeStream(int columnIndex)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getUnicodeStream(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public InputStream getUnicodeStream(String columnLabel)
				throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#getWarnings()
		 */
		@SuppressWarnings("unused")
		@Override
		public SQLWarning getWarnings() throws SQLException {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#insertRow()
		 */
		@SuppressWarnings("unused")
		@Override
		public void insertRow() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#isAfterLast()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean isAfterLast() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#isBeforeFirst()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean isBeforeFirst() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#isClosed()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean isClosed() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#isFirst()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean isFirst() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#isLast()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean isLast() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#last()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean last() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#moveToCurrentRow()
		 */
		@SuppressWarnings("unused")
		@Override
		public void moveToCurrentRow() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#moveToInsertRow()
		 */
		@SuppressWarnings("unused")
		@Override
		public void moveToInsertRow() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#next()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean next() throws SQLException {
			boolean result = false;
			if (notCalled) {
				result = true;
				notCalled = false;
			}
			return result;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#previous()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean previous() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#refreshRow()
		 */
		@SuppressWarnings("unused")
		@Override
		public void refreshRow() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#relative(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean relative(int rows) throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#rowDeleted()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean rowDeleted() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#rowInserted()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean rowInserted() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#rowUpdated()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean rowUpdated() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#setFetchDirection(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void setFetchDirection(int direction) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#setFetchSize(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void setFetchSize(int rows) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateArray(int, java.sql.Array)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateArray(int columnIndex, Array x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateArray(java.lang.String, java.sql.Array)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateArray(String columnLabel, Array x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateAsciiStream(int columnIndex, InputStream x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String,
		 *      java.io.InputStream)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateAsciiStream(String columnLabel, InputStream x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream,
		 *      int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateAsciiStream(int columnIndex, InputStream x, int length)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String,
		 *      java.io.InputStream, int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateAsciiStream(String columnLabel, InputStream x,
				int length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateAsciiStream(int, java.io.InputStream,
		 *      long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateAsciiStream(int columnIndex, InputStream x,
				long length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateAsciiStream(java.lang.String,
		 *      java.io.InputStream, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateAsciiStream(String columnLabel, InputStream x,
				long length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBigDecimal(int, java.math.BigDecimal)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBigDecimal(int columnIndex, BigDecimal x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBigDecimal(java.lang.String,
		 *      java.math.BigDecimal)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBigDecimal(String columnLabel, BigDecimal x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBinaryStream(int columnIndex, InputStream x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String,
		 *      java.io.InputStream)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBinaryStream(String columnLabel, InputStream x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream,
		 *      int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBinaryStream(int columnIndex, InputStream x,
				int length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String,
		 *      java.io.InputStream, int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBinaryStream(String columnLabel, InputStream x,
				int length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBinaryStream(int, java.io.InputStream,
		 *      long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBinaryStream(int columnIndex, InputStream x,
				long length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBinaryStream(java.lang.String,
		 *      java.io.InputStream, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBinaryStream(String columnLabel, InputStream x,
				long length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBlob(int, java.sql.Blob)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBlob(int columnIndex, Blob x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBlob(java.lang.String, java.sql.Blob)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBlob(String columnLabel, Blob x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBlob(int columnIndex, InputStream inputStream)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBlob(java.lang.String,
		 *      java.io.InputStream)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBlob(String columnLabel, InputStream inputStream)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBlob(int, java.io.InputStream, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBlob(int columnIndex, InputStream inputStream,
				long length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBlob(java.lang.String,
		 *      java.io.InputStream, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBlob(String columnLabel, InputStream inputStream,
				long length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBoolean(int, boolean)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBoolean(int columnIndex, boolean x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBoolean(java.lang.String, boolean)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBoolean(String columnLabel, boolean x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateByte(int, byte)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateByte(int columnIndex, byte x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateByte(java.lang.String, byte)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateByte(String columnLabel, byte x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBytes(int, byte[])
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateBytes(java.lang.String, byte[])
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateBytes(String columnLabel, byte[] x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateCharacterStream(int columnIndex, Reader x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String,
		 *      java.io.Reader)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateCharacterStream(String columnLabel, Reader reader)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader,
		 *      int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateCharacterStream(int columnIndex, Reader x, int length)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String,
		 *      java.io.Reader, int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateCharacterStream(String columnLabel, Reader reader,
				int length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateCharacterStream(int, java.io.Reader,
		 *      long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateCharacterStream(int columnIndex, Reader x, long length)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateCharacterStream(java.lang.String,
		 *      java.io.Reader, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateCharacterStream(String columnLabel, Reader reader,
				long length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateClob(int, java.sql.Clob)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateClob(int columnIndex, Clob x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateClob(java.lang.String, java.sql.Clob)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateClob(String columnLabel, Clob x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateClob(int, java.io.Reader)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateClob(int columnIndex, Reader reader)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateClob(String columnLabel, Reader reader)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateClob(int, java.io.Reader, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateClob(int columnIndex, Reader reader, long length)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateClob(java.lang.String, java.io.Reader,
		 *      long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateClob(String columnLabel, Reader reader, long length)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateDate(int, java.sql.Date)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateDate(int columnIndex, Date x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateDate(java.lang.String, java.sql.Date)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateDate(String columnLabel, Date x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateDouble(int, double)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateDouble(int columnIndex, double x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateDouble(java.lang.String, double)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateDouble(String columnLabel, double x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateFloat(int, float)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateFloat(int columnIndex, float x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateFloat(java.lang.String, float)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateFloat(String columnLabel, float x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateInt(int, int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateInt(int columnIndex, int x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateInt(java.lang.String, int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateInt(String columnLabel, int x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateLong(int, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateLong(int columnIndex, long x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateLong(java.lang.String, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateLong(String columnLabel, long x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNCharacterStream(int columnIndex, Reader x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String,
		 *      java.io.Reader)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNCharacterStream(String columnLabel, Reader reader)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNCharacterStream(int, java.io.Reader,
		 *      long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNCharacterStream(int columnIndex, Reader x,
				long length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNCharacterStream(java.lang.String,
		 *      java.io.Reader, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNCharacterStream(String columnLabel, Reader reader,
				long length) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNClob(int, java.sql.NClob)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNClob(int columnIndex, NClob nClob)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.sql.NClob)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNClob(String columnLabel, NClob nClob)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNClob(int, java.io.Reader)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNClob(int columnIndex, Reader reader)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNClob(String columnLabel, Reader reader)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNClob(int, java.io.Reader, long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNClob(int columnIndex, Reader reader, long length)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNClob(java.lang.String, java.io.Reader,
		 *      long)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNClob(String columnLabel, Reader reader, long length)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNString(int, java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNString(int columnIndex, String nString)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNString(java.lang.String,
		 *      java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNString(String columnLabel, String nString)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNull(int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNull(int columnIndex) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateNull(java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateNull(String columnLabel) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateObject(int, java.lang.Object)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateObject(int columnIndex, Object x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateObject(java.lang.String,
		 *      java.lang.Object)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateObject(String columnLabel, Object x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateObject(int, java.lang.Object, int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateObject(int columnIndex, Object x, int scaleOrLength)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateObject(java.lang.String,
		 *      java.lang.Object, int)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateObject(String columnLabel, Object x, int scaleOrLength)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateRef(int, java.sql.Ref)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateRef(int columnIndex, Ref x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateRef(java.lang.String, java.sql.Ref)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateRef(String columnLabel, Ref x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateRow()
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateRow() throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateRowId(int, java.sql.RowId)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateRowId(int columnIndex, RowId x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateRowId(java.lang.String, java.sql.RowId)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateRowId(String columnLabel, RowId x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateSQLXML(int, java.sql.SQLXML)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateSQLXML(int columnIndex, SQLXML xmlObject)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateSQLXML(java.lang.String,
		 *      java.sql.SQLXML)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateSQLXML(String columnLabel, SQLXML xmlObject)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateShort(int, short)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateShort(int columnIndex, short x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateShort(java.lang.String, short)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateShort(String columnLabel, short x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateString(int, java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateString(int columnIndex, String x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateString(java.lang.String,
		 *      java.lang.String)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateString(String columnLabel, String x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateTime(int, java.sql.Time)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateTime(int columnIndex, Time x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateTime(java.lang.String, java.sql.Time)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateTime(String columnLabel, Time x) throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateTimestamp(int, java.sql.Timestamp)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateTimestamp(int columnIndex, Timestamp x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#updateTimestamp(java.lang.String,
		 *      java.sql.Timestamp)
		 */
		@SuppressWarnings("unused")
		@Override
		public void updateTimestamp(String columnLabel, Timestamp x)
				throws SQLException {
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.ResultSet#wasNull()
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean wasNull() throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.Wrapper#isWrapperFor(java.lang.Class)
		 */
		@SuppressWarnings("unused")
		@Override
		public boolean isWrapperFor(Class<?> iface) throws SQLException {
			return false;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see java.sql.Wrapper#unwrap(java.lang.Class)
		 */
		@SuppressWarnings("unused")
		@Override
		public <T> T unwrap(Class<T> iface) throws SQLException {
			return null;
		}

	}

}
