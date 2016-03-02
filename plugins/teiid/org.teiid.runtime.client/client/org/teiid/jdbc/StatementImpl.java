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

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teiid.client.DQP;
import org.teiid.client.RequestMessage;
import org.teiid.client.RequestMessage.ResultsMode;
import org.teiid.client.RequestMessage.ShowPlan;
import org.teiid.client.ResultsMessage;
import org.teiid.client.metadata.ParameterInfo;
import org.teiid.client.metadata.ResultsMetadataConstants;
import org.teiid.client.plan.Annotation;
import org.teiid.client.plan.PlanNode;
import org.teiid.client.util.ResultsFuture;
import org.teiid.core.types.DataTypeManagerService;
import org.teiid.core.types.DataTypeManagerService.DefaultDataTypes;
import org.teiid.core.types.JDBCSQLTypeInfo;
import org.teiid.core.types.SQLXMLImpl;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.query.sql.lang.ISPParameter;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.jdbc.EnhancedTimer.Task;
import org.teiid.net.TeiidURL;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidRuntimePlugin;


public class StatementImpl extends WrapperImpl implements TeiidStatement {
	private static Logger logger = Logger.getLogger("org.teiid.jdbc"); //$NON-NLS-1$
	
	static EnhancedTimer cancellationTimer = new EnhancedTimer("Teiid Statement Timeout"); //$NON-NLS-1$
	
	private static final class QueryTimeoutCancelTask implements Runnable {
		private WeakReference<StatementImpl> ref;
		private QueryTimeoutCancelTask(StatementImpl stmt) {
			this.ref = new WeakReference<StatementImpl>(stmt);
		}

		@Override
		public void run() {
			StatementImpl stmt = ref.get();
			if (stmt != null) {
				stmt.timeoutOccurred();
			}
		}
	}

	enum State {
		RUNNING,
		DONE,
		TIMED_OUT,
		CANCELLED
	}

	public static final String USE_CALLING_THREAD = "useCallingThread"; //$NON-NLS-1$

    protected static final int NO_TIMEOUT = 0;

    // integer indicating no maximum limit - used in some metadata-ish methods.
    private static final int NO_LIMIT = 0;
    
    private QueryTimeoutCancelTask cancelTask = new QueryTimeoutCancelTask(this);

    //######## Configuration state #############
    private ConnectionImpl driverConnection;
    private Properties execProps;

    // fetch size value. This is the default fetch size used by the server
    private int fetchSize = RequestMessage.DEFAULT_FETCH_SIZE;

    // the fetch direction
    private int fetchDirection = ResultSet.FETCH_FORWARD;

    // the result set type
    private int resultSetType = ResultSet.TYPE_FORWARD_ONLY;
    private int resultSetConcurrency = ResultSet.CONCUR_READ_ONLY;

    //######## Processing state #############

    // boolean to indicate if this statement object is closed
    private boolean isClosed = false;

    // Differentiate timeout from cancel in blocking asynch operation
    protected volatile State commandStatus = State.RUNNING;

    // number of seconds for the query to timeout.
    protected long queryTimeoutMS = NO_TIMEOUT;

    //########## Per-execution state ########

    // ID for current request
    protected long currentRequestID = -1;

    //  the last query plan description
    private PlanNode currentPlanDescription;

    // the last query debug log
    private String debugLog;

    // the last query annotations
    private Collection<Annotation> annotations;

    // resultSet object produced by execute methods on the statement.
    protected volatile ResultSetImpl resultSet;

    private List<Throwable> serverWarnings;

    // the per-execution security payload
    private Serializable payload;
    
    /** List of INSERT, UPDATE, DELETE AND SELECT INTO commands */
    private List<String> batchedUpdates;
    
    /** Array of update counts as returned by executeBatch() */
    protected int[] updateCounts;
    
    /** default Calendar instance for converting date/time/timestamp values */
    private Calendar defaultCalendar;
    /** Max rows to be returned by executing the statement */
    private int maxRows = NO_LIMIT;
    private int maxFieldSize = NO_LIMIT;
    
