/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import java.util.Properties;


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
     * @see org.teiid.designer.core.ExternalResourceDescriptor#getExtensionID()
     */
    @Override
	public String getExtensionID() {
        return this.extensionID;
    }

    /** 
     * @see org.teiid.designer.core.ExternalResourceDescriptor#getPluginID()
     */
    @Override
	public String getPluginID() {
        return this.pluginID;
    }

    /** 
     * @see org.teiid.designer.core.ExternalResourceDescriptor#getName()
     */
    @Override
	public String getResourceName() {
        return this.name;
    }

    /** 
     * @see org.teiid.designer.core.ExternalResourceDescriptor#getURI()
     */
    @Override
	public String getInternalUri() {
        return this.uri;
    }

    /** 
     * @see org.teiid.designer.core.ExternalResourceDescriptor#getURL()
     */
    @Override
	public String getResourceUrl() {
        return this.url;
    }

    /** 
     * @see org.teiid.designer.core.ExternalResourceDescriptor#getProperties()
     */
    @Override
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
     * @see org.teiid.designer.core.ExternalResourceDescriptor#getPriority()
     * @since 4.3
     */
    @Override
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
