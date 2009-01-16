/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.jdbc.metadata;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface provides access to the (metadata) capabilities of a JDBC driver
 */
public interface Capabilities {

    // =========================================================================
    //               Specialized (non-JDBC) methods
    // =========================================================================
    /**
     * Retrieves whether this database supports catalogs.
     * @return true if the database supports catalogs.
     * @throws SQLException if there is an error obtaining the information
     */
    public boolean supportsCatalogs() throws SQLException;

    /**
     * Retrieves whether this database supports schemas.
     * @return true if the database supports schemas.
     * @throws SQLException if there is an error obtaining the information
     */
    public boolean supportsSchemas() throws SQLException;
    
    /**
     * Return the list of all table types supported by the JDBC database.
     * @return the list of supported table types; never null, but possibly empty
     * @throws SQLException if there is an error obtaining the information
     */
    public String[] getTableTypes() throws SQLException;
    
    // =========================================================================
    //               Standard JDBC DatabaseMetaData methods
    // =========================================================================

    /**
     * Can all the procedures returned by getProcedures be called by the current user? 
     * @throws SQLException if there is an error
     */
    boolean allProceduresAreCallable() throws SQLException;
    /**
     * Can all the tables returned by getTable be SELECTed by the current user? 
     * @throws SQLException if there is an error
     */
    boolean allTablesAreSelectable() throws SQLException; 
              
    /**
     * Does a data definition statement within a transaction force the transaction to commit? 
     * @throws SQLException if there is an error
     */
    boolean dataDefinitionCausesTransactionCommit() throws SQLException; 
              
    /**
     * Is a data definition statement within a transaction ignored? 
     * @throws SQLException if there is an error
     */
    boolean dataDefinitionIgnoredInTransactions() throws SQLException; 
              
    /**
     * Indicates whether or not a visible row delete can be detected by calling ResultSet.rowDeleted() throws SQLException;. 
     * @throws SQLException if there is an error
     */
    boolean deletesAreDetected(int type) throws SQLException; 
              
    /**
     * Did getMaxRowSize() throws SQLException; include LONGVARCHAR and LONGVARBINARY blobs? 
     * @throws SQLException if there is an error
     */
    boolean doesMaxRowSizeIncludeBlobs() throws SQLException; 
              
    /**
     * What's the separator between catalog and table name? 
     * @throws SQLException if there is an error
     */
    String getCatalogSeparator() throws SQLException; 
              
    /**
     * What's the database vendor's preferred term for "catalog"? 
     * @throws SQLException if there is an error
     */
    String getCatalogTerm() throws SQLException; 
              
    /**
     * Gets all the "extra" characters that can be used in unquoted identifier names (those beyond a-z, A-Z, 0-9 and _). 
     * @throws SQLException if there is an error
     */
    String getExtraNameCharacters() throws SQLException; 
             
    /**
     * What's the string used to quote SQL identifiers? This returns a space " " if identifier quoting isn't supported. 
     * @throws SQLException if there is an error
     */
    String getIdentifierQuoteString() throws SQLException; 
             
    /**
     * How many hex characters can you have in an inline binary literal? 
     * @throws SQLException if there is an error
     */
    int getMaxBinaryLiteralLength() throws SQLException; 
             
    /**
     * What's the maximum length of a catalog name? 
     * @throws SQLException if there is an error
     */
    int getMaxCatalogNameLength() throws SQLException; 
             
    /**
     * What's the max length for a character literal? 
     * @throws SQLException if there is an error
     */
    int getMaxCharLiteralLength() throws SQLException; 
             
    /**
     * What's the limit on column name length? 
     * @throws SQLException if there is an error
     */
    int getMaxColumnNameLength() throws SQLException; 
             
    /**
     * What's the maximum number of columns in a "GROUP BY" clause? 
     * @throws SQLException if there is an error
     */
    int getMaxColumnsInGroupBy() throws SQLException; 
             
    /**
     * What's the maximum number of columns allowed in an index? 
     * @throws SQLException if there is an error
     */
    int getMaxColumnsInIndex() throws SQLException; 
             
    /**
     * What's the maximum number of columns in an "ORDER BY" clause? 
     * @throws SQLException if there is an error
     */
    int getMaxColumnsInOrderBy() throws SQLException; 
             
    /**
     * What's the maximum number of columns in a "SELECT" list? 
     * @throws SQLException if there is an error
     */
    int getMaxColumnsInSelect() throws SQLException; 
             
