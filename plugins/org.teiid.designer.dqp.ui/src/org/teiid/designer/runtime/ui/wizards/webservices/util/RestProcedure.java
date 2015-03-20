/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.wizards.webservices.util;

import java.util.LinkedList;

/**
 * Domain class that defines a procedure to be exposed RESTFully. This class is used to generate the REST resource class in the
 * generated REST war.
 *
 * @since 8.0
 */
public class RestProcedure {

    private String restMethod;
    private String procedureName;
    private String fullyQualifiedProcedureName;
    private String consumesAnnotation;
    private String producesAnnotation;
    private String uri;
    private String description;
    private String modelName;
    private String charSet;
    private LinkedList<String> queryParameterList;
    private LinkedList<String> headerParameterList;

    /**
     * @return consumesAnnotation
     */
    public String getConsumesAnnotation() {
        return consumesAnnotation;
    }

    /**
     * @param consumesAnnotation Sets consumesAnnotation to the specified value.
     */
    public void setConsumesAnnotation( String consumesAnnotation ) {
        this.consumesAnnotation = consumesAnnotation;
    }

    /**
     * @return producesAnnotation
     */
    public String getProducesAnnotation() {
        return producesAnnotation;
    }

    /**
     * @param producesAnnotation Sets producesAnnotation to the specified value.
     */
    public void setProducesAnnotation( String producesAnnotation ) {
        this.producesAnnotation = producesAnnotation;
    }

    /**
	 * @return the charSet
     * @since 8.1
	 */
	public String getCharSet() {
		return this.charSet;
	}

	/**
	 * @param charSet the charSet to set
	 * @since 8.1
	 */
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	/**
     * @return modelName
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * @param modelName Sets modelName to the specified value.
     */
    public void setModelName( String modelName ) {
        this.modelName = modelName;
    }

    /**
     * @return restMethod
     */
    public String getRestMethod() {
        return restMethod;
    }

    /**
     * @param restMethod Sets restMethod to the specified value.
     */
    public void setRestMethod( String restMethod ) {
        this.restMethod = restMethod;
    }

    /**
     * @return procedureName
     */
    public String getProcedureName() {
        return procedureName;
    }

    /**
     * @param procedureName Sets procedureName to the specified value.
     */
    public void setProcedureName( String procedureName ) {
        this.procedureName = procedureName;
    }

    /**
     * @return fullyQualifiedProcedureName
     */
    public String getFullyQualifiedProcedureName() {
        return fullyQualifiedProcedureName;
    }

    /**
     * @param fullyQualifiedProcedureName Sets fullyQualifiedProcedureName to the specified value.
     */
    public void setFullyQualifiedProcedureName( String fullyQualifiedProcedureName ) {
        this.fullyQualifiedProcedureName = fullyQualifiedProcedureName;
    }

    /**
     * @return uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri Sets uri to the specified value.
     */
    public void setUri( String uri ) {
        this.uri = uri;
    }

	/**
	 * @return the queryParameterList
	 * @since 8.1
	 */
	public LinkedList<String> getQueryParameterList() {
		return queryParameterList;
	}

	/**
	 * @param queryParameterList the queryParameterList to set
	 * @since 8.1
	 */
	public void setQueryParameterList(LinkedList<String> queryParameterList) {
		this.queryParameterList = queryParameterList;
	}

	/**
	 * @return the headerParameterList
	 * @since 8.1
	 */
	public LinkedList<String> getHeaderParameterList() {
		return headerParameterList;
	}

	/**
	 * @param headerParameterList the headerParameterList to set
	 * @since 8.1
	 */
	public void setHeaderParameterList(LinkedList<String> headerParameterList) {
		this.headerParameterList = headerParameterList;
	}

	/**
	 * @return get the description of the procedure
	 * @since 9.0.2
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return set the description of the procedure
	 * @since 9.0.2
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}
