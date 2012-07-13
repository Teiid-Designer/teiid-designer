/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.xml.model;

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
    @Override
	public int getDatabaseMajorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseMinorVersion()
     */
    @Override
	public int getDatabaseMinorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDefaultTransactionIsolation()
     */
    @Override
	public int getDefaultTransactionIsolation() {
        return Connection.TRANSACTION_NONE;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverMajorVersion()
     */
    @Override
	public int getDriverMajorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverMinorVersion()
     */
    @Override
	public int getDriverMinorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getJDBCMajorVersion()
     */
    @Override
	public int getJDBCMajorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getJDBCMinorVersion()
     */
    @Override
	public int getJDBCMinorVersion() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxBinaryLiteralLength()
     */
    @Override
	public int getMaxBinaryLiteralLength() {
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
     * @see java.sql.DatabaseMetaData#getMaxProcedureNameLength()
     */
    @Override
	public int getMaxProcedureNameLength() {
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
     * @see java.sql.DatabaseMetaData#getMaxSchemaNameLength()
     */
    @Override
	public int getMaxSchemaNameLength() {
        return 0;
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
        return 1;
    }

    /**
     * @see java.sql.DatabaseMetaData#getMaxUserNameLength()
     */
    @Override
	public int getMaxUserNameLength() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getResultSetHoldability()
     */
    @Override
	public int getResultSetHoldability() {
        return 0;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSQLStateType()
     */
    @Override
	public int getSQLStateType() {
        return DatabaseMetaData.sqlStateSQL99;
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
        return true;
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
     * @see java.sql.DatabaseMetaData#doesMaxRowSizeIncludeBlobs()
     */
    @Override
	public boolean doesMaxRowSizeIncludeBlobs() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#isCatalogAtStart()
     */
    @Override
	public boolean isCatalogAtStart() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#isReadOnly()
     */
    @Override
	public boolean isReadOnly() {
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
     */
    @Override
	public boolean locatorsUpdateCopy() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullPlusNonNullIsNull()
     */
    @Override
	public boolean nullPlusNonNullIsNull() {
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#nullsAreSortedAtEnd()
     */
    @Override
	public boolean nullsAreSortedAtEnd() {
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
     * @see java.sql.DatabaseMetaData#storesLowerCaseIdentifiers()
     */
    @Override
	public boolean storesLowerCaseIdentifiers() {
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
     * @see java.sql.DatabaseMetaData#storesMixedCaseIdentifiers()
     */
    @Override
	public boolean storesMixedCaseIdentifiers() {
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#storesMixedCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesMixedCaseQuotedIdentifiers() {
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
     * @see java.sql.DatabaseMetaData#storesUpperCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesUpperCaseQuotedIdentifiers() {
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
     * @see java.sql.DatabaseMetaData#supportsANSI92FullSQL()
     */
    @Override
	public boolean supportsANSI92FullSQL() {
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
     * @see java.sql.DatabaseMetaData#supportsBatchUpdates()
     */
    @Override
	public boolean supportsBatchUpdates() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsCatalogsInDataManipulation()
     */
    @Override
	public boolean supportsCatalogsInDataManipulation() {
        return true;
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
        return true;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsColumnAliasing()
     */
    @Override
	public boolean supportsColumnAliasing() {
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
     * @see java.sql.DatabaseMetaData#supportsCoreSQLGrammar()
     */
    @Override
	public boolean supportsCoreSQLGrammar() {
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
     * @see java.sql.DatabaseMetaData#supportsExtendedSQLGrammar()
     */
    @Override
	public boolean supportsExtendedSQLGrammar() {
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
     * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys()
     */
    @Override
	public boolean supportsGetGeneratedKeys() {
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
     * @see java.sql.DatabaseMetaData#supportsGroupByBeyondSelect()
     */
    @Override
	public boolean supportsGroupByBeyondSelect() {
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
     * @see java.sql.DatabaseMetaData#supportsIntegrityEnhancementFacility()
     */
    @Override
	public boolean supportsIntegrityEnhancementFacility() {
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
     * @see java.sql.DatabaseMetaData#supportsLimitedOuterJoins()
     */
    @Override
	public boolean supportsLimitedOuterJoins() {
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
     * @see java.sql.DatabaseMetaData#supportsMixedCaseIdentifiers()
     */
    @Override
	public boolean supportsMixedCaseIdentifiers() {
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
     * @see java.sql.DatabaseMetaData#supportsMultipleOpenResults()
     */
    @Override
	public boolean supportsMultipleOpenResults() {
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
     * @see java.sql.DatabaseMetaData#supportsNamedParameters()
     */
    @Override
	public boolean supportsNamedParameters() {
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
     * @see java.sql.DatabaseMetaData#supportsOrderByUnrelated()
     */
    @Override
	public boolean supportsOrderByUnrelated() {
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
     * @see java.sql.DatabaseMetaData#supportsSavepoints()
     */
    @Override
	public boolean supportsSavepoints() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsSchemasInDataManipulation()
     */
    @Override
	public boolean supportsSchemasInDataManipulation() {
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
     * @see java.sql.DatabaseMetaData#supportsSelectForUpdate()
     */
    @Override
	public boolean supportsSelectForUpdate() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsStatementPooling()
     */
    @Override
	public boolean supportsStatementPooling() {
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
     * @see java.sql.DatabaseMetaData#supportsTableCorrelationNames()
     */
    @Override
	public boolean supportsTableCorrelationNames() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsTransactions()
     */
    @Override
	public boolean supportsTransactions() {
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
     * @see java.sql.DatabaseMetaData#usesLocalFilePerTable()
     */
    @Override
	public boolean usesLocalFilePerTable() {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#usesLocalFiles()
     */
    @Override
	public boolean usesLocalFiles() {
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
     * @see java.sql.DatabaseMetaData#othersUpdatesAreVisible(int)
     */
    @Override
	public boolean othersUpdatesAreVisible( int type ) {
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
     * @see java.sql.DatabaseMetaData#ownUpdatesAreVisible(int)
     */
    @Override
	public boolean ownUpdatesAreVisible( int type ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsResultSetHoldability(int)
     */
    @Override
	public boolean supportsResultSetHoldability( int holdability ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#supportsResultSetType(int)
     */
    @Override
	public boolean supportsResultSetType( int type ) {
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
     * @see java.sql.DatabaseMetaData#updatesAreDetected(int)
     */
    @Override
	public boolean updatesAreDetected( int type ) {
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
     * @see java.sql.DatabaseMetaData#supportsResultSetConcurrency(int, int)
     */
    @Override
	public boolean supportsResultSetConcurrency( int type,
                                                 int concurrency ) {
        return false;
    }

    /**
     * @see java.sql.DatabaseMetaData#getCatalogSeparator()
     */
    @Override
	public String getCatalogSeparator() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getCatalogTerm()
     */
    @Override
	public String getCatalogTerm() {
        return "Namespace"; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseProductName()
     */
    @Override
	public String getDatabaseProductName() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDatabaseProductVersion()
     */
    @Override
	public String getDatabaseProductVersion() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverName()
     */
    @Override
	public String getDriverName() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getDriverVersion()
     */
    @Override
	public String getDriverVersion() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getExtraNameCharacters()
     */
    @Override
	public String getExtraNameCharacters() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getIdentifierQuoteString()
     */
    @Override
	public String getIdentifierQuoteString() {
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
     * @see java.sql.DatabaseMetaData#getProcedureTerm()
     */
    @Override
	public String getProcedureTerm() {
        return ""; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getSQLKeywords()
     */
    @Override
	public String getSQLKeywords() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSchemaTerm()
     */
    @Override
	public String getSchemaTerm() {
        return ""; //$NON-NLS-1$
    }

    /**
     * @see java.sql.DatabaseMetaData#getSearchStringEscape()
     */
    @Override
	public String getSearchStringEscape() {
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
     * @see java.sql.DatabaseMetaData#getURL()
     */
    @Override
	public String getURL() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getUserName()
     */
    @Override
	public String getUserName() {
        return null;
    }

    /**
     * @see java.sql.DatabaseMetaData#getConnection()
     */
    @Override
	public Connection getConnection() {
        return connection;
    }

    /**
     * @see java.sql.DatabaseMetaData#getSchemas()
     */
    @Override
	public ResultSet getSchemas() {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getTypeInfo()
     */
    @Override
	public ResultSet getTypeInfo() {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getProcedures(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getProcedures( String catalog,
                                    String schemaPattern,
                                    String procedureNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getSuperTables(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getSuperTables( String catalog,
                                     String schemaPattern,
                                     String tableNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getSuperTypes(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getSuperTypes( String catalog,
                                    String schemaPattern,
                                    String typeNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getTablePrivileges(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getTablePrivileges( String catalog,
                                         String schemaPattern,
                                         String tableNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getVersionColumns(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getVersionColumns( String catalog,
                                        String schema,
                                        String table ) {
        return new MapResultSet(new ArrayList(), new HashMap());
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
        return new MapResultSet(new ArrayList(), new HashMap());
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
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getUDTs(java.lang.String, java.lang.String, java.lang.String, int[])
     */
    @Override
	public ResultSet getUDTs( String catalog,
                              String schemaPattern,
                              String typeNamePattern,
                              int[] types ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getAttributes(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getAttributes( String catalog,
                                    String schemaPattern,
                                    String typeNamePattern,
                                    String attributeNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getColumnPrivileges(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
	public ResultSet getColumnPrivileges( String catalog,
                                          String schema,
                                          String table,
                                          String columnNamePattern ) {
        return new MapResultSet(new ArrayList(), new HashMap());
    }

    /**
     * @see java.sql.DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
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
