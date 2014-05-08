/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;

/**
 *  Methods for selecting Vdb Files (udf jars or other) from the fileSystem or workspace
 */
public class VdbHelper implements StringConstants {

    /**
     * File filter for use with dialog file choosers
     */
    public static class FileFilter {

        private String name;

        private String filter;

        /**
         * @param name
         * @param filter
         */
        public FileFilter(String name, String filter) {
            this.name = name;
            this.filter = filter;
        }

        /**
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return the filter
         */
        public String getFilter() {
            return this.filter;
        }
    }

    /**
     * Jar file extension
     */
    public static final String JAR_EXT = "jar";  //$NON-NLS-1$

    /**
     * File filter for choosing only jar files
     */
    public static final FileFilter JAR_FILE_FILTER = new FileFilter(JAR_EXT, STAR + DOT + JAR_EXT);

    /**
     * File filter that shows all files
     */
    public static final FileFilter ALL_FILE_FILTER = new FileFilter("All files", STAR + DOT + STAR); //$NON-NLS-1$

    /**
     * Folder locations used by files populated
     * in vdbs
     */
    public enum VdbFolders {
        /**
         * Location of project udf files
         */
        UDF("lib", "lib", JAR_FILE_FILTER, JAR_EXT),  //$NON-NLS-1$ //$NON-NLS-2$

        /**
         * Location of other project files
         */
        OTHER_FILES(DOT, "otherFiles", ALL_FILE_FILTER, null); //$NON-NLS-1$

        private String readFolder;

        private String writeFolder;

        private FileFilter fileFilter;

        private String extension;

        private VdbFolders(String readFolder, String writeFolder, FileFilter fileFilter, String extension) {
            this.readFolder = readFolder;
            this.writeFolder = writeFolder;
            this.fileFilter = fileFilter;
            this.extension = extension;
        }

        /**
         * @return folder that should be read from
         */
        public String getReadFolder() {
            return readFolder;
        }

        /**
         * @return folder that should write to
         */
        public String getWriteFolder() {
            return writeFolder;
        }

        @Override
        public String toString() {
            return readFolder;
        }

        /**
         * @return file filter associated with this vdb folder
         */
        public FileFilter getFileFilter() {
            return fileFilter;
        }

        /**
         * @return associated extension
         */
        public String getExtension() {
            return extension;
        }
    }

    /**
     * Get all of the Udf jar resources for the supplied project
     * @param project the supplied project
     * @return the List of jar resource objects.
     */
    public static List<IResource> getUdfJarResources(IProject project) {
        List<IResource> jarResources = new ArrayList<IResource>();
        
        // Get Udf jar folder
        IContainer jarFolder = getFolder(project, VdbFolders.UDF.getReadFolder());
        
        // Iterate the child resources, looking for lib folder
        if(jarFolder!=null) {
            try {
                IResource[] folderEntries = jarFolder.members();
                for(int j=0; j<folderEntries.length; j++) {
                    IResource folderEntry = folderEntries[j];
                    if( folderEntry instanceof IFile && ((IFile)folderEntry).getFileExtension().equalsIgnoreCase(JAR_EXT) ) { 
                        jarResources.add(folderEntry);
                    }
                }
            } catch (CoreException ex) {
                ModelerCore.Util.log(IStatus.ERROR,ex,ModelerCore.Util.getString("VdbHelper.errorWithJarLookupInFolder", jarFolder.getName())); //$NON-NLS-1$
            }
        }
        return jarResources;
    }

    /**
     * Get the project's folder that either contains the Udf jars or other files.  If it doesnt exist, returns null.
     *
     * @param project the supplied project
     * @param folderName the name of the folder to get
     * @return the folder within the project, null if non-existent.
     */
    public static IContainer getFolder(IProject project, String folderName) {
        if (folderName == null || DOT.equals(folderName))
            return project;

        IFolder libFolder = null;
        if (project != null) {
            IResource[] resources = null;
            try {
                resources = project.members();
            } catch (CoreException ex) {
                return null;
            }

            // Iterate the child resources, looking for folder
            if (resources != null) {
                for (int i = 0; i < resources.length; i++) {
                    IResource theResc = resources[i];
                    if (theResc instanceof IFolder && folderName.equalsIgnoreCase(((IFolder)theResc).getName())) {
                        libFolder = (IFolder)theResc;
                        break;
                    }
                }
            }
        }

        return libFolder;
    }
    
