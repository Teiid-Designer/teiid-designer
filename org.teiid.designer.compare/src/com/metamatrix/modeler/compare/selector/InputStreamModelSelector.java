/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.selector;

import java.io.InputStream;
import java.util.List;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.URIConverter;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.resource.RegisteredUriConverter;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * ModelResourceSelector
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
        ArgCheck.isNotNull(stream);
        this.stream = stream;
        this.uri = uri;
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#open()
     */
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
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#getResource()
     */
    @Override
    public Resource getResource() {
        if (this.resource == null) {
            open();
        }
        return this.resource;
    }

    /**
     * @see com.metamatrix.modeler.compare.selector.ModelSelector#getRootObjects()
     */
    public List getRootObjects() {
        return getResource().getContents();
    }

    /**
     * @see com.metamatrix.modeler.compare.processor.ModelSelector#getUri()
     */
    public URI getUri() {
        return getResource().getURI();
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
