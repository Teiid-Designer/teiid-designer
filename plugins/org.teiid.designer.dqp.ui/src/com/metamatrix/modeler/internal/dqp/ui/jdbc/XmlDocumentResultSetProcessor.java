/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
import com.metamatrix.modeler.internal.dqp.ui.views.XmlDocumentSqlResultsView;

/**
 * @since 4.3
 */
public final class XmlDocumentResultSetProcessor implements DqpUiConstants, IResultSetProcessor {

    private boolean processed;

    private IWorkbenchPartSite site;

    IWorkbenchPartSite getSite() {
        return this.site;
    }

    /**
     * @see net.sourceforge.sqlexplorer.IResultSetProcessor#isDefaultResultsViewNeeded()
     * @since 4.3
     */
    public boolean isDefaultResultsViewNeeded() {
        return !this.processed;
    }

    /**
     * @see net.sourceforge.sqlexplorer.IResultSetProcessor#process(java.lang.String, java.sql.ResultSet,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    public IStatus process( final String theSql,
                            final ResultSet theResults,
                            final IProgressMonitor theMonitor ) {
        this.processed = false;

        try {
            if (theResults != null && shouldProcess(theResults)) {
                this.processed = true;

                // create/process result set
                final IResults model = new XmlDocumentResultsModel(theSql, theResults);
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
     * @param model
     * @since 4.3
     */
    private void showResults( final IResults model ) {
        // let the UI display the results
        getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                try {
                    IWorkbenchPage page = getSite().getPage();
                    XmlDocumentSqlResultsView view = (XmlDocumentSqlResultsView)page.showView(Extensions.XML_DOC_SQL_RESULTS_VIEW);
                    view.addResults(model);
                } catch (Exception theException) {
                    theException.printStackTrace();
                    UTIL.log(IStatus.ERROR, theException.getLocalizedMessage());
                }
            }
        });
    }

    /**
     * @see net.sourceforge.sqlexplorer.IResultSetProcessor#processNoResultSet(java.lang.String, java.sql.Statement,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 4.3
     */
    public void processNoResultSet( String theSql,
                                    Statement theStatement,
                                    IProgressMonitor theMonitor ) {
        // do nothing as updates aren't possible for XML
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

        if ((metaData.getColumnCount() == 1) && (metaData.getColumnTypeName(1).equals(DataTypeManager.DefaultDataTypes.XML))) {
            result = true;
        }

        return result;
    }
}
