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

package com.metamatrix.modeler.modelgenerator.wsdl.validation;

import java.util.ArrayList;

import javax.wsdl.WSDLException;


/**
 * 
 * This class represents any errors that might occur while reading or validating a WSDL
 * 
 * @author JChoate
 *
 */
public class WSDLValidationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	private ArrayList m_messages;
		

	public WSDLValidationException() {
		super();
	}
	
	public WSDLValidationException(ArrayList validationMessages) {
		m_messages = validationMessages;
	}
	
	public WSDLValidationException(WSDLException wx) {
		super(wx);
	}

	/**
	 * If a validation error has occurred, calling this method will return a list
	 * of validation error messages
	 * 
	 * @return an array of validation errors or null if there are none
	 */
	public ArrayList getValidationMessages() {
		return m_messages;
	}
	
	
	
	
	

}
