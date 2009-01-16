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
import net.sourceforge.sqlexplorer.IResultSetProcessor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.internal.dqp.ui.views.SqlResultsView;

/**
 * @since 4.3
 */
public class SqlResultSetProcessor implements DqpUiConstants, IResultSetProcessor {

    private IWorkbenchPartSite site;

    IWorkbenchPartSite getSite() {
        return this.site;
    }

    /**
     * @see net.sourceforge.sqlexplorer.IResultSetProcessor#isDefaultResultsViewNeeded()
     * @since 4.3
     */
    public boolean isDefaultResultsViewNeeded() {
        return false;
    }

    /**
     * @see net.sourceforge.sqlexplorer.IResultSetProcessor#process(java.lang.String, java.sql.ResultSet,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    public IStatus process( final String theSql,
                            final ResultSet theResultSet,
                            final IProgressMonitor theMonitor ) {
        try {
            if (theResultSet != null && shouldProcess(theResultSet)) {
                // create/process result set
                final IResults model = new SqlResultsModel(theSql, theResultSet);

                // display results
                showResults(model);
                return model.getStatus();
            }
        } catch (SQLException theException) {
            theException.printStackTrace();
            UTIL.log(IStatus.ERROR, theException.getLocalizedMessage());
            return new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, theException.getLocalizedMessage(), theException);
        }
        return Status.OK_STATUS;
    }

    /**
     * @see net.sourceforge.sqlexplorer.IResultSetProcessor#processNoResultSet(java.lang.String, java.sql.Statement,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    public void processNoResultSet( String theSql,
                                    Statement theStatement,
                                    IProgressMonitor theMonitor ) {
        // create/process update
        final IResults model = new SqlResultsModel(theSql, theStatement);

        // display results
        showResults(model);
    }

    /**
     * @see net.sourceforge.sqlexplorer.IResultSetProcessor#setWorkbenchPartSite(org.eclipse.ui.IWorkbenchPartSite)
     * @since 4.3
     */
    public void setWorkbenchPartSite( IWorkbenchPartSite theSite ) {
        this.site = theSite;
    }

    /**
     * Indicates if the specified results should be processed.
     * 
     * @param theResults the results being checked
     * @return <code>true</code> if results should be processed; <code>false</code>.
     * @throws SQLException
     * @since 4.3
     */
    private boolean shouldProcess( ResultSet theResults ) throws SQLException {
        boolean result = false;
        ResultSetMetaData metaData = theResults.getMetaData();

        if ((metaData.getColumnCount() != 1)
            || ((metaData.getColumnCount() == 1) && !metaData.getColumnTypeName(1).equals(DataTypeManager.DefaultDataTypes.XML))) {
            result = true;
        }

        return result;
    }

    /**
     * Show the specified results in the results view.
     * 
     * @param theResults the results being displayed
     * @since 4.3
     */
    private void showResults( final IResults theResults ) {
        // let the UI display the results
        getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                try {
                    IWorkbenchPage page = getSite().getPage();
                    SqlResultsView view = (SqlResultsView)page.showView(Extensions.SQL_RESULTS_VIEW);
                    view.addResults(theResults);
                } catch (Exception theException) {
                    theException.printStackTrace();
                    UTIL.log(IStatus.ERROR, theException.getLocalizedMessage());
                }
            }
        });
    }
}
