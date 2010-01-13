/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.genericimport.ui.wizards;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.metamodels.builder.MetamodelEntityBuilder;
import com.metamatrix.metamodels.builder.ModelBuilder;
import com.metamatrix.metamodels.builder.execution.MetamodelBuilderConstants;
import com.metamatrix.metamodels.builder.execution.processor.EntityProcessor;
import com.metamatrix.metamodels.builder.execution.processor.ModelProcessor;
import com.metamatrix.metamodels.builder.processor.Processor;
import com.metamatrix.metamodels.internal.builder.execution.MetamodelEntityBuilderImpl;
import com.metamatrix.metamodels.internal.builder.execution.ModelBuilderImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.refactor.OrganizeImportCommand;
import com.metamatrix.modeler.core.refactor.OrganizeImportHandler;
import com.metamatrix.modeler.tools.genericimport.ui.UiConstants;
import com.metamatrix.modeler.tools.genericimport.ui.util.GenericImportUtil;

/**
 * GenericImportProcessor - this processor controls the order in which the schemas are processed for the generic importer.
 */
public class GenericImportProcessor implements Processor {

    Connection sqlConnection;
    String modelName;
    IContainer targetLocation;
    String targetLocationStr;
    ResourceSet resourceSet;
    private MultiStatus status;
    private List allResources;

    private static final String DELIM = "."; //$NON-NLS-1$

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(GenericImportProcessor.class);

    // Possible schemas within the model
    private static final String EXTMODELS_SCHEMA = "ExtModels"; //$NON-NLS-1$
    private static final String MODELS_SCHEMA = "Models"; //$NON-NLS-1$
    private static final String RELATIONAL_SCHEMA = "Relational"; //$NON-NLS-1$
    private static final String EXTENSIONS_SCHEMA = "Extensions"; //$NON-NLS-1$
    private static final String RELATIONSHIP_SCHEMA = "Relationship"; //$NON-NLS-1$
    private static final String XSD_SCHEMA = "XSD"; //$NON-NLS-1$
    private static final String UML_SCHEMA = "UML"; //$NON-NLS-1$
    // Processing order for the schemas
    private static final String[] PROCESS_ORDER = new String[] {EXTMODELS_SCHEMA, EXTENSIONS_SCHEMA, MODELS_SCHEMA,
        RELATIONAL_SCHEMA, RELATIONSHIP_SCHEMA, XSD_SCHEMA, UML_SCHEMA};

    private static String getString( final String id ) {
        return UiConstants.Util.getString(I18N_PREFIX + id);
    }

