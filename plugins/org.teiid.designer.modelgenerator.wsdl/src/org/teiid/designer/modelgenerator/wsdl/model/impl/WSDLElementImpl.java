/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.wsdl.model.impl;

import org.teiid.designer.modelgenerator.wsdl.model.WSDLElement;

public abstract class WSDLElementImpl implements WSDLElement {

	private String m_id;
	private String m_name;
	
	@Override
	public String getName() {
		return m_name;
	}

	@Override
	public void setName(String name) {
		m_name = name;
	}

	@Override
	public String getId() {
		return m_id;		
	}

	@Override
	public void setId(String id) {
		m_id = id;
	}
	
	@Override
    public boolean equals(Object element) {
		//If they are the same type and have the same id then they are equal
		if(element == null || element.getClass() != this.getClass()) return false;
		return (getId().equals(((WSDLElement)element).getId()));
	}
	
	@Override
    public int hashCode() {
		int result = 42;
		result ^= getId().hashCode();		
		return result;
	}

}
