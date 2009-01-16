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

package com.metamatrix.modeler.core.index;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import com.metamatrix.core.index.IIndexer;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * ResourceIndexer
 */
public interface ResourceIndexer extends IIndexer {

	/**
	 * Index the given IResource, creates one index per resource. 
	 * @param resource The IResource to be indexed
	 * @param reuseExistingFile
	 * @param addResource
	 * @param context
	 * @throws ModelerCoreException
	 */
	void indexResource(IResource resource, boolean reuseExistingFile, boolean addResource) throws ModelerCoreException;

	/**
	 * Index the resource at the given IPath, creates one index per resource.
	 * @param resource The IResource to be indexed
	 * @param reuseExistingFile
	 * @param addResource
	 * @param context
	 * @throws ModelerCoreException
	 */
	void indexResource(IPath path, boolean reuseExistingFile, boolean addResource) throws ModelerCoreException;

	/**
	 * Get the type of indexes created by this indexer.
	 * @return The index type for this indexer.
	 */
	String getIndexType(); 
}
