/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.Util;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.ConnectionFactory;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.VDB;
import org.teiid.designer.vdb.Vdb;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

/**
 *
 */
public class ExecutionAdmin {

    private final Admin admin;
    protected Map<String, Connector> connectorByNameMap;
    protected Map<String, ConnectorType> connectorTypeByNameMap;
    private final EventManager eventManager;
    private final Server server;
    private final SourceBindingsManager sourceBindingsMgr;
    private Set<VDB> vdbs;

    /**
     * @param admin the associated Teiid Admin API (never <code>null</code>)
     * @param server the server this admin belongs to (never <code>null</code>)
     * @param eventManager the event manager used to fire events (never <code>null</code>)
     * @throws Exception if there is a problem connecting the server
     */
    public ExecutionAdmin( Admin admin,
                           Server server,
                           EventManager eventManager ) throws Exception {
        CoreArgCheck.isNotNull(admin, "admin"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(server, "server"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(eventManager, "eventManager"); //$NON-NLS-1$

        this.admin = admin;
        this.eventManager = eventManager;
        this.server = server;
        this.sourceBindingsMgr = new SourceBindingsManager(this);

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
        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(type, "type"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(properties, "properties"); //$NON-NLS-1$

        this.admin.addConnectionFactory(name, type.getName(), properties); // TODO get server guys to return the binding
        // TODO ask server guys if type needs to also be in properties

        ConnectionFactory binding = this.admin.getConnectionFactory(name);
        Connector connector = new Connector(binding, type);
        this.connectorByNameMap.put(name, connector);

        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createAddConnectorEvent(connector));
    }

    /**
     * @param vdb
     * @return
     */
    public VDB deployVdb( Vdb vdb ) throws Exception {
        CoreArgCheck.isNotNull(vdb, "vdb"); //$NON-NLS-1$

        VDB deployedVdb = null; // this.admin.deployVDB(vdb.getName(), vdb);

        return deployedVdb;
    }

    /**
     * @param vdb
     * @return
     */
    public VDB deployVdb( IFile vdbFile ) throws Exception {
        CoreArgCheck.isNotNull(vdbFile, "vdbFile"); //$NON-NLS-1$

        VDB deployedVdb = null; // this.admin.deployVDB(vdb.getName(), vdb);

        return deployedVdb;
    }

    /**
     * @param proposedName the proposed name of the connector (must not be <code>null</code> or empty and it must contain all
     *        valid characters)
     * @return the unique connector name (maybe different than the proposed name if a connector of that name already exists)
     * @throws Exception if there is a problem obtaining connectors and connector types from the server or if name contains
     *         invalid characters
     * @see ModelerDqpUtils#isValidBindingName(String)
     */
    public String ensureUniqueConnectorName( String proposedName ) throws Exception {
        CoreArgCheck.isNotEmpty(proposedName, "proposedName"); //$NON-NLS-1$

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

    Admin getAdminApi() {
        return this.admin;
    }

    /**
     * @param name the connector name (never <code>null</code> or empty)
     * @return
     * @throws Exception
     */
    public Connector getConnector( String name ) {
        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        return this.connectorByNameMap.get(name);
    }

    public Collection<Connector> getConnectors() {
        return this.connectorByNameMap.values();
    }

    /**
     * @param type the <code>ConnectorType</code> whose connectors are being requested (never <code>null</code>)
     * @return the connectors (never <code>null</code> or empty)
     */
    public Collection<Connector> getConnectors( ConnectorType type ) {
        CoreArgCheck.isNotNull(type, "type"); //$NON-NLS-1$

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
        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$
        return this.connectorTypeByNameMap.get(name);
    }

    public Collection<ConnectorType> getConnectorTypes() {
        return this.connectorTypeByNameMap.values();
    }

    /**
     * @return the event manager (never <code>null</code>)
     */
    EventManager getEventManager() {
        return this.eventManager;
    }

    /**
     * @return the server who owns this admin object (never <code>null</code>)
     */
    public Server getServer() {
        return this.server;
    }

    /**
     * @return the source bindings manager (never <code>null</code>)
     */
    public SourceBindingsManager getSourceBindingsManager() {
        return this.sourceBindingsMgr;
    }

    /**
     * @return an unmodifiable set of VDBs deployed on the server
     */
    public Set<VDB> getVdbs() {
        return this.vdbs;
    }

    /**
     * @param name the name of the VDB being requested (never <code>null</code> or empty)
     * @return the VDB or <code>null</code> if not found
     */
    public VDB getVdb( String name ) {
        CoreArgCheck.isNotEmpty(name, "name"); //$NON-NLS-1$

        for (VDB vdb : this.vdbs) {
            if (vdb.getName().equals(name)) return vdb;
        }

        return null;
    }

    public void refresh() throws Exception {
        this.connectorByNameMap = new HashMap<String, Connector>();
        this.connectorTypeByNameMap = new HashMap<String, ConnectorType>();

        // populate connector type map
        refreshConnectorTypes(this.admin.getConnectorNames());

        // populate connector map
        refreshConnectors(this.admin.getConnectionFactories());

        // populate VDBs and source bindings
        // TODO may need to filter out hidden vdb
        this.vdbs = Collections.unmodifiableSet(this.admin.getVDBs());
        refreshSourceBindings();
    }

    protected void refreshConnectors( Collection<ConnectionFactory> connectorBindings ) {
        for (ConnectionFactory binding : connectorBindings) {
            ConnectorType type = getConnectorType(binding.getPropertyValue(IConnectorProperties.CONNECTOR_TYPE));
            this.connectorByNameMap.put(binding.getName(), new Connector(binding, type));
        }
    }

    protected void refreshConnectorTypes( Set<String> connectorTypeNames ) throws Exception {
        for (String connectorTypeName : connectorTypeNames) {
            Collection<PropertyDefinition> propDefs = this.admin.getConnectorPropertyDefinitions(connectorTypeName);
            ConnectorType connectorType = new ConnectorType(connectorTypeName, propDefs, this);
            this.connectorTypeByNameMap.put(connectorTypeName, connectorType);
        }
    }

    protected void refreshSourceBindings() throws Exception {
        this.sourceBindingsMgr.refresh();
    }

    public void removeConnector( Connector connector ) throws Exception {
        CoreArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$
        this.admin.deleteConnectionFactory(connector.getName());
        this.connectorByNameMap.remove(connector.getName());
        this.eventManager.notifyListeners(ExecutionConfigurationEvent.createRemoveConnectorEvent(connector));
    }

    public Exception validateConnectorName( String name ) {
        if (name == null) {
            return new Exception(Util.getString("connectorNameCannotBeNull", name)); //$NON-NLS-1$
        }

        if (name.length() == 0) {
            return new Exception(Util.getString("connectorNameCannotBeEmpty", name)); //$NON-NLS-1$
        }

        // TODO is there other name validation needed (number of chars, chars allowed, ...)
        if (this.connectorByNameMap.containsKey(name)) {
            return new Exception(Util.getString("connectorNameAlreadyExists", name)); //$NON-NLS-1$
        }

        return null;
    }

    /**
     * @param connector the connector whose property is being changed (never <code>null</code>)
     * @param propName the name of the property being changed (never <code>null</code> or empty)
     * @param value the new value
     * @throws Exception if there is a problem setting the property
     * @since 7.0
     */
    public void setPropertyValue( Connector connector,
                                  String propName,
                                  String value ) throws Exception {
        CoreArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(propName, "propName"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(value, "value"); //$NON-NLS-1$
        internalSetPropertyValue(connector, propName, value, true);
    }

    private void internalSetPropertyValue( Connector connector,
                                           String propName,
                                           String value,
                                           boolean notify ) throws Exception {
        if (connector.isValidPropertyValue(propName, value)) {
            String oldValue = connector.getPropertyValue(propName);

            // don't set if value has not changed
            if (oldValue == null) {
                if (value == null) return;
            } else if (oldValue.equals(value)) return;

            // set value
            this.admin.setConnectionFactoryProperty(connector.getName(), propName, value);

            if (notify) {
                this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(connector));
            }
        } else {
            throw new Exception(Util.getString("ExecutionAdmin.invalidPropertyValue", value, propName)); //$NON-NLS-1$
        }
    }

    /**
     * @param connector the connector whose properties are being changed (never <code>null</code>)
     * @param changedProperties a collection of properties that have changed (never <code>null</code> or empty)
     * @throws Exception if there is a problem changing the properties
     * @since 7.0
     */
    public void setProperties( Connector connector,
                               Properties changedProperties ) throws Exception {
        CoreArgCheck.isNotNull(connector, "connector"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(changedProperties, "changedProperties"); //$NON-NLS-1$
        CoreArgCheck.isNotEmpty(changedProperties.entrySet(), "changedProperties"); //$NON-NLS-1$

        if (changedProperties.size() == 1) {
            String name = changedProperties.stringPropertyNames().iterator().next();
            setPropertyValue(connector, name, changedProperties.getProperty(name));
        } else {
            // TODO stop connector??

            for (String name : changedProperties.stringPropertyNames()) {
                internalSetPropertyValue(connector, name, changedProperties.getProperty(name), false);
            }

            // TODO restart connector??

            this.eventManager.notifyListeners(ExecutionConfigurationEvent.createUpdateConnectorEvent(connector));
        }
    }

}
