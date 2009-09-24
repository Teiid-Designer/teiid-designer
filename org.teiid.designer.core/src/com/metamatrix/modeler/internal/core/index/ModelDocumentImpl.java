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

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.modeler.core.index.ModelDocument;
import com.metamatrix.modeler.internal.core.workspace.WorkspaceResourceFinderUtil;

/**
 * ModelDocumentImpl
 */
public class ModelDocumentImpl extends ResourceDocumentImpl implements ModelDocument {
    
    private final Resource resource;

    public ModelDocumentImpl(final Resource resource) {
		this(resource.getURI().toFileString(), WorkspaceResourceFinderUtil.findIResource(resource), resource);
    }

	public ModelDocumentImpl(final IResource iResource, final Resource resource) {
		this(iResource.getLocation().toFile(), iResource, resource);
	}

	public ModelDocumentImpl(File file, final IResource iResource, final Resource resource) {
		super(file, iResource);
		this.resource = resource;
	}

	public ModelDocumentImpl(String filePath, final IResource iResource, final Resource resource) {
		super(new File(filePath), iResource);
		ArgCheck.isNotNull(resource);
		this.resource = resource;
	}

    /* (non-Javadoc)
     * @see com.metamatrix.modeler.search.index.ModelDocument#getResource()
     */
    public Resource getResource() {
        return this.resource;
    }

}
