/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.modeler.internal.jdbc.JdbcUtil;
import com.metamatrix.modeler.jdbc.JdbcPlugin;

/**
 * QueryRequest
 */
public class QueryRequest extends Request {
    
    private final String sql;

    /**
     * Construct an instance of QueryRequest.
     * 
     */
    public QueryRequest( final String name, final Connection connection, final String sql ) {
        super(name,connection);
        ArgCheck.isNotNull(sql);
        ArgCheck.isNotZeroLength(sql);
        this.sql = sql;
    }

    /**
     * @return
     */
    public String getSql() {
        return sql;
    }

    /**
     * This method assumes that there the SQL will either return no ResultSet
     * or one ResultSet (not multiple ResultSets).
     * @see com.metamatrix.modeler.internal.jdbc.Request#performInvocation(java.sql.Connection, com.metamatrix.modeler.internal.jdbc.Response)
     */
    @Override
    protected IStatus performInvocation(final Response results) {
        final Connection conn = (Connection) getTarget();
        final List statuses = new ArrayList();
        
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = conn.createStatement();
            final boolean isResultSet = statement.execute(this.sql);
            
            // If the statement resulted in a ResultSet ...
            if ( isResultSet ) {
                resultSet = statement.getResultSet();
                Response.addResults(results,resultSet,this.isMetadataRequested());
            } else {
                // Otherwise, just add in the update count
                try {
                    final int updateCount = statement.getUpdateCount();
                    final Object value = new Integer(updateCount);
                    Response.addResults(results,value,true);
                } catch (SQLException e) {
                    statuses.add( JdbcUtil.createIStatus(e) );
                }
            }
            
            // Add any errors or warnings ...
            SQLWarning warning = statement.getWarnings();
            // Unchain the warnings ...
            while ( warning != null ) {
                statuses.add( JdbcUtil.createIStatus(warning) );
                warning = warning.getNextWarning();
            }
        } catch (SQLException e) {
            statuses.add( JdbcUtil.createIStatus(e) );
        } catch (Throwable e) {
            statuses.add( JdbcUtil.createIStatus(e) );
        } finally {
            if ( resultSet != null ) {
                // attempt to close the result set ...
                try {
                    resultSet.close();
                } catch ( SQLException e ) {
                    statuses.add( JdbcUtil.createIStatus(e) );
                } finally {
                    resultSet = null;
                }
            }
            if ( statement != null ) {
                // attempt to close the statement ...
                try {
                    statement.close();
                } catch ( SQLException e ) {
                    statuses.add( JdbcUtil.createIStatus(e) );
                } finally {
                    statement = null;
                }
            }
        }
        
        // Process the status(es) that may have been created due to problems/warnings
        if ( statuses.size() == 1 ) {
            return (IStatus)statuses.get(0);
        }
        if ( statuses.size() > 1 ) {
            final String text = JdbcPlugin.Util.getString("QueryRequest.Error_while_processing_sql_against_connection",sql,conn); //$NON-NLS-1$
            return JdbcUtil.createIStatus(statuses,text);
        }
        
        // If there are no errors, return null
        return null;
    }

}
