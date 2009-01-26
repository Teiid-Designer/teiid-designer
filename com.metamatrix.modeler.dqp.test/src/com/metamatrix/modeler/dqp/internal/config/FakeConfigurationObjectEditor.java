/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import com.metamatrix.common.actions.ModificationActionQueue;
import com.metamatrix.common.config.api.ComponentObject;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.Configuration;
import com.metamatrix.common.config.api.ConfigurationID;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.DeployedComponent;
import com.metamatrix.common.config.api.Host;
import com.metamatrix.common.config.api.HostID;
import com.metamatrix.common.config.api.ProductServiceConfig;
import com.metamatrix.common.config.api.ProductServiceConfigID;
import com.metamatrix.common.config.api.ProductType;
import com.metamatrix.common.config.api.ProductTypeID;
import com.metamatrix.common.config.api.PropDefnAllowedValue;
import com.metamatrix.common.config.api.ResourceDescriptor;
import com.metamatrix.common.config.api.ServiceComponentDefn;
import com.metamatrix.common.config.api.ServiceComponentDefnID;
import com.metamatrix.common.config.api.SharedResource;
import com.metamatrix.common.config.api.VMComponentDefn;
import com.metamatrix.common.config.api.VMComponentDefnID;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
import com.metamatrix.common.log.LogConfiguration;
import com.metamatrix.common.object.PropertyDefinition;

/**
 * @since 4.3
 */
public class FakeConfigurationObjectEditor extends BasicConfigurationObjectEditor {

