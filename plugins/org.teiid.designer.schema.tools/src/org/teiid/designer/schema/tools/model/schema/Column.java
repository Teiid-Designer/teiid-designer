/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.schema;

/**
 * @since 8.0
 */
public interface Column extends Type {
	public String getXpath();

	public String getSimpleName();

	public SchemaObject getTable();

	public boolean isPrimaryKey();

	public void setTable(SchemaObject table);

	public Column mergeIntoParent(Relationship tableRelationship, int iOccurence);

	public Column copy();

	public void printDebug();

	public org.teiid.designer.schema.tools.model.jdbc.Column getColumnImplementation();
}
