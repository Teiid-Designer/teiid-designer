/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.workspace;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.resource.Resource;
import org.teiid.designer.core.ModelerCore;


/**
 * FakeModelBuffer
 */
public class FakeModelBuffer implements ModelBuffer {

    private boolean changed;
    private boolean closed;
    private boolean readonly;
    private final Object key;

    /**
     * Construct an instance of FakeModelBuffer.
     */
    public FakeModelBuffer( final Object key ) {
        super();
        this.key = key;
    }

    public void setChanged( final boolean changed ) {
        this.changed = changed;
    }

    public void setReadOnly( final boolean readonly ) {
        this.readonly = readonly;
    }

    public Object getKey() {
        return this.key;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.ModelBuffer#close()
     */
    public void close() {
        if (ModelerCore.DEBUG_MODEL_WORKSPACE) {
            ModelerCore.Util.log("Closing FakeModelBuffer " + key); //$NON-NLS-1$
        }
        this.closed = true;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.ModelBuffer#getOwner()
     */
    public Openable getOwner() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.ModelBuffer#getUnderlyingResource()
     */
    public IResource getUnderlyingResource() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.ModelBuffer#getEmfResource()
     */
    public Resource getEmfResource() {
        return null;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.ModelBuffer#hasUnsavedChanges()
     */
    public boolean hasUnsavedChanges() {
        return changed;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.ModelBuffer#isClosed()
     */
    public boolean isClosed() {
        return this.closed;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.ModelBuffer#isReadOnly()
     */
    public boolean isReadOnly() {
        return this.readonly;
    }

    /* (non-Javadoc)
     * @See org.teiid.designer.core.ModelBuffer#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
     */
    public void save( IProgressMonitor progress,
                      boolean force ) {
        if (ModelerCore.DEBUG_MODEL_WORKSPACE) {
            ModelerCore.Util.log("Saving FakeModelBuffer" + key); //$NON-NLS-1$
        }
        this.changed = false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelBuffer#unload()
     */
    public void unload() {
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelBuffer#getLastModificationStamp()
     * @since 4.2
     */
    public long getLastModificationStamp() {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelBuffer#isInProcessOfSaving()
     * @since 4.2
     */
    public boolean isInProcessOfSaving() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelBuffer#reload()
     * @since 4.2
     */
    public void reload() {
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelBuffer#getLastFileSize()
     * @since 4.2
     */
    public long getLastFileSize() {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelBuffer#getLastChecksum()
     * @since 4.2
     */
    public long getLastChecksum() {
        return 0;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelBuffer#hasErrors()
     * @since 4.2
     */
    public boolean hasErrors() {
        return false;
    }

    /**
     * @see org.teiid.designer.core.workspace.ModelBuffer#getErrors()
     * @since 4.2
     */
    public IStatus getErrors() {
        return null;
    }

}
