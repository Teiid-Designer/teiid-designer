package org.teiid.datatools.connectivity.ui.plan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.eclipse.datatools.sqltools.core.SQLDevToolsConfiguration;
import org.eclipse.datatools.sqltools.core.SQLToolsFacade;
import org.eclipse.datatools.sqltools.core.services.ConnectionService;
import org.eclipse.datatools.sqltools.plan.PlanRequest;
import org.eclipse.datatools.sqltools.plan.PlanSupportRunnable;

public class TeiidPlanSupportRunnable extends PlanSupportRunnable {

    public TeiidPlanSupportRunnable( PlanRequest request,
                                     String profileName,
                                     String dbName ) {
        super(request, profileName, dbName);
    }

    // TODO: SQLDevToolsUIConfiguration dbdefinition is undefined.
    @Override
    protected String explainPlan( Statement stmt ) {
        // execute the query
        String result = "Didn't work";
        try {
            String sql = this._request.getSql();
            ResultSet resultSet = stmt.executeQuery(sql + " OPTION PLANONLY");
            result = resultSet.getString(0);
        } catch (SQLException e) {
            result += e.getMessage();
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
