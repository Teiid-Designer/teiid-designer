/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war.ui.wizards;

import java.util.ArrayList;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.teiid.designer.dqp.webservice.war.WebArchiveBuilderConstants;
import com.metamatrix.modeler.core.workspace.ModelResource;

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

    private String warFilenameDefault;
    private String hostNameDefault;
    private String portDefault;
    private String contextNameDefault;
    private String tnsDefault;
    private String jndiNameDefault;

    private IFile theVdb;
    private ArrayList<ModelResource> wsModelResourceArrayList;

    private boolean isReadyForFinish = false;
    private static WarDataserviceModel dataserviceModel;

    /**
     * @return wsModelResourcearrayList
     */
    public ArrayList<ModelResource> getWsModelResourcearrayList() {
        return wsModelResourceArrayList;
    }

    /**
     * @param wsModelResourcearrayList Sets wsModelResourcearrayList to the specified value.
     */
    public void setWsModelResourcearrayList( ArrayList<ModelResource> wsModelResourcearrayList ) {
        this.wsModelResourceArrayList = wsModelResourcearrayList;
    }

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
        this.tns = tnsDefault;
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
     * @return
     * @since 7.1
     */
    public Properties getProperties() {
        Properties properties = new Properties();

        properties.put(WebArchiveBuilderConstants.PROPERTY_WAR_FILE_SAVE_LOCATION, this.getWarFileLocation());
        properties.put(WebArchiveBuilderConstants.PROPERTY_CONTEXT_NAME, this.getContextName());
        properties.put(WebArchiveBuilderConstants.PROPERTY_VDB_FILE_NAME, this.getVdbFile().getLocation().toOSString());
        properties.put(WebArchiveBuilderConstants.PROPERTY_VDB_WS_MODELS, this.getWsModelResourcearrayList());
        properties.put(WebArchiveBuilderConstants.PROPERTY_WAR_HOST, this.getHostName());
        properties.put(WebArchiveBuilderConstants.PROPERTY_WAR_PORT, this.getPort());
        properties.put(WebArchiveBuilderConstants.PROPERTY_WSDL_TNS, this.getTns());
        properties.put(WebArchiveBuilderConstants.PROPERTY_JNDI_NAME, this.getJndiName());

        return properties;
    }
}
