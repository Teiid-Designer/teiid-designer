/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl.custom;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * SqlServerModelProcessor
 */
public class SqlServerModelProcessor extends RelationalModelProcessorImpl {
    private static final String TEXT_TYPE_NAME = "TEXT"; //$NON-NLS-1$
    private static final String IMAGE_TYPE_NAME = "IMAGE"; //$NON-NLS-1$
    
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
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#updateModelAnnotation(com.metamatrix.metamodels.core.ModelAnnotation)
     */
    @Override
    protected void updateModelAnnotation(final ModelAnnotation modelAnnotation) {
        super.updateModelAnnotation(modelAnnotation);
        modelAnnotation.setMaxSetSize(16000);
    }

    /** 
     * SQL Server doesn't need to do both imported and exported FKs.  This is an optimization.
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

}
