/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.wsdl.model;


public abstract class WSDLElement {

	private String m_id;
	private String m_name;
	
	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		m_name = name;
	}

	public String getId() {
		return m_id;		
	}

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
