/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model;

import org.teiid.designer.query.proc.wsdl.model.IBinding;

/**
 * This class represents a Binding as defined in a WSDL
 *
 * @since 8.0
 */
public interface Binding extends IBinding, WSDLElement {

    /**
     * @return returns the operations defined within the Binding
     */
    Operation[] getOperations();

    /**
     * @return the port that contains this binding
     */
    Port getPort();

    /**
     * @param operations the array of operations this binding contains
     */
    void setOperations( Operation[] operations );

    /**
     * @param uri the URI for the SOAP Binding
     */
    void setTransportURI( String uri );

    /**
     * @param style the style for the SOAP web service
     */
    void setStyle( String style );
    
    @Override
    Binding copy();
}
