/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.schema;


public interface Relationship {
	public static final int UNBOUNDED = -1;

	public static final int KEY_IN_PARENT_SINGLE = 0;

	public static final int KEY_IN_PARENT_MULTIPLE = 1;

	public static final int MERGE_IN_PARENT_SINGLE = 2;

	public static final int MERGE_IN_PARENT_MULTIPLE = 3;

	public static final int KEY_IN_CHILD = 4;

	public static final int RELATIONSHIP_TABLE = 5;

	public String getParentRelativeXpath();

	public String getChildRelativeXpath();

	public SchemaObject getParent();

	public SchemaObject getChild();

	public int getMinOccurs();

	public int getMaxOccurs();

	public void setType(int relationshipType);
	
	public int getType();

	public void printDebug();
	
	public void removeRelationship();
	
	public void addNewRelationship();
	
	public Relationship merge(Relationship grandChildRelation);
}
