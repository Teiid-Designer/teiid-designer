/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.teiid.designer.jdbc.JdbcSource;
import org.teiid.designer.jdbc.relational.impl.ColumnStatistics;
import org.teiid.designer.jdbc.relational.impl.DefaultCostAnalyzerImpl;
import org.teiid.designer.jdbc.relational.impl.OracleCostAnalyzerImpl;
import org.teiid.designer.jdbc.relational.impl.TableStatistics;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.Table;


/** 
 * @since 8.0
 */
public class CostAnalyzerFactory {

    private static CostAnalyzerFactory costAnalyzerFactory = null;
    
    private static class DBMS {
        public static final String ORACLE = "oracle"; //$NON-NLS-1$
        public static final String DB2 = "db2"; //$NON-NLS-1$
        public static final String SQL_SERVER = "sqlserver"; //$NON-NLS-1$
    }
    
    
    private CostAnalyzerFactory() {        
    }
    
    /**
     * @return CostAnalyzerFactory
     */
    public static CostAnalyzerFactory getCostAnalyzerFactory() {
        if (CostAnalyzerFactory.costAnalyzerFactory == null) {
            CostAnalyzerFactory.costAnalyzerFactory = new CostAnalyzerFactory();
        }
        return CostAnalyzerFactory.costAnalyzerFactory;
    }
    
    /**
     * @param src the JdbcSource
     * @param password the password
     * @return the CostAnalyzer
     */
    public CostAnalyzer getCostAnalyzer(JdbcSource src, String password) {
        String dbmsType = getDbmsType(src.getDriverName());
        if (DBMS.ORACLE.equals(dbmsType)) {
            return new OracleCostAnalyzerImpl(src, password);
        }
        return new DefaultCostAnalyzerImpl(src, password);
    }
    
    private String getDbmsType(String driverName) {
        driverName = driverName.toLowerCase();
        if (driverName.indexOf(DBMS.ORACLE) > -1) {
            return DBMS.ORACLE;
        }
        if (driverName.indexOf(DBMS.DB2) > -1) {
            return DBMS.DB2;
        }
        if (driverName.indexOf(DBMS.SQL_SERVER) > -1) {
            return DBMS.SQL_SERVER;
        }
        return ""; //$NON-NLS-1$
    }
    
    /**
     * Takes a list of EMF table objects and breaks them down into the CostAnalyzer's TableInfo and ColumnInfo objects. NOTE: The
     * keys used to populate the table and column info maps are the "name in source" field values, not the emf object names of the
     * perspective objects.
     * 
     * @param emfTables the list of EMF Tables
     * @return the TableInfo Map
     * @since 4.3
     */
    public Map createTableInfos( List emfTables ) {
        if (emfTables != null) {
            Map tableInfos = new HashMap();
            for (Iterator tblIt = emfTables.iterator(); tblIt.hasNext();) {
                Table emfTable = (Table)tblIt.next();
                if (emfTable.getNameInSource() != null) {
                    Catalog catalog = emfTable.getCatalog();
                    String catalogName = catalog == null || catalog.getNameInSource() == null ? null : unQualifyName(catalog.getNameInSource());
                    Schema schema = emfTable.getSchema();
                    String schemaName = schema == null || schema.getNameInSource() == null ? null : unQualifyName(schema.getNameInSource());
                    String tblName = emfTable.getNameInSource();
                    TableStatistics tableInfo = new TableStatistics(catalogName, schemaName, tblName);
                    Map columnInfos = tableInfo.getColumnStats();
                    for (Iterator colIt = emfTable.getColumns().iterator(); colIt.hasNext();) {
                        Column emfColumn = (Column)colIt.next();
                        if (emfColumn.getNameInSource() != null) {
                            String colName = unQualifyName(emfColumn.getNameInSource());
                            ColumnStatistics columnInfo = new ColumnStatistics(colName);
                            columnInfos.put(colName, columnInfo);
                        }
                    }
                    tableInfos.put(unQualifyName(tblName), tableInfo);
                }
            }
            return tableInfos;
        }
        return null;
    }

    /**
     * @param emfTables the list of emf tables in this jdbc source physical relational model
     * @param tableInfos the map of value objects containing the newly-computed column statistics
     * @since 4.3
     */
    public void populateEmfColumnStatistics( List emfTables, Map tableInfos ) {
    	for (Iterator itTable = emfTables.iterator(); itTable.hasNext();) {
    		Table emfTable = (Table)itTable.next();
    		if (emfTable.getNameInSource() != null) {
    			TableStatistics tableInfo = (TableStatistics)tableInfos.get(unQualifyName(emfTable.getNameInSource()));
    			if (tableInfo != null) {
    				emfTable.setCardinality(tableInfo.getCardinality());
    				Map columnInfos = tableInfo.getColumnStats();
    				for (Iterator itColumn = emfTable.getColumns().iterator(); itColumn.hasNext();) {
    					Column emfColumn = (Column)itColumn.next();
    					if (emfColumn.getNameInSource() != null) {
    						ColumnStatistics columnInfo = (ColumnStatistics)columnInfos.get(unQualifyName(emfColumn.getNameInSource()));
    						if (columnInfo != null) {
    							emfColumn.setMinimumValue(columnInfo.getMin());
    							emfColumn.setMaximumValue(columnInfo.getMax());
    							emfColumn.setNullValueCount(columnInfo.getNumNullValues());
    							emfColumn.setDistinctValueCount(columnInfo.getNumDistinctValues());
    						}
    					}
    				}
    			}
    		}
    	}
    }
    
    private String unQualifyName( String qualifiedName ) {
    	return qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1, qualifiedName.length());
    }
    
}
