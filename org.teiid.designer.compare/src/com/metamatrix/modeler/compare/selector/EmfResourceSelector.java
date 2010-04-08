/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.selector;

import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * ModelResourceSelector
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
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#open()
     */
    public void open() {
        if (this.resource instanceof EmfResource) {
            this.contents = ((EmfResource)this.resource).getModelContents();
        }
        if (this.contents == null) {
            this.contents = new ModelContents(this.resource);
        }
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#getResource()
     */
    @Override
    public Resource getResource() {
        return this.resource;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getRootObjects()
     */
    public List getRootObjects() {
        return this.resource.getContents();
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#getUri()
     */
    public URI getUri() {
        return resource.getURI();
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#close()
     */
    public void close() {
        this.contents = null;
        this.helper = null;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getLabel()
     */
    public String getLabel() {
        return this.label;
    }

    public void setLabel( final String label ) {
        this.label = label;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getModelAnnotation()
     */
    public ModelAnnotation getModelAnnotation() {
        return this.contents.getModelAnnotation();
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getModelHelper()
     */
    public ModelHelper getModelHelper() {
        if (this.helper == null) {
            this.helper = new ModelContentsModelHelper(this.contents);
        }
        return this.helper;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getModelContents()
     */
    public ModelContents getModelContents() {
        return this.contents;
    }

}
