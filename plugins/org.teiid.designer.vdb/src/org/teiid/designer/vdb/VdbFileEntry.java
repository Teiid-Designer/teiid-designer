/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.vdb;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.jcip.annotations.ThreadSafe;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.util.VdbHelper.VdbFolders;
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
     * @param monitor the progress monitor or <code>null</code>
     */
    public VdbFileEntry( final Vdb vdb,
                  final IPath sourcePath,
                  final FileEntryType entryType,
                  final IProgressMonitor monitor ) {
        
        super(vdb, sourcePath, monitor);
        
        this.sourceFilePath = sourcePath;
        this.fileType = entryType;
        
        // Resets name since this is not a workspace file
        setName(determinePath(sourcePath,entryType));
    } 
    
    /**
     * Constructs a file entry and adds it to the specified VDB. 
     * 
     * @param vdb the VDB where the resource is be added to (may not be <code>null</code>)
     * @param element the EntryElement
     * @param monitor the progress monitor or <code>null</code>
     */
    VdbFileEntry( final Vdb vdb,
                  final EntryElement element,
                  final IProgressMonitor monitor ) {
        
        super(vdb, Path.fromPortableString(element.getPath()), monitor);
        
        this.sourceFilePath = Path.fromPortableString(element.getPath());
        if(element.getPath().startsWith(StringConstants.FORWARD_SLASH + VdbFolders.UDF.getReadFolder())) {
            this.fileType = FileEntryType.UDFJar;
        } else {
            this.fileType = FileEntryType.UserFile;
        }
        
        // Resets name since this is not a workspace file
        setName(determinePath(this.sourceFilePath,this.fileType));
    } 
    
    private IPath determinePath(IPath sourcePath,FileEntryType entryType) {
        String fileName = sourcePath.lastSegment();
        if(entryType==FileEntryType.UserFile) {
            return new Path(StringConstants.FORWARD_SLASH + VdbFolders.OTHER_FILES.getReadFolder() + StringConstants.FORWARD_SLASH + fileName);  
        } else if(entryType==FileEntryType.UDFJar) {
            return new Path(StringConstants.FORWARD_SLASH + VdbFolders.UDF.getReadFolder() + StringConstants.FORWARD_SLASH + fileName);  
        }
        return null;
    }
    
    @Override
    void save( final ZipOutputStream out,
               final IProgressMonitor monitor ) {
        // Name of VDB entry
        String zipName = getName().toString();
        // Need to strip off the leading delimeter if it exists, else a "jar" extract command will result in models
        // being located at the file system "root" folder.
        if(zipName.startsWith(StringConstants.FORWARD_SLASH)) {
            zipName = zipName.substring(1, zipName.length());
        }
        final ZipEntry zipEntry = new ZipEntry(zipName);
        zipEntry.setComment(description.get());
        
        IProject vdbProject = getVdb().getFile().getProject();
        File theFile = findUdfFile(vdbProject);

        if(theFile!=null && theFile.exists()) {
            save(out, zipEntry, theFile, monitor);
        }
    }
    
    /**
     * @return the type of file
     */
    public FileEntryType getFileType() {
        return this.fileType;
    }
    
    /**
     * Find the Udf File for at the specified path, for the specified project.  The
     * udfPath may be absolute or it may be relative to the supplied project.
     * @param project the supplied project
     * @return the File object if found, null if not found
     */
    private File findUdfFile(IProject project) {
        String udfPath = sourceFilePath.toString();
        
        File theFile = null;
        if(udfPath!=null) {
            String filePath = udfPath.toString();

            // The path may be absolute or relative to the project.  Check for both
            theFile = new File(filePath);
            // Check whether file exists.  If not assume the path is relative to the workspace
            if(!theFile.exists()) {
                // The name is relative to the project
                IFile file = project.getFile(getName());
                if(file!=null) {
                    IPath locationPath = file.getLocation();
                    if(locationPath!=null) {
                        theFile = locationPath.toFile();
                        if(!theFile.exists()) {
                            theFile=null;
                        }
                    }
                }
            }
        }
        return theFile;
    }
    
}
