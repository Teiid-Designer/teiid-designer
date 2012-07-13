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
import org.eclipse.core.runtime.IProgressMonitor;


/**
 * FakeConcreteModelWorkspaceItemImpl
 */
public class FakeConcreteModelWorkspaceItemImpl extends ModelWorkspaceItemImpl implements Openable {

    private boolean closed;
    private boolean hasUnsavedChanges;

    /**
     * Construct an instance of FakeConcreteModelWorkspaceItemImpl.
     * 
     * @param type
     * @param parent
     * @param name
     * @throws IllegalArgumentException
     */
    public FakeConcreteModelWorkspaceItemImpl( int type,
                                               ModelWorkspaceItem parent,
                                               String name ) throws IllegalArgumentException {
        super(type, parent, name);
        this.closed = true;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getResource()
     */
    @Override
	public IResource getResource() {
        throw new UnsupportedOperationException("getResource() is not implemented on " + this.getClass().getName()); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getUnderlyingResource()
     */
    @Override
	public IResource getUnderlyingResource() {
        throw new UnsupportedOperationException("getUnderlyingResource() is not implemented on " + this.getClass().getName()); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#getPath()
     */
    @Override
	public IPath getPath() {
        throw new UnsupportedOperationException("getPath() is not implemented on " + this.getClass().getName()); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelWorkspaceItem#hasChildren()
     */
    @Override
	public boolean hasChildren() {
        throw new UnsupportedOperationException("hasChildren() is not implemented on " + this.getClass().getName()); //$NON-NLS-1$
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#close()
     */
    @Override
    public void close() {
        this.closed = true;
        this.hasUnsavedChanges = false;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#hasUnsavedChanges()
     */
    @Override
	public boolean hasUnsavedChanges() {
        return this.hasUnsavedChanges;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#isOpen()
     */
    @Override
	public boolean isOpen() {
        return !this.closed;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#open(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
	public void open( IProgressMonitor progress ) {
        this.closed = false;
        this.hasUnsavedChanges = false;
    }

    /**
     * @see org.teiid.designer.core.workspace.Openable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     */
    @Override
	public void save( IProgressMonitor progress,
                      boolean force ) {
        this.hasUnsavedChanges = false;
    }

    public void setChanged() {
        this.hasUnsavedChanges = true;
    }

}
