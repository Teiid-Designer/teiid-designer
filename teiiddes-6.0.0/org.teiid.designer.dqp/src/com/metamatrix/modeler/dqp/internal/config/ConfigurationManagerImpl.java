/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IPath;

import com.metamatrix.common.actions.ObjectEditor;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.Configuration;
import com.metamatrix.common.config.api.ConfigurationObjectEditor;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.config.ConfigurationChangeEvent;
import com.metamatrix.modeler.dqp.config.ConfigurationConstants;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.config.IConfigurationChangeListener;
import com.metamatrix.modeler.dqp.util.ModelerDqpUtils;
import com.metamatrix.vdb.edit.manifest.ModelReference;

/**
 * @since 4.3
 */
public class ConfigurationManagerImpl implements ConfigurationManager, ConfigurationConstants {

    private Map configList;
    private Collection listenerList;
    private Collection configurationChangeListenerList;
    private ConnectionManager defaultConnectionManager;
    private ConfigFileManager configFileManager;

    private static final String STANDARD = "Standard"; //$NON-NLS-1$

    /**
     * @param configFilePath
     * @throws Exception
     * @since 4.3
     */
    public ConfigurationManagerImpl( IPath configFilePath ) throws Exception {
        listenerList = new ArrayList(39);
        configurationChangeListenerList = new ArrayList(39);
        configFileManager = new ConfigFileManager(configFilePath);
        init();
    }

    /**
     * @throws Exception
     * @since 4.3
     */
    private void init() throws Exception {
        configList = configFileManager.loadConfigFiles();
        defaultConnectionManager = (ConnectionManager)configList.get(ConfigFileManager.CONFIG_FILE_NAME);
    }

    /**
     * @return Collection
     * @since 4.3
     */
    public Collection getConfigurationsNameList() {
        return configList.keySet();
    }

