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
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.ConnectorBinding;
import org.teiid.adminapi.PropertyDefinition;
import com.metamatrix.core.event.IChangeListener;

/**
 *
 */
public final class ServerAdmin {

    private final Admin admin;

    private Map<String, Connector> connectorByNameMap;
    private Map<String, ConnectorType> connectorTypeByNameMap;

    public ServerAdmin( Admin admin ) throws Exception {
        this.admin = admin;
    }

    public void addConnector( String name,
                              String typeName,
                              Properties properties ) throws Exception {
        // TODO implement
        this.admin.addConnectorBinding(name, typeName, properties);
        // this.admin.getConnectorBinding(deployedName) != null
        // TODO send event ???
    }

    public void addListener( IChangeListener listener ) {
        // TODO implement
    }

    /**
     * @param name
     * @return
     * @throws Exception
     */
    public Connector getConnector( String name ) throws Exception {
        initialize();
        return this.connectorByNameMap.get(name);
    }

    public Collection<Connector> getConnectors() throws Exception {
        initialize();
        return this.connectorByNameMap.values();
    }

    public Collection<Connector> getConnectors( ConnectorType type ) throws Exception {
        initialize();
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
    public ConnectorType getConnectorType( String name ) throws Exception {
        initialize();
        return this.connectorTypeByNameMap.get(name);
    }

    public Collection<ConnectorType> getConnectorTypes() throws Exception {
        initialize();
        return this.connectorTypeByNameMap.values();
    }

    private void initialize() throws Exception {
        if (this.connectorTypeByNameMap == null) {
            refresh();
        }
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

    public void removeConnectorType() {
        // TODO implement
    }

    public void removeBinding() {
        // TODO implement
    }

    public void removeListener( IChangeListener listener ) {
        // TODO implement
    }
}
