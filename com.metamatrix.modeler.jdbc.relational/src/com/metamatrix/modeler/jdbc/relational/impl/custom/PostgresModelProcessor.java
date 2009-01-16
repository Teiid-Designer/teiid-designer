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

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;

/**
 * OracleModelProcessor
 */
public class PostgresModelProcessor extends RelationalModelProcessorImpl {

    private static final String BOOLEAN_TYPE_NAME   = "BOOL"; //$NON-NLS-1$
//    private static final String BIG_INT_TYPE_NAME   = "INT8"; //$NON-NLS-1$
//    private static final String DBL_TYPE_NAME       = "FLOAT8"; //$NON-NLS-1$
//    private static final String INT_TYPE_NAME       = "INT4"; //$NON-NLS-1$
//    private static final String FLOAT_TYPE_NAME     = "FLOAT4"; //$NON-NLS-1$
//    private static final String SMALL_INT_TYPE_NAME = "INT2"; //$NON-NLS-1$
//    private static final String TIMESTAMP_TYPE_NAME = "TIMESTAMPZ"; //$NON-NLS-1$
    /**
     * Construct an instance of PostgresModelProcessor.
     * 
     */
    public PostgresModelProcessor() {
        super();
    }

    /**
     * Construct an instance of PostgresModelProcessor.
     * @param factory
     */
    public PostgresModelProcessor(final RelationalFactory factory) {
        super(factory);
    }
    
    /**
     * Construct an instance of PostgresModelProcessor.
     * @param factory
     */
    public PostgresModelProcessor(final RelationalFactory factory, final RelationalTypeMapping mapping) {
        super(factory,mapping);
    }
    
    /**
     * Find the type given the supplied information.  This method is called by the
     * various <code>create*</code> methods, and is currently implemented to use
     * {@link #findType(int, int, List)} when a numeric type and {@link #findType(String, List)}
     * (by name) for other types.
     * @param type
     * @param typeName
     * @return
     */
    @Override
    protected EObject findType(final int jdbcType, final String typeName, 
                                final int length, final int precision, final int scale,
                                final List problems ) {
                                    
        EObject result = null;
        // Map the Postgres type of "BOOL" to our built-in type of Boolean
        if (BOOLEAN_TYPE_NAME.equalsIgnoreCase(typeName)) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.BOOLEAN,problems);
        }
        if ( result != null ) {
            return result;
        }
        
        return super.findType(jdbcType,typeName,length,precision,scale,problems);
    }
}
