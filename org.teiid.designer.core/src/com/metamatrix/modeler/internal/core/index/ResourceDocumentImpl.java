/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.File;

import org.eclipse.core.resources.IResource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.internal.core.index.FileDocument;
import com.metamatrix.modeler.core.index.ResourceDocument;

/**
 * ResourceDocumentImpl
 */
public class ResourceDocumentImpl extends FileDocument implements ResourceDocument {

	private IResource resource;

	public ResourceDocumentImpl(String filePath, final IResource resource) {
		this(new File(filePath), resource);
	}

	public ResourceDocumentImpl(final File file, final IResource resource) {
		super(file);
		ArgCheck.isNotNull(resource);
		this.resource = resource;
	}

	public ResourceDocumentImpl(final IResource resource) {
		this(resource.getLocation().toFile(), resource);
	}

	/* (non-Javadoc)
	 * @see com.metamatrix.modeler.core.index.ResourceDocument#getResource()
	 */
	public IResource getIResource() {
		return this.resource;
	}

}
