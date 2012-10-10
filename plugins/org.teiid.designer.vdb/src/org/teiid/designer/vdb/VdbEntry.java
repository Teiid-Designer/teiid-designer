/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.Vdb.Event.ENTRY_CHECKSUM;
import static org.teiid.designer.vdb.Vdb.Event.ENTRY_DESCRIPTION;
import static org.teiid.designer.vdb.Vdb.Event.ENTRY_SYNCHRONIZATION;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.jcip.annotations.ThreadSafe;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.core.designer.util.ChecksumUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.ZipUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.PropertyElement;

/**
 *
 *
 * @since 8.0
 */
@ThreadSafe
public class VdbEntry {
	private static final String EMPTY_STR = StringUtilities.EMPTY_STRING;
	
    private final IPath name;
    private final Vdb vdb;
    private final AtomicReference<Synchronization> synchronization = new AtomicReference<Synchronization>(
                                                                                                          Synchronization.NotApplicable);
    private long checksum;
    final AtomicReference<String> description = new AtomicReference<String>();
//    private final IResourceChangeListener fileListener;
    private final int hashcode;

    private final ReadWriteLock checksumLock = new ReentrantReadWriteLock();

    VdbEntry( final Vdb vdb,
              final EntryElement element,
              final IProgressMonitor monitor ) {
        this(vdb, Path.fromPortableString(element.getPath()));
        for (final PropertyElement property : element.getProperties()) {
            final String name = property.getName();
            if (EntryElement.CHECKSUM.equals(name)) checksum = Long.parseLong(property.getValue());
        }
        final IFile workspaceFile = findFileInWorkspace();
        if (workspaceFile != null) setSynchronization(checksum == computeChecksum(workspaceFile) ? Synchronization.Synchronized : Synchronization.NotSynchronized);
        this.description.set(element.getDescription() == null ? StringUtilities.EMPTY_STRING : element.getDescription());
    }

    private VdbEntry( final Vdb vdb,
                      final IPath name ) {
        this.vdb = vdb;
        this.name = name;
        // Calculate hashcode
        hashcode = 31 + name.hashCode();
        // Register to listen for changes to this entries associated workspace file
//        fileListener = new IResourceChangeListener() {
//            public void resourceChanged( final IResourceChangeEvent event ) {
//            	if( ResourceChangeUtilities.isProjectClosing(event)) return;
//            	
//                final IResourceDelta delta = event.getDelta() == null ? null : event.getDelta().findMember(name);
//                if (delta == null) return;
//                if (ResourceChangeUtilities.isContentChanged(delta)) fileChanged(delta);
//            }
//        };
//        ModelerCore.getWorkspace().addResourceChangeListener(fileListener);
        if (this.description.get() == null) {
            this.description.set(EMPTY_STR);
        }
    }

    VdbEntry( final Vdb vdb,
              final IPath name,
              final IProgressMonitor monitor ) {
        this(vdb, name);
        // Synchronize with workspace file
        setSynchronization(synchronizeEntry(monitor));
    }

    private long computeChecksum( final IFile file ) {
        return OperationUtil.perform(new OperationUtil.ReturningUnreliable<Long>() {

            private InputStream stream = null;

            @Override
            public void doIfFails() {
                setSynchronization(Synchronization.NotSynchronized);
            }

            @Override
            public void finallyDo() throws Exception {
                if (stream != null) stream.close();
            }

            @Override
            public Long tryToDo() throws Exception {
                stream = file.getContents();
                return ChecksumUtil.computeChecksum(stream).getValue();
            }
        });
    }

