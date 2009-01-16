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

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * SqlServerModelProcessor
 */
public class SqlServerModelProcessor extends RelationalModelProcessorImpl {

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


}
