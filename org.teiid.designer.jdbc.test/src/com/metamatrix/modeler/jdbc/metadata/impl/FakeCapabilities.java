/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import java.sql.ResultSet;
import com.metamatrix.modeler.jdbc.metadata.Capabilities;

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
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsCatalogs()
     */
    public boolean supportsCatalogs() {
        return this.supportsCatalogs;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSchemas()
     */
    public boolean supportsSchemas() {
        return this.supportsSchemas;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getTableTypes()
     */
    public String[] getTableTypes() {
        return new String[] {"Table", "View"}; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#allProceduresAreCallable()
     */
    public boolean allProceduresAreCallable() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#allTablesAreSelectable()
     */
    public boolean allTablesAreSelectable() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#dataDefinitionCausesTransactionCommit()
     */
    public boolean dataDefinitionCausesTransactionCommit() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#dataDefinitionIgnoredInTransactions()
     */
    public boolean dataDefinitionIgnoredInTransactions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#deletesAreDetected(int)
     */
    public boolean deletesAreDetected( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#doesMaxRowSizeIncludeBlobs()
     */
    public boolean doesMaxRowSizeIncludeBlobs() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getCatalogSeparator()
     */
    public String getCatalogSeparator() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getCatalogTerm()
     */
    public String getCatalogTerm() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getExtraNameCharacters()
     */
    public String getExtraNameCharacters() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getIdentifierQuoteString()
     */
    public String getIdentifierQuoteString() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxBinaryLiteralLength()
     */
    public int getMaxBinaryLiteralLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxCatalogNameLength()
     */
    public int getMaxCatalogNameLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxCharLiteralLength()
     */
    public int getMaxCharLiteralLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxColumnNameLength()
     */
    public int getMaxColumnNameLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxColumnsInGroupBy()
     */
    public int getMaxColumnsInGroupBy() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxColumnsInIndex()
     */
    public int getMaxColumnsInIndex() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxColumnsInOrderBy()
     */
    public int getMaxColumnsInOrderBy() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxColumnsInSelect()
     */
    public int getMaxColumnsInSelect() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxColumnsInTable()
     */
    public int getMaxColumnsInTable() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxConnections()
     */
    public int getMaxConnections() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxCursorNameLength()
     */
    public int getMaxCursorNameLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxIndexLength()
     */
    public int getMaxIndexLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxProcedureNameLength()
     */
    public int getMaxProcedureNameLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxRowSize()
     */
    public int getMaxRowSize() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxSchemaNameLength()
     */
    public int getMaxSchemaNameLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxStatementLength()
     */
    public int getMaxStatementLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxStatements()
     */
    public int getMaxStatements() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxTableNameLength()
     */
    public int getMaxTableNameLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxTablesInSelect()
     */
    public int getMaxTablesInSelect() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getMaxUserNameLength()
     */
    public int getMaxUserNameLength() {
        return 0;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getNumericFunctions()
     */
    public String getNumericFunctions() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getProcedureTerm()
     */
    public String getProcedureTerm() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getSchemaTerm()
     */
    public String getSchemaTerm() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getSearchStringEscape()
     */
    public String getSearchStringEscape() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getSQLKeywords()
     */
    public String getSQLKeywords() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getStringFunctions()
     */
    public String getStringFunctions() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getSystemFunctions()
     */
    public String getSystemFunctions() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getTimeDateFunctions()
     */
    public String getTimeDateFunctions() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#getTypeInfo()
     */
    public ResultSet getTypeInfo() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#insertsAreDetected(int)
     */
    public boolean insertsAreDetected( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#isCatalogAtStart()
     */
    public boolean isCatalogAtStart() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#isReadOnly()
     */
    public boolean isReadOnly() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#nullPlusNonNullIsNull()
     */
    public boolean nullPlusNonNullIsNull() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#nullsAreSortedAtEnd()
     */
    public boolean nullsAreSortedAtEnd() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#nullsAreSortedAtStart()
     */
    public boolean nullsAreSortedAtStart() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#nullsAreSortedHigh()
     */
    public boolean nullsAreSortedHigh() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#nullsAreSortedLow()
     */
    public boolean nullsAreSortedLow() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#othersDeletesAreVisible(int)
     */
    public boolean othersDeletesAreVisible( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#othersInsertsAreVisible(int)
     */
    public boolean othersInsertsAreVisible( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#othersUpdatesAreVisible(int)
     */
    public boolean othersUpdatesAreVisible( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#ownDeletesAreVisible(int)
     */
    public boolean ownDeletesAreVisible( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#ownInsertsAreVisible(int)
     */
    public boolean ownInsertsAreVisible( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#ownUpdatesAreVisible(int)
     */
    public boolean ownUpdatesAreVisible( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#storesLowerCaseIdentifiers()
     */
    public boolean storesLowerCaseIdentifiers() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#storesLowerCaseQuotedIdentifiers()
     */
    public boolean storesLowerCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#storesMixedCaseIdentifiers()
     */
    public boolean storesMixedCaseIdentifiers() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#storesMixedCaseQuotedIdentifiers()
     */
    public boolean storesMixedCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#storesUpperCaseIdentifiers()
     */
    public boolean storesUpperCaseIdentifiers() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#storesUpperCaseQuotedIdentifiers()
     */
    public boolean storesUpperCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsAlterTableWithAddColumn()
     */
    public boolean supportsAlterTableWithAddColumn() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsAlterTableWithDropColumn()
     */
    public boolean supportsAlterTableWithDropColumn() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsANSI92EntryLevelSQL()
     */
    public boolean supportsANSI92EntryLevelSQL() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsANSI92FullSQL()
     */
    public boolean supportsANSI92FullSQL() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsANSI92IntermediateSQL()
     */
    public boolean supportsANSI92IntermediateSQL() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsBatchUpdates()
     */
    public boolean supportsBatchUpdates() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsCatalogsInDataManipulation()
     */
    public boolean supportsCatalogsInDataManipulation() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsCatalogsInIndexDefinitions()
     */
    public boolean supportsCatalogsInIndexDefinitions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsCatalogsInPrivilegeDefinitions()
     */
    public boolean supportsCatalogsInPrivilegeDefinitions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsCatalogsInProcedureCalls()
     */
    public boolean supportsCatalogsInProcedureCalls() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsCatalogsInTableDefinitions()
     */
    public boolean supportsCatalogsInTableDefinitions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsColumnAliasing()
     */
    public boolean supportsColumnAliasing() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsConvert()
     */
    public boolean supportsConvert() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsConvert(int, int)
     */
    public boolean supportsConvert( int fromType,
                                    int toType ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsCoreSQLGrammar()
     */
    public boolean supportsCoreSQLGrammar() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsCorrelatedSubqueries()
     */
    public boolean supportsCorrelatedSubqueries() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsDataDefinitionAndDataManipulationTransactions()
     */
    public boolean supportsDataDefinitionAndDataManipulationTransactions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsDataManipulationTransactionsOnly()
     */
    public boolean supportsDataManipulationTransactionsOnly() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsDifferentTableCorrelationNames()
     */
    public boolean supportsDifferentTableCorrelationNames() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsExpressionsInOrderBy()
     */
    public boolean supportsExpressionsInOrderBy() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsExtendedSQLGrammar()
     */
    public boolean supportsExtendedSQLGrammar() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsFullOuterJoins()
     */
    public boolean supportsFullOuterJoins() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsGroupBy()
     */
    public boolean supportsGroupBy() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsGroupByBeyondSelect()
     */
    public boolean supportsGroupByBeyondSelect() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsGroupByUnrelated()
     */
    public boolean supportsGroupByUnrelated() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsIntegrityEnhancementFacility()
     */
    public boolean supportsIntegrityEnhancementFacility() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsLikeEscapeClause()
     */
    public boolean supportsLikeEscapeClause() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsLimitedOuterJoins()
     */
    public boolean supportsLimitedOuterJoins() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsMinimumSQLGrammar()
     */
    public boolean supportsMinimumSQLGrammar() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsMixedCaseIdentifiers()
     */
    public boolean supportsMixedCaseIdentifiers() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsMixedCaseQuotedIdentifiers()
     */
    public boolean supportsMixedCaseQuotedIdentifiers() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsMultipleResultSets()
     */
    public boolean supportsMultipleResultSets() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsMultipleTransactions()
     */
    public boolean supportsMultipleTransactions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsNonNullableColumns()
     */
    public boolean supportsNonNullableColumns() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsOpenCursorsAcrossCommit()
     */
    public boolean supportsOpenCursorsAcrossCommit() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsOpenCursorsAcrossRollback()
     */
    public boolean supportsOpenCursorsAcrossRollback() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsOpenStatementsAcrossCommit()
     */
    public boolean supportsOpenStatementsAcrossCommit() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsOpenStatementsAcrossRollback()
     */
    public boolean supportsOpenStatementsAcrossRollback() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsOrderByUnrelated()
     */
    public boolean supportsOrderByUnrelated() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsOuterJoins()
     */
    public boolean supportsOuterJoins() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsPositionedDelete()
     */
    public boolean supportsPositionedDelete() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsPositionedUpdate()
     */
    public boolean supportsPositionedUpdate() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsResultSetConcurrency(int, int)
     */
    public boolean supportsResultSetConcurrency( int type,
                                                 int concurrency ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsResultSetType(int)
     */
    public boolean supportsResultSetType( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSchemasInDataManipulation()
     */
    public boolean supportsSchemasInDataManipulation() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSchemasInIndexDefinitions()
     */
    public boolean supportsSchemasInIndexDefinitions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSchemasInPrivilegeDefinitions()
     */
    public boolean supportsSchemasInPrivilegeDefinitions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSchemasInProcedureCalls()
     */
    public boolean supportsSchemasInProcedureCalls() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSchemasInTableDefinitions()
     */
    public boolean supportsSchemasInTableDefinitions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSelectForUpdate()
     */
    public boolean supportsSelectForUpdate() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsStoredProcedures()
     */
    public boolean supportsStoredProcedures() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSubqueriesInComparisons()
     */
    public boolean supportsSubqueriesInComparisons() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSubqueriesInExists()
     */
    public boolean supportsSubqueriesInExists() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSubqueriesInIns()
     */
    public boolean supportsSubqueriesInIns() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsSubqueriesInQuantifieds()
     */
    public boolean supportsSubqueriesInQuantifieds() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsTableCorrelationNames()
     */
    public boolean supportsTableCorrelationNames() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsTransactionIsolationLevel(int)
     */
    public boolean supportsTransactionIsolationLevel( int level ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsTransactions()
     */
    public boolean supportsTransactions() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsUnion()
     */
    public boolean supportsUnion() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#supportsUnionAll()
     */
    public boolean supportsUnionAll() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#updatesAreDetected(int)
     */
    public boolean updatesAreDetected( int type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#usesLocalFilePerTable()
     */
    public boolean usesLocalFilePerTable() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.metadata.Capabilities#usesLocalFiles()
     */
    public boolean usesLocalFiles() {
        return false;
    }

}
