package net.sourceforge.sqlexplorer.sqlpanel;

/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 * andreamazzolini@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.IResultSetProcessor;
import net.sourceforge.sqlexplorer.ISqlExecVetoListener;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.views.SqlResultsView;
import net.sourceforge.sqlexplorer.sessiontree.model.SessionTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchPartSite;

public class SqlExecProgress implements IConstants, IRunnableWithProgress {

    private static IResultSetProcessor[] getAdditionalResultSetProcessors() {
        if (SqlExecProgress.resultSetProcessors == null) {
            // get the ResultSet Processor extension point from the plugin class
            final IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(SQLExplorerPlugin.PLUGIN_ID,
                                                                                                     ExtensionPoints.ResultSetProcessor.ID);

            // get the all extensions to this extension point
            final IExtension[] extensions = extensionPoint.getExtensions();

            // if no extensions no work to do
            if (extensions.length == 0) SqlExecProgress.resultSetProcessors = new IResultSetProcessor[0];
            else {
                final List temp = new ArrayList(extensions.length);

                // make executable extensions for every CLASS_NAME
                for (int i = 0; i < extensions.length; ++i) {
                    final IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                    for (int j = 0; j < elements.length; ++j)
                        try {
                            final Object extension = elements[j].createExecutableExtension(ExtensionPoints.ResultSetProcessor.CLASS_NAME);

                            if (extension instanceof IResultSetProcessor) temp.add(extension);
                            else {
                                // not an IResultSetProcessor. just log it and continue
                                final String msg = MessageFormat.format(Messages.getString("SqlExecProgress.invalidResulSetProcessorClass"), //$NON-NLS-1$
                                                                        new Object[] {extension.getClass().getName()});
                                SQLExplorerPlugin.error(msg, null);
                            }
                        } catch (final Exception theException) {
                            final String msg = MessageFormat.format(Messages.getString("SqlExecProgress.resultSetProcessInitializationProblem"), //$NON-NLS-1$
                                                                    new Object[] {elements[j].getAttribute(ExtensionPoints.ResultSetProcessor.CLASS_NAME)});
                            SQLExplorerPlugin.error(msg, theException);
                        }
                }

                SqlExecProgress.resultSetProcessors = (IResultSetProcessor[])temp.toArray(new IResultSetProcessor[temp.size()]);
            }
        }

        return SqlExecProgress.resultSetProcessors;
    }

    private static ISqlExecVetoListener[] getSqlExecVetoListeners() {
        if (SqlExecProgress.sqlExecVetoListeners == null) {
            // get the ResultSet Processor extension point from the plugin class
            final IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(SQLExplorerPlugin.PLUGIN_ID,
                                                                                                     ExtensionPoints.SqlExecVetoListener.ID);

            // get the all extensions to this extension point
            final IExtension[] extensions = extensionPoint.getExtensions();

            // if no extensions no work to do
            if (extensions.length == 0) SqlExecProgress.sqlExecVetoListeners = new ISqlExecVetoListener[0];
            else {
                final List temp = new ArrayList(extensions.length);

                // make executable extensions for every CLASS_NAME
                for (int i = 0; i < extensions.length; ++i) {
                    final IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                    for (int j = 0; j < elements.length; ++j)
                        try {
                            final Object extension = elements[j].createExecutableExtension(ExtensionPoints.SqlExecVetoListener.CLASS_NAME);

                            if (extension instanceof ISqlExecVetoListener) temp.add(extension);
                            else {
                                // not an ISqlExecVetoListener. just log it and continue
                                final String msg = MessageFormat.format(Messages.getString("SqlExecProgress.invalidSqlExecVetoListenerClass"), //$NON-NLS-1$
                                                                        new Object[] {extension.getClass().getName()});
                                SQLExplorerPlugin.error(msg, null);
                            }
                        } catch (final Exception theException) {
                            final String msg = MessageFormat.format(Messages.getString("SqlExecProgress.sqlExecVetoListenerInitializationProblem"), //$NON-NLS-1$
                                                                    new Object[] {elements[j].getAttribute(ExtensionPoints.ResultSetProcessor.CLASS_NAME)});
                            SQLExplorerPlugin.error(msg, theException);
                        }
                }

                SqlExecProgress.sqlExecVetoListeners = (ISqlExecVetoListener[])temp.toArray(new ISqlExecVetoListener[temp.size()]);
            }
        }

        return SqlExecProgress.sqlExecVetoListeners;
    }

