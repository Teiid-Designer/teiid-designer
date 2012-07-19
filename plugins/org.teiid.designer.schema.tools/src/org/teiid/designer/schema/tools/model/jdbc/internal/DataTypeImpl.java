/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.jdbc.internal;

import org.teiid.designer.schema.tools.model.jdbc.DataType;

/**
 * @since 8.0
 */
public class DataTypeImpl implements DataType {

	private String m_type;

	private String m_namespace;

	public DataTypeImpl(String name, String namespace) {
		setTypeName(name);
		setTypeNamespace(namespace);
	}

	public DataTypeImpl() {

	}

	@Override
	public String getTypeName() {
		return m_type;
	}

	@Override
	public void setTypeName(String name) {
		m_type = name;
	}

	@Override
	public String getTypeNamespace() {
		return m_namespace;
	}

	@Override
	public void setTypeNamespace(String namespace) {
		m_namespace = namespace;
	}

}
