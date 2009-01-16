/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.dqp.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.model.BasicConfiguration;
import com.metamatrix.common.config.model.ConfigurationModelContainerImpl;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;

/**
 * @since 4.3
 */
public class ConnectionBindingManager {

    /** an ordered Set of ConnectorBindingTypeId instances */
    private Collection connectorTypeIds = new TreeSet();

    /** an ordered Set of ConnectorBindingTypeId instances filtered for use in the DQP */
    private Collection filteredConnectionTypeIds;

    /** a List of ConnectorBindingType instances */
    private Collection connectorTypes = new ArrayList();

    /** a List of ConnectorBindingType instances filtered for use in the DQP */
    private Collection filteredConnectorTypes = new ArrayList();

    /** a Map of key=ConnectorBindingTypeId, value=ConnectorType */
    private Map typeIdToTypeMap = new HashMap();

    /** a Map of key=ConnectorBindingTypeId, value=ConnectorType */
    private Map filteredTypeIdToTypeMap = new HashMap();

    /** a Map of key=ConnectorBindingTypeId, value=ConnectorBinding */
    private Map typeIdToBindingsMap = new HashMap();

    /** a Map of key=ConnectorBindingTypeId, value=ConnectorBinding */
    private Map filteredTypeIdToBindingsMap = new HashMap();

    /** **/
    private ConfigurationModelContainerImpl configModel;

    /** **/
    private String configurationName;

    /**
     * @param configFileManager
     * @throws Exception
     * @since 4.3
     */
    protected ConnectionBindingManager( ConfigurationModelContainer cmc,
                                        String name ) throws Exception {

        configurationName = name;
        loadConfig(cmc);
    }

    /**
     * @return
     * @since 4.3
     */
    public String getConfigurationName() {
        return configurationName;
    }

    /**
     * @return BasicConfiguration
     * @since 4.3
     */
    protected BasicConfiguration getBasicConfiguration() {
        return (BasicConfiguration)configModel.getConfiguration();
    }

    /**
     * @throws Exception
     * @since 4.3
     */
    protected void loadConfig( ConfigurationModelContainer cmc ) throws Exception {

        configModel = new ConfigurationModelContainerImpl();
        configModel.setConfigurationObjects(cmc.getAllObjects());

        load();
    }

    /**
     * @return ConfigurationModelContainerImpl
     * @since 4.3
     */
    public ConfigurationModelContainerImpl getCMContainerImpl() {
        return configModel;
    }

    /**
     * @since 4.3
     */
    protected void load() {

        Collection connectorTypeIds_temp = new TreeSet();
        Collection filteredConnectorTypeIds_temp = new TreeSet();

        Collection connectorTypes_temp = new ArrayList();
        Collection filteredConnectorTypes_temp = new ArrayList();

        Map typeIdToTypeMap_temp = new HashMap();
        Map filteredTypeIdToTypeMap_temp = new HashMap();

        Map typeIdToBindingsMap_temp = new HashMap();
        Map filteredTypeIdToBindingsMap_temp = new HashMap();

        Collection componentTypes = getCMContainerImpl().getComponentTypes().values();

        for (Iterator iter = componentTypes.iterator(); iter.hasNext();) {
            Object componentType = iter.next();
            // save off all the ConnectorBindingTypes
            if (componentType instanceof ConnectorBindingType) {
                ConnectorBindingType bindingType = (ConnectorBindingType)componentType;
                Object id = bindingType.getID();

                connectorTypes_temp.add(bindingType);
                typeIdToTypeMap_temp.put(id, componentType);
                connectorTypeIds_temp.add(id);

                // filter
                if (ModelerDqpUtils.isValidConnectorType(bindingType)) {
                    filteredConnectorTypes_temp.add(bindingType);
                    filteredConnectorTypeIds_temp.add(id);
                    filteredTypeIdToTypeMap_temp.put(id, componentType);
                }
            }
        }

        // Next, load the connector bindings
        Collection connectorBindings_temp = getBasicConfiguration().getConnectorBindings();

        for (Iterator iter = connectorBindings_temp.iterator(); iter.hasNext();) {
            ConnectorBinding binding = (ConnectorBinding)iter.next();
            // for each binding, load it into the binding type map
            Collection instances = (Collection)typeIdToBindingsMap_temp.get(binding.getComponentTypeID());
            if (instances == null) {
                instances = new ArrayList();
                typeIdToBindingsMap_temp.put(binding.getComponentTypeID(), instances);
            }
            instances.add(binding);

            // filter
            if (filteredConnectorTypeIds_temp.contains(binding.getComponentTypeID())) {
                filteredTypeIdToBindingsMap_temp.put(binding.getComponentTypeID(), instances);
            }
        }

        // if we reach this point with no exceptions, transfer the temp collections to the instance variables
        this.connectorTypeIds = connectorTypeIds_temp;
        this.filteredConnectionTypeIds = filteredConnectorTypeIds_temp;

        this.connectorTypes = connectorTypes_temp;
        this.filteredConnectorTypes = filteredConnectorTypes_temp;

        this.typeIdToTypeMap = typeIdToTypeMap_temp;
        this.filteredTypeIdToTypeMap = filteredTypeIdToTypeMap_temp;

        this.typeIdToBindingsMap = typeIdToBindingsMap_temp;
        this.filteredTypeIdToBindingsMap = filteredTypeIdToBindingsMap_temp;
    }

