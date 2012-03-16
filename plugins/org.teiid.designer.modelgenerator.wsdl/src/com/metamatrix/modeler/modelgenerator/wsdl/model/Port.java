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
	
	public static final String HTTP = "HTTP"; //$NON-NLS-1$
    public static final String SOAP11 = "SOAP11"; //$NON-NLS-1$
    public static final String SOAP12 = "SOAP12"; //$NON-NLS-1$
	
	public static final String HTTP_TRANSPORT_URI = "http://schemas.xmlsoap.org/wsdl/http/"; //$NON-NLS-1$
	public static final String SOAP11_TRANSPORT_URI = "http://schemas.xmlsoap.org/wsdl/soap/"; //$NON-NLS-1$
	public static final String SOAP12_TRANSPORT_URI = "http://schemas.xmlsoap.org/wsdl/soap12/"; //$NON-NLS-1$

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
     * @param uri - the binding namespace URI attribute of the <soap:address> element. 
     */
    public void setBindingTypeURI( String uri );
    
    /**
     * @param uri - the binding type (SOAP11, SOAP12 or HTTP). 
     */
    public String getBindingType( );
    
    /**
     * @param uri - the binding namespace URI attribute of the <soap:address> element. 
     */
    public String getBindingTypeURI();

    /**
     * @return the location attribute of the <soap:address> element. The endpoint URL for the port.
     */
    public String getLocationURI();

    public String getNamespaceURI();

}
