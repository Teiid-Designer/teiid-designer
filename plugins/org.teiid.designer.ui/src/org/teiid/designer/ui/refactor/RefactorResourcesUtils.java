/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ui.refactor;

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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.core.refactor.ModelResourceCollectorVisitor;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.core.refactor.RelatedResourceFinder;
import org.teiid.designer.core.refactor.RelatedResourceFinder.Relationship;
import org.teiid.designer.core.refactor.ResourceStatusList;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelResourceImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.ui.UiConstants;

/**
 * Utilities for moving a resource
 */
public class RefactorResourcesUtils {

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

    /**
     * Callback for individual refactoring classes to process resources/files
     * found to be related to a given resource.
     */
    public interface IRelatedResourceCallback {

        /**
         * Merge in a status with the callbacks existing status
         *
         * @param status
         */
        void mergeStatus(RefactoringStatus status);

        /**
         * Index the given related file against the resource. The type of indexing
         * is dependent on the refactoring class implementing the interface
         *
         * @param resource
         * @param relatedFile
         * @throws Exception
         */
        void indexFile(IResource resource, IFile relatedFile) throws Exception;

        /**
         * Index the given related vdb against the resource. The type of indexing
         * is dependent on the refactoring class implementing the interface
         *
         * @param resource
         * @param vdbFile
         */
        void indexVdb(IResource resource, IFile vdbFile);
    }
    
    /**
     * Get i18n string value
     * 
     * @param key
     * @param parameters
     * 
     * @return i18n string
     */
    public static String getString(String key, Object...parameters) {
        return UiConstants.Util.getString(key, parameters);
    }

    /**
     * Take an array of path components and convert them into
     * a string buffer, complete with path separators.
     *
     * @param pathArray
     * @param startingIndex
     * @param pathBuffer buffer to be populated
     */
    private static void createPathBuffer(String[] pathArray, int startingIndex, StringBuffer pathBuffer) {
        for (int i = startingIndex; i < pathArray.length; ++i) {
            pathBuffer.append(pathArray[i]);
            if ((i + 1) < pathArray.length)
                pathBuffer.append(File.separator);
        }
    }

    /**
     * Take an array of path components and convert them into
     * a string buffer, complete with path separators.
     *
     * @param pathArray
     * @param startingIndex
     *
     * @return {@link StringBuffer} of a path
     */
    private static StringBuffer createPathBuffer(String[] pathArray, int startingIndex) {
        StringBuffer pathBuffer = new StringBuffer();
        createPathBuffer(pathArray, startingIndex, pathBuffer);
        return pathBuffer;
    }

    /**
     * Derive a relative path pair from the given target absolute path pair 
     * using the given base directory
     *
     * This is just horrible but seems little option. A test is available in
     * TestRefactorResourceUtils with different use-cases. If a bug is found then
     * add to this test.
     *
     * @param baseDirectory directory to relativise against
     * @param absPair path pair containing absolute paths
     * 
     * @return path of target relative to the base directory
     * 
     * @throws IOException
     */
    static PathPair getRelativePath(String baseDirectory, PathPair absPair) throws IOException {
        String source = absPair.getSourcePath();
        String target = absPair.getTargetPath();

        String[] barr = baseDirectory.split(File.separator);
        String[] sarr = source.split(File.separator);
        String[] tarr = target.split(File.separator);

        if (barr[0].equals(sarr[0]) && sarr[0].equals(tarr[0])) {
            StringBuffer baseBuffer = createPathBuffer(barr, 1);
            StringBuffer srcBuffer = createPathBuffer(sarr, 1);
            StringBuffer tgtBuffer = createPathBuffer(tarr, 1);

            return getRelativePath(baseBuffer.toString(), new PathPair(srcBuffer.toString(), tgtBuffer.toString()));
        }

        /*
         * The relative source path
         */
        int comIndex = 0;
        StringBuffer src = new StringBuffer();
        if (sarr.length == 0 || ! barr[0].equals(sarr[0])) {
             // This will occur when the source path is a parent directory of base
            src.append(PARENT_DIRECTORY);
            src.append(File.separator);
            comIndex = 0;
        } else {
            // the first component of base and source is the same implying its target's that will be different
            comIndex = 1;
        }

        // For the number of base path components add a ../ to the src path
        for (int i = 0; i < barr.length - 1; ++i) {
            src.append(PARENT_DIRECTORY);
            src.append(File.separator);
        }

        // Append the remainder of source components to the src path subject to the value of comIndex
        createPathBuffer(sarr, comIndex, src);

        /*
         * The relative target path
         * Since the target path does not actually exist yet, we need to use the source path
         * as a base and replace components of it with those from the target path.
         */
        String tgt;

        // At this point 1 of the paths now has a different starting component

        if (sarr[0].equals(tarr[0])) { // The base has the differing 1st component

            // Find the index of the first component that differs between source and target
            int index = 0;
            while (index < sarr.length && index < tarr.length) {
                if (sarr[index].equals(tarr[index])) {
                    break;
                }
                ++index;
            }

            // index is the first index which does not match so these are the components
            StringBuffer srcBuffer = createPathBuffer(sarr, index);
            StringBuffer tgtBuffer = createPathBuffer(tarr, index);

            // Create relative target by replacing the remaining different components of
            // source with target's
            tgt = src.toString().replaceFirst(srcBuffer.toString(), tgtBuffer.toString());

        } else if (sarr[0].equals(barr[0])){    // The source and base are the same but target is not

            // Create relative target by replacing source with target
            tgt = src.toString().replaceFirst(source, target);
            
            // however if target's length is smaller than source then 
            // target has been moved up a directory so append a ../
            if (sarr.length > tarr.length) {
                tgt = PARENT_DIRECTORY + File.separator + tgt;
            }

        } else if (tarr[0].equals(barr[0])){    // target and base are the same

            // Create relative target by
            // replacing source with target
            // since target is 'in' base then remove the base path component and
            // remove the ../ from the start of the path
            tgt = src.toString().replace(source, target);
            tgt = tgt.replaceFirst(barr[0] + File.separator, ""); //$NON-NLS-1$
            tgt = tgt.replaceFirst(PARENT_DIRECTORY + File.separator, ""); //$NON-NLS-1$

        } else {    // All 3 have different starting components

            // Create relative target by directly replacing source with target
            tgt = src.toString().replaceFirst(source, target);

        }

        return new PathPair(src.toString(), tgt);
    }