    /**
     * Obtains the {@link ConnectorBindingType}s loaded in the current configuration and supported by the DQP.
     * 
     * @return the connector types (never <code>null</code>)
     * @since 4.3
     */
    public Collection getConnectorTypes() {
        return getConnectorTypes(true);
    }

    /**
     * Obtains the {@link ConnectorBindingType}s loaded in the current configuration.
     * 
     * @param theFilterFlag the flag indicating if the result should be filtered to only include those that are supported by the
     *        DQP
     * @return an unmodifiable collection of connector types (never <code>null</code>)
     * @since 4.3
     */
    public Collection getConnectorTypes( boolean theFilterFlag ) {
        return Collections.unmodifiableCollection(theFilterFlag ? this.filteredConnectorTypes : this.connectorTypes);
    }

    /**
     * Obtains the {@link ConnectorBindingType} with the specified identifier loaded in the current configuration only if it is
     * supported by the DQP.
     * 
     * @param theTypeId the identifier of the connector binding type being requested
     * @return the connector type or <code>null</code>
     * @since 4.3
     */
    public ComponentType getConnectorType( Object theTypeId ) {
        return getConnectorType(theTypeId, true);
    }

    /**
     * Obtains the {@link ConnectorBindingType} with the specified {@link com.metamatrix.common.namedobject.BaseID} identifier
     * loaded in the current configuration.
     * 
     * @param theTypeId the identifier of the connector binding type being requested
     * @param theFilterFlag the flag indicating if the result should be filtered to only include those that are supported by the
     *        DQP
     * @return the connector type or <code>null</code>
     * @since 4.3
     */
    public ComponentType getConnectorType( Object theTypeId,
                                           boolean theFilterFlag ) {
        return (ComponentType)(theFilterFlag ? this.filteredTypeIdToTypeMap.get(theTypeId) : this.typeIdToTypeMap.get(theTypeId));
    }

    /**
     * Obtains the {@link com.metamatrix.common.namedobject.BaseID}s of all the {@link ComponentType}s loaded in the current
     * configuration that are supported by the DQP.
     * 
     * @return the IDs
     * @since 4.3
     */
    public Collection getConnectorTypeIds() {
        return getConnectorTypeIds(true);
    }

    /**
     * Obtains the {@link com.metamatrix.common.namedobject.BaseID}s of all the {@link ComponentType}s loaded in the current
     * configuration.
     * 
     * @param theFilterFlag the flag indicating if the result should be filtered to only include those that are supported by the
     *        DQP
     * @return an unmodifiable collection of IDs (never <code>null</code>)
     * @since 4.3
     */
    public Collection getConnectorTypeIds( boolean theFilterFlag ) {
        return Collections.unmodifiableCollection(theFilterFlag ? this.filteredConnectionTypeIds : this.connectorTypeIds);
    }

    /**
     * Obtains the {@link ConnectorBinding}s loaded in the current configuration for the specified type identifier. and supported
     * by the DQP.
     * 
     * @param theTypeId the connectory binding type whose bindings are being requested
     * @return the bindings (never <code>null</code>)
     * @since 4.3
     */
    public Collection getBindingsForType( Object theTypeId ) {
        return getBindingsForType(theTypeId, true);
    }

    /**
     * Obtains the {@link ConnectorBinding}s loaded in the current configuration for the specified type identifier.
     * 
     * @param theTypeId the connectory binding type whose bindings are being requested
     * @param theFilterFlag the flag indicating if the result should be filtered to only include those that are supported by the
     *        DQP
     * @return an unmodifiable collection of bindings (never <code>null</code>)
     * @since 4.3
     */
    public Collection getBindingsForType( Object theTypeId,
                                          boolean theFilterFlag ) {

        Collection result = (Collection)(theFilterFlag ? this.filteredTypeIdToBindingsMap.get(theTypeId) : this.typeIdToBindingsMap.get(theTypeId));

        return Collections.unmodifiableCollection(result == null ? Collections.EMPTY_LIST : result);
    }

