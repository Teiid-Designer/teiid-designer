/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model;

import org.teiid.designer.query.proc.wsdl.model.IPort;

/**
 * This class represents a port as defined in a WSDL
 *
 * @since 8.0
 */
public interface Port extends IPort, WSDLElement {

    @Override
    Binding getBinding();

    /**
     * @param binding the binding that is defined by this port
     */
    void setBinding( Binding binding );

    @Override
    Service getService();

    /**
     * @param uri - the location attribute of the <soap:address> element. The endpoint URL for the port.
     */
    void setLocationURI( String uri );
    
    /**
     * @param uri - the binding namespace URI attribute of the <soap:address> element. 
     */
    void setBindingTypeURI( String uri );
    
    @Override
    Port copy();
}
