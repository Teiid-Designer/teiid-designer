/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model.impl;

import org.teiid.designer.modelgenerator.wsdl.model.Message;
import org.teiid.designer.modelgenerator.wsdl.model.Part;
import org.teiid.designer.modelgenerator.wsdl.model.WSDLElement;

public class PartImpl extends WSDLElementImpl implements Part {

	
	private String m_elementName;
	private String m_elementNamespace;
	private String m_typeName;
	private String m_typeNamespace;
	private Message m_message;
	
	
	public PartImpl(Message message) {
		m_message = message;
	}
		
	@Override
	public String getElementName() {
		return m_elementName;
	}

	@Override
	public void setElementName(String name) {
		m_elementName = name;
	}

	@Override
	public String getElementNamespace() {
		return m_elementNamespace;
	}

	@Override
	public void setElementNamespace(String namespace) {
		m_elementNamespace = namespace;
	}

	@Override
	public String getTypeName() {
		return m_typeName;
	}

	@Override
	public void setTypeName(String name) {
		m_typeName = name;
	}

	@Override
	public String getTypeNamespace() {
		return m_typeNamespace;
	}

	@Override
	public void setTypeNamespace(String namespace) {
		m_typeNamespace = namespace;
	}
	
	@Override
	public Message getMessage() {
		return m_message;
	}
	
	@Override
	public boolean isType() {
		return m_typeName != null;
	}
	
	@Override
	public boolean isElement() {
		return m_elementName != null;
	}

	@Override
	public WSDLElement copy() {
		PartImpl newImpl = new PartImpl(getMessage());
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
