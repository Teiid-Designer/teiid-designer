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
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.relational.impl.Context;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * SybaseModelProcessor
 */
public class SybaseModelProcessor extends RelationalModelProcessorImpl {

    /**
     * Construct an instance of SybaseModelProcessor.
     */
    public SybaseModelProcessor() {
        super();
    }

    /**
     * Construct an instance of SybaseModelProcessor.
     * 
     * @param factory
     */
    public SybaseModelProcessor( RelationalFactory factory ) {
        super(factory);
    }

    /**
     * Construct an instance of SybaseModelProcessor.
     * 
     * @param factory
     * @param mapping
     */
    public SybaseModelProcessor( RelationalFactory factory,
                                 RelationalTypeMapping mapping ) {
        super(factory, mapping);
    }

    /**
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#updateModelAnnotation(com.metamatrix.metamodels.core.ModelAnnotation)
     */
    @Override
    protected void updateModelAnnotation( final ModelAnnotation modelAnnotation ) {
        super.updateModelAnnotation(modelAnnotation);
        modelAnnotation.setMaxSetSize(250);
    }

    /**
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#createIndexes(com.metamatrix.modeler.jdbc.metadata.JdbcTable,
     *      com.metamatrix.metamodels.relational.Table, com.metamatrix.modeler.jdbc.relational.impl.Context, java.util.List)
     * @since 4.2
     */
    @Override
    protected List createIndexes( final JdbcTable tableNode,
                                  final Table table,
                                  final Context context,
                                  final List problems ) {
        final List indexes = super.createIndexes(tableNode, table, context, problems);

        // Per defect 13228: Primary keys have no names.
        // Since primary keys are created before this method is called, after indexes are created go through them
        // and look for those that match up with the primary key for the table, and if the primary key doesn't have
        // a name and the corresponding index does, assign the same name to the primary key.

        if (table instanceof BaseTable) {
            final BaseTable theTable = (BaseTable)table;

            // Get the primary key of the table ...
            final PrimaryKey pk = theTable.getPrimaryKey();
            if (pk != null) {
                final String pkName = pk.getName();
                if (pkName == null || pkName.trim().length() != 0) {
                    final List pkColumns = pk.getColumns();

                    // Iterate over the indexes for the table ...
                    boolean foundMatchingIndex = false;
                    final Iterator iter = indexes.iterator();
                    while (iter.hasNext()) {
                        final Index index = (Index)iter.next();

                        // See if the index has a name ...
                        final String indexName = index.getName();
                        if (indexName == null || indexName.trim().length() == 0) {
                            // There is no name on the index, so don't even bother ...
                            continue;
                        }

                        // Go through the columns ...
                        final List indexColumns = index.getColumns();
                        if (indexColumns.size() == pkColumns.size()) {
                            // Iterate through the columns to see if the same columns are referenced
                            // (in the same order!) by both the index and the primary key ...
                            final Iterator indexColumnIter = indexColumns.iterator();
                            final Iterator pkColumnIter = pkColumns.iterator();
                            while (pkColumnIter.hasNext() && indexColumnIter.hasNext()) {
                                final Column indexColumn = (Column)indexColumnIter.next();
                                final Column pkColumn = (Column)pkColumnIter.next();
                                if (indexColumn != pkColumn) {
                                    break;
                                }
                            }
                            // All columns matched so far!
                            foundMatchingIndex = true;
                        }

                        if (foundMatchingIndex) {
                            // Set the primary key name to that of the index ...
                            setNameAndNameInSource(pk, indexName, tableNode, context);
                            break;
                        }
                    } // end of while loop over indexes
                } // end of 'if pk name!=null'
            } // end of 'if pk!=null'
        } // end of 'if BaseTable'

        return indexes;
    }

    /**
     * Sybase doesn't need to do both imported and exported FKs. This is an optimization.
     * 
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#checkExportedForeignKeysIfNoImportedForeignKeysFound()
     * @since 4.2
     */
    @Override
    protected boolean checkExportedForeignKeysIfNoImportedForeignKeysFound() {
        return false;
    }
}
