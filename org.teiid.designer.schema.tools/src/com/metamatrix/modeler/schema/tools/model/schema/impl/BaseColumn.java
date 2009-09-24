/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema.impl;

import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.metamatrix.modeler.schema.tools.model.jdbc.DataType;
import com.metamatrix.modeler.schema.tools.model.jdbc.internal.DataTypeImpl;
import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.QName;
import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;

public abstract class BaseColumn implements Column {
	private SchemaObject table;

	protected boolean pk;

	protected XSDSimpleTypeDefinition type;

	public BaseColumn(boolean pk, XSDSimpleTypeDefinition type) {
		this.pk = pk;
		this.type = type;
		this.table = null;
	}

	public void setTable(SchemaObject table) {
		this.table = table;
	}

	public SchemaObject getTable() {
		return table;
	}

	public boolean isPrimaryKey() {
		return pk;
	}

	public String getBaseType() {
		XSDSimpleTypeDefinition primitiveType = type.getRootTypeDefinition();
		String retval = primitiveType.getName();
		return retval;
	}

	public QName getTypeName() {
		QName retval = new QNameImpl(type.getTargetNamespace(), type.getName());
		return retval;
	}

	public XSDSimpleTypeDefinition getType() {
		return type;
	}

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
