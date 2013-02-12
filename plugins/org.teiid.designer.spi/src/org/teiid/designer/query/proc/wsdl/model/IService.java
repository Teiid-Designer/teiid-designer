/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.query.proc.wsdl.model;

/**
 * This class represents a services as defined by as WSDL
 *
 * @since 8.0
 */
public interface IService extends IWsdlElement {

    /**
     * @return an array of ports defined by the service
     */
    IPort[] getPorts();

    IModel getModel();

    String getNamespaceURI();

}
