/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl.custom;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.jdbc.JdbcException;
import com.metamatrix.modeler.jdbc.data.Request;
import com.metamatrix.modeler.jdbc.data.Results;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.metadata.impl.GetAccessForeignKeysRequest;
import com.metamatrix.modeler.jdbc.metadata.impl.GetIndexesRequest;
import com.metamatrix.modeler.jdbc.relational.ModelerJdbcRelationalConstants;
import com.metamatrix.modeler.jdbc.relational.impl.Context;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * @since 4.3
 */
public class ExcelModelProcessor extends RelationalModelProcessorImpl {
    /**
     * Construct an instance of ExcelModelProcessor.
     */
    public ExcelModelProcessor() {
        super();
    }

    /**
     * Construct an instance of ExcelModelProcessor.
     * 
     * @param factory
     */
    public ExcelModelProcessor( RelationalFactory factory ) {
        super(factory);
    }

    /**
     * Construct an instance of ExcelModelProcessor.
     * 
     * @param factory
     * @param mapping
     */
    public ExcelModelProcessor( RelationalFactory factory,
                                RelationalTypeMapping mapping ) {
        super(factory, mapping);
    }

    @Override
    protected String computeNameInSource( final RelationalEntity object,
                                          final String name,
                                          final JdbcNode node,
                                          final Context context,
                                          final boolean forced, 
                                          List problems) {
        if (object instanceof Table && node instanceof JdbcTable && "Excel_Sheet".equals(((JdbcTable)node).getRemarks())) {//$NON-NLS-1$
            return "[" + node.getName() + "$]";//$NON-NLS-1$         //$NON-NLS-2$ 
        }
        return super.computeNameInSource(object, name, node, context, forced, problems);
    }

    @Override
    protected void createPrimaryKey( final JdbcTable tableNode,
                                     final Table table,
                                     final Context context,
                                     final List problems ) {
        // Can't add a primary key to a view ...
        if (!(table instanceof BaseTable)) {
            return; // don't create anything
        }

        // Skip if no primary keys are to be imported ...
        final boolean includeUniqueIndexes = context.getJdbcImportSettings().isIncludeUniqueIndexes();
        final boolean includeForeignKeys = context.getJdbcImportSettings().isIncludeForeignKeys();
        if (!includeUniqueIndexes && !includeForeignKeys) {
            return; // skip if no unique indexes AND no foreign keys are included
        }

        try {
            // Get the column information ...
            final Request request = tableNode.getRequest(GetIndexesRequest.NAME, false);
            final Results results = request.getResults();
            final Object[] rows = results.getRows();
            final int numRows = results.getRowCount();

            if (numRows == 0) {
                return;
            }

            // Build a map of the columns by their name so they can be found much more quickly ...
            final Map columnsByName = createColumnMapKeyedByNames(table);

            // Go through the results
            boolean hasPrimaryKey = false;
            String pkName = null;
            final Column[] columns = new Column[numRows];
            for (int i = 0; i < numRows; ++i) {
                final Object row = rows[i];
                pkName = results.getString(row, 5);
                if (pkName == null || !pkName.equalsIgnoreCase("PrimaryKey")) { //$NON-NLS-1$
                    continue;
                }
                String columnName = results.getString(row, 8); // COLUMN_NAME

                // Convert the column name to match the form stored in the 'name in source'.
                columnName = tableNode.getUnqualifiedName(columnName);
                final Column column = (Column)columnsByName.get(columnName);
                if (column != null) {
                    // Find the column in the table ...
                    final short seqIndex = results.getShort(row, 7); // KEY_SEQ
                    columns[seqIndex - 1] = column;
                }
                hasPrimaryKey = true;

            }

            if (!hasPrimaryKey) {
                return;
            }

            // Create the primary key
            final PrimaryKey primaryKey = super.getFactory().createPrimaryKey();
            // Set the reference to the table ...
            primaryKey.setTable((BaseTable)table);

            if (pkName != null) {
                setNameAndNameInSource(primaryKey, pkName, tableNode, context, problems);
            }
            // Add the columns in the correct order
            final List keyColumns = primaryKey.getColumns();
            for (int i = 0; i < columns.length; ++i) {
                final Column column = columns[i];
                if (column != null) {
                    keyColumns.add(column);
                }
            }

        } catch (JdbcException e) {
            final String msg = com.metamatrix.modeler.internal.jdbc.relational.ModelerJdbcRelationalConstants.Util.getString("RelationalModelProcessorImpl.Error_while_obtaining_primary_key_info") + e.getLocalizedMessage(); //$NON-NLS-1$
            final IStatus status = new Status(IStatus.ERROR, ModelerJdbcRelationalConstants.PLUGIN_ID, 0, msg, e);
            problems.add(status);
        }
    }

    @Override
    protected Request getForeignKeyRequest( final JdbcTable tableNode,
                                            final String name,
                                            final boolean includeMetadata ) throws JdbcException {
        return tableNode.getRequest(GetAccessForeignKeysRequest.NAME, false);
    }

    @Override
    protected boolean checkExportedForeignKeysIfNoImportedForeignKeysFound() {
        return false;
    }

    @Override
    protected void modifyIndexName( IndexSpec indexSpec ) {
        if (indexSpec.indexName != null && indexSpec.indexName.equalsIgnoreCase("PrimaryKey")) { //$NON-NLS-1$
            String indexName = null;
            Iterator iter = indexSpec.columns.iterator();
            while (iter.hasNext()) {
                Column column = (Column)iter.next();
                if (indexName == null) {
                    indexName = column.getName();
                } else {
                    indexName += column.getName();
                }
            }
            indexSpec.indexName = indexName;
        }
    }
}
