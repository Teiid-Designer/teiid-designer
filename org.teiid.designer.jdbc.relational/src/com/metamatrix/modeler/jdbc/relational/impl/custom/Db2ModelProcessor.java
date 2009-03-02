/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl.custom;

import java.util.List;
import java.util.Map;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * SqlServerModelProcessor
 */
public class Db2ModelProcessor extends RelationalModelProcessorImpl {

    /**
     * Construct an instance of SqlServerModelProcessor.
     * 
     */
    public Db2ModelProcessor() {
        super();
    }

    /**
     * Construct an instance of SqlServerModelProcessor.
     * @param factory
     */
    public Db2ModelProcessor(RelationalFactory factory) {
        super(factory);
    }

    /**
     * Construct an instance of SqlServerModelProcessor.
     * @param factory
     * @param mapping
     */
    public Db2ModelProcessor(RelationalFactory factory, RelationalTypeMapping mapping) {
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
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#findUniqueKey(com.metamatrix.modeler.jdbc.metadata.JdbcDatabase, java.util.Map, java.lang.String, java.lang.String, java.lang.String, java.util.List)
     * @since 4.2
     */
    @Override
    protected UniqueKey findUniqueKey(JdbcDatabase dbNode,
                                      Map nodesToModelObjects,
                                      String catalogName,
                                      String schemaName,
                                      String tableName,
                                      List columnNames) {
        // Per defect 13227, the getImportedKeys and getExportedKeys returns a non-null value in the
        // 'catalog' column, even though DatabaseMetaData returns 'false' for supportsCatalogs.
        // Therefore, as a workaround, ignore the catalog name when supportsCatalogs is false ...
        return super.findUniqueKey(dbNode, nodesToModelObjects, catalogName, schemaName, tableName, columnNames);
    }

    /** 
     * DB2 doesn't need to do both imported and exported FKs.  This is an optimization.
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#checkExportedForeignKeysIfNoImportedForeignKeysFound()
     * @since 4.2
     */
    @Override
    protected boolean checkExportedForeignKeysIfNoImportedForeignKeysFound() {
        return false;
    }
}
