/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.data;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.jdbc.JdbcPlugin;
import org.teiid.designer.jdbc.JdbcUtil;


/**
 * A class into which results from JDBC methods can be placed.  This class is not
 * thread-safe, so it must not be used simultaneously from multiple threads.
 *
 * @since 8.0
 */
public class Response {
    
    private static final int NUM_COLUMNS_NOT_SET = -1;
    
    private final Request request;
    private final List records;
    private int numColumns;
    private ResultsMetadata metadata;

    /**
     * Construct an instance of Response
     * 
     */
    public Response( final Request request ) {
        CoreArgCheck.isNotNull(request);
        this.request = request;
        this.records = new LinkedList();
        this.numColumns = NUM_COLUMNS_NOT_SET;
        this.metadata = null;
    }
    
    /**
     * Return the metadata for this results object.
     * @return the result metadata; never null
     */
    public ResultsMetadata getMetadata() {
        return this.metadata;
    }

    protected void setResultsMetadata( final ResultsMetadata metadata ) {
        this.metadata = metadata;
    }
    
//    public void addColumn( final ColumnMetadata metadata ) { 
//    }
    
    public void addRecord( final List record ) {
        // Set the number of columns
        if ( this.numColumns == NUM_COLUMNS_NOT_SET ) {
            this.numColumns = record.size();
        } else {
            // Otherwise the number of columns have already been set, 
            // so ensure the record has the same number of columns ...
            CoreArgCheck.isTrue(record.size() == this.numColumns,
                            JdbcPlugin.Util.getString("Response.Add_error_column_count_mismatch",record,new Integer(this.numColumns))); //$NON-NLS-1$
        }
        this.records.add(record);
    }
    
    public List getRecords() {
        return this.records;
    }
    
    public int getColumnCount() {
        return this.numColumns;
    }

    /**
     * @return
     */
    public Request getRequest() {
        return request;
    }

    public static void addResults( final Response results, final ResultSet resultSet,
                                   final boolean addMetadata ) throws SQLException {
        addResults(results, resultSet, addMetadata, null);
    }
        

    public static void addResults( final Response results, final ResultSet resultSet,
                                   final boolean addMetadata, TupleValidator validator ) throws SQLException {

        final ResultSetMetaData metadata = resultSet.getMetaData();
            
        final int numColumns = metadata.getColumnCount();
        while ( resultSet.next() ) {
            final List tuple = new ArrayList(numColumns);
            for ( int i=1;i<=numColumns;++i ) {
                final Object cell = resultSet.getObject(i);     // index starts at 1!!!
                tuple.add(cell);
            }
            if(validator == null || validator.isTupleValid(tuple)) {
                results.addRecord(tuple);
            }
        }
        
        if ( addMetadata ) {
            // Try to do this, but don't fail if we can't (some drivers have very poor
            // support for result set metadata, and we'd don't want to stop working
            // with the actual metadata, so do this in a try/catch ...
            try {
                // Put the result set metadata into the Response ...
                final ResultsMetadata resultsMetadata = ResultsMetadata.create(metadata);
                results.setResultsMetadata(resultsMetadata);
            } catch (SQLException e) {
                // Must not have been able to get the result set metadata ...
                final IStatus error = JdbcUtil.createIStatus(e);
                if ( error != null ) {
                    results.getRequest().addProblems(Collections.singletonList(error));
                }
                JdbcPlugin.Util.log(e);
            }
        }

        // If there are warnings, add to the request ...
        SQLWarning warning = resultSet.getWarnings();
        if ( warning != null ) {
            final List warnings = new ArrayList();
            while ( warning != null ) {
                warnings.add(JdbcUtil.createIStatus(warning));
                warning = warning.getNextWarning();
            }
            if ( warnings.size() != 0 ) {
                results.getRequest().addProblems(warnings);
            }
        }

    }
    
    public static void addResults( final Response results, final Object result, 
                                   final boolean addMetadata ) {
        // The actual result ...
        final List tuple = new ArrayList(1);
        tuple.add(result);
        results.addRecord(tuple);

        if ( addMetadata ) {
            // Put the result set metadata into the Response ...
            final ResultsMetadata metadata = new ResultsMetadata();
            results.setResultsMetadata(metadata);
        }
    }

}
