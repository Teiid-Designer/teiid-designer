/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.io.File;
import java.io.FileInputStream;
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
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.PropertyElement;
import org.teiid.designer.vdb.plugin.VdbPlugin;
import com.metamatrix.core.modeler.util.FileUtils;
import com.metamatrix.core.util.ChecksumUtil;

/**
 *
 */
@ThreadSafe
public class VdbEntry {

    static final int IO_BUFFER_SIZE = 8092;

    private final IPath name;
    private final Vdb vdb;
    private final AtomicReference<Synchronization> synchronization = new AtomicReference<Synchronization>(
                                                                                                          Synchronization.NotApplicable);
    private long checksum;
    private final AtomicReference<String> description = new AtomicReference<String>();
    private final IResourceChangeListener fileListener;
    private final int hashcode;

    private final ReadWriteLock checksumLock = new ReentrantReadWriteLock();

    VdbEntry( final EntryElement entry,
              final Vdb vdb,
              final IProgressMonitor monitor ) {
        this(Path.fromPortableString(entry.getPath()), vdb);
        for (final PropertyElement property : entry.getProperties()) {
            final String name = property.getName();
            if (EntryElement.CHECKSUM.equals(name)) checksum = Long.parseLong(property.getValue());
        }
        final IFile workspaceFile = findFileInWorkspace();
        if (workspaceFile != null && checksum != computeChecksum(workspaceFile)) setSynchronization(Synchronization.NotSynchronized);
    }

    private VdbEntry( final IPath name,
                      final Vdb vdb ) {
        this.name = name;
        this.vdb = vdb;
        // Calculate hashcode
        hashcode = 31 + name.hashCode();
        // Register to listen for changes to this entries associated workspace file
        fileListener = new IResourceChangeListener() {
            public void resourceChanged( final IResourceChangeEvent event ) {
                final IResourceDelta delta = event.getDelta().findMember(name);
                if (delta == null) return;
                fileChanged(delta);
            }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(fileListener);
    }

    VdbEntry( final IPath name,
              final Vdb vdb,
              final IProgressMonitor monitor ) {
        this(name, vdb);
        // Synchronize with workspace file
        synchronizeEntry(monitor);
    }

    private long computeChecksum( final IFile file ) {
        Exception significantError = null;
        InputStream stream = null;
        try {
            stream = file.getContents();
            return ChecksumUtil.computeChecksum(stream).getValue();
        } catch (final Exception error) {
            significantError = error;
            setSynchronization(Synchronization.NotSynchronized);
            return 0;
        } finally {
            if (stream != null) try {
                stream.close();
            } catch (final IOException ignored) {
            }
            VdbPlugin.throwRuntimeExeption(significantError);
        }
    }

    void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(fileListener);
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

    final void fileChanged( final IResourceDelta delta ) {
        if ((delta.getFlags() & (IResourceDelta.REPLACED | IResourceDelta.MOVED_FROM | IResourceDelta.MOVED_TO)) > 0) throw new UnsupportedOperationException(
                                                                                                                                                              toString(delta));
        final int kind = delta.getKind();
        if (kind == IResourceDelta.REMOVED) setSynchronization(Synchronization.NotApplicable);
        else if (kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED) {
            if (getChecksum() != computeChecksum((IFile)delta.getResource())) setSynchronization(Synchronization.NotSynchronized);
            else setSynchronization(Synchronization.Synchronized);
        } else throw new UnsupportedOperationException(toString(delta));
    }

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
        final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(name);
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
        final ZipEntry zipEntry = new ZipEntry(name.toString());
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
        Exception significantError = null;
        InputStream in = null;
        try {
            out.putNextEntry(zipEntry);
            in = new FileInputStream(file);
            final byte[] buf = new byte[IO_BUFFER_SIZE];
            for (int len = in.read(buf); len >= 0; len = in.read(buf))
                out.write(buf, 0, len);
        } catch (final Exception error) {
            significantError = error;
        } finally {
            try {
                if (in != null) in.close();
                out.closeEntry();
            } catch (final IOException ignored) {
            }
            VdbPlugin.throwRuntimeExeption(significantError);
        }
    }

    /**
     * @param description
     */
    public final void setDescription( final String description ) {
        this.description.set(description);
    }

    private void setSynchronization( final Synchronization synchronization ) {
        final Synchronization oldSynchronization = getSynchronization();
        if (oldSynchronization == synchronization) return;
        this.synchronization.set(synchronization);
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
                VdbPlugin.throwRuntimeExeption(error);
            }
        } finally {
            checksumLock.writeLock().unlock();
        }
        vdb.setModified(this, Vdb.CHECKSUM, oldChecksum, checksum);
        return Synchronization.Synchronized;
    }

    private String toString( final IResourceDelta delta ) {
        final StringBuilder builder = new StringBuilder("file="); //$NON-NLS-1$
        builder.append(delta.getFullPath().toString());
        builder.append(", kind="); //$NON-NLS-1$
        builder.append(delta.getKind());
        builder.append(", flags="); //$NON-NLS-1$
        builder.append(delta.getFlags());
        builder.append(", from="); //$NON-NLS-1$
        builder.append(delta.getMovedFromPath());
        builder.append(", to="); //$NON-NLS-1$
        builder.append(delta.getMovedToPath());
        return builder.toString();
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
