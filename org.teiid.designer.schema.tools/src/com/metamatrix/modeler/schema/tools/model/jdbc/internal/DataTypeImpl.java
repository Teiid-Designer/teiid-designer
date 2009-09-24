/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
