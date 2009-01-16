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
