/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.search.commands;

import java.util.Collection;

import com.metamatrix.modeler.core.index.IndexSelector;

/**
 * Command that helps find all the objects that have relationships to a given object.
 */
public interface FindRelatedObjectsCommand extends SearchCommand {

	/**
	 * Set the IndexSelector that will be used to obtain models that will be searched.
	 * @param selector the index selector that should be used, or null if the 
	 * {@link com.metamatrix.modeler.internal.core.index.ModelWorkspaceIndexSelector} should be used
	 */
	void setIndexSelector(IndexSelector selector );

	/**
	 * Set the uri to the modelObject whose related objects are returned on executing this command.
	 * @param The modelObject uri for which related objects are returned.
	 */
	public void setModelObjectUri(String uri);
	
	/**
	 * Return a collection of {@link com.metamatrix.modeler.relationship.search.index.RelatedObjectRecord}
	 * objects.
	 * @return a collection of search records that contain related object info.
	 */
	public Collection getRelatedObjectInfo();

}
