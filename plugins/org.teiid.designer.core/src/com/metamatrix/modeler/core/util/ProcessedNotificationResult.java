/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.emf.ecore.resource.Resource;


/** 
 * @since 5.0
 */
public class ProcessedNotificationResult {
    
    private Collection dereferencedResources = new HashSet();
    private Resource targetResource; 
    private boolean importsWereAdded = false;

    /** 
     * 
     * @since 5.0
     */
    public ProcessedNotificationResult(Resource target) {
        super();
        this.targetResource = target;
    }
    
    /** 
     * 
     * @since 5.0
     */
    public ProcessedNotificationResult(ProcessedNotificationResult result) {
        super();
        this.targetResource = result.getTargetResource();
        this.dereferencedResources.addAll(result.getDereferencedResources());
    }
    
    public Resource getTargetResource() {
        return this.targetResource;
    }

    
    public Collection getDereferencedResources() {
        return this.dereferencedResources;
    }

    public void addDereferencedResource(Resource resource) {
        if( resource != null ) {
            dereferencedResources.add(resource);
        }
    }
    
    public void removeDereferencedResource(Resource resource) {
        if( resource != null ) {
            dereferencedResources.remove(resource);
        }
    }
    
    public void addDereferencedResources(Collection resources) {
        if( resources != null && !resources.isEmpty() ) {
            dereferencedResources.addAll(resources);
        }
    }
    
    public boolean importsWereAdded() {
        return this.importsWereAdded;
    }

    public void setImportsWereAdded(boolean theImportsWereAdded) {
        this.importsWereAdded = theImportsWereAdded;
    }
}
