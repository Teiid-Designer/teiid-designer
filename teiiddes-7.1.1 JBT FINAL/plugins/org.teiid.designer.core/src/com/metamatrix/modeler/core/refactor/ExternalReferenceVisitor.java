/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.refactor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.metamodels.diagram.Diagram;
import com.metamatrix.metamodels.diagram.DiagramEntity;
import com.metamatrix.modeler.core.util.ModelVisitor;

/**
 * The ExternalReferenceVisitor walks a model (or part of a model) and records the references to external resources.
 */
public class ExternalReferenceVisitor implements ModelVisitor {

    public static final boolean DEFAULT_RESOLVE_REFERENCE = false;
    public static final boolean DEFAULT_INCLUDE_RESOLVED_REFERENCES = true;
    public static final boolean DEFAULT_INCLUDE_DIAGRAM_REFERENCES = true;

    private final Map referencedObjectsByResourceUri;
    private final Resource resource;
    private boolean includeResolvedReferences = DEFAULT_INCLUDE_RESOLVED_REFERENCES;
    private boolean includeDiagramReferences = DEFAULT_INCLUDE_DIAGRAM_REFERENCES;

    /**
     * Construct an instance of ExternalReferenceVisitor.
     * 
     * @param resource the resource that this visitor is visiting; may not be null
     */
    public ExternalReferenceVisitor( final Resource resource ) {
        super();
        CoreArgCheck.isNotNull(resource);
        this.resource = resource;
        this.referencedObjectsByResourceUri = new HashMap();
    }

    /**
     * Return the resource that this object is visiting over.
     * 
     * @return the resource; never null
     */
    public Resource getResource() {
        return this.resource;
    }

    /**
     * @return
     */
    public boolean isIncludeResolvedReferences() {
        return this.includeResolvedReferences;
    }

    /**
     * @param b
     */
    public void setIncludeResolvedReferences( boolean b ) {
        this.includeResolvedReferences = b;
    }

    /**
     * @return
     */
    public boolean isIncludeDiagramReferences() {
        return this.includeDiagramReferences;
    }

    /**
     * @param b
     */
    public void setIncludeDiagramReferences( boolean b ) {
        this.includeDiagramReferences = b;
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.resource.Resource)
     */
    public boolean visit( final Resource resource ) {
        // Nothing to do for a resource, but we want to visit the children
        return true;
    }

    /**
     * This method should be called only on objects by following ownership references.
     * 
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean visit( final EObject object ) {
        // If we are ignoring external references by a DiagramEntry then return ...
        if (!this.includeDiagramReferences && (object instanceof DiagramEntity || object instanceof Diagram)) {
            return false;
        }
        // Iterate over all the features that are non-containment ...
        final Iterator iter = object.eClass().getEAllReferences().iterator();
        while (iter.hasNext()) {
            final EReference eReference = (EReference)iter.next();
            if (!eReference.isContainment() && !eReference.isContainer() && !eReference.isVolatile()) {
                // The reference is NOT the container NOR a containment feature ...
                final Object value = object.eGet(eReference, DEFAULT_RESOLVE_REFERENCE);
                if (eReference.isMany()) {
                    // There may be many values ...
                    final Iterator valueIter = ((List)value).iterator();
                    while (valueIter.hasNext()) {
                        final Object valueInList = valueIter.next();
                        if (valueInList instanceof EObject) {
                            processReference((EObject)valueInList);
                        }
                    }
                } else {
                    // There may be 0..1 value ...
                    if (value != null && value instanceof EObject) {
                        processReference((EObject)value);
                    }
                }
            }
        }
        return true; // always visit children ...
    }

    /**
     * @see com.metamatrix.modeler.core.util.ModelVisitor#visit(org.eclipse.emf.ecore.EObject)
     */
    public boolean processReference( final EObject object ) {

        if (object == null) {
            return false;
        }
        // Check if the object is an EMF proxy ...
        if (object.eIsProxy()) {
            if (object instanceof InternalEObject) {
                final InternalEObject iObject = (InternalEObject)object;
                final URI proxyUri = iObject.eProxyURI();
                CoreArgCheck.isNotNull(proxyUri);
                recordResourceUsage(proxyUri, object);
            }
            return false;
        }

        // It might be resolved but still be an object in another resource
        final Resource resourceForObject = object.eResource();
        if (resourceForObject == this.resource) {
            // The object is in the resource being visited
            return false;
        }

        // The object is NOT in the resource ...
        if (resourceForObject != null) { // may be null if ref'ed object is transient
            // Record it's usage
            recordResourceUsage(resourceForObject, object);
        }
        return false;
    }

