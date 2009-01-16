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

package com.metamatrix.modeler.ui.event;

import java.util.EventObject;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import com.metamatrix.modeler.core.workspace.ModelResource;

/**
 * ModelResourceEvent
 */
public class ModelResourceEvent extends EventObject {

    private static final long serialVersionUID = 1L;
    public static final int OPENED = 1;
    public static final int CLOSING = 2;
    public static final int CLOSED = 3;
    public static final int CHANGED = 4;
    public static final int REBUILD_IMPORTS = 5;
    public static final int RELOADED = 6;
    public static final int ADDED = 7;
    public static final int REMOVED = 8;
    public static final int MOVED = 9;

    
    private ModelResource modelResource;
    private IResource file;
    private int type;
    private IPath oldPath;

    /**
     * Construct an instance of ModelResourceEvent.
     */
    public ModelResourceEvent(ModelResource modelResource, int type, Object source) {
        super(source);
        this.modelResource = modelResource;
        this.file = (modelResource != null ? (IResource) modelResource.getResource() : null);
        this.type = type;
    }

    public ModelResourceEvent(ModelResource modelResource, int type, Object source, IPath oldPath) {
        super(source);
        this.modelResource = modelResource;
        this.file = (modelResource != null ? (IResource) modelResource.getResource() : null);
        this.type = type;
        this.oldPath = oldPath;
    }

    public ModelResourceEvent(IResource changedResource, int type, Object source) {
        super(source);
        this.file = changedResource;
        this.type = type;
    }

    public ModelResourceEvent(IResource changedResource, int type, Object source, IPath oldPath ) {
        super(source);
        this.file = changedResource;
        this.type = type;
        this.oldPath = oldPath;
    }

    public int getType() {
        return this.type;
    }
    
    public ModelResource getModelResource() {
        return this.modelResource;
    }

    public IResource getResource() {
        return this.file;
    }

    public IPath getOldPath() {
        return this.oldPath;
    }
}
