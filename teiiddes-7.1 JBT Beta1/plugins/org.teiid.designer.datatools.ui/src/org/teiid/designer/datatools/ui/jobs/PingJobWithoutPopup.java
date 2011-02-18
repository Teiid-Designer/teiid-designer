/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datatools.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.datatools.connectivity.ConnectionProfileConstants;
import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.internal.ui.ConnectivityUIPlugin;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.datatools.DatatoolsPlugin;
import org.teiid.designer.datatools.ui.DatatoolsUiConstants;

/**
 * 
 */
public class PingJobWithoutPopup extends Job {

    private IConnectionProfile icp;
    private Throwable exception;
    IStatus result = Status.OK_STATUS;

    /**
     * @param shell
     * @param profile
     */
    public PingJobWithoutPopup( Shell shell,
                                IConnectionProfile profile ) {
        super(DatatoolsUiConstants.UTIL.getStringOrKey("PingJobWithoutPopup.ping.job")); //$NON-NLS-1$
        setSystem(false);
        setUser(true);
        icp = profile;

    }

    @Override
    protected IStatus run( IProgressMonitor monitor ) {

        monitor.beginTask(ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.title"), //$NON-NLS-1$
                          IProgressMonitor.UNKNOWN);

        IConnection con = createTestConnection(icp);

        monitor.done();

        exception = getTestConnectionException(con);

        if (con != null) con.close();

        if (null != exception) {
            Object[] params = new Object[] {icp.getName(), exception.getMessage()};
            String errorMessage = DatatoolsUiConstants.UTIL.getString("PingJobWithoutPopup.errorPingingConnectionProfile", params); //$NON-NLS-1$
            result = new Status(IStatus.ERROR, DatatoolsPlugin.PLUGIN_ID, errorMessage);
        }
        return result;
    }

    public static IConnection createTestConnection( IConnectionProfile icp ) {
        if (icp == null) return null;
        return icp.createConnection(ConnectionProfileConstants.PING_FACTORY_ID);
    }

    public static Throwable getTestConnectionException( IConnection conn ) {
        return conn != null ? conn.getConnectException() : new RuntimeException(
                                                                                ConnectivityUIPlugin.getDefault().getResourceString("actions.ping.failure")); //$NON-NLS-1$
    }

}
