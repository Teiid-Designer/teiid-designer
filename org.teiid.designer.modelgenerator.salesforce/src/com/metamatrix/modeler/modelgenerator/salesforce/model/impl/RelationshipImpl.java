/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;

import com.metamatrix.modeler.modelgenerator.salesforce.model.Relationship;

public class RelationshipImpl implements Relationship {

	private boolean cascadeDelete;
	public String childTablename;
	public String parentTableName;
	public String foreignKeyField;
	
	public void setCascadeDelete(boolean delete) {
		cascadeDelete = delete;
	}

	public boolean isCascadeDelete() {
		return cascadeDelete;
	}

	public void setChildTable(String childTable) {
		childTablename = childTable;
	}

	public String getChildTable() {
		return childTablename;
	}

	public String getForeignKeyField() {
		return foreignKeyField;
	}

	public void setForeignKeyField(String foreignKeyField) {
		this.foreignKeyField = foreignKeyField;
	}

	public String getParentTable() {
		return parentTableName;
	}

	public void setParentTable(String parentTableName) {
		this.parentTableName = parentTableName;
	}
	
	public boolean relatesToAuditField() {
		return SalesforceFieldImpl.isAuditField(foreignKeyField);
	}
}