    /**
     * What's the maximum number of columns in a table? 
     * @throws SQLException if there is an error
     */
    int getMaxColumnsInTable() throws SQLException; 
             
    /**
     * How many active connections can we have at a time to this database? 
     * @throws SQLException if there is an error
     */
    int getMaxConnections() throws SQLException; 
             
    /**
     * What's the maximum cursor name length? 
     * @throws SQLException if there is an error
     */
    int getMaxCursorNameLength() throws SQLException; 
             
    /**
     * Retrieves the maximum number of bytes for an index, including all of the parts of the index. 
     * @throws SQLException if there is an error
     */
    int getMaxIndexLength() throws SQLException; 
             
    /**
     * What's the maximum length of a procedure name? 
     * @throws SQLException if there is an error
     */
    int getMaxProcedureNameLength() throws SQLException; 
             
    /**
     * What's the maximum length of a single row? 
     * @throws SQLException if there is an error
     */
    int getMaxRowSize() throws SQLException; 
             
    /**
     * What's the maximum length allowed for a schema name? 
     * @throws SQLException if there is an error
     */
    int getMaxSchemaNameLength() throws SQLException; 
             
    /**
     * What's the maximum length of an SQL statement? 
     * @throws SQLException if there is an error
     */
    int getMaxStatementLength() throws SQLException; 
             
    /**
     * How many active statements can we have open at one time to this database? 
     * @throws SQLException if there is an error
     */
    int getMaxStatements() throws SQLException; 
             
    /**
     * What's the maximum length of a table name? 
     * @throws SQLException if there is an error
     */
    int getMaxTableNameLength() throws SQLException; 
             
    /**
     * What's the maximum number of tables in a SELECT statement? 
     * @throws SQLException if there is an error
     */
    int getMaxTablesInSelect() throws SQLException; 
             
    /**
     * What's the maximum length of a user name? 
     * @throws SQLException if there is an error
     */
    int getMaxUserNameLength() throws SQLException; 
             
    /**
     * Gets a comma-separated list of math functions. 
     * @throws SQLException if there is an error
     */
    String getNumericFunctions() throws SQLException; 
             
    /**
     * What's the database vendor's preferred term for "procedure"? 
     * @throws SQLException if there is an error
     */
    String getProcedureTerm() throws SQLException; 
             
    /**
     * What's the database vendor's preferred term for "schema"? 
     * @throws SQLException if there is an error
     */
    String getSchemaTerm() throws SQLException; 
             
    /**
     * Gets the string that can be used to escape wildcard characters. 
     * @throws SQLException if there is an error
     */
    String getSearchStringEscape() throws SQLException; 
             
    /**
     * Gets a comma-separated list of all a database's SQL keywords that are NOT also SQL92 keywords. 
     * @throws SQLException if there is an error
     */
    String getSQLKeywords() throws SQLException; 
             
    /**
     * Gets a comma-separated list of string functions. 
     * @throws SQLException if there is an error
     */
    String getStringFunctions() throws SQLException; 
             
    /**
     * Gets a comma-separated list of system functions. 
     * @throws SQLException if there is an error
     */
    String getSystemFunctions() throws SQLException; 
             
    /**
     * Gets a comma-separated list of time and date functions. 
     * @throws SQLException if there is an error
     */
    String getTimeDateFunctions() throws SQLException; 
             
    /**
     * Gets a description of all the standard SQL types supported by this database. 
     * @throws SQLException if there is an error
     */
    ResultSet getTypeInfo() throws SQLException; 
             
    /**
     * Indicates whether or not a visible row insert can be detected by calling {@link java.sql.ResultSet.rowInserted()}. 
     * @throws SQLException if there is an error
     */
    boolean insertsAreDetected(int type)  throws SQLException; 
             
    /**
     * Does a catalog appear at the start of a qualified table name? (Otherwise it appears at the end) 
     * @throws SQLException if there is an error
     */
    boolean isCatalogAtStart() throws SQLException; 
             
    /**
     * Is the database in read-only mode? 
     * @throws SQLException if there is an error
     */
    boolean isReadOnly() throws SQLException; 
             
    /**
     * Are concatenations between NULL and non-NULL values NULL? For SQL-92 compliance, a JDBC technology-enabled driver will return true. 
     * @throws SQLException if there is an error
     */
    boolean nullPlusNonNullIsNull() throws SQLException; 
             
    /**
     * Are NULL values sorted at the end regardless of sort order? 
     * @throws SQLException if there is an error
     */
    boolean nullsAreSortedAtEnd() throws SQLException; 
             
