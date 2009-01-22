/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.ui.workspace;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import com.metamatrix.admin.api.embedded.EmbeddedAdmin;
import com.metamatrix.admin.api.exception.AdminException;
import com.metamatrix.admin.api.objects.AdminObject;
import com.metamatrix.admin.api.objects.AdminOptions;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.xml.XMLConfigurationImportExportUtility;
import com.metamatrix.common.types.DataTypeManager;
import com.metamatrix.core.util.FileUtil;
import com.metamatrix.embeddedquery.workspace.WorkspaceInfo;
import com.metamatrix.embeddedquery.workspace.WorkspaceInfoHolder;
import com.metamatrix.jdbc.api.Connection;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.config.ConfigurationChangeEvent;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.config.ExtensionModuleChangeEvent;
import com.metamatrix.modeler.dqp.config.IConfigurationChangeListener;
import com.metamatrix.modeler.dqp.config.IExtensionModuleChangeListener;
import com.metamatrix.modeler.internal.core.index.ModelResourceIndexSelector;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.IResults;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.SqlResultsModel;
import com.metamatrix.modeler.internal.dqp.ui.jdbc.XmlDocumentResultsModel;
import com.metamatrix.modeler.transformation.metadata.QueryMetadataContext;
import com.metamatrix.modeler.transformation.metadata.TransformationMetadataFactory;
import com.metamatrix.modeler.transformation.udf.UdfManager;
import com.metamatrix.modeler.transformation.udf.UdfModelEvent;
import com.metamatrix.modeler.transformation.udf.UdfModelListener;
import com.metamatrix.modeler.transformation.ui.udf.UdfWorkspaceManager;
import com.metamatrix.query.metadata.QueryMetadataInterface;

/**
 * This class used as mediator to run/execute any workspace execution related tasks.
 */
public class WorkspaceExecutor extends QueryClient {

    static final String DEBUG_CONTEXT = WorkspaceExecutor.class.getSimpleName();
    // private static final String PREFIX = I18nUtil.getPropertyPrefix(WorkspaceExecutor.class);
    private static WorkspaceExecutor _INSTANCE = new WorkspaceExecutor();

    boolean started;
    private WorkspaceInfoImpl workspaceInfo = new WorkspaceInfoImpl();
    Connection adminConnection;

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

                ConfigurationManager config = DqpPlugin.getInstance().getConfigurationManager();
                Collection<ConnectorBinding> bindings = config.getConnectorBindings();
                startConnnectorBindings(bindings);

                // add a change listner with configuration as bindings are added or removed keep them in sync
                DqpPlugin.getInstance().getConfigurationManager().addConfigurationChangeListener(new BindingListener());

