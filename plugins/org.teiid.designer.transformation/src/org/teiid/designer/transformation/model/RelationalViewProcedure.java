/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.model;

import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.relational.model.RelationalProcedure;

/**
 * Virtual version of the RelationalProcedure, also includes transformation SQL
 *
 * @since 8.0
 */
public class RelationalViewProcedure extends RelationalProcedure {

    private String transformationSQL;

    /**
     * 
     */
    public RelationalViewProcedure() {
        super();
        setModelType(ModelType.VIRTUAL);
    }
    /**
     * @param name the procedure name
     */
    public RelationalViewProcedure( String name ) {
        super(name);
    }

    /**
     * @param sql the transformation SQL
     */
    public void setTransformationSQL( String sql ) {
        this.transformationSQL = sql;
    }

    /**
     * @return the transformation SQL
     */
    public String getTransformationSQL() {
        return this.transformationSQL;
    }
    
	/* (non-Javadoc)
	 * @see org.teiid.designer.relational.model.RelationalProcedure#isSourceFunction()
	 */
	@Override
	public boolean isSourceFunction() {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.teiid.designer.relational.model.RelationalProcedure#setSourceFunction(boolean)
	 */
	@Override
	public void setSourceFunction(boolean isSourceFunction) {
		throw new UnsupportedOperationException("RelationalViewProcedure.setSourceFunction() method is not supported");  //$NON-NLS-1$
	}
    
    
}
