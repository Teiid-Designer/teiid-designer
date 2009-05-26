/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model;

/**
 * This class represents a port as defined in a WSDL
 */
public interface Port extends WSDLElement {

    /**
     * @return a binding defined in this port
     */
    public Binding getBinding();

    /**
     * @param binding the binding that is defined by this port
     */
    public void setBinding( Binding binding );

    /**
     * @return the service that defines this port
     */
    public Service getService();

    /**
     * @param uri - the location attribute of the <soap:address> element. The endpoint URL for the port.
     */
    public void setLocationURI( String uri );

    /**
     * @return the location attribute of the <soap:address> element. The endpoint URL for the port.
     */
    public String getLocationURI();

    public String getNamespaceURI();

}
