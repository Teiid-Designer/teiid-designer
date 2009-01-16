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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import com.metamatrix.modeler.core.workspace.ModelBuffer;
import com.metamatrix.modeler.core.workspace.ModelStatusConstants;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.Openable;

/**
 * OpenableImpl
 */
public abstract class OpenableImpl extends ModelWorkspaceItemImpl implements Openable, InternalOpenable {

    /**
     * Construct an instance of OpenableImpl.
     */
    protected OpenableImpl( final int type,
                            final ModelWorkspaceItem parent,
                            final String name ) {
        super(type, parent, name);
    }

    /**
     * Return my underlying resource. Elements that may not have a corresponding resource must override this method.
     * 
     * @see ModelWorkspaceItem
     */
    @Override
    public IResource getCorrespondingResource() throws ModelWorkspaceException {
        return getUnderlyingResource();
    }

    /**
     * @see ModelWorkspaceItem
     */
    @Override
    public Openable getOpenable() {
        return this;
    }

    /**
     * @see ModelWorkspaceItem
     */
    public IResource getUnderlyingResource() throws ModelWorkspaceException {
        IResource parentResource = fParent.getUnderlyingResource();
        if (parentResource == null) {
            return null;
        }
        int type = parentResource.getType();
        if (type == IResource.FOLDER || type == IResource.PROJECT) {
            IContainer folder = (IContainer)parentResource;
            IResource resource = folder.findMember(fName);
            if (resource == null) {
                throw newNotPresentException();
            }
            return resource;
        }
        return parentResource;
    }

    /**
     * Updates the info objects for this element and all of its children by removing the current infos, generating new infos, and
     * then placing the new infos into the Java Model cache tables.
     */
    protected void buildStructure( OpenableModelWorkspaceItemInfo info,
                                   IProgressMonitor monitor ) throws ModelWorkspaceException {

        if (monitor != null && monitor.isCanceled()) return;

        // remove existing (old) infos
        removeInfo();
        HashMap newElements = new HashMap(11);
        info.setIsStructureKnown(generateInfos(info, monitor, newElements, getResource()));
        // ModelWorkspaceManager.getModelWorkspaceManager().getElementsOutOfSynchWithBuffers().remove(this);
        for (Iterator iter = newElements.keySet().iterator(); iter.hasNext();) {
            ModelWorkspaceItem key = (ModelWorkspaceItem)iter.next();
            Object value = newElements.get(key);
            ModelWorkspaceManager.getModelWorkspaceManager().putInfo(key, value);
        }

        // add the info for this at the end, to ensure that a getInfo cannot reply null in case the LRU cache needs
        // to be flushed. Might lead to performance issues.
        // see PR 1G2K5S7: ITPJCORE:ALL - NPE when accessing source for a binary type
        ModelWorkspaceManager.getModelWorkspaceManager().putInfo(this, info);
    }

    /**
     * Returns a new element info for this element.
     */
    protected abstract OpenableModelWorkspaceItemInfo createItemInfo();

    /**
     * This element is being closed. Do any necessary cleanup.
     */
    @Override
    protected void closing( Object info ) {
        OpenableModelWorkspaceItemInfo openableInfo = (OpenableModelWorkspaceItemInfo)info;
        closeBuffer(openableInfo);
        super.closing(info);
    }

    /**
     * Open an <code>Openable</code> that is known to be closed (no check for <code>isOpen()</code>).
     */
    public void openWhenClosed( final IProgressMonitor pm ) throws ModelWorkspaceException {
        try {

            if (ModelWorkspaceManager.VERBOSE) {
                System.out.println("OPENING Item (" + Thread.currentThread() + "): " + this.toStringWithAncestors()); //$NON-NLS-1$//$NON-NLS-2$
            }

            // 1) Parent must be open - open the parent if necessary
            openParent(pm);

            // 2) create the new element info
            OpenableModelWorkspaceItemInfo info = createItemInfo();

            // 3) build the structure of the openable
            buildStructure(info, pm);

            // 4) anything special
            opening(info);

            if (ModelWorkspaceManager.VERBOSE) {
                System.out.println("-> Package cache size = " + ModelWorkspaceManager.getModelWorkspaceManager().cache.pkgSize()); //$NON-NLS-1$
            }

            // if any problems occuring openning the element, ensure that it's info
            // does not remain in the cache (some elements, pre-cache their info
            // as they are being opened).
        } catch (ModelWorkspaceException e) {
            ModelWorkspaceManager.getModelWorkspaceManager().removeInfo(this);
            throw e;
        }
    }

