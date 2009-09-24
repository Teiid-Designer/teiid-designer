/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.model;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.metamatrix.modeler.modelgenerator.xml.wizards.StateManager;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessor;

public class ConnectionImpl implements Connection {

    UserSettings userSettings;
    RelationshipProcessor relationshipTypes;
    SQLWarning warnings;
    DatabaseMetaDataImpl databaseMetaData;

    private Object syncObject;
    private StateManager stateManager;

    /**
	 * 
	 */
    public ConnectionImpl( StateManager manager,
                           UserSettings userSettings,
                           Object syncObject ) {
        this.syncObject = syncObject;
        this.stateManager = manager;
        this.userSettings = userSettings;
        databaseMetaData = new DatabaseMetaDataImpl(manager, userSettings, this, syncObject);
        warnings = null;
    }

    public void changed() {
        synchronized (syncObject) {
            databaseMetaData.changed();
        }
    }

    /**
     * @see java.sql.Connection#getHoldability()
     */
    public int getHoldability() {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    /**
     * @see java.sql.Connection#getTransactionIsolation()
     */
    public int getTransactionIsolation() {
        return TRANSACTION_READ_UNCOMMITTED;
    }

    /**
     * @see java.sql.Connection#clearWarnings()
     */
    public void clearWarnings() {
        warnings = null;

    }

    /**
     * @see java.sql.Connection#close()
     */
    public void close() {
    }

    /**
     * @see java.sql.Connection#commit()
     */
    public void commit() {
        // We don't care about data; only metadata retrieval
    }

    /**
     * @see java.sql.Connection#rollback()
     */
    public void rollback() {
        // We don't care about data; only metadata retrieval

    }

    /**
     * @see java.sql.Connection#getAutoCommit()
     */
    public boolean getAutoCommit() {
        // We don't care about data; only metadata retrieval
        return true;
    }

    /**
     * @see java.sql.Connection#isClosed()
     */
    public boolean isClosed() {
        return false;
    }

    /**
     * @see java.sql.Connection#isReadOnly()
     */
    public boolean isReadOnly() {
        return true;
    }

    /**
     * @see java.sql.Connection#setHoldability(int)
     */
    public void setHoldability( int holdability ) {
        // We don't care about data; only metadata retrieval
    }

    /**
     * @see java.sql.Connection#setTransactionIsolation(int)
     */
    public void setTransactionIsolation( int level ) {
        // We don't care about data; only metadata retrieval
    }

    /**
     * @see java.sql.Connection#setAutoCommit(boolean)
     */
    public void setAutoCommit( boolean autoCommit ) {
        // We don't care about data; only metadata retrieval
    }

    /**
     * @see java.sql.Connection#setReadOnly(boolean)
     */
    public void setReadOnly( boolean readOnly ) {
        // We don't care about data; only metadata retrieval
    }

    /**
     * @see java.sql.Connection#getCatalog()
     */
    public String getCatalog() {
        String namespace = stateManager.getFirstCatalog();
        return namespace;
    }

    /**
     * @see java.sql.Connection#setCatalog(java.lang.String)
     */
    public void setCatalog( String catalog ) {
    }

    /**
     * @see java.sql.Connection#getMetaData()
     */
    public DatabaseMetaData getMetaData() {
        return databaseMetaData;
    }

    /**
     * @see java.sql.Connection#getWarnings()
     */
    public SQLWarning getWarnings() {
        return warnings;
    }

    /**
     * @see java.sql.Connection#setSavepoint()
     */
    public Savepoint setSavepoint() {
        // We don't care about data; only metadata retrieval
        return new Savepoint() {
            public int getSavepointId() {
                return 0;
            }

            public String getSavepointName() {
                return "0"; //$NON-NLS-1$
            }
        };
    }

    /**
     * @see java.sql.Connection#releaseSavepoint(java.sql.Savepoint)
     */
    public void releaseSavepoint( Savepoint savepoint ) {
        // We don't care about data; only metadata retrieval
    }

    /**
     * @see java.sql.Connection#rollback(java.sql.Savepoint)
     */
    public void rollback( Savepoint savepoint ) {
        // We don't care about data; only metadata retrieval
    }

    /**
     * @see java.sql.Connection#createStatement()
     */
    public Statement createStatement() {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#createStatement(int, int)
     */
    public Statement createStatement( int resultSetType,
                                      int resultSetConcurrency ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#createStatement(int, int, int)
     */
    public Statement createStatement( int resultSetType,
                                      int resultSetConcurrency,
                                      int resultSetHoldability ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    Map typemap = new HashMap();

    /**
     * @see java.sql.Connection#getTypeMap()
     */
    public Map<String, Class<?>> getTypeMap() {
        return typemap;
    }

    /**
     * @see java.sql.Connection#nativeSQL(java.lang.String)
     */
    public String nativeSQL( String sql ) {
        // We don't care about data; only metadata retrieval
        return sql;
    }

    /**
     * @see java.sql.Connection#prepareCall(java.lang.String)
     */
    public CallableStatement prepareCall( String sql ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
     */
    public CallableStatement prepareCall( String sql,
                                          int resultSetType,
                                          int resultSetConcurrency ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#prepareCall(java.lang.String, int, int, int)
     */
    public CallableStatement prepareCall( String sql,
                                          int resultSetType,
                                          int resultSetConcurrency,
                                          int resultSetHoldability ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#prepareStatement(java.lang.String)
     */
    public PreparedStatement prepareStatement( String sql ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#prepareStatement(java.lang.String, int)
     */
    public PreparedStatement prepareStatement( String sql,
                                               int autoGeneratedKeys ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
     */
    public PreparedStatement prepareStatement( String sql,
                                               int resultSetType,
                                               int resultSetConcurrency ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#prepareStatement(java.lang.String, int, int, int)
     */
    public PreparedStatement prepareStatement( String sql,
                                               int resultSetType,
                                               int resultSetConcurrency,
                                               int resultSetHoldability ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#prepareStatement(java.lang.String, int[])
     */
    public PreparedStatement prepareStatement( String sql,
                                               int[] columnIndexes ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    /**
     * @see java.sql.Connection#setSavepoint(java.lang.String)
     */
    public Savepoint setSavepoint( final String name ) {
        // We don't care about data; only metadata retrieval
        return new Savepoint() {
            public int getSavepointId() {
                return 0;
            }

            public String getSavepointName() {
                return name;
            }
        };
    }

    /**
     * @see java.sql.Connection#prepareStatement(java.lang.String, java.lang.String[])
     */
    public PreparedStatement prepareStatement( String sql,
                                               String[] columnNames ) {
        // We don't care about data; only metadata retrieval
        return null;
    }

    @Override
    public Array createArrayOf( String typeName,
                                Object[] elements ) {

        return null;
    }

    @Override
    public Blob createBlob() {

        return null;
    }

    @Override
    public Clob createClob() {

        return null;
    }

    @Override
    public NClob createNClob() {

        return null;
    }

    @Override
    public SQLXML createSQLXML() {

        return null;
    }

    @Override
    public Struct createStruct( String typeName,
                                Object[] attributes ) {

        return null;
    }

    @Override
    public Properties getClientInfo() {

        return null;
    }

    @Override
    public String getClientInfo( String name ) {

        return null;
    }

    @Override
    public boolean isValid( int timeout ) {
        return true;
    }

    @Override
    public void setClientInfo( Properties properties ) {

    }

    @Override
    public void setClientInfo( String name,
                               String value ) {

    }

    @Override
    public void setTypeMap( Map<String, Class<?>> map ) {
        typemap = map;
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
