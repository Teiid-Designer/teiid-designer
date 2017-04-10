/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational.impl.custom;

import java.sql.Types;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.types.DatatypeConstants;
import org.teiid.designer.jdbc.metadata.JdbcTable;
import org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Processors;
import org.teiid.designer.jdbc.relational.impl.Context;
import org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.util.RelationalTypeMapping;


/**
 * SqlServerModelProcessor
 *
 * @since 8.0
 */
public class SqlServerModelProcessor extends RelationalModelProcessorImpl {
    private static final String TEXT_TYPE_NAME = "TEXT"; //$NON-NLS-1$
    private static final String IMAGE_TYPE_NAME = "IMAGE"; //$NON-NLS-1$
    private static final String NUMERIC_TYPE_NAME = "NUMERIC"; //$NON-NLS-1$
    
    // NOTE from MS SQLServer 2000 Doc:
    //
    // "The IDENTITY property can be assigned to tinyint, smallint, int, bigint, decimal(p,0), or numeric(p,0) columns."
    //
    private static final String IDENTITY_STR = "identity"; //$NON-NLS-1$
    
    /**
     * Construct an instance of SqlServerModelProcessor.
     * 
     */
    public SqlServerModelProcessor() {
        super();
    }

    /**
     * Construct an instance of SqlServerModelProcessor.
     * @param factory
     */
    public SqlServerModelProcessor(RelationalFactory factory) {
        super(factory);
    }

    /**
     * Construct an instance of SqlServerModelProcessor.
     * @param factory
     * @param mapping
     */
    public SqlServerModelProcessor(RelationalFactory factory, RelationalTypeMapping mapping) {
        super(factory, mapping);
    }

    /**
     * @see org.teiid.designer.jdbc.relational.impl.RelationalModelProcessorImpl#updateModelAnnotation(org.teiid.designer.metamodels.core.ModelAnnotation)
     */
    @Override
    protected void updateModelAnnotation(final ModelAnnotation modelAnnotation) {
        super.updateModelAnnotation(modelAnnotation);
        modelAnnotation.setMaxSetSize(16000);
    }

    /** 
     * SQL Server doesn't need to do both imported and exported FKs.  This is an optimization.
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
     * name) for other types.int identity
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
        // String make sure the name is trimmed
        String trimmedTypeName = typeName.trim();

        if (trimmedTypeName.toUpperCase().startsWith(TEXT_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.CLOB, problems);
        } else if (trimmedTypeName.toUpperCase().startsWith(IMAGE_TYPE_NAME)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.BLOB, problems);
        } else if( trimmedTypeName.endsWith(IDENTITY_STR) ) {
        	int identIndex = trimmedTypeName.indexOf(IDENTITY_STR);
        	String realType = trimmedTypeName.substring(0, identIndex).trim();
        	result = findBuiltinType(realType, problems);
        }
        if (result != null) {
            return result;
        }

        return super.findType(jdbcType, typeName, length, precision, scale, problems);
    }
    
    /**
     * @return True if the specified type should be considered fixed-length.
     * @since 4.2
     */
    @Override
    protected boolean isFixedLength( final int type,
                                     final String typeName ) {
    	if( typeName.toUpperCase().startsWith(IMAGE_TYPE_NAME) ) return false;
    	
        return super.isFixedLength(type, typeName);
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
        
        // SQL Server may return a type name as "int indentity" which really means it's an "int" where autoincrement == TRUE
        // String make sure the name is trimmed
        String trimmedTypeName = typeName.trim();
        if( trimmedTypeName.endsWith(IDENTITY_STR) ) {
        	int identIndex = trimmedTypeName.indexOf(IDENTITY_STR);
        	String realType = trimmedTypeName.substring(0, identIndex).trim();
        	column.setAutoIncremented(true);
        	column.setNativeType(realType);
        }
        
        if( NUMERIC_TYPE_NAME.equalsIgnoreCase(typeName) ) {
        	if( columnSize > 0 ) {
        		column.setPrecision(columnSize);
        	}
        }
    }
    
	@Override
	public String getType() {
		return Processors.SQLSERVER;
	}
}
