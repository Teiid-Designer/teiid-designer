/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.adminapi.PropertyDefinition;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

/**
 *
 */
public final class ExecutionAdmin {

    private final Admin admin;
    private Map<String, Connector> connectorByNameMap;
    private Map<String, ConnectorType> connectorTypeByNameMap;
    private final EventManager eventManager;
    private final Server server;

    public ExecutionAdmin( Admin admin,
                           Server server,
                           EventManager eventManager ) throws Exception {
        this.admin = admin;
        this.eventManager = eventManager;
        this.server = server;

        refresh();
    }

    /**
     * @param name
     * @param type
     * @param properties validated, complete connector properties
     * @throws Exception
     * @since 0.6
     * @see #validateConnectorProperties(Properties)
     */
    public void addConnector( String name,
                              ConnectorType type,
                              Properties properties ) throws Exception {
        ArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        ArgCheck.isNotNull(type, "type"); //$NON-NLS-1$
        ArgCheck.isNotNull(properties, "properties"); //$NON-NLS-1$

        this.admin.addConnectorBinding(name, type.getName(), properties); // TODO get server guys to return the binding
        // TODO ask server guys if type needs to also be in properties

        ConnectorBinding binding = this.admin.getConnectorBinding(name);
        Connector connector = new Connector(binding, type);
        this.connectorByNameMap.put(name, connector);

        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createAddConnectorEvent(connector));
    }

    /**
     * @param vdbName the VDB name
     * @param vdbVersion the version of the VDB
     * @param modelName the name of the model
     * @param connectorBindingName the name of the Connector
     * @throws Exception
     */
    public void assignBindingToModel( String vdbName,
                                      String vdbVersion,
                                      String modelName,
                                      String connectorBindingName ) throws Exception {
        this.admin.assignBindingToModel(vdbName, vdbVersion, modelName, connectorBindingName);
    }

    /**
     * @param vdbName
     * @param vdbVersion
     * @param modelName
     * @param connectorBindingNames
     * @throws Exception
     */
    public void assignBindingsToModel( String vdbName,
                                       String vdbVersion,
                                       String modelName,
                                       String[] connectorBindingNames ) throws Exception {
        this.admin.assignBindingsToModel(connectorBindingNames, vdbName, vdbVersion, modelName);
    }

    /**
     * @param proposedName the proposed name of the connector (must not be <code>null</code> and contain all valid characters)
     * @return the unique connector name (maybe different than the proposed name if a connector of that name already exists)
     * @throws Exception if there is a problem obtaining connectors and connector types from the server or if name contains
     *         invalid characters
     * @see ModelerDqpUtils#isValidBindingName(String)
     */
    public String ensureUniqueConnectorName( String proposedName ) throws Exception {
        String result = proposedName;
        boolean validName = false;
        int suffix = 1;

        while (!validName) {
            IStatus status = (ModelerDqpUtils.isValidBindingName(result));

            if (!status.isOK()) {
                throw new IllegalArgumentException(status.getMessage());
            }

            if ((getConnector(result) != null)) {
                result = proposedName + "_" + suffix; //$NON-NLS-1$
                ++suffix;
            } else {
                validName = true;
            }
        }

        return result;
    }

    /**
     * @param name
     * @return
     * @throws Exception
     */
    public Connector getConnector( String name ) {
        return this.connectorByNameMap.get(name);
    }

    public Collection<Connector> getConnectors() {
        return this.connectorByNameMap.values();
    }

    public Collection<Connector> getConnectors( ConnectorType type ) {
        List<Connector> connectors = new ArrayList<Connector>();
        for (Connector connector : connectorByNameMap.values()) {
            if (connector.getType() == type) connectors.add(connector);
        }
        return connectors;
    }

    /**
     * @param name
     * @return
     * @throws Exception
     */
    public ConnectorType getConnectorType( String name ) {
        ArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        return this.connectorTypeByNameMap.get(name);
    }

    public Collection<ConnectorType> getConnectorTypes() {
        return this.connectorTypeByNameMap.values();
    }

    /**
     * @return the server who owns this admin object
     */
    public Server getServer() {
        return this.server;
    }

    public void refresh() throws Exception {
        this.connectorByNameMap = new HashMap<String, Connector>();
        this.connectorTypeByNameMap = new HashMap<String, ConnectorType>();

        // populate connector type map
        for (String connectorTypeName : this.admin.getConnectorTypes()) {
            Collection<PropertyDefinition> propDefs = this.admin.getConnectorTypePropertyDefinitions(connectorTypeName);
            ConnectorType connectorType = new ConnectorType(connectorTypeName, propDefs, this);
            this.connectorTypeByNameMap.put(connectorTypeName, connectorType);
        }

        // populate connector map
        for (ConnectorBinding binding : this.admin.getConnectorBindings()) {
            ConnectorType type = getConnectorType(binding.getPropertyValue(IConnectorProperties.CONNECTOR_TYPE));
            this.connectorByNameMap.put(binding.getName(), new Connector(binding, type));
        }
    }

    public void removeConnector( Connector connector ) throws Exception {
        this.admin.deleteConnectorBinding(connector.getName());
        this.connectorByNameMap.remove(connector.getName());
        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createRemoveConnectorEvent(connector));
    }

    public Exception validateConnectorName( String name ) {
        // TODO is there other name validation needed (number of chars, chars allowed, ...)
        if (this.connectorByNameMap.containsKey(name)) {
            return new Exception("connectorNameAlreadyExists"); // TODO i18n this
        }

        return null;
    }

    public Exception validateConnectorProperty( PropertyDefinition propDef,
                                                String value ) {
        // TODO implement
        return null;
    }

    public void setPropertyValue( Connector connector,
                                  String propName,
                                  String value ) throws Exception {
        String oldValue = connector.getPropertyValue(propName);

        if (oldValue == null) {
            if (value == null) return;
        } else if (oldValue.equals(value)) return;

        this.admin.setConnectorBindingProperty(connector.getName(), propName, value);
        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(connector));
    }

}
