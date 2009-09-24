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
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.eclipse.core.runtime.IPath;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.modeler.dqp.JDBCConnectionPropertyNames;
import com.metamatrix.modeler.dqp.config.ConfigurationChangeEvent;
import com.metamatrix.modeler.dqp.config.ConfigurationManager;
import com.metamatrix.modeler.dqp.config.IConfigurationChangeListener;
import com.metamatrix.vdb.edit.manifest.ModelReference;

/**
 * @since 4.3
 */
public class FakeConfigurationManager implements ConfigurationManager {

    Collection bindings = new ArrayList();
    Collection bindingTypes = new ArrayList();

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConfigurationsNameList()
     * @since 4.3
     */
    public Collection getConfigurationsNameList() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#loadConfiguration(java.lang.String)
     * @since 4.3
     */
    public void loadConfiguration( String newDefaultConfigName ) {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#importConfig(java.io.File)
     * @since 4.3
     */
    public boolean importConfig( File configFile ) throws Exception {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#addConnectorType(com.metamatrix.common.config.api.ComponentType[],
     *      boolean)
     * @since 4.3
     */
    public void addConnectorType( ComponentType[] componentType,
                                  boolean doOverwrite ) throws Exception {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#saveConfig()
     * @since 4.3
     */
    public boolean saveConfig() throws Exception {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#addConnectorType(java.io.File)
     * @since 4.3
     */
    public void addConnectorType( File filename ) throws Exception {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#addConnectorType(com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    public void addConnectorType( ComponentType filename ) throws Exception {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#isConnectorTypeAlreadyExist(java.io.File)
     * @since 4.3
     */
    public boolean isConnectorTypeAlreadyExist( File filename ) throws Exception {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#isBindingAlreadyExist(com.metamatrix.common.config.api.ConnectorBinding)
     * @since 4.3
     */
    public boolean isBindingAlreadyExist( ConnectorBinding newBinding ) throws Exception {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#addConnectorType(com.metamatrix.common.config.api.ComponentType[])
     * @since 4.3
     */
    public void addConnectorType( ComponentType[] filename ) throws Exception {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getComponentType(com.metamatrix.common.config.api.ConnectorBinding)
     * @since 4.3
     */
    public ComponentType getComponentType( ConnectorBinding binding ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getComponentType(java.lang.String)
     * @since 5.0
     */
    public ComponentType getComponentType( String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorBindings()
     * @since 4.3
     */
    public Collection getConnectorBindings() {
        return this.bindings;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorBindings(boolean)
     * @since 4.3
     */
    public Collection getConnectorBindings( boolean theFilterFlag ) {
        return this.bindings;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getBinding(java.lang.String)
     * @since 4.3
     */
    public ConnectorBinding getBinding( String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#addBinding(com.metamatrix.common.config.api.ConnectorBinding)
     * @since 4.3
     */
    public void addBinding( ConnectorBinding newBinding ) {
        this.bindings.add(newBinding);
    }

    public void addBindingType( ConnectorBindingType newBinding ) {
        this.bindingTypes.add(newBinding);
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#addBinding(java.util.Map)
     * @since 4.3
     */
    public void addBinding( Map binding ) {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorTypes()
     * @since 4.3
     */
    public Collection getConnectorTypes() {
        return this.bindingTypes;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorTypes(boolean)
     * @since 4.3
     */
    public Collection getConnectorTypes( boolean theFilterFlag ) {
        return this.bindingTypes;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorType(java.lang.Object)
     * @since 4.3
     */
    public ComponentType getConnectorType( Object typeId ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorTypeIds()
     * @since 4.3
     */
    public Collection getConnectorTypeIds() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getBindingsForType(java.lang.Object)
     * @since 4.3
     */
    public Collection getBindingsForType( Object typeId ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getDefaultBindingProperties(com.metamatrix.common.config.api.ConnectorBindingType)
     * @since 4.3
     */
    public Properties getDefaultBindingProperties( ComponentTypeID type ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getAllComponentTypeDefinitions(com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    public Collection getAllComponentTypeDefinitions( ComponentType type ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#isStandardComponentType(com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    public boolean isStandardComponentType( ComponentType type ) {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getBasicConfigurationObjectEditor()
     * @since 4.3
     */
    public BasicConfigurationObjectEditor getBasicConfigurationObjectEditor() {
        return new FakeConfigurationObjectEditor();
    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#addChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void addChangeListener( IChangeListener theListener ) {

    }

    /**
     * @see com.metamatrix.core.event.IChangeNotifier#removeChangeListener(com.metamatrix.core.event.IChangeListener)
     * @since 4.3
     */
    public void removeChangeListener( IChangeListener theListener ) {

    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConfigurationPath()
     * @since 4.3
     */
    public IPath getConfigurationPath() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getConnectorTypeIds(boolean)
     * @since 4.3
     */
    public Collection getConnectorTypeIds( boolean theFilterFlag ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#getDefaultConfig()
     * @since 4.3
     */
    public ConnectionManager getDefaultConfig() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#removeBinding(com.metamatrix.common.config.api.ConnectorBinding)
     * @since 4.3
     */
    public void removeBinding( ConnectorBinding newBinding ) {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#removeConnectorType(com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    public void removeConnectorType( ComponentType componentType ) {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#removeConnectorType(com.metamatrix.common.config.api.ComponentTypeID)
     * @since 4.3
     */
    public void removeConnectorType( ComponentTypeID componentTypeID ) {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#createConnectorBinding(com.metamatrix.common.config.api.ConnectorBinding,
     *      java.lang.String)
     * @since 5.0
     */
    public ConnectorBinding createConnectorBinding( ConnectorBinding theSourceBinding,
                                                    String theNewBindingName ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#createConnectorBinding(com.metamatrix.vdb.edit.manifest.ModelReference,
     *      com.metamatrix.common.config.api.ConnectorBindingType, java.lang.String)
     * @since 5.0
     */
    public ConnectorBinding createConnectorBinding( ModelReference theModelReference,
                                                    ConnectorBindingType theConnectorBindingType,
                                                    String theName ) {

        // Return the connector with the same CONNECTOR_JDBC_DRIVER_CLASS as the specified binding type
        Properties props = theConnectorBindingType.getDefaultPropertyValues();
        String bindingTypeClass = props.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS);

        for (Iterator i = this.bindings.iterator(); i.hasNext();) {
            ConnectorBinding binding = (ConnectorBinding)i.next();
            Properties bindingProps = binding.getProperties();
            if (bindingProps != null && !StringUtil.isEmpty(bindingTypeClass)) {
                if (bindingTypeClass.equals(props.getProperty(JDBCConnectionPropertyNames.CONNECTOR_JDBC_DRIVER_CLASS))) {
                    return binding;
                }
            }
        }

        return null;
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#createConnectorBinding(com.metamatrix.common.config.api.ComponentType,
     *      java.lang.String, boolean)
     * @since 5.0
     */
    public ConnectorBinding createConnectorBinding( ComponentType theConnectorType,
                                                    String theNewBindingName,
                                                    boolean theAddToConfigurationFlag ) throws Exception {
        return null;
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
        return new Exception[0];
    }
    
    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#notifyConfigurationChanged(com.metamatrix.modeler.dqp.config.ConfigurationChangeEvent)
     */
    @Override
    public Exception[] notifyConfigurationChanged( ConfigurationChangeEvent event ) {
        return new Exception[0];
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#addConfigurationChangeListener(com.metamatrix.modeler.dqp.config.IConfigurationChangeListener)
     * @since 4.3
     */
    public void addConfigurationChangeListener( IConfigurationChangeListener theListener ) {
    }

    /**
     * @see com.metamatrix.modeler.dqp.config.ConfigurationManager#removeConfigurationChangeListener(com.metamatrix.modeler.dqp.config.IConfigurationChangeListener)
     * @since 4.3
     */
    public void removeConfigurationChangeListener( IConfigurationChangeListener theListener ) {
    }
}
