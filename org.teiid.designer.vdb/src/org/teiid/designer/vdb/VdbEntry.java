/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
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
import com.metamatrix.core.util.ChecksumUtil;

/**
 *
 */
@ThreadSafe
public class VdbEntry {

    private final IPath name;
    private final Vdb vdb;
    private final AtomicReference<Synchronization> synchronization = new AtomicReference<Synchronization>(
                                                                                                          Synchronization.NotSynchronized);
    private final AtomicLong checksum = new AtomicLong();
    private final IResourceChangeListener fileListener;

    VdbEntry( final IPath name,
              final Vdb vdb,
              final IProgressMonitor monitor ) {
        this.name = name;
        this.vdb = vdb;
        synchronizeEntry(monitor);
        fileListener = new IResourceChangeListener() {
            public void resourceChanged( final IResourceChangeEvent event ) {
                final IResourceDelta delta = event.getDelta().findMember(name);
                if (delta == null) return;
                fileChanged(delta);
            }
        };
        ResourcesPlugin.getWorkspace().addResourceChangeListener(fileListener);
    }

    VdbEntry( final String name,
              final Vdb vdb,
              final IProgressMonitor monitor ) {
        this(Path.fromPortableString(name), vdb, monitor);
    }

    private long computeChecksum( final IFile file ) {
        RuntimeException runtimeError = null;
        InputStream stream = null;
        try {
            stream = file.getContents();
            return ChecksumUtil.computeChecksum(stream).getValue();
        } catch (final Exception error) {
            runtimeError = new RuntimeException(error);
            setSynchronization(Synchronization.NotSynchronized);
            throw runtimeError;
        } finally {
            if (stream != null) try {
                stream.close();
            } catch (final IOException ignored) {
                if (runtimeError != null) throw runtimeError;
            }
        }
    }

    void dispose() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(fileListener);
    }

    void fileChanged( final IResourceDelta delta ) {
        if ((delta.getFlags() & (IResourceDelta.REPLACED | IResourceDelta.MOVED_FROM | IResourceDelta.MOVED_TO)) > 0) throw new UnsupportedOperationException(
                                                                                                                                                              toString(delta));
        final int kind = delta.getKind();
        if (kind == IResourceDelta.REMOVED) setSynchronization(Synchronization.NotApplicable);
        else if (kind == IResourceDelta.ADDED || kind == IResourceDelta.CHANGED) {
            if (checksum.get() != computeChecksum((IFile)delta.getResource())) setSynchronization(Synchronization.NotSynchronized);
            else setSynchronization(Synchronization.Synchronized);
        } else throw new UnsupportedOperationException(toString(delta));
    }

    /**
     * @return <code>true</code> if the associated file exists
     */
    public final boolean fileExists() {
        return findFile() != null;
    }

    /**
     * @return the associated workspace file, or <code>null</code> if it doesn't exist
     */
    public final IFile findFile() {
        final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(name);
        if (!(resource instanceof IFile)) {
            setSynchronization(Synchronization.NotApplicable);
            return null;
        }
        return (IFile)resource;
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
     * @throws RuntimeException
     */
    public final Synchronization getSynchronization() {
        return synchronization.get();
    }

    /**
     * @return the VDB containing this entry
     */
    Vdb getVdb() {
        return vdb;
    }

    private void setSynchronization( final Synchronization synchronization ) {
        final Synchronization oldSynchronization = getSynchronization();
        if (oldSynchronization == synchronization) return;
        this.synchronization.set(synchronization);
        vdb.notifyChangeListeners(this, Vdb.SYNCHRONIZATION, oldSynchronization, synchronization);
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
        final IFile file = findFile();
        if (file == null) return Synchronization.NotApplicable;
        checksum.set(computeChecksum(file));
        return Synchronization.Synchronized;
    }

    String toString( final IResourceDelta delta ) {
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
