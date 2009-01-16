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

package com.metamatrix.modeler.core.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.search.runtime.ResourceImportRecord;

/**
 * ResourceStatusList is a reusable component that builds a list of resource
 */
public class ResourceStatusList {

    Collection statusList;
    Collection problemList;
    Collection resourceList;
    Collection importedResourceList;
    int highestSeverity = IStatus.OK;

    /**
     * Construct an instance of <code>ResourceStatusList</code> from a collection of either 
     * <code>IResource</code> or <code>ResourceImportRecord</code> instances.  Using this 
     * constructor will cause any IStatus objects built by this list to have severity of 
     * <code>IStatus.ERROR</code> if they are read-only.
     */
    public ResourceStatusList(Collection c) {
        this(c, IStatus.ERROR);
    }

    /**
     * Construct an instance of ResourceStatusList from a collection of either IResource or
     * ResourceImportRecord instances.
     * @param readOnlySeverity allows the caller to specify the severity of the IStatus if
     * the file is read-only.
     */
    public ResourceStatusList(Collection c, int readOnlySeverity) {
        statusList = new ArrayList(c.size());
        problemList = new ArrayList(c.size());
        resourceList = new ArrayList(c.size());
        this.importedResourceList = new ArrayList(c.size());
        
        for ( Iterator iter = c.iterator() ; iter.hasNext() ; ) {
            Object o = iter.next();
            IResource resource = null; 
            if ( o instanceof IResource ) {
                resource = (IResource) o;
                if ( o instanceof IFile ) {
                    this.importedResourceList.add(o);
                }
            } else if ( o instanceof ResourceImportRecord ) {
                ResourceImportRecord record = (ResourceImportRecord) o;
                IPath path = new Path(record.getPath());
                resource = ModelerCore.getWorkspace().getRoot().getFile(path);
                // Update list of imported resources
                path = new Path(record.getImportedPath());
                this.importedResourceList.add(ModelerCore.getWorkspace().getRoot().getFile(path));
            }
            // Somehow ResourceRefactorCommand.getDependentResources() is returning the starting resource, which is causing
            // the resource to be NULL. Hence the check.
            if( resource != null ) {
	            resourceList.add(resource);
	            
	            IStatus status = new ResourceStatus(readOnlySeverity, resource);
	            statusList.add(status);
	            if ( ! status.isOK() ) {
	                problemList.add(status);
	                if ( status.getSeverity() > this.highestSeverity ) {
	                    highestSeverity = status.getSeverity();
	                }
	            }
			}
        }
    }

    /**
     * Returns a list of ResourceStatus instances.
     * @return a <code>Collection</code> of <code>ResourceStatus</code> instances.  Will not be null.
     */
    public Collection getStatusList() {
        return this.statusList;
    }
    
    /**
     * Returns every the status in this list.
     * @return a <code>Collection</code> of <code>IFile</code> instances.  Will not be null.
     */
    public Collection getResourceList() {
        return this.resourceList;
    }
    
    /**
     * @return a <code>Collection</code> of <code>IFile</code> instances.  Will not be null.
     */
    public Collection getImportedResourceList() {
        return this.importedResourceList;
    }
    
    /**
     * Returns every the status in this list that are not OK
     * @return a <code>Collection</code> of <code>ResourceStatus</code> instances.  Will not be null.
     */
    public Collection getProblems() {
        return this.problemList;
    }

    /**
     * Returns the highest status severity in this list.
     * @return the highest severity in this list, or <code>IStatus.OK</code> if this list is empty.
     */
    public int getHighestSeverity() {
        return highestSeverity;
    }

}
