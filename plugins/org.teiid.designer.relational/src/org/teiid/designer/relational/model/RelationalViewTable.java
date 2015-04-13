/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import org.teiid.designer.metamodels.core.ModelType;

/**
 * Virtual version of the RelationalTable, also includes transformation SQL
 *
 * @since 8.0
 */
public class RelationalViewTable extends RelationalTable {

    private String transformationSQL;
    private boolean isGlobalTempTable;

    public RelationalViewTable() {
        super();
        setModelType(ModelType.VIRTUAL);
    }
    /**
     * @param name
     */
    public RelationalViewTable( String name ) {
        super(name);
    }

    public void setTransformationSQL( String sql ) {
        this.transformationSQL = sql;
    }

    public String getTransformationSQL() {
        return this.transformationSQL;
    }
    
    public void setGlobalTempTable(boolean value) {
    	this.isGlobalTempTable = value;
    }
    
    public boolean isGlobalTempTable() {
    	return this.isGlobalTempTable;
    }
}
