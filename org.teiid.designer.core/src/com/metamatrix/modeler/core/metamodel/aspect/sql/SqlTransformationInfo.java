/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect.sql;

import java.util.ArrayList;
import java.util.List;
import com.metamatrix.core.modeler.util.ArgCheck;

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
