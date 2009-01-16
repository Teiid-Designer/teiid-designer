/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
