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

package com.metamatrix.modeler.core.search.commands;

import java.util.Collection;

import com.metamatrix.modeler.core.index.IndexSelector;

/**
 * This interface is used to find relationship types between models based on specified criteria.
 */
public interface FindRelationshipTypesCommand	extends SearchCommand {

	/**
	 * Return a collection of {@link com.metamatrix.modeler.relationship.search.index.RelationshipTypeRecord}
	 * objects that are found on this command execution. 
	 * @return a collection of relationship type records
	 */
	Collection getRelationShipTypeInfo();

	/**
	 * Set the IndexSelector that will be used to obtain models that will be searched.
	 * @param selector the index selector that should be used, or null if the 
	 * {@link com.metamatrix.modeler.internal.core.index.ModelWorkspaceIndexSelector} should be used
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
