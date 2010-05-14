/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model;


public interface Relationship {

	void setParentTable(String name);

	void setChildTable(String childSObject);

	void setForeignKeyField(String field);

	void setCascadeDelete(boolean b);

	public String getChildTable();

	public String getForeignKeyField();

	public String getParentTable();

	public boolean relatesToAuditField();

}
