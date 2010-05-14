/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for Model workspace items that must be opened before they can be 
 * navigated or modified. Opening a model resource involves opening an EMF resource.
 * <p>
 * To open an item, all openable parent elements must be open.
 * The Model workspace model automatically opens parent items, as it automatically opens elements.
 * Opening an element may provide access to direct children and other descendants,
 * but does not automatically open any descendents which are themselves <code>Openable</code>.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface Openable {
    
    /**
     * Closes this element and its buffer (if any).
     * Closing an element which is not open has no effect.
     *
     * <p>Note: although <code>close</code> is exposed in the API, clients are
     * not expected to open and close elements - the Java model does this automatically
     * as elements are accessed.
     *
     * @exception ModelWorkspaceException if an error occurs closing this element
     */
    void close() throws ModelWorkspaceException;

    /**
     * Returns <code>true</code> if this element is open and:
     * <ul>
     * <li>its buffer has unsaved changes, or
     * <li>one of its descendants has unsaved changes, or
     * <li>a working copy has been created on one of this
     * element's children and has not yet destroyed
     * </ul>
     *
     * @exception ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource.
     * @return <code>true</code> if this element is open and:
     * <ul>
     * <li>its buffer has unsaved changes, or
     * <li>one of its descendants has unsaved changes, or
     * <li>a working copy has been created on one of this
     * element's children and has not yet destroyed
     * </ul>
     */
    boolean hasUnsavedChanges() throws ModelWorkspaceException;

    /**
     * Returns whether this openable is open. This is a handle-only method.
     * @return true if this openable is open, false otherwise
     */
    boolean isOpen();

    /**
     * Opens this element and all parent elements that are not already open.
     * For compilation units, a buffer is opened on the contents of the underlying resource.
     *
     * <p>Note: although <code>open</code> is exposed in the API, clients are
     * not expected to open and close elements - the Java model does this automatically
     * as elements are accessed.
     *
     * @param progress the given progress monitor
     * @exception ModelWorkspaceException if an error occurs accessing the contents
     *      of its underlying resource. Reasons include:
     * <ul>
     *  <li>This Model workspace item does not exist (ELEMENT_DOES_NOT_EXIST)</li>
     * </ul>
     */
    public void open(IProgressMonitor progress) throws ModelWorkspaceException;

    /**
     * Saves any changes in this element's buffer to its underlying resource
     * via a workspace resource operation. This has no effect if the element has no underlying
     * buffer, or if there are no unsaved changed in the buffer.
     * <p>
     * As a result of this operation, the element is consistent with its underlying 
     * resource or buffer. 
     * </p>
     * <p>
     * The FORCE update flag controls how this method deals with cases where the 
     * workspace is not completely in sync with the local file system. If FORCE is <code>false</code> 
     * the method will only attempt to overwrite a corresponding file in the local file 
     * system provided it is in sync with the workspace. This option ensures there is no 
     * unintended data loss; it is the recommended setting. However, if FORCE is passed as <code>true</code>, 
     * an attempt will be made to write a corresponding file in the local file system, 
     * overwriting any existing one if need be. In either case, if this method succeeds, the 
     * resource will be marked as being local (even if it wasn't before). 
     * </p>
     *
     * @param progress the given progress monitor
     * @param force controls how this method deals with cases where the 
     *  workspace is not completely in sync with the local file system 
     * @exception ModelWorkspaceException if an error occurs accessing the contents
     *      of its underlying resource. Reasons include:
     * <ul>
     *  <li>This Model workspace item does not exist (ELEMENT_DOES_NOT_EXIST)</li>
     *  <li>This Model workspace item is read-only (READ_ONLY)</li>
     * </ul>
     */
    void save(IProgressMonitor progress, boolean force) throws ModelWorkspaceException;
}
