/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.model.RelationalReferenceFactory;

/**
 *
 */
public class DdlImporterManager {

    private final RelationalReferenceFactory objFactory = RelationalReferenceFactory.INSTANCE;

    private ModelType modelType;

    private String modelName;

    private ModelResource model;

    private IProgressMonitor monitor;

    private List<String> messages;

    private boolean optToCreateModelEntitiesForUnsupportedDdl;

    private boolean optToSetModelEntityDescription;

    private DdlNodeImporter nodeImporter;

    /**
     * Set the target ModelResource
     * @param model the ModelResource
     */
    public void setRelationalModel(ModelResource model) {
        this.model = model;
    }
    
    /**
     * Get the target model
     * @return the target model
     */
    public ModelResource getModel() {
    	return this.model;
    }

    /**
     * @param modelType the modelType to set
     */
    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    /**
     * @return the modelType
     */
    public ModelType getModelType() {
        return this.modelType;
    }

    /**
     * @param modelName the modelName to set
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * @return the modelName
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * @param monitor the progress monitor
     */
    public void setProgressMonitor(IProgressMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Get the progress monitor
     *
     * @return {@link IProgressMonitor}
     */
    public IProgressMonitor getProgressMonitor() {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }

        return monitor;
    }

    /**
     * @param messages progress messages
     */
    public void setProgressMessages(List<String> messages) {
        this.messages = messages;
    }

    /**
     * Get the progress monitor
     *
     * @return {@link IProgressMonitor}
     */
    public List<String> getProgressMessages() {
        if (messages == null) {
            messages = new ArrayList<String>();
        }

        return messages;
    }

    /**
     * @return the factory
     */
    public RelationalReferenceFactory getObjectFactory() {
        return objFactory;
    }

    /**
     * @return the optToCreateModelEntitiesForUnsupportedDdl
     */
    public boolean optToCreateModelEntitiesForUnsupportedDdl() {
        return optToCreateModelEntitiesForUnsupportedDdl;
    }

    /**
     * @param optToCreateModelEntitiesForUnsupportedDdl option for create entities for unsupported ddl
     */
    public void setOptToCreateModelEntitiesForUnsupportedDdl(boolean optToCreateModelEntitiesForUnsupportedDdl) {
        this.optToCreateModelEntitiesForUnsupportedDdl = optToCreateModelEntitiesForUnsupportedDdl;
    }

    /**
     * @return the optToSetModelEntityDescription
     */
    public boolean optToSetModelEntityDescription() {
        return optToSetModelEntityDescription;
    }

    /**
     * @param optToSetModelEntityDescription option for set model entity descriptions
     */
    public void setOptToSetModelEntityDescription(boolean optToSetModelEntityDescription) {
        this.optToSetModelEntityDescription = optToSetModelEntityDescription;
    }

    /**
     * @param nodeImporter the node importer
     */
    public void setNodeImporter(DdlNodeImporter nodeImporter) {
        this.nodeImporter = nodeImporter;
    }

    /**
     * @return the nodeImporter
     */
    public DdlNodeImporter getNodeImporter() {
        return this.nodeImporter;
    }
}
