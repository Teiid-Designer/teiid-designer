/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.search.runtime;

/**
 * RelationshipSearchRecord
 */
public interface RelationshipSearchRecord extends SearchRecord {

	/**
	 * Return the URI for the entity this record represents.
	 * @return URI for the Record.
	 */
	String getUri();

	/**
	 * Returns the name of the entity
	 * @return the name of the entity
	 */
	String getName();	
}
