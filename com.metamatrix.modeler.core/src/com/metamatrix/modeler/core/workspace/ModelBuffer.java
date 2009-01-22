/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * A buffer contains the materialized contents of a model resource.
 * The contents may be in the process of being edited, differing from the actual contents of the 
 * underlying resource. A buffer has an owner, which is an {@link Openable}. 
 * If a buffer does not have an underlying resource, saving the buffer has no effect. 
 * Buffers can be read-only.
 * <p>
 * This interface may be implemented by clients.
 * </p>
 */
public interface ModelBuffer {
    
    /**
     * Every resource has a modification stamp that Eclipse owns. We're using it to determine if the resource
     * was changed via this editor. It could have been changed via some other editor or from the filesystem.
     * Initially set to the modification stamp of the resource.
     */
    public static final long INITIAL_MOD_STAMP = IResource.NULL_STAMP;
    
    /**
     * Return the last modification stamp of the {@link #getUnderlyingResource() underlying resource}
     * that was known to this buffer.
     * @return the {@link IResource#getModificationStamp()} from the last time this buffer saved to
     * the underlying resource
     * @since 4.2
     */
    public long getLastModificationStamp();
    
    /** 
     * Return the last size of the {@link #getUnderlyingResource() underlying resource}
     * that was known to this buffer.
     * @return the size of the file (in bytes), or 0 if the file doesn't yet exist on the file system ...
     * @since 4.2
     */
    public long getLastFileSize();
    
    /** 
     * Return the last checksum of the {@link #getUnderlyingResource() underlying resource}
     * that was known to this buffer.
     * @return the {@link java.util.zip.CRC32 CRC-32 checksum} of the file, or 0 if the file doesn't yet exist on the file system ...
     * @since 4.2
     */
    public long getLastChecksum();
    
    /**
     * Adds the given listener for changes to this buffer.
     * Has no effect if an identical listener is already registered or if the buffer
     * is closed.
     *
     * @param listener the listener of buffer changes
     */
//    public void addBufferChangedListener(IBufferChangedListener listener);

    /**
     * Removes the given listener from this buffer.
     * Has no affect if an identical listener is not registered or if the buffer is closed.
     *
     * @param listener the listener
     */
//    public void removeBufferChangedListener(IBufferChangedListener listener);

    /**
     * Closes the buffer. Any unsaved changes are lost. Reports a buffer changed event
     * with a 0 offset and a 0 length. When this event is fired, the buffer should already
     * be closed.
     * <p>
     * Further operations on the buffer are not allowed, except for close.  If an
     * attempt is made to close an already closed buffer, the second attempt has no effect.
     */
    public void close();

    /**
     * Returns the {@link Openable} item owning this buffer.
     *
     * @return the openable element owning this buffer
     */
    public Openable getOwner();

    /**
     * Returns the underlying resource for which this buffer was opened,
     * or <code>null</code> if this buffer was not opened on a resource.
     *
     * @return the underlying resource for this buffer, or <code>null</code>
     *  if none.
     */
    public IResource getUnderlyingResource();

    /**
     * Returns the {@link Resource EMF resource} that backs this buffer,
     * or <code>null</code> if this buffer has no corresponding EMF resource.
     *
     * @return the underlying resource for this buffer, or <code>null</code>
     *  if none.
     */
    public Resource getEmfResource();

    /**
     * Returns whether this buffer has been modified since it
     * was opened or since it was last saved.
     * If a buffer does not have an underlying resource, this method always
     * returns <code>true</code>.
     *
     * @return a <code>boolean</code> indicating presence of unsaved changes (in
     *   the absence of any underlying resource, it will always return <code>true</code>).
     */
    public boolean hasUnsavedChanges();

    /**
     * Returns whether this buffer has been closed.
     *
     * @return a <code>boolean</code> indicating whether this buffer is closed.
     */
    public boolean isClosed();

    /**
     * Returns whether this buffer is read-only.
     *
     * @return a <code>boolean</code> indicating whether this buffer is read-only
     */
    public boolean isReadOnly();

    /**
     * Unload the model and lose any changes that have been made so far.
     */
    public void unload();
    
    /**
     * Return whether this resource has errors upon opening.  If the resource is not yet opened,
     * this method does not cause the opening and returns <code>false</code>. 
     * @return true if opening this resource caused errors
     * @since 4.2
     * @see #getErrors()
     */
    public boolean hasErrors();
    
    /**
     * Return any errors that occurred upon opening.  If the resource is not yet opened or there are no errors,
     * this method returns an {@link IStatus#OK OK status}.
     * @return the errors as an {@link IStatus}
     * @since 4.2
     * @see #hasErrors()
     */
    public IStatus getErrors();

//    /**
//     * Unload and then reload the model, losing any changes that have been made so far.
//     */
//    public void reload();
    
    /**
     * Saves the contents of this buffer to its underlying resource. If
     * successful, this buffer will have no unsaved changes.
     * The buffer is left open. Saving a buffer with no unsaved
     * changes has no effect - the underlying resource is not changed.
     * If the buffer does not have an underlying resource or is read-only, this
     * has no effect.
     * A <code>RuntimeException</code> might be thrown if the buffer is closed.
     *
     * @param progress the progress monitor to notify
     *
     * @exception ModelWorkspaceException if an error occurs writing the buffer
     *  to the underlying resource
     */
    public void save(IProgressMonitor progress, boolean force) throws ModelWorkspaceException;

    /** 
     * @return
     * @since 4.2
     */
    public boolean isInProcessOfSaving();

}
