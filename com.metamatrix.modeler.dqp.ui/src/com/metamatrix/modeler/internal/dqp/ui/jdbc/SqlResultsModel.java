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

package com.metamatrix.modeler.internal.dqp.ui.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;


/** 
 * @since 4.3
 */
public class SqlResultsModel implements DqpUiConstants,
                                        IResults {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private static final String PREFIX = I18nUtil.getPropertyPrefix(SqlResultsModel.class);
    
    private static final IStatus GOOD_STATUS = new Status(IStatus.OK, PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
    
    private static final Object[] NO_RESULTS = new Object[0];

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private String[] columnNames;
    
    private String sql;
    
    private Statement statement;
    
    private IStatus status = GOOD_STATUS;
    
    private Object[] rows = NO_RESULTS;
    
    private int totalRows = 0;
    
    private boolean updateType = false;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public SqlResultsModel(String theSql,
                           ResultSet theResults) {
        this.sql = theSql;

        try {
            this.statement = theResults.getStatement();
            processMetadata(theResults.getMetaData());            
            setResults(theResults);
        } catch (SQLException theException) {
            this.status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, theException.getLocalizedMessage(), theException);
            this.columnNames = new String[0];
            this.rows = NO_RESULTS;
            UTIL.log(theException);
        }
    }
    
    /**
     * Constructs a model for an update. 
     * @param theSql
     * @param theStatement
     * @since 4.3
     */
    public SqlResultsModel(String theSql,
                           Statement theStatement) {
        this.sql = theSql;
        this.updateType = true;

        try {
            this.statement = theStatement;
            this.columnNames = new String[] {UTIL.getString(PREFIX + "updateCountColumn")}; //$NON-NLS-1$
            this.totalRows = theStatement.getUpdateCount();
            this.rows = new Object[] {new Object[] {new Integer(this.totalRows)}};
        } catch (SQLException theException) {
            this.status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, theException.getLocalizedMessage(), theException);
            this.columnNames = new String[0];
            UTIL.log(theException);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    public String[] getColumnNames() {
        return this.columnNames;
    }
    
    /**
     * An array of rows. Each row being also being an array of columns. 
     * @return the rows
     * @since 4.3
     */
    public Object[] getRows() {
        return this.rows;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults#getSql()
     * @since 4.3
     */
    public String getSql() {
        return this.sql;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults#getStatement()
     * @since 4.3
     */
    public Statement getStatement() {
        return this.statement;
    }
    
    /** 
     * @see com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults#getStatus()
     * @since 4.3
     */
    public IStatus getStatus() {
        return this.status;
    }
    
    /**
     * The total amount of rows in the {@link ResultSet}. Maybe different than the number of rows processed
     * if the user preference for max row count is less than the total. 
     * @return the total number of rows in the results
     * @since 4.3
     */
    public int getTotalRowCount() {
        return this.totalRows;
    }
    
    /**
     * Indicates if this model was created using a statement that tried to update rows. 
     * @return <code>true</code> if an update model type; <code>false</code>.
     * @since 4.3
     */
    public boolean isUpdateModel() {
        return this.updateType;
    }
    
    private void processMetadata(ResultSetMetaData theMetadata) throws SQLException {
        int numCols = theMetadata.getColumnCount();
        this.columnNames = new String[numCols];
    
        for (int i = 0; i < numCols; ++i) {
            this.columnNames[i] = theMetadata.getColumnName(i + 1);
        }
    }
    
    private void setResults(ResultSet theResults) {
        IPreferenceStore store = SQLExplorerPlugin.getDefault().getPreferenceStore();
        final int MAX_ROWS = store.getInt(IConstants.MAX_SQL_ROWS);
        List<Object[]> temp = new ArrayList<Object[]>();
        this.totalRows = 0;
        
        try {
            // only process rows up to the maximum allowed by the preference
            while ((this.totalRows < MAX_ROWS) && theResults.next()) {
                Object[] row = new Object[this.columnNames.length];
                
                for (int i = 0; i < this.columnNames.length; i++) {
                    row[i] = theResults.getObject(i + 1);
                }
                
                temp.add(row);
                ++this.totalRows;
            }
            
            while (theResults.next()) {
                ++this.totalRows;
            }
        } catch (SQLException theException) {
            // capture the exception but show the rows if we read any of them
            this.status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, theException.getLocalizedMessage(), theException);
            UTIL.log(theException);
        }                
        this.rows = temp.toArray();
    }

}
