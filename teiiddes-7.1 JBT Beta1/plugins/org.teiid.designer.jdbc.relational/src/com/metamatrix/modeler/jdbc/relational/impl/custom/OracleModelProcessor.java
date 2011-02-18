/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl.custom;

import java.sql.Types;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcTable;
import com.metamatrix.modeler.jdbc.relational.impl.Context;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * OracleModelProcessor
 */
public class OracleModelProcessor extends RelationalModelProcessorImpl {

    private static final String BLOB_TYPE_NAME = "BLOB"; //$NON-NLS-1$
    private static final String CLOB_TYPE_NAME = "CLOB"; //$NON-NLS-1$
    private static final String VARCHAR2_TYPE_NAME = "VARCHAR2"; //$NON-NLS-1$
    private static final String NVARCHAR2_TYPE_NAME = "NVARCHAR2"; //$NON-NLS-1$
    private static final String TIMESTAMP_TYPE_NAME = "TIMESTAMP("; //$NON-NLS-1$
    private static final String NUMBER_TYPE_NAME = "NUMBER"; //$NON-NLS-1$
    private static final String REF_CURSOR = "REF CURSOR"; //$NON-NLS-1$

    /**
     * Construct an instance of OracleModelProcessor.
     */
    public OracleModelProcessor() {
        super();
    }

    /**
     * Construct an instance of OracleModelProcessor.
     * 
     * @param factory
     */
    public OracleModelProcessor( final RelationalFactory factory ) {
        super(factory);
    }

    /**
     * Construct an instance of OracleModelProcessor.
     * 
     * @param factory
     */
    public OracleModelProcessor( final RelationalFactory factory,
                                 final RelationalTypeMapping mapping ) {
        super(factory, mapping);
    }

    /**
     * Determine whether the following information from the result set of a call to
     * {@link DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
     * represents a column in a result set for the procedure.
     * <p>
     * This implementation returns true if <code>columnType == DatabaseMetaData.procedureColumnResult</code> or
     * <code>"REF CURSOR".equals(typeName)</code>.
     * </p>
     * 
     * @param columnType the short indicating what the metadata describes; should be one of
     *        {@link DatabaseMetaData#procedureColumnUnknown}, {@link DatabaseMetaData#procedureColumnIn},
     *        {@link DatabaseMetaData#procedureColumnInOut}, {@link DatabaseMetaData#procedureColumnOut},
     *        {@link DatabaseMetaData#procedureColumnInReturn}, or {@link DatabaseMetaData#procedureColumnResult}.
     * @param type the {@link Types JDBC type} of the column
     * @param typeName the DBMS-specific name of the column type
     * @return true if the information designates a column in a result set for the procedure, or false otherwise.
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#isProcedureResultColumn(short, short,
     *      java.lang.String)
     */
    @Override
    protected boolean isProcedureResultColumn( short columnType,
                                               short type,
                                               String typeName ) {
        return REF_CURSOR.equals(typeName) || super.isProcedureResultColumn(columnType, type, typeName);
    }

    /**
     * If a record from the call to
     * {@link DatabaseMetaData#getProcedureColumns(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     * DatabaseMetaData.getProcedureColumns(...)} was determined by {@link #isProcedureResultColumn(short, short, String)} to be a
     * column for a result set (i.e., the method returns true), then determine whether the column should be added to the
     * {@link ProcedureResult}.
     * <p>
     * Some JDBC drivers return a procedure column that represents the result set but no columns in the result set itself. In such
     * cases, this method should return false.
     * </p>
     * <p>
     * This method implementation always returns false, since Oracle does not ever return columns in the result set.
     * </p>
     * 
     * @param columnType the short indicating what the metadata describes; should be one of
     *        {@link DatabaseMetaData#procedureColumnUnknown}, {@link DatabaseMetaData#procedureColumnIn},
     *        {@link DatabaseMetaData#procedureColumnInOut}, {@link DatabaseMetaData#procedureColumnOut},
     *        {@link DatabaseMetaData#procedureColumnInReturn}, or {@link DatabaseMetaData#procedureColumnResult}.
     * @param type the {@link Types JDBC type} of the column
     * @param typeName the DBMS-specific name of the column type
     * @return true if the information designates a column <i>in</i> a result set for the procedure, or false if the column
     *         actually <i>represents</i> the result set and thus should not be considered a column in the {@link ProcedureResult}
     *         .
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#includeColumnInProcedureResult(short, short,
     *      java.lang.String)
     */
    @Override
    protected boolean includeColumnInProcedureResult( short columnType,
                                                      short type,
                                                      String typeName ) {
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
        // If the type is NUMERIC and precision is non-zero, then look at the length of the column ...
        // (assume zero length means the length isn't known)
        if (precision != 0) {
            if (NUMBER_TYPE_NAME.equalsIgnoreCase(typeName) || REF_CURSOR.equalsIgnoreCase(typeName)) {
                result = findType(precision, scale, problems);
            }
        }
        if (result != null) {
            return result;
        }

        // Oracle 9i introduced the "timestamp" type name (with type=1111, or OTHER)
        if (typeName.startsWith(TIMESTAMP_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.TIMESTAMP, problems);
        }
        if (result != null) {
            return result;
        }

        return super.findType(jdbcType, typeName, length, precision, scale, problems);
    }

