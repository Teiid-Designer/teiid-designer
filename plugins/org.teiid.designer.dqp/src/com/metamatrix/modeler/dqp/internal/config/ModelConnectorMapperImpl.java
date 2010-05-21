/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.ConnectorType;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.vdb.Vdb;
import org.teiid.designer.vdb.VdbModelEntry;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.config.ModelConnectorMapper;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

/**
 * The purpose of this class is to provide Connector to VDB source model binding logic.
 */
public class ModelConnectorMapperImpl implements ModelConnectorMapper {

    private final Vdb vdb;

    private Map<VdbModelEntry, Collection<Connector>> modelConnectorMatches;

    private Map<VdbModelEntry, Collection<ConnectorType>> modelConnectorTypeMatches;

    private Map<VdbModelEntry, Connector> modelConnectors;

    private ExecutionAdmin executionAdmin;

    // Used to enable Unit Testing.
    public static boolean HEADLESS = false;

    /**
     * ModelConnectorBindingMapperImpl constructor.
     * 
     * @param vdb The VDB; cannot be null.
     */
    public ModelConnectorMapperImpl( final Vdb vdb ) throws Exception {
        CoreArgCheck.isNotNull(vdb);
        this.vdb = vdb;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorMapper#createConnectionFactory(org.teiid.designer.vdb.VdbModelEntry,
     *      org.teiid.designer.runtime.ConnectorType, java.lang.String)
     */
    @Override
    public Connector createConnector( VdbModelEntry modelEntry,
                                      ConnectorType ConnectorType,
                                      String theName ) throws Exception {
        // TODO: Implement!!!!!!!!!!!
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorMapper#findAllConnectorMatches()
     */
    public Map<VdbModelEntry, Collection<Connector>> findAllConnectorMatches() {
        if (this.modelConnectorMatches == null) updateConnectorMatches();
        return this.modelConnectorMatches;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorMapper#findConnectorBindingMatch(com.metamatrix.vdb.edit.manifest.ModelReference)
     */
    public Collection<Connector> findConnectorMatches( final VdbModelEntry modelEntry ) {
        if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) {
            return Collections.EMPTY_LIST;
        }
        final Map connectorMap = findAllConnectorMatches();
        return (Collection)connectorMap.get(modelEntry);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorMapper#findConnectorTypeMatches(com.metamatrix.vdb.edit.manifest.ModelReference)
     */
    public Collection<ConnectorType> findConnectorTypeMatches( final VdbModelEntry modelEntry ) {
        if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) return Collections.EMPTY_LIST;
        // update matches
        try {
            updateConnectorTypeMatches();
        } catch (Exception e) {
            DqpPlugin.Util.log(e);
        }
        final Collection<ConnectorType> matches = this.modelConnectorTypeMatches.get(modelEntry);
        if (matches != null) return matches;
        return Collections.EMPTY_LIST;
    }

    /**
     * Find a connector whose properties match the JDBC connection properties stored on the model reference.
     * 
     * @param modelEntry The model reference
     * @return collection of Matching connector
     */
    private Collection<Connector> findMatchingConnectors( final VdbModelEntry modelEntry ) {
        if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) return Collections.EMPTY_LIST;
        final Map jdbcProperties = ModelerDqpUtils.getModelJdbcProperties(modelEntry);
        if (!jdbcProperties.isEmpty()) {

            try {
                // First check bindings in the VDB
                final Collection availableConnectors = DqpPlugin.getInstance().getServerManager().getDefaultServer().getAdmin().getConnectors();

                Collection bindingMatches = getMatchingConnectors(jdbcProperties, availableConnectors);

                return bindingMatches;
            } catch (Exception e) {
                DqpPlugin.Util.log(e);
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * Find a connector types whose properties match the JDBC connection properties stored on the model entry.
     * 
     * @param modelEntry The model reference
     * @return Matching connector types
     * @throws Exception
     */
    private Collection<ConnectorType> findMatchingConnectorType( final VdbModelEntry modelEntry ) throws Exception {
        if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) {
            return Collections.EMPTY_LIST;
        }
        final Map<String, String> jdbcProperties = ModelerDqpUtils.getModelJdbcProperties(modelEntry);
        final Collection connTypes = new ArrayList();
        if (!jdbcProperties.isEmpty()) {
            // get all the available connector
            // for each type, get properties
            for (ConnectorType connectorType : getExecutionAdmin().getConnectorTypes()) {
                final Properties connectorTypeProps = connectorType.getDefaultPropertyValues();
                final String driverClassName = connectorTypeProps.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS);
                if (CoreStringUtil.isEmpty(driverClassName)) {
                    continue;
                }

                final String jdbcClassName = jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_DRIVER_CLASS);
                if (CoreStringUtil.isEmpty(jdbcClassName) || !driverClassName.equalsIgnoreCase(jdbcClassName)) {
                    continue;
                }

                connTypes.add(connectorType);
            }
        }
        return connTypes;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorMapper#getAllConnectors()
     */
    public Map getAllConnectors() {
        if (this.modelConnectors == null) {
            // TODO: Load all ConnectionFactory's bound to source models??
        }
        return this.modelConnectors;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorMapper#getConnector(com.metamatrix.vdb.edit.manifest.ModelReference)
     */
    public Connector getConnector( final VdbModelEntry modelEntry ) {
        if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) return null;
        final Map<VdbModelEntry, Connector> connectorMap = getAllConnectors();
        return connectorMap.get(modelEntry);
    }

    /**
     * Get the configuration manager stored on the mapper or lookup the default manager from the plug-in.
     * 
     * @return The configuration manager
     * @throws Exception
     */
    private ExecutionAdmin getExecutionAdmin() throws Exception {
        return (this.executionAdmin != null) ? this.executionAdmin : DqpPlugin.getInstance().getServerManager().getDefaultServer().getAdmin();
    }

    /**
     * Given the JDBC properties and a list of available bindings, find all the matching bindings
     * 
     * @param jdbcProperties the JDBC properties
     * @param bindings the list of available bindings
     * @return the list of matching bindings
     */
    private Collection<Connector> getMatchingConnectors( final Map<String, String> jdbcProperties,
                                                         final Collection<Connector> connectors ) {
        // matching bindings
        final Collection connectorMatches = new ArrayList();
        // for each binding get properties
        for (Connector connector : connectors) {

            final ConnectorType type = connector.getType();

            String driverClassName = connector.getPropertyValue(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS);

            if (driverClassName == null) {
                // if no value set see if the type has a default value
                final PropertyDefinition defn = type.getPropertyDefinition(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS);

                if ((defn != null) && defn.getDefaultValue() != null) driverClassName = defn.getDefaultValue().toString();
            }

            String url = connector.getPropertyValue(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL);

            if (url == null) {
                // if no value set see if the type has a default value
                final PropertyDefinition defn = type.getPropertyDefinition(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL);

                if ((defn != null) && defn.getDefaultValue() != null) url = defn.getDefaultValue().toString();
            }

            String user = connector.getPropertyValue(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER);

            if (user == null) {
                // if no value set see if the type has a default value
                final PropertyDefinition defn = type.getPropertyDefinition(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER);

                if ((defn != null) && defn.getDefaultValue() != null) user = defn.getDefaultValue().toString();
            }

            if (CoreStringUtil.isEmpty(driverClassName) || CoreStringUtil.isEmpty(url)) {
                continue;
            }
            final String jdbcClassName = jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_DRIVER_CLASS);
            if (CoreStringUtil.isEmpty(jdbcClassName) || !driverClassName.equals(jdbcClassName)) {
                continue;
            }

            final String jdbcUrl = jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_URL);
            if (CoreStringUtil.isEmpty(jdbcUrl) || !url.equalsIgnoreCase(jdbcUrl)) {
                continue;
            }

            final String userName = jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_USERNAME);
            if ((CoreStringUtil.isEmpty(userName) && !CoreStringUtil.isEmpty(user))
                || (!CoreStringUtil.isEmpty(userName) && CoreStringUtil.isEmpty(user))
                || ((!CoreStringUtil.isEmpty(userName) && !CoreStringUtil.isEmpty(user)) && !user.equalsIgnoreCase(userName))) {
                continue;
            }

