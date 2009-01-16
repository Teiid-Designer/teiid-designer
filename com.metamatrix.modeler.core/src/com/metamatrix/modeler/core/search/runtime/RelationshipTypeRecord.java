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

package com.metamatrix.modeler.core.search.runtime;

/**
 * RelationshipTypeRecord
 */
public interface RelationshipTypeRecord extends RelationshipSearchRecord {

	/**
	 * Get a the source relationshiprole name for this relationship type.
	 * @return the name of the source relationshiprole
	 */	
	String getSourceRoleName();

	/**
	 * Get a the target relationshiprole name for this relationship type.
	 * @return the name of the target relationshiprole
	 */	
	String getTargetRoleName();

	/**
	 * Get the uuid of the superType for this relationship type. 
	 * @return the uuid of the supertype of this relationship type
	 */
	String getSuperTypeUUID();
}
