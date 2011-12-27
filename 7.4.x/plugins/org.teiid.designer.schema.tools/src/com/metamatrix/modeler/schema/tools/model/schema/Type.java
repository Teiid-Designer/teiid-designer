/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
