/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

import org.teiid.designer.jdbc.metadata.DatabaseInfo;

/**
 * DatabaseInfoImpl
 */
public class DatabaseInfoImpl implements DatabaseInfo {
    
    private String productName;
    private String productVersion;
    private int driverMajorVersion;
    private int driverMinorVersion;
    private String driverName;
    private String driverVersion;
    private String databaseURL;
    private String userName;
    private boolean readOnly;

    /**
     * Construct an instance of DatabaseInfoImpl.
     * 
     */
    public DatabaseInfoImpl() {
        super();
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.DatabaseInfo#getProductName()
     */
    @Override
	public String getProductName() {
        return this.productName;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.DatabaseInfo#getProductVersion()
     */
    @Override
	public String getProductVersion() {
        return this.productVersion;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.DatabaseInfo#getMajorVersion()
     */
    @Override
	public int getDriverMajorVersion() {
        return this.driverMajorVersion;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.DatabaseInfo#getMinorVersion()
     */
    @Override
	public int getDriverMinorVersion() {
        return this.driverMinorVersion;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.DatabaseInfo#getDriverName()
     */
    @Override
	public String getDriverName() {
        return this.driverName;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.DatabaseInfo#getDriverVersion()
     */
    @Override
	public String getDriverVersion() {
        return this.driverVersion;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.DatabaseInfo#getDatabaseURL()
     */
    @Override
	public String getDatabaseURL() {
        return this.databaseURL;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.DatabaseInfo#getUserName()
     */
    @Override
	public String getUserName() {
        return this.userName;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.jdbc.DatabaseInfo#isReadOnly()
     */
    @Override
	public boolean isReadOnly() {
        return this.readOnly;
    }

    /**
     * @param string
     */
    public void setDatabaseURL(String url) {
        this.databaseURL = url;
    }

    /**
     * @param string
     */
    public void setDriverName(String name) {
        this.driverName = name;
    }

    /**
     * @param string
     */
    public void setDriverVersion(String version) {
        this.driverVersion = version;
    }

    /**
     * @param string
     */
    public void setDriverMajorVersion(int majorVersion) {
        this.driverMajorVersion = majorVersion;
    }

    /**
     * @param string
     */
    public void setDriverMinorVersion(int minorVersion) {
        this.driverMinorVersion = minorVersion;
    }

    /**
     * @param string
     */
    public void setProductName(String name) {
        this.productName = name;
    }

    /**
     * @param string
     */
    public void setProductVersion(String version) {
        this.productVersion = version;
    }

    /**
     * @param b
     */
    public void setReadOnly(boolean readonly) {
        this.readOnly = readonly;
    }

    /**
     * @param string
     */
    public void setUserName(String username) {
        this.userName = username;
    }

}
