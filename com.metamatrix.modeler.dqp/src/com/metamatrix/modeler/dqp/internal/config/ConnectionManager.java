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

import java.util.Collection;
import java.util.Map;
import com.metamatrix.common.config.api.ComponentDefnID;
import com.metamatrix.common.config.api.ComponentType;
import com.metamatrix.common.config.api.ConfigurationModelContainer;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.api.exceptions.ConfigurationException;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;


/**
 * @since 4.3
 */
public class ConnectionManager extends ConnectionBindingManager{


    /**
     *
     * @param configFileManager
     * @throws Exception
     * @since 4.3
     */
    public ConnectionManager(ConfigurationModelContainer cmc, String filename)throws Exception {
        super(cmc, filename);
    }


    /**
     *
     * @param configFile
     * @return boolean
     * @throws Exception
     * @since 4.3
     */
    protected boolean importConfig(ConfigurationModelContainer cmc)throws Exception {

        loadConfig(cmc);

        return true;
    }

    /**
     *
     * @param componentType
     * @since 4.3
     */
    protected void overrideConnectorType(ComponentType componentType) {
        getCMContainerImpl().addComponentType(componentType);
        load();
    }

    /**
     *
     * @param componentType
     * @since 4.3
     */
    protected void addConnectorType(ComponentType componentType) {
        addConnectorType(componentType, false);
    }

    /**
     *
     * @param componentType
     * @since 4.3
     */
    protected void addConnectorType(ComponentType componentType, boolean doOverwrite) {
        boolean exists = isConnectorTypeAlreadyExist(componentType);
        if(!exists || doOverwrite) {
            getCMContainerImpl().addComponentType(componentType);
            load();
        }
    }

    /**
     *
     * @param componentType
     * @return
     * @since 4.3
     */
    public boolean isConnectorTypeAlreadyExist(ComponentType componentType) {
        return getCMContainerImpl().getComponentTypes().containsKey(componentType.getFullName());
    }

    /**
     *
     * @param componentType
     * @since 4.3
     */
    protected void addConnectorType(ComponentType[] componentType) {

        boolean isLoaded = false;

        for(int idx = 0; idx<componentType.length; idx++) {
            if(!isConnectorTypeAlreadyExist(componentType[idx])) {
                getCMContainerImpl().addComponentType(componentType[idx]);
                isLoaded = true;
            }
        }

        if(isLoaded) {
            load();
        }
    }

    /**
     *
     * @param componentType
     * @since 4.3
     */
    protected void addConnectorType(ComponentType[] componentType, boolean doOverwrite) {

        boolean isLoaded = false;

        for(int idx = 0; idx<componentType.length; idx++) {
            ComponentType next = componentType[idx];
            if(next != null) {
                boolean exists = isConnectorTypeAlreadyExist(next);
                if(!exists || doOverwrite) {
                    getCMContainerImpl().addComponentType(next);
                    isLoaded = true;
                }
            }
        }

        if(isLoaded) {
            load();
        }
    }


    /**
     *
     * @param name
     * @return ComponentType
     * @since 4.3
     */
    protected ComponentType getComponentType(String name) {
        return getCMContainerImpl().getComponentType(name);
    }


    /**
     *
     * @param newBinding
     * @return ComponentType
     * @since 4.3
     */
    protected ComponentType getComponentType(ConnectorBinding newBinding) {
        return getCMContainerImpl().getComponentType(newBinding.getComponentTypeID().getFullName());
    }


    /**
     *
     * @return Collection
     * @since 4.3
     */
    public Collection getBinding() {
        return getBasicConfiguration().getConnectorBindings();
    }


    /**
     *
     * @param name
     * @return ConnectorBinding
     * @since 4.3
     */
    public ConnectorBinding getBinding(String name) {
        return getBasicConfiguration().getConnectorBinding(name);
    }


    /**
     * @param newBinding the binding being added
     * @throws ConfigurationException if binding type does not exist
     * @since 4.3
     */
    public void addBinding( ConnectorBinding newBinding ) throws ConfigurationException {
        ComponentType type = getCMContainerImpl().getComponentType(newBinding.getComponentTypeID().getFullName());

        if (type != null) {
            getBasicConfiguration().addComponentDefn(newBinding);
            load();
        } else {
            String prefix = I18nUtil.getPropertyPrefix(ConnectionManager.class);
            throw new ConfigurationException(DqpPlugin.Util.getString(prefix + "bindingTypeNotFound", //$NON-NLS-1$
                                                                      newBinding.getFullName(),
                                                                      newBinding.getComponentTypeID().getFullName()));
        }
    }

    /**
     *
     * @param newBinding
     * @return
     * @since 4.3
     */
    protected boolean isBindingAlreadyExist(ConnectorBinding newBinding) {
        return getBasicConfiguration().getConnectorBindings().contains(newBinding);
    }


    /**
     *
     * @param binding
     * @since 4.3
     */
    public void addBinding(Map binding) {
        getBasicConfiguration().setConnectors(binding);
        load();
    }

    /**
     *
     * @param existingBinding
     * @since 4.3
     */
    public void removeBinding(ConnectorBinding existingBinding) {
        ComponentType type = getCMContainerImpl().getComponentType(existingBinding.getComponentTypeID().getFullName());

        if(type != null && existingBinding.getID() != null ) {
            getBasicConfiguration().removeComponentObject((ComponentDefnID)existingBinding.getID());
            load();
		}
    }
    /**
     *
     * @param componentType
     * @throws ConfigurationException
     * @since 4.3
     */
    public void removeConnectorType(ComponentType componentType) throws ConfigurationException {

        if( getCMContainerImpl().getComponentTypes().containsKey(componentType.getFullName())) {
            getCMContainerImpl().remove(componentType.getID());
            load();
        }
    }


}
