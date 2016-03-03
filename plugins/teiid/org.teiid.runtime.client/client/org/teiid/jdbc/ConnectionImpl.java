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

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.xa.Xid;
import org.teiid.client.DQP;
import org.teiid.client.RequestMessage;
import org.teiid.client.plan.Annotation;
import org.teiid.client.plan.PlanNode;
import org.teiid.client.util.ResultsFuture;
import org.teiid.client.xa.XATransactionException;
import org.teiid.client.xa.XidImpl;
import org.teiid.core.types.ArrayImpl;
import org.teiid.core.util.PropertiesUtils;
import org.teiid.designer.annotation.Since;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.net.CommunicationException;
import org.teiid.net.ConnectionException;
import org.teiid.net.ServerConnection;
import org.teiid.net.TeiidURL;
import org.teiid.net.socket.SocketServerConnection;
import org.teiid.runtime.client.Messages;
import org.teiid.runtime.client.TeiidClientException;
import org.teiid.runtime.client.TeiidRuntimePlugin;

/**
 * Teiid's Connection implementation.
 */
public class ConnectionImpl extends WrapperImpl implements TeiidConnection {
	private static Logger logger = Logger.getLogger("org.teiid.jdbc"); //$NON-NLS-1$

	private static final int MAX_OPEN_STATEMENTS = PropertiesUtils.getIntProperty(System.getProperties(), "org.teiid.maxOpenStatements", 1000); //$NON-NLS-1$

	public static final int DEFAULT_ISOLATION = Connection.TRANSACTION_READ_COMMITTED;

	protected static final String DEFAULT_PARTIAL_RESULTS_MODE = "FALSE"; //$NON-NLS-1$
    protected static final String DEFAULT_RESULT_SET_CACHE_MODE = "FALSE"; //$NON-NLS-1$

	// constant value giving product name
    private final static String SERVER_NAME = "Teiid Server"; //$NON-NLS-1$
    private final static String EMBEDDED_NAME = "Teiid Embedded"; //$NON-NLS-1$

    // Unique request ID generator
    private long requestIDGenerator;

    // url used to create the connection
    private String url;
 
    // properties object containing the connection properties.
    protected Properties propInfo;

    // status of connection object
    private boolean closed = false;
    // determines if a statement executed should be immediately committed.
    private boolean autoCommitFlag = true;
    private boolean inLocalTxn;

    // collection of all open statements on this connection
    private Collection<StatementImpl> statements = Collections.newSetFromMap(new ConcurrentHashMap<StatementImpl, Boolean>());
    // cached DatabaseMetadata
    private DatabaseMetaDataImpl dbmm;

   //Xid for participating in TXN
    private XidImpl transactionXid;
            
    //  Flag to represent if the connection state needs to be readOnly, default value false.
    private boolean readOnly = false;

    private DQP dqp;
    protected ServerConnection serverConn;
    private int transactionIsolation = DEFAULT_ISOLATION;
    
    //  the last query plan description
    private PlanNode currentPlanDescription;
    // the last query debug log
    private String debugLog;
    // the last query annotations
    private Collection<Annotation> annotations;
    private Properties connectionProps;
    private Properties payload;

    /**
     * @return teiid version if defined by the connection properties or
     *                the default teiid version for this release.
     */
    private static ITeiidServerVersion deriveTeiidVersion(Properties properties) {
        String teiidVersionString = properties.getProperty(ITeiidServerVersion.TEIID_VERSION_PROPERTY);

        ITeiidServerVersion teiidVersion = null;
        if (teiidVersionString == null)
            teiidVersion = Version.TEIID_DEFAULT.get();
        else
            teiidVersion = new TeiidServerVersion(teiidVersionString);

        return teiidVersion;
    }

    public ConnectionImpl(ServerConnection serverConn, Properties info, String url) {
        super(deriveTeiidVersion(info));
    	this.connectionProps = info;
    	this.serverConn = serverConn;
        this.url = url;
        this.dqp = serverConn.getService(DQP.class);

        if (logger.isLoggable(Level.FINE)) {
            logger.fine(Messages.getString(Messages.JDBC.MMConnection_Session_success));
            logConnectionProperties(url, info);
        }

        setExecutionProperties(info);
    }
    
