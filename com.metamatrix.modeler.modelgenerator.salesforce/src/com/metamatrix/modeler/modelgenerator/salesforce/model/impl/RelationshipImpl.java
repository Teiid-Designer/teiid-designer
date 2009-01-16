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
