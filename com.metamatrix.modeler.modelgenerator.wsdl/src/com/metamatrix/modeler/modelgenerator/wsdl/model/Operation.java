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

import com.metamatrix.modeler.modelgenerator.wsdl.SoapBindingInfo;


/**
 * 
 * This class represents an Operation as defined in the WSDL
 * It does not contain any information about the messages that 
 * are used by the operation as they are of no interest until
 * it is time to actually create an MM model
 * 
 * @author JChoate
 *
 */
public interface Operation extends WSDLElement {
	
	/**
	 * 
	 * @return the binding that contains this operation
	 */
	public Binding getBinding();

	/**
	 * 
	 * @return the name of the input message
	 */
	public Message getInputMessage();
	
	/**
	 * 
	 * @param inputMsg the name of the input message
	 */
	public void setInputMessage(Message inputMsg);
	
	/**
	 * 
	 * @return the name of the output message
	 */
	public Message getOutputMessage();
	
	/**
	 * 
	 * @param outputMsg the name of the output message
	 */
	public void setOutputMessage(Message outputMsg);
	
	/**
	 * 
	 * @return the style of the operation
	 */
	public String getStyle();
	
	/**
	 * 
	 * @param style the style of the operation
	 */
	public void setStyle(String style);
	
	/**
	 * 
	 * @return an array of the names of possible faults
	 */
	public Fault[] getFaults();
	
	/**
	 * 
	 * @param faults an array of the names of the possible faults
	 */
	public void setFaults(Fault[] faults);
	
	public void setSOAPAction(String action);
	
	public String getSOAPAction();
	
	public boolean canModel();
	
	public void setCanModel(boolean canModel);
	
	public void addProblemMessage(String message);
	
	public String[] getProblemMessages();
	
	public SoapBindingInfo getSoapBindingInfo();
	
	public void setSoapBindingInfo(SoapBindingInfo info);
}
