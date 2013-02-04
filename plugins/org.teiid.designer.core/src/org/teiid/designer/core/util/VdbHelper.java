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
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.teiid.designer.core.ModelerCore;

/**
 *  Methods for selecting Vdb Files (udf jars or other) from the fileSystem or workspace
 */
public class VdbHelper {
    
    public static final String UDF_FOLDER = "lib";  //$NON-NLS-1$
    public static final String OTHER_FILES_FOLDER = "otherFiles";  //$NON-NLS-1$
    public static final String JAR_EXT = "jar";  //$NON-NLS-1$

    /**
     * Get all of the Udf jar resources for the supplied project
     * @param project the supplied project
     * @return the List of jar resource objects.
     */
    public static List<IResource> getUdfJarResources(IProject project) {
        List<IResource> jarResources = new ArrayList<IResource>();
        
        // Get Udf jar folder
        IFolder jarFolder = getFolder(project,UDF_FOLDER);
        
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
     * @param project the supplied project
     * @param folderName the name of the folder to get
     * @return the folder within the project, null if non-existent.
     */
    public static IFolder getFolder(IProject project, String folderName) {
        IFolder libFolder = null;
        if(project!=null) {
            IResource[] resources = null;
            try {
                resources = project.members();
            } catch (CoreException ex) {
                return null;
            }
            // Iterate the child resources, looking for lib folder
            if(resources!=null) {
                for(int i=0; i<resources.length; i++) {
                    IResource theResc = resources[i];
                    if(theResc instanceof IFolder && ((IFolder)theResc).getName().equalsIgnoreCase(folderName)) { 
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
    public static String getFileRelativePath(IFolder folder,String fileName) {
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
    public static boolean isFileInFolder(IFolder folder,String fileName) {
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
     * @param udfJar 'true' if looking for only jars, 'false' otherwise
     * @return 'true' if the folder contains at least one file, 'false' if not.
     */
    public static boolean folderContainsOneOrMoreFile(IFolder folder, boolean udfJar) {
        boolean contains = false;
        // Iterate the child resources, looking for lib folder
        if(folder!=null) {
            try {
                IResource[] folderEntries = folder.members();
                for(int j=0; j<folderEntries.length; j++) {
                    IResource folderEntry = folderEntries[j];
                    if(folderEntry instanceof IFile) {
                        if(!udfJar) {
                            contains=true;
                            break;
                        } else if(((IFile)folderEntry).getFileExtension().equalsIgnoreCase(JAR_EXT)) {
                            contains=true;
                            break;
                        }
                    }
                }
            } catch (CoreException ex) {
                ModelerCore.Util.log(IStatus.ERROR,ex,ModelerCore.Util.getString("VdbHelper.errorWithFileLookupInFolder", folder.getName())); //$NON-NLS-1$
            }
        }
        return contains;
    }
    
}
