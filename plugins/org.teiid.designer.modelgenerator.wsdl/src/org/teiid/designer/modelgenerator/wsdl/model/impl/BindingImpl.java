/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model.impl;

import org.teiid.designer.modelgenerator.wsdl.model.Binding;
import org.teiid.designer.modelgenerator.wsdl.model.Operation;
import org.teiid.designer.modelgenerator.wsdl.model.Port;
import org.teiid.designer.modelgenerator.wsdl.model.WSDLElement;

public class BindingImpl extends org.teiid.designer.modelgenerator.wsdl.model.impl.WSDLElementImpl implements Binding {

	private Port m_port;
	private Operation[] m_operations;
	private String m_transportURI;
	private String m_style;
	
	public BindingImpl(Port port) {
		m_port = port;
	}
	
	@Override
	public Operation[] getOperations() {
		//defensive copy
		int arrayLength = m_operations.length;
		Operation[] ops = new Operation[arrayLength];
		for(int i = 0; i < arrayLength; i++) {
			ops[i] = (Operation) m_operations[i].copy();
		}
		return ops;
	}

	@Override
	public Port getPort() {
		return m_port;
	}

	@Override
	public void setOperations(Operation[] operations) {
		m_operations = operations;
	}

	@Override
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

	@Override
	public void setTransportURI(String uri) {
		m_transportURI = uri;		
	}

	@Override
	public String getTransportURI() {
		return m_transportURI;
	}

	@Override
	public void setStyle(String style) {
		m_style = style;
	}

	@Override
	public String getStyle() {
		return m_style;
	}

	
}
