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
