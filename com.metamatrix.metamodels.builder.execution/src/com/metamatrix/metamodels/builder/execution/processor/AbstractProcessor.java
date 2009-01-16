package com.metamatrix.metamodels.builder.execution.processor;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.metamatrix.metamodels.builder.processor.Processor;

/** 
 * AbstractProcessor that the specific processors extend.
 */
public abstract class AbstractProcessor implements Processor {
	
	protected static final String DELIM = ".";  //$NON-NLS-1$
	private static final String PCT = "%";    //$NON-NLS-1$
	private static final String TABLE_NAME_COL = "TABLE_NAME"; //$NON-NLS-1$

	protected final Connection sqlConnection;
	protected final String modelAndSchemaName;

	// ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

	/** 
	 * Contructor
	 * @param sqlConnection the connection
	 * @param modelAndSchemaName the model and schema name string
	 */
	public AbstractProcessor(Connection sqlConnection, String modelAndSchemaName) {
		this.sqlConnection = sqlConnection;
		this.modelAndSchemaName = modelAndSchemaName;
	}
	
	// ==================================================================================
    //                       M E T H O D S
    // ==================================================================================

	/** 
	 * Get the List of schema table names for the specified schema.
	 * @return the List of table names for the schema
	 */
	protected List getSchemaTables( ) throws SQLException {
		// list for the result table names
		List tableNames = new ArrayList();
		
		// Pattern to limit the returned tables
		String schemaNamePattern = this.modelAndSchemaName+DELIM;
		int patternLength = schemaNamePattern.length();
		
		// Get the tables starting with the pattern from the VDB
		DatabaseMetaData metadata = this.sqlConnection.getMetaData();
		ResultSet tablesRS = metadata.getTables(null,null,schemaNamePattern+PCT,null);
		// Add all of the tables from the resultSet to the result list
		while(tablesRS.next()) {
			String tableName = tablesRS.getString(TABLE_NAME_COL);
			// Keep the table short name
			tableNames.add(tableName.substring(patternLength));
		}
		tablesRS.close();
		return tableNames;
	}
	
	/** 
	 * Execute a "SELECT *" query against the specified table and return
	 * the ResultSet
	 * @param tableName the table on which to execute the query.
	 * @return the query ResultSet
	 */
	protected ResultSet executeTableQuery(String tableName) throws SQLException {
		Statement stmt = this.sqlConnection.createStatement();
		// Construct the query string
		String sql = "SELECT * FROM "+this.modelAndSchemaName+DELIM+tableName;  //$NON-NLS-1$
		// Execute the query
		return stmt.executeQuery(sql);
	}

}
