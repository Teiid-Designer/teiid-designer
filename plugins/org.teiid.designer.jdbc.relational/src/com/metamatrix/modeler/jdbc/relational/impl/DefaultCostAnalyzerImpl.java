/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.internal.jdbc.JdbcManagerImpl;
import com.metamatrix.modeler.internal.jdbc.relational.ModelerJdbcRelationalConstants;
import com.metamatrix.modeler.internal.jdbc.relational.util.JdbcRelationalUtil;
import com.metamatrix.modeler.jdbc.JdbcManager;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.relational.CostAnalyzer;
import com.metamatrix.modeler.jdbc.relational.JdbcRelationalPlugin;

/** 
 * Retrieve cost statistics from the tables and columns in the specified catalog and schema
 * in the specified database.
 * 
 * @since 4.3
 */
public class DefaultCostAnalyzerImpl implements CostAnalyzer {       
    // Connection setup
    protected JdbcSource src;
    protected String password;
        
    // Verbose output stream
    protected PrintStream outputStream;
    
    // Runtime state
    protected Connection connection;    
    
    // Used to create JdbcManager outside eclipse environment
    private static Container vdbContainer;
    private static File configFolder;
 
    /** 
     * 
     * @since 4.3
     */    
    public DefaultCostAnalyzerImpl(JdbcSource jdbcSource, String password) {
        this.src = jdbcSource;
        this.password = password;
    }
                 
    /** 
     * @see com.metamatrix.modeler.jdbc.relational.CostAnalyzer#setOutputStream(java.io.PrintStream)
     * @since 4.3
     */
    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
        
    /**
     * Logging implementation 
     * @param message
     * @since 4.3
     */
    protected void log(String message) {
        if(this.outputStream != null) {
            this.outputStream.println(message);
        }
    }

    /**
     * Open a connection to the datasource 
     * @throws Exception
     * @since 4.3
     */
    protected void connect() throws Exception {
        if (this.connection == null) {
            // Create the SQL connection ...
            final JdbcManager mgr = (JdbcRelationalPlugin.getDefault() == null ? JdbcManagerImpl.create(ModelerJdbcRelationalConstants.Util.getString("JdbcManager.name"), configFolder, vdbContainer) : JdbcRelationalUtil.getJdbcManager());//$NON-NLS-1$
            this.connection = mgr.createConnection(this.src, null, this.password);
        }
    }
    
