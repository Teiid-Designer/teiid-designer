/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.modeler.internal.jdbc.relational.util.JdbcRelationalUtil;
import com.metamatrix.modeler.jdbc.JdbcSource;

/** 
 * @since 4.3
 */
public class OracleCostAnalyzerImpl extends DefaultCostAnalyzerImpl {
    private static String ORACLE_8 = "8."; //$NON-NLS-1$ 
    /** 
     * @param jdbcSource
     * @param password
     * @since 4.3
     */    
    public OracleCostAnalyzerImpl(JdbcSource src, String password) {
        super(src, password);
    }

    /**
     * Overridden point for populating table statistics 
     * @see com.metamatrix.modeler.jdbc.relational.impl.DefaultCostAnalyzerImpl#populateTableStatistics(com.metamatrix.modeler.jdbc.relational.impl.TableStatistics)
     * @since 4.3
     */
    @Override
    protected boolean populateTableStatistics(TableStatistics tblStat, IProgressMonitor monitor) throws Exception {
        if (monitor.isCanceled()) {
            return false;
        }
        // try to read from statistics tables
        if (! populateOracleTableStatistics(tblStat)) {
            return super.populateTableStatistics(tblStat, monitor);
        }
        monitor.worked(1);
        return true;
    }
        
    /**
     * Attempt to use Oracle's statistics table if possible; if no statistics are available,
     * default to the original implementation 
     */
    private boolean populateOracleTableStatistics(TableStatistics tblStat) throws Exception {
        long begin = System.currentTimeMillis();
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            String tblName = tblStat.getName();
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery("select num_rows from ALL_TABLES where owner = '" + tblStat.getSchema() + "' AND table_name = '" + tblName + "'");         //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            if(rs.next()) {
                tblStat.setCardinality(rs.getInt(1));
                log("\t" + tblName + ": " + tblStat.getCardinality() + " rows    (collected from Oracle stats in " + (System.currentTimeMillis() - begin) + " ms)");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
                return true;
            }
        } finally { 
            if(rs != null) {
                rs.close();
            }
            if(stmt != null) {
                stmt.close();
            }
        }        
        return false;
    }
    
    /**
     * Overridden point for populating column statistics 
     * @see com.metamatrix.modeler.jdbc.relational.impl.DefaultCostAnalyzerImpl#populateTableStatistics(com.metamatrix.modeler.jdbc.relational.impl.TableStatistics)
     * @since 4.3
     */
    @Override
    protected boolean computeColumnStatistics(TableStatistics tblStat, ColumnStatistics colStat) throws Exception {
        if (! computeOracleColumnStatistics(tblStat, colStat)) {
            return super.computeColumnStatistics(tblStat, colStat);
        }
        return true;
    }

    /**
     * Attempt to use Oracle's statistics table if possible; if no statistics are available,
     * default to the original implementation 
     */    
    private boolean computeOracleColumnStatistics(TableStatistics tblStat, ColumnStatistics colStat) throws Exception {
        long begin = System.currentTimeMillis();
        Statement stmt = null;
        ResultSet rs = null;
        boolean success = false;
        try {
            stmt = this.connection.createStatement(); 
            DatabaseMetaData metadata = this.connection.getMetaData();
            if(metadata.getDatabaseProductVersion().startsWith(ORACLE_8)) {
                rs = stmt.executeQuery("select num_distinct, num_nulls, low_value, high_value from ALL_TAB_COL_STATISTICS where TABLE_NAME = '" + tblStat.getName() + "' and COLUMN_NAME = '" + colStat.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
            }else {
                rs = stmt.executeQuery("select num_distinct, num_nulls, low_value, high_value from ALL_TAB_COL_STATISTICS where owner='" //$NON-NLS-1$
                    + tblStat.getSchema() + "' and TABLE_NAME = '" + tblStat.getName() + "' and COLUMN_NAME = '" + colStat.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
            }
            if(rs.next()) {
                colStat.setNumDistinctValues(rs.getInt(1));
                colStat.setNumNullValues(rs.getInt(2));

                //Can not use these statistics
                //colInfo.getMin() = rs.getString(3);
                //colInfo.getMax() = rs.getString(4);
                
                String tblName = tblStat.getFullyQualifiedEscapedName();
                String colName = JdbcRelationalUtil.escapeDatabaseObjectName(colStat.getName());
                if(colStat.isMinMaxCalculationRequired()) {
                    String minMaxSql = "select min(" + colName + "), max(" + colName + ") from " + tblName; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    
                    rs = stmt.executeQuery(minMaxSql);
                    if (rs.next()) {
                        colStat.setMin(rs.getString(1));
                        colStat.setMax(rs.getString(2));

                        success = true;
                    }                    
                } else {
                    success = true;
                }
                
                log("\t\t" + tblName + "." + colName +  //$NON-NLS-1$ //$NON-NLS-2$
                    ": NDV=" + colStat.getNumDistinctValues() +  //$NON-NLS-1$
                    ": NNV=" + colStat.getNumNullValues() +  //$NON-NLS-1$
                    ", min=" + colStat.getMin() +  //$NON-NLS-1$
                    ", max=" + colStat.getMax() + //$NON-NLS-1$
                    "    (collected from stats in " + (System.currentTimeMillis()-begin) + " ms)"); //$NON-NLS-1$ //$NON-NLS-2$
                return success;
            }
        } finally { 
            if(rs != null) {
                rs.close();
            }
            if(stmt != null) {
                stmt.close();
            }
        }
        return false;
    }
    
    
}
