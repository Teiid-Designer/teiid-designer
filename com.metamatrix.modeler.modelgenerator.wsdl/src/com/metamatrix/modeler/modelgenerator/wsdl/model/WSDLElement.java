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
 * This class is an abstract representation of the elements
 * that appear in a WSDL
 * 
 * @author JChoate
 *
 */
public interface WSDLElement {
	
	/**
	 * 
	 * @return the name of the element
	 */
	public String getName();
	
	/**
	 * 
	 * @param name the name of the element
	 */
	public void setName(String name);
	
	/**
	 * 
	 * @return the id of the element
	 */
	public String getId();
	
	/**
	 * 
	 * @param id the id of the element
	 */
	public void setId(String id);
	
	/**
	 * This method is used for making defensive copies of WSDLElements
	 * 
	 * @return a copy of the element
	 */
	public WSDLElement copy();
	
}
