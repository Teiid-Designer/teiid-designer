/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.index;

import java.io.File;
import org.eclipse.core.resources.IResource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.index.FileDocument;


/**
 * ResourceDocumentImpl
 *
 * @since 8.0
 */
public class ResourceDocumentImpl extends FileDocument implements ResourceDocument {

	private IResource resource;

	public ResourceDocumentImpl(String filePath, final IResource resource) {
		this(new File(filePath), resource);
	}

	public ResourceDocumentImpl(final File file, final IResource resource) {
		super(file);
		CoreArgCheck.isNotNull(resource);
		this.resource = resource;
	}

	public ResourceDocumentImpl(final IResource resource) {
		this(resource.getLocation().toFile(), resource);
	}

	/* (non-Javadoc)
	 * @See org.teiid.designer.core.index.ResourceDocument#getResource()
	 */
	@Override
	public IResource getIResource() {
		return this.resource;
	}

}
