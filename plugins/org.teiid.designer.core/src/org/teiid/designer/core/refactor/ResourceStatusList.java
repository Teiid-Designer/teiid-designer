/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.refactor;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;


/**
 * ResourceStatusList is a reusable component that builds a list of resources
 *
 * @since 8.0
 */
public class ResourceStatusList {

    Collection<IStatus> statusList;
    Collection<IStatus> problemList;
    Collection<IFile> resourceList;
    int highestSeverity = IStatus.OK;

    /**
     * Construct an instance of <code>ResourceStatusList</code> from a collection of either 
     * <code>IFile</code> instances.  Using this
     * constructor will cause any IStatus objects built by this list to have severity of 
     * <code>IStatus.ERROR</code> if they are read-only.
     */
    public ResourceStatusList(Collection<IFile> c) {
        this(c, IStatus.ERROR);
    }

    /**
     * Construct an instance of ResourceStatusList from a collection of either IResource or
     * ResourceImportRecord instances.
     * @param readOnlySeverity allows the caller to specify the severity of the IStatus if
     * the file is read-only.
     */
    public ResourceStatusList(Collection<IFile> c, int readOnlySeverity) {
        statusList = new ArrayList(c.size());
        problemList = new ArrayList(c.size());
        resourceList = new ArrayList(c.size());
        
        for (IFile file : c) {
            this.resourceList.add(file);

            IStatus status = new ResourceStatus(readOnlySeverity, file);
            statusList.add(status);
            if ( ! status.isOK() ) {
                problemList.add(status);
                if ( status.getSeverity() > this.highestSeverity ) {
                    highestSeverity = status.getSeverity();
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
    public Collection<IFile> getResourceList() {
        return this.resourceList;
    }
    
    /**
     * Returns every the status in this list that are not OK
     * @return a <code>Collection</code> of <code>ResourceStatus</code> instances.  Will not be null.
     */
    public Collection<IStatus> getProblems() {
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