    /**
     * @param defaultConnectionManager
     * @since 4.3
     */
    public void loadConfiguration( String newDefaultConfigName ) {

        ConnectionManager newDefaultConnectionManager = (ConnectionManager)configList.get(newDefaultConfigName);

        this.defaultConnectionManager = newDefaultConnectionManager;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getDefaultConfig()
     * @since 4.3
     */
    public ConnectionManager getDefaultConfig() {
        return defaultConnectionManager;
    }

    /**
     * @param configFile
     * @return boolean
     * @throws Exception
     * @since 4.3
     */
    public boolean importConfig( File configFile ) throws Exception {
        boolean status = getDefaultConfig().importConfig(configFileManager.importConfig(configFile));

        ArrayList list = new ArrayList();
        Collection bindings = getConnectorBindings();
        for (Iterator i = bindings.iterator(); i.hasNext();) {
            ConnectorBinding binding = (ConnectorBinding)i.next();
            list.add(binding.getFullName());
        }

        saveConfigLocal(new ConfigurationChangeEvent(ADDED_EVENT, CONNECTOR_BINDINGS_CHANGED,
                                                     (String[])list.toArray(new String[list.size()])));

        return status;
    }

    /**
     * @return boolean
     * @throws Exception
     * @since 4.3
     */
    public boolean saveConfig() throws Exception {
        return configFileManager.saveConfig(defaultConnectionManager.getCMContainerImpl(),
                                            defaultConnectionManager.getConfigurationName());
    }

    /**
     * @return any exceptions thrown by the configuration listeners
     * @throws Exception if there is a problem saving the configuration
     * @since 4.3
     */
    private Exception[] saveConfigLocal( ConfigurationChangeEvent event ) throws Exception {

        configFileManager.saveConfig(defaultConnectionManager.getCMContainerImpl(),
                                     defaultConnectionManager.getConfigurationName());

        return fireChangeEvent(event);
    }

    /**
     * @param ConnectorBinding
     * @return ComponentType
     * @since 4.3
     */
    public ComponentType getComponentType( ConnectorBinding binding ) {
        return getDefaultConfig().getComponentType(binding);
    }

    /**
     * @param name
     * @return ComponentType
     * @since 4.3
     */
    public ComponentType getComponentType( String name ) {
        return getDefaultConfig().getComponentType(name);
    }

    /**
     * @return Collection
     * @since 4.3
     */
    public Collection getConnectorBindings() {
        return getConnectorBindings(true);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorBindings(boolean)
     * @since 4.3
     */
    public Collection getConnectorBindings( boolean theFilterFlag ) {
        Collection result = null;
        Collection temp = getDefaultConfig().getBinding();

        if ((temp == null) || temp.isEmpty()) {
            result = Collections.EMPTY_LIST;
        } else if (!theFilterFlag) {
            // return all
            result = temp;
        } else {
            // return filtered collection
            result = new ArrayList(temp.size());
            Iterator itr = temp.iterator();

            while (itr.hasNext()) {
                ConnectorBinding binding = (ConnectorBinding)itr.next();
                ConnectorBindingType type = (ConnectorBindingType)getComponentType(binding);

                if (ModelerDqpUtils.isValidConnectorType(type)) {
                    result.add(binding);
                }
            }
        }

        return result;
    }

    /**
     * @param name
     * @return ConnectorBinding
     * @since 4.3
     */
    public ConnectorBinding getBinding( String name ) {
        return getDefaultConfig().getBinding(name);
    }

    /**
     * @param newBinding
     * @since 4.3
     */
    public void addBinding( ConnectorBinding newBinding ) throws Exception {
        getDefaultConfig().addBinding(newBinding);
        saveConfigLocal(new ConfigurationChangeEvent(ADDED_EVENT, CONNECTOR_BINDINGS_CHANGED, newBinding.getName()));
    }

    /**
     * @param binding
     * @since 4.3
     */
    public void addBinding( Map binding ) throws Exception {
        getDefaultConfig().addBinding(binding);
        Set bindingNames = binding.keySet();
        saveConfigLocal(new ConfigurationChangeEvent(ADDED_EVENT, CONNECTOR_BINDINGS_CHANGED,
                                                     (String[])bindingNames.toArray(new String[bindingNames.size()])));
    }

    public void removeBinding( ConnectorBinding existingBinding ) throws Exception {

        // 1. remove the binding
        getDefaultConfig().removeBinding(existingBinding);

        // 2. save config
        saveConfigLocal(new ConfigurationChangeEvent(REMOVED_EVENT, CONNECTOR_BINDINGS_CHANGED, existingBinding.getName()));
    }

    /**
     * @param filename
     * @throws Exception
     * @since 4.3
     */
    public void addConnectorType( File filename ) throws Exception {
        ComponentType componentType = configFileManager.loadConnectorType(filename);
        getDefaultConfig().overrideConnectorType(componentType);
        saveConfigLocal(new ConfigurationChangeEvent(ADDED_EVENT, CONNECTOR_TYPE_CHANGE, componentType.getName()));
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#isConnectorTypeAlreadyExist(java.io.File)
     * @since 4.3
     */
    public boolean isConnectorTypeAlreadyExist( File filename ) throws Exception {
        ComponentType componentType = configFileManager.loadConnectorType(filename);
        return getDefaultConfig().isConnectorTypeAlreadyExist(componentType);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#isBindingAlreadyExist(com.metamatrix.common.config.api.ConnectorBinding)
     * @since 4.3
     */
    public boolean isBindingAlreadyExist( ConnectorBinding newBinding ) throws Exception {
        return getDefaultConfig().isBindingAlreadyExist(newBinding);
    }

    /**
     * @param componentType
     * @throws Exception
     * @since 4.3
     */
    public void addConnectorType( ComponentType componentType ) throws Exception {
        getDefaultConfig().addConnectorType(componentType);
        saveConfigLocal(new ConfigurationChangeEvent(ADDED_EVENT, CONNECTOR_TYPE_CHANGE, componentType.getName()));
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#addConnectorType(com.metamatrix.common.config.api.ComponentType[])
     * @since 4.3
     */
    public void addConnectorType( ComponentType[] componentType ) throws Exception {
        addConnectorType(componentType, false);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#addConnectorType(com.metamatrix.common.config.api.ComponentType[])
     * @since 4.3
     */
    public void addConnectorType( ComponentType[] componentType,
                                  boolean doOverwrite ) throws Exception {
        getDefaultConfig().addConnectorType(componentType, doOverwrite);

        ArrayList list = new ArrayList();
        for (int i = 0; i < componentType.length; i++) {
            list.add(componentType[i].getFullName());
        }
        saveConfigLocal(new ConfigurationChangeEvent(ADDED_EVENT, CONNECTOR_TYPE_CHANGE,
                                                     (String[])list.toArray(new String[list.size()])));
    }

    /**
     * 
     */
    public void removeConnectorType( ComponentType componentType ) throws Exception {
        // We need to remove the connector bindings for a type first
        Collection bindingsForType = getDefaultConfig().getBindingsForType(componentType.getID());
        for (Iterator iter = bindingsForType.iterator(); iter.hasNext();) {
            removeBinding((ConnectorBinding)iter.next());
        }
        // Now we can safely remove the type
        getDefaultConfig().removeConnectorType(componentType);
        saveConfigLocal(new ConfigurationChangeEvent(REMOVED_EVENT, CONNECTOR_TYPE_CHANGE, componentType.getName()));
    }

    /**
     * 
     */
    public void removeConnectorType( ComponentTypeID componentTypeID ) throws Exception {
        Collection types = getConnectorTypes();
        ComponentType type = null;
        for (Iterator iter = types.iterator(); iter.hasNext();) {
            type = (ComponentType)iter.next();
            if (type.getID().equals(componentTypeID)) {
                removeConnectorType(type);
                break;
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorTypes()
     * @since 4.3
     */
    public Collection getConnectorTypes() {
        return getConnectorTypes(true);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorTypes(boolean)
     * @since 4.3
     */
    public Collection getConnectorTypes( boolean theFilterFlag ) {
        return getDefaultConfig().getConnectorTypes(theFilterFlag);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorType(java.lang.Object)
     * @since 4.3
     */
    public ComponentType getConnectorType( Object typeId ) {
        return getDefaultConfig().getConnectorType(typeId);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorTypeIds()
     * @since 4.3
     */
    public Collection getConnectorTypeIds() {
        return getConnectorTypeIds(true);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorTypeIds(boolean)
     * @since 4.3
     */
    public Collection getConnectorTypeIds( boolean theFilterFlag ) {
        return getDefaultConfig().getConnectorTypeIds(theFilterFlag);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getBindingsForType(java.lang.Object)
     * @since 4.3
     */
    public Collection getBindingsForType( Object typeId ) {
        return getDefaultConfig().getBindingsForType(typeId);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getDefaultBindingProperties(com.metamatrix.common.config.api.ConnectorBindingType)
     * @since 4.3
     */
    public Properties getDefaultBindingProperties( ComponentTypeID type ) {
        return getDefaultConfig().getDefaultBindingProperties(type);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getAllComponentTypeDefinitions(com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    public Collection getAllComponentTypeDefinitions( ComponentType type ) {
        return getDefaultConfig().getAllComponentTypeDefinitions(type);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#isStandardComponentType(com.metamatrix.common.config.api.ComponentType)
     * @since 5.5.3
     */
    public boolean isStandardComponentType( ComponentType type ) {
        boolean isStandard = false;

        // Get the "Standard" default if available
        String booleanStr = type.getDefaultValue(STANDARD);
        if (booleanStr != null) {
            isStandard = Boolean.valueOf(booleanStr);
        }

        return isStandard;
    }

    /**
     * @return BasicConfigurationObjectEditor
     * @since 4.3
     */
    public BasicConfigurationObjectEditor getBasicConfigurationObjectEditor() {
        return configFileManager.getBasicConfigurationObjectEditor();
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener( IChangeListener theListener ) {

        if (listenerList.contains(theListener)) return;

        listenerList.add(theListener);
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener( IChangeListener theListener ) {
        listenerList.remove(theListener);
    }

    /**
     * @since 4.3
     */
    public void addConfigurationChangeListener( IConfigurationChangeListener theListener ) {

        if (configurationChangeListenerList.contains(theListener)) return;

        configurationChangeListenerList.add(theListener);
    }

    /**
     * @since 4.3
     */
    public void removeConfigurationChangeListener( IConfigurationChangeListener theListener ) {
        configurationChangeListenerList.remove(theListener);
    }

    /**
     * @since 4.3
     */
    protected Exception[] fireChangeEvent( ConfigurationChangeEvent event ) {
        Collection<Exception> errors = new ArrayList<Exception>();
        
        if (listenerList != null && !listenerList.isEmpty()) {
            for (Iterator it = listenerList.iterator(); it.hasNext();) {
                try {
                    IChangeListener listener = (IChangeListener)it.next();
                    listener.stateChanged(this);
                } catch (Exception e) {
                    errors.add(e);
                }
            }
        }

        if (configurationChangeListenerList != null || !configurationChangeListenerList.isEmpty()) {
            for (Iterator it = configurationChangeListenerList.iterator(); it.hasNext();) {
                try {
                    IConfigurationChangeListener listener = (IConfigurationChangeListener)it.next();
                    listener.stateChanged(event);
                } catch (Exception e) {
                    errors.add(e);
                }
            }
        }

        return errors.toArray(new Exception[errors.size()]);
    }
    
    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#notifyConfigurationChanged(com.metamatrix.modeler.dqp.config.ConfigurationChangeEvent)
     */
    @Override
    public Exception[] notifyConfigurationChanged( ConfigurationChangeEvent event ) {
        return fireChangeEvent(event);
    }

    @Override
    public String toString() {
        return "From ConfigurationManagerImpl : " + getDefaultConfig().getConfigurationName();//$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConfigurationPath()
     * @since 4.3
     */
    public IPath getConfigurationPath() {
        return this.configFileManager.getPath();
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#createConnectorBinding(com.metamatrix.common.config.api.ConnectorBinding,
     *      java.lang.String)
     * @since 5.0
     */
    public ConnectorBinding createConnectorBinding( ConnectorBinding theSourceBinding,
                                                    String theNewBindingName ) throws Exception {
        ConfigurationObjectEditor coe = DqpPlugin.getInstance().getConfigurationObjectEditor();
        ConnectorBinding binding = coe.createConnectorComponent(Configuration.NEXT_STARTUP_ID,
                                                                theSourceBinding,
                                                                theNewBindingName,
                                                                null);
        addBinding(binding);
        return binding;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#createConnectorBinding(com.metamatrix.vdb.edit.manifest.ModelReference,
     *      com.metamatrix.common.config.api.ConnectorBindingType, java.lang.String)
     * @since 5.0
     */
    public ConnectorBinding createConnectorBinding( ModelReference theModelReference,
                                                    ConnectorBindingType theConnectorBindingType,
                                                    String theNewBindingName ) throws Exception {
        ConfigurationObjectEditor coe = DqpPlugin.getInstance().getConfigurationObjectEditor();
        ConnectorBinding binding = coe.createConnectorComponent(Configuration.NEXT_STARTUP_ID,
                                                                (ComponentTypeID)theConnectorBindingType.getID(),
                                                                theNewBindingName,
                                                                null);
        // set the properties on the binding
        Properties bindingProps = getConnectorBindingProperties(theModelReference);

        // verify that the properties on the bindingType match that of the modelReference
        // check the driver class
        Properties props = theConnectorBindingType.getDefaultPropertyValues();
        String bindingTypeClass = props.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS);
        String bindingClass = bindingProps.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS);

        if (StringUtil.isEmpty(bindingTypeClass) || StringUtil.isEmpty(bindingClass)
            || !bindingTypeClass.equalsIgnoreCase(bindingClass)) {
            return null;
        }

        binding = (ConnectorBinding)coe.modifyProperties(binding, props, ObjectEditor.ADD);
        binding = (ConnectorBinding)coe.modifyProperties(binding, bindingProps, ObjectEditor.ADD);
        addBinding(binding);

        return binding;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#createConnectorBinding(com.metamatrix.common.config.api.ComponentType,
     *      java.lang.String, boolean)
     * @since 5.0
     */
    public ConnectorBinding createConnectorBinding( ComponentType theConnectorType,
                                                    String theNewBindingName,
                                                    boolean theAddToConfigurationFlag ) throws Exception {
        ConfigurationObjectEditor coe = DqpPlugin.getInstance().getConfigurationObjectEditor();
        ConnectorBinding binding = coe.createConnectorComponent(Configuration.NEXT_STARTUP_ID,
                                                                (ComponentTypeID)theConnectorType.getID(),
                                                                theNewBindingName,
                                                                null);

        // set the properties on the binding
        Properties bindingProps = DqpPlugin.getInstance().getConfigurationManager().getDefaultBindingProperties(binding.getComponentTypeID());
        binding = (ConnectorBinding)coe.modifyProperties(binding, bindingProps, ObjectEditor.ADD);

        if (theAddToConfigurationFlag) {
            addBinding(binding);
        }

        return binding;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#setConnectorPropertyValue(com.metamatrix.common.config.api.ConnectorBinding, java.lang.String, java.lang.String)
     */
    @Override
    public Exception[] setConnectorPropertyValue( ConnectorBinding theBinding,
                                                  String thePropertyId,
                                                  String theValue ) throws Exception {
        DqpPlugin.getInstance().getConfigurationObjectEditor().setProperty(theBinding, thePropertyId, theValue);

        // For New Connector Binding action we don't add the binding until OK from dialog, so we need to check if binding exists
        // and if NOT, then we don't continue (i.e. don't save)
        if (DqpPlugin.getInstance().getConfigurationManager().getBinding(theBinding.getFullName()) != null) {
            return saveConfigLocal(new ConfigurationChangeEvent(CHANGED_EVENT, CONNECTOR_BINDINGS_CHANGED, theBinding.getName()));
        }
        
        return new Exception[0];
    }

    /**
     * Get the connectorbinding properties reading the jdbc import properties on the modelReference.
     * 
     * @param reference The modelReference whose jdbc import properties are read.
     * @return The connector binding properties.
     * @since 4.3
     */
    private Properties getConnectorBindingProperties( ModelReference reference ) {
        Properties connectorProps = new Properties();
        Map jdbcProperties = ModelerDqpUtils.getModelJdbcProperties(reference);

        if (!jdbcProperties.isEmpty()) {
            // get driver class
            String driverClass = (String)jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_DRIVER_CLASS);

            if (!StringUtil.isEmpty(driverClass)) {
                connectorProps.put(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS, driverClass);
            }

            // get URL
            String url = (String)jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_URL);

            if (!StringUtil.isEmpty(url)) {
                connectorProps.put(JDBCConnectionPropertyNames.CONNECTOR_JDBC_URL, url);
            }

            // get user
            String userName = (String)jdbcProperties.get(JDBCConnectionPropertyNames.JDBC_IMPORT_USERNAME);

            if (!StringUtil.isEmpty(userName)) {
                connectorProps.put(JDBCConnectionPropertyNames.CONNECTOR_JDBC_USER, userName);
            }
        }

        return connectorProps;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#compatibilityOf(com.metamatrix.common.config.api.ComponentType,
     *      com.metamatrix.common.config.api.ComponentType)
     * @since 5.5.3
     */
    public Compatibility compatibilityOf( ComponentType type1,
                                          ComponentType type2 ) {
        return compatibilityOf(type1, type2, null);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#compatibilityOf(com.metamatrix.common.config.api.ConnectorBinding,
     *      com.metamatrix.common.config.api.ConnectorBinding)
     * @since 5.5.3
     */
    public Compatibility compatibilityOf( ConnectorBinding connector1,
                                          ConnectorBinding connector2 ) {
        if (connector1 == connector2) {
            return Compatibility.EQUAL;
        }
        return compatibilityOf(getComponentType(connector1), getComponentType(connector2), connector1);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#compatible(com.metamatrix.common.config.api.ConnectorBinding,
     *      com.metamatrix.common.config.api.ComponentType)
     * @since 5.5.3
     */
    public boolean compatible( ConnectorBinding connector,
                               ComponentType type ) {
        return (compatibilityOf(getComponentType(connector), type, connector) != Compatibility.INCOMPATIBLE);
    }

    /*
     * Note, if connector is not null, type1 represents its connector type.
     */
    private Compatibility compatibilityOf( ComponentType type1,
                                           ComponentType type2,
                                           ConnectorBinding connector ) {
        if (type1 == type2) {
            return Compatibility.EQUAL;
        }

        // First, check equality starting with each type's name
        if (!type1.getName().equals(type2.getName())) {
            return Compatibility.INCOMPATIBLE;
        }
        Compatibility compatibility = (connector == null ? Compatibility.EQUAL : Compatibility.COMPATIBLE);

        // Ensure both types have the same code & product type, and that both are or both are not XA
        if (type1.getComponentTypeCode() != type2.getComponentTypeCode()
            || !type1.getParentComponentTypeID().equals(type2.getParentComponentTypeID())
            || type1.isOfTypeXAConnector() != type2.isOfTypeXAConnector()) {
            return Compatibility.INCOMPATIBLE;
        }

        // Check compatibility of property definitions.
        // - To be compatible, all definitions in type1 must either exist in type2 or not be required.
        // - If a connector is supplied, a definition existS in both types, the definitions have different default values, and the
        // connector doesn't provide an explicit value, then the connector will possibly be compatible with the type.
        // - If a definition eixsts in type2 but not in type1:
        // --- If a connector is supplied, the types are compatible if the extra definition is not required or has a default
        // value.
        // --- If a connector is not supplied, the types are incompatible.
        Collection type2Definitions = getAllComponentTypeDefinitions(type2);
        for (Object obj : getAllComponentTypeDefinitions(type1)) {
            ComponentTypeDefn type1Definition = (ComponentTypeDefn)obj;
            if (type2Definitions.remove(type1Definition)) {
                if (connector != null) {
                    String name = type1Definition.getName();
                    if (connector.getProperty(name) == null && !type1.getDefaultValue(name).equals(type2.getDefaultValue(name))) {
                        compatibility = Compatibility.POSSIBLY_COMPATIBLE;
                    }
                }
            } else {
                if (type1Definition.isRequired() && type1.getDefaultValue(type1Definition.getName()) != null) {
                    return Compatibility.INCOMPATIBLE;
                }
                compatibility = Compatibility.COMPATIBLE;
            }
        }
        if (!type2Definitions.isEmpty()) {
            if (connector == null) {
                return Compatibility.INCOMPATIBLE;
            }
            for (Object obj : type2Definitions) {
                ComponentTypeDefn type2Definition = (ComponentTypeDefn)obj;
                if (type2Definition.isRequired() && type2.getDefaultValue(type2Definition.getName()) != null) {
                    return Compatibility.INCOMPATIBLE;
                }
            }
        }
        return compatibility;
    }
}
