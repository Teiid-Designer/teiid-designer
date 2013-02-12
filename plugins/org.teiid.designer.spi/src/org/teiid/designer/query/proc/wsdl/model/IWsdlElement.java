/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.query.proc.wsdl.model;

/**
 * This class is an abstract representation of the elements that appear in a WSDL
 *
 * @since 8.0
 */
public interface IWsdlElement {

    /**
     * @return the name of the element
     */
    String getName();

    /**
     * @return the id of the element
     */
    String getId();

    /**
     * This method is used for making defensive copies of WSDLElements
     * 
     * @return a copy of the element
     */
    IWsdlElement copy();

}