    /**
     * Close the connection to the datasource  
     * @since 4.3
     */
    protected void disconnect() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch(SQLException se) {
                // ignore - nothing to do
            }
            this.connection = null;
        }
    }
                  
    /** 
     * @see com.metamatrix.modeler.jdbc.relational.CostAnalyzer#analyzeCost()
     * @since 4.3
     */
    public void collectStatistics(Map tblStats, IProgressMonitor monitor) throws Exception {
        log("\nLoading table statistics..."); //$NON-NLS-1$
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        long begin = System.currentTimeMillis();
        
        connect();
        try {
            for (Iterator it = tblStats.values().iterator(); it.hasNext();) {
                if (monitor.isCanceled()) {
                    break;
                }
                TableStatistics tblStat = (TableStatistics) it.next();
                try {
                    if (populateTableStatistics(tblStat, monitor)) {
                        prepareColumnStatistics(tblStat, monitor);
                        populateColumnStatistics(tblStat, monitor);                    
                    } else {
                        monitor.worked(tblStat.getColumnStats().size() + 1);
                    }
                } catch (Exception e) {
                    // Defect 21110 - Ignore and move to the next table
                    log("WARNING: Failed to retrieve statistics for table/view " + tblStat.getName()); //$NON-NLS-1$
                }
            }
        } finally {
            disconnect();
        }        
        log("Done loading tables, total time = " + (System.currentTimeMillis() - begin) + " ms"); //$NON-NLS-1$ //$NON-NLS-2$
    }
        
    protected boolean populateTableStatistics(TableStatistics tblStat, IProgressMonitor monitor) throws Exception {        
        if (monitor.isCanceled()) {
            return false;
        }
        monitor.subTask(ModelerJdbcRelationalConstants.Util.getString("DefaultCostAnalyzer.Progress.Calculating_table_statistics", tblStat.getName())); //$NON-NLS-1$
        long begin = System.currentTimeMillis();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String tblName = tblStat.getFullyQualifiedEscapedName();
            stmt = this.connection.createStatement();
            rs = stmt.executeQuery("select count(*) from " + tblName);         //$NON-NLS-1$
            if(rs.next()) {
                tblStat.setCardinality(rs.getInt(1));
                log("\t" + tblName + ": " + tblStat.getCardinality() + " rows    (in " + (System.currentTimeMillis() - begin) + " ms)");    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                return true;
            }
        } finally { 
            monitor.worked(1);
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
     * Set up the columns to collect stats for.
     * This method assumes that the TableInfo object's columnInfo map
     * has been populated with partially initialized ColumnInfo objects.
     * The purpose of this method is to set the uninitialized fields
     * (i.e. type and nullability).
     * 
     * @param tblStat
     * @param monitor 
     * @since 4.3
     */
    protected void prepareColumnStatistics(TableStatistics tblStat, IProgressMonitor monitor) throws Exception {        
        if (monitor.isCanceled()) {
            return;
        }
        ResultSet rs = null;
        try {
            DatabaseMetaData md = this.connection.getMetaData();
            Map colStats = tblStat.getColumnStats();
            rs = md.getColumns(tblStat.getCatalog(), tblStat.getSchema(), tblStat.getName(), "%"); //$NON-NLS-1$
            while(rs.next()) {
                String colName = JdbcRelationalUtil.escapeDatabaseObjectName(rs.getString(4));
                ColumnStatistics colStat = (ColumnStatistics) colStats.get(colName);
                if (colStat != null) {
                    colStat.setJdbcType(rs.getInt(5));
                    colStat.setNullable( rs.getInt(11) != DatabaseMetaData.attributeNoNulls);
                }
            }
        } finally {
            if(rs != null) {
                rs.close();
            }            
            monitor.worked(1);
        }
    }    
    
    /**
     * Collect stats for all the columns in a table 
     * @param tblStat The table 
     * @param monitor 
     * @return True if loaded, false if not (for instance, due to unavailable stats)
     * @throws Exception
     * @since 4.3
     */
    protected boolean populateColumnStatistics(TableStatistics tblStat, IProgressMonitor monitor) throws Exception {       
        for (Iterator it = tblStat.getColumnStats().values().iterator(); it.hasNext();) {
            if (monitor.isCanceled()) {
                return false;
            }
            ColumnStatistics colStat = (ColumnStatistics) it.next();
            monitor.subTask(ModelerJdbcRelationalConstants.Util.getString("DefaultCostAnalyzer.Progress.Calculating_column_statistics", //$NON-NLS-1$
                    new Object[] {colStat.getName(), tblStat.getName()}));
            try {
                if (! computeColumnStatistics(tblStat, colStat)) {
                    return false;
                }
            } catch (Exception e) {
                // Defect 21110 - Ignore and move to the next column
                log("WARNING: Failed to retrieve statistics for column " + colStat.getName() + " in table/view " + tblStat.getName()); //$NON-NLS-1$ //$NON-NLS-2$
            } finally {
                monitor.worked(1);
            }
        }        
        return true;
    }

    /**
     * Use generic SQL queries to detect the statistics for a column.  This is generally 
     * slower than database-specific methods but guaranteed to work.
     *  
     * @param tblStat The table
     * @param colStat The column
     * @return Always true 
     * @throws Exception
     * @since 4.3
     */
    protected boolean computeColumnStatistics(TableStatistics tblStat, ColumnStatistics colStat) throws Exception {

        long begin = System.currentTimeMillis();
        String tblName = tblStat.getFullyQualifiedEscapedName();
        String colName = JdbcRelationalUtil.escapeDatabaseObjectName(colStat.getName());
        boolean isNDVCalcuationRequired = colStat.isNDVCalculationRequired();
        boolean isMinMaxCalculationRequired = colStat.isMinMaxCalculationRequired();
        boolean isNNVCalculationRequired = colStat.isNNVCalculationRequired();
        
        /**
         * Need to enter this if block if either NDV calculation or MinMax calculation
         * is required.  
         */
        if(isNDVCalcuationRequired || isMinMaxCalculationRequired) {
            Statement stmt = null;
            ResultSet rs = null;
            
            try {
                stmt = this.connection.createStatement();
                
                String sql = "select "; //$NON-NLS-1$
                boolean isUniqueIdentifier = "uniqueidentifier".equalsIgnoreCase(colStat.getNativeType());//$NON-NLS-1$ 
                if(isNDVCalcuationRequired) {
                    
                    // Case 4124: have to handle SQL Server native "uniqueIdentifier" type -
                    // a column of that type is not allowed by SQL Server to be used in 
                    // a count() function (or it causes a SQLException).  The workaround
                    // is just to count the number of rows since the column has all unique values
                    if (isUniqueIdentifier) {
                        sql += "count(*)"; //$NON-NLS-1$ 
                    } else {
                        sql += "count(distinct " + colName + ")"; //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    
                    if(isMinMaxCalculationRequired) {
                        sql += ", "; //$NON-NLS-1$
                    }
                }
                if(isMinMaxCalculationRequired) {
                    sql += "min(" + colName + "), max(" + colName + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }

                sql += " from " + tblName; //$NON-NLS-1$
                
                // Case 4124: if column of SQL Server type "uniqueidentifier"
                // is also nullable (which will probably never happen in the
                // real world), add WHERE clause to eliminate nulls
                if (isUniqueIdentifier && isNNVCalculationRequired) {
                    sql += " where " + colName + " is not null"; //$NON-NLS-1$  //$NON-NLS-2$
                }
                
                rs = stmt.executeQuery(sql);
                
                if(rs.next()) {
                    int minOffset = 1;
                    if(isNDVCalcuationRequired) {
                        colStat.setNumDistinctValues(rs.getInt(1));
                        minOffset = 2;
                    }
                    if(isMinMaxCalculationRequired) {
                        colStat.setMin(rs.getString(minOffset));
                        colStat.setMax(rs.getString(++minOffset));
                    }
                }
            } finally { 
                if(rs != null) {
                    rs.close();
                }
                if(stmt != null) {
                    stmt.close();
                }
            }
        } 
        
        if(isNNVCalculationRequired) {
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = this.connection.createStatement();
                String sql = "select count(*) from " + tblName + " where " + colName + " is null"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                rs = stmt.executeQuery(sql); 
                
                if(rs.next()) {
                    colStat.setNumNullValues(rs.getInt(1));
                }
                
            } finally { 
                if(rs != null) {
                    rs.close();
                }
                if(stmt != null) {
                    stmt.close();
                }
            }            
        }
        
        log("\t\t" + tblName + "." + colName +  //$NON-NLS-1$ //$NON-NLS-2$
            ": NDV=" + colStat.getNumDistinctValues() +  //$NON-NLS-1$
            ", NNV=" + colStat.getNumNullValues() +  //$NON-NLS-1$
            ", min=" + colStat.getMin() +  //$NON-NLS-1$
            ", max=" + colStat.getMax() + //$NON-NLS-1$
            "    (in " + (System.currentTimeMillis() - begin) + " ms)"); //$NON-NLS-1$ //$NON-NLS-2$

        return true;
    }          
    
    public static void setVDBContainer(Container container) {
        vdbContainer = container;
    }
    
    public static void setConfigFolder(File folder) {
        configFolder = folder;
    }
}
