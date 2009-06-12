/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.Configuration;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.model.ConfigurationModelContainerAdapter;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * @since 4.2
 */
public class ConfigurationReader {

    private Configuration configuration;
    private ConfigurationModelContainer container;

    /** a List of ConnectorBinding instances */
    private Collection connectorBindings;
    /** a List of ConnectorBinding names */
    private Collection connectorBindingNames;
    /** an ordered Set of ConnectorBindingTypeId instances */
    // private Collection connectorTypeIds;
    /** a List of ConnectorBindingType instances */
    private Collection connectorTypes;

    /** a Map of key=ConnectorBindingTypeId, value=ConnectorType */
    // private HashMap typeIdToTypeMap;
    /** a Map of key=ConnectorBindingTypeId, value=ConnectorBinding */
    // private HashMap typeIdToBindingsMap;
    /** a Map of key=(String) Connector Binding names, value=ConnectorBinding */
    // private HashMap bindingNameToBindingMap;

    /**
     * read the specified server configuration file
     * 
     * @param configurationFile a configuration file exported from a server
     * @return true if the read is successful, false if the read failed.
     * @throws any exception that occurs attempting to open or parse the file
     * @since 4.2
     */
    public boolean readConfiguration( File configurationFile,
                                      boolean removePasswords ) throws Exception {
        boolean importSucceeded = false;

        Collection connectorBindings_temp = new ArrayList();
        Collection connectorBindingNames_temp = new ArrayList();
        Collection connectorTypeIds_temp = new TreeSet();
        Collection connectorTypes_temp = new ArrayList();
        HashMap typeIdToTypeMap_temp = new HashMap();
        HashMap typeIdToBindingsMap_temp = new HashMap();
        HashMap bindingNameToBindingMap_temp = new HashMap();

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configurationFile);

            ConfigurationModelContainerAdapter cma = new ConfigurationModelContainerAdapter();
            container = cma.readConfigurationModel(inputStream, Configuration.NEXT_STARTUP_ID);
            configuration = container.getConfiguration();

            Map typeNameToTypeMap = container.getComponentTypes();

            // First, load the connector types
            Collection componentTypes = typeNameToTypeMap.values();
            for (Iterator iter = componentTypes.iterator(); iter.hasNext();) {
                Object componentType = iter.next();
                // save off all the ConnectorBindingTypes
                if (componentType instanceof ConnectorBindingType) {
                    connectorTypes_temp.add(componentType);
                    typeIdToTypeMap_temp.put(((ConnectorBindingType)componentType).getID(), componentType);
                    connectorTypeIds_temp.add(((ConnectorBindingType)componentType).getID());
                    // } else if ( ((ComponentType) componentType).getName().equals("Service") ) {
                    // // need to add the root service for default types
                    // connectorTypes_temp.add(componentType);
                    // typeIdToTypeMap_temp.put(((ComponentType)componentType).getID(), componentType);
                }
            }

            // Next, load the connector bindings
            connectorBindings_temp = configuration.getConnectorBindings();

            for (Iterator iter = connectorBindings_temp.iterator(); iter.hasNext();) {
                ConnectorBinding binding = (ConnectorBinding)iter.next();

                // remove the password property value, as it will not be usable in the Modeler environment
                if (removePasswords) {
                    removePasswords(binding, (ConnectorBindingType)typeIdToTypeMap_temp.get(binding.getComponentTypeID()));
                }

                connectorBindingNames_temp.add(binding.getName());
                // for each binding, load it into the binding name map
                bindingNameToBindingMap_temp.put(binding.getName(), binding);
                // for each binding, load it into the binding type map
                Collection instances = (Collection)typeIdToBindingsMap_temp.get(binding.getComponentTypeID());
                if (instances == null) {
                    instances = new ArrayList();
                    typeIdToBindingsMap_temp.put(binding.getComponentTypeID(), instances);
                }
                instances.add(binding);
            }

            // if we reach this point with no exceptions, transfer the temp collections to the instance variables
            this.connectorBindings = connectorBindings_temp;
            // this.connectorTypeIds = connectorTypeIds_temp;
            this.connectorTypes = connectorTypes_temp;
            // this.typeIdToTypeMap = typeIdToTypeMap_temp;
            // this.typeIdToBindingsMap = typeIdToBindingsMap_temp;
            // this.bindingNameToBindingMap = bindingNameToBindingMap_temp;

            // import succeeded
            importSucceeded = true;

            // } catch (Exception e) {
            // DqpUiPlugin.Util.log(e);
            // MessageDialog.openError(DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell(),
            // "Error",
            // "Error loading the configuration file.  See the Message Log for details");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    // no need to log
                }
            }
        }

        return importSucceeded;
    }

    public Collection getConnectorBindings() {
        return this.connectorBindings;
    }

    public Collection getConnectorBindingNames() {
        return this.connectorBindingNames;
    }

    public Collection getConnectorTypes() {
        return this.connectorTypes;
    }

    public ConfigurationModelContainer getConfigurationModelContainer() {
        return this.container;
    }

    private void removePasswords( ConnectorBinding binding,
                                  ConnectorBindingType type ) {
        for (Iterator iter = type.getComponentTypeDefinitions().iterator(); iter.hasNext();) {
            ComponentTypeDefn defn = (ComponentTypeDefn)iter.next();
            if (defn.getPropertyDefinition().isMasked()) {
                DqpPlugin.getInstance().getConfigurationObjectEditor().setProperty(binding, defn.getName(), ""); //$NON-NLS-1$
            }
        }
    }

}
