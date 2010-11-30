/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui.plan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.datatools.sqltools.core.SQLDevToolsConfiguration;
import org.eclipse.datatools.sqltools.core.SQLToolsFacade;
import org.eclipse.datatools.sqltools.core.services.ConnectionService;
import org.eclipse.datatools.sqltools.plan.PlanRequest;
import org.eclipse.datatools.sqltools.plan.PlanSupportRunnable;
import org.teiid.datatools.connectivity.ui.Activator;
import org.teiid.datatools.connectivity.ui.Messages;

public class TeiidPlanSupportRunnable extends PlanSupportRunnable {

    public TeiidPlanSupportRunnable( PlanRequest request,
                                     String profileName,
                                     String dbName ) {
        super(request, profileName, dbName);
    }

    @Override
    protected String explainPlan( Statement stmt ) {
        String result;
        try {
            String sql = this._request.getSql();
            stmt.execute("SET SHOWPLAN DEBUG"); //$NON-NLS-1$
            stmt.executeQuery(sql);
            ResultSet planRs = stmt.executeQuery("SHOW PLAN"); //$NON-NLS-1$
            planRs.next();
            result = planRs.getString("PLAN_XML");  //$NON-NLS-1$
        } catch (SQLException e) {
            result = ""; //$NON-NLS-1$
            IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
            		Messages.getString("TeiidPlanSupportRunnable.planDidNotWork"), e); //$NON-NLS-1$
            Activator.getDefault().getLog().log(status);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.sqltools.plan.PlanSupportRunnable#getConnection()
     */
    @Override
    public Connection getConnection() {
        if (_conn == null) {
            _conn = createConnection();
        }
        return _conn;
    }

    /* (non-Javadoc)
     * @see org.eclipse.datatools.sqltools.plan.PlanSupportRunnable#prepareConnection()
     */
    @Override
    protected void prepareConnection() {
        // always use a new connection
        _conn = createConnection();
    }

    /**
     * Returns a new created connection from the SQL tools.
     * 
     * @return the connection
     */
    public Connection createConnection() {
        final SQLDevToolsConfiguration config = SQLToolsFacade.getConfigurationByProfileName(_profileName);
        final ConnectionService conService = config.getConnectionService();
        final Connection con = conService.createConnection(_profileName, _dbName);

        _needReleaseConn = true;

        return con;
    }

    /**
     * Setting the connection has no effect, as we always use an own connection.
     * 
     * @see org.eclipse.datatools.sqltools.plan.PlanSupportRunnable#setConnection(java.sql.Connection)
     */
    @Override
    public void setConnection( final Connection conn ) {
        // do nothing, as we will always use an own connection
    }
}
