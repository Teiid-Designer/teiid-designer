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