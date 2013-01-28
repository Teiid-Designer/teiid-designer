/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;

/**
 * FakeDatabaseMetaData
 */
public class FakeDatabaseMetaData implements DatabaseMetaData {

    /**
     * Construct an instance of FakeDatabaseMetaData.
     */
    public FakeDatabaseMetaData() {
        super();
    }

    /**
     * @see java.sql.DatabaseMetaData#allProceduresAreCallable()
     */
    @Override
	public boolean allProceduresAreCallable() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#allTablesAreSelectable()
     */
    @Override
	public boolean allTablesAreSelectable() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getURL()
     */
    @Override
	public String getURL() {
        return "jdbc:fake:dbmetadata"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getUserName()
     */
    @Override
	public String getUserName() {
        return "PARTSSUPPLIER"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#isReadOnly()
     */
    @Override
	public boolean isReadOnly() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullsAreSortedHigh()
     */
    @Override
	public boolean nullsAreSortedHigh() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullsAreSortedLow()
     */
    @Override
	public boolean nullsAreSortedLow() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullsAreSortedAtStart()
     */
    @Override
	public boolean nullsAreSortedAtStart() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullsAreSortedAtEnd()
     */
    @Override
	public boolean nullsAreSortedAtEnd() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseProductName()
     */
    @Override
	public String getDatabaseProductName() {
        return "FakeDatabase"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseProductVersion()
     */
    @Override
	public String getDatabaseProductVersion() {
        return "1.1.1"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverName()
     */
    @Override
	public String getDriverName() {
        return "FakeJDBCDriver"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverVersion()
     */
    @Override
	public String getDriverVersion() {
        return "1.3"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
     */
    @Override
	public int getDriverMajorVersion() {
        return 1;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
     */
    @Override
	public int getDriverMinorVersion() {
        return 3;
    }

    /**
     * @see java.sql.DatabaseMetaData#usesLocalFiles()
     */
    @Override
	public boolean usesLocalFiles() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#usesLocalFilePerTable()
     */
    @Override
	public boolean usesLocalFilePerTable() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMixedCaseIdentifiers()
     */
    @Override
	public boolean supportsMixedCaseIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesUpperCaseIdentifiers()
     */
    @Override
	public boolean storesUpperCaseIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesLowerCaseIdentifiers()
     */
    @Override
	public boolean storesLowerCaseIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesMixedCaseIdentifiers()
     */
    @Override
	public boolean storesMixedCaseIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMixedCaseQuotedIdentifiers()
     */
    @Override
	public boolean supportsMixedCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesUpperCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesLowerCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesMixedCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesMixedCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getIdentifierQuoteString()
     */
    @Override
	public String getIdentifierQuoteString() {
        return "\""; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getSQLKeywords()
     */
    @Override
	public String getSQLKeywords() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getNumericFunctions()
     */
    @Override
	public String getNumericFunctions() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getStringFunctions()
     */
    @Override
	public String getStringFunctions() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSystemFunctions()
     */
    @Override
	public String getSystemFunctions() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getTimeDateFunctions()
     */
    @Override
	public String getTimeDateFunctions() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSearchStringEscape()
     */
    @Override
	public String getSearchStringEscape() {
        return ""; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getExtraNameCharacters()
     */
    @Override
	public String getExtraNameCharacters() {
        return ""; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsAlterTableWithAddColumn()
     */
    @Override
	public boolean supportsAlterTableWithAddColumn() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsAlterTableWithDropColumn()
     */
    @Override
	public boolean supportsAlterTableWithDropColumn() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsColumnAliasing()
     */
    @Override
	public boolean supportsColumnAliasing() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullPlusNonNullIsNull()
     */
    @Override
	public boolean nullPlusNonNullIsNull() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsConvert()
     */
    @Override
	public boolean supportsConvert() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsConvert(int, int)
     */
    @Override
	public boolean supportsConvert( int fromType,
                                    int toType ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsTableCorrelationNames()
     */
    @Override
	public boolean supportsTableCorrelationNames() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsDifferentTableCorrelationNames()
     */
    @Override
	public boolean supportsDifferentTableCorrelationNames() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsExpressionsInOrderBy()
     */
    @Override
	public boolean supportsExpressionsInOrderBy() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOrderByUnrelated()
     */
    @Override
	public boolean supportsOrderByUnrelated() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsGroupBy()
     */
    @Override
	public boolean supportsGroupBy() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsGroupByUnrelated()
     */
    @Override
	public boolean supportsGroupByUnrelated() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsGroupByBeyondSelect()
     */
    @Override
	public boolean supportsGroupByBeyondSelect() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsLikeEscapeClause()
     */
    @Override
	public boolean supportsLikeEscapeClause() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMultipleResultSets()
     */
    @Override
	public boolean supportsMultipleResultSets() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMultipleTransactions()
     */
    @Override
	public boolean supportsMultipleTransactions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsNonNullableColumns()
     */
    @Override
	public boolean supportsNonNullableColumns() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMinimumSQLGrammar()
     */
    @Override
	public boolean supportsMinimumSQLGrammar() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCoreSQLGrammar()
     */
    @Override
	public boolean supportsCoreSQLGrammar() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsExtendedSQLGrammar()
     */
    @Override
	public boolean supportsExtendedSQLGrammar() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsANSI92EntryLevelSQL()
     */
    @Override
	public boolean supportsANSI92EntryLevelSQL() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsANSI92IntermediateSQL()
     */
    @Override
	public boolean supportsANSI92IntermediateSQL() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsANSI92FullSQL()
     */
    @Override
	public boolean supportsANSI92FullSQL() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsIntegrityEnhancementFacility()
     */
    @Override
	public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOuterJoins()
     */
    @Override
	public boolean supportsOuterJoins() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsFullOuterJoins()
     */
    @Override
	public boolean supportsFullOuterJoins() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsLimitedOuterJoins()
     */
    @Override
	public boolean supportsLimitedOuterJoins() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSchemaTerm()
     */
    @Override
	public String getSchemaTerm() {
        return "Schema"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getProcedureTerm()
     */
    @Override
	public String getProcedureTerm() {
        return "Procedure"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getCatalogTerm()
     */
    @Override
	public String getCatalogTerm() {
        return "Catalog"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#isCatalogAtStart()
     */
    @Override
	public boolean isCatalogAtStart() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getCatalogSeparator()
     */
    @Override
	public String getCatalogSeparator() {
        return "."; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInDataManipulation()
     */
    @Override
	public boolean supportsSchemasInDataManipulation() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInProcedureCalls()
     */
    @Override
	public boolean supportsSchemasInProcedureCalls() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInTableDefinitions()
     */
    @Override
	public boolean supportsSchemasInTableDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInIndexDefinitions()
     */
    @Override
	public boolean supportsSchemasInIndexDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInPrivilegeDefinitions()
     */
    @Override
	public boolean supportsSchemasInPrivilegeDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInDataManipulation()
     */
    @Override
	public boolean supportsCatalogsInDataManipulation() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInProcedureCalls()
     */
    @Override
	public boolean supportsCatalogsInProcedureCalls() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInTableDefinitions()
     */
    @Override
	public boolean supportsCatalogsInTableDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInIndexDefinitions()
     */
    @Override
	public boolean supportsCatalogsInIndexDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInPrivilegeDefinitions()
     */
    @Override
	public boolean supportsCatalogsInPrivilegeDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsPositionedDelete()
     */
    @Override
	public boolean supportsPositionedDelete() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsPositionedUpdate()
     */
    @Override
	public boolean supportsPositionedUpdate() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSelectForUpdate()
     */
    @Override
	public boolean supportsSelectForUpdate() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsStoredProcedures()
     */
    @Override
	public boolean supportsStoredProcedures() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSubqueriesInComparisons()
     */
    @Override
	public boolean supportsSubqueriesInComparisons() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSubqueriesInExists()
     */
    @Override
	public boolean supportsSubqueriesInExists() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSubqueriesInIns()
     */
    @Override
	public boolean supportsSubqueriesInIns() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSubqueriesInQuantifieds()
     */
    @Override
	public boolean supportsSubqueriesInQuantifieds() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCorrelatedSubqueries()
     */
    @Override
	public boolean supportsCorrelatedSubqueries() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsUnion()
     */
    @Override
	public boolean supportsUnion() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsUnionAll()
     */
    @Override
	public boolean supportsUnionAll() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossCommit()
     */
    @Override
	public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossRollback()
     */
    @Override
	public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossCommit()
     */
    @Override
	public boolean supportsOpenStatementsAcrossCommit() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossRollback()
     */
    @Override
	public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxBinaryLiteralLength()
     */
    @Override
	public int getMaxBinaryLiteralLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxCharLiteralLength()
     */
    @Override
	public int getMaxCharLiteralLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnNameLength()
     */
    @Override
	public int getMaxColumnNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInGroupBy()
     */
    @Override
	public int getMaxColumnsInGroupBy() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInIndex()
     */
    @Override
	public int getMaxColumnsInIndex() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInOrderBy()
     */
    @Override
	public int getMaxColumnsInOrderBy() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInSelect()
     */
    @Override
	public int getMaxColumnsInSelect() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInTable()
     */
    @Override
	public int getMaxColumnsInTable() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxConnections()
     */
    @Override
	public int getMaxConnections() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxCursorNameLength()
     */
    @Override
	public int getMaxCursorNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxIndexLength()
     */
    @Override
	public int getMaxIndexLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxSchemaNameLength()
     */
    @Override
	public int getMaxSchemaNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxProcedureNameLength()
     */
    @Override
	public int getMaxProcedureNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxCatalogNameLength()
     */
    @Override
	public int getMaxCatalogNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxRowSize()
     */
    @Override
	public int getMaxRowSize() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#doesMaxRowSizeIncludeBlobs()
     */
    @Override
	public boolean doesMaxRowSizeIncludeBlobs() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxStatementLength()
     */
    @Override
	public int getMaxStatementLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxStatements()
     */
    @Override
	public int getMaxStatements() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxTableNameLength()
     */
    @Override
	public int getMaxTableNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxTablesInSelect()
     */
    @Override
	public int getMaxTablesInSelect() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxUserNameLength()
     */
    @Override
	public int getMaxUserNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDefaultTransactionIsolation()
     */
    @Override
	public int getDefaultTransactionIsolation() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsTransactions()
     */
    @Override
	public boolean supportsTransactions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsTransactionIsolationLevel(int)
     */
    @Override
	public boolean supportsTransactionIsolationLevel( int level ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsDataDefinitionAndDataManipulationTransactions()
     */
    @Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsDataManipulationTransactionsOnly()
     */
    @Override
	public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
     */
    @Override
	public boolean dataDefinitionCausesTransactionCommit() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions()
     */
    @Override
	public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getProcedures( String catalog,
                                    String schemaPattern,
                                    String procedureNamePattern ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getProcedureColumns( String catalog,
                                          String schemaPattern,
                                          String procedureNamePattern,
                                          String columnNamePattern ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getTables(java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
	public ResultSet getTables( String catalog,
                                String schemaPattern,
                                String tableNamePattern,
                                String[] types ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getSchemas()
     */
    @Override
	public ResultSet getSchemas() {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getCatalogs()
     */
    @Override
	public ResultSet getCatalogs() {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getTableTypes()
     */
    @Override
	public ResultSet getTableTypes() {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getColumns( String catalog,
                                 String schemaPattern,
                                 String tableNamePattern,
                                 String columnNamePattern ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getColumnPrivileges( String catalog,
                                          String schema,
                                          String table,
                                          String columnNamePattern ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getTablePrivileges( String catalog,
                                         String schemaPattern,
                                         String tableNamePattern ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getBestRowIdentifier(java.lang.String, java.lang.String, java.lang.String, int, boolean)
     */
    @Override
	public ResultSet getBestRowIdentifier( String catalog,
                                           String schema,
                                           String table,
                                           int scope,
                                           boolean nullable ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getVersionColumns( String catalog,
                                        String schema,
                                        String table ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getPrimaryKeys(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getPrimaryKeys( String catalog,
                                     String schema,
                                     String table ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getImportedKeys(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getImportedKeys( String catalog,
                                      String schema,
                                      String table ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getExportedKeys(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getExportedKeys( String catalog,
                                      String schema,
                                      String table ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getCrossReference(java.lang.String, java.lang.String, java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getCrossReference( String primaryCatalog,
                                        String primarySchema,
                                        String primaryTable,
                                        String foreignCatalog,
                                        String foreignSchema,
                                        String foreignTable ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getTypeInfo()
     */
    @Override
	public ResultSet getTypeInfo() {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
     */
    @Override
	public ResultSet getIndexInfo( String catalog,
                                   String schema,
                                   String table,
                                   boolean unique,
                                   boolean approximate ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsResultSetType(int)
     */
    @Override
	public boolean supportsResultSetType( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsResultSetConcurrency(int, int)
     */
    @Override
	public boolean supportsResultSetConcurrency( int type,
                                                 int concurrency ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#ownUpdatesAreVisible(int)
     */
    @Override
	public boolean ownUpdatesAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#ownDeletesAreVisible(int)
     */
    @Override
	public boolean ownDeletesAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#ownInsertsAreVisible(int)
     */
    @Override
	public boolean ownInsertsAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#othersUpdatesAreVisible(int)
     */
    @Override
	public boolean othersUpdatesAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#othersDeletesAreVisible(int)
     */
    @Override
	public boolean othersDeletesAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#othersInsertsAreVisible(int)
     */
    @Override
	public boolean othersInsertsAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#updatesAreDetected(int)
     */
    @Override
	public boolean updatesAreDetected( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#deletesAreDetected(int)
     */
    @Override
	public boolean deletesAreDetected( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#insertsAreDetected(int)
     */
    @Override
	public boolean insertsAreDetected( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
     */
    @Override
	public boolean supportsBatchUpdates() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])
     */
    @Override
	public ResultSet getUDTs( String catalog,
                              String schemaPattern,
                              String typeNamePattern,
                              int[] types ) {
        return new FakeResultSet();
    }

    /**
     * @see java.sql.DatabaseMetaData#getConnection()
     */
    @Override
	public Connection getConnection() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getAttributes(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getAttributes( String catalog,
                                    String schemaPattern,
                                    String typeNamePattern,
                                    String attributeNamePattern ) {
        /* Implement for JDBC 3.0 */
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseMajorVersion()
     */
    @Override
	public int getDatabaseMajorVersion() {
        /* Implement for JDBC 3.0 */
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseMinorVersion()
     */
    @Override
	public int getDatabaseMinorVersion() {
        /* Implement for JDBC 3.0 */
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getJDBCMajorVersion()
     */
    @Override
	public int getJDBCMajorVersion() {
        /* Implement for JDBC 3.0 */
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getJDBCMinorVersion()
     */
    @Override
	public int getJDBCMinorVersion() {
        /* Implement for JDBC 3.0 */
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getResultSetHoldability()
     */
    @Override
	public int getResultSetHoldability() {
        /* Implement for JDBC 3.0 */
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSQLStateType()
     */
    @Override
	public int getSQLStateType() {
        /* Implement for JDBC 3.0 */
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSuperTables(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getSuperTables( String catalog,
                                     String schemaPattern,
                                     String tableNamePattern ) {
        /* Implement for JDBC 3.0 */
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getSuperTypes( String catalog,
                                    String schemaPattern,
                                    String typeNamePattern ) {
        /* Implement for JDBC 3.0 */
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
     */
    @Override
	public boolean locatorsUpdateCopy() {
        /* Implement for JDBC 3.0 */
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys()
     */
    @Override
	public boolean supportsGetGeneratedKeys() {
        /* Implement for JDBC 3.0 */
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMultipleOpenResults()
     */
    @Override
	public boolean supportsMultipleOpenResults() {
        /* Implement for JDBC 3.0 */
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsNamedParameters()
     */
    @Override
	public boolean supportsNamedParameters() {
        /* Implement for JDBC 3.0 */
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsResultSetHoldability(int)
     */
    @Override
	public boolean supportsResultSetHoldability( int holdability ) {
        /* Implement for JDBC 3.0 */
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSavepoints()
     */
    @Override
	public boolean supportsSavepoints() {
        /* Implement for JDBC 3.0 */
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsStatementPooling()
     */
    @Override
	public boolean supportsStatementPooling() {
        /* Implement for JDBC 3.0 */
        return false;
    }

    @Override
    public boolean autoCommitFailureClosesAllResultSets() {

        return false;
    }

    @Override
    public ResultSet getClientInfoProperties() {

        return null;
    }

    @Override
    public ResultSet getFunctionColumns( String catalog,
                                         String schemaPattern,
                                         String functionNamePattern,
                                         String columnNamePattern ) {

        return null;
    }

    @Override
    public ResultSet getFunctions( String catalog,
                                   String schemaPattern,
                                   String functionNamePattern ) {

        return null;
    }

    @Override
    public RowIdLifetime getRowIdLifetime() {

        return null;
    }

    @Override
    public ResultSet getSchemas( String catalog,
                                 String schemaPattern ) {

        return null;
    }

    @Override
    public boolean supportsStoredFunctionsUsingCallSyntax() {

        return false;
    }

    @Override
    public boolean isWrapperFor( Class<?> iface ) {

        return false;
    }

    @Override
    public <T> T unwrap( Class<T> iface ) {

        return null;
    }

    public ResultSet getPseudoColumns(String catalog,
                                      String schemaPattern,
                                      String tableNamePattern,
                                      String columnNamePattern) throws SQLException {
        return null;
    }

    public boolean generatedKeyAlwaysReturned() throws SQLException {
        return false;
    }

}
