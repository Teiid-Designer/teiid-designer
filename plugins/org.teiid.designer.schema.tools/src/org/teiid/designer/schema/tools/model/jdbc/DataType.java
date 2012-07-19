/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.jdbc;

/**
 * @since 8.0
 */
public interface DataType {

	public String getTypeName();

	public void setTypeName(String name);

	public String getTypeNamespace();

	public void setTypeNamespace(String namespace);

}
