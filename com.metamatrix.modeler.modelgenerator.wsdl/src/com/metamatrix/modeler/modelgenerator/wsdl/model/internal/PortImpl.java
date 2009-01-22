/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Binding;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Service;
import com.metamatrix.modeler.modelgenerator.wsdl.model.WSDLElement;

public class PortImpl extends WSDLElementImpl implements Port {
	
	
	private Service m_parent;
	private Binding m_binding;
	private String m_locationURI;
	
	public PortImpl(Service parent) {
		m_parent = parent;
	}

	public Binding getBinding() {
		//defensive copy of bindings
		return (Binding) m_binding.copy();
	}
	
	public void setBinding(Binding binding) {
		m_binding = binding;
	}

	public Service getService() {
		return m_parent;
	}

	public void setLocationURI(String uri){
		m_locationURI = uri;
	}
	
	public String getLocationURI() {
		return m_locationURI;
	}
	
	public WSDLElement copy() {
		PortImpl impl = new PortImpl(getService());
		impl.setName(getName());
		impl.setId(getId());
		impl.setBinding(getBinding());
		return impl;
	}
	
	@Override
    public String toString() {
		StringBuffer buff = new StringBuffer();
		buff.append("<port name='");		 //$NON-NLS-1$
		buff.append(getName());
		buff.append("' id='"); //$NON-NLS-1$
		buff.append(getId());
		buff.append("'>"); //$NON-NLS-1$
		buff.append(m_binding.toString());
		buff.append("</port>"); //$NON-NLS-1$
		return buff.toString();
	}

}
