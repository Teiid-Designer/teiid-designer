/*
 * Copyright © 2000-2005 MetaMatrix, Inc.
 * All rights reserved.
 */
package net.sourceforge.sqlexplorer;

import java.sql.ResultSet;
import java.sql.Statement;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IWorkbenchPartSite;


/** 
 * An <code>IResultSetProcessor</code> processes {@link java.sql.ResultSet}s produced by VDB execution.
 * @since 4.3
 */
public interface IResultSetProcessor {

    /**
     * Indicates if the default SQL Results View should be shown. If the return of this method is dependent
     * on the completion of an asynchronously run {@link #process(String, ResultSet, IProgressMonitor)} method,
     * make sure this method waits until the the results have been processed before returning an answer.
     * @return <code>true</code> if the default view should be shown; <code>false</code> otherwise.
     * @since 4.3
     */
    boolean isDefaultResultsViewNeeded();
    
    /**
     * Processes the specified <code>ResultSet</code>. <strong>A reference to the <code>ResultSet</code>
     * should not be kept after this method completes. The <code>ResultSet</code> is closed after all
     * <code>IResultSetProcessor</code>s have finished their processing. Also, the <code>ResultSet</code>
     * should not be processed asynchronously or in a differenct thread as it is being reset after this
     * method returns.</strong>
     * @param theSql the SQL executed to produce the specified <code>ResultSet</code>
     * @param theResultSet the result set being processed
     * @param theMonitor the progress monitor
	 * @return IStatus - Staus of the processing Status.OK if everything worked 
     * correctly; 
     * @since 4.3
     */
    IStatus process(String theSql, ResultSet theResultSet, IProgressMonitor theMonitor);
    
    /**
     * Process the specified <code>Statement</code>. The specified <code>Statement</code> did not produce a <code>ResultSet</code>
     * after being executed. This usually means an update occurred.
     * @param theSql the SQL executed to produce the specified <code>ResultSet</code>
     * @param theStatement the executed statement
     * @param theMonitor the progress monitor
     * @since 4.3
     */
    void processNoResultSet(String theSql, Statement theStatement, IProgressMonitor theMonitor);
    
    /**
     * Sets the specified <code>IWorkbenchPartSite</code>. 
     * @param theSite the value being set
     * @since 4.3
     */
    void setWorkbenchPartSite(IWorkbenchPartSite theSite);
}