    /**
     * Overrides the method to find a type simply by name. This method converts some Oracle-specific (non-numeric) types to
     * standard names, and then simply delegates to the superclass. Find the datatype by name.
     * 
     * @param jdbcTypeName the name of the JDBC (or DBMS) type
     * @param problems the list if {@link IStatus} into which problems and warnings are to be placed; never null
     * @return the datatype that is able to represent data with the supplied criteria, or null if no datatype could be found
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#findType(java.lang.String, java.util.List)
     */
    @Override
    protected EObject findType( final String jdbcTypeName,
                                final List problems ) {
        String standardName = jdbcTypeName;
        if (VARCHAR2_TYPE_NAME.equalsIgnoreCase(jdbcTypeName) || NVARCHAR2_TYPE_NAME.equalsIgnoreCase(jdbcTypeName)) {
            standardName = RelationalTypeMapping.SQL_TYPE_NAMES.VARCHAR;
        }
        return super.findType(standardName, problems);
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
        // If the type of the column is BLOB, then set the length to 0 since the value from the driver
        // does not represent the length of the BLOB
        if (type == Types.BLOB || type == Types.CLOB || BLOB_TYPE_NAME.equals(typeName) || CLOB_TYPE_NAME.equals(typeName)) {
            column.setLength(0);
        }
    }

    /**
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#getMaxSetSizeForModel()
     */
    protected int getMaxSetSizeForModel() {
        return 1000;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#updateModelAnnotation(com.metamatrix.metamodels.core.ModelAnnotation)
     */
    @Override
    protected void updateModelAnnotation( final ModelAnnotation modelAnnotation ) {
        super.updateModelAnnotation(modelAnnotation);
        modelAnnotation.setMaxSetSize(1000);
    }

    /**
     * Oracle doesn't need to do both imported and exported FKs. This is an optimization.
     * 
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#checkExportedForeignKeysIfNoImportedForeignKeysFound()
     * @since 4.2
     */
    @Override
    protected boolean checkExportedForeignKeysIfNoImportedForeignKeysFound() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#isFixedLength(int, java.lang.String)
     * @since 4.2
     */
    @Override
    protected boolean isFixedLength( final int type,
                                     final String typeName ) {
        if (NVARCHAR2_TYPE_NAME.equalsIgnoreCase(typeName)) {
            return false;
        }
        return super.isFixedLength(type, typeName);
    }

    /**
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#computeNameInSource(com.metamatrix.metamodels.relational.RelationalEntity,
     *      java.lang.String, com.metamatrix.modeler.jdbc.metadata.JdbcNode, com.metamatrix.modeler.jdbc.relational.impl.Context,
     *      boolean)
     * @since 4.2
     */
    @Override
    protected String computeNameInSource( final RelationalEntity object,
                                          final String name,
                                          final JdbcNode node,
                                          final Context context,
                                          final boolean forced ) {

        String nis = super.computeNameInSource(object, name, node, context, forced);
        if( nis != null ) {
        	nis = nis.replace('@', '.');
        }
        return nis;
    }

}
