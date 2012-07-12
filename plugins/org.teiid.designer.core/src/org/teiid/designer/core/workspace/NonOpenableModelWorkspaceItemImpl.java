/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import org.eclipse.core.resources.IResource;

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
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#hasChildren()
     */
    public boolean hasChildren() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getChildren()
     */
    @Override
    public ModelWorkspaceItem[] getChildren() {
        return new ModelWorkspaceItem[] {};
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getChild(java.lang.String)
     */
    @Override
    public ModelWorkspaceItem getChild( String childName ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getChild(org.eclipse.core.resources.IResource)
     */
    @Override
    public ModelWorkspaceItem getChild( IResource resource ) {
        return null;
    }

}
