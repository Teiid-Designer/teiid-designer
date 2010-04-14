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
import com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

/**
 * @since 4.3
 */
public class ModelConnectorBindingMapperImpl implements ModelConnectorBindingMapper {

    private final Vdb vdb;
    // model entry -> Collection(ConnectorBinding)
    private Map modelConnectorBindingMatches;
    // model entry -> Collection(ConnectorType)
    private Map modelConnectorTypeMatches;
    // model entry -> ConnectorBinding
    private Map modelConnectorBindings;

    private ExecutionAdmin executionAdmin;

    // Used to enable Unit Testing.
    public static boolean HEADLESS = false;

    /**
     * ModelConnectorBindingMapperImpl constructor.
     * 
     * @param vdb The VDB; cannot be null.
     * @since 4.3
     */
    public ModelConnectorBindingMapperImpl( final Vdb vdb ) throws Exception {
        CoreArgCheck.isNotNull(vdb);
        this.vdb = vdb;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper#findAllConnectorBindingMatches()
     * @since 4.3
     */
    public Map findAllConnectorBindingMatches() {
        if (this.modelConnectorBindingMatches == null) updateConnectorBindingMatches();
        return this.modelConnectorBindingMatches;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper#findConnectorBindingMatch(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 4.3
     */
    public Collection findConnectorBindingMatches( final VdbModelEntry modelEntry ) {
        if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) return Collections.EMPTY_LIST;
        final Map bindingMap = findAllConnectorBindingMatches();
        return (Collection)bindingMap.get(modelEntry);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper#findConnectorTypeMatches(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 4.3
     */
    public Collection findConnectorTypeMatches( final VdbModelEntry modelEntry ) {
        if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) return Collections.EMPTY_LIST;
        // update matches
        try {
            updateConnectorTypeMatches();
        } catch (Exception e) {
            DqpPlugin.Util.log(e);
        }
        final Collection matches = (Collection)this.modelConnectorTypeMatches.get(modelEntry);
        if (matches != null) return matches;
        return Collections.EMPTY_LIST;
    }

    /**
     * Find a connector binding whose properties match the JDBC connection properties stored on the model reference.
     * 
     * @param modelEntry The model reference
     * @return collection of Matching connector binding
     * @since 4.3
     */
    private Collection findMatchingConnectorBindings( final VdbModelEntry modelEntry ) {
        // if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) return Collections.EMPTY_LIST;
        // final Map jdbcProperties = ModelerDqpUtils.getModelJdbcProperties(modelEntry);
        // if (!jdbcProperties.isEmpty()) {
        //
        // // First check bindings in the VDB
        // final VdbDefnHelper vdbDefnHelper = getVdbDefnHelper();
        // Collection contextBindings = Collections.EMPTY_LIST;
        // if (vdbDefnHelper != null) contextBindings = vdbDefnHelper.getVdbDefn().getConnectorBindings().values();
        // Collection bindingMatches = getMatchingBindings(jdbcProperties, contextBindings);
        //
        // if (bindingMatches.isEmpty()) {
        // // get all the available connector bindings from configuration manager
        // final Collection configBindings = getConfigurationManager().getConnectorBindings();
        //
        // // matching bindings
        // bindingMatches = getMatchingBindings(jdbcProperties, configBindings);
        // }
        //
        // return bindingMatches;
        // }
        return Collections.EMPTY_LIST;
    }

    /**
     * Find a connector types whose properties match the JDBC connection properties stored on the model reference.
     * 
     * @param modelEntry The model reference
     * @return Matching connector types
     * @throws Exception
     * @since 4.3
     */
    private Collection findMatchingConnectorType( final VdbModelEntry modelEntry ) throws Exception {
        if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) return Collections.EMPTY_LIST;
        final Map jdbcProperties = ModelerDqpUtils.getModelJdbcProperties(modelEntry);
        final Collection connTypes = new ArrayList();
        if (!jdbcProperties.isEmpty()) {
            // get all the available connector bindings
            final Collection types = getExecutionAdmin().getConnectorTypes();
            // for each binding get properties
            for (final Iterator iter1 = types.iterator(); iter1.hasNext();) {
                final ConnectorType connectorType = (ConnectorType)iter1.next();
                final Properties connectorTypeProps = connectorType.getDefaultPropertyValues();
                final String driverClassName = connectorTypeProps.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS);
                if (CoreStringUtil.isEmpty(driverClassName)) continue;
                final String jdbcClassName = (String)jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_DRIVER_CLASS);
                if (CoreStringUtil.isEmpty(jdbcClassName) || !driverClassName.equalsIgnoreCase(jdbcClassName)) continue;
                connTypes.add(connectorType);
            }
        }
        return connTypes;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper#getAllConnectorBindings()
     * @since 4.3
     */
    public Map getAllConnectorBindings() {
        if (this.modelConnectorBindings == null) {
            // TODO: Load all ConnectionFactory's bound to source models??
        }
        return this.modelConnectorBindings;
    }

    /**
     * Get the configuration manager stored on the mapper or lookup the default manager from the plug-in.
     * 
     * @return The configuration manager
     * @throws Exception
     * @since 4.3
     */
    private ExecutionAdmin getExecutionAdmin() throws Exception {
        return (this.executionAdmin != null) ? this.executionAdmin : DqpPlugin.getInstance().getServerManager().getDefaultServer().getAdmin();
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper#getConnector(com.metamatrix.vdb.edit.manifest.ModelReference)
     * @since 4.3
     */
    public Connector getConnector( final VdbModelEntry modelEntry ) {
        if (modelEntry.getType() != ModelType.PHYSICAL_LITERAL) return null;
        final Map bindingMap = getAllConnectorBindings();
        return (Connector)bindingMap.get(modelEntry);
    }

    /**
     * Given the JDBC properties and a list of available bindings, find all the matching bindings
     * 
     * @param jdbcProperties the JDBC properties
     * @param bindings the list of available bindings
     * @return the list of matching bindings
     */
    private Collection getMatchingBindings( final Map jdbcProperties,
                                            final Collection bindings ) {
        // matching bindings
        final Collection bindingMatches = new ArrayList();
        // for each binding get properties
        for (final Iterator iter1 = bindings.iterator(); iter1.hasNext();) {
            final Connector connector = (Connector)iter1.next();

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

            if (CoreStringUtil.isEmpty(driverClassName) || CoreStringUtil.isEmpty(url)) continue;
            final String jdbcClassName = (String)jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_DRIVER_CLASS);
            if (CoreStringUtil.isEmpty(jdbcClassName) || !driverClassName.equals(jdbcClassName)) continue;

            final String jdbcUrl = (String)jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_URL);
            if (CoreStringUtil.isEmpty(jdbcUrl) || !url.equalsIgnoreCase(jdbcUrl)) continue;

            final String userName = (String)jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_USERNAME);
            if ((CoreStringUtil.isEmpty(userName) && !CoreStringUtil.isEmpty(user))
                || (!CoreStringUtil.isEmpty(userName) && CoreStringUtil.isEmpty(user))
                || ((!CoreStringUtil.isEmpty(userName) && !CoreStringUtil.isEmpty(user)) && !user.equalsIgnoreCase(userName))) continue;

            bindingMatches.add(connector);
        }
        return bindingMatches;
    }

    /**
     * Get all model entries on the VDB manifest model.
     * 
     * @return a collection of model entries.
     * @since 4.3
     */
    private Collection getModelEntries() {
        return this.vdb.getModelEntries();
    }

    private VdbModelEntry getModelEntry( final String modelPath ) {
        for (VdbModelEntry entry : this.vdb.getModelEntries()) {
            if (entry.getName().toString().equals(modelPath)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Populate the the map of model entries to the connector bindings that have matching connection properties.
     * 
     * @since 4.3
     */
    private void updateConnectorBindingMatches() {
        if (this.modelConnectorBindingMatches == null) this.modelConnectorBindingMatches = new HashMap();
        for (final Iterator iter = getModelEntries().iterator(); iter.hasNext();) {
            final VdbModelEntry modelEntry = (VdbModelEntry)iter.next();
            final Collection bindings = findMatchingConnectorBindings(modelEntry);
            if (bindings != null && !bindings.isEmpty()) this.modelConnectorBindingMatches.put(modelEntry, bindings);
        }
    }

    /**
     * Populate the the map of model entries to the connector types that have matching connection properties.
     * 
     * @throws Exception
     * @since 4.3
     */
    private void updateConnectorTypeMatches() throws Exception {
        if (this.modelConnectorTypeMatches == null) this.modelConnectorTypeMatches = new HashMap();
        for (final Iterator iter = getModelEntries().iterator(); iter.hasNext();) {
            final VdbModelEntry modelEntry = (VdbModelEntry)iter.next();
            final Collection types = findMatchingConnectorType(modelEntry);
            if (types != null && !types.isEmpty()) this.modelConnectorTypeMatches.put(modelEntry, types);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper#createConnectorBinding(org.teiid.designer.vdb.VdbModelEntry,
     *      org.teiid.designer.runtime.ConnectorType, java.lang.String)
     */
    @Override
    public Connector createConnectorBinding( VdbModelEntry modelEntry,
                                             ConnectorType ConnectorType,
                                             String theName ) throws Exception {
        // TODO: Implement!!!!!!!!!!!
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.modeler.dqp.config.ModelConnectorBindingMapper#setConnector(org.teiid.designer.vdb.VdbModelEntry,
     *      org.teiid.designer.runtime.Connector)
     */
    @Override
    public void setConnector( VdbModelEntry modelEntry,
                              Connector connector ) {
        // TODO: Implement!!!!!!!!!!!v
    }
}