    boolean isInLocalTxn() {
		return inLocalTxn;
	}
    
	private void setExecutionProperties(Properties info) {
		this.propInfo = new Properties();

        String defaultFetchSize = info.getProperty(ExecutionProperties.PROP_FETCH_SIZE);
        if (defaultFetchSize != null) {
        	propInfo.put(ExecutionProperties.PROP_FETCH_SIZE, defaultFetchSize);
        } else {
        	propInfo.put(ExecutionProperties.PROP_FETCH_SIZE, String.valueOf(RequestMessage.DEFAULT_FETCH_SIZE)); 
        }

        String partialResultsMode = info.getProperty(ExecutionProperties.PROP_PARTIAL_RESULTS_MODE);
        if (partialResultsMode != null) {
        	propInfo.put(ExecutionProperties.PROP_PARTIAL_RESULTS_MODE, partialResultsMode);
        } else {
        	propInfo.put(ExecutionProperties.PROP_PARTIAL_RESULTS_MODE, DEFAULT_PARTIAL_RESULTS_MODE);
        }
        
        String resultSetCacheMode = info.getProperty(ExecutionProperties.RESULT_SET_CACHE_MODE);
        if (resultSetCacheMode != null) {
        	propInfo.put(ExecutionProperties.RESULT_SET_CACHE_MODE, resultSetCacheMode);
        } else {
        	propInfo.put(ExecutionProperties.RESULT_SET_CACHE_MODE, DEFAULT_RESULT_SET_CACHE_MODE);
        }

        String ansiQuotes = info.getProperty(ExecutionProperties.ANSI_QUOTED_IDENTIFIERS);
        if (ansiQuotes != null) {
        	propInfo.put(ExecutionProperties.ANSI_QUOTED_IDENTIFIERS, ansiQuotes);
        } else {
        	propInfo.put(ExecutionProperties.ANSI_QUOTED_IDENTIFIERS, Boolean.TRUE.toString());
        }
                                
        for (String key : info.stringPropertyNames()) {
        	String actualKey = JDBCURL.EXECUTION_PROPERTIES.get(key);
        	if (actualKey != null) {
        		propInfo.setProperty(actualKey, info.getProperty(key));
        	}
		}
	}
    
    public Collection<Annotation> getAnnotations() {
		return annotations;
	}
    
    public void setAnnotations(Collection<Annotation> annotations) {
		this.annotations = annotations;
	}
    
    public String getDebugLog() {
		return debugLog;
	}
    
    public void setDebugLog(String debugLog) {
		this.debugLog = debugLog;
	}
    
    public PlanNode getCurrentPlanDescription() {
		return currentPlanDescription;
	}
    
    public void setCurrentPlanDescription(PlanNode currentPlanDescription) {
		this.currentPlanDescription = currentPlanDescription;
	}
    
    protected Properties getExecutionProperties() {
        return this.propInfo;
    }
    
    public void setExecutionProperty(String key, String value) {
    	JDBCURL.addNormalizedProperty(key, value, getExecutionProperties());
    }
    
    public String getExecutionProperty(String key) {
    	return this.getExecutionProperties().getProperty(JDBCURL.getValidKey(key));
    }
    
    DQP getDQP() {
    	return this.dqp;
    }
    
