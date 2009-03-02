/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.config;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;

import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.api.exceptions.ConfigurationException;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.modeler.dqp.internal.config.ConnectionManager;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.vdb.edit.manifest.ModelReference;

/**
 * @since 4.3
 */
public interface ConfigurationManager extends
                                     IChangeNotifier {

    /**
     * @return Collection
     * @since 4.3
     */
    public Collection getConfigurationsNameList();

    /**
     * @param newDefaultConfigName
     * @since 4.3
     */
    public void loadConfiguration(String newDefaultConfigName);

    /**
     * @param configFile
     * @return boolean
     * @throws Exception
     * @since 4.3
     */
    public boolean importConfig(File configFile) throws Exception;

    /**
     * @return boolean
     * @throws Exception
     * @since 4.3
     */
    public boolean saveConfig() throws Exception;

    /**
     * @param filename
     * @throws Exception
     * @since 4.3
     */
    public void addConnectorType(File filename) throws Exception;

    /**
     * @param filename
     * @return true if a ComponentType from the file exist in the configuration.
     * @throws Exception
     * @since 4.3
     */
    public boolean isConnectorTypeAlreadyExist(File filename) throws Exception;

    /**
     * @param newBinding
     * @return true if a ConnectorBinding exist in the configuration.
     * @throws Exception
     * @since 4.3
     */
    public boolean isBindingAlreadyExist(ConnectorBinding newBinding) throws Exception;

    /**
     * @param componentType
     * @throws Exception
     * @since 4.3
     */
    public void addConnectorType(ComponentType componentType) throws Exception;

    /**
     * @param componentType
     * @throws Exception
     * @since 4.3
     */
    public void addConnectorType(ComponentType[] componentType) throws Exception;

    /**
     * @param filename
     * @throws Exception
     * @since 4.3
     */
    public void addConnectorType(ComponentType[] componentType,
                                 boolean doOverwrite) throws Exception;

    /**
     * @param componentType
     * @throws Exception
     * @since 4.3
     */
    public void removeConnectorType(ComponentType componentType) throws Exception;

    /**
     * @param componentTypeID
     * @throws ConfigurationException
     * @since 5.0
     */
    public void removeConnectorType(ComponentTypeID componentTypeID) throws Exception;

    /**
     * @param binding
     * @return ComponentType
     * @since 4.3
     */
    public ComponentType getComponentType(ConnectorBinding binding);

    /**
     * @param name
     * @return ComponentType
     * @since 4.3
     */
    public ComponentType getComponentType(String name);

    /**
     * Obtains the {@link ConnectorBinding}s loaded in the current configuration that are supported by the DQP.
     * 
     * @return the bindings
     * @since 4.3
     */
    public Collection<ConnectorBinding> getConnectorBindings();

    /**
     * Obtains the {@link ConnectorBinding}s loaded in the current configuration.
     * 
     * @param theFilterFlag
     *            the flag indicating if the result should be filtered to only include those that are supported by the DQP
     * @return the bindings
     * @since 4.3
     */
    Collection getConnectorBindings(boolean theFilterFlag);

    /**
     * @param name
     * @return ConnectorBinding
     * @since 4.3
     */
    public ConnectorBinding getBinding(String name);

    /**
     * @param newBinding
     * @since 4.3
     */
    public void addBinding(ConnectorBinding newBinding) throws Exception;

    /**
     * @param binding
     * @since 4.3
     */
    public void addBinding(Map binding) throws Exception;

    /**
     * @param existingBinding
     * @since 4.3
     */
    public void removeBinding(ConnectorBinding newBinding) throws Exception;

    /**
     * @return IPath
     * @since 4.3
     */
    public IPath getConfigurationPath();

    /**
     * Obtains the {@link com.metamatrix.common.config.api.ConnectorBindingType}s loaded in the current configuration and
     * supported by the DQP.
     * 
     * @return the the binding types
     * @since 4.3
     */
    Collection getConnectorTypes();

    /**
     * Obtains the {@link com.metamatrix.common.config.api.ConnectorBindingType}s loaded in the current configuration.
     * 
     * @param theFilterFlag
     *            the flag indicated if the result should be filtered to only include those that are supported by the DQP
     * @return the IDs
     * @since 4.3
     */
    public Collection getConnectorTypes(boolean theFilterFlag);

    /**
     * @param typeId
     * @return ComponentType
     * @since 4.3
     */
    public ComponentType getConnectorType(Object typeId);

    /**
     * Obtains the {@link com.metamatrix.common.namedobject.BaseID}s of all the {@link ComponentType}s loaded in the current
     * configuration and supported by the DQP.
     * 
     * @return the IDs
     * @since 4.3
     */
    public Collection<ComponentTypeID> getConnectorTypeIds();

    /**
     * Obtains the {@link com.metamatrix.common.namedobject.BaseID}s of all the {@link ComponentType}s loaded in the current
     * configuration.
     * 
     * @param theFilterFlag
     *            the flag indicated if the result should be filtered to only include those that are supported by the DQP
     * @return the IDs
     * @since 4.3
     */
    Collection getConnectorTypeIds(boolean theFilterFlag);

    /**
     * @param typeId
     * @return Collection
     * @since 4.3
     */
    public Collection getBindingsForType(Object typeId);

    /**
     * @param type
     * @return Properties
     * @throws ConfigurationException
     * @since 4.3
     */
    public Properties getDefaultBindingProperties(ComponentTypeID type) throws ConfigurationException;

    /**
     * @param type
     * @return Collection
     * @since 4.3
     */
    public Collection getAllComponentTypeDefinitions(ComponentType type);

    /**
     * @param type
     * @return <code>true</code> if type is a standard type.
     * @since 5.5.3
     */
    public boolean isStandardComponentType(ComponentType type);

    /**
     * @return BasicConfigurationObjectEditor
     * @since 4.3
     */
    public BasicConfigurationObjectEditor getBasicConfigurationObjectEditor();

    /**
     * Obtains the <code>ConnectionManager</code>.
     * 
     * @return the connection manager (never <code>null</code>)
     * @since 4.3
     */
    ConnectionManager getDefaultConfig();

    /**
     * Creates a new <code>ConnectorBinding</code> from an existing binding and adds it to the configuration.
     * 
     * @param theSourceBinding
     *            the source binding
     * @param theNewBindingName
     *            the name of the new binding
     * @return the new binding
     * @since 5.0
     */
    ConnectorBinding createConnectorBinding(ConnectorBinding theSourceBinding,
                                            String theNewBindingName) throws Exception;

    /**
     * Creates a new <code>ConnectorBinding</code>, adds it to the configuration, and sets it's properties to match the JDBC
     * properties of the specified model.
     * 
     * @param theModelReference
     *            the model
     * @param theConnectorBindingType
     *            the connector binding type
     * @param theNewBindingName
     *            the name of the new binding
     * @return the new binding
     * @since 5.0
     */
    ConnectorBinding createConnectorBinding(ModelReference theModelReference,
                                            ConnectorBindingType theConnectorBindingType,
                                            String theNewBindingName) throws Exception;

    /**
     * Creates a new <code>ConnectorBinding</code> with the specified name.
     * 
     * @param theConnectorType
     *            the connector type
     * @param theNewBindingName
     *            the name of the new binding
     * @param theAddToConfigurationFlag
     *            the flag indicating if the binding should be added to the configuration
     * @return the new binding
     * @since 5.0
     */
    ConnectorBinding createConnectorBinding(ComponentType theConnectorType,
                                            String theNewBindingName,
                                            boolean theAddToConfigurationFlag) throws Exception;

    /**
     * Creates a new <code>ConnectorBinding</code> and adds it to the configuration. The <code>JdbcSource</code> is used to
     * set the binding user, URL, and driver class.
     * 
     * @param theSource
     *            the JDBC import source
     * @param theConnectorBindingType
     *            the connector binding type
     * @param theNewBindingName
     *            the name of the new binding
     * @return the new binding
     * @since 5.0
     */
    ConnectorBinding createConnectorBinding(JdbcSource theSource,
                                            ComponentType theConnectorBindingType,
                                            String theNewBindingName,
                                            Properties props) throws Exception;

    /**
     * Sets the property value for a connector binding
     * 
     * @param binding
     *            the Connector Binding
     * @param propertyID
     *            the property name
     * @param value
     *            the string value of the property
     * @since 5.0
     */
    void setConnectorPropertyValue(ConnectorBinding theBinding,
                                   String thePropertyId,
                                   String theValue) throws Exception;

    /**
     * Notifys any change listeners that something in the configuration changed.
     * 
     * @since 5.0
     */
    void notifyConfigurationChanged(ConfigurationChangeEvent event) throws Exception;

    void addConfigurationChangeListener(IConfigurationChangeListener theListener);

    void removeConfigurationChangeListener(IConfigurationChangeListener theListener);

    /**
     * Returns a value indicating how compatible the first supplied connector type is with the second supplied connector type.
     * 
     * @param type1
     * @param type2
     * @return A Compatibility value indicating how compatible <code>type1</code> is with <code>type2</code>.
     * @since 5.5.3
     */
    Compatibility compatibilityOf(ComponentType type1,
                                  ComponentType type2);

    /**
     * Returns a value indicating how compatible the first supplied connector is with the second supplied connector.
     * 
     * @param connector1
     * @param connector2
     * @return A Compatibility value indicating how compatible <code>connector1</code> is with <code>connector2</code>.
     * @since 5.5.3
     */
    Compatibility compatibilityOf(ConnectorBinding connector1,
                                  ConnectorBinding connector2);

    /**
     * Returns whether the supplied type is compatible with the supplied connector.
     * 
     * @param type
     * @param connector
     * @return <code>true</code> if <code>type</code> is compatible with <code>connector</code>.
     * @since 5.5.3
     */
    boolean compatible(ConnectorBinding connector,
                       ComponentType type);

    enum Compatibility {
        EQUAL, COMPATIBLE, POSSIBLY_COMPATIBLE, INCOMPATIBLE
    }
}
