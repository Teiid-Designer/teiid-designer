/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.compare.selector;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.resource.RegisteredUriConverter;
import org.teiid.designer.core.util.ModelContents;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.metamodels.core.ModelAnnotation;


/**
 * ModelResourceSelector
 *
 * @since 8.0
 */
public class InputStreamModelSelector extends TemporaryResourceModelSelector {

    public static final URI XMI_URI = URI.createURI("streamscheme://metamatrix/hard-codedstream.xmi"); //$NON-NLS-1$
    public static final URI XML_URI = URI.createURI("streamscheme://metamatrix/hard-codedstream.xml"); //$NON-NLS-1$
    public static final URI XSD_URI = URI.createURI("streamscheme://metamatrix/hard-codedstream.xsd"); //$NON-NLS-1$
    public static final URI VDB_URI = URI.createURI("streamscheme://metamatrix/hard-codedstream.vdb"); //$NON-NLS-1$    

    private Resource resource;
    private final InputStream stream;
    private ModelContents contents;
    private ModelHelper helper;
    private final URI uri;

    /**
     * Construct an instance of ModelResourceSelector.
     */
    public InputStreamModelSelector( final InputStream stream,
                                     final URI uri ) {
        super();
        CoreArgCheck.isNotNull(stream);
        this.stream = stream;
        this.uri = uri;
    }

    /**
     * @see ModelSelector#open()
     */
    @Override
	public void open() {
        if (this.resource == null) {

            // Create a URIConverter that knows how to load the supplied InputStream ...
            final URIConverter origConverter = this.getResourceSet().getURIConverter();
            final RegisteredUriConverter converter = new RegisteredUriConverter(origConverter);
            converter.register(uri, stream);
            this.getResourceSet().setURIConverter(converter);

            // If a resource exists for this URI then unload it so that it will be
            // reloaded from the input stream associated with this UriConverter
            this.resource = this.getResourceSet().getResource(uri, false);
            if (this.resource != null) {
                this.resource.unload();
            }

            // Load the resource with this URI
            this.resource = this.getResourceSet().getResource(uri, true);
            if (this.resource instanceof EmfResource) {
                this.contents = ((EmfResource)this.resource).getModelContents();
            }
            if (this.contents == null) {
                this.contents = new ModelContents(this.resource);
            }
        }
    }

    /**
     * @see AbstractModelSelector#getResource()
     */
    @Override
    public Resource getResource() {
        if (this.resource == null) {
            open();
        }
        return this.resource;
    }

    /**
     * @see org.teiid.designer.compare.selector.ModelSelector#getRootObjects()
     */
    @Override
	public List getRootObjects() {
        return getResource().getContents();
    }

    /**
     * @see ModelSelector#getUri()
     */
    @Override
	public URI getUri() {
        return getResource().getURI();
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

    /**
     * Check the extention on IPath and return the appropriate temp uri.
     * 
     * @param path The path to the resource
     * @return The temp uri
     * @since 4.2
     */
    public static URI getTemporatyResourceURI( final IPath path ) {
        if (ModelUtil.isXsdFile(path)) {
            return InputStreamModelSelector.XSD_URI;
        } else if (ModelUtil.isModelFile(path)) {
            return InputStreamModelSelector.XMI_URI;
        } else if (ModelUtil.isVdbArchiveFile(path)) {
            return InputStreamModelSelector.VDB_URI;
        }
        return null;
    }

}
