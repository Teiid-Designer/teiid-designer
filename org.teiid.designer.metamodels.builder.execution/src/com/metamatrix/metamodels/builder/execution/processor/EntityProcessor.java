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
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.builder.MetamodelEntityBuilder;
import com.metamatrix.metamodels.builder.MetamodelEntityRecord;
import com.metamatrix.metamodels.builder.execution.MetamodelBuilderConstants;
import com.metamatrix.metamodels.builder.translator.RecordGenerator;
import com.metamatrix.metamodels.builder.translator.ResultSetTranslator;
import com.metamatrix.metamodels.internal.builder.execution.util.MetamodelBuilderUtil;

/**
 * EntityProcessor - executes queries against the provided schema, using the sqlConnection. Tables in the schema are processed in
 * the order provided in MetamodelBuilderConstants for the type of model.
 */
public class EntityProcessor extends AbstractProcessor implements MetamodelBuilderConstants {

    private final MetamodelEntityBuilder entityBuilder;
    private final String[] processingOrder;
    private final MultiStatus status;

    public EntityProcessor( Connection sqlConnection,
                            MetamodelEntityBuilder entityBuilder,
                            int modelType,
                            String modelAndSchemaName,
                            MultiStatus status ) {
        super(sqlConnection, modelAndSchemaName);
        ArgCheck.isNotNull(status);
        ArgCheck.isNotNull(entityBuilder);
        this.status = status;
        this.entityBuilder = entityBuilder;
        if (modelType == RELATIONAL_MODEL) {
            this.processingOrder = RELATIONAL_PROCESSING_ORDER;
        } else if (modelType == EXTENSION_MODEL) {
            this.processingOrder = EXTENSIONS_PROCESSING_ORDER;
        } else {
            this.processingOrder = new String[] {};
        }
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
            for (int i = 0; i < processingOrder.length; i++) {
                if (tableNames.contains(processingOrder[i])) {
                    int tableRows = countTableRows(processingOrder[i]);
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
            monitor.subTask("Processing Entities for " + this.modelAndSchemaName); //$NON-NLS-1$
        }

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
            return status;
        }

        // ----------------------------------------------
        // Process Tables in the specified order
        // ----------------------------------------------
        if (tableNames != null) {
            for (int i = 0; i < processingOrder.length; i++) {
                if (tableNames.contains(processingOrder[i])) {
                    processTable(processingOrder[i], monitor);
                    if (status.getSeverity() == IStatus.ERROR) {
                        return status;
                    }
                }
            }
        }

        if (monitor != null && monitor.isCanceled()) {
            final String msg = "Entity Processing Cancelled"; //$NON-NLS-1$
            MetamodelBuilderUtil.addStatus(status, IStatus.CANCEL, msg);
            return status;
        }
        return status;
    }

    private void processTable( String tableName,
                               IProgressMonitor monitor ) {
        if (isValidTable(tableName)) {
            ResultSet rs = null;
            try {
                rs = executeTableQuery(tableName);
                List entityRecords = RecordGenerator.generateEntityRecords(rs, this.status, monitor);
                // Iterate for progress monitor
                Iterator iter = entityRecords.iterator();
                while (iter.hasNext()) {
                    this.entityBuilder.create((MetamodelEntityRecord)iter.next(), monitor);
                }
                rs.close();
            } catch (SQLException e) {
                final String msg = "Error Processing Table " + this.modelAndSchemaName + DELIM + tableName; //$NON-NLS-1$
                MetamodelBuilderUtil.addStatus(status, IStatus.ERROR, msg, e);
                return;
            }
            return;
        }
        final String msg = "Table not found: " + this.modelAndSchemaName + DELIM + tableName; //$NON-NLS-1$
        MetamodelBuilderUtil.addStatus(status, IStatus.WARNING, msg);
    }

    private int countTableRows( String tableName ) {
        int tableRows = 0;
        if (isValidTable(tableName)) {
            ResultSet rs = null;
            try {
                rs = executeTableQuery(tableName);
                int rsRowCount = ResultSetTranslator.getRowCount(rs);
                if (rsRowCount > 0) {
                    tableRows += rsRowCount;
                }
                rs.close();
            } catch (SQLException e) {
                final String msg = "Error doing rowCount on table " + this.modelAndSchemaName + DELIM + tableName; //$NON-NLS-1$
                MetamodelBuilderUtil.addStatus(status, IStatus.WARNING, msg, e);
                return tableRows;
            }
            return tableRows;
        }
        final String msg = "Table not found when counting rows: " + this.modelAndSchemaName + DELIM + tableName; //$NON-NLS-1$
        MetamodelBuilderUtil.addStatus(status, IStatus.WARNING, msg);
        return tableRows;
    }

    private boolean isValidTable( String tableName ) {
        boolean isValid = false;
        for (int i = 0; i < processingOrder.length; i++) {
            if (processingOrder[i].equalsIgnoreCase(tableName)) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }

}
