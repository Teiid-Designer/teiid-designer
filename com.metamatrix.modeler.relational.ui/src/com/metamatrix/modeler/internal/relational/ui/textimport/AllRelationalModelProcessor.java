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

package com.metamatrix.modeler.internal.relational.ui.textimport;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.util.RelationalTypeMapping;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl;


/** 
 * @since 4.2
 */
public class AllRelationalModelProcessor extends RelationalModelProcessorImpl {
    private static final String VARCHAR2_TYPE_NAME = "VARCHAR2"; //$NON-NLS-1$
    private static final String NVARCHAR2_TYPE_NAME = "NVARCHAR2"; //$NON-NLS-1$
    private static final String TIMESTAMP_TYPE_NAME = "TIMESTAMP("; //$NON-NLS-1$
    private static final String NUMBER_TYPE_NAME = "NUMBER"; //$NON-NLS-1$
    private static final String REF_CURSOR = "REF CURSOR"; //$NON-NLS-1$

    /**
     * Construct an instance of OracleModelProcessor.
     * 
     */
    public AllRelationalModelProcessor() {
        super();
    }

    /**
     * Construct an instance of OracleModelProcessor.
     * @param factory
     */
    public AllRelationalModelProcessor(final RelationalFactory factory) {
        super(factory);
    }
    
    /**
     * Construct an instance of OracleModelProcessor.
     * @param factory
     */
    public AllRelationalModelProcessor(final RelationalFactory factory, final RelationalTypeMapping mapping) {
        super(factory,mapping);
        setDatatypeManager(ModelerCore.getWorkspaceDatatypeManager());
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
        // If the type is NUMERIC and precision is non-zero, then look at the length of the column ...
        // (assume zero length means the length isn't known)
        if ( precision != 0 ) {
            if ( NUMBER_TYPE_NAME.equalsIgnoreCase(typeName) || 
                 REF_CURSOR.equalsIgnoreCase(typeName) ) {
                result = findType(precision,scale,problems);
            }
        }
        if ( result != null ) {
            return result;
        }
        
        // Oracle 9i introduced the "timestamp" type name (with type=1111, or OTHER)
        if ( typeName.startsWith(TIMESTAMP_TYPE_NAME) ) {
            result = findBuiltinType(DatatypeConstants.BuiltInNames.TIMESTAMP,problems);
        }
        if ( result != null ) {
            return result;
        }
        
        return super.findType(jdbcType,typeName,length,precision,scale,problems);
    }
    
    /**
     * Overrides the method to find a type simply by name.  This method converts
     * some Oracle-specific (non-numeric) types to standard names, and then
     * simply delegates to the superclass.
     * Find the datatype by name.
     * @param jdbcTypeName the name of the JDBC (or DBMS) type
     * @param problems the list if {@link IStatus} into which problems and warnings
     * are to be placed; never null
     * @return the datatype that is able to represent data with the supplied criteria, or
     * null if no datatype could be found
     * @see com.metamatrix.modeler.jdbc.relational.impl.RelationalModelProcessorImpl#findType(java.lang.String, java.util.List)
     */
    @Override
    protected EObject findType(final String jdbcTypeName, final List problems) {
        String standardName = jdbcTypeName;
        if (VARCHAR2_TYPE_NAME.equalsIgnoreCase(jdbcTypeName) || NVARCHAR2_TYPE_NAME.equalsIgnoreCase(jdbcTypeName)) {
            standardName = RelationalTypeMapping.SQL_TYPE_NAMES.VARCHAR;
        }
        return super.findType(standardName, problems);
    }
    
    @Override
    protected boolean isFixedLength(final int type,
                                    final String typeName) {
        if (NVARCHAR2_TYPE_NAME.equalsIgnoreCase(typeName)) {
            return false;
        }
        return super.isFixedLength(type, typeName);
    }
}
