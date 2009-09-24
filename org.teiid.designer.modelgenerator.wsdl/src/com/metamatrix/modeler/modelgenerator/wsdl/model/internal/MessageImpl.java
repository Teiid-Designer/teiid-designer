/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Fault;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Message;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Operation;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Part;
import com.metamatrix.modeler.modelgenerator.wsdl.model.WSDLElement;

public class MessageImpl extends WSDLElementImpl implements Message {

	private Part[] m_parts;
	private Operation m_operation;
	private Fault m_fault;
	private int m_type;
	private String m_namespace;
	private String m_use;
	private String m_encStyle;

	public MessageImpl(Operation oper) {
		m_operation = oper;
	}
	
	public MessageImpl(Fault fault) {
		m_fault = fault;
	}
	
	public Part[] getParts() {
		//defensive copy
		Part[] newParts = new Part[m_parts.length];
		for(int i = 0; i < m_parts.length; i++) {
			newParts[i] = (Part) m_parts[i].copy();
		}
		return newParts;
	}

	public void setParts(Part[] parts) {
		m_parts = parts;
	}

	public Operation getOperation() {
		return m_operation;
	}

	public boolean isRequest() {
		return m_type == Message.REQUEST_TYPE;
	}

	public boolean isResponse() {
		return m_type == Message.RESPONSE_TYPE;
	}

	public boolean isFault() {
		return m_type == Message.FAULT_TYPE;
	}

	public void setType(int type) {
		m_type = type;
	}

	public int getType() {
		return m_type;
	}
	
	
	public WSDLElement copy() {
		Message message;
		if(m_operation != null) {
			message = new MessageImpl(getOperation());
		} else {
			message = new MessageImpl(getFault());
		}
		message.setName(getName());
		message.setId(getId());
		message.setType(getType());
		message.setEncodingStyle(getEncodingStyle());
		message.setUse(getUse());
		message.setNamespaceURI(getNamespaceURI());
		Part[] newParts = new Part[m_parts.length];
		for(int i = 0; i < newParts.length; i++) {
			newParts[i] = (Part) m_parts[i].copy();
		}
		message.setParts(newParts);
		return message;
	}

	public Fault getFault() {
		return m_fault;
	}
	
	@Override
    public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("<message name='"); //$NON-NLS-1$
		buff.append(getName());
		buff.append("' id='"); //$NON-NLS-1$
		buff.append(getId());
		buff.append("' typeCode='"); //$NON-NLS-1$
		switch (getType()) {
			case Message.REQUEST_TYPE:
				buff.append("Request"); //$NON-NLS-1$
				break;
			case Message.RESPONSE_TYPE:
				buff.append("Response"); //$NON-NLS-1$
				break;
			case Message.FAULT_TYPE:
				buff.append("Fault"); //$NON-NLS-1$
				break;
		}
		buff.append("' namespace='"); //$NON-NLS-1$
		buff.append(getNamespaceURI());
		buff.append("' use='"); //$NON-NLS-1$
		buff.append(getUse());
		buff.append("' ecodingStyles='"); //$NON-NLS-1$
		buff.append(getEncodingStyle());
		buff.append("' >"); //$NON-NLS-1$
		for(int i = 0; i < m_parts.length; i++) {
			buff.append(m_parts[i].toString());
		}
		buff.append("</message>"); //$NON-NLS-1$
		return buff.toString();
	}

	public String getUse() {
		return m_use;
	}

	public void setUse(String use) {
		m_use = use;
	}

	public String getNamespaceURI() {
		return m_namespace;
	}

	public void setNamespaceURI(String ns) {
		m_namespace = ns;
	}

	public void setEncodingStyle(String style) {
		m_encStyle = style;
	}

	public String getEncodingStyle() {
		return m_encStyle;
	}

}
