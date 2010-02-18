/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.jdbc;

import net.sourceforge.sqlexplorer.ISqlExecVetoListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import com.metamatrix.modeler.dqp.internal.execution.WorkspaceProblemsExecutionValidatorImpl;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.modeler.dqp.ui.connection.IVdbConnectionMgr;
import com.metamatrix.modeler.internal.dqp.ui.actions.VdbExecutor;
import com.metamatrix.modeler.internal.dqp.ui.dialogs.StaleVdbConnectionDialog;
import com.metamatrix.vdb.edit.VdbEditingContext;

/**
 * @since 4.3
 */
public class StaledVDBChecker implements ISqlExecVetoListener {
    /**
     * Constant indicating SQL execution should not continue since the user has requested a reconnect.
     * 
     * @since 5.0.1
     */
    static final IStatus STOP_EXECUTION_RECONNECT = new Status(
                                                               IStatus.ERROR,
                                                               DqpUiConstants.PLUGIN_ID,
                                                               IStatus.OK,
                                                               DqpUiConstants.UTIL.getStringOrKey("DqpUiPlugin.stopSqlExecutionReconnect"), //$NON-NLS-1$
                                                               null);

    /**
     * Constant indicating SQL execution should not continue since the user has requested to close the stale connection.
     * 
     * @since 5.0.1
     */
    static final IStatus STOP_EXECUTION_CLOSE_CONNECTION = new Status(
                                                                      IStatus.ERROR,
                                                                      DqpUiConstants.PLUGIN_ID,
                                                                      IStatus.OK,
                                                                      DqpUiConstants.UTIL.getStringOrKey("DqpUiPlugin.stopSqlExecutionCloseConnection"), //$NON-NLS-1$
                                                                      null);

    /**
     * @see net.sourceforge.sqlexplorer.ISqlExecVetoListener#continueSqlExecution(java.lang.Object)
     * @since 4.3
     */
    public IStatus continueSqlExecution( Object theConnection ) {
        IStatus result = ISqlExecVetoListener.CONTINUE_EXECUTION;
        IVdbConnectionMgr connMgr = DqpUiPlugin.getDefault().getVdbConnectionMgr();
        if (connMgr.isConnectionStale(theConnection)) {
            result = handleStaleConnection(theConnection, "StaleVdbConnectionDialog.staledVDB");//$NON-NLS-1$
        }
        return result;
    }

    /**
     * Presents the user with choices to close then reconnect connect, just close the connection, or leave stale connection open.
     * 
     * @param theConnection the stale connection
     * @since 5.0.1
     */
    protected static IStatus handleStaleConnection( final Object theConnection,
                                                    final String messageKey ) {
        final IStatus[] result = new IStatus[] {ISqlExecVetoListener.CONTINUE_EXECUTION};

        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                if (!Display.getDefault().isDisposed()) {
                    try {
                        IVdbConnectionMgr connMgr = DqpUiPlugin.getDefault().getVdbConnectionMgr();
                        VdbEditingContext vdbContext = connMgr.getVdbEditingContext(theConnection);

                        if (vdbContext != null) {
                            VdbExecutor executor = new VdbExecutor(vdbContext, new WorkspaceProblemsExecutionValidatorImpl());
                            boolean validVdb = (executor.canExecute().getSeverity() != IStatus.ERROR);

                            String vdbName = vdbContext.getVirtualDatabase().getName();
                            StaleVdbConnectionDialog dlg = new StaleVdbConnectionDialog(
                                                                                        null,
                                                                                        vdbName,
                                                                                        validVdb,
                                                                                        DqpUiConstants.UTIL.getString(messageKey,
                                                                                                                      vdbName));
                            dlg.setOptions(false, true); // don't show reconnect option
                            dlg.open(); // blocks until gets a user response

                            if (dlg.closeConnection() || dlg.reconnect()) {
                                connMgr.closeConnection(vdbContext);

                                if (dlg.reconnect()) {
                                    result[0] = STOP_EXECUTION_RECONNECT;
                                    // not implemented since reconnect is not an option
                                } else {
                                    result[0] = STOP_EXECUTION_CLOSE_CONNECTION;
                                }
                            }
                        } else {
                            DqpUiConstants.UTIL.log(IStatus.ERROR,
                                                    DqpUiConstants.UTIL.getString("DqpUiPlugin.vdbContextNotFound", theConnection)); //$NON-NLS-1$
                        }
                    } catch (Exception theException) {
                        String msg = DqpUiConstants.UTIL.getStringOrKey("DqpUiPlugin.problemHandlingStaleConnection"); //$NON-NLS-1$
                        DqpUiConstants.UTIL.log(IStatus.ERROR, theException, msg);
                    }
                }
            }
        });

        return result[0];
    }

}
