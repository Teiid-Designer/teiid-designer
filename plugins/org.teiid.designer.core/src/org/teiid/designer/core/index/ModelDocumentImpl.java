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
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;


/**
 * ModelDocumentImpl
 *
 * @since 8.0
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
		CoreArgCheck.isNotNull(resource);
		this.resource = resource;
	}

    /* (non-Javadoc)
     * @See org.teiid.designer.search.index.ModelDocument#getResource()
     */
    @Override
	public Resource getResource() {
        return this.resource;
    }

}
