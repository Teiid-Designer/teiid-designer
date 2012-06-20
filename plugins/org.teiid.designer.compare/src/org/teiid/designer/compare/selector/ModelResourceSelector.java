/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.selector;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCoreException;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.metamodels.core.ModelAnnotation;


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
        CoreArgCheck.isNotNull(modelResource);
        this.modelResource = modelResource;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#open()
     */
    @Override
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
     * @see org.teiid.designer.compare.selector.ModelSelector#getRootObjects()
     */
    @Override
	public List getRootObjects() throws ModelerCoreException {
        return this.getResource().getContents();
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getUri()
     */
    @Override
	public URI getUri() {
        final IPath path = this.modelResource.getPath();
        final URI result = URI.createFileURI(path.toFile().getAbsolutePath());
        return result;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#close()
     */
    @Override
	public void close() {
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
	public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelAnnotation()
     */
    @Override
	public ModelAnnotation getModelAnnotation() throws ModelWorkspaceException {
        return this.modelResource.getModelAnnotation();
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelHelper()
     */
    @Override
	public ModelHelper getModelHelper() throws ModelWorkspaceException {
        if (this.helper == null) {
            this.helper = new ModelContentsModelHelper(getModelContents());
        }
        return this.helper;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelContents()
     */
    @Override
	public ModelContents getModelContents() throws ModelWorkspaceException {
        if (this.contents == null) {
            this.contents = ModelContents.getModelContents(this.modelResource);
        }
        return this.contents;
    }
}
