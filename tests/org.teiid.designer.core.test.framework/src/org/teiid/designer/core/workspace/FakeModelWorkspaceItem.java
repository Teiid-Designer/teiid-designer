/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

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
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getItemType()
     */
    @Override
	public int getItemType() {
        return this.itemType;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getItemName()
     */
    @Override
	public String getItemName() {
        return path.lastSegment();
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getOpenable()
     */
    @Override
	public Openable getOpenable() {
        return null;
    }

    @Override
    public ModelWorkspaceItemInfo getItemInfo() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getModelWorkspace()
     */
    @Override
	public ModelWorkspace getModelWorkspace() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getModelProject()
     */
    @Override
	public ModelProject getModelProject() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getCorrespondingResource()
     */
    @Override
	public IResource getCorrespondingResource() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getResource()
     */
    @Override
	public IResource getResource() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getUnderlyingResource()
     */
    @Override
	public IResource getUnderlyingResource() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getPath()
     */
    @Override
	public IPath getPath() {
        return this.path;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#isReadOnly()
     */
    @Override
	public boolean isReadOnly() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#exists()
     */
    @Override
	public boolean exists() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getParent()
     */
    @Override
	public ModelWorkspaceItem getParent() {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getChildren()
     */
    @Override
	public ModelWorkspaceItem[] getChildren() {
        return null;
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

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#hasChildren()
     */
    @Override
	public boolean hasChildren() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#isStructureKnown()
     */
    @Override
	public boolean isStructureKnown() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#accept(org.teiid.designer.core.workspace.ModelWorkspaceVisitor,
     *      int)
     */
    @Override
	public void accept( ModelWorkspaceVisitor visitor,
                        int depth ) {

    }

    /**
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    @Override
	public Object getAdapter( Class adapter ) {
        return null;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#isOpening()
     * @since 4.3
     */
    @Override
	public boolean isOpening() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#isClosing()
     * @since 4.3
     */
    @Override
	public boolean isClosing() {
        return false;
    }

}
