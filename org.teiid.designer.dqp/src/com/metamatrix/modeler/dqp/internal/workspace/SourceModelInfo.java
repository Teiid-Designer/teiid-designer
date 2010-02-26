/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.workspace;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.teiid.designer.runtime.Connector;
import org.teiid.designer.runtime.Server;
import com.metamatrix.common.util.ByteArrayHelper;
import com.metamatrix.common.vdb.api.ModelInfo;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.vdb.runtime.URIResource;

/**
 * This class is simpler than BasicModelInfo and is targeted for visibility and use outside of a VDB.
 * 
 * @since 5.0
 */
public class SourceModelInfo implements ModelInfo, Serializable {

    private static final long serialVersionUID = -5880868141249826062L;

    private static final URIResource uriresource = new URIResource();

    private String name;
    private String uuid;
    private String version;
    private Date versionDate;
    private String versionedBy;
    private String description;
    private boolean isPhysical = true;
    private boolean requireConnBinding;
    private String containerPath;

    private final Map<Server, Set<Connector>> connectorByServerMap = new HashMap<Server, Set<Connector>>();
    private boolean multiSourceBindingEnabled;

    private int modelType = ModelType.PHYSICAL;
    private String uri = "http://www.metamatrix.com/metamodels/Relational"; //$NON-NLS-1$

    private boolean isVisible = true;

    private Map<String, byte[]> ddlFileNamesToFiles = Collections.emptyMap();

