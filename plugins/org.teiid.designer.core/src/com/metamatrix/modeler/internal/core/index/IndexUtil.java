/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.File;
import java.util.Collection;
import java.util.zip.CRC32;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.api.exception.query.QueryMetadataException;
import com.metamatrix.core.MetaMatrixCoreException;
import com.metamatrix.core.index.IEntryResult;
import com.metamatrix.core.index.IIndex;
import com.metamatrix.core.index.IIndexer;
import com.metamatrix.core.index.SimpleIndexUtil;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;

/**
 * IndexUtil
 */
public class IndexUtil {

    private static final class ProgressMonitorImpl implements SimpleIndexUtil.ProgressMonitor {
        private final IProgressMonitor monitor;

        ProgressMonitorImpl( IProgressMonitor monitor ) {
            this.monitor = monitor;
        }

        public void beginTask( String name,
                               int totalWork ) {
            monitor.beginTask(name, totalWork);
        }

        public void worked( int work ) {
            monitor.worked(work);
        }
    }

    public static final boolean CASE_SENSITIVE_INDEX_FILE_NAMES = false;

    private static final CRC32 CHECK_SUM_CALCULATOR = new CRC32();

    public static String INDEX_PATH;
    static {
        try {
            final Plugin plugin = ModelerCore.getPlugin();
            // If we are running in the Eclipse runtime environment use the modeler.core
            // plugin's state area as the location for writing the index files
            if (plugin != null) {
                INDEX_PATH = plugin.getStateLocation().addTrailingSeparator().toString();
            } else {
                INDEX_PATH = System.getProperty("user.dir") + "\\indexes"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (Throwable t) {
            INDEX_PATH = System.getProperty("user.dir") + "\\indexes"; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Adds/removes an IResource to an Index if the resource exists, if the resource does not exist, the resource is removed from
     * the index.
     * 
     * @param resource The IResource to be added or removed from the index
     * @param reuseExistingFile Boolean indicating if the existing index file needs to be used for indexing this resource.
     * @param addResource Boolean indicating if this resource needs to be added or removed from the index file.
     */
    public static void indexResource( IResource resource,
                                      boolean reuseExistingFile,
                                      boolean addResource ) throws ModelerCoreException {
        ModelWorkspace workspace = ModelerCore.getModelWorkspace();
        ModelResource mResource = workspace.findModelResource(resource);
        IIndexer indexer = new ModelIndexer();
        indexResource(indexer, mResource, INDEX_PATH, reuseExistingFile, addResource);
    }

    /**
     * Create an {@link com.metamatrix.internal.core.index.IIndex} from the {@link org.eclipse.emf.ecore.resource.Resource} if the
     * resource exists.
     * 
     * @param resource The EMF resource to use
     * @param resourcePath The path to the EMF resource
     * @param indexDirectoryPath The path to the directory into which the index file is written
     * @param indexFileName The name of the output index file
     * @param indexPath The path needs to be used for indexing this resource.
     * @return the index
     */
    public static IIndex indexResource( final Resource resource,
                                        final String resourcePath,
                                        final String indexDirectoryPath,
                                        final String indexFileName ) throws ModelerCoreException {
        if (resource == null) {
            CoreArgCheck.isNotNull(resource, ModelerCore.Util.getString("IndexUtil.The_Resource_reference_may_not_be_null_1")); //$NON-NLS-1$
        }

        try {
            IIndexer indexer = new ModelIndexer();
            // runtime index
            String indexFilePath = getIndexFilePath(indexDirectoryPath, indexFileName);
            // IIndex runtimeIndex = new Index(indexFilePath, false);
            IIndex runtimeIndex = getNewIndexFile(indexFileName, indexFilePath, resource.getURI().lastSegment());

            // emf document
            ModelDocumentImpl document = new ModelDocumentImpl(resource);

            runtimeIndex.add(document, indexer);
            runtimeIndex.save();

            return runtimeIndex;

        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, IStatus.OK,
                                        ModelerCore.Util.getString("ModelBuilder.IO_Error_trying_to_index_an_EmfResource_2"), e); //$NON-NLS-1$
            throw new ModelerCoreException(status);
        }

    }

    /**
     * Adds/removes an EmfResource to an Index if the resource exists, if the resource does not exist, the resource is removed
     * from the index.
     * 
     * @param emfresource The EmfResource to be added or removed from the index
     * @param reuseExistingFile Boolean indicating if the existing index file needs to be used for indexing this resource.
     * @param addResource Boolean indicating if this resource needs to be added or removed from the index file.
     */
    public static void indexResource( EmfResource emfresource,
                                      boolean reuseExistingFile,
                                      boolean addResource ) throws ModelerCoreException {

        IIndexer indexer = new ModelIndexer();
        ModelResource mResource = ModelWorkspaceManager.getModelWorkspaceManager().findModelResource(emfresource);
        indexResource(indexer, mResource, INDEX_PATH, reuseExistingFile, addResource);
    }

    /**
     * Adds/removes an ModelResource to an Index if the resource exists, if the resource does not exist, the resource is removed
     * from the index.
     * 
     * @param mResource The ModelResource to be added or removed from the index
     * @param reuseExistingFile Boolean indicating if the existing index file needs to be used for indexing this resource.
     * @param addResource Boolean indicating if this resource needs to be added or removed from the index file.
     */
    public static void indexResource( ModelResource mResource,
                                      boolean reuseExistingFile,
                                      boolean addResource ) throws ModelerCoreException {

        IIndexer indexer = new ModelIndexer();
        indexResource(indexer, mResource, INDEX_PATH, reuseExistingFile, addResource);
    }

    /**
     * Adds/removes an ModelResource to/form an Index if the resource exists, if the resource does not exist, the resource is
     * removed from the index.
     * 
     * @param mResource The ModelResource to be added or removed from the index
     * @param indexFile Path to the file containing indexes
     * @param reuseExistingFile Boolean indicating if the existing index file needs to be used for indexing this resource.
     * @param addResource Boolean indicating if this resource needs to be added or removed from the index file.
     */
    public static void indexResource( IIndexer indexer,
                                      ModelResource mResource,
                                      String indexPath,
                                      boolean reuseExistingFile,
                                      boolean addResource ) throws ModelerCoreException {

        CoreArgCheck.isNotNull(mResource, ModelerCore.Util.getString("IndexUtil.The_Resource_reference_may_not_be_null_1")); //$NON-NLS-1$

        try {
            String resourceFileName = mResource.getEmfResource().getURI().lastSegment();
            // runtime index
            String indexFileName = getRuntimeIndexFileName(mResource);
            // runtime index
            String indexFilePath = getIndexFilePath(indexPath, indexFileName);
            // IIndex runtimeIndex = new Index(indexFilePath, reuseExistingFile);
            IIndex runtimeIndex = getNewIndexFile(indexFileName, indexFilePath, resourceFileName);

            IResource resource = mResource.getResource();
            String resourcePath = resource.getFullPath().toString();
            resourcePath = resourcePath.replace(IPath.SEPARATOR, File.separatorChar);
            if (ModelerCore.DEBUG || ModelerCore.DEBUG_PROJECT_BUILDER) {
                final Object[] params = new Object[] {indexFileName, resourcePath};
                final String msg = ModelerCore.Util.getString("IndexUtil.DEBUG.Creating_index_file_0_for_resource_1_1", params); //$NON-NLS-1$
                ModelerCore.Util.log(IStatus.INFO, msg);
            }

            // emf document
            ModelDocumentImpl document = new ModelDocumentImpl(resource, mResource.getEmfResource());

            if (addResource) {
                runtimeIndex.add(document, indexer);
            } else {
                runtimeIndex.remove(document.getName());
            }
            runtimeIndex.save();

            // this is a new resource, mark it as indexed
            if (indexer instanceof ModelIndexer) mResource.setIndexType(ModelResource.INDEXED);
            if (indexer instanceof ModelSearchIndexer && mResource.getIndexType() == ModelResource.NOT_INDEXED) mResource.setIndexType(ModelResource.SEARCH_INDEXED);
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, ModelerCore.PLUGIN_ID, IStatus.OK,
                                        ModelerCore.Util.getString("ModelBuilder.IO_Error_trying_to_index_an_EmfResource_2"), e); //$NON-NLS-1$
            throw new ModelerCoreException(status);
        }
    }

    public static String getIndexFilePath( final String indexDirectoryPath,
                                           final String indexFileName ) {
        return SimpleIndexUtil.getIndexFilePath(indexDirectoryPath, indexFileName);
    }

    /**
     * Return all index file records that match the specified record pattern. The pattern can be constructed from any combination
     * of characters including the multiple character wildcard '*' and single character wildcard '?'. The field delimiter is used
     * to tokenize both the pattern and the index record so that individual fields can be matched. The method assumes that the
     * first occurrence of the delimiter in the record alligns with the first occurrence in the pattern. Any wildcard characters
     * in the pattern cannot represent a delimiter character.
     * 
     * @param indexes the array of MtkIndex instances to query
     * @param pattern
     * @param fieldDelimiter
     * @return results
     * @throws QueryMetadataException
     */
    public static IEntryResult[] queryIndex( final Index[] indexes,
                                             final char[] pattern,
                                             final char fieldDelimiter ) throws ModelerCoreException {
        try {
            return SimpleIndexUtil.queryIndex(indexes, pattern, fieldDelimiter);
        } catch (MetaMatrixCoreException e) {
            throw new ModelerCoreException(e);
        }
    }

    /**
     * Return all index file records that match the specified record prefix or pattern. The pattern can be constructed from any
     * combination of characters including the multiple character wildcard '*' and single character wildcard '?'. The prefix may
     * be constructed from any combination of characters excluding the wildcard characters. The prefix specifies a fixed number of
     * characters that the index record must start with.
     * 
     * @param indexes the array of MtkIndex instances to query
     * @param pattern
     * @return results
     * @throws ModelerCoreException
     */
    public static IEntryResult[] queryIndex( final Index[] indexes,
                                             final char[] pattern,
                                             final boolean isPrefix,
                                             final boolean returnFirstMatch ) throws ModelerCoreException {
        try {
            return SimpleIndexUtil.queryIndex(indexes, pattern, isPrefix, returnFirstMatch);
        } catch (MetaMatrixCoreException e) {
            throw new ModelerCoreException(e);
        }
    }

    /**
     * Return all index file records that match the specified record prefix or pattern. The pattern can be constructed from any
     * combination of characters including the multiple character wildcard '*' and single character wildcard '?'. The prefix may
     * be constructed from any combination of characters excluding the wildcard characters. The prefix specifies a fixed number of
     * characters that the index record must start with.
     * 
     * @param monitor an optional ProgressMonitor
     * @param indexes the array of MtkIndex instances to query
     * @param pattern
     * @return results
     * @throws ModelerCoreException
     */
    public static IEntryResult[] queryIndex( final IProgressMonitor monitor,
                                             final Index[] indexes,
                                             final char[] pattern,
                                             final boolean isPrefix,
                                             final boolean returnFirstMatch ) throws ModelerCoreException {
        try {
            return SimpleIndexUtil.queryIndex(monitor != null ? new ProgressMonitorImpl(monitor) : null,
                                              indexes,
                                              pattern,
                                              isPrefix,
                                              returnFirstMatch);
        } catch (MetaMatrixCoreException e) {
            throw new ModelerCoreException(e);
        }
    }

    /**
     * Return all index file records that match the specified record prefix or pattern. The pattern can be constructed from any
     * combination of characters including the multiple character wildcard '*' and single character wildcard '?'. The prefix may
     * be constructed from any combination of characters excluding the wildcard characters. The prefix specifies a fixed number of
     * characters that the index record must start with.
     * 
     * @param monitor an optional ProgressMonitor
     * @param indexes the array of MtkIndex instances to query
     * @param pattern
     * @return results
     * @throws ModelerCoreException
     */
    public static IEntryResult[] queryIndex( IProgressMonitor monitor,
                                             final Index[] indexes,
                                             final Collection patterns,
                                             final boolean isPrefix,
                                             final boolean isCaseSensitive,
                                             final boolean returnFirstMatch ) throws ModelerCoreException {
        try {
            return SimpleIndexUtil.queryIndex(monitor != null ? new ProgressMonitorImpl(monitor) : null,
                                              indexes,
                                              patterns,
                                              isPrefix,
                                              isCaseSensitive,
                                              returnFirstMatch);
        } catch (MetaMatrixCoreException e) {
            throw new ModelerCoreException(e);
        }
    }

    public static String getRuntimeIndexFileName( final ModelResource model ) {
        CoreArgCheck.isNotNull(model);
        return getRuntimeIndexFileName(model.getResource());
    }

    public static String getRuntimeIndexFileName( final Resource model ) {
        CoreArgCheck.isNotNull(model);
        ModelResource mResource = ModelWorkspaceManager.getModelWorkspaceManager().findModelResource(model);
        return getRuntimeIndexFileName(mResource);
    }

    public static String getRuntimeIndexFileName( final IResource resource ) {
        CoreArgCheck.isNotNull(resource);
        final String resourcePath = resource.getFullPath().toString();
        return getRuntimeIndexFileName(resourcePath);
    }

    public static String getRuntimeIndexFileName( final String fullPath ) {
        CoreArgCheck.isNotNull(fullPath);
        return getIndexFileName(fullPath, IndexConstants.INDEX_EXT);
    }

    public static String getSearchIndexFileName( final ModelResource model ) {
        CoreArgCheck.isNotNull(model);
        return getSearchIndexFileName(model.getResource());
    }

    public static String getSearchIndexFileName( final Resource model ) {
        CoreArgCheck.isNotNull(model);
        ModelResource mResource = ModelWorkspaceManager.getModelWorkspaceManager().findModelResource(model);
        return getSearchIndexFileName(mResource);
    }

    public static String getSearchIndexFileName( final IResource resource ) {
        CoreArgCheck.isNotNull(resource);
        final String resourcePath = resource.getFullPath().toString();
        return getSearchIndexFileName(resourcePath);
    }

    public static String getSearchIndexFileName( final String fullPath ) {
        CoreArgCheck.isNotNull(fullPath);
        return getIndexFileName(fullPath, IndexConstants.SEARCH_INDEX_EXT);
    }

    public static String getIndexFileName( final String fullPath,
                                           final String extension ) {
        CoreArgCheck.isNotNull(fullPath);
        CoreArgCheck.isNotNull(extension);
        String pathForName = fullPath;
        // If the index file names should be case insensitive ...
        if (!CASE_SENSITIVE_INDEX_FILE_NAMES) {
            pathForName = pathForName.toUpperCase();
        }
        CHECK_SUM_CALCULATOR.reset();
        CHECK_SUM_CALCULATOR.update(pathForName.getBytes());

        // Create an index file name for this path
        final String indexFileName = CHECK_SUM_CALCULATOR.getValue() + IndexConstants.EXTENSION_CHAR + extension;
        //System.out.println("Index filename = "+indexFileName+", original filename = "+pathForName); //$NON-NLS-1$ //$NON-NLS-2$
        return indexFileName;
    }

    /**
     * Return true if the specifed index file exists on the file system otherwise return false.
     */
    public static boolean indexFileExists( final String indexFilePath ) {
        return SimpleIndexUtil.indexFileExists(indexFilePath);
    }

    /**
     * Return true if the specifed index file exists on the file system otherwise return false.
     */
    public static boolean indexFileExists( final File indexFile ) {
        return SimpleIndexUtil.indexFileExists(indexFile);
    }

    /**
     * Return true if the specifed index file represents a known index file on the file system otherwise return false.
     */
    public static boolean isModelIndex( final String indexFileName ) {
        return SimpleIndexUtil.isModelIndex(indexFileName);
    }

    /**
     * Return true if the specifed index file represents a index file on the file system otherwise return false.
     */
    public static boolean isIndexFile( final String indexFileName ) {
        return SimpleIndexUtil.isIndexFile(indexFileName);
    }

    /**
     * Return true if the specifed index file represents a index file on the file system otherwise return false.
     */
    public static boolean isIndexFile( final File indexFile ) {
        return SimpleIndexUtil.isIndexFile(indexFile);
    }

    /**
     * Return an array of indexes given a indexName.
     * 
     * @param indexName The shortName of the index file
     * @param selector The indexSelector to lookup indexes
     * @return An array of indexes, may be duplicates depending on index selector.
     * @throws ModelerCoreException If there is an error looking up indexes
     * @since 4.2
     */
    public static Index[] getIndexes( final String indexName,
                                      final IndexSelector selector ) throws ModelerCoreException {
        try {
            return SimpleIndexUtil.getIndexes(indexName, selector);
        } catch (MetaMatrixCoreException e) {
            throw new ModelerCoreException(e);
        }
    }

    /**
     * Return the name of the index file to use for the specified record type, applies only for sever and vdb index files.
     * 
     * @param recordType
     * @return
     */
    public static String getIndexFileNameForRecordType( final char recordType ) {
        return SimpleIndexUtil.getIndexFileNameForRecordType(recordType);
    }

    /**
     * Return the name of the index file to use for the specified record type, applies only for sever and vdb index files.
     * 
     * @param recordType
     * @return
     */
    public static String getRecordTypeForIndexFileName( final String indexName ) {
        return SimpleIndexUtil.getRecordTypeForIndexFileName(indexName);
    }

    /**
     * Return the prefix match string that could be used to exactly match a fully qualified entity name in an index record. All
     * index records contain a header portion of the form: recordType|name|
     * 
     * @param name The fully qualified name for which the prefix match string is to be constructed.
     * @return The pattern match string of the form: recordType|name|
     */
    public static String getPrefixPattern( final char recordType,
                                           final String uuid ) {

        return SimpleIndexUtil.getPrefixPattern(recordType, uuid);
    }

    /**
     * Deletes all index files in the specified collection of files.
     * 
     * @param theFiles the collection of files whose index files are being deleted
     * @param theContinueOnErrorFlag the flag indicated if this method should proceed even after a problem is encountered deleting
     *        a file
     * @return <code>true</code> if all index files were successfully deleted; <code>false</code> otherwise.
     * @since 5.0.1
     */
    public static boolean deleteIndexFiles( File[] theFiles,
                                            boolean theContinueOnErrorFlag ) {
        boolean result = true;

        for (int i = theFiles.length; --i >= 0;) {
            File file = theFiles[i];

            if (isIndexFile(file)) {
                ModelWorkspaceManager.getModelWorkspaceManager().getIndexManager().disposeIndex(file.getName());
            }
        }

        return result;
    }

    public static Index getIndexFile( String indexFileName,
                                      String indexFilePath,
                                      String resourceFileName ) {
        return ModelWorkspaceManager.getModelWorkspaceManager().getIndexManager().getIndex(indexFileName,
                                                                                           indexFilePath,
                                                                                           resourceFileName);
    }

    public static Index getIndexFile( String indexFileName,
                                      String indexFilePath ) {
        return ModelWorkspaceManager.getModelWorkspaceManager().getIndexManager().getIndex(indexFileName, indexFilePath);
    }

    public static Index getNewIndexFile( String indexFileName,
                                         String indexFilePath,
                                         String resourceFileName ) {
        return ModelWorkspaceManager.getModelWorkspaceManager().getIndexManager().getNewIndex(indexFileName,
                                                                                              indexFilePath,
                                                                                              resourceFileName);
    }

    public static Index getNewIndexFile( String indexFileName,
                                         String indexFilePath ) {
        return ModelWorkspaceManager.getModelWorkspaceManager().getIndexManager().getNewIndex(indexFileName, indexFilePath);
    }

    public static Index[] getExistingIndexes( File[] indexFiles ) {
        return ModelWorkspaceManager.getModelWorkspaceManager().getIndexManager().getExistingIndexes(indexFiles);
    }
}
