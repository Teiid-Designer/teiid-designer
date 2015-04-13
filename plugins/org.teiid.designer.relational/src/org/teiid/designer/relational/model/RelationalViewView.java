/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;


/**
 * Virtual version of the RelationalView, also includes transformation SQL
 *
 * @since 8.0
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
