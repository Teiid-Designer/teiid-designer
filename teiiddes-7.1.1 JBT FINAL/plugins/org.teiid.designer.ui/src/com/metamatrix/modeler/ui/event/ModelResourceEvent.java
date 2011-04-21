/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