    /**
     * @param type
     * @return Properties
     * @throws ConfigurationException
     * @since 4.3
     */
    public Properties getDefaultBindingProperties( ComponentTypeID type ) {
        return getCMContainerImpl().getDefaultPropertyValues(type);
    }

    /**
     * @param type
     * @return Collection
     * @since 4.3
     */
    public Collection getAllComponentTypeDefinitions( ComponentType type ) {

        if (type == null) {
            return Collections.EMPTY_LIST;
        }

        Collection defns = type.getComponentTypeDefinitions();
        Collection inheritedDefns = getSuperComponentTypeDefinitions(null, null, type);

        // We want the final, returned Collection to NOT have any
        // duplicate objects in it. The two Collections above may have
        // duplicates - one in inheritedDefns which is a name and a default
        // value for a super-type, and one in defns which is a name AND A
        // DIFFERENT DEFAULT VALUE, from the specified type, which overrides
        // the default value of the supertype. We want to only keep the
        // BasicComponentTypeDefn corresponding to the sub-type name and default
        // value.
        // For example, type "JDBCConnector" has a ComponentType for the
        // property called "ServiceClassName" and a default value equal to
        // "com.metamatrix.connector.jdbc.JDBCConnectorTranslator". The
        // super type "Connector" also defines a "ServiceClassName" defn,
        // but defines no default values. Or worse yet, it might define
        // in invalid default value. So we only want to keep the right
        // defn and value.

        Iterator inheritedIter = inheritedDefns.iterator();
        Iterator localIter = defns.iterator();

        ComponentTypeDefn inheritedDefn = null;
        ComponentTypeDefn localDefn = null;

        while (localIter.hasNext()) {
            localDefn = (ComponentTypeDefn)localIter.next();
            while (inheritedIter.hasNext()) {
                inheritedDefn = (ComponentTypeDefn)inheritedIter.next();
                if (localDefn.getPropertyDefinition().equals(inheritedDefn.getPropertyDefinition())) {
                    inheritedIter.remove();
                }
            }
            inheritedIter = inheritedDefns.iterator();
        }

        defns.addAll(inheritedDefns);

        return defns;
    }

    /**
     * This method calls itself recursively to return a Collection of all ComponentTypeDefn objects defined for the super-type of
     * the componentTypeID parameter. The equality of each PropertyDefn object contained within each ComponentTypeDefn is the
     * criteria to determine if a defn exists in the sub-type already, or not.
     * 
     * @param defnMap Map of PropertyDefn object to the ComponentTypeDefn object containing that PropertyDefn
     * @param defns return-by-reference Collection, built recursively
     * @param componentTypes Collection of all possible ComponentType objects
     * @param componentTypeID the type for which super-type ComponentTypeDefn objects are sought
     * @param transaction The transaction to operate within
     * @return Collection of all super-type ComponentTypeDefn objects (which are <i>not </i> overridden by sup-types)
     */
    private Collection getSuperComponentTypeDefinitions( Map defnMap,
                                                         Collection defns,
                                                         ComponentType type ) {
        if (defnMap == null) {
            defnMap = new HashMap();
        }

        if (defns == null) {
            defns = new ArrayList();
        }

        if (type == null) {
            return defns;
        }

        if (type.getSuperComponentTypeID() == null) {
            return defns;
        }

        ComponentTypeID superTypeId = type.getSuperComponentTypeID();
        ComponentType superType = getComponentType(superTypeId);

        Collection superDefns = superType.getComponentTypeDefinitions();
        // add the defns not already defined to the map
        ComponentTypeDefn sDefn;
        if (superDefns != null && superDefns.size() > 0) {
            Iterator it = superDefns.iterator();
            while (it.hasNext()) {
                sDefn = (ComponentTypeDefn)it.next();
                // this map has been changed to be keyed
                // on the PropertyDefn object of a ComponentTypeDefn,
                // instead of the i.d. of the ComponentTypeDefn
                if (!defnMap.containsKey(sDefn.getPropertyDefinition())) {
                    defnMap.put(sDefn.getPropertyDefinition(), sDefn);
                    defns.add(sDefn);
                }
            }
        }

        return getSuperComponentTypeDefinitions(defnMap, defns, superType);
    }

    /**
     * @param typeId the ID of the component type being requested
     * @return the component type
     * @since 5.5.3
     */
    private ComponentType getComponentType( ComponentTypeID typeId ) {
        return this.configModel.getComponentType(typeId.getFullName());
    }

    /**
     * @see java.lang.Object#toString()
     * @since 4.3
     */
    @Override
    public String toString() {
        return "From ConnectionBindingManager : " + getConfigurationName();//$NON-NLS-1$
    }
}
