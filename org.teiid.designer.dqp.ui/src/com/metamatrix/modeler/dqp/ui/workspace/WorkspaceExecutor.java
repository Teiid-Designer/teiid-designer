/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.ui.workspace;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.PLUGIN_ID;
import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.lf5.viewer.configure.ConfigurationManager;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminObject;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.adminapi.ProcessObject;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.jdbc.api.Connection;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.config.ConfigurationChangeEvent;
import com.metamatrix.modeler.dqp.config.IConfigurationChangeListener;
import com.metamatrix.modeler.dqp.internal.config.DqpPath;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.SqlResultsModel;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.XmlDocumentResultsModel;
import com.metamatrix.query.metadata.QueryMetadataInterface;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * This class used as mediator to run/execute any workspace execution related tasks.
 */
public class WorkspaceExecutor extends QueryClient {

    private static final String PREFIX = I18nUtil.getPropertyPrefix(WorkspaceExecutor.class);
    private static WorkspaceExecutor _INSTANCE = new WorkspaceExecutor();

    boolean started;
    private WorkspaceInfoImpl workspaceInfo = new WorkspaceInfoImpl();
    private Connection adminConnection;

    /**
     * The current JDBC statement or <code>null</code>
     * 
     * @since 5.5.3
     */
    private PreparedStatement stmt;

    /**
     * Indicates if a JDBC statement is currently executing
     * 
     * @since 5.5.3
     */
    private boolean runningQuery;

    /**
     * The results of the last run query. Set to <code>null</code> when execute is called.
     * 
     * @since 5.5.3
     */
    private IResults resultsModel;

    public static WorkspaceExecutor getInstance() {
        return _INSTANCE;
    }

    /**
     * Cancels the currently running query. Does nothing if there is no running query.
     * 
     * @throws SQLException if there is a problem canceling
     * @since 5.5.3
     */
    public void cancel() throws SQLException {
        if ((this.stmt != null) && this.runningQuery) {
            this.stmt.cancel();
        }
    }

    /**
     * @return the results of the last successfully executed query or <code>null</code> if no query has been successfully executed
     *         or if there is a query currently executing
     * @since 5.5.3
     */
    public IResults getResults() {
        return this.resultsModel;
    }

    public void start() {
        if (!started) {
            try {
                // register workspaceinfo with the dqp and workspace will supply the rest.
                this.adminConnection = getAdminConnection();
                WorkspaceInfoHolder.setInfo(this.workspaceInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            this.started = true;
        }
    }

    public void stop() {
        if (started) {
            try {
                if (this.adminConnection != null) {
                    this.adminConnection.close();
                    this.adminConnection = null;
                }
            } catch (SQLException e) {
                UTIL.log(IStatus.ERROR, e, UTIL.getString(PREFIX + "errorStoppingDqp")); //$NON-NLS-1$
            }
            started = false;
        }
    }

    /**
     * @param qmi
     * @param sql
     * @param paramValues
     * @param displaySQL
     * @param maxrows
     * @param monitor the cancelable progress monitor
     * @throws SQLException
     * @see {@link #getResults()} for obtaining query results
     */
    public void executeSQL( QueryMetadataInterface qmi,
                            String sql,
                            Object[] paramValues,
                            String displaySQL,
                            int maxrows,
                            IProgressMonitor monitor ) throws SQLException {
        this.stmt = null;
        this.resultsModel = null;
        ResultSet resultset = null;

        try {
            // inject the metadata into the current dqp instance
            this.workspaceInfo.setMetadata(qmi);

            // now act normal in executing the query..
            stmt = this.adminConnection.prepareStatement(sql);
            if (paramValues != null && paramValues.length > 0) {
                int params = 1;
                for (int i = 0; i < paramValues.length; i++) {
                    // null values do not have to be bound to the parameter
                    if (paramValues[i] != null) {
                        this.stmt.setObject(params++, paramValues[i]);
                    }
                }
            }
            this.stmt.setMaxRows(maxrows);

            if (!monitor.isCanceled()) {
                this.runningQuery = true;
                resultset = this.stmt.executeQuery();
                this.runningQuery = false;

                if (!monitor.isCanceled()) {
                    if (isXmlResults(resultset)) {
                        this.resultsModel = new XmlDocumentResultsModel(displaySQL, resultset);
                    } else {
                        this.resultsModel = new SqlResultsModel(displaySQL, resultset);
                    }
                }
            }
        } finally {
            if (resultset != null) {
                resultset.close();
            }
            if (this.stmt != null) {
                this.stmt.close();
            }

            this.runningQuery = false;
        }
    }

    private boolean isXmlResults( ResultSet results ) throws SQLException {
        ResultSetMetaData metaData = results.getMetaData();

        if ((metaData.getColumnCount() == 1) && (metaData.getColumnTypeName(1).equals(DataTypeManager.DefaultDataTypes.XML))) {
            return true;
        }

        return false;
    }

    public boolean modelHasConnectorBinding( String modelName ) {
        return !this.workspaceInfo.getBinding(modelName).isEmpty();
    }

    static class WorkspaceInfoImpl implements WorkspaceInfo {
        private static final String XMI = ".xmi"; //$NON-NLS-1$
        Object metadata;

        public List<String> getBinding( String modelName ) {
            ArrayList<String> names = new ArrayList<String>();

            if (!modelName.endsWith(XMI)) {
                modelName = modelName + XMI;
            }
            Collection c = DqpPlugin.getInstance().getWorkspaceConfig().getBindingsForModel(modelName);
            for (Iterator i = c.iterator(); i.hasNext();) {
                Connector connector = (Connector)i.next();
                names.add(connector.getName());
            }
            return names;
        }

        public Object getMetadata() {
            return metadata;
        }

        public void setMetadata( Object obj ) {
            this.metadata = obj;
        }
    }
}
