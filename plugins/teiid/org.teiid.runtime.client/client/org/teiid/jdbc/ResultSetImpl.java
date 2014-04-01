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

import java.io.IOException;
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
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.teiid.client.RequestMessage;
import org.teiid.client.ResultsMessage;
import org.teiid.client.lob.LobChunkInputStream;
import org.teiid.client.lob.StreamingLobChunckProducer;
import org.teiid.client.plan.PlanNode;
import org.teiid.client.util.ResultsFuture;
import org.teiid.core.types.BinaryType;
import org.teiid.core.types.BlobImpl;
import org.teiid.core.types.BlobType;
import org.teiid.core.types.ClobImpl;
import org.teiid.core.types.ClobType;
import org.teiid.core.types.InputStreamFactory;
import org.teiid.core.types.SQLXMLImpl;
import org.teiid.core.types.Streamable;
import org.teiid.core.types.XMLType;
import org.teiid.core.util.PropertiesUtils;
import org.teiid.core.util.TimestampWithTimezone;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.jdbc.BatchResults.Batch;
import org.teiid.jdbc.BatchResults.BatchFetcher;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;

public class ResultSetImpl extends WrapperImpl implements TeiidResultSet, BatchFetcher {
	private static Logger logger = Logger.getLogger("org.teiid.jdbc"); //$NON-NLS-1$
	
	private static final int BEFORE_FIRST_ROW = 0;

	public static final String DISABLE_FETCH_SIZE = "disableResultSetFetchSize"; //$NON-NLS-1$

	// the object which was last read from Results
	private Object currentValue;

	// This object represents metadata for this result set.
	private ResultSetMetaData rmetadata;
	// Statement that causes this results
	private StatementImpl statement;

    // Cursor related state
    private int cursorType;
    private boolean isClosed;

    // reuse the original request's state
    private long requestID;
    private BatchResults batchResults;
    private int columnCount;
    private int resultColumns;
    private int parameters;
    private TimeZone serverTimeZone;
    private PlanNode updatedPlanDescription;
    private int maxFieldSize;
    private int fetchSize;
    private int maxRows;
    
	private Map<String, Integer> columnMap;
	
	//blocking operations that throw positioning errors are recoverable if we attempt to honor the
	//results requested
	private ResultsFuture<ResultsMessage> asynchResults;
    boolean asynch;
    
    private ResultsFuture<ResultsMessage> prefetch;
    private boolean usePrefetch;

	private int skipTo;

	private static boolean DISABLE_FETCH_SIZE_DEFAULT = PropertiesUtils.getBooleanProperty(System.getProperties(), "org.teiid." + DISABLE_FETCH_SIZE, false); //$NON-NLS-1$
	
	private Boolean disableFetchSize;

	/**
	 * Constructor.
	 * 
	 * @param resultsMsg
	 * @param statement
	 * @throws SQLException
	 */
	ResultSetImpl(ResultsMessage resultsMsg, StatementImpl statement) throws SQLException {
		this(resultsMsg, statement, null, 0);
	}

	ResultSetImpl(ResultsMessage resultsMsg, StatementImpl statement,
			ResultSetMetaData metadata, int parameters) throws SQLException {
		this.statement = statement;
		this.parameters = parameters;
		// server latency-related timestamp
        this.requestID = statement.getCurrentRequestID();
        this.cursorType = statement.getResultSetType();
        this.serverTimeZone = statement.getServerTimeZone();
		if (metadata == null) {
			MetadataProvider provider = new DeferredMetadataProvider(resultsMsg.getColumnNames(),
							resultsMsg.getDataTypes(), statement,
							statement.getCurrentRequestID());
			rmetadata = new ResultSetMetaDataImpl(provider, this.statement.getExecutionProperty(ExecutionProperties.JDBC4COLUMNNAMEANDLABELSEMANTICS));
		} else {
			rmetadata = metadata;
		}
        // Cache the column count and isLOB values since every call to getObject uses these.
		this.columnCount = rmetadata.getColumnCount();
		
		this.resultColumns = columnCount - parameters;
		if (this.parameters > 0) {
			rmetadata = new FilteredResultsMetadata(rmetadata, resultColumns);
		}
		this.fetchSize = statement.getFetchSize();
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("Creating ResultSet requestID: " + requestID + " beginRow: " + resultsMsg.getFirstRow() + " resultsColumns: " + resultColumns + " parameters: " + parameters); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		this.usePrefetch = cursorType == ResultSet.TYPE_FORWARD_ONLY && !statement.useCallingThread();
		this.maxRows = statement.getMaxRows();
		this.batchResults = new BatchResults(this, getCurrentBatch(resultsMsg), this.cursorType == ResultSet.TYPE_FORWARD_ONLY ? 1 : BatchResults.DEFAULT_SAVED_BATCHES);
	}

	private ITeiidServerVersion getTeiidVersion() {
	    return statement.getTeiidVersion();
	}

	private boolean isLessThanTeiidEight() {
        return getTeiidVersion().isLessThan(TeiidServerVersion.TEIID_8_SERVER);
    }

    private void checkSupportedVersion() {
        if (isLessThanTeiidEight())
            throw new UnsupportedOperationException();
    }

	public void setMaxFieldSize(int maxFieldSize) {
		this.maxFieldSize = maxFieldSize;
	}
	
    /**
     * Close this result set.
     */
    @Override
    public void close() throws SQLException{
    	if(!isClosed) {
            // close the the server's statement object (if necessary)
    		if(this.requestID >= 0){
	            try {
					this.statement.getDQP().closeRequest(requestID);
				} catch (Exception e) {
					throw new SQLException(e);
				}
    		}
            isClosed = true;
        }
    }
    
