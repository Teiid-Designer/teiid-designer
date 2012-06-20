/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.selector;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.metamodels.core.ModelAnnotation;


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
        CoreArgCheck.isNotNull(modelUri);
        this.modelUri = modelUri;
    }

    /**
     * @see ModelSelector#open()
     */
    @Override
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
     * Get the resource underlying this selector
     */
    @Override
    public Resource getResource() {
        open();
        return this.resource;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getRootObjects()
     */
    @Override
	public List getRootObjects() {
        open();
        return this.resource.getContents();
    }

    /**
     * @see ModelSelector#getUri()
     */
    @Override
	public URI getUri() {
        return modelUri;
    }

    /**
     * @see ModelSelector#close()
     */
    @Override
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
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelAnnotation()
     */
    @Override
	public ModelAnnotation getModelAnnotation() {
        open();
        return this.contents.getModelAnnotation();
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelHelper()
     */
    @Override
	public ModelHelper getModelHelper() {
        if (this.helper == null) {
            open();
            this.helper = new ModelContentsModelHelper(this.contents);
        }
        return this.helper;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelContents()
     */
    @Override
	public ModelContents getModelContents() {
        open();
        return this.contents;
    }

}
