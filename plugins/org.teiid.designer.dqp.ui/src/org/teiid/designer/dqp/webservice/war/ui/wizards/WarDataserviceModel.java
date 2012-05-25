/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war.ui.wizards;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.teiid.designer.dqp.webservice.war.WebArchiveBuilderConstants;

/**
 * @since 7.1
 */
public class WarDataserviceModel {

    private String warFilename;
    private String licenseFilename;
    private String contextName;
    private String hostName;
    private String port;
    private String vdbLocation;
    private String tns;
    private String jndiName;
    private String securityType;
    private String securityRealm;
    private String securityRole;
    private String securityUsername;
    private String securityPassword;
    private boolean useMtom;

    private String warFilenameDefault;
    private String hostNameDefault;
    private String portDefault;
    private String contextNameDefault;
    private String tnsDefault;
    private String jndiNameDefault;
    private String securityTypeDefault;
    private String securityRealmDefault;
    private String securityRoleDefault;
    private String securityUsernameDefault;
    private String securityPasswordDefault;
    private boolean useMtomDefault;

    private IFile theVdb;

    private boolean isReadyForFinish = false;
    private static WarDataserviceModel dataserviceModel;

    /**
     * @since 7.1
     */
    private WarDataserviceModel() {
    }

    /**
     * @return DataserviceModel
     * @since 7.1
     */
    public static WarDataserviceModel getInstance() {
        if (dataserviceModel == null) {
            dataserviceModel = new WarDataserviceModel();
        }
        return dataserviceModel;
    }

    /**
     * @return Returns the contextName.
     * @since 7.1
     */
    public String getContextName() {
        return this.contextName;
    }

    /**
     * @param contextName The contextName to set.
     * @since 7.1
     */
    public void setContextName( String contextName ) {
        this.contextName = contextName;
    }

    /**
     * @return Returns the licenseFilename.
     * @since 7.1
     */
    public String getLicenseFileLocation() {
        return this.licenseFilename;
    }

    /**
     * @param namspace The default.
     * @since 7.1
     */
    public void setLicenseFileLocation( String licenseFilename ) {
        this.licenseFilename = licenseFilename;
    }

    /**
     * @return Returns the warFilename.
     * @since 7.1
     */
    public String getWarFileLocation() {
        return this.warFilename;
    }

    /**
     * @param warFilename The warFilename to set.
     * @since 7.1
     */
    public void setWarFileLocation( String warFilename ) {
        this.warFilename = warFilename;
    }

    /**
     * @return Returns the vdbLocation.
     * @since 7.1
     */
    public String getVdbLocation() {
        return this.vdbLocation;
    }

    /**
     * @return Returns the tns.
     * @since 7.1
     */
    public String getTns() {
        return this.tns;
    }

    /**
     * @return Returns the jndiName.
     * @since 7.1
     */
    public String getJndiName() {
        return this.jndiName;
    }

    /**
     * @return Returns securityType value.
     * @since 7.1
     */
    public String getSecurityType() {
        return this.securityType;
    }

    /**
     * @return Returns securityRealm value.
     * @since 7.1
     */
    public String getSecurityRealm() {
        return this.securityRealm;
    }

    /**
     * @return Returns securityRole value.
     * @since 7.1
     */
    public String getSecurityRole() {
        return this.securityRole;
    }

    /**
     * @param vdbLocation The vdbLocation to set.
     * @since 7.1
     */
    public void setVdbLocation( String vdbLocation ) {
        this.vdbLocation = vdbLocation;
    }

    /**
     * @param contextNameDefault The contextNameDefault to set.
     * @since 7.1
     */
    public void setContextNameDefault( String contextNameDefault ) {
        this.contextNameDefault = contextNameDefault;
        this.contextName = contextNameDefault;

    }

    /**
     * @param jndiNameDefault The jndiNameDefault to set.
     * @since 7.1
     */
    public void setJndiNameDefault( String jndiNameDefault ) {
        this.jndiNameDefault = jndiNameDefault;
        this.jndiName = jndiNameDefault;

    }

    /**
     * @param securityType The SecurityType to set.
     * @since 7.1.1
     */
    public void setSecurityTypeDefault( String securityTypeDefault ) {
        this.securityTypeDefault = securityTypeDefault;
        this.securityType = securityTypeDefault;

    }

