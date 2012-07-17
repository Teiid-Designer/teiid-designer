/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.schema.impl;

import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.teiid.designer.schema.tools.model.jdbc.DataType;
import org.teiid.designer.schema.tools.model.jdbc.internal.DataTypeImpl;
import org.teiid.designer.schema.tools.model.schema.Column;
import org.teiid.designer.schema.tools.model.schema.QName;
import org.teiid.designer.schema.tools.model.schema.Relationship;
import org.teiid.designer.schema.tools.model.schema.SchemaObject;

public abstract class BaseColumn implements Column {
	private SchemaObject table;

	protected boolean pk;

	protected XSDSimpleTypeDefinition type;

	public BaseColumn(boolean pk, XSDSimpleTypeDefinition type) {
		this.pk = pk;
		this.type = type;
		this.table = null;
	}

	@Override
	public void setTable(SchemaObject table) {
		this.table = table;
	}

	@Override
	public SchemaObject getTable() {
		return table;
	}

	@Override
	public boolean isPrimaryKey() {
		return pk;
	}

	@Override
	public String getBaseType() {
		XSDSimpleTypeDefinition primitiveType = type.getRootTypeDefinition();
		String retval = primitiveType.getName();
		return retval;
	}

	@Override
	public QName getTypeName() {
		QName retval = new QNameImpl(type.getTargetNamespace(), type.getName());
		return retval;
	}

	@Override
	public XSDSimpleTypeDefinition getType() {
		return type;
	}

	@Override
	public Column mergeIntoParent(Relationship tableRelationship, int iOccurence) {
		Column mergedColumn = new MergedColumn(this, tableRelationship,
				iOccurence);
		SchemaObject parent = tableRelationship.getParent();
		parent.addAttribute(mergedColumn);
		return mergedColumn;
	}

	@Override
    public String toString() {
		return getSimpleName();
	}
	
	protected DataType getDataType() {
		String typeName = type.getName();
		String namespace = type.getTargetNamespace();
		if(null == typeName) {
			typeName = type.getRootType().getName();
			namespace = type.getRootType().getTargetNamespace();
		}

		return new DataTypeImpl(typeName, namespace);
	}
}
