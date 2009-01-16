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

package com.metamatrix.modeler.jdbc.relational.impl;

import java.util.HashMap;
import java.util.Map;

import com.metamatrix.modeler.internal.jdbc.relational.util.JdbcRelationalUtil;

/**
 * Table statistics value object 
 * @since 4.3
 */
public class TableStatistics {
    
    private static final String DATABASE_OBJECT_NAME_SEPARATOR = ".";  //$NON-NLS-1$
    
    private String catalog;
    private String schema;
    private String name;
    private String fullyQualifiedName;
    
    private int cardinality;        
    private Map columnStatistics;


    public TableStatistics(String fullName) {
        parseTableName(fullName);
        initialize();            
    }
    
    public TableStatistics(String catalog, String schema, String fullName) {
        this(fullName);
        if (catalog != null) {
            this.catalog = catalog;
        }
        if (schema != null) {
            this.schema = schema;
        }
    }
    
    private void parseTableName(String fullName) {
        String[] qualifiedParts = fullName.split("\\" + DATABASE_OBJECT_NAME_SEPARATOR); //$NON-NLS-1$
        int numParts = qualifiedParts.length;
        if (numParts > 0) {
            this.name = qualifiedParts[--numParts];
            if (numParts > 0) {
                this.schema = qualifiedParts[--numParts];
                if (numParts > 0) {
                    this.catalog = qualifiedParts[--numParts];
                }
            }
        } else {
            this.name = fullName;
        }        
    }
    
    private void initialize() {
        this.columnStatistics = new HashMap();
    }
    
    public Map getColumnStats() {
        return this.columnStatistics;
    }

    public void setColumnStats(Map columnStats) {
        this.columnStatistics = columnStats;
    }

    public int getCardinality() {
        return this.cardinality;
    }

    public void setCardinality(int cardinality) {
        this.cardinality = cardinality;
    }
        
    public String getCatalog() {
        return this.catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
        resetFullyQualifiedName();
    }

    public String getSchema() {
        return this.schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
        resetFullyQualifiedName();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        resetFullyQualifiedName();
    }       
    
    // build the fully qualified, escaped database object name
    // for this table
    public String getFullyQualifiedEscapedName() {
        if (this.fullyQualifiedName == null) {
            String temp = ""; //$NON-NLS-1$
            if (getCatalog() != null) {
                temp += JdbcRelationalUtil.escapeDatabaseObjectName(getCatalog()) + DATABASE_OBJECT_NAME_SEPARATOR;
            }
            if (getSchema() != null) {
                temp += JdbcRelationalUtil.escapeDatabaseObjectName(getSchema()) + DATABASE_OBJECT_NAME_SEPARATOR;
            }
            temp += JdbcRelationalUtil.escapeDatabaseObjectName(getName());
            this.fullyQualifiedName = temp;
        }
        return this.fullyQualifiedName;
    }

    private void resetFullyQualifiedName() {
        this.fullyQualifiedName = null;
    }            
}