    private static final long serialVersionUID = 1L;

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createHost(java.lang.String)
     * @since 4.3
     */
    @Override
    public Host createHost( String hostName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createHost(com.metamatrix.common.config.api.ConfigurationID,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public Host createHost( ConfigurationID configurationID,
                            String hostName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createHost(com.metamatrix.common.config.api.Configuration,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public Host createHost( Configuration configuration,
                            String hostName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createConfiguration(com.metamatrix.common.config.api.ConfigurationID,
     *      java.util.Collection)
     * @since 4.3
     */
    @Override
    public void createConfiguration( ConfigurationID configID,
                                     Collection configObjects ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createConfiguration(java.lang.String)
     * @since 4.3
     */
    @Override
    public Configuration createConfiguration( String configurationName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createConfiguration(java.lang.String, java.util.Date,
     *      java.util.Date)
     * @since 4.3
     */
    @Override
    public Configuration createConfiguration( String configurationName,
                                              Date creationDate,
                                              Date lastChangedDate ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createConfiguration(com.metamatrix.common.config.api.Configuration,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public Configuration createConfiguration( Configuration original,
                                              String newName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createComponentType(int, java.lang.String,
     *      com.metamatrix.common.config.api.ComponentTypeID, com.metamatrix.common.config.api.ComponentTypeID, boolean, boolean)
     * @since 4.3
     */
    @Override
    public ComponentType createComponentType( int classTypeCode,
                                              String name,
                                              ComponentTypeID parentID,
                                              ComponentTypeID superID,
                                              boolean deployable,
                                              boolean monitored ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createComponentType(com.metamatrix.common.config.api.ComponentType,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public ComponentType createComponentType( ComponentType componentType,
                                              String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createProductType(java.lang.String, boolean, boolean)
     * @since 4.3
     */
    @Override
    public ProductType createProductType( String name,
                                          boolean deployable,
                                          boolean monitored ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createProductType(java.lang.String, java.util.Collection,
     *      boolean, boolean)
     * @since 4.3
     */
    @Override
    public ProductType createProductType( String name,
                                          Collection serviceComponentTypes,
                                          boolean deployable,
                                          boolean monitored ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createComponentTypeDefn(com.metamatrix.common.config.api.ComponentType,
     *      com.metamatrix.common.object.PropertyDefinition, boolean)
     * @since 4.3
     */
    @Override
    public ComponentTypeDefn createComponentTypeDefn( ComponentType type,
                                                      PropertyDefinition propertyDefinition,
                                                      boolean isEffectiveImmediately ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createComponentTypeDefn(com.metamatrix.common.config.api.ComponentType,
     *      com.metamatrix.common.object.PropertyDefinition)
     * @since 4.3
     */
    @Override
    public ComponentTypeDefn createComponentTypeDefn( ComponentType type,
                                                      PropertyDefinition propertyDefinition ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createComponentTypeDefn(com.metamatrix.common.config.api.ComponentTypeID,
     *      com.metamatrix.common.object.PropertyDefinition, boolean)
     * @since 4.3
     */
    @Override
    public ComponentTypeDefn createComponentTypeDefn( ComponentTypeID typeID,
                                                      PropertyDefinition propertyDefinition,
                                                      boolean isEffectiveImmediately ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createComponentTypeDefn(com.metamatrix.common.config.api.ComponentTypeID,
     *      com.metamatrix.common.object.PropertyDefinition)
     * @since 4.3
     */
    @Override
    public ComponentTypeDefn createComponentTypeDefn( ComponentTypeID typeID,
                                                      PropertyDefinition propertyDefinition ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createPropDefnAllowedValue(com.metamatrix.common.config.api.ComponentTypeDefn,
     *      com.metamatrix.common.object.PropertyDefinition, java.lang.String)
     * @since 4.3
     */
    @Override
    public PropDefnAllowedValue createPropDefnAllowedValue( ComponentTypeDefn typeDefn,
                                                            PropertyDefinition propDefn,
                                                            String value ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createVMComponentDefn(com.metamatrix.common.config.api.ConfigurationID,
     *      com.metamatrix.common.config.api.HostID, com.metamatrix.common.config.api.ComponentTypeID, java.lang.String)
     * @since 4.3
     */
    @Override
    public VMComponentDefn createVMComponentDefn( ConfigurationID configurationID,
                                                  HostID hostID,
                                                  ComponentTypeID typeID,
                                                  String componentName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createVMComponentDefn(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.HostID, com.metamatrix.common.config.api.ComponentTypeID, java.lang.String)
     * @since 4.3
     */
    @Override
    public VMComponentDefn createVMComponentDefn( Configuration configuration,
                                                  HostID hostID,
                                                  ComponentTypeID typeID,
                                                  String componentName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createServiceComponentDefn(com.metamatrix.common.config.api.ConfigurationID,
     *      com.metamatrix.common.config.api.ComponentTypeID, java.lang.String)
     * @since 4.3
     */
    @Override
    public ServiceComponentDefn createServiceComponentDefn( ConfigurationID configurationID,
                                                            ComponentTypeID typeID,
                                                            String componentName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createServiceComponentDefn(com.metamatrix.common.config.api.ConfigurationID,
     *      com.metamatrix.common.config.api.ComponentTypeID, java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ServiceComponentDefn createServiceComponentDefn( ConfigurationID configurationID,
                                                            ComponentTypeID typeID,
                                                            String componentName,
                                                            String routingUUID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createServiceComponentDefn(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ComponentTypeID, java.lang.String)
     * @since 4.3
     */
    @Override
    public ServiceComponentDefn createServiceComponentDefn( Configuration configuration,
                                                            ComponentTypeID typeID,
                                                            String componentName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createServiceComponentDefn(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ComponentTypeID, java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ServiceComponentDefn createServiceComponentDefn( Configuration configuration,
                                                            ComponentTypeID typeID,
                                                            String componentName,
                                                            String routingUUID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createServiceComponentDefn(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ComponentTypeID, java.lang.String,
     *      com.metamatrix.common.config.api.ProductServiceConfigID)
     * @since 4.3
     */
    @Override
    public ServiceComponentDefn createServiceComponentDefn( Configuration configuration,
                                                            ComponentTypeID typeID,
                                                            String componentName,
                                                            ProductServiceConfigID pscID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createResourceDescriptor(com.metamatrix.common.config.api.ConfigurationID,
     *      com.metamatrix.common.config.api.ComponentTypeID, java.lang.String)
     * @since 4.3
     */
    @Override
    public ResourceDescriptor createResourceDescriptor( ConfigurationID configurationID,
                                                        ComponentTypeID typeID,
                                                        String descriptorName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createResourceDescriptor(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ComponentTypeID, java.lang.String)
     * @since 4.3
     */
    @Override
    public ResourceDescriptor createResourceDescriptor( Configuration configuration,
                                                        ComponentTypeID typeID,
                                                        String descriptorName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createSharedResource(com.metamatrix.common.config.api.ComponentTypeID,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public SharedResource createSharedResource( ComponentTypeID typeID,
                                                String resourceName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createDeployedServiceComponent(java.lang.String,
     *      com.metamatrix.common.config.api.ConfigurationID, com.metamatrix.common.config.api.HostID,
     *      com.metamatrix.common.config.api.VMComponentDefnID, com.metamatrix.common.config.api.ServiceComponentDefnID,
     *      com.metamatrix.common.config.api.ProductServiceConfigID, com.metamatrix.common.config.api.ComponentTypeID)
     * @since 4.3
     */
    @Override
    public DeployedComponent createDeployedServiceComponent( String instanceName,
                                                             ConfigurationID configurationID,
                                                             HostID hostId,
                                                             VMComponentDefnID vmId,
                                                             ServiceComponentDefnID serviceComponentDefnID,
                                                             ProductServiceConfigID pscID,
                                                             ComponentTypeID serviceComponentTypeID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createDeployedServiceComponent(java.lang.String,
     *      com.metamatrix.common.config.api.Configuration, com.metamatrix.common.config.api.HostID,
     *      com.metamatrix.common.config.api.VMComponentDefnID, com.metamatrix.common.config.api.ServiceComponentDefn,
     *      com.metamatrix.common.config.api.ProductServiceConfigID)
     * @since 4.3
     */
    @Override
    public DeployedComponent createDeployedServiceComponent( String instanceName,
                                                             Configuration configuration,
                                                             HostID hostId,
                                                             VMComponentDefnID vmId,
                                                             ServiceComponentDefn serviceComponentDefn,
                                                             ProductServiceConfigID pscID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createProductServiceConfig(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ProductServiceConfig, java.lang.String)
     * @since 4.3
     */
    @Override
    public ProductServiceConfig createProductServiceConfig( Configuration configuration,
                                                            ProductServiceConfig originalPSC,
                                                            String newName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createProductServiceConfig(com.metamatrix.common.config.api.ConfigurationID,
     *      com.metamatrix.common.config.api.ProductTypeID, java.lang.String)
     * @since 4.3
     */
    @Override
    public ProductServiceConfig createProductServiceConfig( ConfigurationID configurationID,
                                                            ProductTypeID productTypeID,
                                                            String componentName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createProductServiceConfig(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ProductTypeID, java.lang.String)
     * @since 4.3
     */
    @Override
    public ProductServiceConfig createProductServiceConfig( Configuration configuration,
                                                            ProductTypeID productTypeID,
                                                            String componentName ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#deployProductServiceConfig(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ProductServiceConfig, com.metamatrix.common.config.api.HostID,
     *      com.metamatrix.common.config.api.VMComponentDefnID)
     * @since 4.3
     */
    @Override
    public Collection deployProductServiceConfig( Configuration configuration,
                                                  ProductServiceConfig psc,
                                                  HostID hostId,
                                                  VMComponentDefnID vmId ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#deployServiceDefn(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ServiceComponentDefn, com.metamatrix.common.config.api.ProductServiceConfigID)
     * @since 4.3
     */
    @Override
    public Collection deployServiceDefn( Configuration configuration,
                                         ServiceComponentDefn serviceComponentDefn,
                                         ProductServiceConfigID pscID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setRoutingUUID(com.metamatrix.common.config.api.ServiceComponentDefn,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public void setRoutingUUID( ServiceComponentDefn serviceComponentDefn,
                                String newRoutingUUID ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setEnabled(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ServiceComponentDefn, com.metamatrix.common.config.api.ProductServiceConfig, boolean,
     *      boolean)
     * @since 4.3
     */
    @Override
    public Collection setEnabled( Configuration configuration,
                                  ServiceComponentDefn serviceComponentDefn,
                                  ProductServiceConfig psc,
                                  boolean enabled,
                                  boolean deleteDeployedComps ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setEnabled(com.metamatrix.common.config.api.ServiceComponentDefnID,
     *      com.metamatrix.common.config.api.ProductServiceConfig, boolean)
     * @since 4.3
     */
    @Override
    public ProductServiceConfig setEnabled( ServiceComponentDefnID serviceComponentDefnID,
                                            ProductServiceConfig psc,
                                            boolean enabled ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#updateProductServiceConfig(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ProductServiceConfig, java.util.Collection)
     * @since 4.3
     */
    @Override
    public ProductServiceConfig updateProductServiceConfig( Configuration config,
                                                            ProductServiceConfig psc,
                                                            Collection newServiceIDList ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#addServiceComponentDefn(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ProductServiceConfig, com.metamatrix.common.config.api.ServiceComponentDefnID)
     * @since 4.3
     */
    @Override
    public ProductServiceConfig addServiceComponentDefn( Configuration configuration,
                                                         ProductServiceConfig psc,
                                                         ServiceComponentDefnID serviceComponentDefnID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#addServiceComponentDefn(com.metamatrix.common.config.api.ProductServiceConfig,
     *      com.metamatrix.common.config.api.ServiceComponentDefnID)
     * @since 4.3
     */
    @Override
    public ProductServiceConfig addServiceComponentDefn( ProductServiceConfig psc,
                                                         ServiceComponentDefnID serviceComponentDefnID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#addServiceComponentType(com.metamatrix.common.config.api.ProductType,
     *      com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    @Override
    public ProductType addServiceComponentType( ProductType productType,
                                                ComponentType serviceComponentType ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#removeServiceComponentType(com.metamatrix.common.config.api.ProductType,
     *      com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    @Override
    public ProductType removeServiceComponentType( ProductType productType,
                                                   ComponentType serviceComponentType ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#getEditableProperties(com.metamatrix.common.config.api.ComponentObject)
     * @since 4.3
     */
    @Override
    public Properties getEditableProperties( ComponentObject t ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#renamePSC(com.metamatrix.common.config.api.ProductServiceConfig,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public ProductServiceConfig renamePSC( ProductServiceConfig psc,
                                           String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#renameVM(com.metamatrix.common.config.api.VMComponentDefn,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public VMComponentDefn renameVM( VMComponentDefn vm,
                                     String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setIsReleased(com.metamatrix.common.config.api.Configuration,
     *      boolean)
     * @since 4.3
     */
    @Override
    public Configuration setIsReleased( Configuration t,
                                        boolean newValue ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setIsDeployed(com.metamatrix.common.config.api.Configuration,
     *      boolean)
     * @since 4.3
     */
    @Override
    public Configuration setIsDeployed( Configuration t,
                                        boolean newValue ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setLogConfiguration(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.log.LogConfiguration)
     * @since 4.3
     */
    @Override
    public Configuration setLogConfiguration( Configuration t,
                                              LogConfiguration logConfiguration ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setProperty(com.metamatrix.common.config.api.ComponentObject,
     *      java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ComponentObject setProperty( ComponentObject t,
                                        String name,
                                        String value ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#addProperty(com.metamatrix.common.config.api.ComponentObject,
     *      java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ComponentObject addProperty( ComponentObject t,
                                        String name,
                                        String value ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#removeProperty(com.metamatrix.common.config.api.ComponentObject,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public ComponentObject removeProperty( ComponentObject t,
                                           String name ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#modifyProperties(com.metamatrix.common.config.api.ComponentObject,
     *      java.util.Properties, int)
     * @since 4.3
     */
    @Override
    public ComponentObject modifyProperties( ComponentObject t,
                                             Properties props,
                                             int command ) {
        for (Enumeration enumer = props.propertyNames(); enumer.hasMoreElements();) {
            String propName = (String)enumer.nextElement();
            String propValue = props.getProperty(propName);
            t.getProperties().put(propName, propValue);
        }
        return t;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#modifyComponentTypeDefn(com.metamatrix.common.config.api.ComponentTypeDefn,
     *      com.metamatrix.common.config.api.ComponentTypeDefn)
     * @since 4.3
     */
    @Override
    public ComponentTypeDefn modifyComponentTypeDefn( ComponentTypeDefn original,
                                                      ComponentTypeDefn updated ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setParentComponentTypeID(com.metamatrix.common.config.api.ComponentType,
     *      com.metamatrix.common.config.api.ComponentTypeID)
     * @since 4.3
     */
    @Override
    public ComponentType setParentComponentTypeID( ComponentType t,
                                                   ComponentTypeID parentID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setSuperComponentTypeID(com.metamatrix.common.config.api.ComponentType,
     *      com.metamatrix.common.config.api.ComponentTypeID)
     * @since 4.3
     */
    @Override
    public ComponentType setSuperComponentTypeID( ComponentType t,
                                                  ComponentTypeID superID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setIsDeployable(com.metamatrix.common.config.api.ComponentType,
     *      boolean)
     * @since 4.3
     */
    @Override
    public ComponentType setIsDeployable( ComponentType t,
                                          boolean newValue ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setIsDeprecated(com.metamatrix.common.config.api.ComponentType,
     *      boolean)
     * @since 4.3
     */
    @Override
    public ComponentType setIsDeprecated( ComponentType t,
                                          boolean newValue ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setIsMonitored(com.metamatrix.common.config.api.ComponentType,
     *      boolean)
     * @since 4.3
     */
    @Override
    public ComponentType setIsMonitored( ComponentType t,
                                         boolean newValue ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setLastChangedHistory(com.metamatrix.common.config.api.ComponentType,
     *      java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ComponentType setLastChangedHistory( ComponentType type,
                                                String lastChangedBy,
                                                String lastChangedDate ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setCreationChangedHistory(com.metamatrix.common.config.api.ComponentType,
     *      java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ComponentType setCreationChangedHistory( ComponentType type,
                                                    String createdBy,
                                                    String creationDate ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setLastChangedHistory(com.metamatrix.common.config.api.ComponentObject,
     *      java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ComponentObject setLastChangedHistory( ComponentObject compObject,
                                                  String lastChangedBy,
                                                  String lastChangedDate ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setCreationChangedHistory(com.metamatrix.common.config.api.ComponentObject,
     *      java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ComponentObject setCreationChangedHistory( ComponentObject compObject,
                                                      String createdBy,
                                                      String creationDate ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setComponentType(com.metamatrix.common.config.api.ComponentObject,
     *      com.metamatrix.common.config.api.ComponentTypeID)
     * @since 4.3
     */
    @Override
    public ComponentObject setComponentType( ComponentObject t,
                                             ComponentTypeID componentType ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setComponentTypeDefinitions(com.metamatrix.common.config.api.ComponentType,
     *      java.util.Collection)
     * @since 4.3
     */
    @Override
    public ComponentType setComponentTypeDefinitions( ComponentType t,
                                                      Collection defns ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setPropertyDefinition(com.metamatrix.common.config.api.ComponentTypeDefn,
     *      com.metamatrix.common.object.PropertyDefinition)
     * @since 4.3
     */
    @Override
    public ComponentTypeDefn setPropertyDefinition( ComponentTypeDefn t,
                                                    PropertyDefinition defn ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setBootStrapConfiguration(com.metamatrix.common.config.api.ConfigurationID)
     * @since 4.3
     */
    @Override
    public void setBootStrapConfiguration( ConfigurationID configurationID ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setNextStartupConfiguration(com.metamatrix.common.config.api.ConfigurationID)
     * @since 4.3
     */
    @Override
    public void setNextStartupConfiguration( ConfigurationID configurationID ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#delete(com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    @Override
    public void delete( ComponentType target ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#delete(com.metamatrix.common.config.api.Host)
     * @since 4.3
     */
    @Override
    public void delete( Host target ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#delete(com.metamatrix.common.config.api.ComponentTypeDefn,
     *      com.metamatrix.common.config.api.ComponentType)
     * @since 4.3
     */
    @Override
    public void delete( ComponentTypeDefn target,
                        ComponentType type ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#delete(com.metamatrix.common.config.api.Configuration)
     * @since 4.3
     */
    @Override
    public void delete( Configuration target ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#delete(com.metamatrix.common.config.api.ConfigurationID)
     * @since 4.3
     */
    @Override
    public void delete( ConfigurationID targetID ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#delete(com.metamatrix.common.config.api.Configuration,
     *      boolean)
     * @since 4.3
     */
    @Override
    public void delete( Configuration target,
                        boolean deleteDependencies ) {
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#delete(com.metamatrix.common.config.api.SharedResource)
     * @since 4.3
     */
    @Override
    public SharedResource delete( SharedResource target ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#delete(com.metamatrix.common.config.api.ComponentObject,
     *      com.metamatrix.common.config.api.Configuration)
     * @since 4.3
     */
    @Override
    public Configuration delete( ComponentObject target,
                                 Configuration configuration ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#delete(com.metamatrix.common.config.api.ComponentObject,
     *      com.metamatrix.common.config.api.Configuration, boolean)
     * @since 4.3
     */
    @Override
    public Configuration delete( ComponentObject target,
                                 Configuration configuration,
                                 boolean deleteDependencies ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createConnectorComponent(com.metamatrix.common.config.api.ConfigurationID,
     *      com.metamatrix.common.config.api.ComponentTypeID, java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ConnectorBinding createConnectorComponent( ConfigurationID configuID,
                                                      ComponentTypeID typeID,
                                                      String descriptorName,
                                                      String routingUUID ) {
        return new FakeConnectorBinding();
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createConnectorComponent(com.metamatrix.common.config.api.ConfigurationID,
     *      com.metamatrix.common.config.api.ConnectorBinding, java.lang.String, java.lang.String)
     * @since 4.3
     */
    @Override
    public ConnectorBinding createConnectorComponent( ConfigurationID configurationID,
                                                      ConnectorBinding original,
                                                      String newName,
                                                      String routingUUID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#createConnectorComponent(com.metamatrix.common.config.api.Configuration,
     *      com.metamatrix.common.config.api.ComponentTypeID, java.lang.String,
     *      com.metamatrix.common.config.api.ProductServiceConfigID)
     * @since 4.3
     */
    @Override
    public ConnectorBinding createConnectorComponent( Configuration configuration,
                                                      ComponentTypeID typeID,
                                                      String componentName,
                                                      ProductServiceConfigID pscID ) {
        return null;
    }

    /**
     * @see com.metamatrix.common.actions.ObjectEditor#getDestination()
     * @since 4.3
     */
    @Override
    public ModificationActionQueue getDestination() {
        return null;
    }

    /**
     * @see com.metamatrix.common.actions.ObjectEditor#setDestination(com.metamatrix.common.actions.ModificationActionQueue)
     * @since 4.3
     */
    @Override
    public void setDestination( ModificationActionQueue destination ) {
    }

}
