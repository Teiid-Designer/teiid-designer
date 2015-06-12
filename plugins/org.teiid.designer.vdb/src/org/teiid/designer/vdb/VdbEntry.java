/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import static org.teiid.designer.vdb.Vdb.Event.ENTRY_CHECKSUM;
import java.io.File;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.teiid.core.designer.util.ChecksumUtil;
import org.teiid.core.designer.util.FileUtils;
import org.teiid.core.designer.util.OperationUtil;
import org.teiid.core.designer.util.ZipUtil;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.vdb.Vdb.Event;
import org.teiid.designer.vdb.manifest.EntryElement;
import org.teiid.designer.vdb.manifest.PropertyElement;

/**
 *
 *
 * @since 8.0
 */
@ThreadSafe
public abstract class VdbEntry extends VdbUnit {

    private IPath path;

    private final AtomicReference<Synchronization> synchronization = new AtomicReference<Synchronization>(
                                                                                                          Synchronization.NotApplicable);
    private long checksum;

    private final ReadWriteLock checksumLock = new ReentrantReadWriteLock();

    /**
     * @param vdb
     * @param element
     * @throws Exception
     */
    public VdbEntry( final Vdb vdb, final EntryElement element) throws Exception {
        this(vdb, Path.fromPortableString(element.getPath()));

        long propChecksum = -1;
        for (final PropertyElement property : element.getProperties()) {
            final String name = property.getName();
            if (EntryElement.CHECKSUM.equals(name))
                propChecksum = Long.parseLong(property.getValue());
        }

        if (Synchronization.Synchronized.equals(getSynchronization())) {
            //
            // Already been synchronized from calling 'this' above.
            // Means there is a file in the workspace but don't yet know if
            // it is still the same file as that in the vdb.
            //
            // Now check if the checksum created matches that extracted
            // from the properties. If it does not then entry is not synchronized
            // with workspace.
            //
            if (this.checksum != propChecksum)
                setSynchronization( Synchronization.NotSynchronized);
        }

        setDescription(element.getDescription() == null ? EMPTY_STRING : element.getDescription());
    }

    /**
     * <li>The name of the entry is the path name WITHOUT the extension
     * <li>The path of the entry is available via {@link #getPath()}
     *
     * @param vdb
     * @param path
     * @throws Exception
     *
     */
    public VdbEntry( final Vdb vdb, final IPath path) throws Exception {
        super(vdb);
        this.path = path;
        setName(path.removeFileExtension().lastSegment());

        setSynchronization(synchronizeEntry());
    }

    /**
     * @return path of this entry
     */
    public IPath getPath() {
        return path;
    }

    /**
     * @param path
     */
    public void setPath(IPath path) {
        this.path = path;
        super.setName(path.removeFileExtension().lastSegment());
    }

    /**
     * @return the file name based on the path's last segment
     */
    public String getPathName() {
        if (path == null)
            return null;

        return path.lastSegment();
    }

    /**
     * @return the parent directory represented by the given path
     */
    public String getDirectory() {
        if (path == null)
            return null;

        return path.removeLastSegments(1).toOSString();
    }

    private long computeChecksum( final IFile file ) throws Exception {
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
                if (stream == null)
                    return -1L;

                return ChecksumUtil.computeChecksum(stream).getValue();
            }
        });
    }

    /**
     * 
     */
    public void dispose() {
        new File(getVdb().getStagingFolder(), getPath().lastSegment()).delete();
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
    public IFile findFileInWorkspace() {
        IResource resource = ModelerCore.getWorkspace().getRoot().findMember(getPath());
        if (resource == null) {
            // Lets try a little harder since the file may be in the project but not a model resource
            if (getVdb() != null && getVdb().getSourceFile() != null && getVdb().getSourceFile().getProject() != null) {
                IProject vdbProject = getVdb().getSourceFile().getProject();
                resource = vdbProject.findMember(getPath());
            }
        }

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
     * @return <code>true</code> if the associated file doesn't exist or this file entry is synchronized with the associated file,
     *         i.e., the entry information matches the file information.
     */
    public final Synchronization getSynchronization() {
        return synchronization.get();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.path == null) ? 0 : this.path.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        VdbEntry other = (VdbEntry)obj;
        if (this.path == null) {
            if (other.path != null)
                return false;
        } else if (!this.path.equals(other.path))
            return false;
        return true;
    }

    /**
     * @param out
     * @throws Exception
     */
    public void save( final ZipOutputStream out) throws Exception {
    	String zipName = getPath().toString();
    	// Need to strip off the leading delimeter if it exists, else a "jar" extract command will result in models
    	// being located at the file system "root" folder.
    	if( zipName.startsWith("/") ) { //$NON-NLS-1$
    		zipName = zipName.substring(1, zipName.length());
    	}
        final ZipEntry zipEntry = new ZipEntry(zipName);
        zipEntry.setComment(getDescription());
        save(out, zipEntry, new File(getVdb().getStagingFolder(), getPath().toOSString()));
    }

    /**
     * @param out
     * @param zipEntry
     * @param file
     * @throws Exception
     */
    protected final void save( final ZipOutputStream out,
                               final ZipEntry zipEntry,
                               final File file) throws Exception {
        ZipUtil.copy(file, zipEntry, out);
    }

    /**
     * @param synchronization the new sychronization
     */
    public void setSynchronization( final Synchronization synchronization ) {
        final Synchronization oldSynchronization = getSynchronization();
        if (oldSynchronization == synchronization) return;
        this.synchronization.set(synchronization);
        setModified(this, Event.ENTRY_SYNCHRONIZATION, oldSynchronization, synchronization);
    }

    /**
     * @throws Exception
     */
    public void synchronize() throws Exception {
        if (synchronization.get() != Synchronization.NotSynchronized) return;
        setSynchronization(synchronizeEntry());
    }

    /*
     * Private since called by constructor and don't want subclasses overriding
     */
    protected Synchronization synchronizeEntry() throws Exception {
        final IFile workspaceFile = findFileInWorkspace();
        if (workspaceFile == null) return Synchronization.NotApplicable;
        long oldChecksum = 0L;
        checksumLock.writeLock().lock();
        try {
            oldChecksum = checksum;
            checksum = computeChecksum(workspaceFile);
            // Copy snapshot of workspace file to VDB folder
            FileUtils.copy(workspaceFile.getLocation().toFile(),
                           new File(getVdb().getStagingFolder(), getPath().toOSString()).getParentFile(),
                           true);
        } finally {
            checksumLock.writeLock().unlock();
        }
        setModified(this, ENTRY_CHECKSUM, oldChecksum, checksum);
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
        builder.append(getName());
        builder.append(", synchronization="); //$NON-NLS-1$
        builder.append(synchronization);
        builder.append(", description="); //$NON-NLS-1$
        builder.append(getDescription());
        toString(builder);
        builder.append(')');
        return builder.toString();
    }

    /**
     * Intended for a subclass to append its properties and their values, in the form ", <name>=<value>, ...", to the supplied
     * string builder, which represents the entry's {@link #toString()} value. Each name-value pair, including the first, must be
     * preceded by a comma followed by a space.
     * 
     * @param builder
     */
    protected void toString( final StringBuilder builder ) {
        // Does Nothing
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
