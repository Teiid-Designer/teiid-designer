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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Status;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * ResourceStatus is a specialization of Status that can access a resource.  This is useful for
 * error messages 
 */
public class ResourceStatus extends Status {

    private static final int RESOURCE_OKAY = 2000;
    private static final int RESOURCE_READONLY = 2001;
    private static final int RESOURCE_PROJECT_CLOSED = 2002;
    private static final String PID = ModelerCore.PLUGIN_ID;
    
    private IResource resource;
//    private int readOnlySeverity = ERROR;

    /**
     * Construct an instance of ResourceStatus.  If the resource is read-only, this status will be
     * of severity <code>IStatus.ERROR</code>
     */
    public ResourceStatus(IResource resource) {
        this(ERROR, resource);
    }

    /**
     * Construct an instance of ResourceStatus with the specified severity.
     */
    public ResourceStatus(int readOnlySeverity, IResource resource) {
        super(OK, PID, RESOURCE_OKAY, "", null); //$NON-NLS-1$
        this.resource = resource;
//        this.readOnlySeverity = readOnlySeverity;
        if (ModelUtil.isIResourceReadOnly(resource)) {
            setSeverity(readOnlySeverity);
            setMessage(ModelerCore.Util.getString("ResourceStatus.read_only_resource", resource.getFullPath())); //$NON-NLS-1$
            setCode(RESOURCE_READONLY);
        } else if ( ! resource.getProject().isOpen() ) {
            setSeverity(ERROR);
            setMessage(ModelerCore.Util.getString("ResourceStatus.closed_project", resource.getFullPath())); //$NON-NLS-1$
            setCode(RESOURCE_PROJECT_CLOSED);
        }
        
    }

    /**
     * Return the resource that this status references.
     * @return
     */
    public IResource getResource() {
        return resource;
    }

}
