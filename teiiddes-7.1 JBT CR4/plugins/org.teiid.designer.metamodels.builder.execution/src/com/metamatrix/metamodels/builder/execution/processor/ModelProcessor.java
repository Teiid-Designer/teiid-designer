/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.builder.execution.processor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.builder.ModelBuilder;
import com.metamatrix.metamodels.builder.translator.RecordGenerator;
import com.metamatrix.metamodels.builder.translator.ResultSetTranslator;
import com.metamatrix.metamodels.internal.builder.execution.util.MetamodelBuilderUtil;

/**
 * ModelsProcessor - executes a query against the provided schema 'ToCreate' table, using the sqlConnection. The results of the
 * query are used to generate model Resources.
 */
public class ModelProcessor extends AbstractProcessor {

    // Possible Tables
    private static final String TOCREATE_TABLE = "ToCreate"; //$NON-NLS-1$
    // Processing order for the Tables
    private static final String[] PROCESSING_ORDER = new String[] {TOCREATE_TABLE};

    private final ModelBuilder modelBuilder;
    private final MultiStatus status;
    private List createdResources;
    private String targetLocationStr;

    /**
     * Processor for handling of the Models schema.
     */
    public ModelProcessor( Connection sqlConnection,
                           ModelBuilder modelBuilder,
                           String modelAndSchemaName,
                           ResourceSet resourceSet,
                           String targetLocStr,
                           MultiStatus status ) {
        super(sqlConnection, modelAndSchemaName);
        CoreArgCheck.isNotNull(resourceSet);
        CoreArgCheck.isNotNull(status);
        this.status = status;
        this.targetLocationStr = targetLocStr;
        this.modelBuilder = modelBuilder;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.metamodels.builder.processor.Processor#getRecordCount(org.eclipse.core.runtime.IStatus)
     */
    public int getRecordCount() {
        int totalRows = 0;

        // ----------------------------------------------
        // Get the available Tables for this schema
        // ----------------------------------------------
        List tableNames = null;
        try {
            tableNames = getSchemaTables();
        } catch (SQLException e) {
            // Log the exception
            final String msg = "Error retrieving the tables for " + this.modelAndSchemaName; //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg, e);
            return totalRows;
        }

        // ----------------------------------------------
        // Count the table rows
        // ----------------------------------------------
        if (tableNames != null) {
            for (int i = 0; i < PROCESSING_ORDER.length; i++) {
                if (tableNames.contains(PROCESSING_ORDER[i])) {
                    int tableRows = countToCreateTableRows();
                    if (tableRows > 0) {
                        totalRows += tableRows;
                    }
                    if (status.getSeverity() == IStatus.ERROR) {
                        return totalRows;
                    }
                }
            }
        }
        return totalRows;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.metamodels.builder.processor.Processor#process(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus process( IProgressMonitor monitor ) {
        if (monitor != null) {
            monitor.subTask("Processing Models"); //$NON-NLS-1$
        }

        // ----------------------------------------------
        // Get the available Model Tables
        // ----------------------------------------------
        List tableNames = null;
        try {
            tableNames = getSchemaTables();
        } catch (SQLException e) {
            // Log the exception
            final String msg = "Error retrieving the Model Table"; //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(this.status, IStatus.ERROR, msg, e);
            return this.status;
        }

        // ----------------------------------------------
        // Process Tables in the specified order
        // ----------------------------------------------
        if (tableNames != null) {
            for (int i = 0; i < PROCESSING_ORDER.length; i++) {
                if (tableNames.contains(PROCESSING_ORDER[i])) {
                    processTable(PROCESSING_ORDER[i], monitor);
                    if (this.status.getSeverity() == IStatus.ERROR) {
                        return this.status;
                    }
                }
            }
        }

        if (monitor != null && monitor.isCanceled()) {
            final String msg = "Model Processing Cancelled"; //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(this.status, IStatus.CANCEL, msg);
            return this.status;
        }
        return this.status;
    }

    /**
     * @return the List of created resources
     */
    public List getCreatedResources() {
        return this.createdResources;
    }

    /**
     * Process the table with the provided name
     * 
     * @param tableName the table name to process
     */
    private void processTable( String tableName,
                               IProgressMonitor monitor ) {
        if (tableName.equalsIgnoreCase(TOCREATE_TABLE)) {
            processToCreateTable(monitor);
            return;
        }
        final String msg = "Model Table not found"; //$NON-NLS-1$
        MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg);
    }

    /**
     * Process the ToCreate table
     */
    private void processToCreateTable( IProgressMonitor monitor ) {
        ResultSet rs = null;
        try {
            rs = executeTableQuery(TOCREATE_TABLE);
            List modelRecords = RecordGenerator.generateModelRecords(rs, targetLocationStr, this.status, null);
            this.createdResources = this.modelBuilder.create(modelRecords);
            if (monitor != null) {
                monitor.worked(modelRecords.size());
            }
            rs.close();
        } catch (SQLException e) {
            final String msg = "Error Processing the ToCreate Table"; //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(this.status, IStatus.ERROR, msg, e);
            return;
        }
    }

    /**
     * Process the ToCreate table
     */
    private int countToCreateTableRows() {
        int tableRows = 0;
        ResultSet rs = null;
        try {
            rs = executeTableQuery(TOCREATE_TABLE);
            int rsRowCount = ResultSetTranslator.getRowCount(rs);
            if (rsRowCount > 0) {
                tableRows += rsRowCount;
            }
            rs.close();
        } catch (SQLException e) {
            final String msg = "Error doing row count on the ToCreate Table"; //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(this.status, IStatus.WARNING, msg, e);
            return tableRows;
        }
        return tableRows;
    }

}
