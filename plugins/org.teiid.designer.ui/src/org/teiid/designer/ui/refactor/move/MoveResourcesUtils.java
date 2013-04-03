/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor.move;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.core.refactor.ModelResourceCollectorVisitor;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelResourceImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.UiConstants;

/**
 * Utilities for moving a resource
 */
public class MoveResourcesUtils {

private static final String PARENT_DIRECTORY = ".."; //$NON-NLS-1$
    
    /**
     * Options for types of pair to be included in calculations
     */
    public enum Option {
        /**
         * Exclude folders from collection of path pairs
         */
        EXCLUDE_FOLDERS;
    }

    private enum AttributeNames {
        MODEL_LOCATION("modelLocation"), //$NON-NLS-1$
        
        HREF("href"); //$NON-NLS-1$
        
        private final String name;
        
        private AttributeNames(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
    
    /**
     * Get i18n string value
     * 
     * @param key
     * @param parameters
     * @return
     */
    public static String getString(String key, Object...parameters) {
        return UiConstants.Util.getString(key, parameters);
    }

    /**
     * Derive a relative path from the given target file and base directory
     * 
     * @param baseDirectory
     * @param target
     * 
     * @return path of target relative to the base directory
     * 
     * @throws IOException
     */
    public static String getRelativePath(File baseDirectory, File target) throws IOException {
        File parent = baseDirectory.getParentFile();

        if (parent == null) {
            return target.getAbsolutePath();
        }

        String basePath = baseDirectory.getCanonicalPath();
        String targetPath = target.getCanonicalPath();

        if (targetPath.startsWith(basePath)) {
            return targetPath.substring(basePath.length() + 1);
        } else {
            return (PARENT_DIRECTORY + File.separator + getRelativePath(parent, target));
        }
    }
    
    private static void calculateResourceMoves(IResource resource, String destination, Collection<PathPair> pathPairs, Option...options) throws Exception {
        if (! (resource instanceof IFolder) && ! (resource instanceof IFile)) {
            // Ignore other types of resource
            return;
        }
        
        List<Option> optionList = Collections.emptyList();
        if (options != null || options.length > 0) {
            optionList = Arrays.asList(options);
        }

        String resourcePath = resource.getRawLocation().makeAbsolute().toOSString();
        
        if (resource instanceof IFolder) {
            IFolder folder = (IFolder) resource;
            
            if (! optionList.contains(Option.EXCLUDE_FOLDERS)) {
                pathPairs.add(new PathPair(resourcePath, destination + IPath.SEPARATOR + resource.getName()));
            }
            
            for (IResource subResource : folder.members(false)) {
                calculateResourceMoves(subResource, destination + IPath.SEPARATOR + folder.getName(), pathPairs);
            }
        } else {
            /*
             * resource is a file
             */
            pathPairs.add(new PathPair(resourcePath, destination + IPath.SEPARATOR + resource.getName()));
        }
    }
    
    private static void calculateResourceMoves(List<IResource> resources, String destination, Collection<PathPair> pathPairs, Option...options) throws Exception {
        for (IResource resource : resources) {
            calculateResourceMoves(resource, destination, pathPairs, options);
        }
    }
    
    /**
     * Calculates the {@link PathPair}s produced when moving the given resources
     * to the given destination.
     * 
     * <p>
     * Assumptions:
     * <ul>
     * <li>Resources are either files or directories and are absolute</li>
     * <li>Destination is an absolute path</li>
     * </ul>
     * </p>
     * 
     * @param resources
     * @param destination 
     * @param options
     * 
     * @return list of {@link PathPair}s 
     * @throws Exception 
     */
    public static Set<PathPair> calculateResourceMoves(List<IResource> resources, String destination, Option... options) throws Exception {
        Set<PathPair> pathPairs = new HashSet<PathPair>();
        calculateResourceMoves(resources, destination, pathPairs, options);
        return pathPairs;
    }

    /**
     * Calculates the set of text changes for the given file where the paths in the given {@link PathPair}s
     *  are substituted.
     *  
     * @param file
     * @param pathPairs
     * 
     * @return all the changes in the file
     * 
     * @throws Exception 
     */
    public static TextFileChange calculateTextChanges(IFile file, Collection<PathPair> pathPairs) throws Exception {
        File nativeFile = file.getRawLocation().makeAbsolute().toFile();
        if (nativeFile == null || ! nativeFile.exists())
            throw new Exception(UiConstants.Util.getString("MoveResourceUtils.fileNotFoundError", file.getFullPath())); //$NON-NLS-1$
        
        MultiTextEdit fileChangeRootEdit = new MultiTextEdit();
        
        // Convert the path pairs to relative paths based on the file
        List<PathPair> relPathPairs = new ArrayList<PathPair>();
        for (PathPair pathPair : pathPairs) {
            String sourceRelativePath = getRelativePath(nativeFile.getParentFile(), new File(pathPair.getSourcePath()));
            String targetRelativePath = getRelativePath(nativeFile.getParentFile(), new File(pathPair.getTargetPath()));
            relPathPairs.add(new PathPair(sourceRelativePath, targetRelativePath));
        }
        
        BufferedReader reader = null;
        String line;
        int docOffset = 0;
        
        try {
            reader = new BufferedReader(new FileReader(nativeFile));

            while ((line = reader.readLine()) != null) {
                for (PathPair pathPair : relPathPairs) {
                    int lineOffset = line.indexOf(pathPair.getSourcePath());
                    if (lineOffset < 0) continue;

                    int offset = docOffset + lineOffset;
                    ReplaceEdit edit = new ReplaceEdit(offset, pathPair.getSourcePath().length(), pathPair.getTargetPath());
                    fileChangeRootEdit.addChild(edit);
                }

                // Add the line length and a +1 represent the newline character
                docOffset += line.length() + 1;
            }
        } finally {
            if (reader != null)
                reader.close();
        }
        
//        // No edits required so no change for file
//        if (fileChangeRootEdit.getChildrenSize() == 0) {
//            return null;
//        }
        
        TextFileChange textFileChange = new TextFileChange(file.getName(), file);
        textFileChange.setEdit(fileChangeRootEdit);
        return textFileChange;
    }
    
    public static void unloadModelResource(IResource resource) throws CoreException {
        System.out.println("Unloading resource " + resource.getFullPath());
        
        // Collect all IResources within all IProjects
        ModelResourceCollectorVisitor visitor = new ModelResourceCollectorVisitor();
        resource.accept(visitor);
        for (Iterator iter = visitor.getModelResources().iterator(); iter.hasNext();) {
            ModelResource mResource = (ModelResource)iter.next();

            mResource.unload();
            mResource.close();
            if (mResource instanceof ModelResourceImpl) {
                ((ModelResourceImpl)mResource).removeEmfResource();
            }
        }

        // The resources move/rename will trigger the event that will actually remove and create
        // the corresponding resources, since these too are workspace management events
        // they are processed after the refactoring is done. But since we need the index files at
        // the old path to be deleted and the index files at the new path to be created,
        // we do it explicitly.

        // Delete the index files corresponding to the model resource at the old path
        for (Iterator iter = visitor.getResources().iterator(); iter.hasNext();) {
            IResource tmpResource = (IResource)iter.next();

            if (ModelUtil.isModelFile(tmpResource) && tmpResource.getLocation() != null) {
                // Remove the runtime index file associated with the resource being removed
                String runtimeIndexFileName = IndexUtil.getRuntimeIndexFileName(tmpResource);
                File runtimeIndexFile = new File(IndexUtil.INDEX_PATH, runtimeIndexFileName);
                if (!runtimeIndexFile.delete()) {
                    runtimeIndexFile.deleteOnExit();
                }

                // Remove the search index file associated with the resource being removed
                String searchIndexFileName = IndexUtil.getSearchIndexFileName(tmpResource);
                File searchIndexFile = new File(IndexUtil.INDEX_PATH, searchIndexFileName);
                if (!searchIndexFile.delete()) {
                    searchIndexFile.deleteOnExit();
                }
            }
        }
    }
}