    /**
     * Remove password & trusted token and log all other properties
     * @param connUrl - URL used to connect to server
     * @param info - properties object supplied
     */
    private void logConnectionProperties(String connUrl, Properties info) {
        StringBuffer modifiedUrl = new StringBuffer();

        // If we have valid URL
        if (connUrl != null) {
	        // We need wipe out the password here, before we write to the log
	        int startIndex = connUrl.indexOf("password="); //$NON-NLS-1$
	        if (startIndex != -1) {
	            modifiedUrl.append(connUrl.substring(0, startIndex));
	            modifiedUrl.append("password=***"); //$NON-NLS-1$
	            int endIndex = connUrl.indexOf(";", startIndex+9); //$NON-NLS-1$
	            if (endIndex != -1) {
	                modifiedUrl.append(";").append(connUrl.substring(endIndex)); //$NON-NLS-1$
	            }
	        }
	        logger.fine("Connection Url="+modifiedUrl); //$NON-NLS-1$
        }
        
        // Now clone the properties object and remove password and trusted token
        if (info != null) {
            Enumeration enumeration = info.keys();
            while (enumeration.hasMoreElements()) {
                String key = (String)enumeration.nextElement();
                Object anObj = info.get(key);
                // Log each property except for password and token.
                if (!TeiidURL.CONNECTION.PASSWORD.equalsIgnoreCase(key)) { 
                    logger.fine(key+"="+anObj); //$NON-NLS-1$
                }
            }
        }                              
    }
        
    String getUrl() {
        return this.url;
    }
    
    /**
     * Connection identifier of this connection 
     * @return identifier
     * @throws SQLException 
     */
    public String getConnectionId() {
    	return this.serverConn.getLogonResult().getSessionID();
    }
    
    /**
     * Generate the next unique requestID for matching up requests with responses.
     * These IDs should be unique only in the context of a ServerConnection instance.
     * @return Request ID
     */
    protected synchronized long nextRequestID() {
        return requestIDGenerator++;
    }

    @Override
    public void clearWarnings() throws SQLException {
        // do nothing
    }

    @Override
    public void close() throws SQLException {
    	Throwable firstException = null;

    	if(closed) {
            return;
        }

        try {
            // close any statements that were created on this connection
        	try {
        		closeStatements();
        	} catch (SQLException se) {
        		firstException = se;
        	} finally {
        		this.serverConn.close();
                if ( firstException != null )
                	throw (SQLException)firstException;
        	}
        } catch (SQLException se) {
            throw new SQLException(Messages.getString(Messages.JDBC.MMConnection_Err_connection_close, se.getMessage()), se);
        } finally {
            logger.fine(Messages.getString(Messages.JDBC.MMConnection_Connection_close_success));
            // set the status of the connection to closed
            closed = true;
        }
    }

    /**
     * <p>
     * Close all the statements open on this connection
     * </p>
     * 
     * @throws SQLException
     *             server statement object could not be closed.
     */
    void closeStatements() throws SQLException {
        // Closing the statement will cause the
        // MMConnection.closeStatement() method to be called,
        // which will modify this.statements.  So, we do this iteration
        // in a separate safe copy of the list
        List<StatementImpl> statementsSafe = new ArrayList<StatementImpl>(this.statements);
        SQLException ex = null;
        for (StatementImpl statement : statementsSafe) {
            try {
            	statement.close();
            } catch (SQLException e) {
            	ex = e;
            }
        }
        if (ex != null) {
            throw new SQLException(Messages.getString(Messages.JDBC.MMConnection_Err_closing_stmts), ex);
        }
    }

    /**
     * Called by MMStatement to notify the connection that the
     * statement has been closed.
     * @param statement
     */
    void closeStatement(Statement statement) {
        this.statements.remove(statement);
    }

    /**
     * <p>This method makes any changes involved in a transaction permanent and releases
     * any locks held by the connection object.  This is only used when auto-commit
     * is set to false.</p>
     * @throws SQLException if the transaction had been rolled back or marked to roll back.
     */
    @Override
    public void commit() throws SQLException {
        checkConnection();
        if (!autoCommitFlag) {
            try {
                directCommit();
            } finally {
                inLocalTxn = false; 
            }
        }
    }

    private void directCommit() throws SQLException {
    	if (inLocalTxn) {
	        try {
				ResultsFuture<?> future = this.dqp.commit();
				future.get();
			} catch (Exception e) {
				throw new SQLException(e);
			}
	        logger.fine(Messages.getString(Messages.JDBC.MMConnection_Commit_success));
    	}
    }