            connectorMatches.add(connector);
        }
        return connectorMatches;
    }

    /**
     * Get all model entries on the VDB manifest model.
     * 
     * @return a collection of model entries.
     */
    private Collection getModelEntries() {
        return this.vdb.getModelEntries();
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorMapper#setConnector(org.teiid.designer.vdb.VdbModelEntry,
     *      org.teiid.designer.runtime.Connector)
     */
    @Override
    public void setConnector( VdbModelEntry modelEntry,
                              Connector connector ) {
        // TODO: Implement!!!!!!!!!!!v
    }

    /**
     * Populate the the map of model entries to the connector that have matching connection properties.
     */
    private void updateConnectorMatches() {
        if (this.modelConnectorMatches == null) this.modelConnectorMatches = new HashMap();
        for (final Iterator iter = getModelEntries().iterator(); iter.hasNext();) {
            final VdbModelEntry modelEntry = (VdbModelEntry)iter.next();
            final Collection connectors = findMatchingConnectors(modelEntry);
            if (connectors != null && !connectors.isEmpty()) {
                this.modelConnectorMatches.put(modelEntry, connectors);
            }
        }
    }

    /**
     * Populate the the map of model entries to the connector types that have matching connection properties.
     * 
     * @throws Exception
     */
    private void updateConnectorTypeMatches() throws Exception {
        if (this.modelConnectorTypeMatches == null) this.modelConnectorTypeMatches = new HashMap();
        for (final Iterator iter = getModelEntries().iterator(); iter.hasNext();) {
            final VdbModelEntry modelEntry = (VdbModelEntry)iter.next();
            final Collection types = findMatchingConnectorType(modelEntry);
            if (types != null && !types.isEmpty()) {
                this.modelConnectorTypeMatches.put(modelEntry, types);
            }
        }
    }
}
