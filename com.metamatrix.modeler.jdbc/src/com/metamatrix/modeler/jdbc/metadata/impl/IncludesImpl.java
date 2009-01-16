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

package com.metamatrix.modeler.jdbc.metadata.impl;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.jdbc.metadata.Includes;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;

/**
 * IncludesImpl
 */
public class IncludesImpl implements Includes {
    
    private static final String[] NO_TYPES = new String[]{};
    
    private final JdbcDatabase database;
    private String[] includedTableTypes;
    private boolean fks         = DEFAULT_INCLUDE_FOREIGN_KEYS;
    private boolean indexes     = DEFAULT_INCLUDE_INDEXES;
    private boolean procedures  = DEFAULT_INCLUDE_PROCEDURES;
    private boolean approximateIndexes = DEFAULT_APPROXIMATE_INDEXES;
    private boolean uniqueIndexes = DEFAULT_UNIQUE_INDEXES;

    /**
     * Construct an instance of IncludesImpl.
     * 
     */
    public IncludesImpl( final JdbcDatabase database ) {
        super();
        ArgCheck.isNotNull(database);
        this.database = database;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#getJdbcDatabase()
     */
    public JdbcDatabase getJdbcDatabase() {
        return this.database;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#getIncludedTableTypes()
     */
    public String[] getIncludedTableTypes() {
        return includedTableTypes;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#getIncludedTableTypes(java.lang.String[])
     */
    public void setIncludedTableTypes(String[] typesToInclude) {
        includedTableTypes = typesToInclude != null ? typesToInclude : NO_TYPES;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#includeProcedures()
     */
    public boolean includeProcedures() {
        return procedures;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#setIncludeProcedures(boolean)
     */
    public void setIncludeProcedures(boolean include) {
        procedures = include;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#includeForeignKeys()
     */
    public boolean includeForeignKeys() {
        return fks;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#setIncludeForeignKeys(boolean)
     */
    public void setIncludeForeignKeys(boolean include) {
        fks = include;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#includeIndexes()
     */
    public boolean includeIndexes() {
        return indexes;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.jdbc.metadata.Includes#setIncludeIndexes(boolean)
     */
    public void setIncludeIndexes(boolean include) {
        indexes = include;
    }

    /**
     * @return
     */
    public boolean getApproximateIndexes() {
        return approximateIndexes;
    }

    /**
     * @return
     */
    public boolean getUniqueIndexesOnly() {
        return uniqueIndexes;
    }

    /**
     * @param b
     */
    public void setApproximateIndexes(boolean b) {
        approximateIndexes = b;
    }

    /**
     * @param b
     */
    public void setUniqueIndexesOnly(boolean b) {
        uniqueIndexes = b;
    }

}