    /**
     * @param securityRealmDefault The securityRealmDefault to set.
     * @since 7.1.1
     */
    public void setSecurityRealmDefault( String securityRealmDefault ) {
        this.securityRealmDefault = securityRealmDefault;
        this.securityRealm = securityRealmDefault;

    }

    /**
     * @param securityRoleDefault The securityRoleDefault to set.
     * @since 7.1.1
     */
    public void setSecurityRoleDefault( String securityRoleDefault ) {
        this.securityRoleDefault = securityRoleDefault;
        this.securityRole = securityRoleDefault;

    }

    /**
     * @param securityRealm The SecurityRealm to set.
     * @since 7.1.1
     */
    public void setSecurityRealm( String securityRealm ) {
        this.securityRealm = securityRealm;
    }

    /**
     * @param securityRole The SecurityRole to set.
     * @since 7.1.1
     */
    public void setSecurityRole( String securityRole ) {
        this.securityRole = securityRole;

    }

    /**
     * @param jndiName The jndiName to set.
     * @since 7.1
     */
    public void setJndiName( String jndiName ) {
        this.jndiName = jndiName;
    }

    /**
     * @param tnsDefault The tnsDefault to set.
     * @since 7.1
     */
    public void setTnsDefault( String tnsDefault ) {
        this.tnsDefault = tnsDefault;
        this.tns = tnsDefault;

    }

    /**
     * @param tns The tns to set.
     * @since 7.1
     */
    public void setTns( String tns ) {
        this.tns = tns;
    }

    /**
     * @param hostNameDefault The hostNameDefault to set.
     * @since 7.1
     */
    public void setHostNameDefault( String hostNameDefault ) {
        this.hostNameDefault = hostNameDefault;
        this.hostName = hostNameDefault;
    }

    /**
     * @param portDefault The portDefault to set.
     * @since 7.1
     */
    public void setPortDefault( String portDefault ) {
        this.portDefault = portDefault;
        this.port = portDefault;
    }

    /**
     * @param warFilenameDefault The warFilenameDefault to set.
     * @since 7.1
     */
    public void setWarFilenameDefault( String warFilenameDefault ) {
        this.warFilenameDefault = warFilenameDefault;
        this.warFilename = warFilenameDefault;
    }

    /**
     * @return hostNameDefault
     */
    public String getHostNameDefault() {
        return hostNameDefault;
    }

    /**
     * @return portDefault
     */
    public String getPortDefault() {
        return portDefault;
    }

    /**
     * @return Returns the contextNameDefault.
     * @since 7.1
     */
    public String getContextNameDefault() {
        return this.contextNameDefault;
    }

    /**
     * @return Returns the warFilenameDefault.
     * @since 7.1
     */
    public String getWarFilenameDefault() {
        return this.warFilenameDefault;
    }

    /**
     * @return Returns the securityTypeDefault.
     * @since 7.1
     */
    public String getSecurityTypeDefault() {
        return this.securityTypeDefault;
    }

    /**
     * @return Returns the securityRealmDefault.
     * @since 7.1
     */
    public String getSecurityRealmDefault() {
        return this.securityRealmDefault;
    }

    /**
     * @return Returns the securityRoleDefault.
     * @since 7.1
     */
    public String getSecurityRoleDefault() {
        return this.securityRoleDefault;
    }

    /**
     * @return Returns the JNDINameDefault.
     * @since 7.1
     */
    public String getJndiNameDefault() {
        return this.jndiNameDefault;
    }

    /**
     * @return Returns the tnsDefaultDefault.
     * @since 7.1
     */
    public String getTnsDefault() {
        return this.tnsDefault;
    }

    /**
     * @return boolean isReadyForFinish
     * @since 7.1
     */
    public boolean isReadyForFinish() {
        return this.isReadyForFinish;
    }

    /**
     * @param boolean isReadyForFinish
     * @since 7.1
     */
    public void setIsReadyForFinish( boolean isReadyForFinish ) {
        this.isReadyForFinish = isReadyForFinish;
    }

    /**
     * @param theVdb
     * @since 7.1
     */
    public void setVdbFile( IFile theVdb ) {
        this.theVdb = theVdb;
    }