	@Override
    public boolean isClosed() throws SQLException {
		return isClosed;
	}
    
    protected void checkClosed() throws SQLException {
    	if (isClosed) {
    		String msg = Messages
    				.getString(Messages.JDBC.MMResultSet_Cant_call_closed_resultset);
    		throw new SQLException(msg);
    	}
    }
    
     /**
      * Return the value for output/return parameter given the index
      * of the parameter in the ResultSet
      * @param index Index of the parameter to be retrieved.
      */
     Object getOutputParamValue(int index) throws SQLException {
         if (index <= resultColumns || index > resultColumns + parameters) {
             throw new SQLException(Messages.getString(Messages.JDBC.StoredProcedureResultsImpl_Invalid_parameter_index__0_2, index));
         }
         // Mark the row we're on
         final int originalRow = getAbsoluteRowNumber();
         
         this.batchResults.absolute(-1);
         try {
         	return getObjectDirect(index);
         } finally {
         	this.batchResults.absolute(originalRow);
         }
     }
     
     /**
      * <p>Get a java object based on the column index for the current row.</p>
      * @param The index of the column whose value needs to be fetched.
      * @return The value of the column as an object.
      * @throws SQLException if a results access error occurs or transform fails.
      */
     @Override
    public Object getObject(int column) throws SQLException {
         if (isAfterLast()) {
             throw new SQLException(Messages.getString(Messages.JDBC.StoredProcedureResultsImpl_ResultSet_cursor_is_after_the_last_row_1));
         }
         // only get the Object of the result set
         if(column > resultColumns){
             throw new SQLException(Messages.getString(Messages.JDBC.ResultsImpl_Invalid_col_index, column));
         }
         return getObjectDirect(column);
     }

    @Override
    public int getFetchSize() throws SQLException {
        return this.fetchSize;
    }
    
    /**
     * Assumes forward only cursoring
     */
    public ResultsFuture<Boolean> submitNext() throws SQLException {
    	Boolean hasNext = batchResults.hasNext(getOffset() + 1, false);
    	if (hasNext != null) {
    		return StatementImpl.booleanFuture(next());
    	}
    	ResultsFuture<ResultsMessage> pendingResult = submitRequestBatch(batchResults.getCurrentRowNumber() + 1);
    	final ResultsFuture<Boolean> result = new ResultsFuture<Boolean>();
    	pendingResult.addCompletionListener(new ResultsFuture.CompletionListener<ResultsMessage>() {
    		@Override
    		public void onCompletion(ResultsFuture<ResultsMessage> future) {
    			try {
					batchResults.setBatch(processBatch(future.get()));
					result.getResultsReceiver().receiveResults(next());
				} catch (Throwable t) {
					result.getResultsReceiver().exceptionOccurred(t);
				}
    		}
		});
    	return result;
    }

    @Override
    public boolean next() throws SQLException {
        checkClosed();
        if (hasNext()) {
    		return batchResults.next();
    	}
        batchResults.next();
    	return false;
    }

    @Override
    public boolean previous() throws SQLException {
        checkClosed();
        checkNotForwardOnly();
        return batchResults.previous();
    }

    @Override
    public int getRow() throws SQLException {
        checkClosed();
        if (isAfterLast()) {
        	return 0;
        }
        return getAbsoluteRowNumber();
    }

    /**
     * Get the value of the current row at the column index specified.
     * @param column Column index
     * @return Value at column, which may be null
     * @throws SQLException if this result set has an exception
     */
    public Object getObjectDirect(int column) throws SQLException {
    	checkClosed();
    	if(column < 1 || column > columnCount) {
            throw new IllegalArgumentException(Messages.getString(Messages.JDBC.ResultsImpl_Invalid_col_index, column));
        }
        List<?> cursorRow = batchResults.getCurrentRow();
        
        if (cursorRow == null) {
            throw new SQLException(Messages.getString(Messages.JDBC.ResultsImpl_The_cursor_is_not_on_a_valid_row_1));
        }

        // defect 13539 - set the currentValue (defined in MMResultSet) so that wasNull() accurately returns whether this value was null
        currentValue = cursorRow.get(column-1);
            
        if (currentValue instanceof Streamable<?>) {
    		Object reference = ((Streamable<?>)currentValue).getReference();
        	if (reference != null) {
        		currentValue = reference;
        		return currentValue;
        	}
            if(currentValue instanceof ClobType){
            	currentValue = new ClobImpl(createInputStreamFactory((ClobType)currentValue), ((ClobType)currentValue).getLength());
            }
            else if (currentValue instanceof BlobType) {
            	InputStreamFactory isf = createInputStreamFactory((BlobType)currentValue);
            	isf.setLength(((BlobType)currentValue).getLength());
            	currentValue = new BlobImpl(isf);
            }
            else if (currentValue instanceof XMLType) {
            	XMLType val = (XMLType)currentValue;
            	currentValue = new SQLXMLImpl(createInputStreamFactory(val));
            	((SQLXMLImpl)currentValue).setEncoding(val.getEncoding());
            } 
        } 
        else if (currentValue instanceof java.util.Date) {
            return TimestampWithTimezone.create((java.util.Date)currentValue, serverTimeZone, getDefaultCalendar(), currentValue.getClass());
        }
        else if (maxFieldSize > 0 && currentValue instanceof String) {
        	String val = (String)currentValue;
        	currentValue = val.substring(0, Math.min(maxFieldSize/2, val.length()));
        }
        else if (currentValue instanceof BinaryType) {
        	BinaryType val = (BinaryType)currentValue;
        	currentValue = val.getBytesDirect();
        }
        return currentValue;
    }
    
