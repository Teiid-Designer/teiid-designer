/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.jdbc;

public interface DatabaseElement {

	public String getName();

	public void setName(String name);

	public String getInputXPath();

	public void setInputXPath(String xpathIn);

	/**
	 * returns the XPath for outputs from this table
	 * 
	 * @return - the output XPath
	 */
	public String getOutputXPath();

	/**
	 * 
	 * Sets the XPath for this table for reading output
	 * 
	 * @param xpath - the output XPath
	 */
	public void setOutputXPath(String xpath);

}