    /**
     * @return
     * @since 7.1
     */
    public IFile getVdbFile() {
        return this.theVdb;
    }

    /**
     * @param hostName Sets hostName to the specified value.
     */
    public void setHostName( String hostName ) {
        this.hostName = hostName;
    }

    /**
     * @return hostName
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param port Sets port to the specified value.
     */
    public void setPort( String port ) {
        this.port = port;
    }

    /**
     * @return port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param securityUsername the securityUsername to set
     * @since 7.1.1
     */
    public void setSecurityUsername( String securityUsername ) {
        this.securityUsername = securityUsername;
    }

    /**
     * @return the securityUsername
     * @since 7.1.1
     */
    public String getSecurityUsername() {
        return securityUsername;
    }

    /**
     * @param securityPassword the securityPassword to set
     * @since 7.1.1
     */
    public void setSecurityPassword( String securityPassword ) {
        this.securityPassword = securityPassword;
    }

    /**
     * @return the securityPassword
     * @since 7.1.1
     */
    public String getSecurityPassword() {
        return securityPassword;
    }

    /**
     * @param securityUsernameDefault the securityUsernameDefault to set
     * @since 7.1.1
     */
    public void setSecurityUsernameDefault( String securityUsernameDefault ) {
        this.securityUsernameDefault = securityUsernameDefault;
        this.securityUsername = securityUsernameDefault;
    }

    /**
     * @return the securityUsernameDefault
     * @since 7.1.1
     */
    public String getSecurityUsernameDefault() {
        return securityUsernameDefault;
    }

    /**
     * @param securityPasswordDefault the securityPasswordDefault to set
     * @since 7.1.1
     */
    public void setSecurityPasswordDefault( String securityPasswordDefault ) {
        this.securityPasswordDefault = securityPasswordDefault;
        this.securityPassword = securityPasswordDefault;
    }

    /**
     * @return the securityPasswordDefault
     * @since 7.1.1
     */
    public String getSecurityPasswordDefault() {
        return securityPasswordDefault;
    }

    /**
     * @param useMtomDefault Sets useMtomDefault to the specified value.
     */
    public void setUseMtomDefault( boolean useMtomDefault ) {
        this.useMtomDefault = useMtomDefault;
    }

    /**
     * @return useMtomDefault
     */
    public boolean getUseMtomDefault() {
        return useMtomDefault;
    }

    /**
     * @param useMtom Sets useMtom to the specified value.
     */
    public void setUseMtom( boolean useMtom ) {
        this.useMtom = useMtom;
    }

    /**
     * @return useMtom
     */
    public boolean getUseMtom() {
        return useMtom;
    }

    /**
     * @return
     * @since 7.1
     */
    public Properties getProperties() {
        Properties properties = new Properties();

        properties.put(WebArchiveBuilderConstants.PROPERTY_WAR_FILE_SAVE_LOCATION, this.getWarFileLocation());
        properties.put(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME, this.getContextName());
        properties.put(WebArchiveBuilderConstants.PROPERTY_VDB_FILE_NAME, this.getVdbFile().getFullPath().toString()); //getLocation().toOSString());
        properties.put(WebArchiveBuilderConstants.PROPERTY_WAR_HOST, this.getHostName());
        properties.put(WebArchiveBuilderConstants.PROPERTY_WAR_PORT, this.getPort());
        properties.put(WebArchiveBuilderConstants.PROPERTY_WSDL_TNS, this.getTns());
        properties.put(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME, this.getJndiName());
        properties.put(WebArchiveBuilderConstants.PROPERTY_SECURITY_TYPE, this.getSecurityType());
        properties.put(WebArchiveBuilderConstants.PROPERTY_SECURITY_REALM, this.getSecurityRealm());
        properties.put(WebArchiveBuilderConstants.PROPERTY_SECURITY_ROLE, this.getSecurityRole());
        properties.put(WebArchiveBuilderConstants.PROPERTY_SECURITY_USERNAME, this.getSecurityUsername());
        properties.put(WebArchiveBuilderConstants.PROPERTY_SECURITY_PASSWORD, this.getSecurityPassword());
        properties.put(WebArchiveBuilderConstants.PROPERTY_USE_MTOM, this.getUseMtom());

        return properties;
    }

}
