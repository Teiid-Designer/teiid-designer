/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Checksum;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xsd.util.XSDResourceImpl;
import com.metamatrix.core.util.ChecksumUtil;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StreamPipe;
import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.extension.XPackage;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.DuplicateResourceException;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.util.ModelContents;
import com.metamatrix.modeler.core.workspace.ModelBuffer;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.Openable;
import com.metamatrix.modeler.internal.core.XsdObjectExtension;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.container.ResourceAction;
import com.metamatrix.modeler.internal.core.resource.EmfResource;

/**
 * ModelBufferImpl
 */
public class ModelBufferImpl implements ModelBuffer {

    private final IFile file;
    private final Openable owner;
    private final ResourceSet emfResourceSet;
    private Resource emfResource;
    private ModelContents contents;
    private boolean readonly;
    private Map options;
    private long lastModificationStamp;
    private long lastChecksum;
    private long lastFileSize;
    private String errorMessage;

    /**
     * Flag that is set to true just before the contents of the IFile are set, and set to false when the lastModificationStamp is
     * set immediately following. It is possible that events get processed after the contents are set but before the
     * lastModificationStamp is set; this flag is a hint to the resource processing the event to not unload.
     */
    private boolean inProcessOfSavingContents;

    /**
     * Construct an instance of ModelBufferImpl.
     */
    public ModelBufferImpl( final IFile file,
                            final Openable owner,
                            final ResourceSet emfResourceSet,
                            final boolean readonly ) {
        this.file = file;
        this.owner = owner;
        if (file == null) {
            setReadOnly(readonly);
        }
        updateCachedFileInformation();
        this.emfResourceSet = emfResourceSet;
        this.emfResource = null;
        this.options = new HashMap();
        this.contents = null;
        this.inProcessOfSavingContents = false;
        this.errorMessage = null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelBuffer#getEmfResource()
     */
    public Resource getEmfResource() {
        return this.emfResource;
    }

    protected ResourceSet getEmfResourceSet() {
        return this.emfResourceSet;
    }

    public ModelContents getModelContents() {
        if (this.contents == null && this.emfResource != null) {
            this.contents = new ModelContents(this.emfResource);
        }

        return this.contents;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelBuffer#getOwner()
     */
    public Openable getOwner() {
        return this.owner;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelBuffer#getUnderlyingResource()
     */
    public IResource getUnderlyingResource() {
        return this.file;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelBuffer#hasUnsavedChanges()
     */
    public boolean hasUnsavedChanges() {
        if (this.emfResource != null) {
            return this.emfResource.isModified();
        }
        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelBuffer#isClosed()
     */
    public boolean isClosed() {
        if (this.emfResource == null || !this.emfResource.isLoaded()) {
            return true;
        }

        return false;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelBuffer#isReadOnly()
     */
    public boolean isReadOnly() {
        if (this.file == null) {
            return this.readonly;
        }
        return this.file.isReadOnly();
    }

    /**
     * Sets this <code>Buffer</code> to be read only.
     */
    protected void setReadOnly( final boolean readOnly ) {
        this.readonly = readOnly;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelBuffer#unload()
     */
    public void unload() {
        if (!isClosed()) {
            this.emfResource.unload();
            this.contents = null;
            updateCachedFileInformation();
            this.errorMessage = null;
        }
    }

    protected void refresh( final IProgressMonitor progress ) {
        if (this.file != null) {
            try {
                // Refresh the file and update the lastModificationStamp ...
                this.file.refreshLocal(IResource.DEPTH_INFINITE, progress);
                updateCachedFileInformation();
                this.errorMessage = null;
            } catch (CoreException err) {
                final Object[] params = new Object[] {this.file.getFullPath().toString()};
                final String msg = ModelerCore.Util.getString("ModelBufferImpl.Error_while_refreshing", params); //$NON-NLS-1$
                throw new ModelerCoreRuntimeException(err, msg);
            }
        }
    }

    protected void open( final IProgressMonitor progress ) {
        if (this.emfResourceSet != null && this.file != null) {
            IPath path = this.file.getLocation();
            final URI uri = URI.createFileURI(path.toString());
            if (ModelerCore.DEBUG_MODEL_WORKSPACE) {
                final String pathInProj = this.file.getProject().getName() + IPath.SEPARATOR + this.file.getProjectRelativePath();
                final Object[] params = new Object[] {pathInProj};
                ModelerCore.Util.log(IStatus.INFO, ModelerCore.Util.getString("ModelBufferImpl.DEBUG.Opening_model", params)); //$NON-NLS-1$
            }

            boolean startedTxn = false;
            UnitOfWork txn = null;
            try {
                if (emfResourceSet instanceof Container) {
                    final Container cntr = ((Container)emfResourceSet);
                    txn = cntr.getEmfTransactionProvider().getCurrent();
                    if (!txn.isStarted()) {
                        try {
                            txn.begin();
                            txn.setSignificant(false);
                            txn.setUndoable(false);
                            txn.setSource(cntr);
                            // Defect 23120 - just adding a Description for this case so logging/debugging will know when this
                            // class is the "source" of the txn
                            txn.setDescription("ModelBufferImpl.open(" + this.getUnderlyingResource().getName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                        } catch (ModelerCoreException e) {
                            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                        }
                        startedTxn = true;
                    }
                }

                // Get or create the resource ...
                ResourceAction action = null;
                Resource duplicateOfResource = null;
                this.errorMessage = null;
                boolean justLoaded = false;
                try {
                    action = ContainerImpl.getOrCreateResource(this.emfResourceSet, uri); // may throw DuplicateResourceException
                    this.emfResource = action.getResource();
                    File file = this.file.getLocation().toFile();
                    if (file.canRead() && file.exists() && file.length() != 0 && !this.emfResource.isLoaded()) {
                        this.emfResource.load(this.emfResourceSet.getLoadOptions());
                        justLoaded = true;
                    }
                } catch (DuplicateResourceException e) {
                    duplicateOfResource = e.getDuplicateOfModel();
                    this.errorMessage = e.getMessage();
                } catch (Throwable t) {
                    final Object[] params = new Object[] {uri};
                    final String msg = ModelerCore.Util.getString("ModelBufferImpl.Error_creating_resource_for_URI", params); //$NON-NLS-1$
                    throw new ModelerCoreRuntimeException(t, msg);
                }

                if (this.emfResource != null) {
                    if (this.emfResource instanceof EmfResource) {
                        this.contents = ((EmfResource)this.emfResource).getModelContents();
                        CoreArgCheck.isNotNull(this.contents);
                    }
                    if (this.contents == null) {
                        this.contents = new ModelContents(this.emfResource);
                    }

                    // Register the resource
                    final OpenableImpl modelResource = (OpenableImpl)this.getOwner();
                    modelResource.getBufferManager().registerEmfResource(this.emfResource, modelResource);

                    if (justLoaded) {
                        // Force creation of ModelAnnotation and save model
                        this.getModelContents().getModelAnnotation();
                        try {
                            // this.emfResource.save( new HashMap() );
                            this.updateCachedFileInformation();
                        } catch (Exception e) {
                            final Object[] params = new Object[] {uri};
                            final String msg = ModelerCore.Util.getString("ModelBufferImpl.Unable_to_initialize_new_model_URI", params); //$NON-NLS-1$
                            throw new ModelerCoreRuntimeException(e, msg);
                        }
                    }
                    if (this.file.exists()) {
                        try {
                            // Be sure to unmark as duplicate ...
                            this.file.setSessionProperty(ModelerCore.DUPLICATE_MODEL_OF_IPATH_KEY, null);
                        } catch (CoreException e) {
                            ModelerCore.Util.log(e);
                        }
                    }
                } else if (duplicateOfResource != null) {
                    final ModelResource duplicateOfModelResource = ModelerCore.getModelWorkspace().findModelResource(duplicateOfResource);

                    if (duplicateOfModelResource == null) {
                        throw new DuplicateResourceException(duplicateOfResource, null, this.errorMessage);
                    }

                    final IPath duplicateOfModelPath = duplicateOfModelResource.getPath();

                    // Mark the resource with a "duplicate" session property ...
                    try {
                        this.file.setSessionProperty(ModelerCore.DUPLICATE_MODEL_OF_IPATH_KEY, duplicateOfModelPath.toString());
                    } catch (CoreException err) {
                        final Object[] params = new Object[] {uri, duplicateOfModelPath};
                        final String msg = ModelerCore.Util.getString("ModelBufferImpl.Unable_to_mark_resource_as_duplicate", params); //$NON-NLS-1$
                        throw new ModelerCoreRuntimeException(err, msg);
                    }

                    throw new DuplicateResourceException(duplicateOfResource, duplicateOfModelPath, this.errorMessage);
                } else {
                    throw new ModelerCoreRuntimeException(
                                                          ModelerCore.Util.getString("ModelBufferImpl.Could_not_resolve_local_resource_for_{0}_1", uri)); //$NON-NLS-1$
                }

            } finally {
                if (startedTxn && txn != null) {
                    try {
                        txn.commit();
                    } catch (ModelerCoreException e) {
                        ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                    }
                }
                // ensure resource does not have changes just because we opened it
                if (this.emfResource != null) {
                    this.emfResource.setModified(false);
                }

            }

        } else {
            throw new ModelerCoreRuntimeException(
                                                  ModelerCore.Util.getString("ModelBufferImpl.Can_not_open_Model_Buffer_with_both_a_null_ResourceSet_and_null_IFile_1")); //$NON-NLS-1$
        }
    }

    protected void updateCachedFileInformation() {
        if (this.file == null) {
            this.lastModificationStamp = INITIAL_MOD_STAMP;
            this.lastFileSize = 0;
            this.lastChecksum = 0;
        } else {
            this.lastModificationStamp = this.file.getModificationStamp();
            if (!this.file.exists()) {
                this.lastFileSize = 0;
                this.lastChecksum = 0;
            } else {
                final IPath rawLocation = file.getRawLocation();
                if (rawLocation != null) {
                    final File rawFile = new File(rawLocation.toString());
                    if (rawFile.exists()) {
                        this.lastFileSize = rawFile.length();
                        InputStream stream = null;
                        try {
                            stream = new FileInputStream(rawFile);
                            final InputStream buffer = new BufferedInputStream(stream);
                            final Checksum checksum = ChecksumUtil.computeChecksum(buffer);
                            this.lastChecksum = checksum.getValue();
                        } catch (IOException err) {
                            ModelerCore.Util.log(err);
                        } finally {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (IOException err1) {
                                    // Ignore; cant' do anything anyway
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelBuffer#getErrors()
     * @since 4.2
     */
    public IStatus getErrors() {
        if (hasErrors()) {
            final String msg = this.errorMessage != null ? this.errorMessage : ""; //$NON-NLS-1$
            return new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, 0, msg, null);
        }
        return new Status(IStatus.OK, ModelerCore.PLUGIN_ID, 0, "", null); //$NON-NLS-1$
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelBuffer#hasErrors()
     * @since 4.2
     */
    public boolean hasErrors() {
        if (this.file == null) {
            return false;
        }

        // See if the model was marked as a duplicate ...
        Object duplicateOfModel = null;
        try {
            duplicateOfModel = this.file.getSessionProperty(ModelerCore.DUPLICATE_MODEL_OF_IPATH_KEY);
        } catch (CoreException err) {
            // Do nothing; treat as tho not a duplicate ...
        }

        return duplicateOfModel != null;
    }

    /**
     * @see com.metamatrix.modeler.core.ModelBuffer#close()
     */
    public void close() {
        if (this.emfResource != null) {
            if ((this.file != null) && ModelerCore.DEBUG_MODEL_WORKSPACE) {
                final String pathInProj = this.file.getProject().getName() + IPath.SEPARATOR + this.file.getProjectRelativePath();
                final Object[] params = new Object[] {pathInProj};
                ModelerCore.Util.log(IStatus.INFO, ModelerCore.Util.getString("ModelBufferImpl.DEBUG.Closing_model", params)); //$NON-NLS-1$
            }
            // Remove the EMF resource from the resource set ...
            boolean startedTxn = false;
            UnitOfWork txn = null;
            try {
                // We assume any ModelWorkspace resources container will be the ModelContainer
                final Container cntr = ModelerCore.getModelContainer();
                txn = cntr.getEmfTransactionProvider().getCurrent();
                if (!txn.isStarted()) {
                    try {
                        txn.begin();
                        txn.setSignificant(false);
                        txn.setUndoable(false);
                        // Defect 23120 - just adding a Description for this case so logging/debugging will know when this
                        // class is the "source" of the txn
                        txn.setDescription("ModelBufferImpl.close(" + this.getUnderlyingResource().getName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                    } catch (ModelerCoreException e) {
                        ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                    }
                    startedTxn = true;
                }

                // Unregister the resource
                final OpenableImpl modelResource = (OpenableImpl)this.getOwner();
                modelResource.getBufferManager().unregisterEmfResource(this.emfResource);

                this.emfResource.unload();

                this.contents = null;
            } catch (CoreException theException) {
                ModelerCore.Util.log(IStatus.ERROR, theException, theException.getMessage());
            } finally {
                if (startedTxn && txn != null) {
                    try {
                        txn.commit();
                    } catch (ModelerCoreException e) {
                        ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                    }
                }
            }
        }

        updateCachedFileInformation();
    }

    /**
     * @see com.metamatrix.modeler.core.ModelBuffer#save(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void save( final IProgressMonitor progress,
                      boolean force ) throws ModelWorkspaceException {
        if (this.file == null) {
            return;
        }

        if (this.file.isReadOnly()) {
            final String pathInProj = this.file.getProject().getName() + IPath.SEPARATOR + this.file.getProjectRelativePath();
            final Object[] params = new Object[] {pathInProj};
            throw new ModelWorkspaceException(ModelerCore.Util.getString("ModelBufferImpl.Model_is_readonly", params)); //$NON-NLS-1$
        }

        // use a platform operation to update the resource contents
        try {
            // Since the EMF Resource is saved using an OutputStream but the
            // Platform saves using an InputStream, use a stream pipe ...
            final StreamPipe pipe = new StreamPipe();
            final InputStream istream = pipe.getInputStream();
            final OutputStream ostream = pipe.getOutputStream();

            // -------------------------------------------------------------------------
            // Write to the pipe
            // -------------------------------------------------------------------------
            if (ModelerCore.DEBUG_MODEL_WORKSPACE) {
                final String pathInProj = this.file.getProject().getName() + IPath.SEPARATOR + this.file.getProjectRelativePath();
                final Object[] params = new Object[] {pathInProj};
                ModelerCore.Util.log(IStatus.INFO, ModelerCore.Util.getString("ModelBufferImpl.DEBUG.Saving_model", params)); //$NON-NLS-1$
            }
            // Write the EMF Resource to the ostream (and in a separate thread)
            final Map theOptions = this.options; // must be in final variable
            new Thread("ModelBuffer.Save") { //$NON-NLS-1$
                @Override
                public void run() {
                    saveInTransaction(ostream, theOptions);
                }
            }.start();

            // -------------------------------------------------------------------------
            // Read from the pipe
            // -------------------------------------------------------------------------
            // Put the contents into the file ...
            //           
            // It is possible that events get processed
            // after the contents are set but before the lastModificationStamp is set; this flag is a hint
            // to the resource processing the event to not unload.
            if (this.file.exists()) {
                this.file.refreshLocal(IResource.DEPTH_ZERO, null);
                this.inProcessOfSavingContents = true;
                this.file.setContents(istream,
                                      force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY,
                                      progress);
            } else {
                this.inProcessOfSavingContents = true;
                this.file.create(istream, force ? IResource.FORCE | IResource.KEEP_HISTORY : IResource.KEEP_HISTORY, progress);
            }
            updateCachedFileInformation();
            this.inProcessOfSavingContents = false;
        } catch (CoreException e) {
            throw new ModelWorkspaceException(e);
        }
    }

    void saveInTransaction( OutputStream ostream,
                            Map theOptions ) {
        // Remove the EMF resource from the resource set ...
        boolean startedTxn = false;
        UnitOfWork txn = null;
        try {
            // We assume any ModelWorkspace resources container will be the ModelContainer
            final Container cntr = ModelerCore.getModelContainer();
            txn = cntr.getEmfTransactionProvider().getCurrent();
            if (!txn.isStarted()) {
                try {
                    txn.begin();
                    txn.setSignificant(false);
                    txn.setUndoable(false);
                    // Defect 23120 - just adding a Description for this case so logging/debugging will know when this
                    // class is the "source" of the txn
                    txn.setDescription("ModelBufferImpl.saveInTransaction(" + this.getUnderlyingResource().getName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
                } catch (ModelerCoreException e) {
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                }
                startedTxn = true;
            }

            try {
                final Resource resource = ModelBufferImpl.this.emfResource;

                // mmDefect_12555 - If the resource is an XSD resource then make sure any extension package
                // value is set on the schema annotation
                if (resource instanceof XSDResourceImpl) {
                    ModelAnnotation annotation = ModelBufferImpl.this.contents.getModelAnnotation();
                    XPackage extPackage = annotation.getExtensionPackage();
                    try {
                        if (extPackage != null) {
                            XsdObjectExtension.setExtensionPackage((XSDResourceImpl)resource, extPackage);
                        } else {
                            XsdObjectExtension.removeExtensionPackage((XSDResourceImpl)resource);
                        }
                    } catch (ModelerCoreException err) {
                        ModelerCore.Util.log(IStatus.ERROR, err.getMessage());
                    }
                }

                ModelBufferImpl.this.emfResource.save(ostream, theOptions);
            } catch (IOException e) {
                // Do nothing, since this was probably a cancel from
                // the file.setContents(...)
            } finally {
                try {
                    ostream.close();
                } catch (IOException e1) {
                    final String pathInProj = file.getProject().getName() + IPath.SEPARATOR + file.getProjectRelativePath();
                    final Object[] params = new Object[] {pathInProj, e1.getMessage()};
                    ModelerCore.Util.log(IStatus.INFO,
                                         e1,
                                         ModelerCore.Util.getString("ModelBufferImpl.Error_closing_stream", params)); //$NON-NLS-1$
                }
            }
        } catch (CoreException theException) {
            ModelerCore.Util.log(IStatus.ERROR, theException, theException.getMessage());
        } finally {
            if (startedTxn && txn != null) {
                try {
                    txn.commit();
                } catch (ModelerCoreException e) {
                    ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
                }
            }
        }
    }

    /**
     * @return
     */
    public Map getOptions() {
        return options;
    }

    /**
     * @param map
     */
    public void setOptions( Map map ) {
        options = map;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelBuffer#getLastModificationStamp()
     * @since 4.2
     */
    public long getLastModificationStamp() {
        return lastModificationStamp;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelBuffer#getLastFileSize()
     * @since 4.2
     */
    public long getLastFileSize() {
        return lastFileSize;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelBuffer#getLastChecksum()
     * @since 4.2
     */
    public long getLastChecksum() {
        return lastChecksum;
    }

    /**
     * @see com.metamatrix.modeler.core.workspace.ModelBuffer#isInProcessOfSaving()
     * @since 4.2
     */
    public boolean isInProcessOfSaving() {
        return inProcessOfSavingContents;
    }
}
