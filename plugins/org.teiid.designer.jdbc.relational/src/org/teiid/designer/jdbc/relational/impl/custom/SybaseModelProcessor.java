/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational.impl.custom;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.jdbc.JdbcException;
import org.teiid.designer.jdbc.JdbcImportSettings;
import org.teiid.designer.jdbc.metadata.JdbcNode;
import org.teiid.designer.jdbc.metadata.JdbcTable;
import org.teiid.designer.jdbc.relational.impl.Context;
import org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.RelationalEntity;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.Table;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMapping;


/**
 * SybaseModelProcessor
 *
 * @since 8.0
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
     * @see org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl#updateModelAnnotation(org.teiid.designer.metamodels.core.ModelAnnotation)
     */
    @Override
    protected void updateModelAnnotation( final ModelAnnotation modelAnnotation ) {
        super.updateModelAnnotation(modelAnnotation);
        modelAnnotation.setMaxSetSize(250);
    }

    /**
     * @see org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl#createIndexes(org.teiid.designer.jdbc.metadata.JdbcTable,
     *      org.teiid.designer.metamodels.relational.Table, org.teiid.designer.jdbc.relational.impl.Context, java.util.List)
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
                if (pkName == null || pkName.trim().length() == 0) {
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
                            setNameAndNameInSource(pk, indexName, tableNode, context, problems);
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
     * @see org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl#checkExportedForeignKeysIfNoImportedForeignKeysFound()
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
     * @see org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl#setColumnInfo(org.teiid.designer.metamodels.relational.Column,
     *      org.teiid.designer.jdbc.metadata.JdbcTable, org.teiid.designer.jdbc.relational.impl.Context, java.util.List,
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
    
    /* (non-Javadoc)
     * @see org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl#computeNameInSource(org.teiid.designer.metamodels.relational.RelationalEntity, java.lang.String, org.teiid.designer.jdbc.metadata.JdbcNode, org.teiid.designer.jdbc.relational.impl.Context, boolean, java.util.List)
     */
    @Override
	protected String computeNameInSource(RelationalEntity object, String name,
			JdbcNode node, Context context, boolean forced, List problems) {

		/*
		 * sybase with the jconn driver does not return schemas from
		 * DatabaseMetaData.getSchemas, but it requires the fqn to be in the
		 * form <database>.<owner>.<table> and it does return the <owner> part as
		 * the SCHEMA value in DatabaseMetaData.getTables
		 * 
		 * In case we are unable to obtain the correct name in source, we do not fail,
		 * but let the super implementation handle it and just show a warning.
		 * 
		 */

		final JdbcImportSettings settings = context.getJdbcImportSettings();
		boolean includeCatalogs = settings.isCreateCatalogsInModel();

		if (node instanceof JdbcTable && includeCatalogs) {
			JdbcTable tableEntity = (JdbcTable) node;
			try {
				DatabaseMetaData databaseMetaData = context.getJdbcDatabase()
						.getDatabaseMetaData();
				JdbcNode parentDatabase = tableEntity.getParentDatabaseObject(
						true, false);
				ResultSet tables = databaseMetaData.getTables(
						parentDatabase.getName(), null, name, null);

				final StringBuffer sb = new StringBuffer();
				String quoteString = databaseMetaData
						.getIdentifierQuoteString();

				if (tables.next()) {
					sb.append(quoteString);
					sb.append(tables.getString("TABLE_CAT")); //$NON-NLS-1$
					sb.append(quoteString);
					sb.append(databaseMetaData.getCatalogSeparator());
					sb.append(quoteString);
					sb.append(tables.getString("TABLE_SCHEM")); //$NON-NLS-1$
					sb.append(quoteString);
					sb.append(databaseMetaData.getCatalogSeparator());
					sb.append(quoteString);
					sb.append(tables.getString("TABLE_NAME")); //$NON-NLS-1$
					sb.append(quoteString);
				}

				if (tables.next()) {
					// multiple tables with same name but different owners
					addNameInSourceWarning(name, context, problems, null);
				}
				
				return sb.toString();
				
			} catch (JdbcException ex) {
				addNameInSourceWarning(name, context, problems, ex);
			} catch (SQLException ex) {
				addNameInSourceWarning(name, context, problems, ex);
			}
		}
		return super.computeNameInSource(object, name, node, context, forced,
				problems);
	}

	/**
	 * @param context
	 * @param problems
	 * @param ex
	 */
	private void addNameInSourceWarning(String name, Context context,
			List problems, Exception ex) {
		final String msg = org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Util
				.getString(
						"RelationalModelProcessorImpl.Error_while_obtaining_name_in_source", //$NON-NLS-1$
						context.getJdbcDatabase().getName())
				+ (ex == null ? "" : ex.getLocalizedMessage()); //$NON-NLS-1$
		final IStatus status = new Status(
				IStatus.WARNING,
				org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.PLUGIN_ID,
				0, msg, ex);
		problems.add(status);
	}
    
}