	private InputStreamFactory createInputStreamFactory(Streamable<?> type) {
		final StreamingLobChunckProducer.Factory factory = new StreamingLobChunckProducer.Factory(this.statement.getDQP(), this.requestID, type);
		InputStreamFactory isf = new InputStreamFactory() {
			@Override
			public InputStream getInputStream() throws IOException {
				return new LobChunkInputStream(factory.getLobChunkProducer());
			}
		};
		return isf;
	}

    /**
     * Get all values in current record in column order
     * @return List of Object values in current row
     * @throws SQLException if an access error occurs.
     */
    public List<?> getCurrentRecord() throws SQLException {
    	checkClosed();
        return batchResults.getCurrentRow();
    }
    /*
     * @see java.sql.ResultSet#getType()
     */
    @Override
    public int getType() throws SQLException {
        return this.cursorType;
    }

    @Override
    public boolean absolute( int row) throws SQLException {
        checkClosed();
        checkNotForwardOnly();
        return batchResults.absolute(row, getOffset());
    }
    
    protected PlanNode getUpdatedPlanDescription() {
    	return updatedPlanDescription;
    }
    
    @Override
    public Batch requestBatch(int beginRow) throws SQLException{
    	checkClosed();
        try {
        	if (prefetch != null) {
    			//TODO: this is not efficient if the user is skipping around the results
    			//but the server logic at this point basically requires us
    			//to read what we have requested before requesting more (no queuing)
    			ResultsMessage result = getResults(prefetch);
    			prefetch = null;
    			Batch nextBatch = processBatch(result);
				return nextBatch;
        	}
        	ResultsFuture<ResultsMessage> results = submitRequestBatch(beginRow);
        	if (asynch && !results.isDone()) {
        		synchronized (this) {
            		asynchResults = results;
				}
        		throw new AsynchPositioningException();
        	}
        	ResultsMessage currentResultMsg = getResults(results);
            return processBatch(currentResultMsg);
		} catch (InterruptedException e) {
			throw new SQLException(e);
		} catch (ExecutionException e) {
			throw new SQLException(e);
		} catch (TimeoutException e) {
			throw new SQLException(e);
		}
    }

	private ResultsFuture<ResultsMessage> submitRequestBatch(int beginRow)
			throws SQLException {
		if (beginRow > maxRows && skipTo > 0) {
			beginRow = skipTo;
		}
		ResultsFuture<ResultsMessage> results;
		if (asynch) {
			synchronized (this) {
				if (this.asynchResults != null) {
					results = this.asynchResults;
					this.asynchResults = null;
					return results;
				}
			}
		}
		if (logger.isLoggable(Level.FINER)) {
			logger.finer("requestBatch requestID: " + requestID + " beginRow: " + beginRow ); //$NON-NLS-1$ //$NON-NLS-2$
		}
		try {
			results = statement.getDQP().processCursorRequest(requestID, beginRow, fetchSize);
		} catch (TeiidClientException e) {
			throw new SQLException(e);
		}
		return results;
	}

	private Batch processBatch(
			ResultsMessage currentResultMsg) throws SQLException {
		if (currentResultMsg.getException() != null) {
		    throw new SQLException(currentResultMsg.getException());
		}

		this.accumulateWarnings(currentResultMsg);
		return getCurrentBatch(currentResultMsg);
	}

	private ResultsMessage getResults(ResultsFuture<ResultsMessage> results)
			throws SQLException, InterruptedException, ExecutionException,
			TimeoutException {
		int timeoutSeconds = statement.getQueryTimeout();
		if (timeoutSeconds == 0) {
			timeoutSeconds = Integer.MAX_VALUE;
		}
		ResultsMessage currentResultMsg = results.get(timeoutSeconds, TimeUnit.SECONDS);
		return currentResultMsg;
	}

	private Batch getCurrentBatch(ResultsMessage currentResultMsg) throws SQLException {
		this.updatedPlanDescription = currentResultMsg.getPlanDescription();
		if (usePrefetch && !asynch 
				&& prefetch == null && currentResultMsg.getLastRow() != currentResultMsg.getFinalRow()) {
			//fetch before processing the results
			prefetch = submitRequestBatch(currentResultMsg.getLastRow() + 1);
		}
		currentResultMsg.processResults();
		List<?> lastTuple = null;
		List<List<?>> resultsList = (List<List<?>>) currentResultMsg.getResultsList();
		//similar logic to BatchCollector on the server side
		//this is a catch all in case the server doesn't enforce the max
		//such as currently the case with cached subset results
		List<?>[] tuples = null;
		int firstRow = currentResultMsg.getFirstRow();
		int endRow = currentResultMsg.getLastRow();
		int lastRow = currentResultMsg.getFinalRow();
		if (maxRows > 0) {
	    	if (parameters > 0) {
	    		if (currentResultMsg.getLastRow() == currentResultMsg.getFinalRow()) {
	    			lastTuple = resultsList.get(resultsList.size() - 1);
	    		} else if (maxRows < currentResultMsg.getFirstRow()) {
	    			//awkward scenario - there are parameters at the end
	    			//skip ahead as far as possible
	    			if (lastRow != 0) {
	    				skipTo = lastRow;
	    			} else {
	    				skipTo = endRow + 1;
	    			}
	    			return new Batch(new List<?>[0], firstRow, firstRow - 1);
	    		}
	    	}
	    	if (maxRows < currentResultMsg.getLastRow()) {
	    		firstRow = Math.min(maxRows + 1, firstRow);
	    		resultsList = resultsList.subList(0, maxRows - firstRow + 1);
	    		endRow = maxRows;
	    		lastRow = endRow;
	    	}
	    	tuples = resultsList.toArray(new List<?>[resultsList.size()+(lastTuple!=null?1:0)]);
	    	if (lastTuple != null) {
	    		endRow++;
	    		lastRow = endRow;
	    		tuples[tuples.length-1] = lastTuple;
	    	}
		} else {
			tuples = resultsList.toArray(new List<?>[resultsList.size()]);
		}
		Batch result = new Batch(tuples, firstRow, endRow);
		result.setLastRow(lastRow);
		return result;
	}
    
