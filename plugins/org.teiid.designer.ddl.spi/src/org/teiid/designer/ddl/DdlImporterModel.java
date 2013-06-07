/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.compare.DifferenceProcessor;
import org.teiid.designer.compare.ModelerComparePlugin;
import org.teiid.designer.compare.processor.DifferenceProcessorImpl;
import org.teiid.designer.compare.selector.ModelResourceSelector;
import org.teiid.designer.compare.selector.ModelSelector;
import org.teiid.designer.compare.selector.TransientModelSelector;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.ddl.DdlImporterModel.EntityDescriptions.DescriptionOperation;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.RelationalFactory;

/**
 *
 */
public class DdlImporterModel {

    /**
     * A 1-1 mapping of an entity with a proposed description.
     * Preferable to return a collection of these pair classes than
     * expose a {@link Map}.
     */
    public static class EntityDescriptions {
        
        /**
         * Enum of operations used to add descriptions
         */
        public static enum DescriptionOperation {
            /**
             * Insert at the beginning
             */
            PREPEND,
            
            /**
             * Insert at the end
             */
            APPEND;
        }

        private RelationalEntity entity;

        private List<String> descriptions = new ArrayList<String>();

        /**
         * @param entity 
         * @param description 
         */
        public EntityDescriptions(RelationalEntity entity, String description) {
            CoreArgCheck.isNotNull(entity);
            CoreArgCheck.isNotNull(description);

            this.entity = entity;
            this.descriptions.add(description);
        }

        /**
         * @return the entity
         */
        public RelationalEntity getEntity() {
            return this.entity;
        }

        /**
         * @return the first description in the list, 
         *                  which is considered to have the highest priority
         */
        public String getPreferredDescription() {
            return this.descriptions.get(0);
        }

        /**
         * @return the complete description,
         *                  which is the concatenation of all descriptions
         */
        public String getCompleteDescription() {
            StringBuilder builder = new StringBuilder();
            for (String description : descriptions) {
                builder.append(description);
                builder.append("\n"); //$NON-NLS-1$
            }
            
            return builder.toString();
        }
        /**
         * Add a description to the list of descriptions.
         *
         * Descriptions are in priority order with the preferred description
         * being first in the list. Therefore, the first description is
         * considered to be the preferred description.
         * <p>
         * Use the given {@link DescriptionOperation} to determine whether
         * to prepend or append the given description.
         *
         * @param description
         * @param operation
         */
        public void addDescription(String description, DescriptionOperation operation) {
            if (DescriptionOperation.PREPEND.equals(operation))
                descriptions.add(0, description);
            else
                descriptions.add(description);
        }

        @Override
        public int hashCode() {
            return entity.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            EntityDescriptions other = (EntityDescriptions)obj;
            if (this.entity == null) {
                if (other.entity != null) return false;
            } else if (!this.entity.equals(other.entity)) return false;
            return true;
        }
    }

    private final RelationalFactory factory = RelationalFactory.eINSTANCE;

    private ModelType modelType;

    private String modelName;

    private ModelResource model;

    private ModelResourceSelector startingSelector;

    private TransientModelSelector endingSelector;

    private DifferenceProcessor differenceProcessor;

    private IProgressMonitor monitor;

    private List<String> messages;

    private boolean optToCreateModelEntitiesForUnsupportedDdl;

    private boolean optToSetModelEntityDescription;

    private Collection<EntityDescriptions> descriptionPairs = new ArrayList<EntityDescriptions>();

    private DdlNodeImporter nodeImporter;

    /**
     * @param model
     */
    public void setRelationalModel(ModelResource model) {
        this.model = model;
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
     * @param monitor
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
     * @param messages
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
     * Get the starting selector
     *
     * @return {@link ModelSelector}
     */
    public ModelSelector getStartingSelector() {
        CoreArgCheck.isNotNull(model);

        if (startingSelector == null)
            startingSelector = new ModelResourceSelector(model);
        
        return startingSelector;
    }

    /**
     * Get the ending selector
     *
     * @return {@link ModelSelector}
     *
     * @throws Exception
     */
    public ModelSelector getEndingSelector() throws Exception {
        CoreArgCheck.isNotNull(model);

        if (endingSelector == null) {
            final URI mdlUri = URI.createFileURI(model.getPath().toFile().getAbsolutePath());
            final ModelAnnotation mdlAnnotation = model.getModelAnnotation();
            endingSelector = new TransientModelSelector(mdlUri, mdlAnnotation);
        }

        return endingSelector;
    }

    /**
     * Get the DifferenceProcessor
     *
     * @return the difference processor
     * @throws Exception
     */
    public DifferenceProcessor getDifferenceProcessor() throws Exception {
        if (differenceProcessor == null) {
            differenceProcessor = new DifferenceProcessorImpl(getStartingSelector(), getEndingSelector());
            differenceProcessor.addEObjectMatcherFactories(ModelerComparePlugin.createEObjectMatcherFactories());
        }

        return differenceProcessor;
    }

    /**
     * @return the factory
     */
    public RelationalFactory getFactory() {
        return factory;
    }

    /**
     * @return the optToCreateModelEntitiesForUnsupportedDdl
     */
    public boolean optToCreateModelEntitiesForUnsupportedDdl() {
        return optToCreateModelEntitiesForUnsupportedDdl;
    }

    /**
     * @param optToCreateModelEntitiesForUnsupportedDdl
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
     * @param optToSetModelEntityDescription
     */
    public void setOptToSetModelEntityDescription(boolean optToSetModelEntityDescription) {
        this.optToSetModelEntityDescription = optToSetModelEntityDescription;
    }

    /**
     * @param entity
     * @param description
     * @param operation
     */
    public void addDescription(RelationalEntity entity, String description, DescriptionOperation operation) {
        for (EntityDescriptions pair : descriptionPairs) {
            if (pair.getEntity().equals(entity)) {
                pair.addDescription(description, operation);
                return;
            }
        }

        // Completely new entity description
        descriptionPairs.add(new EntityDescriptions(entity, description));
    }

    /**
     * @return the descriptionPairs
     */
    public Collection<EntityDescriptions> getEntityDescriptions() {
        return this.descriptionPairs;
    }

    /**
     * @param nodeImporter
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
