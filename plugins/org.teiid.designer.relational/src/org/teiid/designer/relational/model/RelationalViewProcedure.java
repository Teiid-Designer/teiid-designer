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
 * Virtual version of the RelationalProcedure, also includes transformation SQL
 *
 * @since 8.0
 */
public class RelationalViewProcedure extends RelationalProcedure {

    private String transformationSQL;
    
    private boolean restEnabled;
    
    private String restMethod;
    
    private String restUri;
    
    private String restCharSet;
    
    private String restHeaders;

    private String restDescription;

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
	
	/**
	 * Rest enablement flag
	 * 
	 * @return
	 */
	public boolean isRestEnabled() {
		return restEnabled;
	}
	
	/**
	 * Sets Rest enablement
	 * 
	 * @param restEnabled
	 */
	public void setRestEnabled(boolean restEnabled) {
		this.restEnabled = restEnabled;
	}
	
	/**
	 * Returns the current Rest method (GET, PUT, POST, DELETE)
	 * 
	 * @return
	 */
	public String getRestMethod() {
		return restMethod;
	}
	
	/**
	 * Sets the Rest method
	 * 
	 * @param restMethod
	 */
	public void setRestMethod(String restMethod) {
		this.restMethod = restMethod;
	}
	
	/**
	 * Returns the current Rest URI value
	 * 
	 * @return
	 */
	public String getRestUri() {
		return restUri;
	}
	
	/**
	 * Sets the Rest URI value
	 * 
	 * @param restUri
	 */
	public void setRestUri(String restUri) {
		this.restUri = restUri;
	}
    
	/**
	 * Returns the current Rest CharSet value
	 * 
	 * @return
	 */
	public String getRestCharSet() {
		return restCharSet;
	}
	
	/**
	 * Sets the Rest CharSet value
	 * 
	 * @param restCharSet
	 */
	public void setRestCharSet(String restCharSet) {
		this.restCharSet = restCharSet;
	}
	
	/**
	 * Returns the current Rest Headers value
	 * 
	 * @return
	 */
	public String getRestHeaders() {
		return restHeaders;
	}
	
	/**
	 * Sets the Rest Headers value
	 * 
	 * @param restHeaders
	 */
	public void setRestHeaders(String restHeaders) {
		this.restHeaders = restHeaders;
	}
	
	/**
	 * Sets the Rest Description value
	 * 
	 * @param restDescription
	 */
	public String getRestDescription() {
		return restDescription;
	}
	
	/**
	 * Sets the Rest Description value
	 * 
	 * @param restDescription
	 */
	public void setRestDescription(String restDescription) {
		this.restDescription = restDescription;
	}
    
}
