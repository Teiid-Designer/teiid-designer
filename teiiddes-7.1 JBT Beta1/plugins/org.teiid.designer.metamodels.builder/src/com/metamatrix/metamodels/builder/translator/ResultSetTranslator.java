/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.builder.translator;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class simply takes a ResultSet and turns it into a List of HashMaps for passing to the 
 * builder framework
 */
public class ResultSetTranslator {

	// ==================================================================================
    //                        S T A T I C  M E T H O D S
    // ==================================================================================

	/**
	 * Translate the resultSet into a List of HashMaps - one map for each row of data
	 * @param resultSet the query ResultSet
	 * @return the list of maps 
	 */
	public static List translate(ResultSet resultSet) throws SQLException {
		List listOfMaps = new ArrayList();
		
		// Get the ResultSet column names from ResultSet MetaData
		ResultSetMetaData metadata = resultSet.getMetaData();
		int nCols = metadata.getColumnCount();
		String[] colNames = new String[nCols];
		for(int i=0; i<nCols; i++) {
			colNames[i] = metadata.getColumnName(i+1);
		}
		// Iterate the resultSet, creating hashmap for each row
		while(resultSet.next()) {
			HashMap map = new HashMap(nCols);
			for(int i=0; i<nCols; i++) {
				String colName = colNames[i];
				Object value = resultSet.getObject(i+1);
				map.put(colName,value);
			}
			listOfMaps.add(map);
		}
		return listOfMaps;
	}
	
	/**
	 * Count the number of rows in the ResultSet
	 * @param resultSet the query ResultSet
	 * @return the number of rows
	 */
	public static int getRowCount(ResultSet resultSet) throws SQLException {
		int rowCount = 0;
		
		// Iterate the resultSet, creating hashmap for each row
		while(resultSet.next()) {
			rowCount++;
		}
		return rowCount;
	}
	
}
