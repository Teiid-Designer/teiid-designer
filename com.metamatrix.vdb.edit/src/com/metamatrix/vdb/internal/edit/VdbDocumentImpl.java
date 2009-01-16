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

package com.metamatrix.vdb.internal.edit;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

import com.metamatrix.core.util.StringUtil;
import com.metamatrix.internal.core.index.FileDocument;
import com.metamatrix.modeler.core.index.VDBDocument;

/**
 * VdbDocumentImpl
 */
public class VdbDocumentImpl extends FileDocument implements VDBDocument {

    private final Collection resources;
    private final String indexName;
    private final Map modelPathsByUri;

    public VdbDocumentImpl(String indexName, final Collection resources, final Map pathsByResourceUri) {
        super(new File(indexName));
        this.resources = resources;
        this.indexName = indexName;
        this.modelPathsByUri = (pathsByResourceUri != null ? pathsByResourceUri : Collections.EMPTY_MAP);
    }

    /**
     * @see com.metamatrix.modeler.core.index.VDBDocument#getResources()
     */
    public Collection getResources() {
        return resources;
    }

    /**
     * @see com.metamatrix.modeler.core.index.VDBDocument#getIndexName()
     */
    public String getIndexName() {
        return indexName;
    }
    
    /** 
     * @see com.metamatrix.modeler.core.index.VDBDocument#getModelPath(org.eclipse.emf.common.util.URI)
     * @since 4.2
     */
    public String getModelPath(final URI resourceURI) {
        final String modelPath = (String) this.modelPathsByUri.get(resourceURI);
        return (modelPath == null ? StringUtil.Constants.EMPTY_STRING : modelPath);
    }
}
