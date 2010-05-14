/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DatabaseMetaDataBase implements DatabaseMetaData {
    // Dividing the database metadata into DatabaseMetaDataBase and
    // DatabaseMetaDataImpl is just a way of keeping the "uninteresting"
    // JDBC stuff and the "interesting" tables as a representation of XML
    // elements separate. It makes it easier to scroll through the classes,
    // that's all.
    protected static Map catalogsResultsetMetadata;
    protected static Map columnsResultsetMetadata;
    protected static Map crossReferencesResultsetMetadata;
    protected static Map primaryKeysResultsetMetadata;
    protected static Map tablesResultsetMetadata;
    protected static Map tableTypesResultsetMetadata;

    static {
        int column = 1;
        catalogsResultsetMetadata = new HashMap();
        catalogsResultsetMetadata.put("TABLE_CAT", new Integer(column++)); //$NON-NLS-1$
        catalogsResultsetMetadata = Collections.unmodifiableMap(catalogsResultsetMetadata);
    }

    static {
        int column = 1;
        columnsResultsetMetadata = new HashMap();
        columnsResultsetMetadata.put("TABLE_CAT", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("TABLE_SCHEM", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("TABLE_NAME", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("COLUMN_NAME", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("DATA_TYPE", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("TYPE_NAME", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("COLUMN_SIZE", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("BUFFER_LENGTH", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("DECIMAL_DIGITS", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("NUM_PREC_RADIX", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("NULLABLE", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("REMARKS", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("COLUMN_DEF", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("SQL_DATA_TYPE", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("SQL_DATETIME_SUB", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("CHAR_OCTET_LENGTH", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("ORDINAL_POSITION", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("IS_NULLABLE", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("SCOPE_CATLOG", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("SCOPE_SCHEMA", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("SCOPE_TABLE", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata.put("SOURCE_DATA_TYPE", new Integer(column++)); //$NON-NLS-1$
        columnsResultsetMetadata = Collections.unmodifiableMap(columnsResultsetMetadata);
    }

    static {
        int column = 1;
        crossReferencesResultsetMetadata = new HashMap();
        crossReferencesResultsetMetadata.put("PKTABLE_CAT", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("PKTABLE_SCHEM", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("PKTABLE_NAME", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("PKCOLUMN_NAME", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("FKTABLE_CAT", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("FKTABLE_SCHEM", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("FKTABLE_NAME", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("FKCOLUMN_NAME", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("KEY_SEQ", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("UPDATE_RULE", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("DELETE_RULE", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("FK_NAME", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("PK_NAME", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata.put("DEFERRABILITY", new Integer(column++)); //$NON-NLS-1$
        crossReferencesResultsetMetadata = Collections.unmodifiableMap(crossReferencesResultsetMetadata);
    }

    static {
        int column = 1;
        primaryKeysResultsetMetadata = new HashMap();
        primaryKeysResultsetMetadata.put("TABLE_CAT", new Integer(column++)); //$NON-NLS-1$
        primaryKeysResultsetMetadata.put("TABLE_SCHEM", new Integer(column++)); //$NON-NLS-1$
        primaryKeysResultsetMetadata.put("TABLE_NAME", new Integer(column++)); //$NON-NLS-1$
        primaryKeysResultsetMetadata.put("COLUMN_NAME", new Integer(column++)); //$NON-NLS-1$
        primaryKeysResultsetMetadata.put("KEY_SEQ", new Integer(column++)); //$NON-NLS-1$
        primaryKeysResultsetMetadata.put("PK_NAME", new Integer(column++)); //$NON-NLS-1$
        primaryKeysResultsetMetadata = Collections.unmodifiableMap(primaryKeysResultsetMetadata);
    }

    static {
        int column = 1;
        tablesResultsetMetadata = new HashMap();
        tablesResultsetMetadata.put("TABLE_CAT", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata.put("TABLE_SCHEM", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata.put("TABLE_NAME", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata.put("TABLE_TYPE", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata.put("REMARKS", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata.put("TYPE_CAT", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata.put("TYPE_SCHEM", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata.put("TYPE_NAME", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata.put("SELF_REFERENCING_COL_NAME", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata.put("REF_GENERATION", new Integer(column++)); //$NON-NLS-1$
        tablesResultsetMetadata = Collections.unmodifiableMap(tablesResultsetMetadata);
    }

    static {
        tableTypesResultsetMetadata = new HashMap();
        tableTypesResultsetMetadata.put("type", new Integer(1)); //$NON-NLS-1$
        tableTypesResultsetMetadata = Collections.unmodifiableMap(tableTypesResultsetMetadata);
    }

    Connection connection;

    /**
     * 
     */
    protected DatabaseMetaDataBase( Connection connection ) {
        this.connection = connection;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseMajorVersion()
     */
    public int getDatabaseMajorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseMinorVersion()
     */
    public int getDatabaseMinorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDefaultTransactionIsolation()
     */
    public int getDefaultTransactionIsolation() {
        return Connection.TRANSACTION_NONE;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
     */
    public int getDriverMajorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
     */
    public int getDriverMinorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getJDBCMajorVersion()
     */
    public int getJDBCMajorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getJDBCMinorVersion()
     */
    public int getJDBCMinorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxBinaryLiteralLength()
     */
    public int getMaxBinaryLiteralLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxCatalogNameLength()
     */
    public int getMaxCatalogNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxCharLiteralLength()
     */
    public int getMaxCharLiteralLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnNameLength()
     */
    public int getMaxColumnNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInGroupBy()
     */
    public int getMaxColumnsInGroupBy() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInIndex()
     */
    public int getMaxColumnsInIndex() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInOrderBy()
     */
    public int getMaxColumnsInOrderBy() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInSelect()
     */
    public int getMaxColumnsInSelect() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxColumnsInTable()
     */
    public int getMaxColumnsInTable() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxConnections()
     */
    public int getMaxConnections() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxCursorNameLength()
     */
    public int getMaxCursorNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxIndexLength()
     */
    public int getMaxIndexLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxProcedureNameLength()
     */
    public int getMaxProcedureNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxRowSize()
     */
    public int getMaxRowSize() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxSchemaNameLength()
     */
    public int getMaxSchemaNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxStatementLength()
     */
    public int getMaxStatementLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxStatements()
     */
    public int getMaxStatements() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxTableNameLength()
     */
    public int getMaxTableNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxTablesInSelect()
     */
    public int getMaxTablesInSelect() {
        return 1;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxUserNameLength()
     */
    public int getMaxUserNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getResultSetHoldability()
     */
    public int getResultSetHoldability() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSQLStateType()
     */
    public int getSQLStateType() {
        return DatabaseMetaData.sqlStateSQL99;
    }

    /**
     * @see java.sql.DatabaseMetaData#allProceduresAreCallable()
     */
    public boolean allProceduresAreCallable() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#allTablesAreSelectable()
     */
    public boolean allTablesAreSelectable() {
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
     */
    public boolean dataDefinitionCausesTransactionCommit() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions()
     */
    public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#doesMaxRowSizeIncludeBlobs()
     */
    public boolean doesMaxRowSizeIncludeBlobs() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#isCatalogAtStart()
     */
    public boolean isCatalogAtStart() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#isReadOnly()
     */
    public boolean isReadOnly() {
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
     */
    public boolean locatorsUpdateCopy() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullPlusNonNullIsNull()
     */
    public boolean nullPlusNonNullIsNull() {
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullsAreSortedAtEnd()
     */
    public boolean nullsAreSortedAtEnd() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullsAreSortedAtStart()
     */
    public boolean nullsAreSortedAtStart() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullsAreSortedHigh()
     */
    public boolean nullsAreSortedHigh() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullsAreSortedLow()
     */
    public boolean nullsAreSortedLow() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesLowerCaseIdentifiers()
     */
    public boolean storesLowerCaseIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesLowerCaseQuotedIdentifiers()
     */
    public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesMixedCaseIdentifiers()
     */
    public boolean storesMixedCaseIdentifiers() {
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesMixedCaseQuotedIdentifiers()
     */
    public boolean storesMixedCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesUpperCaseIdentifiers()
     */
    public boolean storesUpperCaseIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesUpperCaseQuotedIdentifiers()
     */
    public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsANSI92EntryLevelSQL()
     */
    public boolean supportsANSI92EntryLevelSQL() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsANSI92FullSQL()
     */
    public boolean supportsANSI92FullSQL() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsANSI92IntermediateSQL()
     */
    public boolean supportsANSI92IntermediateSQL() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsAlterTableWithAddColumn()
     */
    public boolean supportsAlterTableWithAddColumn() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsAlterTableWithDropColumn()
     */
    public boolean supportsAlterTableWithDropColumn() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
     */
    public boolean supportsBatchUpdates() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInDataManipulation()
     */
    public boolean supportsCatalogsInDataManipulation() {
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInIndexDefinitions()
     */
    public boolean supportsCatalogsInIndexDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInPrivilegeDefinitions()
     */
    public boolean supportsCatalogsInPrivilegeDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInProcedureCalls()
     */
    public boolean supportsCatalogsInProcedureCalls() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInTableDefinitions()
     */
    public boolean supportsCatalogsInTableDefinitions() {
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsColumnAliasing()
     */
    public boolean supportsColumnAliasing() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsConvert()
     */
    public boolean supportsConvert() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCoreSQLGrammar()
     */
    public boolean supportsCoreSQLGrammar() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCorrelatedSubqueries()
     */
    public boolean supportsCorrelatedSubqueries() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsDataDefinitionAndDataManipulationTransactions()
     */
    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsDataManipulationTransactionsOnly()
     */
    public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsDifferentTableCorrelationNames()
     */
    public boolean supportsDifferentTableCorrelationNames() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsExpressionsInOrderBy()
     */
    public boolean supportsExpressionsInOrderBy() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsExtendedSQLGrammar()
     */
    public boolean supportsExtendedSQLGrammar() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsFullOuterJoins()
     */
    public boolean supportsFullOuterJoins() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys()
     */
    public boolean supportsGetGeneratedKeys() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsGroupBy()
     */
    public boolean supportsGroupBy() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsGroupByBeyondSelect()
     */
    public boolean supportsGroupByBeyondSelect() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsGroupByUnrelated()
     */
    public boolean supportsGroupByUnrelated() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsIntegrityEnhancementFacility()
     */
    public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsLikeEscapeClause()
     */
    public boolean supportsLikeEscapeClause() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsLimitedOuterJoins()
     */
    public boolean supportsLimitedOuterJoins() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMinimumSQLGrammar()
     */
    public boolean supportsMinimumSQLGrammar() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMixedCaseIdentifiers()
     */
    public boolean supportsMixedCaseIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMixedCaseQuotedIdentifiers()
     */
    public boolean supportsMixedCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMultipleOpenResults()
     */
    public boolean supportsMultipleOpenResults() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMultipleResultSets()
     */
    public boolean supportsMultipleResultSets() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsMultipleTransactions()
     */
    public boolean supportsMultipleTransactions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsNamedParameters()
     */
    public boolean supportsNamedParameters() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsNonNullableColumns()
     */
    public boolean supportsNonNullableColumns() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossCommit()
     */
    public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOpenCursorsAcrossRollback()
     */
    public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossCommit()
     */
    public boolean supportsOpenStatementsAcrossCommit() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOpenStatementsAcrossRollback()
     */
    public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOrderByUnrelated()
     */
    public boolean supportsOrderByUnrelated() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsOuterJoins()
     */
    public boolean supportsOuterJoins() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsPositionedDelete()
     */
    public boolean supportsPositionedDelete() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsPositionedUpdate()
     */
    public boolean supportsPositionedUpdate() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSavepoints()
     */
    public boolean supportsSavepoints() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInDataManipulation()
     */
    public boolean supportsSchemasInDataManipulation() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInIndexDefinitions()
     */
    public boolean supportsSchemasInIndexDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInPrivilegeDefinitions()
     */
    public boolean supportsSchemasInPrivilegeDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInProcedureCalls()
     */
    public boolean supportsSchemasInProcedureCalls() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInTableDefinitions()
     */
    public boolean supportsSchemasInTableDefinitions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSelectForUpdate()
     */
    public boolean supportsSelectForUpdate() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsStatementPooling()
     */
    public boolean supportsStatementPooling() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsStoredProcedures()
     */
    public boolean supportsStoredProcedures() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSubqueriesInComparisons()
     */
    public boolean supportsSubqueriesInComparisons() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSubqueriesInExists()
     */
    public boolean supportsSubqueriesInExists() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSubqueriesInIns()
     */
    public boolean supportsSubqueriesInIns() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSubqueriesInQuantifieds()
     */
    public boolean supportsSubqueriesInQuantifieds() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsTableCorrelationNames()
     */
    public boolean supportsTableCorrelationNames() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsTransactions()
     */
    public boolean supportsTransactions() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsUnion()
     */
    public boolean supportsUnion() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsUnionAll()
     */
    public boolean supportsUnionAll() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#usesLocalFilePerTable()
     */
    public boolean usesLocalFilePerTable() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#usesLocalFiles()
     */
    public boolean usesLocalFiles() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#deletesAreDetected(int)
     */
    public boolean deletesAreDetected( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#insertsAreDetected(int)
     */
    public boolean insertsAreDetected( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#othersDeletesAreVisible(int)
     */
    public boolean othersDeletesAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#othersInsertsAreVisible(int)
     */
    public boolean othersInsertsAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#othersUpdatesAreVisible(int)
     */
    public boolean othersUpdatesAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#ownDeletesAreVisible(int)
     */
    public boolean ownDeletesAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#ownInsertsAreVisible(int)
     */
    public boolean ownInsertsAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#ownUpdatesAreVisible(int)
     */
    public boolean ownUpdatesAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsResultSetHoldability(int)
     */
    public boolean supportsResultSetHoldability( int holdability ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsResultSetType(int)
     */
    public boolean supportsResultSetType( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsTransactionIsolationLevel(int)
     */
    public boolean supportsTransactionIsolationLevel( int level ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#updatesAreDetected(int)
     */
    public boolean updatesAreDetected( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsConvert(int, int)
     */
    public boolean supportsConvert( int fromType,
                                    int toType ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsResultSetConcurrency(int, int)
     */
    public boolean supportsResultSetConcurrency( int type,
                                                 int concurrency ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getCatalogSeparator()
     */
    public String getCatalogSeparator() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getCatalogTerm()
     */
    public String getCatalogTerm() {
        return "Namespace"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseProductName()
     */
    public String getDatabaseProductName() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseProductVersion()
     */
    public String getDatabaseProductVersion() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverName()
     */
    public String getDriverName() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverVersion()
     */
    public String getDriverVersion() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getExtraNameCharacters()
     */
    public String getExtraNameCharacters() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getIdentifierQuoteString()
     */
    public String getIdentifierQuoteString() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getNumericFunctions()
     */
    public String getNumericFunctions() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getProcedureTerm()
     */
    public String getProcedureTerm() {
        return ""; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getSQLKeywords()
     */
    public String getSQLKeywords() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSchemaTerm()
     */
    public String getSchemaTerm() {
        return ""; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getSearchStringEscape()
     */
    public String getSearchStringEscape() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getStringFunctions()
     */
    public String getStringFunctions() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSystemFunctions()
     */
    public String getSystemFunctions() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getTimeDateFunctions()
     */
    public String getTimeDateFunctions() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getURL()
     */
    public String getURL() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getUserName()
     */
    public String getUserName() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getConnection()
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSchemas()
     */
    public ResultSet getSchemas() {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getTypeInfo()
     */
    public ResultSet getTypeInfo() {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getProcedures( String catalog,
                                    String schemaPattern,
                                    String procedureNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getSuperTables(java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getSuperTables( String catalog,
                                     String schemaPattern,
                                     String tableNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getSuperTypes( String catalog,
                                    String schemaPattern,
                                    String typeNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getTablePrivileges( String catalog,
                                         String schemaPattern,
                                         String tableNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getVersionColumns( String catalog,
                                        String schema,
                                        String table ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getBestRowIdentifier(java.lang.String, java.lang.String, java.lang.String, int, boolean)
     */
    public ResultSet getBestRowIdentifier( String catalog,
                                           String schema,
                                           String table,
                                           int scope,
                                           boolean nullable ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getIndexInfo(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
     */
    public ResultSet getIndexInfo( String catalog,
                                   String schema,
                                   String table,
                                   boolean unique,
                                   boolean approximate ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])
     */
    public ResultSet getUDTs( String catalog,
                              String schemaPattern,
                              String typeNamePattern,
                              int[] types ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getAttributes(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getAttributes( String catalog,
                                    String schemaPattern,
                                    String typeNamePattern,
                                    String attributeNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getColumnPrivileges( String catalog,
                                          String schema,
                                          String table,
                                          String columnNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public ResultSet getProcedureColumns( String catalog,
                                          String schemaPattern,
                                          String procedureNamePattern,
                                          String columnNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
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

}
