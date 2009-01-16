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

package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;


import com.metamatrix.modeler.modelgenerator.wsdl.model.Fault;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.WSDLElement;

public class FaultImpl extends WSDLElementImpl implements Fault {

	private Message m_message;
	private Operation m_operation;
	
	public FaultImpl(Operation oper) {
		m_operation = oper;
	}
	
	public Operation getOperation() {
		return m_operation;
	}
	
	public void setMessage(Message message) {
		m_message = message;
	}

	public Message getMessage() {
		return m_message;
	}

	public WSDLElement copy() {
		FaultImpl fault = new FaultImpl(getOperation());
		fault.setMessage(getMessage());
		fault.setName(getName());
		fault.setId(getId());
		return fault;
	}
	
	@Override
    public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("<fault name='"); //$NON-NLS-1$
		buff.append(getName());
		buff.append("' id='"); //$NON-NLS-1$
		buff.append(getId());
		buff.append("'>"); //$NON-NLS-1$
		buff.append(getMessage().toString());
		buff.append("</fault>"); //$NON-NLS-1$
		return buff.toString();
	}
}
