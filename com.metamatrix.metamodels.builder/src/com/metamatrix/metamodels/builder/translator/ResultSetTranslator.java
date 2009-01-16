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
