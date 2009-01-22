/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model;

public interface Part extends WSDLElement {
	
	public String getElementName();

	public void setElementName(String name);
	
	public String getElementNamespace();
	
	public void setElementNamespace(String namespace);
	
	public String getTypeName();
	
	public void setTypeName(String name);
	
	public String getTypeNamespace();
	
	public void setTypeNamespace(String namespace);
	
	public Message getMessage();
	
	public boolean isType();
	
	public boolean isElement();
	
}
