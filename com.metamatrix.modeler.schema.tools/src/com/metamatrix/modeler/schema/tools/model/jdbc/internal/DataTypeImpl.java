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

package com.metamatrix.modeler.schema.tools.model.jdbc.internal;

import com.metamatrix.modeler.schema.tools.model.jdbc.DataType;

public class DataTypeImpl implements DataType {

	private String m_type;

	private String m_namespace;

	public DataTypeImpl(String name, String namespace) {
		setTypeName(name);
		setTypeNamespace(namespace);
	}

	public DataTypeImpl() {

	}

	public String getTypeName() {
		return m_type;
	}

	public void setTypeName(String name) {
		m_type = name;
	}

	public String getTypeNamespace() {
		return m_namespace;
	}

	public void setTypeNamespace(String namespace) {
		m_namespace = namespace;
	}

}
