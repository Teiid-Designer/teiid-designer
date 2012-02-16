/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.model;

import org.teiid.designer.relational.model.RelationalView;

/**
 * Virtual version of the RelationalView, also includes transformation SQL
 */
public class RelationalViewView extends RelationalView {

    private String transformationSQL;

    public RelationalViewView() {
        super();
    }
    /**
     * @param name
     */
    public RelationalViewView( String name ) {
        super(name);
    }

    public void setTransformationSQL( String sql ) {
        this.transformationSQL = sql;
    }

    public String getTransformationSQL() {
        return this.transformationSQL;
    }
}