    void dispose() {
//        ModelerCore.getWorkspace().removeResourceChangeListener(fileListener);
        new File(vdb.getFolder(), name.toString()).delete();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals( final Object object ) {
        if (this == object) return true;
        if (object == null) return false;
        if (getClass() != object.getClass()) return false;
        final VdbEntry other = (VdbEntry)object;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }
//
//    final void fileChanged( final IResourceDelta delta ) {
//        // TODO: Handle renames
//        if ((delta.getFlags() & (IResourceDelta.REPLACED | IResourceDelta.MOVED_FROM | IResourceDelta.MOVED_TO)) > 0) return; // throw
//                                                                                                                              // new
//                                                                                                                              // UnsupportedOperationException(
//        // toString(delta));
//        final int kind = delta.getKind();
//        if (kind == IResourceDelta.REMOVED) setSynchronization(Synchronization.NotApplicable);
//        else if (kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED) {
//            if (getChecksum() != computeChecksum((IFile)delta.getResource())) setSynchronization(Synchronization.NotSynchronized);
//            else setSynchronization(Synchronization.Synchronized);
//        } else throw new UnsupportedOperationException(toString(delta));
//    }

    /**
     * @return <code>true</code> if the associated file exists
     */
    public final boolean fileExistsInWorkspace() {
        return findFileInWorkspace() != null;
    }

    /**
     * @return the associated workspace file, or <code>null</code> if it doesn't exist
     */
    public final IFile findFileInWorkspace() {
        final IResource resource = ModelerCore.getWorkspace().getRoot().findMember(name);
        if (!(resource instanceof IFile)) {
            setSynchronization(Synchronization.NotApplicable);
            return null;
        }
        return (IFile)resource;
    }

    /**
     * @return the checksum of this entry's associated file
     */
    public final long getChecksum() {
        checksumLock.readLock().lock();
        try {
            return checksum;
        } finally {
            checksumLock.readLock().unlock();
        }
    }

    /**
     * @return description
     */
    public final String getDescription() {
        return description.get();
    }

    /**
     * @return name
     */
    public final IPath getName() {
        return name;
    }

    /**
     * @return <code>true</code> if the associated file doesn't exist or this file entry is synchronized with the associated file,
     *         i.e., the entry information matches the file information.
     */
    public final Synchronization getSynchronization() {
        return synchronization.get();
    }

    /**
     * @return the VDB containing this entry
     */
    final Vdb getVdb() {
        return vdb;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public final int hashCode() {
        return hashcode;
    }

    void save( final ZipOutputStream out,
               final IProgressMonitor monitor ) {
    	String zipName = name.toString();
    	// Need to strip off the leading delimeter if it exists, else a "jar" extract command will result in models
    	// being located at the file system "root" folder.
    	if( zipName.startsWith("/") ) { //$NON-NLS-1$
    		zipName = zipName.substring(1, zipName.length());
    	}
        final ZipEntry zipEntry = new ZipEntry(zipName);
        zipEntry.setComment(description.get());
        save(out, zipEntry, new File(vdb.getFolder(), name.toString()), monitor);
    }

    /**
     * @param out
     * @param zipEntry
     * @param file
     * @param monitor
     */
    protected final void save( final ZipOutputStream out,
                               final ZipEntry zipEntry,
                               final File file,
                               final IProgressMonitor monitor ) {
        ZipUtil.copy(file, zipEntry, out);
    }

    /**
     * @param description (never <code>null</code>)
     */
    public final void setDescription( String description ) {
        final String oldDescription = this.description.get();
        if (StringUtilities.equals(description, oldDescription)) return;
        this.description.set(description);
        vdb.setModified(this, ENTRY_DESCRIPTION, oldDescription, description);
    }

    /**
     * @param synchronization the new sychronization
     */
    public void setSynchronization( final Synchronization synchronization ) {
        final Synchronization oldSynchronization = getSynchronization();
        if (oldSynchronization == synchronization) return;
        this.synchronization.set(synchronization);
        vdb.notifyChangeListeners(this, ENTRY_SYNCHRONIZATION, oldSynchronization, synchronization);
    }

    /**
     * @param monitor
     */
    public void synchronize( final IProgressMonitor monitor ) {
        if (synchronization.get() != Synchronization.NotSynchronized) return;
        setSynchronization(synchronizeEntry(monitor));
    }

    /*
     * Private since called by constructor and don't want subclasses overriding
     */
    private Synchronization synchronizeEntry( final IProgressMonitor monitor ) {
        final IFile workspaceFile = findFileInWorkspace();
        if (workspaceFile == null) return Synchronization.NotApplicable;
        long oldChecksum = 0L;
        checksumLock.writeLock().lock();
        try {
            oldChecksum = checksum;
            checksum = computeChecksum(workspaceFile);
            // Copy snapshot of workspace file to VDB folder
            try {
                FileUtils.copy(workspaceFile.getLocation().toFile(),
                               new File(vdb.getFolder(), name.toString()).getParentFile(),
                               true);
            } catch (final IOException error) {
                throw CoreModelerPlugin.toRuntimeException(error);
            }
        } finally {
            checksumLock.writeLock().unlock();
        }
        vdb.setModified(this, ENTRY_CHECKSUM, oldChecksum, checksum);
        return Synchronization.Synchronized;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public final String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("(name="); //$NON-NLS-1$
        builder.append(name);
        builder.append(", synchronization="); //$NON-NLS-1$
        builder.append(synchronization);
        builder.append(", description="); //$NON-NLS-1$
        builder.append(description);
        toString(builder);
        builder.append(')');
        return builder.toString();
    }
//
//    private String toString( final IResourceDelta delta ) {
//        final StringBuilder builder = new StringBuilder("file="); //$NON-NLS-1$
//        builder.append(delta.getFullPath().toString());
//        builder.append(", kind="); //$NON-NLS-1$
//        builder.append(delta.getKind());
//        builder.append(", flags="); //$NON-NLS-1$
//        builder.append(delta.getFlags());
//        builder.append(", from="); //$NON-NLS-1$
//        builder.append(delta.getMovedFromPath());
//        builder.append(", to="); //$NON-NLS-1$
//        builder.append(delta.getMovedToPath());
//        return builder.toString();
//    }

    /**
     * Intended for a subclass to append its properties and their values, in the form ", <name>=<value>, ...", to the supplied
     * string builder, which represents the entry's {@link #toString()} value. Each name-value pair, including the first, must be
     * preceded by a comma followed by a space.
     * 
     * @param builder
     */
    protected void toString( final StringBuilder builder ) {
    }

    /**
     * 
     */
    public enum Synchronization {

        /**
         * This entry is synchronized with its corresponding workspace file
         */
        Synchronized,

        /**
         * This entry is out-of-sync with its corresponding workspace file
         */
        NotSynchronized,

        /**
         * Synchronization is not applicable to this entry, generally because the corresponding workspace file does not exist
         */
        NotApplicable;
    }
}