    /**
     * Are NULL values sorted at the start regardless of sort order? 
     * @throws SQLException if there is an error
     */
    boolean nullsAreSortedAtStart() throws SQLException; 
             
    /**
     * Are NULL values sorted high? 
     * @throws SQLException if there is an error
     */
    boolean nullsAreSortedHigh() throws SQLException; 
             
    /**
     * Are NULL values sorted low? 
     * @throws SQLException if there is an error
     */
    boolean nullsAreSortedLow() throws SQLException; 
             
    /**
     * Indicates whether deletes made by others are visible. 
     * @throws SQLException if there is an error
     */
    boolean othersDeletesAreVisible(int type)  throws SQLException; 
             
    /**
     * Indicates whether inserts made by others are visible. 
     * @throws SQLException if there is an error
     */
    boolean othersInsertsAreVisible(int type)  throws SQLException; 
             
    /**
     * Indicates whether updates made by others are visible. 
     * @throws SQLException if there is an error
     */
    boolean othersUpdatesAreVisible(int type)  throws SQLException; 
             
    /**
     * Indicates whether a result set's own deletes are visible. 
     * @throws SQLException if there is an error
     */
    boolean ownDeletesAreVisible(int type)  throws SQLException; 
             
    /**
     * Indicates whether a result set's own inserts are visible. 
     * @throws SQLException if there is an error
     */
    boolean ownInsertsAreVisible(int type)  throws SQLException; 
             
    /**
     * Indicates whether a result set's own updates are visible. 
     * @throws SQLException if there is an error
     */
    boolean ownUpdatesAreVisible(int type)  throws SQLException; 
             
    /**
     * Does the database treat mixed case unquoted SQL identifiers as case insensitive and store them in lower case? 
     * @throws SQLException if there is an error
     */
    boolean storesLowerCaseIdentifiers() throws SQLException; 
             
    /**
     * Does the database treat mixed case quoted SQL identifiers as case insensitive and store them in lower case? 
     * @throws SQLException if there is an error
     */
    boolean storesLowerCaseQuotedIdentifiers() throws SQLException; 
             
    /**
     * Does the database treat mixed case unquoted SQL identifiers as case insensitive and store them in mixed case? 
     * @throws SQLException if there is an error
     */
    boolean storesMixedCaseIdentifiers() throws SQLException; 
             
    /**
     * Does the database treat mixed case quoted SQL identifiers as case insensitive and store them in mixed case? 
     * @throws SQLException if there is an error
     */
    boolean storesMixedCaseQuotedIdentifiers() throws SQLException; 
             
    /**
     * Does the database treat mixed case unquoted SQL identifiers as case insensitive and store them in upper case? 
     * @throws SQLException if there is an error
     */
    boolean storesUpperCaseIdentifiers() throws SQLException; 
             
    /**
     * Does the database treat mixed case quoted SQL identifiers as case insensitive and store them in upper case? 
     * @throws SQLException if there is an error
     */
    boolean storesUpperCaseQuotedIdentifiers() throws SQLException; 
             
    /**
     * Is "ALTER TABLE" with add column supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsAlterTableWithAddColumn() throws SQLException; 
             
    /**
     * Is "ALTER TABLE" with drop column supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsAlterTableWithDropColumn() throws SQLException; 
             
    /**
     * Is the ANSI92 entry level SQL grammar supported? All JDBC CompliantTM drivers must return true. 
     * @throws SQLException if there is an error
     */
    boolean supportsANSI92EntryLevelSQL() throws SQLException; 
             
    /**
     * Is the ANSI92 full SQL grammar supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsANSI92FullSQL() throws SQLException; 
             
    /**
     * Is the ANSI92 intermediate SQL grammar supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsANSI92IntermediateSQL() throws SQLException; 
             
    /**
     * Indicates whether the driver supports batch updates. 
     * @throws SQLException if there is an error
     */
    boolean supportsBatchUpdates() throws SQLException; 
             
    /**
     * Can a catalog name be used in a data manipulation statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsCatalogsInDataManipulation() throws SQLException; 
             
    /**
     * Can a catalog name be used in an index definition statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsCatalogsInIndexDefinitions() throws SQLException; 
             
    /**
     * Can a catalog name be used in a privilege definition statement?
     * @throws SQLException if there is an error
     */
    boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException; 
              
