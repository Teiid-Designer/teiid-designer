/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.connection;

import java.util.Properties;
import org.teiid.designer.datatools.connection.ConnectionInfoProviderFactory;
import org.teiid.designer.datatools.connection.IConnectionInfoHelper;
import org.teiid.designer.datatools.connection.IConnectionInfoProvider;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.vdb.connections.VdbSourceConnection;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * 
 */
public class ModelConnectionMapper {

    String modelName;
    Properties properties;

    private ModelResource modelResource;

    private IConnectionInfoProvider connectionInfoProvider;

    /**
     * ModelConnectionFactoryMapper
     * 
     * @param modelName
     * @param properties
     */
    public ModelConnectionMapper( String modelName,
                                  Properties properties ) {
        CoreArgCheck.isNotEmpty(modelName);
        CoreArgCheck.isNotEmpty(properties);
        this.modelName = modelName;
        this.properties = (Properties)properties.clone();
        String profileID = this.properties.getProperty(IConnectionInfoHelper.CONNECTION_PROFILE_NAMESPACE
                                                       + IConnectionInfoHelper.PROFILE_ID_KEY);
        connectionInfoProvider = new ConnectionInfoProviderFactory().getProviderFromProfileID(profileID);

    }

    public ModelConnectionMapper( String modelName,
                                  Properties properties,
                                  IConnectionInfoProvider connectionInfoProvider ) {
        this(connectionInfoProvider);
        CoreArgCheck.isNotEmpty(modelName);
        CoreArgCheck.isNotEmpty(properties);
        this.modelName = modelName;
        this.properties = (Properties)properties.clone();

    }

    public ModelConnectionMapper( ModelResource modelResource ) {
        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        this.modelResource = modelResource;
        try {
            connectionInfoProvider = new ConnectionInfoProviderFactory().getProvider(this.modelResource);
        } catch (Exception e) {
        }
    }

    public ModelConnectionMapper( ModelResource modelResource,
                                  IConnectionInfoProvider connectionInfoProvider ) {
        this(connectionInfoProvider);

        CoreArgCheck.isNotNull(modelResource, "modelResource"); //$NON-NLS-1$
        this.modelResource = modelResource;
    }

    private ModelConnectionMapper( IConnectionInfoProvider connectionInfoProvider ) {
        CoreArgCheck.isNotNull(connectionInfoProvider, "connectionInfoProvider"); //$NON-NLS-1$
        this.connectionInfoProvider = connectionInfoProvider;
    }

    /**
     * @return modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @return properties
     */
    public Properties getProperties() {
        return properties;
    }

    private IConnectionInfoProvider getConnectionInfoProvider() throws Exception {
        if (null == connectionInfoProvider) {
            if (modelResource != null) {
                connectionInfoProvider = new ConnectionInfoProviderFactory().getProvider(this.modelResource);
            } else if (properties != null) {
                connectionInfoProvider = new ConnectionInfoProviderFactory().getProvider(properties);
            } else {
                throw new Exception(DqpPlugin.Util.getString("ModelConnectionoMapper.cant.create.ConnectionInfoProvider")); //$NON-NLS-1$
            }
        }
        return connectionInfoProvider;
    }

    public VdbSourceConnection getVdbSourceConnection( ExecutionAdmin executionAdmin,
                                                       String workspaceUuid ) throws Exception {
        if (executionAdmin == null) {
            return null;
        }

        VdbSourceConnection sourceConnection = null;
        String translatorName = null;
        String jndiName = null;
        String dsTypeName = null;

        IConnectionInfoProvider provider = getConnectionInfoProvider();
        Properties sourceProps = provider.getConnectionProperties(modelResource);
        jndiName = provider.generateUniqueConnectionJndiName(modelResource, workspaceUuid);

        // Insure this name exists as data source on server
        dsTypeName = provider.findMatchingDataSourceTypeName(modelResource);
        executionAdmin.getOrCreateDataSource(modelResource.getItemName(), jndiName, dsTypeName, sourceProps);

        // Select a translator type;
        translatorName = provider.getTranslatorName(modelResource);

        sourceConnection = new VdbSourceConnection(modelName, translatorName, jndiName);
        sourceConnection = new VdbSourceConnection(modelName, translatorName, jndiName);
        return sourceConnection;
    }
}
