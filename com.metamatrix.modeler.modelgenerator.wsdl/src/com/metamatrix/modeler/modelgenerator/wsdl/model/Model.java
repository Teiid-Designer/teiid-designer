/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.modelgenerator.wsdl.model;


import java.util.Map;

import org.eclipse.xsd.XSDSchema;
import org.jdom.Namespace;



/**
 * 
 * This class represents the model hierarchy as defined by a give WSDL
 * 
 * @author JChoate
 *
 */
public interface Model {

	/**
	 * 
	 * @return an array of the services defined in the WSDL
	 */
	public Service[] getServices();
	
	/**
	 * 
	 * @param services an array of services that are defined by the WSDL
	 */
	public void setServices(Service[] services);
	
	
	/**
	 * 
	 * @return the schemas used by this model
	 */
	public XSDSchema[] getSchemas();
	
	/**
	 * 
	 * @param schemas the schemas used by this model
	 */
	public void setSchemas(XSDSchema[] schemas);
	
	public Map getNamespaces();
	
	public void setNamespaces(Map namespaceMap);
	
	public void addNamespaceToMap(Namespace ns);
	
	public void addNamespaceToMap(String prefix, String namespaceURI);
	
	public void addNamespaceToMap(String namespaceURI);
}