    private static void calculateResourceMoves(IResource resource, String destination, Collection<PathPair> pathPairs, Option...options) throws Exception {
        if (! (resource instanceof IFolder) && ! (resource instanceof IFile)) {
            // Ignore other types of resource
            return;
        }
        
        List<Option> optionList = Collections.emptyList();
        if (options != null && options.length > 0) {
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

    private static TextEdit setRootEdit(TextFileChange textFileChange) {
        if (textFileChange.getEdit() != null)
            return textFileChange.getEdit();
        
        MultiTextEdit edit = new MultiTextEdit();
        textFileChange.setEdit(edit);
        
        return edit;
    }
    
    /**
     * Calculates the set of text changes for the given file where the paths in the given {@link PathPair}s
     *  are substituted.
     *  
     * @param file
     * @param pathPairs
     * @param textFileChange
     * 
     * @throws Exception 
     */
    public static void calculatePathChanges(IFile file, Collection<PathPair> pathPairs, TextFileChange textFileChange) throws Exception {
        File nativeFile = file.getRawLocation().makeAbsolute().toFile();
        if (nativeFile == null || ! nativeFile.exists())
            throw new Exception(getString("RefactorResourceUtils.fileNotFoundError", file.getFullPath())); //$NON-NLS-1$

        // Convert the path pairs to relative paths based on the file
        List<PathPair> relPathPairs = new ArrayList<PathPair>();
        for (PathPair pathPair : pathPairs) {
            PathPair relativePath = getRelativePath(nativeFile.getParentFile().getAbsolutePath(), pathPair);
            if (! relativePath.getSourcePath().equals(relativePath.getTargetPath())) {
                // Only if there is going to be a genuine replacement should we persist with a relative path pair
                relPathPairs.add(relativePath);
            }
        }

        TextEdit fileChangeRootEdit = setRootEdit(textFileChange);

        if (relPathPairs.isEmpty()) {
            return;
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
    }


    /**
     * Calculate changes to user-generated sql
     * 
     * @param file
     * @param pathPair
     * @param textFileChange
     * 
     * @throws Exception 
     */
    public static void calculateSQLChanges(IFile file, PathPair pathPair, TextFileChange textFileChange) throws Exception {
        if (!ModelUtil.isModelFile(file)) {
            // Only model files' names will be included in any SQL statements. Projects and folders will certainly not be.
            return;
        }
        IPath sourcePath = new Path(pathPair.getSourcePath());
        IPath targetPath = new Path(pathPair.getTargetPath());
        
        String sourceName = sourcePath.removeFileExtension().lastSegment();
        String targetName = targetPath.removeFileExtension().lastSegment();
        if (sourceName.equals(targetName))
            return;
        
        File nativeFile = file.getRawLocation().makeAbsolute().toFile();
        if (nativeFile == null || ! nativeFile.exists())
            throw new Exception(getString("MoveResourceUtils.fileNotFoundError", file.getFullPath())); //$NON-NLS-1$

        TextEdit fileChangeRootEdit = setRootEdit(textFileChange);
        BufferedReader reader = null;
        String line;
        int docOffset = 0;
        char[] prefixChars = { ' ', ',', ';', '\t', '\n' };
        
        try {
            reader = new BufferedReader(new FileReader(nativeFile));

            while ((line = reader.readLine()) != null) {
                if (line.contains("Sql=")) { //$NON-NLS-1$
                    for (char prefixChar : prefixChars) {
                        int lineOffset = line.indexOf(prefixChar + sourceName + '.');
                        if (lineOffset < 0) continue;

                        // +1 on the end taking care of the prefix character
                        int offset = docOffset + lineOffset + 1;
                        ReplaceEdit edit = new ReplaceEdit(offset, sourceName.length(), targetName);
                        fileChangeRootEdit.addChild(edit);
                    }
                }
                
                // Add the line length and a +1 represent the newline character
                docOffset += line.length() + 1;
            }
        } finally {
            if (reader != null)
                reader.close();
        }
        
    }
    
    /**
     * Unload the model resource related to the given resource
     * 
     * @param resource
     * @throws CoreException
     */
    public static void unloadModelResource(IResource resource) throws CoreException {
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

    /**
     * Return the given resources cumulative type
     *
     * @param resources
     *
     * @return int representing the cumulative type of resources
     */
    public static int getResourceTypes(List<IResource> resources) {
        if (resources == null || resources.isEmpty())
            return 0;

        int types = 0;
        for (IResource resource : resources) {
            types |= resource.getType();
        }
        return types;
    }

    /**
     * @param resources
     *
     * @return true if resources are only non-projects
     */
    public static boolean containsOnlyNonProjects(List<IResource> resources) {
        int types = getResourceTypes(resources);
        // check for empty selection
        if (types == 0) {
            return false;
        }
        return (types & IResource.PROJECT) == 0;
    }

    /**
     * @param resources
     *
     * @return true if resources are only projects
     */
    public static boolean containsOnlyProjects(List<IResource> resources) {
        int types = getResourceTypes(resources);
        return types == IResource.PROJECT;
    }

    /**
     * @param resources
     *
     * @return true is resources contain linked resource
     */
    public static boolean containsLinkedResource(List<IResource> resources) {
        if (resources == null || resources.isEmpty())
            return false;

        for (IResource resource : resources) {
            if (resource != null && resource.isLinked()) { // paranoia code, can not be null
                return true;
            }
        }
        return false;
    }

    /**
     * Check each resource in the collection as read-only and in turn check
     * all dependent resources as to whether they are read-only. Populates
     * the given status object with errors as appropriate.
     *
     * @param resources
     * @param status
     */
    public static void checkReadOnlyResources(Collection<IResource> resources, RefactoringStatus status) {
        CoreArgCheck.isNotNull(resources);
        CoreArgCheck.isNotNull(status);
        
        for (IResource resource : resources) {
            try {
                ModelResource modelResource = ModelUtil.getModel(resource);

                if (modelResource != null && modelResource.isReadOnly()) {
                    status.merge(RefactoringStatus.createFatalErrorStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyResourceError", resource.getName()))); //$NON-NLS-1$
                    return;
                }

                RelatedResourceFinder finder = new RelatedResourceFinder(resource);
                Collection<IFile> relatedFiles = finder.findRelatedResources(Relationship.ALL);

                for (IFile relatedFile : relatedFiles) {
                    try {
                        modelResource = ModelUtil.getModel(relatedFile);
                        if (modelResource != null && modelResource.isReadOnly()) {
                            status.merge(RefactoringStatus.createWarningStatus(RefactorResourcesUtils.getString("ResourcesRefactoring.readOnlyRelatedResourceError", modelResource.getItemName()))); //$NON-NLS-1$
                        }
                    } catch (ModelWorkspaceException err) {
                        ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                    }
                }
            } catch (Exception err) {
                ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
                status.merge(RefactoringStatus.createFatalErrorStatus(err.getMessage()));
                return;
            }
        }
    }

    /**
     * Calculate VDBs related to the given resource and use the given
     * callback to process them appropriately
     *
     * @param resource
     * @param callback
     */
    public static void calculateRelatedVdbResources(IResource resource, IRelatedResourceCallback callback) {
        IResource[] vdbResources = WorkspaceResourceFinderUtil.getVdbResourcesThatContain(resource);
        for (IResource vdb : vdbResources) {
            if (! (vdb instanceof IFile))
                continue;

            callback.indexVdb(resource, (IFile) vdb);
        }
    }

    /**
     * Calculate resources related to the given resource and use the given
     * callback to process them appropriately
     *
     * @param resource
     * @param callback
     */
    public static void calculateRelatedResources(IResource resource, IRelatedResourceCallback callback) {
        RelatedResourceFinder finder = new RelatedResourceFinder(resource);

        // Determine dependent resources
        Collection<IFile> searchResults = finder.findRelatedResources(Relationship.DEPENDENT);
        ResourceStatusList statusList = new ResourceStatusList(searchResults);

        for (IStatus problem : statusList.getProblems()) {
            callback.mergeStatus(RefactoringStatus.create(problem));
        }

        for (IFile file : statusList.getResourceList()) {
            try {
                callback.indexFile(resource, file);
            } catch (Exception ex) {
                UiConstants.Util.log(ex);
                callback.mergeStatus(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                return;
            }
        }

        // Determine dependencies
        searchResults = finder.findRelatedResources(Relationship.DEPENDENCY);
        statusList = new ResourceStatusList(searchResults, IStatus.OK);

        for (IStatus problem : statusList.getProblems()) {
            callback.mergeStatus(RefactoringStatus.create(problem));
        }

        for (IFile file : statusList.getResourceList()) {
            try {
                callback.indexFile(resource, file);
            } catch (Exception ex) {
                UiConstants.Util.log(ex);
                callback.mergeStatus(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                return;
            }
        }
    }
}