    /**
     * Builds this element's structure and properties in the given info object, based on this element's current contents (reuse
     * buffer contents if this element has an open buffer, or resource contents if this element does not have an open buffer).
     * Children are placed in the given newElements table (note, this element has already been placed in the newElements table).
     * Returns true if successful, or false if an error is encountered while determining the structure of this element.
     */
    protected abstract boolean generateInfos( OpenableModelWorkspaceItemInfo info,
                                              IProgressMonitor pm,
                                              Map newElements,
                                              IResource underlyingResource ) throws ModelWorkspaceException;

    @Override
    public boolean exists() {
        // ModelPackageFragmentRoot root = this.getPackageFragmentRoot();
        // if (root == null || root == this || !root.isArchive()) {
        // return parentExists() && resourceExists();
        // } else {
        // return super.exists();
        // }
        return parentExists() && resourceExists();
    }

    /**
     * @see IParent
     */
    public boolean hasChildren() throws ModelWorkspaceException {
        return getChildren().length > 0;
    }

    /**
     * @see IOpenable
     */
    public boolean hasUnsavedChanges() {

        if (isReadOnly() || !isOpen()) {
            return false;
        }

        if (hasBuffer()) {
            ModelBuffer buffer = getBufferManager().getOpenBuffer(this);
            if (buffer == null) {
                return false;
            }

            if (buffer.hasUnsavedChanges()) {
                return true;
            }
        }

        // for package fragments, package fragment roots, and projects must check open buffers
        // to see if they have an child with unsaved changes
        if (fType == MODEL_FOLDER || fType == MODEL_PROJECT || fType == MODEL_WORKSPACE) { // fix for 1FWNMHH
            Iterator openBuffers = getBufferManager().getOpenBuffers();
            while (openBuffers.hasNext()) {
                ModelBuffer buffer = (ModelBuffer)openBuffers.next();
                if (buffer.hasUnsavedChanges()) {
                    ModelWorkspaceItem owner = (ModelWorkspaceItem)buffer.getOwner();
                    if (isAncestorOf(owner)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Subclasses must override as required.
     * 
     * @see IOpenable
     */
    public boolean isConsistent() {
        return true;
    }

    /**
     * @see IOpenable
     */
    public boolean isOpen() {
        synchronized (ModelWorkspaceManager.getModelWorkspaceManager()) {
            return ModelWorkspaceManager.getModelWorkspaceManager().getInfo(this) != null;
        }
    }

    /**
     * Returns true if this represents a source element. Openable source elements have an associated buffer created when they are
     * opened.
     */
    protected boolean isSourceElement() {
        return false;
    }

    /**
     * @see IOpenable
     */
    public void makeConsistent( IProgressMonitor pm ) throws ModelWorkspaceException {
        if (!isConsistent()) {
            buildStructure((OpenableModelWorkspaceItemInfo)getItemInfo(), pm);
        }
    }

    /**
     * @see IOpenable
     */
    public void open( IProgressMonitor pm ) throws ModelWorkspaceException {
        if (!isOpen()) {
            // TODO: need to synchronize (IOpenable.open(IProgressMonitor) is API
            // TODO: could use getItemInfo instead
            this.openWhenClosed(pm);
        }
    }

    /**
     * Open the parent element if necessary
     */
    protected void openParent( IProgressMonitor pm ) throws ModelWorkspaceException {

        OpenableImpl openableParent = (OpenableImpl)getOpenableParent();
        if (openableParent != null) {
            if (!openableParent.isOpen()) {
                openableParent.openWhenClosed(pm);
            }
        }
    }

    /**
     * Answers true if the parent exists (null parent is answering true)
     */
    protected boolean parentExists() {

        ModelWorkspaceItem parent = this.getParent();
        if (parent == null) return true;
        return parent.exists();
    }

    /**
     * Returns whether the corresponding resource or associated file exists
     */
    protected boolean resourceExists() {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        if (workspace == null) return false; // workaround for http://bugs.eclipse.org/bugs/show_bug.cgi?id=34069
        return ModelWorkspaceImpl.getTarget(workspace.getRoot(), this.getPath().makeRelative(), // ensure path is relative (see
                                            // http://dev.eclipse.org/bugs/show_bug.cgi?id=22517)
                                            true) != null;
    }

    /**
     * @see IOpenable
     */
    public void save( IProgressMonitor pm,
                      boolean force ) throws ModelWorkspaceException {
        if (isReadOnly() || ModelUtil.isIResourceReadOnly(getResource())) {
            throw new ModelWorkspaceException(new ModelStatusImpl(ModelStatusConstants.READ_ONLY, this));
        }
        ModelBuffer buf = getBuffer();
        if (buf != null) { // some Openables (like a JavaProject) don't have a buffer
            buf.save(pm, force); // can't refresh inside this (threading issues)
            this.makeConsistent(pm); // update the element info of this element
            ((ModelBufferImpl)buf).refresh(pm); // refresh and update mod stamp here
        }
    }

    // /**
    // * Find enclosing package fragment root if any
    // */
    // public ModelPackageFragmentRoot getPackageFragmentRoot() {
    // ModelWorkspaceItem current = this;
    // do {
    // if (current instanceof ModelPackageFragmentRoot) return (ModelPackageFragmentRoot)current;
    // current = current.getParent();
    // } while(current != null);
    // return null;
    // }
    //
    /**
     * Returns the buffer manager for this element.
     */
    protected ModelBufferManager getBufferManager() {
        return ModelBufferManager.getDefaultBufferManager();
    }

    // /**
    // * Note: a buffer with no unsaved changes can be closed by the Java Model
    // * since it has a finite number of buffers allowed open at one time. If this
    // * is the first time a request is being made for the buffer, an attempt is
    // * made to create and fill this element's buffer. If the buffer has been
    // * closed since it was first opened, the buffer is re-created.
    // *
    // * @see IOpenable
    // */
    // public synchronized ModelBuffer getBuffer() throws ModelWorkspaceException {
    // return getBuffer(false);
    // }

    /**
     * This is a hack to get around a problem with newly saved files not indicating that they exist (the call to refreshLocal),
     * and a problem with possible deadlock if the call to refreshLocal is placed within the synchronized {@link #getBuffer()}
     * method.
     */
    public ModelBuffer getBufferHack() throws ModelWorkspaceException {
        final ModelBuffer buf = getBuffer();
        if (!getResource().exists() && getResource().getLocation().toFile().exists()) {
            try {
                getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
            } catch (final CoreException err) {
                throw new ModelWorkspaceException(err);
            }
        }
        return buf;
    }

    /**
     * Note: a buffer with no unsaved changes can be closed by the Java Model since it has a finite number of buffers allowed open
     * at one time. If this is the first time a request is being made for the buffer, an attempt is made to create and fill this
     * element's buffer. If the buffer has been closed since it was first opened, the buffer is re-created.
     * 
     * @see IOpenable
     */
    public synchronized ModelBuffer getBuffer() throws ModelWorkspaceException {
        if (hasBuffer()) {
            // ensure element is open
            if (!isOpen()) {
                getItemInfo();
            }
            ModelBuffer buffer = getBufferManager().getOpenBuffer(this);
            if (buffer == null) {
                // try to (re)open a buffer
                buffer = openBuffer(null);
            }
            return buffer;
        }
        return null;
    }

    /**
     * Opens a buffer on the contents of this element, and returns the buffer, or returns <code>null</code> if opening fails. By
     * default, do nothing - subclasses that have buffers must override as required.
     */
    @SuppressWarnings( "unused" )
    protected ModelBuffer openBuffer( IProgressMonitor pm ) throws ModelWorkspaceException {
        return null;
    }

    // /**
    // * Opens a buffer on the contents of this element, and returns
    // * the buffer, or returns <code>null</code> if opening fails.
    // * By default, do nothing - subclasses that have buffers
    // * must override as required.
    // */
    // protected ModelBuffer openBuffer(IProgressMonitor pm, final boolean force) throws ModelWorkspaceException {
    // return null;
    // }

    /**
     * Close the buffer associated with this element, if any.
     */
    protected void closeBuffer( OpenableModelWorkspaceItemInfo info ) {
        if (!hasBuffer()) return; // nothing to do
        ModelBuffer buffer = getBufferManager().getOpenBuffer(this);
        if (buffer != null) {
            buffer.close();
            getBufferManager().removeBuffer(buffer);
            // buffer.removeBufferChangedListener(this);
        }
    }

    /**
     * Returns true if this element may have an associated source buffer, otherwise false. Subclasses must override as required.
     */
    protected boolean hasBuffer() {
        return false;
    }

    // /**
    // * The buffer associated with this element has changed. Registers
    // * this element as being out of synch with its buffer's contents.
    // * If the buffer has been closed, this element is set as NOT out of
    // * synch with the contents.
    // *
    // * @see IBufferChangedListener
    // */
    // public void emfResourceChanged(BufferChangedEvent event) {
    // if (event.getBuffer().isClosed()) {
    // ModelWorkspaceManager.getModelWorkspaceManager().getElementsOutOfSynchWithBuffers().remove(this);
    // getBufferManager().removeBuffer(event.getBuffer());
    // } else {
    // ModelWorkspaceManager.getModelWorkspaceManager().getElementsOutOfSynchWithBuffers().put(this, this);
    // }
    // }

}
