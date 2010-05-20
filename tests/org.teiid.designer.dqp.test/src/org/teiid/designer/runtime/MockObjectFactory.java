/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 *
 */
public class MockObjectFactory {

    /**
     * Creates a mock <code>ConnectionFactory</code>. The name and the properties can be obtained. The connector type property
     * value can be obtained.
     * 
     * @param name the name of the connection factory
     * @param connectorTypeName the name of the connector type of this connection factory
     * @return the connection factory
     */
    public static Translator createTranslator( String name,
                                                             String connectorTypeName ) {
    	Translator connectionFactory = mock(Translator.class);
        Properties props = new Properties();
        props.setProperty(IConnectorProperties.CONNECTOR_TYPE, connectorTypeName);

        when(connectionFactory.getName()).thenReturn(name);
        when(connectionFactory.getProperties()).thenReturn(props);
        when(connectionFactory.getPropertyValue(IConnectorProperties.CONNECTOR_TYPE)).thenReturn(connectorTypeName);

        return connectionFactory;
    }

    /**
     * Creates a <code>Connector</code> using a mock <code>ConnectionFactory</code> and mock <code>ConnectorType</code>. The names
     * can be obtained from the connector and connector type. The connector type name can be obtained from the connector
     * properties.
     * 
     * @param name the name of the connector
     * @param connectorTypeName the name of the connector type
     * @return the connector
     * @since 7.0
     */
    public static Connector createConnector( String name,
                                             String connectorTypeName ) {
        ConnectorType connectorType = createConnectorType(connectorTypeName);
        Translator translator = createTranslator(name, connectorTypeName);

        return new Connector(translator, connectorType);
    }

    /**
     * Creates a mock <code>ConnectorType</code>. The name and admin can be obtained from the type. The server and Admin API can
     * be obtained from the admin.
     * 
     * @param name the name of the connector type
     * @return the connector type
     */
    public static ConnectorType createConnectorType( String name ) {
        return createConnectorType(name, createExecutionAdmin());
    }

    public static ConnectorType createConnectorType( String name,
                                                     ExecutionAdmin admin ) {
        Collection<PropertyDefinition> propDefs = new ArrayList<PropertyDefinition>();
        PropertyDefinition jndiProp = mock(PropertyDefinition.class);
        when(jndiProp.getName()).thenReturn(IConnectorProperties.JNDI_NAME);
        when(jndiProp.getPropertyValue(IConnectorProperties.JNDI_NAME)).thenReturn("jndiName");
        propDefs.add(jndiProp);

        return new ConnectorType(name, propDefs, admin);
    }

    public static ExecutionAdmin createExecutionAdmin() {
        Server server = mock(Server.class);
        Admin adminApi = mock(Admin.class);
        EventManager eventManager = mock(EventManager.class);
        ExecutionAdmin admin = mock(ExecutionAdmin.class);

        when(admin.getServer()).thenReturn(server);
        when(admin.getAdminApi()).thenReturn(adminApi);
        when(admin.getEventManager()).thenReturn(eventManager);

        return admin;
    }

    /**
     * Creates a mock <code>ModelResource</code>. The item name and parent can be obtained from the resource. The path can be
     * obtained from the parent.
     * 
     * @param name the name of the model
     * @param parentPath the model's parent path
     * @return the model resource
     */
    public static ModelResource createModelResource( String name,
                                                     String parentPath ) {
        ModelResource parent = mock(ModelResource.class);
        when(parent.getPath()).thenReturn(new Path(parentPath));

        ModelResource modelResource = mock(ModelResource.class);
        when(modelResource.getItemName()).thenReturn(name);
        when(modelResource.getParent()).thenReturn(parent);

        return modelResource;
    }

    /**
     * Mocks static Eclipse classes used when running Eclipse. Needs to be called from the @Before method of the test class.
     */
    public static void initializeStaticWorkspaceClasses() {
        // ResourcesPlugin
        mockStatic(ResourcesPlugin.class);
        IWorkspace workspace = mock(IWorkspace.class);
        when(ResourcesPlugin.getWorkspace()).thenReturn(workspace);

        // ModelWorkspaceManager
        mockStatic(ModelWorkspaceManager.class);
        ModelWorkspaceManager modelWorkspaceMgr = mock(ModelWorkspaceManager.class);
        when(ModelWorkspaceManager.getModelWorkspaceManager()).thenReturn(modelWorkspaceMgr);

        // ModelerCore
        mockStatic(ModelerCore.class);
        ModelerCore modelerCore = mock(ModelerCore.class);
        when(ModelerCore.getPlugin()).thenReturn(modelerCore);
    }

    /**
     * Prevent construction.
     */
    private MockObjectFactory() {
        // nothing to do
    }

}
