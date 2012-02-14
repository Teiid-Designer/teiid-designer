/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.wsdl.model;


public class Part extends WSDLElement {

	
	private String m_elementName;
	private String m_elementNamespace;
	private String m_typeName;
	private String m_typeNamespace;
	private Message m_message;
	
	
	public Part(Message message) {
		m_message = message;
	}
		
	public String getElementName() {
		return m_elementName;
	}

	public void setElementName(String name) {
		m_elementName = name;
	}

	public String getElementNamespace() {
		return m_elementNamespace;
	}

	public void setElementNamespace(String namespace) {
		m_elementNamespace = namespace;
	}

	public String getTypeName() {
		return m_typeName;
	}

	public void setTypeName(String name) {
		m_typeName = name;
	}

	public String getTypeNamespace() {
		return m_typeNamespace;
	}

	public void setTypeNamespace(String namespace) {
		m_typeNamespace = namespace;
	}
	
	public Message getMessage() {
		return m_message;
	}
	
	public boolean isType() {
		return m_typeName != null;
	}
	
	public boolean isElement() {
		return m_elementName != null;
	}

	public WSDLElement copy() {
		Part newImpl = new Part(getMessage());
		newImpl.setName(getName());
		newImpl.setId(getId());
		newImpl.setElementName(getElementName());
		newImpl.setElementNamespace(getElementNamespace());
		newImpl.setTypeName(getTypeName());
		newImpl.setTypeNamespace(getTypeNamespace());
		return newImpl;
	}
	
	@Override
    public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("<part name='"); //$NON-NLS-1$
		buff.append(getName());
		buff.append("' id='"); //$NON-NLS-1$
		buff.append(getId());
		buff.append("'> <element> <name>"); //$NON-NLS-1$
		buff.append(getElementName());
		buff.append("</name> <namespace>"); //$NON-NLS-1$
		buff.append(getElementNamespace());
		buff.append("</namespace> </element> <type> <name>"); //$NON-NLS-1$
		buff.append(getTypeName());
		buff.append("</name> <namespace>"); //$NON-NLS-1$
		buff.append(getTypeNamespace());
		buff.append("</namespace> </type> </part>"); //$NON-NLS-1$
		return buff.toString();		
	}

}