    public SourceModelInfo( String modelName ) {
        this.name = modelName;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.common.vdb.api.ModelInfo#getUUID()
     */
    public String getUUID() {
        return uuid;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.common.vdb.api.ModelInfo#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see com.metamatrix.common.vdb.api.ModelInfo#getDescription()
     * @since 4.2
     */
    public String getDescription() {
        return description;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.common.vdb.api.ModelInfo#getVersion()
     */
    public String getVersion() {
        return version;
    }

    public int getModelType() {
        return this.modelType;
    }

    public String getModelTypeName() {
        return ModelType.get(this.modelType).getName();
    }

    public String getModelURI() {
        return this.uri;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.common.vdb.api.ModelInfo#getDateVersioned()
     */
    public Date getDateVersioned() {
        return versionDate;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.common.vdb.api.ModelInfo#getVersionedBy()
     */
    public String getVersionedBy() {
        return versionedBy;
    }

    /**
     * @see com.metamatrix.common.vdb.api.ModelInfo#isPhysical()
     */
    public boolean isPhysical() {
        return isPhysical;
    }

    /* (non-Javadoc)
    * @see com.metamatrix.common.vdb.api.ModelInfo#requiresConnectorBinding()
    */
    public boolean requiresConnectorBinding() {
        return requireConnBinding;
    }
    
    public Set<Connector> getConnectors() {
        Set<Connector> allConnectors = new HashSet<Connector>();
        
        for (Server server : this.connectorByServerMap.keySet()) {
            allConnectors.addAll(this.connectorByServerMap.get(server));
        }
        
        return allConnectors;
    }

    /**
     * @see com.metamatrix.common.vdb.api.ModelInfo#getConnectorBindingNames()
     * @throws UnsupportedOperationException when called
     * @see #getConnectors()
     */
    public List<String> getConnectorBindingNames() {
        throw new UnsupportedOperationException(); // TODO fill in i18n message here
    }

    /**
     * Returns true if the model, based on its model type, supports mutliple connector bindings. If true, {@see
     * #isMultiSourceBindingEnabled()} to determine if the model has been flagged so that the user can actually assign multi
     * connector bindngs.
     * 
     * @see com.metamatrix.common.vdb.api.ModelInfo#supportsMultiSourceBindings()
     * @since 4.2
     */
    public boolean supportsMultiSourceBindings() {
        switch (modelType) {
            case ModelType.PHYSICAL: {
                return true;
            }
            default: {
                return false;
            }
        }

    }

    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @param uuid
     */
    public void setUuid( String uuid ) {
        this.uuid = uuid;
    }

    /**
     * @param version
     */
    public void setVersion( String version ) {
        this.version = version;
    }

    /**
     * @param date
     */
    public void setVersionDate( Date date ) {
        versionDate = date;
    }

    /**
     * @param versionedBy
     */
    public void setVersionedBy( String versionedBy ) {
        this.versionedBy = versionedBy;
    }

    public boolean isVisible() {
        if (this.uri == null) {
            return false;
        }
        return isVisible;
    }

    /**
     * Check whether this model is a materialization of a virtual group.
     * 
     * @return Returns the isMaterialization.
     * @since 4.2
     */
    public boolean isMaterialization() {
        return (this.modelType == ModelType.MATERIALIZATION);
    }

    /**
     * @see com.metamatrix.common.vdb.api.ModelInfo#getDDLFileContentsGetBytes(java.lang.String)
     * @since 4.2
     */
    public byte[] getDDLFileContentsGetBytes( String ddlFileName ) {
        byte[] ddlFile = null;
        if (this.modelType == ModelType.MATERIALIZATION) {
            ddlFile = this.ddlFileNamesToFiles.get(ddlFileName);
        }
        return ddlFile;
    }

    /**
     * @see com.metamatrix.common.vdb.api.ModelInfo#getDDLFileContentsAsStream(java.lang.String)
     * @since 4.2
     */
    public InputStream getDDLFileContentsAsStream( String ddlFileName ) {
        if (this.modelType == ModelType.MATERIALIZATION) {
            byte[] ddlFile = this.ddlFileNamesToFiles.get(ddlFileName);
            if (ddlFile == null) {
                return null;
            }
            InputStream fileStream;
            try {
                fileStream = ByteArrayHelper.toInputStream(ddlFile);
            } catch (Exception err) {
                return null;
            }
            return fileStream;
        }

        return null;
    }

    /**
     * @see com.metamatrix.common.vdb.api.ModelInfo#getDDLFileNames()
     * @since 4.2
     */
    public String[] getDDLFileNames() {
        if (this.modelType == ModelType.MATERIALIZATION) {
            Set<String> keys = this.ddlFileNamesToFiles.keySet();
            String[] ddlFileNames = new String[keys.size()];
            Iterator<String> fileNameItr = keys.iterator();
            for (int i = 0; fileNameItr.hasNext(); i++) {
                String afileName = fileNameItr.next();
                ddlFileNames[i] = afileName;
            }
            return ddlFileNames;
        }
        return new String[] {};
    }

    /**
     * @see com.metamatrix.common.vdb.api.ModelInfo#setDDLFiles(Map)
     * @param ddlFileNamesToFiles
     * @since 4.2
     */
    public void setDDLFiles( Map ddlFileNamesToFiles ) {
        this.ddlFileNamesToFiles = ddlFileNamesToFiles;
    }

    public void setIsVisible( boolean visibility ) {
        this.isVisible = visibility;
    }

    public void setVisibility( short visibility ) {
        if (visibility == PUBLIC) {
            setIsVisible(true);
        } else {
            setIsVisible(false);
        }
    }

    public short getVisibility() {
        if (isVisible()) {
            return PUBLIC;
        }
        return PRIVATE;
    }

    boolean isConnectorBindingUsed( Connector connector ) {
        Server server = connector.getType().getAdmin().getServer();

        if (this.connectorByServerMap.containsKey(server)) {
            return this.connectorByServerMap.get(server).contains(connector);
        }
        
        return false;
    }

    public void addConnector( Connector connector ) {
        Server server = connector.getType().getAdmin().getServer();

        if (!this.connectorByServerMap.containsKey(server)) {
            this.connectorByServerMap.put(server, new HashSet<Connector>());
        }

        this.connectorByServerMap.get(server).add(connector);
    }

    public void removeConnector( Connector connector ) {
        Server server = connector.getType().getAdmin().getServer();
        Set<Connector> connectors = this.connectorByServerMap.get(server);

        if (connectors != null) {
            connectors.remove(connector);

            if (connectors.isEmpty()) {
                this.connectorByServerMap.remove(server);
            }
        }
    }

    public void setModelType( int type ) {
        this.modelType = type;

        setTypeLogic();
    }

    public void setModelURI( String uri ) {
        this.uri = uri;
        setTypeLogic();
    }

    public void setDescription( String desc ) {
        this.description = desc;
    }

    private void setTypeLogic() {
        if (this.modelType == ModelType.PHYSICAL || this.modelType == ModelType.MATERIALIZATION) {
            setIsPhysical(true);

            if (this.uri != null && this.uri.trim().length() > 0) {
                if (uriresource.isPhysicalBindingAllowed(uri)) {
                    setRequireConnectorBinding(true);
                } else {
                    setRequireConnectorBinding(false);
                }
            } else {
                setRequireConnectorBinding(false);

            }

        } else {

            setIsPhysical(false);
            setRequireConnectorBinding(false);

        }

    }

    private void setRequireConnectorBinding( boolean requireConnBinding ) {
        this.requireConnBinding = requireConnBinding;
    }

    private void setIsPhysical( boolean isPhysical ) {
        this.isPhysical = isPhysical;
    }

    /**
     * @see com.metamatrix.common.vdb.api.ModelInfo#enableMutliSourceBindings(boolean)
     * @since 4.2
     */
    public void enableMutliSourceBindings( boolean isEnabled ) {
        this.multiSourceBindingEnabled = isEnabled;
    }

    /**
     * @see com.metamatrix.common.vdb.api.ModelInfo#isMultiSourceBindingEnabled()
     * @since 4.2
     */

    public boolean isMultiSourceBindingEnabled() {
        return this.multiSourceBindingEnabled;
    }

    public void setContainerPath( String path ) {
        this.containerPath = path;
    }

    public String getContainerPath() {
        return this.containerPath;
    }

    public String getPath() {
        return this.containerPath;
    }

    @Override
    public String toString() {
        StringBuffer sw = new StringBuffer();

        sw.append("SourceModelInfo: " + this.getName());//$NON-NLS-1$
        sw.append("\n\tFolder: " + this.getContainerPath());//$NON-NLS-1$
        sw.append("\n\tVersion: " + this.getVersion());//$NON-NLS-1$    
        sw.append("\n\tTypeCode: " + this.getModelType());//$NON-NLS-1$

        sw.append("\n\tType: " + this.getModelTypeName());//$NON-NLS-1$
        sw.append("\n\thasBindings: " + (this.getConnectorBindingNames().size() > 0));//$NON-NLS-1$

        sw.append("\n\tIsVisible: " + this.isVisible());//$NON-NLS-1$
        sw.append("\n\tIsPhysical: " + this.isPhysical());//$NON-NLS-1$
        sw.append("\n\tIsMaterialization: " + this.isMaterialization());//$NON-NLS-1$
        sw.append("\n\tURI: " + this.getModelURI());//$NON-NLS-1$
        sw.append("\n\tRequiresBinding: " + this.requiresConnectorBinding());//$NON-NLS-1$

        return sw.toString();

    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.common.vdb.api.ModelInfo#getProperties()
     */
    @Override
    public Properties getProperties() {
        return null;
    }

}
