/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model;

/**
 * This class represents a Binding as defined in a WSDL
 */
public interface Binding extends WSDLElement {

    /**
     * @return returns the operations defined within the Binding
     */
    public Operation[] getOperations();

    /**
     * @return the port that contains this binding
     */
    public Port getPort();

    /**
     * @param operations the array of operations this binding contains
     */
    public void setOperations( Operation[] operations );

    /**
     * @param uri the URI for the SOAP Binding
     */
    public void setTransportURI( String uri );

    /**
     * @return uri the URI for the SOAP Binding
     */
    public String getTransportURI();

    /**
     * @param style the style for the SOAP web service
     */
    public void setStyle( String style );

    /**
     * This returns the style information returned by the SOAP binding (RPC or DOC)
     * 
     * @return the style for the SOAP web service
     */
    public String getStyle();
}
