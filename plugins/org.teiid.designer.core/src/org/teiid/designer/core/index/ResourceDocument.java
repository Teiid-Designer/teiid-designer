/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.index;

import org.eclipse.core.resources.IResource;
import org.teiid.designer.core.index.IDocument;

/**
 * ResourceDocument
 *
 * @since 8.0
 */
public interface ResourceDocument extends IDocument {

	/**
	 * Get the {@link org.eclipse.core.resources.IResource} 
	 * instance that this document contains
	 * @return IResource for this document
	 */
	IResource getIResource();
}
