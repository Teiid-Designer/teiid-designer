/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.jdbc.JdbcPlugin;
import org.teiid.designer.jdbc.metadata.Capabilities;


/**
 * CapabilitiesImpl
 *
 * @since 8.0
 */
public class CapabilitiesImpl implements Capabilities {

    private final DatabaseMetaData metadata;
    private String[] tableTypes;

    /**
     * Construct an instance of CapabilitiesImpl.
     */
    public CapabilitiesImpl( final DatabaseMetaData metadata ) {
        super();
        CoreArgCheck.isNotNull(metadata);
        this.metadata = metadata;
    }

    protected DatabaseMetaData getMetadata() {
        return this.metadata;
    }

    // =========================================================================
    // Specialized (non-JDBC) methods
    // =========================================================================
/**
     * Retrieves whether this database supports catalogs.  This method returns
     * true if any of the following methods returns true:
     * <ol>
     *   <li>{@link #supportsCatalogsInTableDefinitions()</li>
     *   <li>{@link #supportsCatalogsInDataManipulation()</li>
     *   <li>{@link #supportsCatalogsInProcedureCalls()</li>
     * </ol>
     * @return true if the database supports catalogs.
     * @throws SQLException if there is an error obtaining the information
     */
    @Override
	public boolean supportsCatalogs() throws SQLException {
        return supportsCatalogsInTableDefinitions() || supportsCatalogsInDataManipulation() || supportsCatalogsInProcedureCalls();
    }

/**
     * Retrieves whether this database supports schemas.  This method returns
     * true if any of the following methods returns true:
     * <ol>
     *   <li>{@link #supportsSchemasInDataManipulation()</li>
     *   <li>{@link #supportsSchemasInTableDefinitions()</li>
     * </ol>
     * @return true if the database supports schemas.
     * @throws SQLException if there is an error obtaining the information
     */
    @Override
	public boolean supportsSchemas() throws SQLException {
        return supportsSchemasInDataManipulation() || supportsSchemasInTableDefinitions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getTableTypes()
     */
    @Override
	public String[] getTableTypes() throws SQLException {
        if (tableTypes == null) {
            final List results = new ArrayList();
            final ResultSet resultSet = getMetadata().getTableTypes();
            try {
                while (resultSet.next()) {
                    final String tableType = resultSet.getString(1);
                    results.add(tableType);
                }
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        JdbcPlugin.Util.log(e);
                    }
                }
            }
            // Convert the list to an array and return ...
            tableTypes = (String[])results.toArray(new String[results.size()]);
        }
        return tableTypes;
    }

    // =========================================================================
    // Standard JDBC DatabaseMetaData methods
    // =========================================================================

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#allProceduresAreCallable()
     */
    @Override
	public boolean allProceduresAreCallable() throws SQLException {
        return getMetadata().allProceduresAreCallable();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#allTablesAreSelectable()
     */
    @Override
	public boolean allTablesAreSelectable() throws SQLException {
        return getMetadata().allTablesAreSelectable();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#dataDefinitionCausesTransactionCommit()
     */
    @Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        return getMetadata().dataDefinitionCausesTransactionCommit();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#dataDefinitionIgnoredInTransactions()
     */
    @Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        return getMetadata().dataDefinitionIgnoredInTransactions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#deletesAreDetected(int)
     */
    @Override
	public boolean deletesAreDetected( int type ) throws SQLException {
        return getMetadata().deletesAreDetected(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#doesMaxRowSizeIncludeBlobs()
     */
    @Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        return getMetadata().doesMaxRowSizeIncludeBlobs();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getCatalogSeparator()
     */
    @Override
	public String getCatalogSeparator() throws SQLException {
        return getMetadata().getCatalogSeparator();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getCatalogTerm()
     */
    @Override
	public String getCatalogTerm() throws SQLException {
        return getMetadata().getCatalogTerm();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getExtraNameCharacters()
     */
    @Override
	public String getExtraNameCharacters() throws SQLException {
        return getMetadata().getExtraNameCharacters();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getIdentifierQuoteString()
     */
    @Override
	public String getIdentifierQuoteString() throws SQLException {
        return getMetadata().getIdentifierQuoteString();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxBinaryLiteralLength()
     */
    @Override
	public int getMaxBinaryLiteralLength() throws SQLException {
        return getMetadata().getMaxBinaryLiteralLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxCatalogNameLength()
     */
    @Override
	public int getMaxCatalogNameLength() throws SQLException {
        return getMetadata().getMaxCatalogNameLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxCharLiteralLength()
     */
    @Override
	public int getMaxCharLiteralLength() throws SQLException {
        return getMetadata().getMaxCharLiteralLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnNameLength()
     */
    @Override
	public int getMaxColumnNameLength() throws SQLException {
        return getMetadata().getMaxColumnNameLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInGroupBy()
     */
    @Override
	public int getMaxColumnsInGroupBy() throws SQLException {
        return getMetadata().getMaxColumnsInGroupBy();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInIndex()
     */
    @Override
	public int getMaxColumnsInIndex() throws SQLException {
        return getMetadata().getMaxColumnsInIndex();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInOrderBy()
     */
    @Override
	public int getMaxColumnsInOrderBy() throws SQLException {
        return getMetadata().getMaxColumnsInOrderBy();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInSelect()
     */
    @Override
	public int getMaxColumnsInSelect() throws SQLException {
        return getMetadata().getMaxColumnsInSelect();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxColumnsInTable()
     */
    @Override
	public int getMaxColumnsInTable() throws SQLException {
        return getMetadata().getMaxColumnsInTable();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxConnections()
     */
    @Override
	public int getMaxConnections() throws SQLException {
        return getMetadata().getMaxConnections();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxCursorNameLength()
     */
    @Override
	public int getMaxCursorNameLength() throws SQLException {
        return getMetadata().getMaxCursorNameLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxIndexLength()
     */
    @Override
	public int getMaxIndexLength() throws SQLException {
        return getMetadata().getMaxIndexLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxProcedureNameLength()
     */
    @Override
	public int getMaxProcedureNameLength() throws SQLException {
        return getMetadata().getMaxProcedureNameLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxRowSize()
     */
    @Override
	public int getMaxRowSize() throws SQLException {
        return getMetadata().getMaxRowSize();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxSchemaNameLength()
     */
    @Override
	public int getMaxSchemaNameLength() throws SQLException {
        return getMetadata().getMaxSchemaNameLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxStatementLength()
     */
    @Override
	public int getMaxStatementLength() throws SQLException {
        return getMetadata().getMaxStatementLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxStatements()
     */
    @Override
	public int getMaxStatements() throws SQLException {
        return getMetadata().getMaxStatements();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxTableNameLength()
     */
    @Override
	public int getMaxTableNameLength() throws SQLException {
        return getMetadata().getMaxTableNameLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxTablesInSelect()
     */
    @Override
	public int getMaxTablesInSelect() throws SQLException {
        return getMetadata().getMaxTablesInSelect();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getMaxUserNameLength()
     */
    @Override
	public int getMaxUserNameLength() throws SQLException {
        return getMetadata().getMaxUserNameLength();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getNumericFunctions()
     */
    @Override
	public String getNumericFunctions() throws SQLException {
        return getMetadata().getNumericFunctions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getProcedureTerm()
     */
    @Override
	public String getProcedureTerm() throws SQLException {
        return getMetadata().getProcedureTerm();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getSchemaTerm()
     */
    @Override
	public String getSchemaTerm() throws SQLException {
        return getMetadata().getSchemaTerm();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getSearchStringEscape()
     */
    @Override
	public String getSearchStringEscape() throws SQLException {
        return getMetadata().getSearchStringEscape();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getSQLKeywords()
     */
    @Override
	public String getSQLKeywords() throws SQLException {
        return getMetadata().getSQLKeywords();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getStringFunctions()
     */
    @Override
	public String getStringFunctions() throws SQLException {
        return getMetadata().getStringFunctions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getSystemFunctions()
     */
    @Override
	public String getSystemFunctions() throws SQLException {
        return getMetadata().getSystemFunctions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getTimeDateFunctions()
     */
    @Override
	public String getTimeDateFunctions() throws SQLException {
        return getMetadata().getTimeDateFunctions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#getTypeInfo()
     */
    @Override
	public ResultSet getTypeInfo() throws SQLException {
        return getMetadata().getTypeInfo();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#insertsAreDetected(int)
     */
    @Override
	public boolean insertsAreDetected( int type ) throws SQLException {
        return getMetadata().insertsAreDetected(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#isCatalogAtStart()
     */
    @Override
	public boolean isCatalogAtStart() throws SQLException {
        return getMetadata().isCatalogAtStart();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#isReadOnly()
     */
    @Override
	public boolean isReadOnly() throws SQLException {
        return getMetadata().isReadOnly();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#nullPlusNonNullIsNull()
     */
    @Override
	public boolean nullPlusNonNullIsNull() throws SQLException {
        return getMetadata().nullPlusNonNullIsNull();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#nullsAreSortedAtEnd()
     */
    @Override
	public boolean nullsAreSortedAtEnd() throws SQLException {
        return getMetadata().nullsAreSortedAtEnd();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#nullsAreSortedAtStart()
     */
    @Override
	public boolean nullsAreSortedAtStart() throws SQLException {
        return getMetadata().nullsAreSortedAtStart();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#nullsAreSortedHigh()
     */
    @Override
	public boolean nullsAreSortedHigh() throws SQLException {
        return getMetadata().nullsAreSortedHigh();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#nullsAreSortedLow()
     */
    @Override
	public boolean nullsAreSortedLow() throws SQLException {
        return getMetadata().nullsAreSortedLow();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#othersDeletesAreVisible(int)
     */
    @Override
	public boolean othersDeletesAreVisible( int type ) throws SQLException {
        return getMetadata().othersDeletesAreVisible(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#othersInsertsAreVisible(int)
     */
    @Override
	public boolean othersInsertsAreVisible( int type ) throws SQLException {
        return getMetadata().othersInsertsAreVisible(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#othersUpdatesAreVisible(int)
     */
    @Override
	public boolean othersUpdatesAreVisible( int type ) throws SQLException {
        return getMetadata().othersUpdatesAreVisible(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#ownDeletesAreVisible(int)
     */
    @Override
	public boolean ownDeletesAreVisible( int type ) throws SQLException {
        return getMetadata().ownDeletesAreVisible(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#ownInsertsAreVisible(int)
     */
    @Override
	public boolean ownInsertsAreVisible( int type ) throws SQLException {
        return getMetadata().ownInsertsAreVisible(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#ownUpdatesAreVisible(int)
     */
    @Override
	public boolean ownUpdatesAreVisible( int type ) throws SQLException {
        return getMetadata().ownUpdatesAreVisible(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#storesLowerCaseIdentifiers()
     */
    @Override
	public boolean storesLowerCaseIdentifiers() throws SQLException {
        return getMetadata().storesLowerCaseIdentifiers();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#storesLowerCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        return getMetadata().storesLowerCaseQuotedIdentifiers();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#storesMixedCaseIdentifiers()
     */
    @Override
	public boolean storesMixedCaseIdentifiers() throws SQLException {
        return getMetadata().storesMixedCaseIdentifiers();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#storesMixedCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        return getMetadata().storesMixedCaseQuotedIdentifiers();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#storesUpperCaseIdentifiers()
     */
    @Override
	public boolean storesUpperCaseIdentifiers() throws SQLException {
        return getMetadata().storesUpperCaseIdentifiers();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#storesUpperCaseQuotedIdentifiers()
     */
    @Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        return getMetadata().storesUpperCaseQuotedIdentifiers();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsAlterTableWithAddColumn()
     */
    @Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
        return getMetadata().supportsAlterTableWithAddColumn();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsAlterTableWithDropColumn()
     */
    @Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
        return getMetadata().supportsAlterTableWithDropColumn();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsANSI92EntryLevelSQL()
     */
    @Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        return getMetadata().supportsANSI92EntryLevelSQL();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsANSI92FullSQL()
     */
    @Override
	public boolean supportsANSI92FullSQL() throws SQLException {
        return getMetadata().supportsANSI92FullSQL();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsANSI92IntermediateSQL()
     */
    @Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
        return getMetadata().supportsANSI92IntermediateSQL();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsBatchUpdates()
     */
    @Override
	public boolean supportsBatchUpdates() throws SQLException {
        return getMetadata().supportsBatchUpdates();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInDataManipulation()
     */
    @Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
        return getMetadata().supportsCatalogsInDataManipulation();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInIndexDefinitions()
     */
    @Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        return getMetadata().supportsCatalogsInIndexDefinitions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInPrivilegeDefinitions()
     */
    @Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        return getMetadata().supportsCatalogsInPrivilegeDefinitions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInProcedureCalls()
     */
    @Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        return getMetadata().supportsCatalogsInProcedureCalls();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsCatalogsInTableDefinitions()
     */
    @Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        return getMetadata().supportsCatalogsInTableDefinitions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsColumnAliasing()
     */
    @Override
	public boolean supportsColumnAliasing() throws SQLException {
        return getMetadata().supportsColumnAliasing();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsConvert()
     */
    @Override
	public boolean supportsConvert() throws SQLException {
        return getMetadata().supportsConvert();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsConvert(int, int)
     */
    @Override
	public boolean supportsConvert( int fromType,
                                    int toType ) throws SQLException {
        return getMetadata().supportsConvert(fromType, toType);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsCoreSQLGrammar()
     */
    @Override
	public boolean supportsCoreSQLGrammar() throws SQLException {
        return getMetadata().supportsCoreSQLGrammar();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsCorrelatedSubqueries()
     */
    @Override
	public boolean supportsCorrelatedSubqueries() throws SQLException {
        return getMetadata().supportsCorrelatedSubqueries();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsDataDefinitionAndDataManipulationTransactions()
     */
    @Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        return getMetadata().supportsDataDefinitionAndDataManipulationTransactions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsDataManipulationTransactionsOnly()
     */
    @Override
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        return getMetadata().supportsDataManipulationTransactionsOnly();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsDifferentTableCorrelationNames()
     */
    @Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        return getMetadata().supportsDifferentTableCorrelationNames();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsExpressionsInOrderBy()
     */
    @Override
	public boolean supportsExpressionsInOrderBy() throws SQLException {
        return getMetadata().supportsExpressionsInOrderBy();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsExtendedSQLGrammar()
     */
    @Override
	public boolean supportsExtendedSQLGrammar() throws SQLException {
        return getMetadata().supportsExtendedSQLGrammar();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsFullOuterJoins()
     */
    @Override
	public boolean supportsFullOuterJoins() throws SQLException {
        return getMetadata().supportsFullOuterJoins();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsGroupBy()
     */
    @Override
	public boolean supportsGroupBy() throws SQLException {
        return getMetadata().supportsGroupBy();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsGroupByBeyondSelect()
     */
    @Override
	public boolean supportsGroupByBeyondSelect() throws SQLException {
        return getMetadata().supportsGroupByBeyondSelect();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsGroupByUnrelated()
     */
    @Override
	public boolean supportsGroupByUnrelated() throws SQLException {
        return getMetadata().supportsGroupByUnrelated();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsIntegrityEnhancementFacility()
     */
    @Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        return getMetadata().supportsIntegrityEnhancementFacility();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsLikeEscapeClause()
     */
    @Override
	public boolean supportsLikeEscapeClause() throws SQLException {
        return getMetadata().supportsLikeEscapeClause();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsLimitedOuterJoins()
     */
    @Override
	public boolean supportsLimitedOuterJoins() throws SQLException {
        return getMetadata().supportsLimitedOuterJoins();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsMinimumSQLGrammar()
     */
    @Override
	public boolean supportsMinimumSQLGrammar() throws SQLException {
        return getMetadata().supportsMinimumSQLGrammar();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsMixedCaseIdentifiers()
     */
    @Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
        return getMetadata().supportsMixedCaseIdentifiers();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsMixedCaseQuotedIdentifiers()
     */
    @Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        return getMetadata().supportsMixedCaseQuotedIdentifiers();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsMultipleResultSets()
     */
    @Override
	public boolean supportsMultipleResultSets() throws SQLException {
        return getMetadata().supportsMultipleResultSets();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsMultipleTransactions()
     */
    @Override
	public boolean supportsMultipleTransactions() throws SQLException {
        return getMetadata().supportsMultipleTransactions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsNonNullableColumns()
     */
    @Override
	public boolean supportsNonNullableColumns() throws SQLException {
        return getMetadata().supportsNonNullableColumns();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsOpenCursorsAcrossCommit()
     */
    @Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        return getMetadata().supportsOpenCursorsAcrossCommit();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsOpenCursorsAcrossRollback()
     */
    @Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        return getMetadata().supportsOpenCursorsAcrossRollback();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsOpenStatementsAcrossCommit()
     */
    @Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        return getMetadata().supportsOpenStatementsAcrossCommit();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsOpenStatementsAcrossRollback()
     */
    @Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        return getMetadata().supportsOpenStatementsAcrossRollback();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsOrderByUnrelated()
     */
    @Override
	public boolean supportsOrderByUnrelated() throws SQLException {
        return getMetadata().supportsOrderByUnrelated();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsOuterJoins()
     */
    @Override
	public boolean supportsOuterJoins() throws SQLException {
        return getMetadata().supportsOuterJoins();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsPositionedDelete()
     */
    @Override
	public boolean supportsPositionedDelete() throws SQLException {
        return getMetadata().supportsPositionedDelete();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsPositionedUpdate()
     */
    @Override
	public boolean supportsPositionedUpdate() throws SQLException {
        return getMetadata().supportsPositionedUpdate();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsResultSetConcurrency(int, int)
     */
    @Override
	public boolean supportsResultSetConcurrency( int type,
                                                 int concurrency ) throws SQLException {
        return getMetadata().supportsResultSetConcurrency(type, concurrency);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsResultSetType(int)
     */
    @Override
	public boolean supportsResultSetType( int type ) throws SQLException {
        return getMetadata().supportsResultSetType(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInDataManipulation()
     */
    @Override
	public boolean supportsSchemasInDataManipulation() throws SQLException {
        return getMetadata().supportsSchemasInDataManipulation();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInIndexDefinitions()
     */
    @Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        return getMetadata().supportsSchemasInIndexDefinitions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInPrivilegeDefinitions()
     */
    @Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        return getMetadata().supportsSchemasInPrivilegeDefinitions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInProcedureCalls()
     */
    @Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
        return getMetadata().supportsSchemasInProcedureCalls();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSchemasInTableDefinitions()
     */
    @Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
        return getMetadata().supportsSchemasInTableDefinitions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSelectForUpdate()
     */
    @Override
	public boolean supportsSelectForUpdate() throws SQLException {
        return getMetadata().supportsSelectForUpdate();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsStoredProcedures()
     */
    @Override
	public boolean supportsStoredProcedures() throws SQLException {
        return getMetadata().supportsStoredProcedures();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSubqueriesInComparisons()
     */
    @Override
	public boolean supportsSubqueriesInComparisons() throws SQLException {
        return getMetadata().supportsSubqueriesInComparisons();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSubqueriesInExists()
     */
    @Override
	public boolean supportsSubqueriesInExists() throws SQLException {
        return getMetadata().supportsSubqueriesInExists();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSubqueriesInIns()
     */
    @Override
	public boolean supportsSubqueriesInIns() throws SQLException {
        return getMetadata().supportsSubqueriesInIns();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsSubqueriesInQuantifieds()
     */
    @Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        return getMetadata().supportsSubqueriesInQuantifieds();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsTableCorrelationNames()
     */
    @Override
	public boolean supportsTableCorrelationNames() throws SQLException {
        return getMetadata().supportsTableCorrelationNames();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsTransactionIsolationLevel(int)
     */
    @Override
	public boolean supportsTransactionIsolationLevel( int level ) throws SQLException {
        return getMetadata().supportsTransactionIsolationLevel(level);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsTransactions()
     */
    @Override
	public boolean supportsTransactions() throws SQLException {
        return getMetadata().supportsTransactions();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsUnion()
     */
    @Override
	public boolean supportsUnion() throws SQLException {
        return getMetadata().supportsUnion();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#supportsUnionAll()
     */
    @Override
	public boolean supportsUnionAll() throws SQLException {
        return getMetadata().supportsUnionAll();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#updatesAreDetected(int)
     */
    @Override
	public boolean updatesAreDetected( int type ) throws SQLException {
        return getMetadata().updatesAreDetected(type);
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#usesLocalFilePerTable()
     */
    @Override
	public boolean usesLocalFilePerTable() throws SQLException {
        return getMetadata().usesLocalFilePerTable();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.metadata.Capabilities#usesLocalFiles()
     */
    @Override
	public boolean usesLocalFiles() throws SQLException {
        return getMetadata().usesLocalFiles();
    }

}
