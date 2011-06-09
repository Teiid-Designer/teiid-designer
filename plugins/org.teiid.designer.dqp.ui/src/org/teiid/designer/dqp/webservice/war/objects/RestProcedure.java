/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dqp.webservice.war.objects;

/**
 * Domain class that defines a procedure to be exposed RESTFully. This class is used to generate the REST resource class in the
 * generated REST war.
 */
public class RestProcedure {

    private String restMethod;
    private String procedureName;
    private String fullyQualifiedProcedureName;
    private String consumesAnnotation;
    private String producesAnnotation;
    private String uri;
    private String modelName;

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
     * @return produces
     */
    public String getProduces() {
        return producesAnnotation;
    }

    /**
     * @param produces Sets produces to the specified value.
     */
    public void setProduces( String produces ) {
        this.producesAnnotation = produces;
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

}
