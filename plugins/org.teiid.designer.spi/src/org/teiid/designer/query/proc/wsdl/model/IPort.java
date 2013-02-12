/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.query.proc.wsdl.model;

/**
 * This class represents a port as defined in a WSDL
 *
 * @since 8.0
 */
public interface IPort extends IWsdlElement {
	
	static final String HTTP = "HTTP"; //$NON-NLS-1$
    static final String SOAP11 = "SOAP11"; //$NON-NLS-1$
    static final String SOAP12 = "SOAP12"; //$NON-NLS-1$
	
	static final String HTTP_TRANSPORT_URI = "http://schemas.xmlsoap.org/wsdl/http/"; //$NON-NLS-1$
	static final String SOAP11_TRANSPORT_URI = "http://schemas.xmlsoap.org/wsdl/soap/"; //$NON-NLS-1$
	static final String SOAP12_TRANSPORT_URI = "http://schemas.xmlsoap.org/wsdl/soap12/"; //$NON-NLS-1$

    /**
     * @return a binding defined in this port
     */
    IBinding getBinding();

    /**
     * @return the service that defines this port
     */
    IService getService();
    
    /**
     * @param uri - the binding type (SOAP11, SOAP12 or HTTP). 
     */
    String getBindingType( );
    
    /**
     * @param uri - the binding namespace URI attribute of the <soap:address> element. 
     */
    String getBindingTypeURI();

    /**
     * @return the location attribute of the <soap:address> element. The endpoint URL for the port.
     */
    String getLocationURI();

    String getNamespaceURI();

}
