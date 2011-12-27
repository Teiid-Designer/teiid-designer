/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.eclipse.core.runtime.IProgressMonitor;
import com.metamatrix.modeler.internal.jdbc.relational.util.JdbcRelationalUtil;
import com.metamatrix.modeler.jdbc.JdbcSource;

/** 
 * @since 4.3
 */
public class OracleCostAnalyzerImpl extends DefaultCostAnalyzerImpl {
    /**
     */
    private static final String NUMBER = "number"; //$NON-NLS-1$
    private static String ORACLE_8 = "8."; //$NON-NLS-1$ 
    
    private final static Map<Integer, String> type_to_raw_mapping = new HashMap<Integer, String>();
    private final static Map<String, String> native_to_raw_mapping = new TreeMap<String, String>();
    
    static {
        type_to_raw_mapping.put(Types.BIGINT, NUMBER);
        type_to_raw_mapping.put(Types.INTEGER, NUMBER);
        type_to_raw_mapping.put(Types.SMALLINT, NUMBER);
        type_to_raw_mapping.put(Types.BOOLEAN, NUMBER);
        type_to_raw_mapping.put(Types.NUMERIC, NUMBER);
        type_to_raw_mapping.put(Types.DECIMAL, NUMBER);
        type_to_raw_mapping.put(Types.REAL, NUMBER);
        type_to_raw_mapping.put(Types.DOUBLE, NUMBER);
        type_to_raw_mapping.put(Types.FLOAT, NUMBER);
        type_to_raw_mapping.put(Types.INTEGER, NUMBER);
        
        type_to_raw_mapping.put(Types.VARCHAR, "varchar2"); //$NON-NLS-1$
        type_to_raw_mapping.put(Types.NVARCHAR, "nvarchar2"); //$NON-NLS-1$
        
        native_to_raw_mapping.put("binary_float", "binary_float"); //$NON-NLS-1$ //$NON-NLS-2$
        native_to_raw_mapping.put("binary_integer", "binary_integer"); //$NON-NLS-1$ //$NON-NLS-2$
    }
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
            DatabaseMetaData metadata = this.connection.getMetaData();
            String sql = "select num_distinct, num_nulls"; //$NON-NLS-1$
            boolean unknownType = false;
            if (colStat.isMinMaxCalculationRequired()) {
                int jdbcType = colStat.jdbcType;
                String type = native_to_raw_mapping.get(colStat.nativeType);
                if (type == null) {
                    type = type_to_raw_mapping.get(jdbcType);
                }
                if (type != null) {
                    sql += ", utl_raw.cast_to_"+type+"(low_value), utl_raw.cast_to_"+type+"(high_value)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                } else {
                    sql += ", low_value, high_value"; //$NON-NLS-1$
                    unknownType = true;
                }
            }
            sql += " from ALL_TAB_COL_STATISTICS"; //$NON-NLS-1$
            if(metadata.getDatabaseProductVersion().startsWith(ORACLE_8)) {
                sql += " where TABLE_NAME = '" + tblStat.getName() + "' and COLUMN_NAME = '" + colStat.getName() + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
            }else {
                sql += " where owner='" //$NON-NLS-1$
                    + tblStat.getSchema() + "' and TABLE_NAME = '" + tblStat.getName() + "' and COLUMN_NAME = '" + colStat.getName() + "'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
            }
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next()) {
                colStat.setNumDistinctValues(rs.getInt(1));
                colStat.setNumNullValues(rs.getInt(2));

                if (colStat.isMinMaxCalculationRequired()) {
                    if (unknownType) {
                        int conversionType = colStat.jdbcType;
                        if (conversionType == Types.TIME || conversionType == Types.TIMESTAMP) {
                            conversionType = Types.DATE;
                        }
                        byte[] bytes = rs.getBytes(3);
                        String val = getRawAsString(colStat, bytes, conversionType);
                        colStat.setMin(val);
                        bytes = rs.getBytes(4);
                        val = getRawAsString(colStat, bytes, conversionType);
                        colStat.setMax(val);
                        //if not a known conversion type (rowid, date, char), then we could choose to compensate
                    } else {
                        colStat.setMin(rs.getString(3));
                        colStat.setMax(rs.getString(4));
                    }
                }
                
                String tblName = tblStat.getFullyQualifiedEscapedName();
                String colName = JdbcRelationalUtil.escapeDatabaseObjectName(colStat.getName());
                success = true;
                
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

    /**
     * @param colStat
     * @param bytes
     * @return
     * @throws SQLException
     */
    private String getRawAsString( ColumnStatistics colStat, byte[] bytes, int type ) {
        CallableStatement cs = null;
        try {
            cs = this.connection.prepareCall("{call dbms_stats.convert_raw_value(?, ?)}"); //$NON-NLS-1$
            cs.registerOutParameter(2, type);
            cs.setBytes(1, bytes);
            cs.execute();
            String val = cs.getString(2);
            return val;
        } catch (SQLException e) {
            return null; //TODO
        } finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException e) {
                }
            }
        }
    }
    
    
}
