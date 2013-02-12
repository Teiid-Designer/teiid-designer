/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model;

import java.util.Map;
import org.eclipse.xsd.XSDSchema;
import org.jdom.Namespace;
import org.teiid.designer.query.proc.wsdl.model.IModel;

/**
 * This class represents the model hierarchy as defined by a give WSDL
 *
 * @since 8.0
 */
public interface Model extends IModel {

    /**
     * @return an array of the services defined in the WSDL
     */
    @Override
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

    @Override
    public Map<String, String> getNamespaces();

    public void setNamespaces( Map namespaceMap );

    public void addNamespaceToMap( Namespace ns );

    public void addNamespaceToMap( String prefix,
                                   String namespaceURI );

    public void addNamespaceToMap( String namespaceURI );

    @Override
    public Service getService( String name );

    @Override
    public Port getPort( String name );

    @Override
    public Operation getOperation( String name );
    
    @Override
    public Operation[] getModelableOperations(String portName);
    
    @Override
    public String[] getModelablePortNames();
}
