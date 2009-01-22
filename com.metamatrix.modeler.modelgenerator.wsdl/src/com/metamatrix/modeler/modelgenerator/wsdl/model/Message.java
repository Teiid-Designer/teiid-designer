/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model;

public interface Message extends WSDLElement {
	
	public Part[] getParts();	
	public void setParts(Part[] parts);
	
	
	public int REQUEST_TYPE = 0x00;
	public int RESPONSE_TYPE = 0x02;
	public int FAULT_TYPE = 0x04;
	
	public Operation getOperation();
	public Fault getFault();
	
	public boolean isRequest();
	public boolean isResponse();
	public boolean isFault();
	
	public void setType(int Type);
	public int getType();

	public String getUse();
	public void setUse(String use);
	
	public String getNamespaceURI();
	public void setNamespaceURI(String ns);
	
	public void setEncodingStyle(String style);
	public String getEncodingStyle();
	
}