    /**
     * Can a catalog name be used in a procedure call statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsCatalogsInProcedureCalls() throws SQLException; 
             
    /**
     * Can a catalog name be used in a table definition statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsCatalogsInTableDefinitions() throws SQLException; 
             
    /**
     * Is column aliasing supported?  
     * @throws SQLException if there is an error
     */
    boolean supportsColumnAliasing() throws SQLException; 
             
    /**
     * Is the CONVERT function between SQL types supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsConvert() throws SQLException; 
             
    /**
     * Is CONVERT between the given SQL types supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsConvert(int fromType, int toType)  throws SQLException; 
             
    /**
     * Is the ODBC Core SQL grammar supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsCoreSQLGrammar() throws SQLException; 
             
    /**
     * Are correlated subqueries supported? A JDBC CompliantTM driver always returns true. 
     * @throws SQLException if there is an error
     */
    boolean supportsCorrelatedSubqueries() throws SQLException; 
             
    /**
     * Are both data definition and data manipulation statements within a transaction supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException; 
             
    /**
     * Are only data manipulation statements within a transaction supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsDataManipulationTransactionsOnly() throws SQLException; 
             
    /**
     * If table correlation names are supported, are they restricted to be different from the names of the tables? 
     * @throws SQLException if there is an error
     */
    boolean supportsDifferentTableCorrelationNames() throws SQLException; 
             
    /**
     * Are expressions in "ORDER BY" lists supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsExpressionsInOrderBy() throws SQLException; 
             
    /**
     * Is the ODBC Extended SQL grammar supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsExtendedSQLGrammar() throws SQLException; 
             
    /**
     * Are full nested outer joins supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsFullOuterJoins() throws SQLException; 
             
    /**
     * Is some form of "GROUP BY" clause supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsGroupBy() throws SQLException; 
             
    /**
     * Can a "GROUP BY" clause add columns not in the SELECT provided it specifies all the columns in the SELECT? 
     * @throws SQLException if there is an error
     */
    boolean supportsGroupByBeyondSelect() throws SQLException; 
             
    /**
     * Can a "GROUP BY" clause use columns not in the SELECT? 
     * @throws SQLException if there is an error
     */
    boolean supportsGroupByUnrelated() throws SQLException; 
             
    /**
     * Is the SQL Integrity Enhancement Facility supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsIntegrityEnhancementFacility() throws SQLException; 
             
    /**
     * Is the escape character in "LIKE" clauses supported? A JDBC CompliantTM driver always returns true. 
     * @throws SQLException if there is an error
     */
    boolean supportsLikeEscapeClause() throws SQLException; 
             
    /**
     * Is there limited support for outer joins? (This will be true if supportFullOuterJoins is true.) 
     * @throws SQLException if there is an error
     */
    boolean supportsLimitedOuterJoins() throws SQLException; 
             
    /**
     * Is the ODBC Minimum SQL grammar supported? All JDBC CompliantTM drivers must return true. 
     * @throws SQLException if there is an error
     */
    boolean supportsMinimumSQLGrammar() throws SQLException; 
             
    /**
     * Does the database treat mixed case unquoted SQL identifiers as case sensitive and as a result store them in mixed case? A JDBC CompliantTM driver will always return false. 
     * @throws SQLException if there is an error
     */
    boolean supportsMixedCaseIdentifiers() throws SQLException; 
             
    /**
     * Does the database treat mixed case quoted SQL identifiers as case sensitive and as a result store them in mixed case? A JDBC CompliantTM driver will always return true. 
     * @throws SQLException if there is an error
     */
    boolean supportsMixedCaseQuotedIdentifiers() throws SQLException; 
             
    /**
     * Are multiple ResultSet from a single execute supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsMultipleResultSets() throws SQLException; 
             
    /**
     * Can we have multiple transactions open at once (on different connections)? 
     * @throws SQLException if there is an error
     */
    boolean supportsMultipleTransactions() throws SQLException; 
             
    /**
     * Can columns be defined as non-nullable? A JDBC CompliantTM driver always returns true. 
     * @throws SQLException if there is an error
     */
    boolean supportsNonNullableColumns() throws SQLException; 
             
    /**
     * Can cursors remain open across commits? 
     * @throws SQLException if there is an error
     */
    boolean supportsOpenCursorsAcrossCommit() throws SQLException; 
             
    /**
     * Can cursors remain open across rollbacks? 
     * @throws SQLException if there is an error
     */
    boolean supportsOpenCursorsAcrossRollback() throws SQLException; 
             
