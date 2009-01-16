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

package com.metamatrix.modeler.compare.selector;

import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * The URIModelSelector represents a selector for a model that exists and can be loaded via a URI.
 */
public class URIModelSelector extends TemporaryResourceModelSelector {

    protected Resource resource;
    private final URI modelUri;
    protected ModelContents contents;
    private ModelHelper helper;

    /**
     * Construct an instance of ModelResourceSelector.
     */
    public URIModelSelector( final URI modelUri ) {
        super();
        ArgCheck.isNotNull(modelUri);
        this.modelUri = modelUri;
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#open()
     */
    public void open() {
        if (this.resource == null) {
            this.resource = this.getResourceSet().getResource(modelUri, true);
            if (this.resource instanceof EmfResource) {
                this.contents = ((EmfResource)this.resource).getModelContents();
            }
            if (this.contents == null) {
                this.contents = new ModelContents(this.resource);
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#getResource()
     */
    @Override
    public Resource getResource() {
        open();
        return this.resource;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getRootObjects()
     */
    public List getRootObjects() {
        open();
        return this.resource.getContents();
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#getUri()
     */
    public URI getUri() {
        return modelUri;
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#close()
     */
    public void close() {
        try {
            if (this.resource != null) {
                this.resource.unload();
            }
        } finally {
            this.contents = null;
            this.helper = null;
            this.resource = null;
        }
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getModelAnnotation()
     */
    public ModelAnnotation getModelAnnotation() {
        open();
        return this.contents.getModelAnnotation();
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getModelHelper()
     */
    public ModelHelper getModelHelper() {
        if (this.helper == null) {
            open();
            this.helper = new ModelContentsModelHelper(this.contents);
        }
        return this.helper;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getModelContents()
     */
    public ModelContents getModelContents() {
        open();
        return this.contents;
    }

}