    void beginLocalTxnIfNeeded() throws SQLException {
        if (this.transactionXid != null || inLocalTxn || this.autoCommitFlag || isDisableLocalTxn()) {
            return;
        }
        String prop = this.propInfo.getProperty(ExecutionProperties.DISABLE_LOCAL_TRANSACTIONS);
        if (prop != null && Boolean.valueOf(prop)) {
            return;
        }
        try {
        	try {
        		this.dqp.begin();
    		} catch (XATransactionException e) {
    			throw new SQLException(e);
    		} 
            inLocalTxn = true;
        } finally {
            if (!inLocalTxn) {
                autoCommitFlag = true;
            }
        }
    }

    private boolean isDisableLocalTxn() {
        String prop = this.propInfo.getProperty(ExecutionProperties.DISABLE_LOCAL_TRANSACTIONS);
        return prop != null && Boolean.valueOf(prop);
    }

    @Override
    public StatementImpl createStatement() throws SQLException {
        return createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * <p>Creates a Statement object that will produce ResultSet objects of the type
     * resultSetType and concurrency level resultSetConcurrency.</p>
     * @param intvalue indicating the ResultSet's type
     * @param intValue indicating the ResultSet's concurrency
     * @return Statement object.
     */
    @Override
    public StatementImpl createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
    	return createStatement(resultSetType, resultSetConcurrency, ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }

    /** 
     * @param resultSetType
     * @throws TeiidClientException
     * @since 4.3
     */
    private void validateResultSetType(int resultSetType) throws SQLException {
        if (resultSetType == ResultSet.TYPE_SCROLL_SENSITIVE ) {
            String msg = Messages.getString(Messages.JDBC.MMConnection_Scrollable_type_not_supported, "ResultSet.TYPE_SCROLL_SENSITIVE"); //$NON-NLS-1$ 
            throw new SQLException(msg);
        }
    }

    /** 
     * @param resultSetConcurrency
     * @throws TeiidClientException
     * @since 4.3
     */
    private void validateResultSetConcurrency(int resultSetConcurrency) throws SQLException {
        if (resultSetConcurrency == ResultSet.CONCUR_UPDATABLE) {
            String msg = Messages.getString(Messages.JDBC.MMConnection_Concurrency_type_not_supported, "ResultSet.CONCUR_UPDATABLE"); //$NON-NLS-1$ 
            throw new SQLException(msg);
        }
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        //Check to see the connection is open
       checkConnection();
       return autoCommitFlag;
    }

    @Override
    public String getCatalog() throws SQLException {
        //Check to see the connection is open
        checkConnection();
        //catalogs are not supported
        return this.serverConn.getLogonResult().getVdbName();
    }

    /**
     * <p>This method gets the ServerConnection object wrapped by this object.</p>
     * @return ServerConnection object
     */
    public ServerConnection getServerConnection() throws SQLException {
        //Check to see the connection is open
        checkConnection();
        return serverConn;
    }

    String getVDBName() throws SQLException {
        //Check to see the connection is open
        checkConnection();
        //get the virtual database name to which we are connected.

        return this.serverConn.getLogonResult().getVdbName();
    }
    
    public int getVDBVersion() throws SQLException {
    	checkConnection();
        return this.serverConn.getLogonResult().getVdbVersion();
    }

    /**
     * Get's the name of the user who got this connection.
     * @return Sring object giving the user name
     * @throws SQLException if the connection is closed
     */
    String getUserName() throws SQLException {
        checkConnection();

        return this.serverConn.getLogonResult().getUserName();
    }
    
    @Override
    public DatabaseMetaDataImpl getMetaData() throws SQLException {
        //Check to see the connection is open
        checkConnection();
        
        if (dbmm == null) {
            dbmm = new DatabaseMetaDataImpl(this);
        }       
        return dbmm;
    }

    /**
     * Get the database name that this connection is representing 
     * @return String name of the database
     */
    public String getDatabaseName() {
    	if (this.serverConn instanceof SocketServerConnection) {
    		return SERVER_NAME;
    	}
    	return EMBEDDED_NAME;
    }
    
    /**
     * Retrieves the current holdability of ResultSet objects created using this Connection object.
     * @param holdability int indicating the holdability
     * @return int holdability
     * @throws SQLException
     */
    @Override
    public int getHoldability() throws SQLException {
        return ResultSet.HOLD_CURSORS_OVER_COMMIT;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
    	return this.transactionIsolation;
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
    	return Collections.emptyMap();
    }
    
    /**
     * <p>This method will return the first warning reported by calls on this connection,
     * or null if none exist.</p>
     * @return A SQLWarning object if there are any warnings.
     * @throws SQLException, should never occur
     */
    @Override
    public SQLWarning getWarnings() throws SQLException {
        //Check to see the connection is open
        checkConnection();
        return null;  // we don't have any warnings
    }

    /**
     * <p>This method will return whether this connection is closed or not.</p>
     * @return booleanvalue indicating if the connection is closed
     * @throws SQLException, should never occur
     */
    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
         return readOnly; 
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        // return the string argument without any modifications.
        // escape syntaxes are directly supported in the server
        return sql;
    }