    /**
     * Return the collection of {@link ExternalReferenceVisitor.ExternalReferences ExternalReferences}.
     * 
     * @return the collection of external references; never null
     */
    public Collection getExternalReferences() {
        return this.referencedObjectsByResourceUri.values();
    }

    /**
     * Record that the supplied resource was referenced by an object in this resource. This method is called when the EObject is
     * resolved.
     * 
     * @param externalResource the external resource referenced by this resource; never null
     * @param referencedObject the referenced object that is in the external resource; never null
     */
    protected void recordResourceUsage( final Resource externalResource,
                                        final EObject referencedObject ) {
        // Get the URI of the resource ...
        final URI resourceUri = externalResource.getURI();

        // And find the list of external references for that resource ...
        final ExternalReferences refedObjs = getReferencedObjects(resourceUri, true);
        refedObjs.setResource(externalResource);
        if (this.includeResolvedReferences) {
            refedObjs.addReferencedObject(referencedObject);
        }
    }

    /**
     * Record that the supplied object URI to an external object was used in this resource. This method is called when the
     * referenced object has not yet been resolved.
     * 
     * @param uriToExternalObject the URI to the external object; never null
     * @param referencedObject the referenced object that is in the external resource; never null
     */
    protected void recordResourceUsage( final URI uriToExternalObject,
                                        final EObject referencedObject ) {
        // Get the URI of the resource ...
        URI resourceUri = uriToExternalObject.trimFragment();
        // Make the relative URI absolute if necessary
        if (resourceUri != null && resourceUri.isRelative()) {
            resourceUri = resourceUri.resolve(resource.getURI());
        }

        // And find the list of external references for that resource ...
        final ExternalReferences refedObjs = getReferencedObjects(resourceUri, true);
        refedObjs.addReferencedObject(referencedObject);
    }

    /**
     * Helper to find the referenced object container for the resource with the supplied URI.
     * 
     * @param resourceUri the URI of the resource; may not be null
     * @param createIfNeeded true if a ExternalReferences object should be created for the supplied resource URI if none already
     *        exists, or false otherwise.
     * @return the ExternalReferences for the resource URI; null only if there was no collection for the reosurce and
     *         <code>createIfNeeded</code> was false
     */
    protected ExternalReferences getReferencedObjects( final URI resourceUri,
                                                       final boolean createIfNeeded ) {
        ExternalReferences refedObjs = (ExternalReferences)this.referencedObjectsByResourceUri.get(resourceUri);
        if (refedObjs == null && createIfNeeded) {
            // First time we've seen the resource, so create the container
            refedObjs = new ExternalReferences(resourceUri);
            this.referencedObjectsByResourceUri.put(resourceUri, refedObjs);
        }
        return refedObjs;
    }

    public class ExternalReferences {
        private final URI resourceUri;
        private final Set objects;
        private Resource resource;

        protected ExternalReferences( final URI resourceUri ) {
            CoreArgCheck.isNotNull(resourceUri);
            this.resourceUri = resourceUri;
            this.objects = new HashSet();
        }

        /**
         * @param referencedObject
         */
        protected void addReferencedObject( EObject referencedObject ) {
            this.objects.add(referencedObject);
        }

        /**
         * @return
         */
        public Collection getReferencedObjects() {
            return objects;
        }

        /**
         * @return
         */
        public URI getResourceUri() {
            return resourceUri;
        }

        /**
         * @return
         */
        public Resource getResource() {
            return resource;
        }

        /**
         * @param resource
         */
        protected void setResource( final Resource resource ) {
            if (this.resource != null) {
                CoreArgCheck.isTrue(resource == this.resource, "Resource cannot be reset to itself"); //$NON-NLS-1$
                return;
            }
            this.resource = resource;
        }

    }

}
