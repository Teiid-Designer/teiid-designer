/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.config;

import java.util.Date;
import java.util.Properties;

import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConfigurationID;
import com.metamatrix.common.config.api.ConnectorBinding;
import com.metamatrix.common.config.model.ConfigurationVisitor;
import com.metamatrix.common.namedobject.BaseID;


/** 
 * @since 4.3
 */
public class FakeConnectorBinding implements
                                 ConnectorBinding {
    
    Properties props = new Properties();

    /** 
     * @see com.metamatrix.common.config.api.ServiceComponentDefn#isQueuedService()
     * @since 4.3
     */
    public boolean isQueuedService() {
        return false;
    }

    /** 
     * @see com.metamatrix.common.config.api.ServiceComponentDefn#accept(com.metamatrix.common.config.model.ConfigurationVisitor)
     * @since 4.3
     */
    public void accept(ConfigurationVisitor visitor) {
    }

    /** 
     * @see com.metamatrix.common.config.api.ServiceComponentDefn#getRoutingUUID()
     * @since 4.3
     */
    public String getRoutingUUID() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentDefn#getConfigurationID()
     * @since 4.3
     */
    public ConfigurationID getConfigurationID() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentDefn#isEnabled()
     * @since 4.3
     */
    public boolean isEnabled() {
        return false;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#getName()
     * @since 4.3
     */
    public String getName() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#getProperties()
     * @since 4.3
     */
    public Properties getProperties() {
        return this.props;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#getProperty(java.lang.String)
     * @since 4.3
     */
    public String getProperty(String name) {
        return this.props.getProperty(name);
    }

    public void setProperty(String name, String value) {
        this.props.setProperty(name, value);
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#getComponentTypeID()
     * @since 4.3
     */
    public ComponentTypeID getComponentTypeID() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#getDescription()
     * @since 4.3
     */
    public String getDescription() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#getCreatedBy()
     * @since 4.3
     */
    public String getCreatedBy() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#getCreatedDate()
     * @since 4.3
     */
    public Date getCreatedDate() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#getLastChangedBy()
     * @since 4.3
     */
    public String getLastChangedBy() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#getLastChangedDate()
     * @since 4.3
     */
    public Date getLastChangedDate() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentObject#isDependentUpon(com.metamatrix.common.namedobject.BaseID)
     * @since 4.3
     */
    public boolean isDependentUpon(BaseID componentObjectId) {
        return false;
    }

    /** 
     * @see com.metamatrix.common.namedobject.BaseObject#getID()
     * @since 4.3
     */
    public BaseID getID() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.namedobject.BaseObject#getFullName()
     * @since 4.3
     */
    public String getFullName() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.namedobject.BaseObject#compareTo(java.lang.Object)
     * @since 4.3
     */
    public int compareTo(Object obj) {
        return 0;
    }

    /** 
     * @see java.lang.Object#clone()
     * @since 4.3
     */
    @Override
    public Object clone() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ConnectorBinding#getConnectorClass()
     * @since 4.3
     */
    public String getConnectorClass() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ConnectorBinding#getDeployedName()
     * @since 4.3
     */
    public String getDeployedName() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentDefn#isEssential()
     */
    public boolean isEssential() {
        return false;
    }

}