    /**
     * <p>Creates a CallableStatement object that contains sql and that will produce
     * ResultSet objects that are non-scrollable and non-updatable. A SQL stored
     * procedure call statement is handled by creating a CallableStatement for it.</p>
     * @param sql String(escape syntax) for invoking a stored procedure.
     * @return CallableStatement object that can be used to execute the storedProcedure
     * @throws SQLException if there is an error creating the callable statement object
     */
    @Override
    public CallableStatementImpl prepareCall(String sql) throws SQLException {
    	//there is a problem setting the result set type to be non-scrollable
    	//See defect 17768
        return prepareCall(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    /**
     * <p>Creates a CallableStatement object that contains a sql and that will produce
     * ResultSet objects of the type resultSetType and with a concurrency level of
     * resultSetConcurrency. A SQL stored procedure call statement is handled by
     * creating a CallableStatement for it.</p>
     * @param sql String(escape syntax) for invoking a stored procedure.
     * @param intvalue indicating the ResultSet's type
     * @param intValue indicating the ResultSet's concurrency
     * @return CallableStatement object that can be used to execute the storedProcedure
     */
    @Override
    public CallableStatementImpl prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    	return prepareCall(sql, resultSetType, resultSetConcurrency, ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }

    /** 
     * @param sql
     * @throws TeiidClientException
     * @since 4.3
     */
    private void validateSQL(String sql) throws SQLException {
        if (sql == null) {
            String msg = Messages.getString(Messages.JDBC.MMConnection_SQL_cannot_be_null);
            throw new SQLException(msg);
        }
    }

    @Override
    public PreparedStatementImpl prepareStatement(String sql) throws SQLException {
        return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    @Override
    public PreparedStatementImpl prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
    	return prepareStatement(sql, resultSetType, resultSetConcurrency, ResultSet.HOLD_CURSORS_OVER_COMMIT);
    }

    public PreparedStatementImpl prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
        int resultSetHoldability, int autoGeneratedKeys) throws SQLException {
        //Check to see the connection is open
        checkConnection();
        
        validateResultSetType(resultSetType);
        validateResultSetConcurrency(resultSetConcurrency);
        validateSQL(sql);
        
        // add the statement object to the map
        PreparedStatementImpl newStatement = new PreparedStatementImpl(this, sql, resultSetType, resultSetConcurrency);
        newStatement.setAutoGeneratedKeys(autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS);
        addStatement(newStatement);
        return newStatement;
    }
    
	@Override
    public PreparedStatementImpl prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
	        int resultSetHoldability ) throws SQLException {
		return prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability, Statement.NO_GENERATED_KEYS);
    }

    @Override
    public void rollback() throws SQLException {
        rollback(true);
    }
    
    /**
     * Rollback the current local transaction
     * @param startTxn
     * @throws SQLException
     */
    public void rollback(boolean startTxn) throws SQLException {

        //Check to see the connection is open
        checkConnection();
        if (!autoCommitFlag) {
            if (this.transactionXid != null) {
                throw new SQLException(Messages.getString(Messages.JDBC.MMStatement_In_XA_Transaction)); 
            }
            try {
            	if (this.inLocalTxn) {
            		this.inLocalTxn = false;
	            	try {
	            		ResultsFuture<?> future = this.dqp.rollback();
	            		future.get();
	        		} catch (Exception e) {
	        			throw new SQLException(e);
	        		}
	                logger.fine(Messages.getString(Messages.JDBC.MMConnection_Rollback_success));
            	}
            } finally {
                if (startTxn) {
                    this.inLocalTxn = false;
                }
                else {
                    this.autoCommitFlag = true;
                }
            }
        }
    }
    
	public ResultsFuture<?> submitSetAutoCommitTrue(boolean commit) throws SQLException {
    	//Check to see the connection is open
        checkConnection();

        if (this.autoCommitFlag) {
            return ResultsFuture.NULL_FUTURE;
        }

        this.autoCommitFlag = true;

        if (isDisableLocalTxn()) {
            return ResultsFuture.NULL_FUTURE;
        }

        try {
	        if (commit) {
	        	return dqp.commit();
	        } 
	        return dqp.rollback();
        } catch (XATransactionException e) {
        	throw new SQLException(e);
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        //Check to see the connection is open
        checkConnection();

        if (autoCommit == this.autoCommitFlag) {
            return;
        }
        
        this.autoCommitFlag = autoCommit;

        if (autoCommit) {
            directCommit();   
        } else {
        	inLocalTxn = false;
        }
    }

    /**
     * <p>Teiid does not allow setting a catalog through a connection. This
     * method silently ignores the request as per the specification.</p>
     * @param The string values which sets the catalog name on the connection.
     * @throws SQLException This should never occur.
     */
    @Override
    public void setCatalog(String catalog) throws SQLException {
        // do nothing, silently ignore the request
    }

    /**
     * @param A boolean value specifying whether the connection is readonly.
     * @throws throws SQLException.
     */
    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        if (this.readOnly == readOnly) {
            return;
        }
        // During transaction do not allow to change this flag
        if (!autoCommitFlag || this.transactionXid != null) {
            throw new SQLException(Messages.getString(Messages.JDBC.MMStatement_Invalid_During_Transaction, "setReadOnly(" + readOnly + ")"));//$NON-NLS-1$ //$NON-NLS-2$
        }
        this.readOnly = readOnly;
    }

    /**
     * <p> This utility method checks if the jdbc connection is closed and
     * throws an exception if it is closed. </p>
     * @throws SQLException if the connection object is closed.
     */
    void checkConnection() throws SQLException{
        //Check to see the connection is closed and proceed if it is not
       if (closed) {
            throw new SQLException(Messages.getString(Messages.JDBC.MMConnection_Cant_use_closed_connection)); 
        }
     }

    protected void commitTransaction(XidImpl arg0, boolean arg1) throws SQLException {
        checkConnection();
        transactionXid = null;
        this.autoCommitFlag = true;
        try {
        	ResultsFuture<?> future = this.dqp.commit(arg0, arg1);
        	future.get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
    }

    protected void endTransaction(XidImpl arg0, int arg1) throws SQLException {
        checkConnection();
        this.autoCommitFlag = true;
        try {
        	ResultsFuture<?> future = this.dqp.end(arg0, arg1);
        	future.get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
    }

    protected void forgetTransaction(XidImpl arg0) throws SQLException {
        checkConnection();
        try {
        	ResultsFuture<?> future = this.dqp.forget(arg0);
        	future.get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
    }

    protected int prepareTransaction(XidImpl arg0) throws SQLException  {
        checkConnection();
        transactionXid = null;
        try {
        	ResultsFuture<Integer> future = this.dqp.prepare(arg0);
        	return future.get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
    }

    protected Xid[] recoverTransaction(int arg0) throws SQLException  {
        checkConnection();
        try {
			ResultsFuture<Xid[]> future = this.dqp.recover(arg0);
			return future.get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
    }

    protected void rollbackTransaction(XidImpl arg0) throws SQLException {
        checkConnection();
        transactionXid = null;
        this.autoCommitFlag = true;
        try {
        	ResultsFuture<?> future = this.dqp.rollback(arg0);
        	future.get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
    }

    protected void startTransaction(XidImpl arg0, int arg1, int timeout) throws SQLException {
        checkConnection();
        try {
        	ResultsFuture<?> future = this.dqp.start(arg0, arg1, timeout);
        	future.get();
		} catch (Exception e) {
			throw new SQLException(e);
		}
        transactionXid = arg0;
        this.autoCommitFlag = false;
    }

    protected XidImpl getTransactionXid() {
        return transactionXid;
    }
    
	@Override
    public boolean isValid(int timeout) throws SQLException {
		return this.getServerConnection().isOpen(timeout * 1000);
	}
	
	public void recycleConnection(boolean selectNewInstance) {
		this.payload = null;
        try {
        	//close all open statements
        	this.closeStatements();
        } catch (SQLException e) {
            logger.log(Level.WARNING, Messages.getString(Messages.JDBC.MMXAConnection_rolling_back_error), e); 
        }
        try {
            //rollback if still in a transaction
            if (!this.getAutoCommit()) {
                logger.warning(Messages.getString(Messages.JDBC.MMXAConnection_rolling_back)); 
                
                if (this.getTransactionXid() == null) {
                    this.rollback(false);
                } else {
                	this.rollbackTransaction(getTransactionXid());
                }
            }
        } catch (SQLException e) {
        	logger.log(Level.WARNING, Messages.getString(Messages.JDBC.MMXAConnection_rolling_back_error), e); 
        }
        
        if (selectNewInstance) {
            this.serverConn.cleanUp();
        }
	}
	
	public boolean isSameProcess(ConnectionImpl conn) throws CommunicationException {
		return this.serverConn.isSameInstance(conn.serverConn);
	}
	
	@Override
    public void setClientInfo(Properties properties)
		throws SQLClientInfoException {
	}

	@Override
    public void setClientInfo(String name, String value)
		throws SQLClientInfoException {
	}
	
	@Override
    public Properties getClientInfo() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public String getClientInfo(String name) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
	    checkSupportedVersion(Version.TEIID_8_5);
		return new ArrayImpl(getTeiidVersion(), elements);
	}

	@Override
    public Blob createBlob() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public Clob createClob() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public NClob createNClob() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public SQLXML createSQLXML() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public StatementImpl createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
        //Check to see the connection is open
        checkConnection();

        validateResultSetType(resultSetType);
        validateResultSetConcurrency(resultSetConcurrency);
        //TODO: implement close cursors at commit

        // add the statement object to the map
        StatementImpl newStatement = new StatementImpl(this, resultSetType, resultSetConcurrency);
        addStatement(newStatement);

        return newStatement;
	}

	private void addStatement(StatementImpl newStatement) throws SQLException {
        if (statements.size() > MAX_OPEN_STATEMENTS) {
            this.close();
            throw new SQLException(Messages.gs(Messages.TEIID.TEIID20036, MAX_OPEN_STATEMENTS));
        }
        statements.add(newStatement);
    }

	@Override
    public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public CallableStatementImpl prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
        //Check to see the connection is open
        checkConnection();
        
        validateResultSetType(resultSetType);
        validateResultSetConcurrency(resultSetConcurrency);
        validateSQL(sql);
        //TODO: implement close cursors at commit
        
        // add the statement object to the map
        CallableStatementImpl newStatement = new CallableStatementImpl(this, sql, resultSetType, resultSetConcurrency);
        addStatement(newStatement);
        return newStatement;
	}

	@Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT, Statement.RETURN_GENERATED_KEYS);
	}

	@Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT, Statement.RETURN_GENERATED_KEYS);
	}

	@Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		checkSupportedVersion(Version.TEIID_7_7);
		return prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT, Statement.RETURN_GENERATED_KEYS);
	}

	@Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void rollback(Savepoint savepoint) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void setHoldability(int holdability) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public Savepoint setSavepoint() throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public Savepoint setSavepoint(String name) throws SQLException {
		throw new UnsupportedOperationException();
	}

	@Override
    public void setTransactionIsolation(int level) throws SQLException {
		this.transactionIsolation = level;
	}

	@Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		throw new UnsupportedOperationException();
	}
	
	Object setPassword(Object newPassword) {
		if (newPassword != null) {
			return this.connectionProps.put(TeiidURL.CONNECTION.PASSWORD, newPassword);
		} 
		return this.connectionProps.remove(TeiidURL.CONNECTION.PASSWORD);
	}
	
	String getPassword() {
		Object result = this.connectionProps.get(TeiidURL.CONNECTION.PASSWORD);
		if (result == null) {
			return null;
		}
		return result.toString();
	}
	
	@Override
	public void changeUser(String userName, String newPassword)
			throws SQLException {
		//TODO: recycleConnection();
		Object oldName = null;
		Object oldPassword = null;
		if (userName != null) {
			oldName = this.connectionProps.put(TeiidURL.CONNECTION.USER_NAME, userName);
		} else {
			oldName = this.connectionProps.remove(TeiidURL.CONNECTION.USER_NAME);
		}
		oldPassword = setPassword(newPassword);
		boolean success = false;
		try {
			this.serverConn.authenticate();
			success = true;
		} catch (ConnectionException e) {
			throw new SQLException(e);
		} catch (CommunicationException e) {
			throw new SQLException(e);
		} finally {
			if (!success) {
				if (oldName != null) {
					this.connectionProps.put(TeiidURL.CONNECTION.USER_NAME, oldName);
				} else {
					this.connectionProps.remove(TeiidURL.CONNECTION.USER_NAME);
				}
				setPassword(oldPassword);
			}
		}
	}

    protected boolean isLessThanTeiidEight() {
        return getTeiidVersion().isLessThan(Version.TEIID_8_0);
    }

    protected void checkSupportedVersion(Version teiidVersion) {
        ITeiidServerVersion minVersion = getTeiidVersion().getMinimumVersion();
        if (minVersion.isLessThan(teiidVersion.get())) { 
            TeiidRuntimePlugin.logError("StatementImpl.checkSupportedVersion", "Method being executed that is not supported in teiid version " + getTeiidVersion());  //$NON-NLS-1$//$NON-NLS-2$
            throw new UnsupportedOperationException();
        }
    }

    /* Do not override to allow compatibility with jdk 1.6 */
    @Override
    public void abort(Executor executor) throws SQLException {
		if (closed) {
			return;
		}
		//TODO: ensure that threads are released.  In theory they will be since close effectively cancels current executions
		close();
	}

    /* Do not override to allow compatibility with jdk 1.6 */
    @Override
    public int getNetworkTimeout() throws SQLException {
		throw new UnsupportedOperationException();
	}

    /* Do not override to allow compatibility with jdk 1.6 */
    @Override
    public String getSchema() throws SQLException {
		return null;
	}

	/**
	 * @see query timeouts and the synchronousTtl setting if using socket connections
	 */
    /* Do not override to allow compatibility with jdk 1.6 */
    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		throw new UnsupportedOperationException();
	}

    /* Do not override to allow compatibility with jdk 1.6 */
    @Override
    public void setSchema(String schema) throws SQLException {
		
	}
	
	public Properties getPayload() {
		return payload;
	}
	
	public void setPayload(Properties payload) {
		this.payload = payload;
	}
	
	public Properties getConnectionProps() {
		return connectionProps;
	}

	@Since(Version.TEIID_8_12_4)
	void setTransactionXid(XidImpl transactionXid) {
        this.transactionXid = transactionXid;
    }
}
