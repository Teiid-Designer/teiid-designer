/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
