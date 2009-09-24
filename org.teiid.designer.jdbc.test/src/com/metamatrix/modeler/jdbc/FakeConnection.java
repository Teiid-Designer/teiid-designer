/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

/**
 * FakeConnection
 */
public class FakeConnection implements Connection {

    /**
     * Construct an instance of FakeConnection.
     */
    public FakeConnection() {
        super();
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createStatement()
     */
    public Statement createStatement() {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareStatement(java.lang.String)
     */
    public PreparedStatement prepareStatement( String sql ) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareCall(java.lang.String)
     */
    public CallableStatement prepareCall( String sql ) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#nativeSQL(java.lang.String)
     */
    public String nativeSQL( String sql ) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setAutoCommit(boolean)
     */
    public void setAutoCommit( boolean autoCommit ) {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getAutoCommit()
     */
    public boolean getAutoCommit() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#commit()
     */
    public void commit() {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#rollback()
     */
    public void rollback() {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#close()
     */
    public void close() {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#isClosed()
     */
    public boolean isClosed() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getMetaData()
     */
    public DatabaseMetaData getMetaData() {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setReadOnly(boolean)
     */
    public void setReadOnly( boolean readOnly ) {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#isReadOnly()
     */
    public boolean isReadOnly() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setCatalog(java.lang.String)
     */
    public void setCatalog( String catalog ) {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getCatalog()
     */
    public String getCatalog() {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#setTransactionIsolation(int)
     */
    public void setTransactionIsolation( int level ) {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getTransactionIsolation()
     */
    public int getTransactionIsolation() {
        return 0;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getWarnings()
     */
    public SQLWarning getWarnings() {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#clearWarnings()
     */
    public void clearWarnings() {

    }

    /* (non-Javadoc)
     * @see java.sql.Connection#createStatement(int, int)
     */
    public Statement createStatement( int resultSetType,
                                      int resultSetConcurrency ) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareStatement(java.lang.String, int, int)
     */
    public PreparedStatement prepareStatement( String sql,
                                               int resultSetType,
                                               int resultSetConcurrency ) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#prepareCall(java.lang.String, int, int)
     */
    public CallableStatement prepareCall( String sql,
                                          int resultSetType,
                                          int resultSetConcurrency ) {
        return null;
    }

    /* (non-Javadoc)
     * @see java.sql.Connection#getTypeMap()
     */
    public Map<String, Class<?>> getTypeMap() {
        return null;
    }

    // ==============================================================================
    // Following methods are implementations of the
    // JDBC 2.0 API Connection interface. They are just stubbed out
    // here so that this class will compile with j2sdk 1.4.
    // ==============================================================================

    /**
     * Changes the holdability of <code>ResultSet</code> objects created using this <code>Connection</code> object to the given
     * holdability.
     * 
     * @param holdability a <code>ResultSet</code> holdability constant; one of <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
     *        <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code> @ if a database access occurs, the given parameter is not a
     *        <code>ResultSet</code> constant indicating holdability, or the given holdability is not supported
     * @see #getHoldability
     * @see java.sql.ResultSet
     * @since 1.4
     */
    public void setHoldability( int holdability ) {
    }

    /**
     * Retrieves the current holdability of <code>ResultSet</code> objects created using this <code>Connection</code> object.
     * 
     * @return the holdability, one of <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
     *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code> @ if a database access occurs
     * @see #setHoldability
     * @see java.sql.ResultSet
     * @since 1.4
     */
    public int getHoldability() {
        return 0;
    }

    /**
     * Creates an unnamed savepoint in the current transaction and returns the new <code>Savepoint</code> object that represents
     * it.
     * 
     * @return the new <code>Savepoint</code> object
     * @exception SQLException if a database access error occurs or this <code>Connection</code> object is currently in
     *            auto-commit mode
     * @see java.sql.Savepoint
     * @since 1.4
     */
    public Savepoint setSavepoint() {
        return null;
    }

    /**
     * Creates a savepoint with the given name in the current transaction and returns the new <code>Savepoint</code> object that
     * represents it.
     * 
     * @param name a <code>String</code> containing the name of the savepoint
     * @return the new <code>Savepoint</code> object
     * @exception SQLException if a database access error occurs or this <code>Connection</code> object is currently in
     *            auto-commit mode
     * @see java.sql.Savepoint
     * @since 1.4
     */
    public Savepoint setSavepoint( String name ) {
        return null;
    }

    /**
     * Undoes all changes made after the given <code>Savepoint</code> object was set.
     * <P>
     * This method should be used only when auto-commit has been disabled.
     * 
     * @param savepoint the <code>Savepoint</code> object to roll back to
     * @exception SQLException if a database access error occurs, the <code>Savepoint</code> object is no longer valid, or this
     *            <code>Connection</code> object is currently in auto-commit mode
     * @see java.sql.Savepoint
     * @see #rollback
     * @since 1.4
     */
    public void rollback( Savepoint savepoint ) {
    }

    /**
     * Removes the given <code>Savepoint</code> object from the current transaction. Any reference to the savepoint after it have
     * been removed will cause an <code>SQLException</code> to be thrown.
     * 
     * @param savepoint the <code>Savepoint</code> object to be removed
     * @exception SQLException if a database access error occurs or the given <code>Savepoint</code> object is not a valid
     *            savepoint in the current transaction
     * @since 1.4
     */
    public void releaseSavepoint( Savepoint savepoint ) {
    }

    /**
     * Creates a <code>Statement</code> object that will generate <code>ResultSet</code> objects with the given type, concurrency,
     * and holdability. This method is the same as the <code>createStatement</code> method above, but it allows the default result
     * set type, concurrency, and holdability to be overridden.
     * 
     * @param resultSetType one of the following <code>ResultSet</code> constants: <code>ResultSet.TYPE_FORWARD_ONLY</code>,
     *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
     * @param resultSetConcurrency one of the following <code>ResultSet</code> constants: <code>ResultSet.CONCUR_READ_ONLY</code>
     *        or <code>ResultSet.CONCUR_UPDATABLE</code>
     * @param resultSetHoldability one of the following <code>ResultSet</code> constants:
     *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @return a new <code>Statement</code> object that will generate <code>ResultSet</code> objects with the given type,
     *         concurrency, and holdability
     * @exception SQLException if a database access error occurs or the given parameters are not <code>ResultSet</code> constants
     *            indicating type, concurrency, and holdability
     * @see java.sql.ResultSet
     * @since 1.4
     */
    public Statement createStatement( int resultSetType,
                                      int resultSetConcurrency,
                                      int resultSetHoldability ) {
        return null;
    }

    /**
     * Creates a <code>PreparedStatement</code> object that will generate <code>ResultSet</code> objects with the given type,
     * concurrency, and holdability.
     * <P>
     * This method is the same as the <code>prepareStatement</code> method above, but it allows the default result set type,
     * concurrency, and holdability to be overridden.
     * 
     * @param sql a <code>String</code> object that is the SQL statement to be sent to the database; may contain one or more ? IN
     *        parameters
     * @param resultSetType one of the following <code>ResultSet</code> constants: <code>ResultSet.TYPE_FORWARD_ONLY</code>,
     *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
     * @param resultSetConcurrency one of the following <code>ResultSet</code> constants: <code>ResultSet.CONCUR_READ_ONLY</code>
     *        or <code>ResultSet.CONCUR_UPDATABLE</code>
     * @param resultSetHoldability one of the following <code>ResultSet</code> constants:
     *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @return a new <code>PreparedStatement</code> object, containing the pre-compiled SQL statement, that will generate
     *         <code>ResultSet</code> objects with the given type, concurrency, and holdability
     * @exception SQLException if a database access error occurs or the given parameters are not <code>ResultSet</code> constants
     *            indicating type, concurrency, and holdability
     * @see java.sql.ResultSet
     * @since 1.4
     */
    public PreparedStatement prepareStatement( String sql,
                                               int resultSetType,
                                               int resultSetConcurrency,
                                               int resultSetHoldability ) {
        return null;
    }

    /**
     * Creates a <code>CallableStatement</code> object that will generate <code>ResultSet</code> objects with the given type and
     * concurrency. This method is the same as the <code>prepareCall</code> method above, but it allows the default result set
     * type, result set concurrency type and holdability to be overridden.
     * 
     * @param sql a <code>String</code> object that is the SQL statement to be sent to the database; may contain on or more ?
     *        parameters
     * @param resultSetType one of the following <code>ResultSet</code> constants: <code>ResultSet.TYPE_FORWARD_ONLY</code>,
     *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
     * @param resultSetConcurrency one of the following <code>ResultSet</code> constants: <code>ResultSet.CONCUR_READ_ONLY</code>
     *        or <code>ResultSet.CONCUR_UPDATABLE</code>
     * @param resultSetHoldability one of the following <code>ResultSet</code> constants:
     *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @return a new <code>CallableStatement</code> object, containing the pre-compiled SQL statement, that will generate
     *         <code>ResultSet</code> objects with the given type, concurrency, and holdability
     * @exception SQLException if a database access error occurs or the given parameters are not <code>ResultSet</code> constants
     *            indicating type, concurrency, and holdability
     * @see java.sql.ResultSet
     * @since 1.4
     */
    public CallableStatement prepareCall( String sql,
                                          int resultSetType,
                                          int resultSetConcurrency,
                                          int resultSetHoldability ) {
        return null;
    }

    /**
     * Creates a default <code>PreparedStatement</code> object that has the capability to retrieve auto-generated keys. The given
     * constant tells the driver whether it should make auto-generated keys available for retrieval. This parameter is ignored if
     * the SQL statement is not an <code>INSERT</code> statement.
     * <P>
     * <B>Note:</B> This method is optimized for handling parametric SQL statements that benefit from precompilation. If the
     * driver supports precompilation, the method <code>prepareStatement</code> will send the statement to the database for
     * precompilation. Some drivers may not support precompilation. In this case, the statement may not be sent to the database
     * until the <code>PreparedStatement</code> object is executed. This has no direct effect on users; however, it does affect
     * which methods throw certain SQLExceptions.
     * <P>
     * Result sets created using the returned <code>PreparedStatement</code> object will by default be type
     * <code>TYPE_FORWARD_ONLY</code> and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
     * 
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param autoGeneratedKeys a flag indicating whether auto-generated keys should be returned; one of
     *        <code>Statement.RETURN_GENERATED_KEYS</code> or <code>Statement.NO_GENERATED_KEYS</code>
     * @return a new <code>PreparedStatement</code> object, containing the pre-compiled SQL statement, that will have the
     *         capability of returning auto-generated keys
     * @exception SQLException if a database access error occurs or the given parameter is not a <code>Statement</code> constant
     *            indicating whether auto-generated keys should be returned
     * @since 1.4
     */
    public PreparedStatement prepareStatement( String sql,
                                               int autoGeneratedKeys ) {
        return null;
    }

    /**
     * Creates a default <code>PreparedStatement</code> object capable of returning the auto-generated keys designated by the
     * given array. This array contains the indexes of the columns in the target table that contain the auto-generated keys that
     * should be made available. This array is ignored if the SQL statement is not an <code>INSERT</code> statement.
     * <P>
     * An SQL statement with or without IN parameters can be pre-compiled and stored in a <code>PreparedStatement</code> object.
     * This object can then be used to efficiently execute this statement multiple times.
     * <P>
     * <B>Note:</B> This method is optimized for handling parametric SQL statements that benefit from precompilation. If the
     * driver supports precompilation, the method <code>prepareStatement</code> will send the statement to the database for
     * precompilation. Some drivers may not support precompilation. In this case, the statement may not be sent to the database
     * until the <code>PreparedStatement</code> object is executed. This has no direct effect on users; however, it does affect
     * which methods throw certain SQLExceptions.
     * <P>
     * Result sets created using the returned <code>PreparedStatement</code> object will by default be type
     * <code>TYPE_FORWARD_ONLY</code> and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
     * 
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param columnIndexes an array of column indexes indicating the columns that should be returned from the inserted row or
     *        rows
     * @return a new <code>PreparedStatement</code> object, containing the pre-compiled statement, that is capable of returning
     *         the auto-generated keys designated by the given array of column indexes
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    public PreparedStatement prepareStatement( String sql,
                                               int columnIndexes[] ) {
        return null;
    }

    /**
     * Creates a default <code>PreparedStatement</code> object capable of returning the auto-generated keys designated by the
     * given array. This array contains the names of the columns in the target table that contain the auto-generated keys that
     * should be returned. This array is ignored if the SQL statement is not an <code>INSERT</code> statement.
     * <P>
     * An SQL statement with or without IN parameters can be pre-compiled and stored in a <code>PreparedStatement</code> object.
     * This object can then be used to efficiently execute this statement multiple times.
     * <P>
     * <B>Note:</B> This method is optimized for handling parametric SQL statements that benefit from precompilation. If the
     * driver supports precompilation, the method <code>prepareStatement</code> will send the statement to the database for
     * precompilation. Some drivers may not support precompilation. In this case, the statement may not be sent to the database
     * until the <code>PreparedStatement</code> object is executed. This has no direct effect on users; however, it does affect
     * which methods throw certain SQLExceptions.
     * <P>
     * Result sets created using the returned <code>PreparedStatement</code> object will by default be type
     * <code>TYPE_FORWARD_ONLY</code> and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
     * 
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @param columnNames an array of column names indicating the columns that should be returned from the inserted row or rows
     * @return a new <code>PreparedStatement</code> object, containing the pre-compiled statement, that is capable of returning
     *         the auto-generated keys designated by the given array of column names
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    public PreparedStatement prepareStatement( String sql,
                                               String columnNames[] ) {
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

        return false;
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
