/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model;

/**
 * 
 * This class represents a services as defined by as WSDL
 * 
 * @author JChoate
 *
 */
public interface Service extends WSDLElement {
	
	/**
	 * 
	 * @return an array of ports defined by the service
	 */
	public Port[] getPorts();
	
	/**
	 * 
	 * @param ports the ports that this service defines
	 */
	public void setPorts(Port[] ports);

	public void setModel(Model theModel);
	
	public Model getModel();
	
}