    //Map<out/inout/return param index --> index in results>
    protected Map<Integer, Integer> outParamIndexMap = new HashMap<Integer, Integer>();
    protected Map<String, Integer> outParamByName = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);

    private boolean closeOnCompletion;

    static Pattern TRANSACTION_STATEMENT = Pattern.compile("\\s*(commit|rollback|(start\\s+transaction))\\s*;?\\s*", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
    static Pattern SET_STATEMENT = Pattern.compile("\\s*set(?:\\s+(payload))?\\s+((?:session authorization)|(?:[a-zA-Z]\\w*))\\s+(?:to\\s+)?((?:[^\\s]*)|(?:'[^']*')+)\\s*;?\\s*", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    @Since(Version.TEIID_8_10)
    static Pattern SET_CHARACTERISTIC_STATEMENT = Pattern.compile("\\s*set\\s+session\\s+characteristics\\s+as\\s+transaction\\s+isolation\\s+level\\s+((?:read\\s+(?:(?:committed)|(?:uncommitted)))|(?:repeatable\\s+read)|(?:serializable))\\s*", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$

    static Pattern SHOW_STATEMENT = Pattern.compile("\\s*show\\s+([a-zA-Z]\\w*)\\s*;?\\s*?", Pattern.CASE_INSENSITIVE); //$NON-NLS-1$
    
    /**
     * MMStatement Constructor.
     * @param driverConnection
     * @param resultSetType
     * @param resultSetConcurrency
     */
    StatementImpl(ConnectionImpl driverConnection, int resultSetType, int resultSetConcurrency) {
        super(driverConnection.getTeiidVersion());
        this.driverConnection = driverConnection;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.execProps = new Properties(this.driverConnection.getExecutionProperties());
        
        // Set initial fetch size
        String fetchSizeStr = this.execProps.getProperty(ExecutionProperties.PROP_FETCH_SIZE);
        if(fetchSizeStr != null) {
            try {
                this.fetchSize = Integer.parseInt(fetchSizeStr);
            } catch(Exception e) {
                // silently failover to default
            }
        }
        setTimeoutFromProperties();
    }

    protected boolean isLessThanTeiidEight() {
        ITeiidServerVersion minVersion = getTeiidVersion().getMinimumVersion();
        return minVersion.isLessThan(Version.TEIID_8_0);
    }

    protected boolean isGreaterThanTeiidSeven() {
        ITeiidServerVersion minVersion = getTeiidVersion().getMinimumVersion();
        return minVersion.isGreaterThan(Version.TEIID_7_7);
    }

    protected void checkSupportedVersion(Version teiidVersion) {
        ITeiidServerVersion minVersion = getTeiidVersion().getMinimumVersion();
        if (minVersion.isLessThan(teiidVersion.get())) { 
            TeiidRuntimePlugin.logError("StatementImpl.checkSupportedVersion", "Method being executed that is not supported in teiid version " + getTeiidVersion());  //$NON-NLS-1$//$NON-NLS-2$
            throw new UnsupportedOperationException();
        }
    }

    protected DataTypeManagerService getDataTypeManager() {
        return DataTypeManagerService.getInstance(driverConnection.getTeiidVersion());
    }

	private void setTimeoutFromProperties() {
		String queryTimeoutStr = this.execProps.getProperty(ExecutionProperties.QUERYTIMEOUT);
        if(queryTimeoutStr != null) {
            try {
                this.queryTimeoutMS = Integer.parseInt(queryTimeoutStr)*1000;
            } catch(Exception e) {
                // silently failover to default
            }
        }
	}

    protected DQP getDQP() {
    	return this.driverConnection.getDQP();
    }
    
    protected ConnectionImpl getMMConnection() {
    	return this.driverConnection;
    }
    
    protected TimeZone getServerTimeZone() throws SQLException {
    	return this.driverConnection.getServerConnection().getLogonResult().getTimeZone();
    }
        
    /**
     * Reset all per-execution state - this should be done before executing
     * a new command.
     */
    protected synchronized void resetExecutionState() throws SQLException {
        this.currentRequestID = -1;

        this.currentPlanDescription = null;
        this.debugLog = null;
        this.annotations = null;

        if ( this.resultSet != null ) {
            ResultSet rs = this.resultSet;
            this.resultSet = null;
            rs.close();
            checkStatement();
        }

        this.serverWarnings = null;
        
        this.batchedUpdates = null;
        this.updateCounts = null;
        this.outParamIndexMap.clear();
        this.outParamByName.clear();
        this.commandStatus = State.RUNNING;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if (batchedUpdates == null) {
            batchedUpdates = new ArrayList<String>();
        }
        batchedUpdates.add(sql);
    }

    @Override
    public synchronized void cancel() throws SQLException {
        /* Defect 19848 - Mark the statement cancelled before sending the CANCEL request.
         * Otherwise, it's possible get into a race where the server response is quicker
         * than the exception in the exception in the conditionalWait(), which results in
         * the statement.executeQuery() call throwing the server's exception instead of the
         * one generated by the conditionalWait() method.
         */
        commandStatus = State.CANCELLED;
        cancelRequest();
    }

    @Override
    public void clearWarnings() throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();

        // clear all the warnings on this statement, after this, getWarnings() should return null
        serverWarnings = null;
    }

    @Override
    public void clearBatch() throws SQLException {
    	if (batchedUpdates != null) {
    		batchedUpdates.clear();
    	}
    }

    @Override
    public void close() throws SQLException {
        if ( isClosed ) {
            return;
        }

        // close the the server's statement object (if necessary)
        if(resultSet != null) {
            ResultSet rs = this.resultSet;
            resultSet = null;
            rs.close();
        }

        isClosed = true;

        // Remove link from connection to statement
        this.driverConnection.closeStatement(this);

        if (logger.isLoggable(Level.FINE)) {
            logger.fine(Messages.getString(Messages.JDBC.MMStatement_Close_stmt_success));
        }
    }

    /**
     * <p> This utility method checks if the jdbc statement is closed and
     * throws an exception if it is closed. </p>
     * @throws SQLException if the statement object is closed.
     */
    protected void checkStatement() throws SQLException {
        //Check to see the connection is closed and proceed if it is not
        driverConnection.checkConnection();
        if ( isClosed ) {
            throw new SQLException(Messages.getString(Messages.JDBC.MMStatement_Stmt_closed));
        }
    }
    
    @Override
    public void submitExecute(String sql, StatementCallback callback, RequestOptions options) throws SQLException {
    	NonBlockingRowProcessor processor = new NonBlockingRowProcessor(this, callback);
    	submitExecute(sql, options).addCompletionListener(processor);
    }
    
    public ResultsFuture<Boolean> submitExecute(String sql, RequestOptions options) throws SQLException {
    	return executeSql(new String[] {sql}, false, ResultsMode.EITHER, false, options);
    }

	@Override
    public boolean execute(String sql) throws SQLException {
	    if (isLessThanTeiidEight()) {
	        executeSql(new String[] {sql}, false, ResultsMode.EITHER, true, null);
            return hasResultSet();
	    }

        return execute(sql, Statement.NO_GENERATED_KEYS);
    }
    
	@Override
    public int[] executeBatch() throws SQLException {
        if (batchedUpdates == null || batchedUpdates.isEmpty()) {
            return new int[0];
        }
        String[] commands = batchedUpdates.toArray(new String[batchedUpdates.size()]);
        executeSql(commands, true, ResultsMode.UPDATECOUNT, true, null);
        return updateCounts;
    }

	@Override
    public ResultSet executeQuery(String sql) throws SQLException {
        executeSql(new String[] {sql}, false, ResultsMode.RESULTSET, true, null);
        return resultSet;
    }

	@Override
    public int executeUpdate(String sql) throws SQLException {
		return executeUpdate(sql, Statement.NO_GENERATED_KEYS);
    }

    protected boolean hasResultSet() throws SQLException {
        return updateCounts == null && resultSet != null && resultSet.getMetaData().getColumnCount() > 0;
    }
    
	protected void createResultSet(ResultsMessage resultsMsg) throws SQLException {
		//create out/return parameter index map if there is any
		List listOfParameters = resultsMsg.getParameters();
		if(listOfParameters != null){
		    //get the size of result set
		    int resultSetSize = 0;
		    Iterator iteratorOfParameters = listOfParameters.iterator();
		    while(iteratorOfParameters.hasNext()){
		        ParameterInfo parameter = (ParameterInfo)iteratorOfParameters.next();
		        if(parameter.getType() == ISPParameter.ParameterInfo.RESULT_SET.index()){
		            resultSetSize = parameter.getNumColumns();
		            //one ResultSet only
		            break;
		        }
		    }

		    //return needs to be the first
		    int index = 0; //index in user call - {?=call sp(?)}
		    int count = 0;
		    iteratorOfParameters = listOfParameters.iterator();
		    while(iteratorOfParameters.hasNext()){
		        ParameterInfo parameter = (ParameterInfo)iteratorOfParameters.next();
		        if(parameter.getType() == ISPParameter.ParameterInfo.RETURN_VALUE.index()){
		            count++;
		            index++;
		            int resultIndex = resultSetSize + count;
		            outParamIndexMap.put(index, resultIndex);
		            outParamByName.put(resultsMsg.getColumnNames()[resultIndex - 1].toUpperCase(), resultIndex);
		            break;
		        }
		    }

		    iteratorOfParameters = listOfParameters.iterator();
		    while(iteratorOfParameters.hasNext()){
		        ParameterInfo parameter = (ParameterInfo)iteratorOfParameters.next();
		        if(parameter.getType() != ISPParameter.ParameterInfo.RETURN_VALUE.index() && parameter.getType() != ISPParameter.ParameterInfo.RESULT_SET.index()){
		            index++;
		            if(parameter.getType() == ISPParameter.ParameterInfo.OUT.index() || parameter.getType() == ISPParameter.ParameterInfo.INOUT.index()){
		                count++;
		                int resultIndex = resultSetSize + count;
		                outParamIndexMap.put(index, resultIndex);
		                outParamByName.put(resultsMsg.getColumnNames()[resultIndex - 1].toUpperCase(), resultIndex);
		            }
		        }
		    }
		}

		resultSet = new ResultSetImpl(resultsMsg, this, null, outParamIndexMap.size());
		resultSet.setMaxFieldSize(this.maxFieldSize);
	}
    
	protected ResultsFuture<Boolean> executeSql(String[] commands, boolean isBatchedCommand, ResultsMode resultsMode, boolean synch, RequestOptions options)
	        throws SQLException {
		return executeSql(commands, isBatchedCommand, resultsMode, synch, options, false);
	}

	@SuppressWarnings("unchecked")
	protected ResultsFuture<Boolean> executeSql(String[] commands, boolean isBatchedCommand, ResultsMode resultsMode, boolean synch, RequestOptions options, boolean autoGenerateKeys)
        throws SQLException {
        checkStatement();
        resetExecutionState();
        if (options != null) {
        	if (options.isContinuous()) {
        		if (!this.driverConnection.getServerConnection().supportsContinuous()) {
	                throw new SQLException(Messages.getString(Messages.JDBC.continuous));
        		}
        		if (this.getResultSetType() != ResultSet.TYPE_FORWARD_ONLY) {
	        		String msg = Messages.getString(Messages.JDBC.forward_only_resultset);
	                throw new SQLException(msg);
        		}
        		if (resultsMode == ResultsMode.EITHER) {
        			resultsMode = ResultsMode.RESULTSET;
        		} else if (resultsMode == ResultsMode.UPDATECOUNT) {
	        		String msg = Messages.getString(Messages.JDBC.forward_only_resultset);
	                throw new SQLException(msg);
        		}
        	}
        }
        if (logger.isLoggable(Level.FINER)) {
			logger.finer("Executing: requestID " + getCurrentRequestID() + " commands: " + Arrays.toString(commands) + " expecting: " + resultsMode); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
        if (commands.length == 1) {
        	Matcher match = SET_STATEMENT.matcher(commands[0]);
        	if (match.matches()) {
        		if (resultsMode == ResultsMode.RESULTSET) {
        			throw new SQLException(Messages.getString(Messages.JDBC.StatementImpl_set_result_set));
        		}
        		String val = match.group(2);
                String key = unescapeId(val);
                String value = match.group(3);
                if (value != null && value.startsWith("\'") && value.endsWith("\'")) { //$NON-NLS-1$ //$NON-NLS-2$
                	value = value.substring(1, value.length() - 1);
        			value = StringUtil.replaceAll(value, "''", "'"); //$NON-NLS-1$ //$NON-NLS-2$
        		}
        		if (match.group(1) != null) {
        			//payload case
        			Properties p = this.getMMConnection().getPayload();
        			if (p == null) {
        				p = new Properties();
        				this.getMMConnection().setPayload(p);
        			}
        			p.setProperty(key, value);
        		} else if (val == key && "SESSION AUTHORIZATION".equalsIgnoreCase(key)) { //$NON-NLS-1$
        			this.getMMConnection().changeUser(value, this.getMMConnection().getPassword());
        		} else if (key.equalsIgnoreCase(TeiidURL.CONNECTION.PASSWORD)) {
        			this.getMMConnection().setPassword(value);
        		} else if (ExecutionProperties.NEWINSTANCE.equalsIgnoreCase(key)) {
        			if (Boolean.valueOf(value)) {
        				this.getMMConnection().getServerConnection().cleanUp();
        			}
        		} else {
        			this.driverConnection.setExecutionProperty(key, value);
        		}
        		this.updateCounts = new int[] {0};
        		return booleanFuture(false);
        	}

        	if (getTeiidVersion().isGreaterThanOrEqualTo(Version.TEIID_8_10)) {
                match = SET_CHARACTERISTIC_STATEMENT.matcher(commands[0]);
                if (match.matches()) {
                    String value = match.group(1);
                    if (StringUtil.endsWithIgnoreCase(value, "uncommitted")) { //$NON-NLS-1$
                        this.getMMConnection().setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                    } else if (StringUtil.endsWithIgnoreCase(value, "committed")) { //$NON-NLS-1$
                        this.getMMConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    } else if (StringUtil.startsWithIgnoreCase(value, "repeatable")) { //$NON-NLS-1$
                        this.getMMConnection().setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                    } else if ("serializable".equalsIgnoreCase(value)) { //$NON-NLS-1$
                        this.getMMConnection().setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                    }
                    this.updateCounts = new int[] {0};
                    return booleanFuture(false);
                }
        	}

        	match = TRANSACTION_STATEMENT.matcher(commands[0]);
        	if (match.matches()) {
    			logger.finer("Executing as transaction statement"); //$NON-NLS-1$
        		if (resultsMode == ResultsMode.RESULTSET) {
        			throw new SQLException(Messages.getString(Messages.JDBC.StatementImpl_set_result_set)); 
        		}
        		String command = match.group(1);
        		Boolean commit = null;
        		if (StringUtil.startsWithIgnoreCase(command, "start")) { //$NON-NLS-1$
        			//TODO: this should force a start and through an exception if we're already in a txn
        			this.getConnection().setAutoCommit(false);
        		} else if (command.equalsIgnoreCase("commit")) { //$NON-NLS-1$
        			commit = true;
        			if (synch) {
        				this.getConnection().setAutoCommit(true);
        			}
        		} else if (command.equalsIgnoreCase("rollback")) { //$NON-NLS-1$
        			commit = false;
        			if (synch || !this.getConnection().isInLocalTxn()) {
        				this.getConnection().rollback(false);
        			}
        		}
        		this.updateCounts = new int[] {0};
        		if (commit != null && !synch) {
        			ResultsFuture<?> pending = this.getConnection().submitSetAutoCommitTrue(commit);
        			final ResultsFuture<Boolean> result = new ResultsFuture<Boolean>();
        			pending.addCompletionListener(new ResultsFuture.CompletionListener() {
        				@Override
        				public void onCompletion(ResultsFuture future) {
        					try {
								future.get();
								result.getResultsReceiver().receiveResults(false);
							} catch (Throwable t) {
								result.getResultsReceiver().exceptionOccurred(t);
							}
        				}
					});
        			return result;
        		}
        		return booleanFuture(false);
        	}
        	match = SHOW_STATEMENT.matcher(commands[0]);
        	if (match.matches()) {
    			logger.finer("Executing as show statement"); //$NON-NLS-1$
        		if (resultsMode == ResultsMode.UPDATECOUNT) {
        			throw new SQLException(Messages.getString(Messages.JDBC.StatementImpl_show_update_count)); 
        		}
        		return executeShow(match);
        	}
        }
        
        final RequestMessage reqMessage = createRequestMessage(commands, isBatchedCommand, resultsMode);
        reqMessage.setReturnAutoGeneratedKeys(autoGenerateKeys);
        reqMessage.setRequestOptions(options);
    	ResultsFuture<ResultsMessage> pendingResult = execute(reqMessage, synch);
    	final ResultsFuture<Boolean> result = new ResultsFuture<Boolean>();
    	pendingResult.addCompletionListener(new ResultsFuture.CompletionListener<ResultsMessage>() {
    		@Override
    		public void onCompletion(ResultsFuture<ResultsMessage> future) {
    			try {
					postReceiveResults(reqMessage, future.get());
					result.getResultsReceiver().receiveResults(hasResultSet());
				} catch (Throwable t) {
					result.getResultsReceiver().exceptionOccurred(t);
				}
    		}
		});
    	if (synch) {
    		try {
				pendingResult.get(queryTimeoutMS==0?Integer.MAX_VALUE:queryTimeoutMS, TimeUnit.MILLISECONDS);
    			result.get(); //throw an exception if needed
    			return result;
    		} catch (ExecutionException e) {
    			if (e.getCause() instanceof SQLException) {
    				throw (SQLException)e.getCause();
    			}
    			if (e.getCause() != null) {
        			throw new SQLException(e.getCause());
    			}
    			throw new SQLException(e);
    		} catch (InterruptedException e) {
    			timeoutOccurred();
			} catch (TimeoutException e) {
    			timeoutOccurred();
			}
    		throw new SQLException(Messages.getString(Messages.JDBC.MMStatement_Timeout_before_complete)); 
    	}
    	return result;
    }

	private String unescapeId(String key) {
        if (key.startsWith("\"")) { //$NON-NLS-1$
            key = key.substring(1, key.length() - 1);
            key = StringUtil.replaceAll(key, "\"\"", "\""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return key;
    }

	ResultsFuture<Boolean> executeShow(Matcher match)
			throws SQLException {
		String show = match.group(1);
		show = unescapeId(show);
		if (show.equalsIgnoreCase("PLAN")) { //$NON-NLS-1$
			List<ArrayList<Object>> records = new ArrayList<ArrayList<Object>>(1);
			PlanNode plan = driverConnection.getCurrentPlanDescription();
			String connDebugLog = driverConnection.getDebugLog();
			if (plan != null || connDebugLog != null) {
				ArrayList<Object> row = new ArrayList<Object>(3);
				if (plan != null) {
        			row.add(DataTypeTransformer.getClob(getTeiidVersion(), plan.toString()));
    				row.add(new SQLXMLImpl(plan.toXml()));
				} else {
					row.add(null);
					row.add(null);
				}
				row.add(DataTypeTransformer.getClob(getTeiidVersion(), connDebugLog));
				records.add(row);
			}
			createResultSet(records, new String[] {"PLAN_TEXT", "PLAN_XML", "DEBUG_LOG"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					new String[] { DataTypeManagerService.DefaultDataTypes.CLOB.getId(),
			                                DataTypeManagerService.DefaultDataTypes.XML.getId(),
			                                DataTypeManagerService.DefaultDataTypes.CLOB.getId()});
			return booleanFuture(true);
		}
		if (show.equalsIgnoreCase("ANNOTATIONS")) { //$NON-NLS-1$
			List<ArrayList<Object>> records = new ArrayList<ArrayList<Object>>(1);
			Collection<Annotation> annos = driverConnection.getAnnotations();
			for (Annotation annotation : annos) {
				ArrayList<Object> row = new ArrayList<Object>(4);
				row.add(annotation.getCategory());
				row.add(annotation.getPriority().name());
				row.add(annotation.getAnnotation());
				row.add(annotation.getResolution());
				records.add(row);
			}
			createResultSet(records, new String[] {"CATEGORY", "PRIORITY", "ANNOTATION", "RESOLUTION"}, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					new String[] { DataTypeManagerService.DefaultDataTypes.STRING.getId(),
			                                DataTypeManagerService.DefaultDataTypes.STRING.getId(),
			                                DataTypeManagerService.DefaultDataTypes.STRING.getId(),
			                                DataTypeManagerService.DefaultDataTypes.STRING.getId()});
			return booleanFuture(true);
		}
		if (show.equalsIgnoreCase("ALL")) { //$NON-NLS-1$
			List<ArrayList<Object>> records = new ArrayList<ArrayList<Object>>(1);
			for (String key : driverConnection.getExecutionProperties().stringPropertyNames()) {
				ArrayList<Object> row = new ArrayList<Object>(4);
				row.add(key);
				row.add(driverConnection.getExecutionProperties().get(key));
				records.add(row);
			}
			createResultSet(records, new String[] {"NAME", "VALUE"}, //$NON-NLS-1$ //$NON-NLS-2$
					new String[] { DataTypeManagerService.DefaultDataTypes.STRING.getId(),
			                                DataTypeManagerService.DefaultDataTypes.STRING.getId()});
			return booleanFuture(true);
		}
		List<List<String>> records = Collections.singletonList(Collections.singletonList(driverConnection.getExecutionProperty(show)));
		createResultSet(records, new String[] {show}, new String[] {DataTypeManagerService.DefaultDataTypes.STRING.getId()});
		return booleanFuture(true);
	}

	private ResultsFuture<ResultsMessage> execute(final RequestMessage reqMsg, boolean synch) throws SQLException,
			SQLException {
		this.getConnection().beginLocalTxnIfNeeded();
        this.currentRequestID = this.driverConnection.nextRequestID();
        // Create a request message
        if (this.payload != null) {
        	reqMsg.setExecutionPayload(this.payload);        
        } else {
        	reqMsg.setExecutionPayload(this.getMMConnection().getPayload());
        }
        reqMsg.setDelaySerialization(true);
        reqMsg.setCursorType(this.resultSetType);
        reqMsg.setFetchSize(this.fetchSize);
        reqMsg.setRowLimit(this.maxRows);
        reqMsg.setTransactionIsolation(this.driverConnection.getTransactionIsolation());
        reqMsg.setSync(synch && useCallingThread());
        // Get connection properties and set them onto request message
        copyPropertiesToRequest(reqMsg);

        reqMsg.setExecutionId(this.currentRequestID);
        
        ResultsFuture.CompletionListener<ResultsMessage> compeletionListener = null;
		if (queryTimeoutMS > 0 && (!synch || this.driverConnection.getServerConnection().isLocal())) {
			final Task c = cancellationTimer.add(cancelTask, queryTimeoutMS);
			compeletionListener = new ResultsFuture.CompletionListener<ResultsMessage>() {
				@Override
				public void onCompletion(ResultsFuture<ResultsMessage> future) {
					c.cancel();
				}
			};
		} 
        
    	ResultsFuture<ResultsMessage> pendingResult = null;
		try {
			pendingResult = this.getDQP().executeRequest(this.currentRequestID, reqMsg);
		} catch (Exception e) {
			throw new SQLException(e);
		}
		if (compeletionListener != null) {
			pendingResult.addCompletionListener(compeletionListener);
		}
    	return pendingResult;
    }
	
	boolean useCallingThread() throws SQLException {
		if (this.getConnection().getServerConnection() == null || !this.getConnection().getServerConnection().isLocal()) {
			return false;
		}
		String useCallingThread = getExecutionProperty(USE_CALLING_THREAD);
		return (useCallingThread == null || Boolean.valueOf(useCallingThread));
	}

	public static ResultsFuture<Boolean> booleanFuture(boolean isTrue) {
		ResultsFuture<Boolean> rs = new ResultsFuture<Boolean>();
		rs.getResultsReceiver().receiveResults(isTrue);
		return rs;
	}
	
	private synchronized void postReceiveResults(RequestMessage reqMessage,
			ResultsMessage resultsMsg) throws SQLException {
		commandStatus = State.DONE;
		// warnings thrown
        List resultsWarning = resultsMsg.getWarnings();

        /**
         * Set the teiid version on the results message
         */
        resultsMsg.setTeiidVersion(getTeiidVersion());

        setAnalysisInfo(resultsMsg);

        if (resultsMsg.getException() != null) {
            throw new SQLException(resultsMsg.getException());
        }

        // save warnings if have any
        if (resultsWarning != null) {
            accumulateWarnings(resultsWarning);
        }

        resultsMsg.processResults();

        if (resultsMsg.isUpdateResult()) {
        	List<? extends List<?>> results = resultsMsg.getResultsList();
        	if (resultsMsg.getUpdateCount() == -1) { 
            	this.updateCounts = new int[results.size()];
                for (int i = 0; i < results.size(); i++) {
                	updateCounts[i] = (Integer)results.get(i).get(0);
                }
        	} else {
        		this.updateCounts = new int[] {resultsMsg.getUpdateCount()};
        		this.createResultSet(resultsMsg);
        	}
            if (logger.isLoggable(Level.FINER)) {
            	logger.finer("Recieved update counts: " + Arrays.toString(updateCounts)); //$NON-NLS-1$
            }
            // In update scenarios close the statement implicitly
            try {
				getDQP().closeRequest(getCurrentRequestID());
			} catch (Exception e) {
				throw new SQLException(e);
			}            
        } else {
            createResultSet(resultsMsg);
        }
        
        logger.fine(Messages.getString(Messages.JDBC.MMStatement_Success_query, reqMessage.getCommandString())); 
	}

	protected RequestMessage createRequestMessage(String[] commands,
			boolean isBatchedCommand, ResultsMode resultsMode) {
        RequestMessage reqMessage = new RequestMessage();
    	reqMessage.setCommands(commands);
    	reqMessage.setBatchedUpdate(isBatchedCommand);
    	reqMessage.setResultsMode(resultsMode);
		return reqMessage;
	}

    @Override
    public int getFetchDirection() throws SQLException {
        return this.fetchDirection;
    }

    @Override
    public int getFetchSize() throws SQLException {
        return fetchSize;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return maxFieldSize;
    }

    @Override
    public int getMaxRows() throws SQLException {
        return maxRows;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();

        // close any current ResultSet
        if ( resultSet != null ) {
            resultSet.close();
            resultSet = null;
        }
        // set the existing update count to -1
        // indicating that there are no more results
        this.updateCounts = null;
        return false;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        checkStatement();

        /*if (current == CLOSE_ALL_RESULTS || current == CLOSE_CURRENT_RESULT) {
            // since MetaMatrix only supports one ResultSet per statement,
            // these two cases are handled the same way.
            if (resultSet != null) {
                resultSet.close();
            }
        } else if (current == KEEP_CURRENT_RESULT) {
            // do nothing
        }

        rowsAffected = -1;
        */
        return false;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        return (int)this.queryTimeoutMS/1000;
    }

    @Override
    public ResultSetImpl getResultSet() throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if (!hasResultSet()) {
        	return null;
        }
        return resultSet;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.resultSetConcurrency;
    }

    @Override
    public int getResultSetType() {
        return this.resultSetType;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        checkStatement();
        if (this.updateCounts == null) {
        	return -1;
        }
        return this.updateCounts[0];
    }

    protected void accumulateWarnings(List<Throwable> serverWarnings) {
    	if (serverWarnings == null || serverWarnings.isEmpty()) {
    		return;
    	}
    	if (this.serverWarnings == null) {
    		this.serverWarnings = new ArrayList<Throwable>();
    	}
    	this.serverWarnings.addAll(serverWarnings);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();

        if (serverWarnings != null && serverWarnings.size() != 0) {
            return WarningUtil.convertWarnings(serverWarnings);
        }
        return null;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        // do nothing, escape processing is always enabled.
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        checkStatement();
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if ( rows < 0 ) {
            String msg = Messages.getString(Messages.JDBC.MMStatement_Invalid_fetch_size); 
            throw new SQLException(msg);
        }
        // sets the fetch size on this statement
        if (rows == 0) {
            this.fetchSize = RequestMessage.DEFAULT_FETCH_SIZE;
        } else {
            this.fetchSize = rows;
        }
    }

    @Override
    public void setMaxRows(int maxRows) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if (maxRows < 0 || maxRows == Integer.MAX_VALUE) {
        	maxRows = 0;
        }
        this.maxRows = maxRows;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        //Check to see the statement is closed and throw an exception
        checkStatement();
        if (seconds >= 0) {
            queryTimeoutMS = seconds*1000;
        }
        else {
            throw new SQLException(Messages.getString(Messages.JDBC.MMStatement_Bad_timeout_value));
        }
    }
    
    void setQueryTimeoutMS(int queryTimeoutMS) {
		this.queryTimeoutMS = queryTimeoutMS;
	}

    /**
     * Helper method for copy the connection properties to request message.
     * @param res Request message that these properties to be copied to.
     * @throws SQLException 
     */
    protected void copyPropertiesToRequest(RequestMessage res) throws SQLException {
        // Get partial mode
        String partial = getExecutionProperty(ExecutionProperties.PROP_PARTIAL_RESULTS_MODE);
        res.setPartialResults(Boolean.valueOf(partial).booleanValue());

        // Get xml validation mode
        String validate = getExecutionProperty(ExecutionProperties.PROP_XML_VALIDATION);
        if(validate == null) {
            res.setValidationMode(false);
        } else {
            res.setValidationMode(Boolean.valueOf(validate).booleanValue());
        }

        // Get xml format mode
        String format = getExecutionProperty(ExecutionProperties.PROP_XML_FORMAT);
        res.setXMLFormat(format);

        // Get transaction auto-wrap mode
        String txnAutoWrapMode = getExecutionProperty(ExecutionProperties.PROP_TXN_AUTO_WRAP);
        try {
			res.setTxnAutoWrapMode(txnAutoWrapMode);
		} catch (Exception e) {
			throw new SQLException(e);
		}
        
        // Get result set cache mode
        String rsCache = getExecutionProperty(ExecutionProperties.RESULT_SET_CACHE_MODE);
        res.setUseResultSetCache(Boolean.valueOf(rsCache).booleanValue());
        
        res.setAnsiQuotedIdentifiers(Boolean.valueOf(
                getExecutionProperty(ExecutionProperties.ANSI_QUOTED_IDENTIFIERS))
                .booleanValue());
        String showPlan = getExecutionProperty(ExecutionProperties.SQL_OPTION_SHOWPLAN);
        if (showPlan != null) {
        	try {
        		res.setShowPlan(ShowPlan.valueOf(showPlan.toUpperCase()));
        	} catch (IllegalArgumentException e) {
        		
        	}
        }
        String noExec = getExecutionProperty(ExecutionProperties.NOEXEC);
        if (noExec != null) {
    		res.setNoExec(noExec.equalsIgnoreCase("ON")); //$NON-NLS-1$
        }
    }

    /**
     * Ends the command and sets the status to TIMED_OUT.
     */
    protected synchronized void timeoutOccurred() {
    	if (this.commandStatus != State.RUNNING) {
    		return;
    	}
        logger.warning(Messages.getString(Messages.JDBC.MMStatement_Timeout_ocurred_in_Statement)); 
        try {
        	cancel();        
            commandStatus = State.TIMED_OUT;
            queryTimeoutMS = NO_TIMEOUT;
            setTimeoutFromProperties();
            currentRequestID = -1;
            if (this.resultSet != null) {
                this.resultSet.close();
            }
        } catch (SQLException se) {
            logger.log(Level.SEVERE, Messages.getString(Messages.JDBC.MMStatement_Error_timing_out), se);
        }
    }

    protected void cancelRequest() throws SQLException {
        checkStatement();

        try {
			this.getDQP().cancelRequest(currentRequestID);
		} catch (Exception e) {
			throw new SQLException(e);
		}
    }

    @Override
    public void setPayload(Serializable payload) {
        this.payload = payload;
    }

    @Override
    public void setExecutionProperty(String name, String value) {
        this.execProps.setProperty(name, value);
    }

    @Override
    public String getExecutionProperty(String name) {
        return this.execProps.getProperty(name);
    }

    long getCurrentRequestID() {
        return this.currentRequestID;
    }

    @Override
    public PlanNode getPlanDescription() {
        if(this.resultSet != null) {
			return this.resultSet.getUpdatedPlanDescription();
        }
        if(currentPlanDescription != null) {
            return this.currentPlanDescription;
        }
        return null;
    }

    @Override
    public String getDebugLog() {
        return this.debugLog;
    }

    @Override
    public Collection<Annotation> getAnnotations() {
        return this.annotations;
    }
    
    @Override
    public String getRequestIdentifier() {
        if(this.currentRequestID >= 0) {
            return Long.toString(this.currentRequestID);
        }
        return null;
    }
    
    @Override
    public boolean isClosed() {
        return this.isClosed;
    }

	protected void setAnalysisInfo(ResultsMessage resultsMsg) {
		if (resultsMsg.getDebugLog() != null) {
			this.debugLog = resultsMsg.getDebugLog();
		}
		if (resultsMsg.getPlanDescription() != null) {
			this.currentPlanDescription = resultsMsg.getPlanDescription();
		}
		if (resultsMsg.getAnnotations() != null) {
			this.annotations = resultsMsg.getAnnotations();
		}
        this.driverConnection.setDebugLog(debugLog);
        this.driverConnection.setCurrentPlanDescription(currentPlanDescription);
        this.driverConnection.setAnnotations(annotations);
	}
    
    Calendar getDefaultCalendar() {
        if (defaultCalendar == null) {
            defaultCalendar = Calendar.getInstance();
        }
        return defaultCalendar;
    }
    
    void setDefaultCalendar(Calendar cal) {
        this.defaultCalendar = cal;
    }
            
	@Override
    public boolean isPoolable() throws SQLException {
		checkStatement();
		return false;
	}

	@Override
    public void setPoolable(boolean arg0) throws SQLException {
		checkStatement();
	}
	
	@Override
    public ConnectionImpl getConnection() throws SQLException {
		return this.driverConnection;
	}

	@Override
    public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		executeSql(new String[] {sql}, false, ResultsMode.EITHER, true, null, autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS);
        return hasResultSet();
	}

	@Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return execute(sql, Statement.RETURN_GENERATED_KEYS);	
	}

	@Override
    public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return execute(sql, Statement.RETURN_GENERATED_KEYS);	
	}

	@Override
    public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
        executeSql(new String[] {sql}, false, ResultsMode.UPDATECOUNT, true, null, autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS);
        if (this.updateCounts == null) {
        	return 0;
        }
        return this.updateCounts[0];
	}

	@Override
    public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);	
	}

	@Override
    public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);	
	}

	@Override
    public ResultSet getGeneratedKeys() throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		if (this.updateCounts != null && this.resultSet != null) {
			return this.resultSet;
		}
		return createResultSet(Collections.emptyList(), new Map[0]);
	}

	@Override
    public int getResultSetHoldability() throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void setCursorName(String name) throws SQLException {
		throw new UnsupportedOperationException();	
	}

	@Override
    public void setMaxFieldSize(int max) throws SQLException {
		checkStatement();
        if ( max < 0 ) {
            throw new SQLException(Messages.getString(Messages.JDBC.MMStatement_Invalid_field_size, max)); 
        }
		this.maxFieldSize = max;
	}
	
	ResultSetImpl createResultSet(List records, String[] columnNames, String[] dataTypes) throws SQLException {
        Map[] metadata = new Map[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            metadata[i] = getColumnMetadata(null, columnNames[i], dataTypes[i], ResultsMetadataConstants.NULL_TYPES.UNKNOWN, driverConnection);
        }
		return createResultSet(records, metadata);
	}
	
    ResultSetImpl createResultSet(List records, Map[] columnMetadata) throws SQLException {
        ResultSetMetaData rsmd = new ResultSetMetaDataImpl(getTeiidVersion(), new MetadataProvider(columnMetadata), this.getExecutionProperty(ExecutionProperties.JDBC4COLUMNNAMEANDLABELSEMANTICS));

        return createResultSet(records, rsmd);
    }

    ResultSetImpl createResultSet(List records, ResultSetMetaData rsmd) throws SQLException {
    	if (rsmd.getColumnCount() > 0) {
    		rsmd.getScale(1); //force the load of the metadata
    	}
        ResultsMessage resultsMsg = createDummyResultsMessage(null, null, records);
		resultSet = new ResultSetImpl(resultsMsg, this, rsmd, 0);
		resultSet.setMaxFieldSize(this.maxFieldSize);
        return resultSet;
    }

    static ResultsMessage createDummyResultsMessage(String[] columnNames, String[] dataTypes, List records) {
        ResultsMessage resultsMsg = new ResultsMessage();
        resultsMsg.setColumnNames(columnNames);
        resultsMsg.setDataTypes(dataTypes);
        resultsMsg.setFirstRow(1);
        resultsMsg.setLastRow(records.size());
        resultsMsg.setFinalRow(records.size());
        resultsMsg.setResults((List[])records.toArray(new List[records.size()]));
        return resultsMsg;
    }
    
    static Map<Integer, Object> getColumnMetadata(String tableName, String columnName, String dataType, Integer nullable, ConnectionImpl driverConnection) throws SQLException {
        return getColumnMetadata(tableName, columnName, dataType, nullable, ResultsMetadataConstants.SEARCH_TYPES.UNSEARCHABLE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, driverConnection);
	}

    static Map<Integer, Object> getColumnMetadata(String tableName, String columnName, DefaultDataTypes dataType, Integer nullable, ConnectionImpl driverConnection) throws SQLException {
        return getColumnMetadata(tableName, columnName, dataType.getId(), nullable, ResultsMetadataConstants.SEARCH_TYPES.UNSEARCHABLE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, driverConnection);
    }

	static Map<Integer, Object> getColumnMetadata(String tableName, String columnName, String dataType, Integer nullable, Integer searchable, Boolean writable, Boolean signed, Boolean caseSensitive, ConnectionImpl driverConnection) throws SQLException {
	
	    // map that would contain metadata details
	    Map<Integer, Object> metadataMap = new HashMap<Integer, Object>();
	
	    /*******************************************************
	     HardCoding Column metadata details for the given column
	    ********************************************************/
	
	    metadataMap.put(ResultsMetadataConstants.VIRTUAL_DATABASE_NAME, driverConnection.getVDBName());
	    metadataMap.put(ResultsMetadataConstants.GROUP_NAME, tableName);
	    metadataMap.put(ResultsMetadataConstants.ELEMENT_NAME, columnName);
	    metadataMap.put(ResultsMetadataConstants.DATA_TYPE, dataType);
	    metadataMap.put(ResultsMetadataConstants.PRECISION, JDBCSQLTypeInfo.getDefaultPrecision(driverConnection.getTeiidVersion(), dataType));
	    metadataMap.put(ResultsMetadataConstants.RADIX, new Integer(10));
	    metadataMap.put(ResultsMetadataConstants.SCALE, new Integer(0));
	    metadataMap.put(ResultsMetadataConstants.AUTO_INCREMENTING, Boolean.FALSE);
	    metadataMap.put(ResultsMetadataConstants.CASE_SENSITIVE, caseSensitive);
	    metadataMap.put(ResultsMetadataConstants.NULLABLE, nullable);
	    metadataMap.put(ResultsMetadataConstants.SEARCHABLE, searchable);
	    metadataMap.put(ResultsMetadataConstants.SIGNED, signed);
	    metadataMap.put(ResultsMetadataConstants.WRITABLE, writable);
	    metadataMap.put(ResultsMetadataConstants.CURRENCY, Boolean.FALSE);
	    metadataMap.put(ResultsMetadataConstants.DISPLAY_SIZE, JDBCSQLTypeInfo.getMaxDisplaySize(driverConnection.getTeiidVersion(), dataType));
	
	    return metadataMap;
	}

	static Map<Integer, Object> getColumnMetadata(String tableName, String columnName, DefaultDataTypes dataType, Integer nullable, Integer searchable, Boolean writable, Boolean signed, Boolean caseSensitive, ConnectionImpl driverConnection) throws SQLException {
	    return getColumnMetadata(tableName, columnName, dataType.getId(), nullable, searchable, writable, signed, caseSensitive, driverConnection);
	}

	/* Do not override to allow compatibility with jdk 1.6 */
    public void closeOnCompletion() throws SQLException {
		this.closeOnCompletion = true;
	}

    /* Do not override to allow compatibility with jdk 1.6 */
    public boolean isCloseOnCompletion() throws SQLException {
		return closeOnCompletion;
	}
}