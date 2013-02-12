/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model;

import org.teiid.designer.query.proc.wsdl.model.IService;

/**
 * This class represents a services as defined by as WSDL
 *
 * @since 8.0
 */
public interface Service extends IService, WSDLElement {

    @Override
    Port[] getPorts();

    /**
     * @param ports the ports that this service defines
     */
    void setPorts( Port[] ports );

    void setModel( Model theModel );

    @Override
    Model getModel();
    
    @Override
    Service copy();
}
