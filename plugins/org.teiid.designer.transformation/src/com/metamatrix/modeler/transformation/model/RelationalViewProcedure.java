/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.model;

import org.teiid.designer.relational.model.RelationalProcedure;

/**
 * Virtual version of the RelationalProcedure, also includes transformation SQL
 */
public class RelationalViewProcedure extends RelationalProcedure {

    private String transformationSQL;

    public RelationalViewProcedure() {
        super();
    }
    /**
     * @param name
     */
    public RelationalViewProcedure( String name ) {
        super(name);
    }

    public void setTransformationSQL( String sql ) {
        this.transformationSQL = sql;
    }

    public String getTransformationSQL() {
        return this.transformationSQL;
    }
}
