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

import com.metamatrix.modeler.modelgenerator.wsdl.model.WSDLElement;

public abstract class WSDLElementImpl implements WSDLElement {

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
