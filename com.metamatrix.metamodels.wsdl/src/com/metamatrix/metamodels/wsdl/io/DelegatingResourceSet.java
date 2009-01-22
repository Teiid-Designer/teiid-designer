/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;


/** 
 * @since 4.2
 */
public class DelegatingResourceSet extends ResourceSetImpl {

    private List readOnlyResourceSets;
    /**
     * Constructor for EmfResourceSetImpl.
     * @param container The {@link Container} referencing this
     */
    public DelegatingResourceSet() {
        super();
        this.readOnlyResourceSets = new ArrayList();
    }
    
    
    /** 
     * @see org.eclipse.emf.ecore.resource.impl.ResourceSetImpl#getResource(org.eclipse.emf.common.util.URI, boolean)
     * @since 4.2
     */
    @Override
    public Resource getResource(URI uri,
                                boolean loadOnDemand) {
        return super.getResource(uri, loadOnDemand);
    }

    /**
     * Returns a resolved resource available outside of the resource set.
     * It is called by {@link #getResource(URI, boolean) getResource(URI, boolean)} 
     * after it has determined that the URI cannot be resolved 
     * based on the existing contents of the resource set.
     * @param uri the URI
     * @param loadOnDemand whether demand loading is required.
     */
    @Override
    protected Resource delegatedGetResource(URI uri, boolean loadOnDemand) {
        // Check all read-only resource sets for this URI
        final Iterator iter = this.readOnlyResourceSets.iterator();
        while (iter.hasNext()) {
            ResourceSet resourceSet = (ResourceSet)iter.next();
            Resource resource = resourceSet.getResource(uri,false);
            if (resource != null) {
                return resource;
            }
        }
        
        return super.delegatedGetResource(uri,loadOnDemand);
    }
    
    /**
     * Add a ResourceSet to be used for resolution of a resource URI.  The
     * specified ResourceSet will be treated as read-only and will never be
     * used to load a resource for the URI being checked.
     * @param listener
     */
    public void addDelegateResourceSet(final ResourceSet resourceSet) {
        if (resourceSet != null) {
            this.readOnlyResourceSets.add(resourceSet);
        }
    }
    
}
