/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import java.sql.ResultSet;

import org.teiid.designer.jdbc.metadata.Capabilities;

/**
 * FakeCapabilities
 */
public class FakeCapabilities implements Capabilities {

    private boolean supportsCatalogs;
    private boolean supportsSchemas;

    /**
     * Construct an instance of FakeCapabilities.
     */
    public FakeCapabilities() {
        super();
        this.supportsCatalogs = false;
        this.supportsSchemas = true;
    }

    public void setSupportsCatalogs( boolean supports ) {
        this.supportsCatalogs = supports;
    }

    public void setSupportsSchemas( boolean supports ) {
        this.supportsSchemas = supports;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogs()
     */
    @Override
	public boolean supportsCatalogs() {
        return this.supportsCatalogs;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemas()
     */
    @Override
	public boolean supportsSchemas() {
        return this.supportsSchemas;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getTableTypes()
     */
    @Override
	public String[] getTableTypes() {
        return new String[] {"Table", "View"}; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#allProceduresAreCallable()
     */
    @Override
	public boolean allProceduresAreCallable() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#allTablesAreSelectable()
     */
    @Override
	public boolean allTablesAreSelectable() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#dataDefinitionCausesTransactionCommit()
     */
    @Override
	public boolean dataDefinitionCausesTransactionCommit() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#dataDefinitionIgnoredInTransactions()
     */
    @Override
	public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#deletesAreDetected(int)
     */
    @Override
	public boolean deletesAreDetected( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#doesMaxRowSizeIncludeBlobs()
     */
    @Override
	public boolean doesMaxRowSizeIncludeBlobs() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getCatalogSeparator()
     */
    @Override
	public String getCatalogSeparator() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getCatalogTerm()
     */
    @Override
	public String getCatalogTerm() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getExtraNameCharacters()
     */
    @Override
	public String getExtraNameCharacters() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getIdentifierQuoteString()
     */
    @Override
	public String getIdentifierQuoteString() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxBinaryLiteralLength()
     */
    @Override
	public int getMaxBinaryLiteralLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxCatalogNameLength()
     */
    @Override
	public int getMaxCatalogNameLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxCharLiteralLength()
     */
    @Override
	public int getMaxCharLiteralLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnNameLength()
     */
    @Override
	public int getMaxColumnNameLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInGroupBy()
     */
    @Override
	public int getMaxColumnsInGroupBy() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInIndex()
     */
    @Override
	public int getMaxColumnsInIndex() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInOrderBy()
     */
    @Override
	public int getMaxColumnsInOrderBy() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInSelect()
     */
    @Override
	public int getMaxColumnsInSelect() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInTable()
     */
    @Override
	public int getMaxColumnsInTable() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxConnections()
     */
    @Override
	public int getMaxConnections() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxCursorNameLength()
     */
    @Override
	public int getMaxCursorNameLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxIndexLength()
     */
    @Override
	public int getMaxIndexLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxProcedureNameLength()
     */
    @Override
	public int getMaxProcedureNameLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxRowSize()
     */
    @Override
	public int getMaxRowSize() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxSchemaNameLength()
     */
    @Override
	public int getMaxSchemaNameLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxStatementLength()
     */
    @Override
	public int getMaxStatementLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxStatements()
     */
    @Override
	public int getMaxStatements() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxTableNameLength()
     */
    @Override
	public int getMaxTableNameLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxTablesInSelect()
     */
    @Override
	public int getMaxTablesInSelect() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getMaxUserNameLength()
     */
    @Override
	public int getMaxUserNameLength() {
        return 0;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getNumericFunctions()
     */
    @Override
	public String getNumericFunctions() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getProcedureTerm()
     */
    @Override
	public String getProcedureTerm() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getSchemaTerm()
     */
    @Override
	public String getSchemaTerm() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getSearchStringEscape()
     */
    @Override
	public String getSearchStringEscape() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getSQLKeywords()
     */
    @Override
	public String getSQLKeywords() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getStringFunctions()
     */
    @Override
	public String getStringFunctions() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getSystemFunctions()
     */
    @Override
	public String getSystemFunctions() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getTimeDateFunctions()
     */
    @Override
	public String getTimeDateFunctions() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#getTypeInfo()
     */
    @Override
	public ResultSet getTypeInfo() {
        return null;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#insertsAreDetected(int)
     */
    @Override
	public boolean insertsAreDetected( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#isCatalogAtStart()
     */
    @Override
	public boolean isCatalogAtStart() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#isReadOnly()
     */
    @Override
	public boolean isReadOnly() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#nullPlusNonNullIsNull()
     */
    @Override
	public boolean nullPlusNonNullIsNull() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#nullsAreSortedAtEnd()
     */
    @Override
	public boolean nullsAreSortedAtEnd() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#nullsAreSortedAtStart()
     */
    @Override
	public boolean nullsAreSortedAtStart() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#nullsAreSortedHigh()
     */
    @Override
	public boolean nullsAreSortedHigh() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#nullsAreSortedLow()
     */
    @Override
	public boolean nullsAreSortedLow() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#othersDeletesAreVisible(int)
     */
    @Override
	public boolean othersDeletesAreVisible( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#othersInsertsAreVisible(int)
     */
    @Override
	public boolean othersInsertsAreVisible( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#othersUpdatesAreVisible(int)
     */
    @Override
	public boolean othersUpdatesAreVisible( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#ownDeletesAreVisible(int)
     */
    @Override
	public boolean ownDeletesAreVisible( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#ownInsertsAreVisible(int)
     */
    @Override
	public boolean ownInsertsAreVisible( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#ownUpdatesAreVisible(int)
     */
    @Override
	public boolean ownUpdatesAreVisible( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#storesLowerCaseIdentifiers()
     */
    @Override
	public boolean storesLowerCaseIdentifiers() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#storesLowerCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#storesMixedCaseIdentifiers()
     */
    @Override
	public boolean storesMixedCaseIdentifiers() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#storesMixedCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesMixedCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#storesUpperCaseIdentifiers()
     */
    @Override
	public boolean storesUpperCaseIdentifiers() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#storesUpperCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsAlterTableWithAddColumn()
     */
    @Override
	public boolean supportsAlterTableWithAddColumn() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsAlterTableWithDropColumn()
     */
    @Override
	public boolean supportsAlterTableWithDropColumn() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsANSI92EntryLevelSQL()
     */
    @Override
	public boolean supportsANSI92EntryLevelSQL() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsANSI92FullSQL()
     */
    @Override
	public boolean supportsANSI92FullSQL() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsANSI92IntermediateSQL()
     */
    @Override
	public boolean supportsANSI92IntermediateSQL() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsBatchUpdates()
     */
    @Override
	public boolean supportsBatchUpdates() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInDataManipulation()
     */
    @Override
	public boolean supportsCatalogsInDataManipulation() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInIndexDefinitions()
     */
    @Override
	public boolean supportsCatalogsInIndexDefinitions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInPrivilegeDefinitions()
     */
    @Override
	public boolean supportsCatalogsInPrivilegeDefinitions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInProcedureCalls()
     */
    @Override
	public boolean supportsCatalogsInProcedureCalls() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInTableDefinitions()
     */
    @Override
	public boolean supportsCatalogsInTableDefinitions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsColumnAliasing()
     */
    @Override
	public boolean supportsColumnAliasing() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsConvert()
     */
    @Override
	public boolean supportsConvert() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsConvert(int, int)
     */
    @Override
	public boolean supportsConvert( int fromType,
                                    int toType ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsCoreSQLGrammar()
     */
    @Override
	public boolean supportsCoreSQLGrammar() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsCorrelatedSubqueries()
     */
    @Override
	public boolean supportsCorrelatedSubqueries() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsDataDefinitionAndDataManipulationTransactions()
     */
    @Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsDataManipulationTransactionsOnly()
     */
    @Override
	public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsDifferentTableCorrelationNames()
     */
    @Override
	public boolean supportsDifferentTableCorrelationNames() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsExpressionsInOrderBy()
     */
    @Override
	public boolean supportsExpressionsInOrderBy() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsExtendedSQLGrammar()
     */
    @Override
	public boolean supportsExtendedSQLGrammar() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsFullOuterJoins()
     */
    @Override
	public boolean supportsFullOuterJoins() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsGroupBy()
     */
    @Override
	public boolean supportsGroupBy() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsGroupByBeyondSelect()
     */
    @Override
	public boolean supportsGroupByBeyondSelect() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsGroupByUnrelated()
     */
    @Override
	public boolean supportsGroupByUnrelated() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsIntegrityEnhancementFacility()
     */
    @Override
	public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsLikeEscapeClause()
     */
    @Override
	public boolean supportsLikeEscapeClause() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsLimitedOuterJoins()
     */
    @Override
	public boolean supportsLimitedOuterJoins() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsMinimumSQLGrammar()
     */
    @Override
	public boolean supportsMinimumSQLGrammar() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsMixedCaseIdentifiers()
     */
    @Override
	public boolean supportsMixedCaseIdentifiers() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsMixedCaseQuotedIdentifiers()
     */
    @Override
	public boolean supportsMixedCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsMultipleResultSets()
     */
    @Override
	public boolean supportsMultipleResultSets() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsMultipleTransactions()
     */
    @Override
	public boolean supportsMultipleTransactions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsNonNullableColumns()
     */
    @Override
	public boolean supportsNonNullableColumns() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsOpenCursorsAcrossCommit()
     */
    @Override
	public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsOpenCursorsAcrossRollback()
     */
    @Override
	public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsOpenStatementsAcrossCommit()
     */
    @Override
	public boolean supportsOpenStatementsAcrossCommit() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsOpenStatementsAcrossRollback()
     */
    @Override
	public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsOrderByUnrelated()
     */
    @Override
	public boolean supportsOrderByUnrelated() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsOuterJoins()
     */
    @Override
	public boolean supportsOuterJoins() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsPositionedDelete()
     */
    @Override
	public boolean supportsPositionedDelete() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsPositionedUpdate()
     */
    @Override
	public boolean supportsPositionedUpdate() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsResultSetConcurrency(int, int)
     */
    @Override
	public boolean supportsResultSetConcurrency( int type,
                                                 int concurrency ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsResultSetType(int)
     */
    @Override
	public boolean supportsResultSetType( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInDataManipulation()
     */
    @Override
	public boolean supportsSchemasInDataManipulation() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInIndexDefinitions()
     */
    @Override
	public boolean supportsSchemasInIndexDefinitions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInPrivilegeDefinitions()
     */
    @Override
	public boolean supportsSchemasInPrivilegeDefinitions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInProcedureCalls()
     */
    @Override
	public boolean supportsSchemasInProcedureCalls() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInTableDefinitions()
     */
    @Override
	public boolean supportsSchemasInTableDefinitions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSelectForUpdate()
     */
    @Override
	public boolean supportsSelectForUpdate() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsStoredProcedures()
     */
    @Override
	public boolean supportsStoredProcedures() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSubqueriesInComparisons()
     */
    @Override
	public boolean supportsSubqueriesInComparisons() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSubqueriesInExists()
     */
    @Override
	public boolean supportsSubqueriesInExists() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSubqueriesInIns()
     */
    @Override
	public boolean supportsSubqueriesInIns() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsSubqueriesInQuantifieds()
     */
    @Override
	public boolean supportsSubqueriesInQuantifieds() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsTableCorrelationNames()
     */
    @Override
	public boolean supportsTableCorrelationNames() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsTransactionIsolationLevel(int)
     */
    @Override
	public boolean supportsTransactionIsolationLevel( int level ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsTransactions()
     */
    @Override
	public boolean supportsTransactions() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsUnion()
     */
    @Override
	public boolean supportsUnion() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#supportsUnionAll()
     */
    @Override
	public boolean supportsUnionAll() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#updatesAreDetected(int)
     */
    @Override
	public boolean updatesAreDetected( int type ) {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#usesLocalFilePerTable()
     */
    @Override
	public boolean usesLocalFilePerTable() {
        return false;
    }

    /**
     * @see org.teiid.designer.jdbc.metadata.Capabilities#usesLocalFiles()
     */
    @Override
	public boolean usesLocalFiles() {
        return false;
    }

}