	protected int getFinalRowNumber() {
    	return Math.max(-1, batchResults.getFinalRowNumber() - getOffset());
	}

	protected boolean hasNext() throws SQLException {
		return batchResults.hasNext(getOffset() + 1, true);
	}
	
	@Override
	public int available() throws SQLException {
		int current = batchResults.getCurrentRowNumber();
		int highest = batchResults.getHighestRowNumber();
		return highest - current - getOffset() - (batchResults.isTailLast()?1:0);
	}
	
	protected int getOffset() {
		return parameters > 0 ? 1 : 0;
	}

	protected int getAbsoluteRowNumber() {
		return batchResults.getCurrentRowNumber();
	}

	@Override
    public void cancelRowUpdates() throws SQLException {
		// do nothing.
		checkClosed(); // check to see if the ResultSet is closed
	}

	@Override
	public void clearWarnings() throws SQLException {
		// do nothing
		checkClosed(); // check to see if the ResultSet is closed
	}

	@Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return DataTypeTransformer.getBigDecimal(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public BigDecimal getBigDecimal(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getBigDecimal(findColumn(columnName));
	}

	@Override
    public BigDecimal getBigDecimal(int columnIndex, int scale)
			throws SQLException {

		// do the necessary transformation depending on the datatype of the
		// object at the given index.
		BigDecimal bigDecimalObject = DataTypeTransformer
				.getBigDecimal(getTeiidVersion(), getObject(columnIndex));
		
		if (bigDecimalObject == null) {
			return null;
		}

		// set the scale on the bigDecimal
		return bigDecimalObject.setScale(scale);
	}

	@Override
    public BigDecimal getBigDecimal(String columnName, int scale)
			throws SQLException {
		// find the columnIndex for the given column name.
		return getBigDecimal(findColumn(columnName), scale);
	}

	@Override
    public java.io.InputStream getBinaryStream(int columnIndex)
			throws SQLException {
		Object value = getObject(columnIndex);
		if (value == null) {
			return null;
		}

		if (value instanceof Blob) {
			return ((Blob) value).getBinaryStream();
		}
		
		if (value instanceof SQLXML) {
			return ((SQLXML)value).getBinaryStream();
		}

		throw new SQLException(Messages.getString(Messages.JDBC.MMResultSet_cannot_convert_to_binary_stream));
	}

	@Override
    public java.io.InputStream getBinaryStream(String columnName)
			throws SQLException {
		return getBinaryStream(findColumn(columnName));
	}

	@Override
    public Blob getBlob(int columnIndex) throws SQLException {
		return DataTypeTransformer.getBlob(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public Blob getBlob(String columnName) throws SQLException {
		return getBlob(findColumn(columnName));
	}

	@Override
    public boolean getBoolean(int columnIndex) throws SQLException {
		return DataTypeTransformer.getBoolean(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public boolean getBoolean(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getBoolean(findColumn(columnName));
	}

	@Override
    public byte getByte(int columnIndex) throws SQLException {
		return DataTypeTransformer.getByte(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public byte getByte(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getByte(findColumn(columnName));
	}

	@Override
    public byte[] getBytes(int columnIndex) throws SQLException {
		return DataTypeTransformer.getBytes(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public byte[] getBytes(String columnName) throws SQLException {
		return getBytes(findColumn(columnName));
	}

	/**
	 * Get the concurrency type for this ResultSet object. The concurrency was
	 * set by the Statement object. The possible concurrency types are
	 * CONCUR_READ_ONLY and CONCUR_UPDATABLE.
	 * 
	 * @return The resultSets are not updatable, this method returns
	 * 	CONCUR_READ_ONLY.
	 * @throws SQLException
	 * 		if the there is an error accesing results
	 */
	@Override
    public int getConcurrency() throws SQLException {

		checkClosed(); // check to see if the ResultSet is closed
		return ResultSet.CONCUR_READ_ONLY;
	}

	@Override
    public java.io.Reader getCharacterStream(int columnIndex)
			throws SQLException {
		Object value = getObject(columnIndex);
		return DataTypeTransformer.getCharacterStream(getTeiidVersion(), value);
	}

	@Override
    public java.io.Reader getCharacterStream(String columnName)
			throws SQLException {
		return getCharacterStream(findColumn(columnName));
	}

	/**
	 * This method will return the value in the current row as a Date object.
	 * This will use the timeZone info of the calendar object.
	 * 
	 * @param The
	 * 		index of the column whose value needs to be fetched.
	 * @param Calender
	 * 		object used to get the date value.
	 * @return The value of the column as a Date object.
	 * @throws SQLException
	 * 		if a results access error occurs or transform fails.
	 */
	@Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		Date value = DataTypeTransformer.getDate(getTeiidVersion(), getObject(columnIndex));

		if (value != null && cal != null) {
			value = TimestampWithTimezone.createDate(value,
					getDefaultCalendar().getTimeZone(), cal);
		}

		return value;
	}

	/**
	 * Get the Date value for the given column.
	 * 
	 * @param columnName
	 * 		. The name of the column whose value needs to be fetched.
	 * @param Calender
	 * 		object used to get the date value.
	 * @return value of the column as an int.
	 * @throws SQLException
	 * 		unable to obtain date value for the given column.
	 */
	@Override
    public Date getDate(String columnName, Calendar cal) throws SQLException {
		// find the columnIndex for the given column name.
		return getDate(findColumn(columnName), cal);
	}

	@Override
    public Date getDate(int columnIndex) throws SQLException {
		return getDate(columnIndex, null);
	}

	@Override
    public Date getDate(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getDate(findColumn(columnName));
	}

	@Override
    public double getDouble(int columnIndex) throws SQLException {
		return DataTypeTransformer.getDouble(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public double getDouble(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getDouble(findColumn(columnName));
	}

	/**
	 * Gets the direction suggested to the driver as the direction in which to
	 * fetch rows.
	 * 
	 * @return fetch direction for this ResultSet. This cannot be set and is
	 * 	alwayd FETCH_FORWARD.
	 * @throws SQLException
	 */
	@Override
    public int getFetchDirection() throws SQLException {
		checkClosed(); // check to see if the ResultSet is closed
		return ResultSet.FETCH_FORWARD;
	}

	/**
	 * This method will return the value in the current row as a float value.
	 * 
	 * @param The
	 * 		index of the column whose value needs to be fetched.
	 * @return The value of the column as a float value.
	 * @throws SQLException
	 * 		if a results access error occurs or transform fails.
	 */
	@Override
    public float getFloat(int columnIndex) throws SQLException {
		return DataTypeTransformer.getFloat(getTeiidVersion(), getObject(columnIndex));
	}

	/**
	 * Get a float value based on the column name.
	 * 
	 * @param name
	 * 		of the column in the resultset whose value is to be fetched.
	 * @return value of the column as a float.
	 * @throw a SQLException if a resultSet access error occurs.
	 */
	@Override
    public float getFloat(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getFloat(findColumn(columnName));
	}

	/**
	 * This method will return the value in the current row as a int value.
	 * 
	 * @param The
	 * 		index of the column whose value needs to be fetched.
	 * @return The value of the column as a int value.
	 * @throws SQLException
	 * 		if a results access error occurs or transform fails.
	 */
	@Override
    public int getInt(int columnIndex) throws SQLException {
		return DataTypeTransformer.getInteger(getTeiidVersion(), getObject(columnIndex));
	}

	/**
	 * Get an integer based on the column index.
	 * 
	 * @param name
	 * 		of the column in the resultset whose value is to be fetched.
	 * @return value of the column as an int.
	 * @throw a SQLException if a resultSet access error occurs.
	 */
	@Override
    public int getInt(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getInt(findColumn(columnName));
	}

	/**
	 * This method will return the value in the current row as a long value.
	 * 
	 * @param The
	 * 		index of the column whose value needs to be fetched.
	 * @return The value of the column as a long value.
	 * @throws SQLException
	 * 		if a results access error occurs or transform fails.
	 */
	@Override
    public long getLong(int columnIndex) throws SQLException {
		return DataTypeTransformer.getLong(getTeiidVersion(), getObject(columnIndex));
	}

	/**
	 * Get a long based on the column name.
	 * 
	 * @param name
	 * 		of the column in the resultset whose value is to be fetched.
	 * @return value of the column as a long.
	 * @throw a SQLException if a resultSet access error occurs.
	 */
	@Override
    public long getLong(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getLong(findColumn(columnName));
	}

	/**
	 * Get a java object based on the column name.
	 * 
	 * @param name
	 * 		of the column in the resultset whose value is to be fetched.
	 * @return object which gives the column value.
	 * @throw a SQLException if a resultSet access error occurs.
	 */
	@Override
    public Object getObject(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getObject(findColumn(columnName));
	}

	/**
	 * Get a primitive short based on the column index.
	 * 
	 * @param The
	 * 		index of the column whose value needs to be fetched.
	 * @return The value of the column as a short value.
	 * @throws SQLException
	 * 		if a results access error occurs or transform fails.
	 */
	@Override
    public short getShort(int columnIndex) throws SQLException {
		return DataTypeTransformer.getShort(getTeiidVersion(), getObject(columnIndex));
	}

	/**
	 * Get a short based on the column name.
	 * 
	 * @param String
	 * 		representing name of the column.
	 * @return short value of the column.
	 * @throws SQLException
	 * 		if a results access error occurs.
	 */
	@Override
    public short getShort(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getShort(findColumn(columnName));
	}

	/**
	 * Get a String based on the column index.
	 * 
	 * @param The
	 * 		index of the column whose value needs to be fetched.
	 * @return The value of the column as a string value.
	 * @throws SQLException
	 * 		if a results access error occurs or transform fails.
	 */
	@Override
    public String getString(int columnIndex) throws SQLException {
		return DataTypeTransformer.getString(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public String getString(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getString(findColumn(columnName));
	}

	@Override
    public Time getTime(int columnIndex) throws SQLException {
		return getTime(columnIndex, null);
	}

	@Override
    public Time getTime(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getTime(findColumn(columnName));
	}

	/**
	 * This method will return the value in the current row as a Time object.
	 * This will use the timeZone info of the calendar object.
	 * 
	 * @param The
	 * 		index of the column whose value needs to be fetched.
	 * @param Calendar
	 * 		object to be used to construct the Time object.
	 * @return The value of the column as a Time object.
	 * @throws SQLException
	 * 		if a results access error occurs or transform fails.
	 */
	@Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		Time value = DataTypeTransformer.getTime(getTeiidVersion(), getObject(columnIndex));

		if (value != null && cal != null) {
			value = TimestampWithTimezone.createTime(value,
					getDefaultCalendar().getTimeZone(), cal);
		}

		return value;
	}

	/**
	 * Get a java.sql.Time based on the column name.
	 * 
	 * @param name
	 * 		of the column whose value is to be fetched as a timestamp
	 * @param calender
	 * 		object to include the timezone info in the object returned
	 * @return value of the column as a Timestamp object
	 * @throws SQLException
	 * 		if a results access error occurs.
	 */
	@Override
    public Time getTime(String columnName, Calendar cal) throws SQLException {
		// find the columnIndex for the given column name.
		return getTime((findColumn(columnName)), cal);
	}

	/**
	 * This method will return the value in the current row as a Timestamp
	 * object. This will assume the default timeZone.
	 * 
	 * @param The
	 * 		index of the column whose value needs to be fetched.
	 * @return The value of the column as a Timestamp object.
	 * @throws SQLException
	 * 		if a results access error occurs or transform fails.
	 */
	@Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return getTimestamp(columnIndex, null);
	}

	/**
	 * Get a java.sql.Timestamp based on the column name.
	 * 
	 * @param name
	 * 		of the column whose value is to be fetched as a timestamp
	 * @return value of the column as a Timestamp object
	 * @throws SQLException
	 * 		if a results access error occurs.
	 */
	@Override
    public Timestamp getTimestamp(String columnName) throws SQLException {
		// find the columnIndex for the given column name.
		return getTimestamp(findColumn(columnName));
	}

	/**
	 * This method will return the value in the current row as a Timestamp
	 * object. This will use the timeZone info of the calendar object.
	 * 
	 * @param The
	 * 		index of the column whose value needs to be fetched.
	 * @param Calendar
	 * 		object to be used to construct the Timestamp object.
	 * @return The value of the column as a Timestamp object.
	 * @throws SQLException
	 * 		if a results access error occurs or transform fails.
	 */
	@Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal)
			throws SQLException {
		Timestamp value = DataTypeTransformer.getTimestamp(getTeiidVersion(), getObject(columnIndex));

		if (value != null && cal != null) {
			value = TimestampWithTimezone.createTimestamp(value,
					getDefaultCalendar().getTimeZone(), cal);
		}

		return value;
	}

	/**
	 * Get a java.sql.Timestamp based on the column name.
	 * 
	 * @param name
	 * 		of the column whose value is to be fetched as a timestamp
	 * @param calender
	 * 		object to include the timezone info in the object returned
	 * @return value of the column as a Timestamp object
	 * @throws SQLException
	 * 		if a results access error occurs.
	 */
	@Override
    public Timestamp getTimestamp(String columnName, Calendar cal)
			throws SQLException {
		// find the columnIndex for the given column name.
		return getTimestamp(findColumn(columnName), cal);
	}

	/**
	 * Moves the cursor to the remembered cursor position, usually the current
	 * row. This method does not have any effect if the cursor is not on the
	 * insert row. ResultSet cannot currently be updated.
	 */
	@Override
    public void moveToCurrentRow() throws SQLException {
		// do nothing
	}

	/**
	 * This will return a boolean value if the last column that was read had a
	 * value equal to null. If the last column read was null return true, else
	 * return false.
	 * 
	 * @return A boolean value showing if the lastvalue read in was null or not.
	 * @throws SQLException
	 */
	@Override
    public boolean wasNull() throws SQLException {
		
		checkClosed(); // check to see if the ResultSet is closed

		return currentValue == null;
	}

	protected void accumulateWarnings(ResultsMessage resultsMsg) {
		this.statement.accumulateWarnings(resultsMsg.getWarnings());
	}

	/**
	 * <p>
	 * This method returns the meta data of the result set, such as the number,
	 * types and properties of this resultSets columns.
	 * </p>
	 * 
	 * @return ResultSerMetaData object for these results.
	 * @throws SQLException
	 * 		if results access error occurs
	 */
	@Override
    public ResultSetMetaData getMetaData() throws SQLException {
		checkClosed();
		return rmetadata;
	}

	/**
	 * Retrieves the RequestID for the query that created this ResultSet.
	 * 
	 * @return The requestID for the query that created these results
	 * @throws SQLException
	 */
	@Override
    public String getCursorName() throws SQLException {
		return null;
	}

	/**
	 * <p>
	 * Retrieves the Statement object that produced this ResultSet object.
	 * 
	 * @return a Statement object.
	 * 	</p>
	 * @throws SQLException
	 * 		if the there is an error accesing results
	 */
	@Override
    public StatementImpl getStatement() throws SQLException {
		checkClosed();
		return statement;
	}

	@Override
    public SQLWarning getWarnings() throws SQLException {
		checkClosed();

		return null;
	}

	/**
	 * True if current record is the first in the result set.
	 * 
	 * @return True if current row is first
	 * @throws QueryResultsException
	 * 		if this result set has an exception
	 * @throws InvalidatedResultsException
	 * 		if the results were obtained during a transaction and the
	 * 		transaction has been rolled back
	 */
	@Override
    public boolean isFirst() throws SQLException {
		return this.getAbsoluteRowNumber() == BEFORE_FIRST_ROW + 1 && hasNext();
	}

	/**
	 * True if current record is the last in the result set.
	 * 
	 * @return True if current row is last
	 * @throws QueryResultsException
	 * 		if this result set has an exception
	 * @throws InvalidatedResultsException
	 * 		if the results were obtained during a transaction and the
	 * 		transaction has been rolled back
	 */
	@Override
    public boolean isLast() throws SQLException {
		return !hasNext() && this.getAbsoluteRowNumber() > BEFORE_FIRST_ROW && this.getAbsoluteRowNumber() == getFinalRowNumber();
	}
	
	/**
	 * <p>
	 * Determines whether the cursor is after the last row in this ResultSet
	 * object. This method should be called only if the result set is
	 * scrollable.
	 * </p>
	 * 
	 * @return true if the cursor is after the last row in the resultSet.
	 * @throws SQLException
	 */
	@Override
    public boolean isAfterLast() throws SQLException {
		if (getFinalRowNumber() == -1) {
			return false;
		}
		// return true if the current row has a next row
		// it is also not the last
		return !hasNext() && this.getAbsoluteRowNumber() > BEFORE_FIRST_ROW && this.getAbsoluteRowNumber() > getFinalRowNumber();
	}

	/**
	 * <p>
	 * Determines whether the cursor is before the first row in this ResultSet
	 * object. This method should be called only if the result set is
	 * scrollable.
	 * </p>
	 * 
	 * @return true if the cursor is before the last row in the resultSet;false
	 * 	if the cursor is at any other position or the result set contains no
	 * 	rows.
	 * @throws SQLException
	 */
	@Override
    public boolean isBeforeFirst() throws SQLException {
		// return true if there are rows and the current row is before first
		return getAbsoluteRowNumber() == BEFORE_FIRST_ROW && hasNext();
	}

	/**
	 * <p>
	 * Moves the cursor a number of rows relative to the current row in this
	 * ResultSet object. The number of rows may be positive or negative.
	 * </p>
	 * 
	 * @param number
	 * 		of rows to move relative to the present row.
	 * @return true if the cursor is on a valid row in the resultSet.
	 * @throws SQLException
	 * 		if the there is an error accessing results
	 */
	@Override
    public boolean relative(int rows) throws SQLException {
		if (isBeforeFirst() || isAfterLast() || getFinalRowNumber() == 0) {
			throw new SQLException(
					Messages
							.getString(Messages.JDBC.ResultsImpl_The_cursor_is_not_on_a_valid_row_1));
		}

		checkNotForwardOnly();
		
    	return this.absolute(Math.max(0, getAbsoluteRowNumber() + rows));
	}
	
	/**
	 * <p>
	 * Moves the cursor to the last row in the in this ResultSet object. This
	 * method should be called only if the result set is scrollable.
	 * </p>
	 * 
	 * @return true if the cursor is on a validRow, false otherwise or if no
	 * 	rows exist.
	 * @throws SQLException
	 * 		if the type of the ResultSet is TYPE_FORWARD_ONLY
	 */
	@Override
    public boolean last() throws SQLException {
		checkNotForwardOnly();
		
    	return absolute(-1);
	}

	protected void checkNotForwardOnly() throws SQLException {
		if (getType() == ResultSet.TYPE_FORWARD_ONLY) {
			String msg = Messages
					.getString(Messages.JDBC.ResultsImpl_Op_invalid_fwd_only);
			throw new SQLException(msg);
		}
	}

	/**
	 * <p>
	 * Moves the cursor to the end of the result set, just after the last row.
	 * Has no effect if the result set contains no rows.
	 * </p>
	 * 
	 * @throws SQLException
	 * 		if a results access error occurs or the result set type is
	 * 		TYPE_FORWARD_ONLY
	 */
	@Override
    public void afterLast() throws SQLException {
		if (last()) {
    		next();
    	}
	}

	/**
	 * <p>
	 * Moves the cursor to the front of the result set, just before the first
	 * row. Has no effect if the result set contains no rows.
	 * </p>
	 * 
	 * @exception SQLException
	 * 		if a results can not be accessed or the result set type is
	 * 		TYPE_FORWARD_ONLY
	 */
	@Override
    public void beforeFirst() throws SQLException {
		if (first()) {
    		previous();
    	}
	}
	
	/**
	 * <p>
	 * Moves the cursor to the first row in this ResultSet object.
	 * </p>
	 * 
	 * @return true if the cursor is on valid row, false if there are no rows in
	 * 	the resultset.
	 * @throws SQLException
	 * 		if the ResulSet is of TYPE_FORWARD_ONLY.
	 */
	@Override
    public boolean first() throws SQLException {
		checkNotForwardOnly();
		return absolute(1);
	}

	@Override
    public int findColumn(String columnName) throws SQLException {
		checkClosed();

		// get the column index using ResultsMetadata object
		return findColumnIndex(columnName);
	}

	protected int findColumnIndex(String columnName) throws SQLException {
		if (this.columnMap == null) {
			columnMap = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
			int colCount = getMetaData().getColumnCount();
			for (int i = 1; i <= colCount; i++) {
				columnMap.put(getMetaData().getColumnLabel(i), i);
			}
		}
		Integer index = columnMap.get(columnName);
		if (index != null) {
			return index;
		}
		String msg = Messages.getString(
				Messages.JDBC.MMResultsImpl_Col_doesnt_exist, columnName);
		throw new SQLException(msg);
	}
		
	protected Calendar getDefaultCalendar() {
		return statement.getDefaultCalendar();
	}

	@Override
    public void deleteRow() throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public Array getArray(int columnIndex) throws SQLException {
		checkSupportedVersion();
		return DataTypeTransformer.getArray(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public Array getArray(String columnLabel) throws SQLException {
		checkSupportedVersion();
		return DataTypeTransformer.getArray(getTeiidVersion(), getObject(columnLabel));
	}

	@Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
		checkSupportedVersion();
		return DataTypeTransformer.getAsciiStream(getTeiidVersion(), getObject(columnIndex)); 	
	}

	@Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
		checkSupportedVersion();
		return getAsciiStream(findColumn(columnLabel));	
	}

	@Override
    public Clob getClob(int columnIndex) throws SQLException {
		return DataTypeTransformer.getClob(getTeiidVersion(), getObject(columnIndex));
	}
	
	@Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return DataTypeTransformer.getSQLXML(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public Clob getClob(String columnLabel) throws SQLException {
		return getClob(findColumn(columnLabel));
	}
	
	@Override
    public int getHoldability() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
		checkSupportedVersion();
		return getCharacterStream(columnIndex);
	}

	@Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
		checkSupportedVersion();
		return getCharacterStream(columnLabel);
	}

	@Override
    public NClob getNClob(int columnIndex) throws SQLException {
		checkSupportedVersion();
		return DataTypeTransformer.getNClob(getTeiidVersion(), getObject(columnIndex));
	}

	@Override
    public NClob getNClob(String columnLabel) throws SQLException {
		checkSupportedVersion();
		return getNClob(findColumn(columnLabel));
	}
	
	@Override
    public String getNString(int columnIndex) throws SQLException {
		checkSupportedVersion();
		return getString(columnIndex);
	}

	@Override
    public String getNString(String columnLabel) throws SQLException {
		checkSupportedVersion();
		return getString(columnLabel);
	}
	
	@Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public Ref getRef(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public Ref getRef(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public RowId getRowId(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public RowId getRowId(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
		checkSupportedVersion();
		return getSQLXML(findColumn(columnLabel));
	}

	@Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();	
	}
	
	@Override
    public URL getURL(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public URL getURL(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void insertRow() throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void moveToInsertRow() throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void refreshRow() throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public boolean rowDeleted() throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public boolean rowInserted() throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public boolean rowUpdated() throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void setFetchDirection(int direction) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void setFetchSize(int rows) throws SQLException {
		checkClosed();
        if ( rows < 0 ) {
            throw new SQLException(Messages.getString(Messages.JDBC.MMStatement_Invalid_fetch_size));
        }
        // sets the fetch size on this statement
        if (rows == 0) {
            this.fetchSize = RequestMessage.DEFAULT_FETCH_SIZE;
        } else {
        	if (disableFetchSize == null) {
        		this.disableFetchSize = PropertiesUtils.getBooleanProperty(statement.getConnection().getConnectionProps(), DISABLE_FETCH_SIZE, DISABLE_FETCH_SIZE_DEFAULT);
        	}
        	if (disableFetchSize == null || !disableFetchSize) {
                this.fetchSize = rows;
            }
	    }
	}

	@Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}
	
	@Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length)
		throws SQLException {
		throw new UnsupportedOperationException();	
	}
	
	@Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateAsciiStream(int columnIndex, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateAsciiStream(String columnLabel, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBigDecimal(int columnIndex, BigDecimal x)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateBigDecimal(String columnLabel, BigDecimal x)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}
	
	@Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length)
		throws SQLException {
		throw new UnsupportedOperationException();	
	}
	

	@Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBinaryStream(int columnIndex, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBinaryStream(String columnLabel, InputStream x,
			long length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBinaryStream(String columnLabel, InputStream x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
		throw new UnsupportedOperationException();	
	}
	
	@Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateBlob(int columnIndex, InputStream inputStream)
			throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
    public void updateBlob(String columnLabel, InputStream inputStream,
			long length) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
    public void updateBlob(String columnLabel, InputStream inputStream)
			throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	@Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateBoolean(String columnLabel, boolean x)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	
	@Override
    public void updateCharacterStream(int columnIndex, Reader x, int length)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}
	
	@Override
    public void updateCharacterStream(String columnLabel, Reader reader,
			int length) throws SQLException {
		throw new UnsupportedOperationException();	
	}	

	@Override
    public void updateCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
		throw new UnsupportedOperationException();	
	}
	
	@Override
    public void updateClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateClob(String columnLabel, Reader reader, long length)
		throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateClob(String columnLabel, Reader reader)
		throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
		throw new UnsupportedOperationException();	
	}		

	@Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateInt(int columnIndex, int x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateInt(String columnLabel, int x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateLong(int columnIndex, long x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateLong(String columnLabel, long x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
    public void updateNCharacterStream(int columnIndex, Reader x)
			throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
    public void updateNCharacterStream(String columnLabel, Reader reader,
			long length) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNCharacterStream(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();
		
	}

	@Override
    public void updateNClob(int columnIndex, NClob clob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNClob(int columnIndex, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNClob(String columnLabel, NClob clob) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNClob(String columnLabel, Reader reader, long length)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNClob(String columnLabel, Reader reader)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNString(int columnIndex, String string)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNString(String columnLabel, String string)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateNull(int columnIndex) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateNull(String columnLabel) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateRow() throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateShort(int columnIndex, short x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateShort(String columnLabel, short x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void updateString(int columnIndex, String x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateString(String columnLabel, String x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateTimestamp(int columnIndex, Timestamp x)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void updateTimestamp(String columnLabel, Timestamp x)
			throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		return DataTypeTransformer.transform(getTeiidVersion(), getObject(columnIndex), type);
	}

	@Override
    public <T> T getObject(String columnLabel, Class<T> type)
			throws SQLException {
		return DataTypeTransformer.transform(getTeiidVersion(), getObject(columnLabel), type);
	}
	
	ResultsFuture<ResultsMessage> getPrefetch() {
		return prefetch;
	}

}
