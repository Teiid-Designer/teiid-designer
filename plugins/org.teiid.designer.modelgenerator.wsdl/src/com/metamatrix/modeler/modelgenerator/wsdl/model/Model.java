/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model;

import java.util.Map;

import org.eclipse.xsd.XSDSchema;
import org.jdom.Namespace;

/**
 * This class represents the model hierarchy as defined by a give WSDL
 */
public interface Model {

    /**
     * @return an array of the services defined in the WSDL
     */
    public Service[] getServices();

    /**
     * @param services an array of services that are defined by the WSDL
     */
    public void setServices( Service[] services );

    /**
     * @return the schemas used by this model
     */
    public XSDSchema[] getSchemas();

    /**
     * @param schemas the schemas used by this model
     */
    public void setSchemas( XSDSchema[] schemas );

    public Map getNamespaces();

    public void setNamespaces( Map namespaceMap );

    public void addNamespaceToMap( Namespace ns );

    public void addNamespaceToMap( String prefix,
                                   String namespaceURI );

    public void addNamespaceToMap( String namespaceURI );

    public Service getService( String name );

    public Port getPort( String name );

    public Operation getOperation( String name );
    
    public Operation[] getModelableOperations(String portName);
    
    public String[] getModelablePortNames();
}
