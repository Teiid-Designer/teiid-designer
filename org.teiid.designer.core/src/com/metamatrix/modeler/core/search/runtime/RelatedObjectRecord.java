/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.search.runtime;


/**
 * RelatedObjectRecord
 */
public interface RelatedObjectRecord extends RelationshipSearchRecord {

	/**
	 * Returns the UUID of the relationship this object is involved in.
	 * @return the UUID of the relationship
	 */
	String getRelationshipUUID();

	/**
	 * Returns the UUID of the object at the other end of the relationship
	 * @return the UUID of the object at the other end of the relationship
	 */
	String getRelatedObjectUUID();
	
	/**
	 * Returns the meta class URI of this object.
	 * @return the meta class URI
	 */
	String getMetaClassUri();

	/**
	 * Returns the meta class URI of the related object.
	 * @return the meta class URI
	 */
	String getRelatedMetaClassUri();

	/**
	 * Returns boolean indicating if the object is involved as
	 * a source in a relationship.
	 * @return boolean indicating if the object is involved as a source
	 */
	boolean isSourceObject();

	/**
	 * Return the URI for the related entity this entity has a relationship with.
	 * @return URI for the Record.
	 */
	String getRelatedObjectUri();

	/**
	 * Return the role name for the entity this record represents.
	 * @return role name for the Record.
	 */
	String getRoleName();

	/**
	 * Return the role name for the related entity this entity has a relationship with.
	 * @return role name for the Record.
	 */
	String getRelatedRoleName();

	/**
	 * Return the name for the related entity involved in the relationship.
	 * May be null
	 * @return name of the object for this related record.
	 */
	String getRelatedObjectName();

	/**
	 * Return the path to the resource of the entity this record represents.
	 * @return path for the resource of the entity.
	 */
	String getResourcePath();

	/**
	 * Return the path to the resource of the related entity.
	 * @return path for the resource of the related entity.
	 */
	String getRelatedResourcePath();	
}
