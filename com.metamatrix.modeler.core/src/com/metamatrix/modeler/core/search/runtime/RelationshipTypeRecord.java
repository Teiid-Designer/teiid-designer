/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
