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

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.relational.impl.Context;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * SybaseModelProcessor
 */
public class SybaseModelProcessor extends RelationalModelProcessorImpl {
    private static final String TEXT_TYPE_NAME = "TEXT"; //$NON-NLS-1$
    private static final String IMAGE_TYPE_NAME = "IMAGE"; //$NON-NLS-1$
    
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
    
    /**
     * Find the type given the supplied information. This method is called by the various <code>create*</code> methods, and is
     * currently implemented to use {@link #findType(int, int, List)} when a numeric type and {@link #findType(String, List)} (by
     * name) for other types.
     * 
     * @param type
     * @param typeName
     * @return
     */
    @Override
    protected EObject findType( final int jdbcType,
                                final String typeName,
                                final int length,
                                final int precision,
                                final int scale,
                                final List problems ) {

        EObject result = null;

        if (typeName.toUpperCase().startsWith(TEXT_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.CLOB, problems);
        } else if (typeName.toUpperCase().startsWith(IMAGE_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.BLOB, problems);
        }
        if (result != null) {
            return result;
        }

        return super.findType(jdbcType, typeName, length, precision, scale, problems);
    }
    
    /**
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#setColumnInfo(com.metamatrix.metamodels.relational.Column,
     *      com.metamatrix.modeler.jdbc.metadata.JdbcTable, com.metamatrix.modeler.jdbc.relational.impl.Context, java.util.List,
     *      java.lang.String, int, java.lang.String, int, int, int, int, java.lang.String, int)
     */
    @Override
    protected void setColumnInfo( final Column column,
                                  final JdbcTable tableNode,
                                  final Context context,
                                  final List problems,
                                  final String name,
                                  final int type,
                                  final String typeName,
                                  final int columnSize,
                                  final int numDecDigits,
                                  final int numPrecRadix,
                                  final int nullable,
                                  final String defaultValue,
                                  final int charOctetLen ) {
        super.setColumnInfo(column,
                            tableNode,
                            context,
                            problems,
                            name,
                            type,
                            typeName,
                            columnSize,
                            numDecDigits,
                            numPrecRadix,
                            nullable,
                            defaultValue,
                            charOctetLen);
        
        // For some reason Sybase is returning a truncated native type name for DOUBLE PRECISION
        // The DDL Exporter requires that this string match a valid native type so users can round-trip
        // the process. We need to intercept it here and set it to the correct value.
        
        if( "double precis".equalsIgnoreCase(typeName) ) { //$NON-NLS-1$
        	column.setNativeType("double precision"); //$NON-NLS-1$
        }
    }
}
