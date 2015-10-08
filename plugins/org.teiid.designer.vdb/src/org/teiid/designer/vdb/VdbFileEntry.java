/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.vdb.manifest.EntryElement;


/**
 * VdbFileEntry - represents VDB file entries, either UDF jars or otherFiles
 *
 * @since 8.0
 */
@ThreadSafe
public final class VdbFileEntry extends VdbEntry {

    private IPath sourceFilePath;
    private FileEntryType fileType;

    /**
     * Enumerates different fileTypes in the VDB (Udf jars, user files)
     */
    public enum FileEntryType {

        /**
         * User File Entry
         */
        UserFile,

        /**
         * UDF jar Entry
         */
        UDFJar;
    }
    
    /**
     * Constructs a file entry and adds it to the specified VDB. 
     * 
     * @param vdb the VDB where the resource is be added to (may not be <code>null</code>)
     * @param sourcePath the resource path (may not be <code>null</code>)
     * @param entryType the type of FileEntry
     * @throws Exception
     */
    public VdbFileEntry( final Vdb vdb,
                  final IPath sourcePath,
                  final FileEntryType entryType) throws Exception {

        super(vdb, sourcePath);
        this.sourceFilePath = sourcePath;
        this.fileType = entryType;

        // // Reset the path since this is not a workspace file
        resetPath();
    }

    /**
     * Constructs a file entry and adds it to the specified VDB. 
     * 
     * @param vdb the VDB where the resource is be added to (may not be <code>null</code>)
     * @param element the EntryElement
     * @throws Exception
     */
    public VdbFileEntry( final Vdb vdb,
                  final EntryElement element) throws Exception {
        
        super(vdb, Path.fromPortableString(element.getPath()));
        
        this.sourceFilePath = Path.fromPortableString(element.getPath());
        if(element.getPath().startsWith(StringConstants.FORWARD_SLASH + VdbFolders.UDF.getReadFolder())) {
            this.fileType = FileEntryType.UDFJar;
        } else {
            this.fileType = FileEntryType.UserFile;
        }

        // Reset the path since this is not a workspace file
        resetPath();
    }

    private void resetPath() throws Exception {
        if (sourceFilePath == null)
            return;

        String fileName = this.sourceFilePath.lastSegment();
        IPath newPath;
        
        if(this.fileType == FileEntryType.UDFJar)
            newPath =  new Path(StringConstants.FORWARD_SLASH + VdbFolders.UDF.getWriteFolder() + StringConstants.FORWARD_SLASH + fileName);
        else
            newPath = new Path(StringConstants.FORWARD_SLASH + VdbFolders.OTHER_FILES.getWriteFolder() + StringConstants.FORWARD_SLASH + fileName);

        setPath(newPath);

        // Need to re-synchronize due to changing the path
        setSynchronization(Synchronization.NotSynchronized);
        synchronize();

    }

    /**
     * @return source file path
     */
    public IPath getSourceFilePath() {
        return sourceFilePath;
    }

    /**
     * Called via {@link VdbEntry} constructor when {@link #sourceFilePath}
     * has not been initialised. So override to avoid the synchronisation until
     * {@link #resetPath()} has been called in our constructor.
     */
    @Override
    protected Synchronization synchronizeEntry() throws Exception {
        if (sourceFilePath == null)
            return Synchronization.NotApplicable;

        return super.synchronizeEntry();
    }