    /**
     * Can statements remain open across commits? 
     * @throws SQLException if there is an error
     */
    boolean supportsOpenStatementsAcrossCommit() throws SQLException; 
             
    /**
     * Can statements remain open across rollbacks? 
     * @throws SQLException if there is an error
     */
    boolean supportsOpenStatementsAcrossRollback() throws SQLException; 
             
    /**
     * Can an "ORDER BY" clause use columns not in the SELECT statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsOrderByUnrelated() throws SQLException; 
             
    /**
     * Is some form of outer join supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsOuterJoins() throws SQLException; 
             
    /**
     * Is positioned DELETE supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsPositionedDelete() throws SQLException; 
             
    /**
     * Is positioned UPDATE supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsPositionedUpdate() throws SQLException; 
             
    /**
     * Does the database support the concurrency type in combination with the given result set type? 
     * @throws SQLException if there is an error
     */
    boolean supportsResultSetConcurrency(int type, int concurrency)  throws SQLException; 
             
    /**
     * Does the database support the given result set type? 
     * @throws SQLException if there is an error
     */
    boolean supportsResultSetType(int type)  throws SQLException; 
             
    /**
     * Can a schema name be used in a data manipulation statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsSchemasInDataManipulation() throws SQLException; 
             
    /**
     * Can a schema name be used in an index definition statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsSchemasInIndexDefinitions() throws SQLException; 
             
    /**
     * Can a schema name be used in a privilege definition statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsSchemasInPrivilegeDefinitions() throws SQLException; 
             
    /**
     * Can a schema name be used in a procedure call statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsSchemasInProcedureCalls() throws SQLException; 
             
    /**
     * Can a schema name be used in a table definition statement? 
     * @throws SQLException if there is an error
     */
    boolean supportsSchemasInTableDefinitions() throws SQLException; 
             
    /**
     * Is SELECT for UPDATE supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsSelectForUpdate() throws SQLException; 
             
    /**
     * Are stored procedure calls using the stored procedure escape syntax supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsStoredProcedures() throws SQLException; 
             
    /**
     * Are subqueries in comparison expressions supported? A JDBC CompliantTM driver always returns true. 
     * @throws SQLException if there is an error
     */
    boolean supportsSubqueriesInComparisons() throws SQLException; 
             
    /**
     * Are subqueries in 'exists' expressions supported? A JDBC CompliantTM driver always returns true. 
     * @throws SQLException if there is an error
     */
    boolean supportsSubqueriesInExists() throws SQLException; 
             
    /**
     * Are subqueries in 'in' statements supported? A JDBC CompliantTM driver always returns true. 
     * @throws SQLException if there is an error
     */
    boolean supportsSubqueriesInIns() throws SQLException; 
             
    /**
     * Are subqueries in quantified expressions supported? A JDBC CompliantTM driver always returns true. 
     * @throws SQLException if there is an error
     */
    boolean supportsSubqueriesInQuantifieds() throws SQLException; 
             
    /**
     * Are table correlation names supported? A JDBC CompliantTM driver always returns true. 
     * @throws SQLException if there is an error
     */
    boolean supportsTableCorrelationNames() throws SQLException; 
             
    /**
     * Does this database support the given transaction isolation level?
     * @throws SQLException if there is an error
     */
    boolean supportsTransactionIsolationLevel(int level)  throws SQLException; 
              
    /**
     * Are transactions supported? If not, invoking the method commit is a noop and the isolation level is TRANSACTION_NONE. 
     * @throws SQLException if there is an error
     */
    boolean supportsTransactions() throws SQLException; 
             
    /**
     * Is SQL UNION supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsUnion() throws SQLException; 
             
    /**
     * Is SQL UNION ALL supported? 
     * @throws SQLException if there is an error
     */
    boolean supportsUnionAll() throws SQLException; 
             
    /**
     * Indicates whether or not a visible row update can be detected by calling the method ResultSet.rowUpdated. 
     * @throws SQLException if there is an error
     */
    boolean updatesAreDetected(int type)  throws SQLException; 
             
    /**
     * Does the database use a file for each table? 
     * @throws SQLException if there is an error
     */
    boolean usesLocalFilePerTable() throws SQLException; 
             
    /**
     * Does the database store tables in a local file? 
     * @throws SQLException if there is an error
     */
    boolean usesLocalFiles() throws SQLException; 
             









}
