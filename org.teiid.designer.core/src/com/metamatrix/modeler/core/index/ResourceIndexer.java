/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
