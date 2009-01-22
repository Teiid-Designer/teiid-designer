/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core;

import java.util.Properties;

import com.metamatrix.modeler.core.ExternalResourceDescriptor;

/**
 * MappingAdapterDescriptorImpl
 */
public class ExternalResourceDescriptorImpl implements ExternalResourceDescriptor {

    private String pluginID;
    private String extensionID;
    private String name;
    private int priority;
    private String url;
    private String uri;
    private Properties properties;
    private String tempDirectoryPath;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of ExternalResourceDescriptorImpl.
     * 
     */
    public ExternalResourceDescriptorImpl() {
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /** 
     * @see com.metamatrix.modeler.core.ExternalResourceDescriptor#getExtensionID()
     */
    public String getExtensionID() {
        return this.extensionID;
    }

    /** 
     * @see com.metamatrix.modeler.core.ExternalResourceDescriptor#getPluginID()
     */
    public String getPluginID() {
        return this.pluginID;
    }

    /** 
     * @see com.metamatrix.modeler.core.ExternalResourceDescriptor#getName()
     */
    public String getResourceName() {
        return this.name;
    }

    /** 
     * @see com.metamatrix.modeler.core.ExternalResourceDescriptor#getURI()
     */
    public String getInternalUri() {
        return this.uri;
    }

    /** 
     * @see com.metamatrix.modeler.core.ExternalResourceDescriptor#getURL()
     */
    public String getResourceUrl() {
        return this.url;
    }

    /** 
     * @see com.metamatrix.modeler.core.ExternalResourceDescriptor#getProperties()
     */
    public Properties getProperties() {
        return this.properties;
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    /**
     * @param string
     */
    public void setResourceName(final String string) {
        this.name = string;
    }

    /**
     * @param uri
     */
    public void setInternalUri(final String uri) {
        this.uri = uri;
    }

    /**
     * @param url
     */
    public void setResourceUrl(final String url) {
        this.url = url;
    }

    /**
     * @param properties
     */
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }

    /**
     * @param string
     */
    public void setTempDirectoryPath(final String string) {
        tempDirectoryPath = string;
    }

    /**
     * @return
     */
    public String getTempDirectoryPath() {
        return tempDirectoryPath;
    }

    /**
     * @param string
     */
    public void setExtensionID(String string) {
        extensionID = string;
    }

    /**
     * @param string
     */
    public void setPluginID(String string) {
        pluginID = string;
    }

    /** 
     * @see com.metamatrix.modeler.core.ExternalResourceDescriptor#getPriority()
     * @since 4.3
     */
    public int getPriority() {
        return priority;
    }

    
    /** 
     * @param priority The priority to set.
     * @since 4.3
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

}
