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

package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.resources.IResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;

/**
 * NonOpenableModelWorkspaceItemImpl
 */
public abstract class NonOpenableModelWorkspaceItemImpl extends ModelWorkspaceItemImpl {

    /**
     * Construct an instance of NonOpenableModelWorkspaceItemImpl.
     * 
     * @param type
     * @param parent
     * @param name
     * @throws IllegalArgumentException
     */
    public NonOpenableModelWorkspaceItemImpl( int type,
                                              ModelWorkspaceItem parent,
                                              String name ) throws IllegalArgumentException {
        super(type, parent, name);
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#hasChildren()
     */
    public boolean hasChildren() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getChildren()
     */
    @Override
    public ModelWorkspaceItem[] getChildren() {
        return new ModelWorkspaceItem[] {};
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getChild(java.lang.String)
     */
    @Override
    public ModelWorkspaceItem getChild( String childName ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getChild(org.eclipse.core.resources.IResource)
     */
    @Override
    public ModelWorkspaceItem getChild( IResource resource ) {
        return null;
    }

}
