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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorPart;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.core.index.IndexUtil;
import org.teiid.designer.core.refactor.IRefactorModelHandler.RefactorType;
import org.teiid.designer.core.refactor.ModelResourceCollectorVisitor;
import org.teiid.designer.core.refactor.PathPair;
import org.teiid.designer.core.refactor.RefactorModelExtensionManager;
import org.teiid.designer.core.refactor.RelatedResourceFinder;
import org.teiid.designer.core.refactor.RelatedResourceFinder.Relationship;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelResourceImpl;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.WorkspaceResourceFinderUtil;
import org.teiid.designer.ui.UiConstants;
import org.teiid.designer.ui.common.util.UiUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Utilities for moving a resource
 */
public class RefactorResourcesUtils {

    private static final String PARENT_DIRECTORY = ".."; //$NON-NLS-1$
    private static final String MODEL_IMPORTS_ELEMENT_START = "<modelImports "; //$NON-NLS-1$
    private static final String SQL_STATEMENT_START = "Sql="; //$NON-NLS-1$
    
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
    public interface IResourceCallback {

        /**
         * Check that the given related file is valid according to the rules of
         * the refactoring taking place.
         *
         * Populates the given status with any problems.
         *
         * @param relatedFile
         * @param status
         */
        void checkValidFile(IFile relatedFile, RefactoringStatus status);

        /**
         * Index the given related file against the resource. The type of indexing
         * is dependent on the refactoring class implementing the interface
         *
         * Populates the given status with any problems.
         *
         * @param resource
         * @param relatedFile
         * @param status
         *
         * @throws Exception
         */
        void indexFile(IResource resource, IFile relatedFile, RefactoringStatus status) throws Exception;

        /**
         * Index the given related vdb against the resource. The type of indexing
         * is dependent on the refactoring class implementing the interface
         *
         * Populates the given status with any problems.
         *
         * @param resource
         * @param vdbFile
         * @param status
         */
        void indexVdb(IResource resource, IFile vdbFile, RefactoringStatus status);
    }

    /**
     * Abstract implementation of {@link IResourceCallback}
     */
    public abstract static class AbstractResourceCallback implements IResourceCallback {

        @Override
        public void checkValidFile(IFile relatedFile, RefactoringStatus status) {
            // do nothing
        }

        @Override
        public void indexFile(IResource resource, IFile relatedFile, RefactoringStatus status) throws Exception {
            // do nothing
        }

