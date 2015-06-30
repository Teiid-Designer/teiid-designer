/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl;

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

    private ImportMessages importMessages;
    
    private boolean optToCreateModelEntitiesForUnsupportedDdl;

    private boolean optToSetModelEntityDescription;

    private boolean optToGenerateDefaultSQL;

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
     * Get the ImportMessages 
     *
     * @return the import messages
     */
    public ImportMessages getImportMessages() {
    	if(this.importMessages==null) {
    		this.importMessages = new ImportMessages();
    	}
    	return this.importMessages;
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
     * @return optToGenerateDefaultSQL
     */
    public boolean optToGenerateDefaultSQL() {
        return optToGenerateDefaultSQL;
    }

    /**
     * @param value
     */
    public void optToGenerateDefaultSQL(boolean value) {
        this.optToGenerateDefaultSQL = value;
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