                UDFListener udfListener = new UDFListener();
                DqpPlugin.getInstance().getExtensionsHandler().addChangeListener(udfListener);
                UdfManager.INSTANCE.addChangeListener(udfListener);
            } catch (SQLException e) {
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
                DqpPlugin.Util.log(IStatus.ERROR, e, "Failed to properly shutdown the Workspace Execution"); //$NON-NLS-1$
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
            if (DqpPlugin.Util.isDebugEnabled(DEBUG_CONTEXT)) {
                DqpPlugin.Util.debug(DEBUG_CONTEXT, "executing the query = " + sql); //$NON-NLS-1$
            }

            // inject the metadata into the current dqp instance
            this.workspaceInfo.setMetadata(qmi);

            // now act normal in executing the query..
            stmt = this.adminConnection.prepareStatement(sql);
            if (paramValues != null && paramValues.length > 0) {
                int params = 1;
                for (int i = 0; i < paramValues.length; i++) {
                    this.stmt.setObject(params++, paramValues[i]);
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

    private void startConnnectorBindings( Collection<ConnectorBinding> bindings ) throws SQLException {
        ConnectorBinding binding = null;
        if (bindings != null && !bindings.isEmpty()) {
            for (Iterator<ConnectorBinding> i = bindings.iterator(); i.hasNext();) {
                binding = i.next();
                startConnectorBinding(binding);
            }
        }
    }

    /**
     * Start the given connector binding
     */
    private boolean startConnectorBinding( ConnectorBinding binding ) throws SQLException {
        EmbeddedAdmin admin = (EmbeddedAdmin)this.adminConnection.getAdminAPI();

        boolean started = false;
        try {
            com.metamatrix.admin.api.objects.ConnectorBinding existing = findDeployedBinding(admin, binding.getFullName());
            if (existing != null) {
                admin.startConnectorBinding(existing.getIdentifier());
                started = true;
                if (DqpPlugin.Util.isDebugEnabled(DEBUG_CONTEXT)) {
                    DqpPlugin.Util.debug(DEBUG_CONTEXT, "Started the connector binding = " + existing.getName()); //$NON-NLS-1$
                }

            }
        } catch (AdminException e) {
            DqpPlugin.Util.log(IStatus.ERROR, e, "Error loading the connector binding = " + binding.getFullName()); //$NON-NLS-1$
        }
        return started;
    }

    private com.metamatrix.admin.api.objects.ConnectorBinding findDeployedBinding( EmbeddedAdmin admin,
                                                                                   String bindingName ) throws AdminException {
        Collection deployedBindings = admin.getConnectorBindings(AdminObject.WILDCARD);
        if (deployedBindings != null && !deployedBindings.isEmpty()) {
            for (Iterator i = deployedBindings.iterator(); i.hasNext();) {
                com.metamatrix.admin.api.objects.ConnectorBinding existing = (com.metamatrix.admin.api.objects.ConnectorBinding)i.next();
                if (existing.getName().equals(bindingName)) {
                    return existing;
                }
            }
        }
        return null;
    }

    private com.metamatrix.admin.api.objects.ConnectorType findDeployedConnectorType( EmbeddedAdmin admin,
                                                                                      String typeName ) throws AdminException {
        Collection deployedTypes = admin.getConnectorTypes(AdminObject.WILDCARD);
        if (deployedTypes != null && !deployedTypes.isEmpty()) {
            for (Iterator i = deployedTypes.iterator(); i.hasNext();) {
                com.metamatrix.admin.api.objects.ConnectorType existing = (com.metamatrix.admin.api.objects.ConnectorType)i.next();
                if (existing.getName().equals(typeName)) {
                    return existing;
                }
            }
        }
        return null;
    }

    void removeConnectorBinding( String bindingName ) {
        // if we already have this binding then remove it first.
        try {
            EmbeddedAdmin admin = (EmbeddedAdmin)this.adminConnection.getAdminAPI();
            com.metamatrix.admin.api.objects.ConnectorBinding existing = findDeployedBinding(admin, bindingName);
            if (existing != null) {
                admin.stopConnectorBinding(existing.getIdentifier(), true);
                admin.deleteConnectorBinding(existing.getIdentifier());
                if (DqpPlugin.Util.isDebugEnabled(DEBUG_CONTEXT)) {
                    DqpPlugin.Util.debug(DEBUG_CONTEXT, "stoped and removed connector binding = " + bindingName); //$NON-NLS-1$
                }
            }
        } catch (AdminException e) {
            DqpPlugin.Util.log(IStatus.ERROR, e, "Error removing the connector binding = " + bindingName); //$NON-NLS-1$
        } catch (SQLException e) {
            DqpPlugin.Util.log(IStatus.ERROR, e, "Error removing the connector binding = " + bindingName); //$NON-NLS-1$
        }
    }

    void removeConnectorType( String typeName ) {
        // if we already have this binding then remove it first.
        try {
            EmbeddedAdmin admin = (EmbeddedAdmin)this.adminConnection.getAdminAPI();
            com.metamatrix.admin.api.objects.ConnectorType existing = findDeployedConnectorType(admin, typeName);
            if (existing != null) {
                admin.deleteConnectorType(existing.getIdentifier());
                if (DqpPlugin.Util.isDebugEnabled(DEBUG_CONTEXT)) {
                    DqpPlugin.Util.debug(DEBUG_CONTEXT, "stoped and removed connector type = " + typeName); //$NON-NLS-1$
                }
            }
        } catch (AdminException e) {
            DqpPlugin.Util.log(IStatus.ERROR, e, "Error removing the connector type = " + typeName); //$NON-NLS-1$
        } catch (SQLException e) {
            DqpPlugin.Util.log(IStatus.ERROR, e, "Error removing the connector type = " + typeName); //$NON-NLS-1$
        }
    }

    /**
     * Look up and add and start the given connector binding
     */
    void addConnectorBinding( ConnectorBinding binding ) throws AdminException, SQLException {
        EmbeddedAdmin admin = (EmbeddedAdmin)this.adminConnection.getAdminAPI();

        // if we already have this binding then remove it first.
        removeConnectorBinding(binding.getFullName());

        // if not started means that we did not find the binding in the configuration,
        // so let's add and then start it
        // add and start the new one.
        com.metamatrix.admin.api.objects.ConnectorBinding added = admin.addConnectorBinding(binding.getFullName(),
                                                                                            binding.getComponentTypeID().getName(),
                                                                                            binding.getProperties(),
                                                                                            new AdminOptions(
                                                                                                             AdminOptions.OnConflict.OVERWRITE
                                                                                                             | AdminOptions.BINDINGS_IGNORE_DECRYPT_ERROR));
        admin.startConnectorBinding(added.getIdentifier());
        if (DqpPlugin.Util.isDebugEnabled(DEBUG_CONTEXT)) {
            DqpPlugin.Util.debug(DEBUG_CONTEXT, "Added and Started connector binding = " + added.getName()); //$NON-NLS-1$
        }
    }

    /**
     * Look up and add and start the given connector binding
     */
    void addConnectorType( ConnectorBindingType type ) throws AdminException, SQLException {
        EmbeddedAdmin admin = (EmbeddedAdmin)this.adminConnection.getAdminAPI();

        // if we already have this binding then remove it first.
        removeConnectorType(type.getFullName());

        try {
            XMLConfigurationImportExportUtility exporter = new XMLConfigurationImportExportUtility();
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            exporter.exportComponentType(bos, type, null);

            char[] typeContents = new String(bos.toByteArray()).toCharArray();

            // if not started means that we did not find the binding in the configuration,
            // so let's add and then start it
            // add and start the new one.
            admin.addConnectorType(type.getFullName(), typeContents);
            if (DqpPlugin.Util.isDebugEnabled(DEBUG_CONTEXT)) {
                DqpPlugin.Util.debug(DEBUG_CONTEXT, "Added connector type = " + type.getFullName()); //$NON-NLS-1$
            }
        } catch (IOException e) {
            DqpPlugin.Util.log(IStatus.ERROR, e, "Failed to add the connector type = " + type.getFullName()); //$NON-NLS-1$
        }
    }

    public boolean modelHasConnectorBinding( String modelName ) {
        return !this.workspaceInfo.getBinding(modelName).isEmpty();
    }

    public static QueryMetadataInterface getMetadata( org.eclipse.emf.ecore.resource.Resource resource ) {
        TransformationMetadataFactory factory = TransformationMetadataFactory.getInstance();
        Container container = null;

        // create a indexSelector for this resource and instantiate transformation validator
        ModelResourceIndexSelector selector = new ModelResourceIndexSelector(resource);
        QueryMetadataContext queryContext = new QueryMetadataContext(selector);
        queryContext.setRestrictedSearch(false);
        try {
            // queryContext.setContainer(ModelerCore.getModelContainer());
            container = ModelerCore.getModelContainer();
            ModelWorkspace workspace = ModelerCore.getModelWorkspace();
            queryContext.setResources(Arrays.asList(workspace.getEmfResources()));
        } catch (CoreException e) {
            DqpPlugin.Util.log(IStatus.ERROR, e, "Failed to get Metadata from transformation metadata"); //$NON-NLS-1$
            return null;
        }
        return factory.getModelerMetadata(queryContext, container);
    }

    final class UDFListener implements IExtensionModuleChangeListener, UdfModelListener {
        /**
         * @see com.metamatrix.modeler.dqp.config.IExtensionModuleChangeListener#extensionModulesChanged(com.metamatrix.modeler.dqp.config.ExtensionModuleChangeEvent)
         * @since 5.5.3
         */
        public void extensionModulesChanged( ExtensionModuleChangeEvent event ) {
            if (event.udfModulesAdded() || event.udfModulesDeleted()) {
                udfChanged();
            }
        }

        private void udfChanged() {
            IFile udfModel = UdfWorkspaceManager.getUdfModel(false);

            try {
                File file = udfModel.getLocation().toFile();

                if (file.exists()) {
                    EmbeddedAdmin admin = (EmbeddedAdmin)adminConnection.getAdminAPI();
                    FileUtil f = new FileUtil(file);
                    admin.addUDF(f.readBytes(), buildUDFClasspath());
                    if (DqpPlugin.Util.isDebugEnabled(DEBUG_CONTEXT)) {
                        DqpPlugin.Util.debug(DEBUG_CONTEXT, "updated the FunctionDefinitions.xmi in dqp"); //$NON-NLS-1$
                    }
                } else {
                    // delete
                    EmbeddedAdmin admin = (EmbeddedAdmin)adminConnection.getAdminAPI();
                    admin.deleteUDF();
                    if (DqpPlugin.Util.isDebugEnabled(DEBUG_CONTEXT)) {
                        DqpPlugin.Util.debug(DEBUG_CONTEXT, "deleted the FunctionDefinitions.xmi in dqp"); //$NON-NLS-1$
                    }
                }
            } catch (SQLException e) {
                DqpPlugin.Util.log(IStatus.ERROR, e, "Error during the synchronization of UDF"); //$NON-NLS-1$
            } catch (AdminException e) {
                DqpPlugin.Util.log(IStatus.ERROR, e, "Error during the synchronization of UDF"); //$NON-NLS-1$
            }
        }

        private String buildUDFClasspath() {
            // Get UDF jar files from extension hancler
            List<File> udfJarFiles = DqpPlugin.getInstance().getExtensionsHandler().getUdfJarFiles();

            StringBuffer udfClasspath = new StringBuffer();
            for (File udfJar : udfJarFiles) {
                udfClasspath.append("extensionjar:").append(udfJar.getName()).append(";"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return udfClasspath.toString();
        }

        @Override
        public void processEvent( UdfModelEvent event ) {
            udfChanged();
        }
    }

    final class BindingListener implements IConfigurationChangeListener {

        public void stateChanged( ConfigurationChangeEvent event ) throws Exception {
            ConfigurationManager config = DqpPlugin.getInstance().getConfigurationManager();

            // we only care about bindings - so do not edit types
            if (event.isConnectorBindingEvent()) {
                if (event.isMultipleUpdate()) {
                    String[] bindings = event.getObjectNames();
                    for (int i = 0; i < bindings.length; i++) {
                        handleConnectorBindingEvent(event, config, bindings[i]);
                    }
                } else {
                    handleConnectorBindingEvent(event, config, event.getObjectName());
                }
            }

            if (event.isConnectorTypeEvent()) {
                if (event.isMultipleUpdate()) {
                    String[] types = event.getObjectNames();
                    for (int i = 0; i < types.length; i++) {
                        handleConnectorTypeEvent(event, config, types[i]);
                    }
                } else {
                    handleConnectorTypeEvent(event, config, event.getObjectName());
                }
            }
        }

        private void handleConnectorBindingEvent( ConfigurationChangeEvent event,
                                                  ConfigurationManager config,
                                                  String bindingName ) throws AdminException, SQLException {
            if (event.isAdded()) {
                ConnectorBinding binding = config.getBinding(bindingName);
                addConnectorBinding(binding);
            } else if (event.isRemoved()) {
                removeConnectorBinding(bindingName);
            } else if (event.isChanged() || event.isReplaced()) {
                ConnectorBinding binding = config.getBinding(bindingName);
                addConnectorBinding(binding);
            }
        }

        private void handleConnectorTypeEvent( ConfigurationChangeEvent event,
                                               ConfigurationManager config,
                                               String typeName ) throws AdminException, SQLException {
            if (event.isAdded()) {
                ConnectorBindingType type = (ConnectorBindingType)config.getComponentType(typeName);
                addConnectorType(type);
            } else if (event.isRemoved()) {
                removeConnectorType(typeName);
            } else if (event.isChanged() || event.isReplaced()) {
                ConnectorBindingType type = (ConnectorBindingType)config.getComponentType(typeName);
                addConnectorType(type);
            }
        }
    }

    static class WorkspaceInfoImpl implements WorkspaceInfo {
        private static final String XMI = ".xmi"; //$NON-NLS-1$
        Object metadata;

        public List<String> getBinding( String modelName ) {
            ArrayList<String> names = new ArrayList<String>();

            if (!modelName.endsWith(XMI)) {
                modelName = modelName + XMI;
            }
            Collection c = DqpPlugin.getWorkspaceConfig().getBindingsForModel(modelName);
            for (Iterator i = c.iterator(); i.hasNext();) {
                ConnectorBinding binding = (ConnectorBinding)i.next();
                names.add(binding.getFullName());
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
