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

package com.metamatrix.modeler.core.metamodel.aspect.sql;

import java.util.ArrayList;
import java.util.List;

import com.metamatrix.core.util.ArgCheck;

/**
 * SqlTransformationInfo
 */
public class SqlTransformationInfo {

    private String sqlTransform;

    private List schemaPaths;

    private List bindings;

    public SqlTransformationInfo(String sqlTransform) {
        ArgCheck.isNotEmpty(sqlTransform);
        this.sqlTransform = sqlTransform;
    }

    public SqlTransformationInfo(String sqlTransform, List bindings) {
        this(sqlTransform);
        this.bindings = bindings;
    }

    public String getSqlTransform() {
        return this.sqlTransform;
    }

    public List getBindings() {
        return this.bindings;
    }

    public void addBinding(Object binding) {
        if(this.bindings == null) {
            this.bindings = new ArrayList();    
        }
        this.bindings.add(binding);
    }

    public void setBindings(List bindings) {
        this.bindings = bindings;
    }

    /**
     * @return
     */
    public List getSchemaPaths() {
        return schemaPaths;
    }

    /**
     * @param collection
     */
    public void setSchemaPaths(List collection) {
        schemaPaths = collection;
    }

}