    /**
     * @see org.eclipse.jface.operation.IRunnableWithProgress#run(IProgressMonitor)
     */

    private final String _sql;
    SQLEditor txtComp;

    int maxRows;

    private final SessionTreeNode sessionTreeNode;

    private static IResultSetProcessor[] resultSetProcessors;

    private static ISqlExecVetoListener[] sqlExecVetoListeners;

    Throwable exception;

    public SqlExecProgress( final String sqlString,
                            final SQLEditor txtComp,
                            final int maxRows,
                            final SessionTreeNode sessionTreeNode ) {
        _sql = sqlString;
        this.txtComp = txtComp;
        this.maxRows = maxRows;
        this.sessionTreeNode = sessionTreeNode;

    }

    /**
     * @return
     */
    public Throwable getException() {
        return exception;
    }

    private SqlTableModel processQuery( final String sql,
                                        final IProgressMonitor monitor ) {
        LocalThread lt = null;
        Statement stmt = null;
        try {
            // need to be able to navigate the ResultSet in both directions.
            stmt = sessionTreeNode.getConnection().getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                                                   ResultSet.CONCUR_READ_ONLY);
            lt = new LocalThread(monitor, stmt);
            lt.start();

            final boolean b = stmt.execute(sql);
            final IResultSetProcessor[] processors = getAdditionalResultSetProcessors();
            final IWorkbenchPartSite site = this.txtComp.getSite();

            if (b) {
                SqlTableModel md = null;
                final ResultSet rs = stmt.getResultSet();
                if (rs != null) {
                    // send results to the processors
                    boolean showDefaultView = true;

                    if (processors.length != 0) for (int i = 0; i < processors.length; ++i)
                        try {
                            if (monitor.isCanceled()) break;

                            processors[i].setWorkbenchPartSite(site);
                            final IStatus status = processors[i].process(sql, rs, monitor);

                            // when one of the processors doesn't need the default view don't ask the others
                            if (showDefaultView) showDefaultView = processors[i].isDefaultResultsViewNeeded();

                            // reset the result set for the next processor
                            if (!status.isOK()) break;
                            // get ready for the next processor
                            rs.beforeFirst();
                        } catch (final Exception theException) {
                            final String msg = MessageFormat.format(Messages.getString("SqlExecProgress.resulSetProcessorProblem"), //$NON-NLS-1$
                                                                    new Object[] {processors[i].getClass().getName()});
                            SQLExplorerPlugin.error(msg, theException);
                        }

                    if (!monitor.isCanceled() && showDefaultView) {
                        final ResultSetMetaData metaData = rs.getMetaData();
                        final int count = metaData.getColumnCount();

                        final String[] ss = new String[count];
                        for (int i = 0; i < count; i++)
                            ss[i] = metaData.getColumnName(i + 1);
                        final SQLTableSorter sorter = new SQLTableSorter(count, metaData);
                        final ResultSetReader reader = new ResultSetReader(rs);
                        md = new SqlTableModel(reader, metaData, maxRows, sessionTreeNode.getConnection(), ss, sorter, sql);
                    }

                    return md;
                }
            } else {
                lt.endMonitor();

                // ResultSet is null so let processors process the Statement
                if (processors.length != 0) for (int i = 0; i < processors.length; ++i)
                    try {
                        if (monitor.isCanceled()) break;

                        processors[i].setWorkbenchPartSite(site);
                        processors[i].processNoResultSet(sql, stmt, monitor);
                    } catch (final Exception theException) {
                        final String msg = MessageFormat.format(Messages.getString("SqlExecProgress.resulSetProcessorProblem"), //$NON-NLS-1$
                                                                new Object[] {processors[i].getClass().getName()});
                        SQLExplorerPlugin.error(msg, theException);
                    }

                // commented out this code that displays the update row count since we now display it in our result views
                // txtComp.getSite().getShell().getDisplay().asyncExec(new Runnable(){
                // public void run(){
                // try{
                // long endTime=System.currentTimeMillis();
                //							String message=Messages.getString("Time__1")+" "+(int)(endTime-startTime)+Messages.getString("_ms");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                //							txtComp.setMessage(message + Messages.getString("SqlExecProgress._updated_rowcount__5") + stmt.getUpdateCount()); //$NON-NLS-1$
                //							
                // }catch(Throwable e){
                //							SQLExplorerPlugin.error("Error displaying data ",e); //$NON-NLS-1$
                // txtComp.setMessage(e.getMessage());
                // }
                // };
                // });
            }

        } catch (final Throwable e) {
            if (!monitor.isCanceled()) {
                SQLExplorerPlugin.error("Error processing query ", e); //$NON-NLS-1$
                exception = e;
            }

            return null;
        } finally {
            if (lt != null) lt.endMonitor();
            if (stmt != null) try {
                stmt.close();
            } catch (final Exception e) {
                final String msg = Messages.getString("SqlExecProgress.errorClosingStatement"); //$NON-NLS-1$
                SQLExplorerPlugin.error(msg, e);
            }
            SQLExplorerPlugin.getDefault().performGc(1000);
        }
        return null;

    }

    public void run( final IProgressMonitor monitor ) {

        // ask the veto listeners if we should continue. if one listeners wishes to stop execution
        // just return after logging
        final ISqlExecVetoListener[] vetoListeners = getSqlExecVetoListeners();

        if (vetoListeners.length != 0) for (int i = 0; i < vetoListeners.length; ++i) {
            final IStatus status = vetoListeners[i].continueSqlExecution(this.sessionTreeNode);

            if (status.getSeverity() == IStatus.ERROR) // String msg = status.getMessage();
            //                    
            // if (StringUtilities.isEmpty(msg)) {
            //                        msg = MessageFormat.format(Messages.getString("SqlExecProgress.executionVetoedMsg"), //$NON-NLS-1$
            // new Object[] {vetoListeners[i].getClass().getName()});
            // }
            //                    
            // SQLExplorerPlugin.error(msg, null);
            return;
        }

        final long startTime = System.currentTimeMillis();
        final QueryTokenizer qt = new QueryTokenizer(_sql, ";", "#"); //$NON-NLS-1$
        final List queryStrings = new ArrayList();
        while (qt.hasQuery()) {
            final String querySql = qt.nextQuery();
            // ignore commented lines.
            if (!querySql.startsWith("--")) queryStrings.add(querySql);
        }

        final ArrayList rsLis = new ArrayList();
        SqlTableModel sqlTbModel = null;
        while (!queryStrings.isEmpty()) {

            final String querySql = (String)queryStrings.remove(0);
            if (querySql != null) {
                SQLExplorerPlugin.getDefault().addSQLtoHistory(querySql, this.sessionTreeNode);
                sqlTbModel = processQuery(querySql, monitor);
                if (sqlTbModel != null) rsLis.add(sqlTbModel);
            }
        }

        if (!rsLis.isEmpty()) txtComp.getSite().getShell().getDisplay().asyncExec(new Runnable() {
            public void run() {
                try {
                    final SqlResultsView resultsView = (SqlResultsView)txtComp.getSite().getPage().showView("net.sourceforge.sqlexplorer.plugin.views.SqlResultsView");
                    resultsView.setData(((SqlTableModel[])rsLis.toArray(new SqlTableModel[rsLis.size()])));// mo,new
                    // SQLTableSorter(count,metaData));
                    final long endTime = System.currentTimeMillis();
                    final String message = Messages.getString("Time__1") + " " + (int)(endTime - startTime) + Messages.getString("_ms"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    txtComp.setMessage(message);
                } catch (final Throwable e) {
                    SQLExplorerPlugin.error("Error displaying data", e);
                    txtComp.setMessage(e.getMessage());
                }
            }
        });
    }

    private class LocalThread extends Thread {

        IProgressMonitor monitor;

        Statement stmt;

        boolean end = false;

        public LocalThread( final IProgressMonitor monitor,
                            final Statement stmt ) {
            this.monitor = monitor;
            this.stmt = stmt;
        }

        public void endMonitor() {
            end = true;
        }

        @Override
        public void run() {
            try {
                while (true) {

                    if (end) break;
                    if (monitor.isCanceled()) {
                        stmt.cancel();
                        break;
                    }
                    Thread.sleep(100);
                }
            } catch (final Throwable e) {
            }

        }
    }
}
