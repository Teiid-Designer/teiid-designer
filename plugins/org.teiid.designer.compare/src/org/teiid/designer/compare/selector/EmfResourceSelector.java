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
 * ModelResourceSelector
 *
 * @since 8.0
 */
public class EmfResourceSelector extends AbstractModelSelector {

    private final Resource resource;
    private String label;
    private ModelContents contents;
    private ModelHelper helper;

    /**
     * Construct an instance of ModelResourceSelector.
     */
    public EmfResourceSelector( final Resource resource ) {
        super();
        CoreArgCheck.isNotNull(resource);
        this.resource = resource;
    }

    /**
     * @see ModelSelector#open()
     */
    @Override
	public void open() {
        if (this.resource instanceof EmfResource) {
            this.contents = ((EmfResource)this.resource).getModelContents();
        }
        if (this.contents == null) {
            this.contents = new ModelContents(this.resource);
        }
    }

    /**
     * Return the resource of this selector
     */
    @Override
    public Resource getResource() {
        return this.resource;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getRootObjects()
     */
    @Override
	public List getRootObjects() {
        return this.resource.getContents();
    }

    /**
     * @see ModelSelector#getUri()
     */
    @Override
	public URI getUri() {
        return resource.getURI();
    }

    /**
     * @see ModelSelector#close()
     */
    @Override
	public void close() {
        this.contents = null;
        this.helper = null;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getLabel()
     */
    @Override
	public String getLabel() {
        return this.label;
    }

    @Override
	public void setLabel( final String label ) {
        this.label = label;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelAnnotation()
     */
    @Override
	public ModelAnnotation getModelAnnotation() {
        return this.contents.getModelAnnotation();
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelHelper()
     */
    @Override
	public ModelHelper getModelHelper() {
        if (this.helper == null) {
            this.helper = new ModelContentsModelHelper(this.contents);
        }
        return this.helper;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelContents()
     */
    @Override
	public ModelContents getModelContents() {
        return this.contents;
    }

}
