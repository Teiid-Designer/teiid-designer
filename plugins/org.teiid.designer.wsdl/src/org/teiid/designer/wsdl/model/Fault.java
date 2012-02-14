/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.wsdl.model;


public class Fault extends WSDLElement  {

	private Message m_message;
	private Operation m_operation;
	
	public Fault(Operation oper) {
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
		Fault fault = new Fault(getOperation());
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
