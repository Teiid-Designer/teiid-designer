/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl.model.internal;

import com.metamatrix.modeler.modelgenerator.wsdl.model.Model;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Port;
import com.metamatrix.modeler.modelgenerator.wsdl.model.Service;
import com.metamatrix.modeler.modelgenerator.wsdl.model.WSDLElement;

public class ServiceImpl extends WSDLElementImpl implements Service {

	private Port[] m_ports;
	private Model m_model;
	
	public Port[] getPorts() {
		if(m_ports == null) return null;
		//defensive copy of Port array
		int arrayLength = m_ports.length;
		Port[] retPorts = new Port[arrayLength];
		for(int i = 0; i < arrayLength; i++) {
			retPorts[i] = (Port) m_ports[i].copy();
		}
		return retPorts;
	}



	public WSDLElement copy() {
		ServiceImpl newImpl = new ServiceImpl();
		newImpl.setId(getId());
		newImpl.setName(getName());
		newImpl.setPorts(getPorts());
		return newImpl;		
	}

	public void setPorts(Port[] ports) {
		m_ports = ports;		
	}	
	
	@Override
    public String toString() {
		
		StringBuffer buff = new StringBuffer();
		buff.append("<service name='"); //$NON-NLS-1$
		buff.append(getName());
		buff.append("' id='" ); //$NON-NLS-1$
		buff.append(getId());
		buff.append("'>"); //$NON-NLS-1$
		for(int i = 0; i < m_ports.length; i++) {
			buff.append(m_ports[i].toString());			
		}
		buff.append("</service>"); //$NON-NLS-1$
		return buff.toString();
	}



	public void setModel(Model theModel) {
		m_model = theModel;
	}



	public Model getModel() {
		return m_model;
	}
}
