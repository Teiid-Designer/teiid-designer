/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.selector;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.metamodels.core.Annotation;
import org.teiid.designer.metamodels.core.CoreFactory;
import org.teiid.designer.metamodels.core.ModelAnnotation;


public class NullModelSelector implements ModelSelector {

    public static final String DEFAULT_URI = "http://default.model.selector"; //$NON-NLS-1$

    private String label;
    final List rootObjects;
    private final URI uri;
    private final ModelAnnotation modelAnnotation;
    private final ModelHelper modelHelper = new ModelHelper() {
        @Override
		public Annotation getAnnotation( EObject eObject ) {
            return null;
        }
    };
    private final ModelContents contents = new CustomModelContents();

    /**
     * Construct an instance of NullModelSelector.
     */
    public NullModelSelector() {
        this(DEFAULT_URI);
    }

    /**
     * Construct an instance of NullModelSelector.
     */
    public NullModelSelector( final String uri ) {
        super();
        this.rootObjects = new ArrayList();
        this.uri = URI.createURI(uri);
        this.modelAnnotation = CoreFactory.eINSTANCE.createModelAnnotation();
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#open()
     */
    @Override
	public void open() {

    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#close()
     */
    @Override
	public void close() {

    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getLabel()
     */
    @Override
	public String getLabel() {
        return this.label;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#setLabel(java.lang.String)
     */
    @Override
	public void setLabel( final String label ) {
        this.label = label;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getRootObjects()
     */
    @Override
	public List getRootObjects() {
        return this.rootObjects;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getUri()
     */
    @Override
	public URI getUri() {
        return this.uri;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelAnnotation()
     */
    @Override
	public ModelAnnotation getModelAnnotation() {
        return this.modelAnnotation;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelHelper()
     */
    @Override
	public ModelHelper getModelHelper() {
        return this.modelHelper;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getModelContents()
     */
    @Override
	public ModelContents getModelContents() {
        return this.contents;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#addRootObjects(java.util.List)
     */
    @Override
	public void addRootObjects( List newRoots ) {
        this.rootObjects.addAll(newRoots);
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#addRootObjects(java.util.List, int)
     */
    @Override
	public void addRootObjects( List newRoots,
                                int startingIndex ) {
        this.rootObjects.addAll(startingIndex, newRoots);
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#rebuildModelImports()
     */
    @Override
	public void rebuildModelImports() {
        // do nothing
    }

    protected class CustomModelContents extends ModelContents {
        public CustomModelContents() {
            super();
        }

        @Override
        public List getAllRootEObjects() {
            return NullModelSelector.this.rootObjects;
        }

        @Override
        protected URI getUri() {
            return NullModelSelector.this.getUri();
        }

        @Override
        protected void setModified( boolean modified ) {
            // do nothing
        }
    }
}
