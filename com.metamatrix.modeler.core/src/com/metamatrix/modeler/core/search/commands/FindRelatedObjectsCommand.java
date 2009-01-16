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