    /* 
     * Contructor
     * @param sqlConnection the SQL connection 
     * @param modelName the name of the model to execute queries against
     * @param targetLocation the location where any generated models will be placed
     * @param status the MultiStatus object for recording warnings and errors
     */
    GenericImportProcessor( Connection sqlConnection,
                            String modelName,
                            IContainer targetLocation,
                            MultiStatus status ) {
        this.sqlConnection = sqlConnection;
        this.modelName = modelName;
        this.targetLocation = targetLocation;
        this.status = status;

        IPath targetPath = targetLocation.getLocation();
        targetLocationStr = targetPath.toOSString();
        try {
            this.resourceSet = ModelerCore.getModelContainer();
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ArgCheck.isNotNull(sqlConnection);
        ArgCheck.isNotNull(modelName);
        ArgCheck.isNotNull(targetLocation);
    }

    /* (non-Javadoc)
     * @see com.metamatrix.metamodels.builder.processor.Processor#getRecordCount(org.eclipse.core.runtime.IStatus)
     */
    public int getRecordCount() {
        int rowCount = 0;
        for (int i = 0; i < PROCESS_ORDER.length; i++) {
            int schemaCount = getSchemaRecordCount(PROCESS_ORDER[i]);
            if (schemaCount > 0) {
                rowCount += schemaCount;
            }
        }
        return rowCount;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.metamodels.builder.processor.Processor#process(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus process( IProgressMonitor monitor ) {
        // Get the total number of records to process
        monitor.beginTask(getString("progressTitle"), 1); //$NON-NLS-1$
        monitor.subTask("Calculating total number of entities... "); //$NON-NLS-1$
        int totalRecords = getRecordCount();
        monitor.worked(1);

        // Total work is 2*numberOfRecords. Work is counted for (1) creating record and
        // (2) creating the entity from the record
        monitor.beginTask(getString("progressTitle"), totalRecords * 2); //$NON-NLS-1$
        this.allResources = new ArrayList();

        for (int i = 0; i < PROCESS_ORDER.length; i++) {
            process(PROCESS_ORDER[i], monitor);
            if (monitor != null && monitor.isCanceled()) {
                break;
            }
            // If an error was encountered, stop processing
            if (status.getSeverity() == IStatus.ERROR) {
                break;
            }
        }

        Iterator resrcIter = this.allResources.iterator();
        while (resrcIter.hasNext()) {
            Resource resource = (Resource)resrcIter.next();
            try {
                rebuildImports(resource);
                resource.save(Collections.EMPTY_MAP);
            } catch (IOException e) {
            }
        }
        return new Status(IStatus.OK, UiConstants.PLUGIN_ID, 1, "Import Complete", null); //$NON-NLS-1$
    }

    /* 
     * get the record count for the specified schema
     * @param schemaType the name of the schema
     * @return the number of records obtained by querying all tables for this supplied schema.
     */
    private int getSchemaRecordCount( String schemaType ) {
        int modelType = getModelTypeConstant(schemaType);
        String modelAndSchema = this.modelName + DELIM + schemaType;
        int schemaRecordCount = 0;
        // Check for special ExtModels and Models schemas
        if (MODELS_SCHEMA.equals(schemaType) || EXTMODELS_SCHEMA.equals(schemaType)) {
            ModelBuilder modelBuilder = new ModelBuilderImpl(this.status, this.resourceSet);
            ModelProcessor modelProcessor = new ModelProcessor(this.sqlConnection, modelBuilder, modelAndSchema,
                                                               this.resourceSet, this.targetLocationStr, this.status);
            schemaRecordCount = modelProcessor.getRecordCount();
            // If modelType was determined, check its record count
        } else if (modelType != MetamodelBuilderConstants.UNKNOWN_MODEL) {
            MetamodelEntityBuilder entityBuilder = new MetamodelEntityBuilderImpl(this.status, this.resourceSet);
            EntityProcessor entityProcessor = new EntityProcessor(this.sqlConnection, entityBuilder, modelType, modelAndSchema,
                                                                  this.status);
            schemaRecordCount = entityProcessor.getRecordCount();
        }
        return schemaRecordCount;
    }

    /* 
     * process the specified schema
     * @param schemaType the name of the schema
     * @param monitor the progress monitor
     */
    private void process( String schemaType,
                          IProgressMonitor monitor ) {
        int modelType = getModelTypeConstant(schemaType);

        String modelAndSchema = this.modelName + DELIM + schemaType;

        // Check for special ExtModels and Models schemas
        if (MODELS_SCHEMA.equals(schemaType) || EXTMODELS_SCHEMA.equals(schemaType)) {
            ModelBuilder modelBuilder = new ModelBuilderImpl(this.status, this.resourceSet);
            ModelProcessor modelProcessor = new ModelProcessor(this.sqlConnection, modelBuilder, modelAndSchema,
                                                               this.resourceSet, this.targetLocationStr, this.status);
            modelProcessor.process(monitor);
            if (this.status.getSeverity() < IStatus.ERROR) {
                List createdResources = modelProcessor.getCreatedResources();
                if (createdResources != null) {
                    // Save the created models
                    Iterator iter = createdResources.iterator();
                    while (iter.hasNext()) {
                        Resource resource = (Resource)iter.next();
                        this.allResources.add(resource);
                        try {
                            resource.save(Collections.EMPTY_MAP);
                        } catch (IOException e) {
                            String message = "Attempt to save the models failed "; //$NON-NLS-1$
                            GenericImportUtil.addStatus(this.status, IStatus.ERROR, message, e);
                        }
                    }
                }
                // Refresh the container to bring models into workspace
                try {
                    this.targetLocation.refreshLocal(IResource.DEPTH_INFINITE, null);
                } catch (CoreException e) {
                    String message = "Attempt to refresh the workspace failed "; //$NON-NLS-1$
                    GenericImportUtil.addStatus(this.status, IStatus.ERROR, message, e);
                }
            }
            // If modelType was determined, process the entities
        } else if (modelType != MetamodelBuilderConstants.UNKNOWN_MODEL) {
            MetamodelEntityBuilder entityBuilder = new MetamodelEntityBuilderImpl(this.status, this.resourceSet);
            EntityProcessor entityProcessor = new EntityProcessor(this.sqlConnection, entityBuilder, modelType, modelAndSchema,
                                                                  this.status);
            entityProcessor.process(monitor);
        }
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#rebuildModelImports()
     */
    public void rebuildImports( Resource resource ) {
        final OrganizeImportHandler handler = new OrganizeImportHandler() {
            public Object choose( List options ) {
                return null;
            }
        };
        final OrganizeImportCommand command = new OrganizeImportCommand();
        command.setResource(resource);
        command.setHandler(handler);
        command.execute(null);
    }

    /**
     * Get the modelType constant for the provided schemaType string
     * 
     * @param schemaType the schema string
     * @return the modelType for the provided schema
     */
    private int getModelTypeConstant( String schemaType ) {
        int modelType = MetamodelBuilderConstants.UNKNOWN_MODEL;
        if (RELATIONAL_SCHEMA.equalsIgnoreCase(schemaType)) {
            modelType = MetamodelBuilderConstants.RELATIONAL_MODEL;
        } else if (EXTENSIONS_SCHEMA.equalsIgnoreCase(schemaType)) {
            modelType = MetamodelBuilderConstants.EXTENSION_MODEL;
        } else if (RELATIONSHIP_SCHEMA.equalsIgnoreCase(schemaType)) {
            modelType = MetamodelBuilderConstants.RELATIONSHIP_MODEL;
        } else if (XSD_SCHEMA.equalsIgnoreCase(schemaType)) {
            modelType = MetamodelBuilderConstants.XSD_MODEL;
        } else if (UML_SCHEMA.equalsIgnoreCase(schemaType)) {
            modelType = MetamodelBuilderConstants.UML_MODEL;
        }
        return modelType;
    }

}
