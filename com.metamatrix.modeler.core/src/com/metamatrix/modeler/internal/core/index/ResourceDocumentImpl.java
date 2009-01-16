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
