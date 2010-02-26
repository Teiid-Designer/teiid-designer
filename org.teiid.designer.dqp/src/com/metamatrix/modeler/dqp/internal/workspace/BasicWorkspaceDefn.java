/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.dqp.internal.workspace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import org.teiid.designer.runtime.Connector;

/**
 * This class maintains the basic structural contents of the WorkspaceConfig's information. This includes the ModelInfo
 * information pertaining to mappings to connector bindings.
 * 
 * @since 5.0
 */
public class BasicWorkspaceDefn implements Serializable {

    /**
     */
    private static final long serialVersionUID = 1L;

    private Properties headerProperties;

    private String name;

    private Map modelInfos = Collections.synchronizedMap(new HashMap());

    /**
     * @since 5.0
     */
    public BasicWorkspaceDefn() {
        super();
    }

    public Properties getHeaderProperties() {
        return headerProperties;
    }

    public void setHeaderProperties( Properties props ) {
        this.headerProperties = props;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.metadata.runtime.api.VDBInfo#getName()
     */
    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.metamatrix.metadata.runtime.api.VDBInfo#getModels()
     */
    public Collection<SourceModelInfo> getModels() {
        if (modelInfos == null || modelInfos.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        Collection models = new ArrayList(modelInfos.size());
        models.addAll(modelInfos.values());
        return models;
    }

    /**
     * Returns the {@link ModelInfo ModelInfo} for the name specified
     * 
     * @return ModelInfo
     */
    public SourceModelInfo getModel( String name ) {
        return (SourceModelInfo)modelInfos.get(name);
    }

    // /**
    // * Clears all model infos and adds all new input models
    // *
    // * @param modelInfos
    // */
    // public void setModelInfos( final Collection models ) {
    // modelInfos.clear();
    // addModelInfos(models);
    // }
    //
    // /**
    // * Adds input models
    // *
    // * @param modelInfos
    // */
    // public void addModelInfos( final Collection models ) {
    //
    // for (Iterator it = models.iterator(); it.hasNext();) {
    // SourceModelInfo mi = (SourceModelInfo)it.next();
    // modelInfos.put(mi.getName(), mi);
    // }
    // }

    /**
     * Adds SourceModelInfo to the map.
     * 
     * @param model
     * @since 5.0
     */
    public void addModelInfo( SourceModelInfo model ) {
        if (model == null) {
            return;
        }
        // first remove an existing model,
        // model names are assumed unique
        removeModelInfo(model.getName());
        this.modelInfos.put(model.getName(), model);
    }

    /**
     * Removes the model info matching the input name from the map
     * 
     * @param modelName
     * @return
     * @since 5.0
     */
    public SourceModelInfo removeModelInfo( String modelName ) {
        if (modelName == null || modelName.length() == 0) {
            return null;
        }
        if (this.modelInfos != null) {
            return (SourceModelInfo)modelInfos.remove(modelName);
        }
        return null;
    }

    // public void renameModelInfo(String modelName,
    // String newModelName) {
    // if (modelName == null || modelName.length() == 0) {
    // return;
    // }
    // if (this.modelInfos != null) {
    // SourceModelInfo smi = (SourceModelInfo)modelInfos.remove(modelName);
    // if (smi != null) {
    // smi.setName(newModelName);
    // addModelInfo(smi);
    // }
    // }
    // }

    /**
     * Returns all models which are bound to a ConnectorBinding given the input name.
     * 
     * @param connectorBindingName
     * @return Collection
     * @since 5.0
     */
    public Collection getModelsForConnector( Connector connector ) {
        Collection matchingModelInfos = new ArrayList();
        Collection allModelInfos = new ArrayList(modelInfos.values());
        for (Iterator iter = allModelInfos.iterator(); iter.hasNext();) {
            SourceModelInfo nextModelInfo = (SourceModelInfo)iter.next();
            if (isModelBound(connector, nextModelInfo)) {
                matchingModelInfos.add(nextModelInfo);
            }
        }
        return matchingModelInfos;
    }

    public void removeModelInfosForProject( String projectName ) {
        Collection allModelInfos = new ArrayList(modelInfos.values());
        for (Iterator iter = allModelInfos.iterator(); iter.hasNext();) {
            SourceModelInfo nextModelInfo = (SourceModelInfo)iter.next();
            if (nextModelInfo.getContainerPath().startsWith(projectName)) {
                removeModelInfo(nextModelInfo.getName());
            }
        }
    }

    /**
     * returns whether or not a model, <code>SourceModelInfo</code>, is bound to a connector.
     * 
     * @param the connector
     * @param modelInfo
     * @return
     * @since 5.0
     */
    public boolean isModelBound( Connector connector,
                                 SourceModelInfo modelInfo ) {
        return modelInfo.getConnectors().contains(connector);
    }
}
