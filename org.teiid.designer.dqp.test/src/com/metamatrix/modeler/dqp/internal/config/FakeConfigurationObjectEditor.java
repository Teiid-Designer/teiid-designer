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
import com.metamatrix.common.config.api.Host;
import com.metamatrix.common.config.api.HostID;
import com.metamatrix.common.config.api.PropDefnAllowedValue;
import com.metamatrix.common.config.api.ResourceDescriptor;
import com.metamatrix.common.config.api.ServiceComponentDefn;
import com.metamatrix.common.config.api.SharedResource;
import com.metamatrix.common.config.api.VMComponentDefn;
import com.metamatrix.common.config.model.BasicConfigurationObjectEditor;
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
     * @see com.metamatrix.common.config.api.ConfigurationObjectEditor#setRoutingUUID(com.metamatrix.common.config.api.ServiceComponentDefn,
     *      java.lang.String)
     * @since 4.3
     */
    @Override
    public void setRoutingUUID( ServiceComponentDefn serviceComponentDefn,
                                String newRoutingUUID ) {
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