    /**
     * @return the associated workspace file, or <code>null</code> if it doesn't exist
     */
    @Override
    public IFile findFileInWorkspace() {
        IResource resource = ModelerCore.getWorkspace().getRoot().findMember(getSourceFilePath());
        if (resource == null) {
            // Lets try a little harder since the file may be in the project but not a model resource
            if (getVdb() != null && getVdb().getSourceFile() != null && getVdb().getSourceFile().getProject() != null) {
                IProject vdbProject = getVdb().getSourceFile().getProject();
                resource = vdbProject.findMember(getPath());

                if (resource == null)
                    resource = vdbProject.findMember(getSourceFilePath());

                if (resource == null) {
                    Collection<IFile> files = WorkspaceResourceFinderUtil.findIResourceInProjectByName(getPathName(), vdbProject);
                    //
                    // Pick the first one, which is the best we can do.
                    // If user has 2 files of the same name in the same project then its impossible
                    // to tell what to do.
                    //
                    if (! files.isEmpty())
                        resource = files.iterator().next();
                }
            }
        }

        if (!(resource instanceof IFile)) {
            setSynchronization(Synchronization.NotApplicable);
            return null;
        }

        //
        // Update the source file path to the resource path
        //
        sourceFilePath = resource.getFullPath();
        return (IFile)resource;
    }

    @Override
    public void save( final ZipOutputStream out) throws Exception {
        // Name of VDB entry
        String zipName = getPath().toOSString();

        //
        // Path on Windows will be using backslashes but zip entries only
        // deal with forward slashes so need to replace with them.
        //
        zipName = zipName.replace(DOUBLE_BACK_SLASH, FORWARD_SLASH);

        // Need to strip off the leading delimeter if it exists, else a "jar" extract command will result in models
        // being located at the file system "root" folder.
        if(zipName.startsWith(StringConstants.FORWARD_SLASH)) {
            zipName = zipName.substring(1, zipName.length());
        }
        final ZipEntry zipEntry = new ZipEntry(zipName);
        zipEntry.setComment(getDescription());

        File theFile = findSourceFile();
        save(out, zipEntry, theFile);
    }
    
    /**
     * @return the type of file
     */
    public FileEntryType getFileType() {
        return this.fileType;
    }
    
    /**
     * Find the artifact file at the specified path, for the specified project.  The
     * path may be absolute or it may be relative to the supplied project. The
     * source file could be in a couple of places depending on how the vdb
     * has been created.
     * <li>if the source file is in the workspace then the source file path will be
     *       relative to the project
     * <li>if the vdb has been freshly open then it could be beneath the staging folder
     *       of the parent vdb
     * <li>if the vdb has been created from scratch and the source file added
     *       then the source file path should be absolute
     *
     * @return the File object if found or throws a {@link FileNotFoundException}
     * @throws Exception
     */
    private File findSourceFile() throws Exception {
        //
        // Try and find the source file adjacent to the project since
        // this should be the most up to date version
        //
        IProject vdbProject = getVdb().getSourceFile().getProject();
        if (vdbProject != null) {

            IFile projectFile = vdbProject.getFile(getPath());
            if (projectFile.exists())
                return projectFile.getLocation().toFile();

            IPath sourceFilePath = getSourceFilePath();
            if (getSourceFilePath().isAbsolute()) {
                sourceFilePath = getSourceFilePath().makeRelativeTo(vdbProject.getFullPath());
            }

            projectFile = vdbProject.getFile(sourceFilePath);
            if (projectFile.exists())
                return projectFile.getLocation().toFile();
        }

        //
        // File cannot be found in project so see if the source file path
        // leads us to the file
        //
        File sourceFile = sourceFilePath.toFile();
        if (sourceFile.exists())
            return sourceFile;

        //
        // Source file refers to the path used in the jar, eg. lib/blah.jar.
        // This can happen when a vdb was opened that is not in the workspace.
        // To ensure that the file is saved, fetch the file from the staging area
        //
        sourceFile = new File(getVdb().getStagingFolder(), getPath().toOSString());
        if (sourceFile.exists())
            return sourceFile;

      throw new FileNotFoundException("Cannot save vdb file entry '" + sourceFilePath + "' to vdb file");  //$NON-NLS-1$//$NON-NLS-2$        
    }

    @Override
    public VdbFileEntry clone() {
        try {
            VdbFileEntry clone = new VdbFileEntry(getVdb(), sourceFilePath, fileType);
            cloneVdbObject(clone);
            return clone;
        } catch (Exception ex) {
            VdbPlugin.UTIL.log(ex);
            return null;
        }
    }
}