        @Override
        public void indexVdb(IResource resource, IFile vdbFile, RefactoringStatus status) {
            // do nothing
        }
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
            if (pathArray[i].length() == 0)
                continue;

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
     * Append the given number of parent symbols ("..") to the given buffer.
     *
     * @param buffer to append to
     * @param numder of parent symbols to append
     */
    private static void appendParentSymbol(StringBuffer buffer, int number) {
        for (int i = 0; i < number; ++i) {
            buffer.append(PARENT_DIRECTORY);
            buffer.append(File.separator);
        }
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
    public static PathPair getRelativePath(String baseDirectory, PathPair absPair) throws IOException {
        String source = absPair.getSourcePath();
        String target = absPair.getTargetPath();

        String separator = java.util.regex.Pattern.quote(File.separator);
        String[] barr = baseDirectory.split(separator);
        String[] sarr = source.split(separator);
        String[] tarr = target.split(separator);

        if (barr[0].equals(sarr[0]) && sarr[0].equals(tarr[0])) {
            StringBuffer baseBuffer = createPathBuffer(barr, 1);
            StringBuffer srcBuffer = createPathBuffer(sarr, 1);
            StringBuffer tgtBuffer = createPathBuffer(tarr, 1);

            return getRelativePath(baseBuffer.toString(), new PathPair(srcBuffer.toString(), tgtBuffer.toString()));
        }

        /*
         * The common pieces of the paths have been removed so only the
         * different components remain.
         */
        boolean RENAME = false;

        /*
         * Determine whether this is a rename by analysing the
         * remaining components of sarr and tarr.
         *
         * Only if the lengths of sarr and tarr match and the last component in
         * tarr differs from sarr then it could be a rename.
         */
        if (sarr.length == tarr.length && ! sarr[sarr.length - 1].equals(tarr[tarr.length - 1])) {
            // Need to analyse whether components other than the last
            // are different. If other components differ as well then its a move
            // and not a rename
            RENAME = true;
            for (int i = 0; i < (sarr.length - 1); ++i) {
                if (! sarr[i].equals(tarr[i])) {
                    RENAME = false;
                    break;
                }
            }
        }

        StringBuffer src = new StringBuffer();
        StringBuffer tgt = new StringBuffer();

        if (baseDirectory.length() == 0) {
            /* both source and target are inside base directory */

            // Append the remainder of source components to the src and target paths
            createPathBuffer(sarr, 0, src);
            createPathBuffer(tarr, 0, tgt);
        }
        else if (barr[0].equals(sarr[0])) {
            /* source is below base but target has now branched off */

            appendParentSymbol(src, barr.length - 1);
            createPathBuffer(sarr, 1, src);
            
            if (RENAME) {
                appendParentSymbol(tgt, barr.length - 1);
                createPathBuffer(tarr, 1, tgt);
            }
            else {
                appendParentSymbol(tgt, barr.length);
                createPathBuffer(tarr, 0, tgt);
            }
        }
        else if (barr[0].equals(tarr[0])) {
            /* target is below base but source has now branched off */

            appendParentSymbol(src, barr.length);
            createPathBuffer(sarr, 0, src);

            if (RENAME) {
                appendParentSymbol(tgt, barr.length);
                createPathBuffer(tarr, 1, tgt);
            }
            else {
                appendParentSymbol(tgt, barr.length - 1);
                createPathBuffer(tarr, 1, tgt);
            }
        } else {
            /* both source and target have branched off from base */

            appendParentSymbol(src, barr.length);
            createPathBuffer(sarr, 0, src);

            appendParentSymbol(tgt, barr.length);
            createPathBuffer(tarr, 0, tgt);
        }

        return new PathPair(src.toString(), tgt.toString());
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

        String resourcePath = ModelUtil.getLocation(resource).makeAbsolute().toOSString();
        
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
     * Calculates the absolute {@link PathPair}s produced when moving the given resources
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
     * @return list of absolute {@link PathPair}s
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
     * Calculates a {@link TextFileChange} for all the substitutions that need to take place in the
     * given file based on the collection of path pairs.
     *
     * @param file
     * @param pathPairs
     * @return text file change
     *
     * @throws Exception
     */
    public static TextFileChange calculateTextChanges(IFile file, Collection<PathPair> pathPairs) throws Exception {
        File nativeFile = ModelUtil.getLocation(file).makeAbsolute().toFile();
        if (nativeFile == null || ! nativeFile.exists())
            throw new Exception(getString("ResourcesRefactoring.fileNotFoundError", file.getFullPath())); //$NON-NLS-1$

        TextFileChange textFileChange = new TextFileChange(file.getName(), file);
        TextEdit fileChangeRootEdit = setRootEdit(textFileChange);

        if (pathPairs.isEmpty()) {
            return textFileChange;
        }

        BufferedReader reader = null;
        String line;
        int docOffset = 0;
        
        try {
            reader = new BufferedReader(new FileReader(nativeFile));

			while ((line = reader.readLine()) != null) {
				if (!line.contains(MODEL_IMPORTS_ELEMENT_START)) {
					for (PathPair pathPair : pathPairs) {
						if (pathPair.getSourcePath().equals(pathPair.getTargetPath())) {
							/*
							 * Absolutely nothing to do since the replacement is
							 * the same as the change
							 */
							continue;
						}
						
						String sourcePath = pathPair.getSourcePath().replace('\\','/'); // TEIIDDES-2434 - Ensure srcPath separators agree with xmi line
						boolean sourcePathHasSlash = sourcePath.indexOf('/') > -1;

						int lineOffset = line.indexOf(sourcePath);
						if (lineOffset < 0) continue;

						if( sourcePathHasSlash || line.charAt(lineOffset-1) == '"') {
							int offset = docOffset + lineOffset;
							ReplaceEdit edit = new ReplaceEdit(offset, pathPair.getSourcePath().length(), pathPair.getTargetPath().replace('\\','/'));
							fileChangeRootEdit.addChild(edit);
						}
					}
				}

				// Add the line length and a +1 represent the newline character
				docOffset += line.length() + 1;
                if( Platform.getOS().equalsIgnoreCase(Platform.WS_WIN32) ) {
                	docOffset++;
                }
			}
        } finally {
            if (reader != null)
                reader.close();
        }

        return textFileChange;
    }

    /**
     * Finds the import locations in the given file and calculates the modified paths against the 
     * given destination. If an import location points to a resource in the given set then nothing
     * should be done since that resource is also being moved to the destination and no change
     * is necessary.
     *
     * @param file the file to be refactored
     * @param destination the location the file is to be refactored to
     * @param refactorResources the collection of all resources being refactored
     *
     * @return a set of path pairs representing the import locations
     *
     * @throws Exception
     */
    public static Set<PathPair> calculateImportChanges(IFile file, String destination, Set<IResource> refactorResources) throws Exception {
        File nativeFile = ModelUtil.getLocation(file).makeAbsolute().toFile();
        if (nativeFile == null || ! nativeFile.exists())
            throw new Exception(getString("ResourcesRefactoring.fileNotFoundError", file.getFullPath())); //$NON-NLS-1$

        Set<String> refactorResourcePaths = new HashSet<String>();
        for (IResource resource : refactorResources) {
            File nativeRes = ModelUtil.getLocation(resource).makeAbsolute().toFile();
            refactorResourcePaths.add(nativeRes.getCanonicalPath());
        }

        Set<PathPair> importPairs = new HashSet<PathPair>();
        File parentFolder = nativeFile.getParentFile();

        // Find the imports in the file and return a collection of relative resource paths
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document xmlDocument = dBuilder.parse(nativeFile);

        NodeList modelList = xmlDocument.getElementsByTagName("modelImports"); //$NON-NLS-1$
        for (int i = 0; i < modelList.getLength(); ++i) {
            Node modelNode = modelList.item(i);
            if (modelNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element element = (Element) modelNode;
            if (!element.hasAttribute("modelLocation")) //$NON-NLS-1$
                continue;

            String relativeLocation = element.getAttribute("modelLocation"); //$NON-NLS-1$
            if (relativeLocation.startsWith("http:") || relativeLocation.startsWith("https:")) { //$NON-NLS-1$ //$NON-NLS-2$
                continue;
            }

            // Find the absolute path of the model location based on the location of the file
            File absLocationFile = new File(parentFolder, relativeLocation);
            String absLocation = absLocationFile.getCanonicalPath();
            if (refactorResourcePaths.contains(absLocation))
                continue;

            // Use the new proposed location of the file to extrapolate the relative path of the
            // import location. This takes advantage of the getRelativePath function by adding
            // the absLocation to the source and target of a path pair. This is for convenience.

            // Original model location   -> ../sources/sourcemodel.xmi
            // Absolute model location  -> /home/test1/programming/java/td-projects/parts/sources/sourcemodel.xmi
            // /home/test1/programming/java/td-projects/parts                 -> sources/sourcemodel.xmi
            // /home/test1/programming/java/td-projects/parts/test          -> ../sources/sourcemodel.xmi
            // /home/test1/programming/java/td-projects/parts/sources    -> sourcemodel.xmi
            PathPair newRelativePair = getRelativePath(destination, new PathPair(absLocation, absLocation));
            String newRelativeLocation = newRelativePair.getSourcePath();

            if (! relativeLocation.equals(newRelativeLocation)) {
                // Only if the import location must change do we need to include it
                importPairs.add(new PathPair(relativeLocation, newRelativeLocation));
            }
        }

        return importPairs;
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
        
        File nativeFile = ModelUtil.getLocation(file).makeAbsolute().toFile();
        if (nativeFile == null || ! nativeFile.exists())
            throw new Exception(getString("ResourcesRefactoring.fileNotFoundError", file.getFullPath())); //$NON-NLS-1$

        TextEdit fileChangeRootEdit = setRootEdit(textFileChange);
        BufferedReader reader = null;
        String line;
        int docOffset = 0;
        char[] prefixChars = { ' ', ',', ';', '\t', '\n', '(' };
        
        try {
            reader = new BufferedReader(new FileReader(nativeFile));

            while ((line = reader.readLine()) != null) {
                if (line.contains(SQL_STATEMENT_START)) { //$NON-NLS-1$
                    for (char prefixChar : prefixChars) {
                        String toReplace = prefixChar + sourceName + '.';

                        for (int lineOffset = line.indexOf(toReplace); lineOffset >= 0; lineOffset = line.indexOf(toReplace, lineOffset + 1)) {
                            if (lineOffset < 0) continue;

                            // +1 on the end taking care of the prefix character
                            int offset = docOffset + lineOffset + 1;
                            ReplaceEdit edit = new ReplaceEdit(offset, sourceName.length(), targetName);
                            fileChangeRootEdit.addChild(edit);
                        }
                    }
                }
                
                // Add the line length and a +1 represent the newline character
                docOffset += line.length() + 1;
                if( Platform.getOS().equalsIgnoreCase(Platform.WS_WIN32) ) {
                	docOffset++;
                }
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
    public static void calculateModelImportsElementLChanges(IFile file, PathPair pathPair, TextFileChange textFileChange) throws Exception {
        File nativeFile = ModelUtil.getLocation(file).makeAbsolute().toFile();
        if (nativeFile == null || ! nativeFile.exists())
            throw new Exception(getString("ResourcesRefactoring.fileNotFoundError", file.getFullPath())); //$NON-NLS-1$

        TextEdit fileChangeRootEdit = setRootEdit(textFileChange);
        boolean isWindows = Platform.getOS().equals(Platform.WS_WIN32);

        BufferedReader reader = null;
        String line;
        int docOffset = 0;
        
        try {
            reader = new BufferedReader(new FileReader(nativeFile));

            while ((line = reader.readLine()) != null) {
                if (line.contains(MODEL_IMPORTS_ELEMENT_START)) { //$NON-NLS-1$
                	// Let's replace entire line
                	StringBuilder sb = new StringBuilder(line);
                	
                	// Assuming it's amodel import, we need to 1) Check for path changes and create an edit
                	// 2) if (1) is performed, the create a second edit to replace the model name
					if (pathPair.getSourcePath().equals(pathPair.getTargetPath())) {
						/*
						 * Absolutely nothing to do since the replacement is
						 * the same as the change
						 */
						// Add the line length and a +1 represent the newline character
		                docOffset += line.length() + 1;
						continue;
					}
					
					// 	name="partssupplier" modelLocation="partssupplier.xmi"
				    //	name="partssupplier_view_2" modelLocation="views/partssupplier_view_2.xmi"
					//  HAVE TO PREVENT THE FOLLOWING
				    //	name="partssupplier_RENAMED" modelLocation="partssupplier_RENAMED.xmi"
				    //	name="partssupplier_RENAMED_view_2" modelLocation="views/partssupplier_view_2.xmi"

					// Note that a file renaming a file named "partssupplier.xmi" will end up modifying "my_partssupplier.xmi"
					// Need to add a check to see that the index-1 is a " double-quote character
					
					// Note that a file renaming a file named "partssupplier.xmi" will end up modifying "partssupplier_otherfile.xmi"
					// Need to add a check to see that the index + path/name length is a " double-quote character
					
					// Model location may not contain any '/' chars, so the indexOf(sourcePath) will look the same as 
					// EXAMPLE :  name="views_products" modelLocation="views_products.xmi"
					// where products.xmi is being renamed to products_RENAMED.xmi
					
					String sourcePath = pathPair.getSourcePath().replace('\\','/'); // TEIIDDES-2434 - Ensure srcPath separators agree with xmi line
					String sourceNameOnly = pathPair.getSourceNameNoExtension();
					boolean sourcePathHasSlash = sourcePath.indexOf('/') > -1;
					
					int lineOffset = line.indexOf(sourcePath);
					if (lineOffset > 0 ) {
						// Check for use-case where there is no slash in path and source name is the suffix of a different model
						// EXAMPLE products.xmi  is in the modelLocation="view_products.xmi"
						// If slash is in path, then it won't get here because of full path check
						if( sourcePathHasSlash || line.charAt(lineOffset-1) == '"') {
							int offset = docOffset + lineOffset;
							ReplaceEdit edit = new ReplaceEdit(offset, pathPair.getSourcePath().length(), pathPair.getTargetPath().replace('\\','/'));
							fileChangeRootEdit.addChild(edit);
						}
					}
					
					lineOffset = line.indexOf(sourceNameOnly);
					if (lineOffset > 0 && line.charAt(lineOffset-1) == '"' && line.charAt(lineOffset + sourceNameOnly.length()) == '"')  {
						int offset = docOffset + lineOffset;
						ReplaceEdit edit = new ReplaceEdit(offset, pathPair.getSourceNameNoExtension().length(), pathPair.getTargetNameNoExtension());
						fileChangeRootEdit.addChild(edit);
					}
                }
                
                // Add the line length and a +1 represent the newline character
                docOffset += line.length() + 1;
                if( isWindows ) {
                	docOffset++;
                }
            }
        } finally {
            if (reader != null)
                reader.close();
        }
        
    }

    /**
     * Is the given resource a closed project
     *
     * @param resource
     * @return true if the resource is a closed project
     */
    public static boolean isClosedProject(IResource resource) {
        if (resource instanceof IProject && !((IProject) resource).isOpen())
            return true;

        return false;
    }

    /**
     * Unload the model resource related to the given resource
     * 
     * @param resource
     * @throws CoreException
     */
    public static void unloadModelResource(IResource resource) throws CoreException {
        if (isClosedProject(resource)) {
            /*
             * A closed project will not have any resources to unload
             */
            return;
        }

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

    private static void checkSavedFile(IFile file, RefactoringStatus status) {
        if (!file.exists()) {
            status.merge(RefactoringStatus.createFatalErrorStatus(getString("ResourcesRefactoring.resourceNoExistError", file.getName()))); //$NON-NLS-1$
            return;
        }

        IEditorPart fileEditor = UiUtil.getEditorForFile(file, false);
        if (fileEditor != null && fileEditor.isDirty()) {
            status.addFatalError(getString("ResourcesRefactoring.unsavedFile", file.getFullPath())); //$NON-NLS-1$
            return;
        }

        ITextFileBuffer buffer= FileBuffers.getTextFileBufferManager().getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
        if (buffer != null && buffer.isDirty()) {
            if (buffer.isStateValidated() && buffer.isSynchronized()) {
                status.addWarning(getString("ResourcesRefactoring.unsavedFile", file.getFullPath())); //$NON-NLS-1$
            } else {
                status.addFatalError(getString("ResourcesRefactoring.unsavedFile", file.getFullPath())); //$NON-NLS-1$
            }
        }
    }

    /**
     * Check if the given resource has unsaved content.
     *
     * Populates the given status object accordingly.
     *
     * @param resource
     * @param status
     *
     */
    public static void checkSavedResource(IResource resource, final RefactoringStatus status) {
        if (isClosedProject(resource)) {
            /*
             * All resources in a closed project must already be saved
             */
            return;
        }

        try {
            resource.accept(new IResourceVisitor() {
                @Override
                public boolean visit(IResource visitedResource) {
                    if (visitedResource instanceof IFile) {
                        checkSavedFile((IFile)visitedResource, status);
                    }
                    return true;
                }
            }, IResource.DEPTH_INFINITE, false);

        } catch (CoreException ex) {
            status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
        }
    }

    /**
     * Check if the given file has an open editor.
     *
     * @param file
     * @param status
     */
    private static void checkOpenEditors(IFile file, RefactoringStatus status) {
        if (!file.exists()) {
            status.merge(RefactoringStatus.createFatalErrorStatus(getString("ResourcesRefactoring.resourceNoExistError", file.getName()))); //$NON-NLS-1$
            return;
        }

        IEditorPart fileEditor = UiUtil.getEditorForFile(file, false);
        if (fileEditor != null) {
            status.addFatalError(getString("ResourcesRefactoring.openEditorError", file.getFullPath())); //$NON-NLS-1$
            return;
        }
    }

    /**
     * Check if the given resource has open editors.
     *
     * Populates the given status object accordingly.
     *
     * @param resource
     * @param status
     *
     */
    public static void checkOpenEditors(IResource resource, final RefactoringStatus status) {
        if (isClosedProject(resource)) {
            /*
             * A closed project cannot have any open editors since its not open!
             */
            return;
        }

        try {
            resource.accept(new IResourceVisitor() {
                @Override
                public boolean visit(IResource visitedResource) {
                    if (visitedResource instanceof IFile) {
                        checkOpenEditors((IFile) visitedResource, status);
                    }
                    return true;
                }
            }, IResource.DEPTH_INFINITE, false);

        } catch (CoreException ex) {
            status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
        }
    }

    /**
     * Check the given resource exists.
     *
     * Populates the given status object accordingly.
     *
     * @param resource
     * @param status
     *
     */
    public static void checkResourceExists(IResource resource, RefactoringStatus status) {
        if (!resource.exists()) {
            status.merge(RefactoringStatus.createFatalErrorStatus(getString("ResourcesRefactoring.resourceNoExistError", resource.getName()))); //$NON-NLS-1$
        }
    }

    /**
     * Check the given resource is synchronized.
     *
     * Populates the given status object accordingly.
     *
     * @param resource
     * @param status
     *
     */
    public static void checkResourceSynched(IResource resource, RefactoringStatus status) {
        if (!resource.isSynchronized(IResource.DEPTH_INFINITE)) {
            status.merge(RefactoringStatus.createFatalErrorStatus(getString("ResourcesRefactoring.warningOutOfSync", resource.getFullPath()))); //$NON-NLS-1$
        }
    }

    /**
     * Check the given resource is writable
     *
     * Populates the given status object accordingly.
     *
     * @param resource
     * @param status
     * @param statusLevel
     * @param statusMsg
     *
     */
    public static void checkResourceWritable(IResource resource, RefactoringStatus status, int statusLevel, String statusMsg) {
        if (ModelUtil.isIResourceReadOnly(resource)) {
            switch (statusLevel) {
                case IStatus.INFO:
                    status.merge(RefactoringStatus.createInfoStatus(statusMsg));
                    break;
                case IStatus.WARNING:
                    status.merge(RefactoringStatus.createWarningStatus(statusMsg));
                    break;
                default:
                    status.merge(RefactoringStatus.createFatalErrorStatus(statusMsg));
            }
        }
    }

    /**
     * Checks whether the given resource's related {@link ModelResource} and its
     * related resources are writable.
     *
     * Populates the given status object accordingly.
     *
     * @param resource
     * @param status
     * @param statusLevel
     * @param statusMsg
     *
     */
    public static void checkModelResourceWritable(IResource resource, RefactoringStatus status, int statusLevel, String statusMsg) {
        try {
            ModelResource modelResource = ModelUtil.getModel(resource);
            if (modelResource == null) {
                // Test does not apply since this resource is not a model
                return;
            }

            if (modelResource.isReadOnly()) {
                switch (statusLevel) {
                    case IStatus.INFO:
                        status.merge(RefactoringStatus.createInfoStatus(statusMsg));
                        break;
                    case IStatus.WARNING:
                        status.merge(RefactoringStatus.createWarningStatus(statusMsg));
                        break;
                    default:
                        status.merge(RefactoringStatus.createFatalErrorStatus(statusMsg));
                }
            }
        } catch (Exception err) {
            ModelerCore.Util.log(IStatus.ERROR, err, err.getMessage());
            status.merge(RefactoringStatus.createFatalErrorStatus(err.getMessage()));
        }
    }

    /**
     * Check the given resource is not a project
     *
     * Populates the given status object accordingly.
     *
     * @param resource
     * @param status
     *
     */
    public static void checkResourceIsNotProject(IResource resource, RefactoringStatus status) {
        if (resource instanceof IProject) {
            status.merge(RefactoringStatus.createFatalErrorStatus(getString("ResourcesRefactoring.refactorProjectError", resource.getName()))); //$NON-NLS-1$
        }
    }

    /**
     * Check the {@link RefactorModelExtensionManager#preProcess(RefactorType, IResource, IProgressMonitor)}
     * for problems.
     *
     * Populates the given status object accordingly.
     *
     * @param resource
     * @param refactorType
     * @param progressMonitor
     * @param status
     */
    public static void checkExtensionManager(IResource resource, RefactorType refactorType, IProgressMonitor progressMonitor, RefactoringStatus status) {
        if (! RefactorModelExtensionManager.preProcess(refactorType, resource, progressMonitor)) {
            status.merge(RefactoringStatus.createFatalErrorStatus(getString("ResourcesRefactoring.extensionManagerError"))); //$NON-NLS-1$
        }
    }

    /**
     * Calculate VDBs related to the given resource and use the given
     * callback to process them appropriately
     *
     * @param resource
     * @param status
     * @param callback
     */
    public static void calculateRelatedVdbResources(IResource resource, RefactoringStatus status, IResourceCallback callback) {
        Collection<IFile> vdbResources = WorkspaceResourceFinderUtil.getVdbResourcesThatContain(resource);
        for (IFile vdb : vdbResources) {

            callback.checkValidFile(vdb, status);
            if (! status.isOK()) {
                return;
            }

            callback.indexVdb(resource, vdb, status);
        }
    }

    /**
     * Calculate resources related to the given resource and use the given
     * callback to process them appropriately
     *
     * @param resource
     * @param status
     * @param callback
     * @param relationship one of {@link Relationship}
     */
    public static void calculateRelatedResources(IResource resource, RefactoringStatus status,
                                                                             IResourceCallback callback, Relationship relationship) {
        RelatedResourceFinder finder = new RelatedResourceFinder(resource);

        // Determine dependent resources
        Collection<IFile> searchResults = finder.findRelatedResources(relationship);
        if (searchResults == null)
            return;

        for (IFile file : searchResults) {
            try {
                callback.checkValidFile(file, status);
                if (status.getSeverity() > IStatus.WARNING) {
                    return;
                }

                callback.indexFile(resource, file, status);
            } catch (Exception ex) {
                UiConstants.Util.log(ex);
                status.merge(RefactoringStatus.createFatalErrorStatus(ex.getMessage()));
                return;
            }
        }
    }
}
