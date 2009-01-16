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

package com.metamatrix.modeler.schema.tools.model.schema;

import org.eclipse.xsd.XSDSimpleTypeDefinition;

public interface Type {
	/**
	 * Returns the type as a namespace prefix qualified string; 
	 * i.e. xsd:string, xsd:int, etc 
	 * @return - the type 
	 */
	public String getBaseType(); 

	/**
	 * Returns the qualified name of the actual declared type.
	 * @return - the name of the type.
	 */
	public QName getTypeName(); 

	/**
	 * Returns the XSDSimpleTypeDefinition representing the actual declared type
	 * @return - the XSDSimpleTypeDefinition
	 */
	public XSDSimpleTypeDefinition getType(); 
}
