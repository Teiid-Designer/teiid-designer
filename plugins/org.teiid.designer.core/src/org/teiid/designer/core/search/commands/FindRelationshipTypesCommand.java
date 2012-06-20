/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.search.commands;

import java.util.Collection;

import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.search.runtime.RelationshipTypeRecord;

/**
 * This interface is used to find relationship types between models based on specified criteria.
 */
public interface FindRelationshipTypesCommand	extends SearchCommand {

	/**
	 * Return a collection of {@link RelationshipTypeRecord}
	 * objects that are found on this command execution. 
	 * @return a collection of relationship type records
	 */
	Collection getRelationShipTypeInfo();

	/**
	 * Set the IndexSelector that will be used to obtain models that will be searched.
	 * @param selector the index selector that should be used, or null if the 
	 * {@link org.teiid.designer.core.index.ModelWorkspaceIndexSelector} should be used
	 */
	void setIndexSelector( IndexSelector selector );

	/**
	 * Set the name pattern of the relationship type whose record are returned on executing this command.
	 * @param namePattern The name pattern of the relationship types.
	 */
	public void setRelationshipTypeName(String namePattern);

	/**
	 * Set the flag indicating all the subTypes to the types matching the UUID/name pattern be included in the results.
	 * @param includeSubTypes The flag indicating if subTypes of a relationship type need to be returned.
	 */
	public void setIncludeSubTypes(boolean includeSubTypes);	
}
