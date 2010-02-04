/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.selector;

import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * ModelResourceSelector
 */
public class ModelResourceSelector extends AbstractModelSelector {

    private final ModelResource modelResource;

    private ModelContents contents;

    private String label;

    private ModelHelper helper;

    /**
     * Construct an instance of ModelResourceSelector.
     */
    public ModelResourceSelector(final ModelResource modelResource) {
        super();
        ArgCheck.isNotNull(modelResource);
        this.modelResource = modelResource;
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#open()
     */
    public void open() {
        // don't do anything ...
    }

    @Override
    public Resource getResource() throws ModelerCoreException {
        return this.modelResource.getEmfResource();
    }

    /**
     * @return The model to which the contents of this selector belong.
     * @since 4.1
     */
    public ModelResource getModelResource() {
        return this.modelResource;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getRootObjects()
     */
    public List getRootObjects() throws ModelerCoreException {
        return this.getResource().getContents();
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#getUri()
     */
    public URI getUri() {
        final IPath path = this.modelResource.getPath();
        final URI result = URI.createFileURI(path.toFile().getAbsolutePath());
        return result;
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#close()
     */
    public void close() {
        this.helper = null;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getLabel()
     */
    public String getLabel() {
        return this.label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getModelAnnotation()
     */
    public ModelAnnotation getModelAnnotation() throws ModelWorkspaceException {
        return this.modelResource.getModelAnnotation();
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getModelHelper()
     */
    public ModelHelper getModelHelper() throws ModelWorkspaceException {
        if (this.helper == null) {
            this.helper = new ModelContentsModelHelper(getModelContents());
        }
        return this.helper;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getModelContents()
     */
    public ModelContents getModelContents() throws ModelWorkspaceException {
        if (this.contents == null) {
            this.contents = ModelContents.getModelContents(this.modelResource);
        }
        return this.contents;
    }
}