    /**
     * Find the Vdb File at the specified path, for the specified project.  The
     * filePath may be absolute or it may be relative to the supplied project.
     * @param filePath the supplied path to the jar
     * @param project the supplied project
     * @return the File object if found, null if not found
     */
    public static File findVdbFile(String filePath, IProject project) {
        File theFile = null;
        if(filePath!=null && !filePath.trim().isEmpty()) {
            String thePath = filePath.toString();

            // The path may be absolute or relative to the project.  Check for both
            theFile = new File(thePath);
            // Check whether file exists.  If not assume the path is relative to the workspace
            if(!theFile.exists()) {
                IFile file = project.getFile(new Path(thePath));
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
    
    /**
     * Get the relative path to the file with the specified name
     * @param folder the supplied folder
     * @param fileName the specified file short name
     * @return the file relative path in the project, null if not found
     */
    public static String getFileRelativePath(IContainer folder,String fileName) {
        String relativePath = null;
        // Iterate the child resources, looking for jar
        if(folder!=null) {
            try {
                IResource[] folderEntries = folder.members();
                for(int j=0; j<folderEntries.length; j++) {
                    IResource folderEntry = folderEntries[j];
                    if( folderEntry instanceof IFile && ((IFile)folderEntry).getName().equalsIgnoreCase(fileName) ) { 
                        relativePath=folderEntry.getProjectRelativePath().toString();
                        break;
                    }
                }
            } catch (CoreException ex) {
                ModelerCore.Util.log(IStatus.ERROR,ex,ModelerCore.Util.getString("VdbHelper.errorGettingFileRelativePath", fileName)); //$NON-NLS-1$
            }
        }
        return relativePath;
    }
    
    /**
     * Determine if a file with the specified name is in the specified folder
     * @param folder the supplied folder
     * @param fileName the name of the file to find
     * @return 'true' if found, 'false' if not.
     */
    public static boolean isFileInFolder(IContainer folder, String fileName) {
        boolean found = false;
        // Iterate the child resources, looking for lib folder
        if(folder!=null) {
            try {
                IResource[] folderEntries = folder.members();
                for(int j=0; j<folderEntries.length; j++) {
                    IResource folderEntry = folderEntries[j];
                    if( folderEntry instanceof IFile && ((IFile)folderEntry).getProjectRelativePath().toString().equalsIgnoreCase(fileName) ) { 
                        found = true;
                        break;
                    }
                }
            } catch (CoreException ex) {
                ModelerCore.Util.log(IStatus.ERROR,ex,ModelerCore.Util.getString("VdbHelper.errorWithFileLookupInFolder", folder.getName())); //$NON-NLS-1$
            }
        }
        return found;
    }
    
    /**
     * Determine if the supplied folder contains at least one jar file
     * @param folder the supplied folder
     * @param extension of files contained in folder or null for any type of file
     * @return 'true' if the folder contains at least one file, 'false' if not.
     */
    public static boolean folderContainsOneOrMoreFile(IContainer folder, String extension) {
        boolean contains = false;
        // Iterate the child resources, looking for lib folder
        if(folder!=null) {
            try {
                IResource[] folderEntries = folder.members();
                for(int j=0; j<folderEntries.length; j++) {
                    IResource folderEntry = folderEntries[j];
                    if(! (folderEntry instanceof IFile))
                        continue;

                    if (extension == null)
                        return true; // found a file in the folder

                    IFile file = (IFile) folderEntry;
                    if (file.getFileExtension().equalsIgnoreCase(extension)) {
                            contains = true;
                            break;
                    }
                }
            } catch (CoreException ex) {
                ModelerCore.Util.log(IStatus.ERROR,ex,ModelerCore.Util.getString("VdbHelper.errorWithFileLookupInFolder", folder.getName())); //$NON-NLS-1$
            }
        }
        return contains;
    }
    
}
