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
