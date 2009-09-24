/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * FakeModelWorkspaceItem
 */
public class FakeModelWorkspaceItem implements ModelWorkspaceItem {

    private final int itemType;
    private final IPath path;

    /**
     * Construct an instance of FakeModelWorkspaceItem.
     */
    public FakeModelWorkspaceItem() {
        this(ModelWorkspaceItem.MODEL_WORKSPACE);
    }

    /**
     * Construct an instance of FakeModelWorkspaceItem.
     */
    public FakeModelWorkspaceItem( final int itemType ) {
        this(itemType, "/"); //$NON-NLS-1$
    }

    /**
     * Construct an instance of FakeModelWorkspaceItem.
     */
    public FakeModelWorkspaceItem( final int itemType,
                                   final String path ) {
        super();
        this.itemType = itemType;
        this.path = new Path(path);
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getItemType()
     */
    public int getItemType() {
        return this.itemType;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getItemName()
     */
    public String getItemName() {
        return path.lastSegment();
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getOpenable()
     */
    public Openable getOpenable() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getModelWorkspace()
     */
    public ModelWorkspace getModelWorkspace() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getModelProject()
     */
    public ModelProject getModelProject() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getCorrespondingResource()
     */
    public IResource getCorrespondingResource() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getResource()
     */
    public IResource getResource() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getUnderlyingResource()
     */
    public IResource getUnderlyingResource() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getPath()
     */
    public IPath getPath() {
        return this.path;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#isReadOnly()
     */
    public boolean isReadOnly() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#exists()
     */
    public boolean exists() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getParent()
     */
    public ModelWorkspaceItem getParent() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getChildren()
     */
    public ModelWorkspaceItem[] getChildren() {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getChild(java.lang.String)
     */
    public ModelWorkspaceItem getChild( String childName ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#getChild(org.eclipse.core.resources.IResource)
     */
    public ModelWorkspaceItem getChild( IResource resource ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#hasChildren()
     */
    public boolean hasChildren() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#isStructureKnown()
     */
    public boolean isStructureKnown() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#accept(com.metamatrix.modeler.core.workspace.ModelWorkspaceVisitor,
     *      int)
     */
    public void accept( ModelWorkspaceVisitor visitor,
                        int depth ) {

    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter ) {
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#isOpening()
     * @since 4.3
     */
    public boolean isOpening() {
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelWorkspaceItem#isClosing()
     * @since 4.3
     */
    public boolean isClosing() {
        return false;
    }

}
