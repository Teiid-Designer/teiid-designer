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
import java.util.Date;
import java.util.Properties;
import com.metamatrix.common.config.api.ComponentTypeDefn;
import com.metamatrix.common.config.api.ComponentTypeID;
import com.metamatrix.common.config.api.ConnectorBindingType;
import com.metamatrix.common.config.model.ConfigurationVisitor;
import com.metamatrix.common.namedobject.BaseID;


/** 
 * @since 4.3
 */
public class FakeConnectorBindingType implements
                                     ConnectorBindingType {
    
    Properties props = new Properties();    

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getComponentTypeDefinitions()
     * @since 4.3
     */
    public Collection getComponentTypeDefinitions() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getComponentTypeDefinition(java.lang.String)
     * @since 4.3
     */
    public ComponentTypeDefn getComponentTypeDefinition(String name) {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getDefaultPropertyValues()
     * @since 4.3
     */
    public Properties getDefaultPropertyValues() {
        return this.props;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getParentComponentTypeID()
     * @since 4.3
     */
    public ComponentTypeID getParentComponentTypeID() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getSuperComponentTypeID()
     * @since 4.3
     */
    public ComponentTypeID getSuperComponentTypeID() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getComponentTypeCode()
     * @since 4.3
     */
    public int getComponentTypeCode() {
        return 0;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#isDeployable()
     * @since 4.3
     */
    public boolean isDeployable() {
        return false;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#isDeprecated()
     * @since 4.3
     */
    public boolean isDeprecated() {
        return false;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#isMonitored()
     * @since 4.3
     */
    public boolean isMonitored() {
        return false;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#isOfTypeConnector()
     * @since 4.3
     */
    public boolean isOfTypeConnector() {
        return false;
    }
    
    /** 
     * @see com.metamatrix.common.config.api.ComponentType#isOfTypeXAConnector()
     * @since 4.3
     */
    public boolean isOfTypeXAConnector() {
        return false;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getCreatedBy()
     * @since 4.3
     */
    public String getCreatedBy() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getCreatedDate()
     * @since 4.3
     */
    public Date getCreatedDate() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getLastChangedBy()
     * @since 4.3
     */
    public String getLastChangedBy() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getLastChangedDate()
     * @since 4.3
     */
    public Date getLastChangedDate() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getDescription()
     * @since 4.3
     */
    public String getDescription() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#accept(com.metamatrix.common.config.model.ConfigurationVisitor)
     * @since 4.3
     */
    public void accept(ConfigurationVisitor visitor) {
    }

    /** 
     * @see com.metamatrix.common.namedobject.BaseObject#getID()
     * @since 4.3
     */
    public BaseID getID() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.namedobject.BaseObject#getName()
     * @since 4.3
     */
    public String getName() {
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
     * @see java.lang.Object#toString()
     * @since 4.3
     */
    @Override
    public String toString() {
        return super.toString();
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
     * @see com.metamatrix.common.config.api.ComponentType#getDefaultValue(java.lang.String)
     * @since 4.3
     */
    public String getDefaultValue(String propertyName) {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#getMaskedPropertyNames()
     * @since 4.3
     */
    public Collection getMaskedPropertyNames() {
        Collection maskedProps = new ArrayList(1);
        maskedProps.add("password"); //$NON-NLS-1$
        return maskedProps;
    }

    /** 
     * @see com.metamatrix.common.config.api.ConnectorBindingType#getExtensionModules()
     * @since 4.3
     */
    public String[] getExtensionModules() {
        return null;
    }

    /** 
     * @see com.metamatrix.common.config.api.ComponentType#isOfConnectorProductType()
     * @since 5.0
     */
    public boolean isOfConnectorProductType() {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.common.config.api.ComponentType#getDefaultPropertyValues(java.util.Properties)
     */
    @Override
    public Properties getDefaultPropertyValues( Properties arg0 ) {
        return null;
    }
}
