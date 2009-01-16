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

import com.metamatrix.modeler.modelgenerator.wsdl.model.Binding;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;
import com.metamatrix.modeler.modelgenerator.wsdl.model.WSDLElement;

public class BindingImpl extends com.metamatrix.modeler.modelgenerator.wsdl.model.internal.WSDLElementImpl implements Binding {

	private Port m_port;
	private Operation[] m_operations;
	private String m_transportURI;
	private String m_style;
	
	public BindingImpl(Port port) {
		m_port = port;
	}
	
	public Operation[] getOperations() {
		//defensive copy
		int arrayLength = m_operations.length;
		Operation[] ops = new Operation[arrayLength];
		for(int i = 0; i < arrayLength; i++) {
			ops[i] = (Operation) m_operations[i].copy();
		}
		return ops;
	}

	public Port getPort() {
		return m_port;
	}

	public void setOperations(Operation[] operations) {
		m_operations = operations;
	}

	public WSDLElement copy() {
		Binding theBinding = new BindingImpl(m_port);
		theBinding.setName(getName());
		theBinding.setId(getId());
		theBinding.setStyle(getStyle());
		theBinding.setOperations(getOperations());
		return theBinding;
	}
	
	@Override
    public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("<binding name='"); //$NON-NLS-1$
		buff.append(getName());
		buff.append("' id='"); //$NON-NLS-1$
		buff.append(getId());
		buff.append("'>"); //$NON-NLS-1$
		for(int i = 0; i < m_operations.length; i++) {
			buff.append(m_operations[i].toString());
		}
		buff.append("</binding>"); //$NON-NLS-1$
		return buff.toString();
	}

	public void setTransportURI(String uri) {
		m_transportURI = uri;		
	}

	public String getTransportURI() {
		return m_transportURI;
	}

	public void setStyle(String style) {
		m_style = style;
	}

	public String getStyle() {
		return m_style;
	}

	
}
