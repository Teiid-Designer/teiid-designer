/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.core.xmi;

import java.util.ArrayList;
import java.util.List;
import org.teiid.core.util.StringUtil;

//We also need to bring back our SAX handler "header reader" 
//that reads a stream and extracts the model information 
//(metamodels, primary metamodel, UUID, description, and model type) 
//before closing the stream as soon as the information is found.  
//(Note that this should exit quickly for non-model files.)  We'll have 
//to create an interceptor in the repository component that extracts 
//this information from the attached file and adds it to the request 
//so that the information can subsequently be used in the check-in/add/other operations.  

/**
 * Class that represents the content of an XMI file's header.
 */
public final class XMIHeader {

    private final List<String> namespaceURIs = new ArrayList<String>();
    private final List<ModelImportInfo> modelImportInfos = new ArrayList<ModelImportInfo>();
    private String primaryMetamodelURI;
    private String uuid;
    private String description;
    private String modelType;
    private String modelNamespaceUri;
    private boolean visible;
    private String producerName;
    private String producerVersion;
    private String xmiVersion;

    /**
     * Constructor for XMIHeader.
     */
    public XMIHeader() {
        this.visible = true; // default value for ModelAnnotation property
    }

    public void addModelImportInfo( final ModelImportInfo modelImportInfo ) {
        if (modelImportInfo != null && !this.modelImportInfos.contains(modelImportInfo)) this.modelImportInfos.add(modelImportInfo);
    }

    public void addNamespaceURI( final String uri ) {
        if (!StringUtil.isEmpty(uri) && !this.namespaceURIs.contains(uri)) this.namespaceURIs.add(uri);
    }

    public String getDescription() {
        return this.description;
    }

    public ModelImportInfo[] getModelImportInfos() {
        return modelImportInfos.toArray(new ModelImportInfo[modelImportInfos.size()]);
    }

    public String[] getModelImportLocations() {
        final String[] result = new String[this.modelImportInfos.size()];
        for (int i = 0; i < this.modelImportInfos.size(); i++) {
            final ModelImportInfo info = this.modelImportInfos.get(i);
            result[i] = info.getLocation();
        }
        return result;
    }

    public String[] getModelImportPaths() {
        final String[] result = new String[this.modelImportInfos.size()];
        for (int i = 0; i < this.modelImportInfos.size(); i++) {
            final ModelImportInfo info = this.modelImportInfos.get(i);
            result[i] = info.getPath();
        }
        return result;
    }

    public String getModelNamespaceUri() {
        return this.modelNamespaceUri;
    }

    public String getModelType() {
        return this.modelType;
    }

    public String[] getNamespaceURIs() {
        return namespaceURIs.toArray(new String[namespaceURIs.size()]);
    }

    public String getPrimaryMetamodelURI() {
        return this.primaryMetamodelURI;
    }

    public String getProducerName() {
        return this.producerName;
    }

    public String getProducerVersion() {
        return this.producerVersion;
    }

    /**
     * The UUID associated with the model is the ModelAnnotation UUID unless this is a VDB in which case it is the VirtualDatabase
     * UUID
     */
    public String getUUID() {
        return this.uuid;
    }

    public String getXmiVersion() {
        return this.xmiVersion;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setDescription( final String description ) {
        this.description = description;
    }

    public void setModelNamespaceUri( final String theNamespaceUri ) {
        this.modelNamespaceUri = theNamespaceUri;
    }

    public void setModelType( final String modelType ) {
        this.modelType = modelType;
    }

    public void setPrimaryMetamodelURI( final String uri ) {
        this.primaryMetamodelURI = uri;
    }

    public void setProducerName( final String producerName ) {
        this.producerName = producerName;
    }

    public void setProducerVersion( final String producerVersion ) {
        this.producerVersion = producerVersion;
    }

    public void setUUID( final String uuid ) {
        this.uuid = uuid;
    }

    public void setVisible( final String isVisible ) {
        this.visible = true;
        if (isVisible.toUpperCase().startsWith("F")) this.visible = false; //$NON-NLS-1$
    }

    public void setXmiVersion( final String xmiVersion ) {
        this.xmiVersion = xmiVersion;
    }

    /**
     * Method to print the contents of the XMI Header object.
     * 
     * @param stream the stream
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(100);
        sb.append("XMI Header:"); //$NON-NLS-1$
        sb.append("\n  XMI version:           "); //$NON-NLS-1$
        sb.append(this.getXmiVersion());
        sb.append("\n  UUID:                  "); //$NON-NLS-1$
        sb.append(this.getUUID());
        sb.append("\n  Producer:              "); //$NON-NLS-1$
        sb.append(this.getProducerName());
        sb.append(" "); //$NON-NLS-1$
        sb.append(this.getProducerVersion());
        sb.append("\n  Description:           "); //$NON-NLS-1$
        sb.append(this.getDescription());
        sb.append("\n  Model type:            "); //$NON-NLS-1$
        sb.append(this.getModelType());
        sb.append("\n  Model namespace URI:            "); //$NON-NLS-1$
        sb.append(this.getModelNamespaceUri());
        sb.append("\n  isVisible:             "); //$NON-NLS-1$
        sb.append(this.isVisible());
        sb.append("\n  Primary Metamodel URI: "); //$NON-NLS-1$
        sb.append(this.getPrimaryMetamodelURI());
        sb.append("\n  Namespace URIs:"); //$NON-NLS-1$
        final String[] uris = this.getNamespaceURIs();
        for (final String uri : uris) {
            sb.append("\n    "); //$NON-NLS-1$
            sb.append(uri);
        }
        sb.append("\n  ModelImportInfos:"); //$NON-NLS-1$
        final ModelImportInfo[] infos = this.getModelImportInfos();
        for (final ModelImportInfo info : infos) {
            sb.append("\n    "); //$NON-NLS-1$
            sb.append(info);
        }
        return sb.toString();
    }
}
