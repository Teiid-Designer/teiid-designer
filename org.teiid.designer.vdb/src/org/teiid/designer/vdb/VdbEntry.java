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
import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import com.metamatrix.core.util.ChecksumUtil;

/**
 *
 */
@ThreadSafe
public class VdbEntry {

    private final IPath name;
    private final AtomicLong checksum = new AtomicLong();

    /**
     * @param name
     */
    public VdbEntry( final IPath name ) {
        this.name = name;
        synchronize();
    }

    private long computeChecksum() {
        final IFile file = findFile();
        if (file == null) return 0L;
        RuntimeException runtimeError = null;
        InputStream stream = null;
        try {
            stream = file.getContents();
            return ChecksumUtil.computeChecksum(stream).getValue();
        } catch (final Exception error) {
            runtimeError = new RuntimeException(error);
            throw runtimeError;
        } finally {
            if (stream != null) try {
                stream.close();
            } catch (final IOException ignored) {
                throw runtimeError;
            }
        }
    }

    void dispose() {
        // TODO: Drop file ref
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
        return (resource instanceof IFile ? (IFile)resource : null);
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
    public final SyncState getSyncState() {
        try {
            return (computeChecksum() == this.checksum.get() ? SyncState.InSync : SyncState.OutOfSync);
        } catch (final RuntimeException error) {
            return SyncState.NoFile;
        }
    }

    /**
     * @return <code>true</code> if synchronization was successful
     */
    public boolean synchronize() {
        // Return if resource to synchronize on doesn't exist
        if (!fileExists()) return false;
        // TODO: get ref to resource
        // Synchronize file checksum
        checksum.set(computeChecksum());
        return true;
    }

    /**
     * 
     */
    public enum SyncState {

        /**
         * The associated file for this entry does not exist
         */
        NoFile,

        /**
         * This entry is in-sync with its associated file
         */
        InSync,

        /**
         * This entry is out-of-sync with its associated file
         */
        OutOfSync;
    }
}
