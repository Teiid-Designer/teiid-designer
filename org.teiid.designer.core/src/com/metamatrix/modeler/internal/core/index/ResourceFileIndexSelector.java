/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.DateUtil;
import com.metamatrix.core.util.FileUtils;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * ResourceFileIndexSelector returns indexes associated with a specified file. The specified file can represent a single EMF
 * resource, a folder containing zero or more resources, or an archive containing resource files. Indexes will be generated for
 * all resources associated with the specified file.
 */

// mtkTODO: Remove this class (and the subclass, associated tests, and factory functionality)
public class ResourceFileIndexSelector extends AbstractIndexSelector {

    private static final File[] EMPTY_FILE_ARRAY = new File[0];
    private static final Index[] EMPTY_INDEX_ARRAY = new Index[0];
    private static final Resource[] EMPTY_RESOURCE_ARRAY = new Resource[0];

    protected static final String TEMP_DIR = FileUtils.TEMP_DIRECTORY + "\\indexes"; //$NON-NLS-1$

    protected final File selectedFile;
    protected Index[] indexes;
    protected String indexDirectoryPath;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * Construct an instance of ResourceFileIndexSelector.
     * 
     * @param filePath the absolute path to the file; may not be null
     * @throws CoreException
     */
    public ResourceFileIndexSelector( String filepath ) throws ModelerCoreException {
        if (filepath == null) {
            ArgCheck.isNotNull(filepath,
                               ModelerCore.Util.getString("ResourceFileIndexSelector.The_filepath_string_may_not_be_null_1")); //$NON-NLS-1$
        }

        this.selectedFile = new File(filepath);

        // Check if the selected file exists ...
        if (!this.selectedFile.exists()) {
            throw new IllegalArgumentException(
                                               ModelerCore.Util.getString("ResourceFileIndexSelector.The_specified_file_does_not_exist._2", filepath)); //$NON-NLS-1$
        }

        // The specified file is an archive ...
        if (FileUtils.isArchiveFileName(filepath)) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("ResourceFileIndexSelector.Building_indexes_from_resource_found_in_archive_._3", filepath)); //$NON-NLS-1$
        }
        // The specified file is a folder ...
        else if (this.selectedFile.isDirectory()) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("ResourceFileIndexSelector.Building_indexes_from_resource_found_under_folder_._4", filepath)); //$NON-NLS-1$
        }
        // The specified file is the resource itself ...
        else if (this.selectedFile.isFile()) {
            ModelerCore.Util.log(IStatus.INFO,
                                 ModelerCore.Util.getString("ResourceFileIndexSelector.Building_indexes_from_resource_at_._5", filepath)); //$NON-NLS-1$
        }
        // The specified resource cannot be interpretted
        else {
            throw new ModelerCoreException(
                                           ModelerCore.Util.getString("ResourceFileIndexSelector.The_specified_file_cannot_be_processed_by_the_ResourceFileIndexSelector_6", filepath)); //$NON-NLS-1$
        }
    }

    // ==================================================================================
    // I N T E R F A C E M E T H O D S
    // ==================================================================================

    /*
     * @see com.metamatrix.modeler.core.index.IndexSelector#getIndexes()
     */
    @Override
    public Index[] getIndexes() throws IOException {

        // If no index files exist yet then look for EMF resource
        // files to use for creating the index files
        if (this.indexes == null || this.indexes.length == 0) {
            this.indexes = buildIndexes(this.selectedFile);
        }

        return this.indexes;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    /**
     * Builds new index files for any models that are part of a Archive/directory.
     */
    protected Index[] buildIndexes( final File f ) throws IOException {

        // Retrieve any model files from the specified File
        File[] modelFiles = loadModelFiles(f);

        // Load the EMF resources for any model files that were found
        Resource[] models = EMPTY_RESOURCE_ARRAY;
        try {
            models = loadResources(modelFiles);
        } catch (Throwable e) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 e,
                                 ModelerCore.Util.getString("ResourceFileIndexSelector.Error_loading_resources_for_7", f)); //$NON-NLS-1$
        }

        // Create new index files for any loaded resources
        Index[] newIndexes = EMPTY_INDEX_ARRAY;
        try {
            newIndexes = indexResources(models);
        } catch (Throwable e) {
            ModelerCore.Util.log(IStatus.ERROR,
                                 e,
                                 ModelerCore.Util.getString("ResourceFileIndexSelector.Error_building_indexes_for_resources_in_8", f)); //$NON-NLS-1$
        }

        return newIndexes;
    }

    /**
     * Return the MtkIndex[] corresponding to the specified array of EMF resources.
     * 
     * @param modelFiles
     * @return
     * @throws CoreException
     */
    protected Index[] indexResources( final Resource[] models ) throws CoreException {
        ArrayList tmp = new ArrayList();
        File indexDirectory = new File(this.getIndexDirectoryPath());

        // Create index files for all model resources
        for (int i = 0; i < models.length; i++) {
            EmfResource resource = (EmfResource)models[i];
            if (resource != null) {
                String indexFileName = resource.getURI().lastSegment();
                String extension = resource.getURI().fileExtension();
                if (extension != null) {
                    int endIndex = indexFileName.indexOf(extension) - 1;
                    indexFileName = indexFileName.substring(0, endIndex);
                }
                indexFileName = indexFileName + IndexConstants.EXTENSION_CHAR + IndexConstants.INDEX_EXT;

                // Remove any existing index file prior to creating the new one
                File indexFilePath = new File(indexDirectory.getAbsolutePath() + File.separator + indexFileName);
                if (indexFilePath.exists()) {
                    indexFilePath.delete();
                }

                // Index the EMF resource
                String resourcePath = resource.getURI().toFileString();
                IndexUtil.indexResource(resource, resourcePath, this.getIndexDirectoryPath(), indexFileName);

                // Retrive the index file from the file system
                File indexFile = new File(indexDirectory, indexFileName);
                if (isIndexFile(indexFile)) {
                    Index theIndex = IndexUtil.getIndexFile(indexFile.getName(),
                                                            indexFile.getAbsolutePath(),
                                                            resource.getURI().lastSegment());
                    if (theIndex != null) {
                        tmp.add(theIndex);
                    }
                }
            }
        }

        Index[] result = new Index[tmp.size()];
        tmp.toArray(result);
        return result;
    }

    /**
     * Return the Resource[] for the EMF resources corresponding to the specified array of model files.
     * 
     * @param modelFiles
     * @return
     * @throws CoreException
     */
    protected Resource[] loadResources( final File[] modelFiles ) throws CoreException {

        ArrayList tmp = new ArrayList();

        // Create a temporary container
        final String tempContainerName = DateUtil.getCurrentDateAsString() + "_Container"; //$NON-NLS-1$
        final Container container = this.createContainer(tempContainerName);

        // Load the resources into the temporary container
        for (int i = 0; i < modelFiles.length; i++) {
            File modelFile = modelFiles[i];
            Resource resource = loadResource(container, modelFile);
            if (resource != null && resource instanceof EmfResource) {
                tmp.add(resource);
            }
        }

        Resource[] result = new Resource[tmp.size()];
        tmp.toArray(result);
        return result;
    }

    /**
     * Load the Resource for the specified model file
     * 
     * @param modelFile
     * @return
     * @throws CoreException
     */
    protected Resource loadResource( final Container container,
                                     final File modelFile ) {
        URI uri = URI.createFileURI(modelFile.getAbsolutePath());
        Resource resource = container.getResource(uri, true);
        return resource;
    }

    /**
     * Create a {@link com.metamatrix.modeler.core.container.Container} instance to be used when loading the resources.
     * 
     * @param name
     * @return
     */
    protected Container createContainer( final String name ) throws CoreException {
        return ModelerCore.createContainer(name);
    }

    /**
     * Return the File[] for the specified file if it is a model file, folder, or archive.
     * 
     * @param f
     * @return
     * @throws IOException
     */
    protected File[] loadModelFiles( final File f ) throws IOException {
        File[] modelFiles = EMPTY_FILE_ARRAY;
        // Retrieve any model files from the archive
        if (FileUtils.isArchiveFileName(f.getName())) {
            modelFiles = loadModelsFromZip(f);
        }
        // Retrieve model files in the given directory
        else if (f.isDirectory()) {
            modelFiles = loadModelsFromFolder(f);
        }
        // Retrieve the model file from the specified file
        else {
            modelFiles = loadModelsFromFile(f);
        }
        return modelFiles;
    }

    /**
     * Return the File[] for the specified file if it is a model file.
     * 
     * @param file
     * @return
     * @throws IOException
     */
    protected File[] loadModelsFromFile( final File file ) {
        File[] result = EMPTY_FILE_ARRAY;
        if (ModelUtil.isModelFile(file)) {
            result = new File[1];
            result[0] = file;
        }
        return result;
    }

    /**
     * Return the File[] constructed from model files found in the specified folder
     * 
     * @param folder
     * @return
     * @throws IOException
     */
    protected File[] loadModelsFromFolder( final File folder ) {
        ArrayList tmp = new ArrayList();

        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (ModelUtil.isModelFile(files[i])) {
                tmp.add(files[i]);
            }
        }

        File[] result = new File[tmp.size()];
        tmp.toArray(result);
        return result;
    }

    /**
     * Return the File[] constructed from model files found in the specified archive
     * 
     * @param folder
     * @return
     * @throws IOException
     */
    protected File[] loadModelsFromZip( final File zip ) throws IOException {
        // the zip file that would be initialized
        // if the file being read is an archive
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zip);

            // ArrayListy of MtkIndexes, one for each entry in the zip file
            ArrayList tmp = new ArrayList();

            // Iterate over all entries in the zip file ...
            Enumeration entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                // need to read the entry and write a file to a temporary location
                ZipEntry entry = (ZipEntry)entries.nextElement();
                if (entry == null) {
                    break;
                }
                IPath zipEntryPath = new Path(entry.getName());
                String entryName = zipEntryPath.lastSegment();

                // read the contents of the entry
                InputStream inputStream = zipFile.getInputStream(entry);

                // buffer that would contain the contents of the entry
                byte[] buffer;
                int length = (int)entry.getSize();
                if (length >= 0) {
                    buffer = new byte[length];

                    int offset = 0;
                    do {
                        int n = inputStream.read(buffer, offset, length);
                        offset += n;
                        length -= n;
                    } while (length > 0);
                } else {
                    buffer = new byte[1024];
                    int n;
                    do {
                        n = inputStream.read(buffer, 0, 1024);
                    } while (n >= 0);
                }

                // create the directory into which the models will be extracted - use
                // the same directory path into which the index files will be written
                File indexDirectory = new File(this.getIndexDirectoryPath());
                if (!indexDirectory.exists()) {
                    indexDirectory.mkdir();
                }

                // create an index file at the temporary location
                // write contents of the zip entry to this file
                File entryFile = new File(indexDirectory.getAbsolutePath() + File.separator + entryName);
                entryFile.createNewFile();
                entryFile.deleteOnExit();
                FileOutputStream outputStream = new FileOutputStream(entryFile);
                outputStream.write(buffer);
                outputStream.flush();
                outputStream.close();

                // Add an File reference if zip entry is a model file
                if (ModelUtil.isModelFile(entryFile)) {
                    tmp.add(entryFile);
                }
            }

            File[] result = new File[tmp.size()];
            tmp.toArray(result);
            return result;

        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException e) {
            }// Ignore

        }
    }

    /**
     * Check if the given file name is an index name, by checking the extension.
     * 
     * @param fileName The index file name.
     * @return true if it is an index file.
     */
    protected boolean isIndexFile( final String fileName ) {
        if (fileName == null || fileName.length() == 0) {
            return false;
        }
        if (fileName.endsWith(IndexConstants.INDEX_EXT)) {
            return true;
        }
        return false;
    }

    /**
     * Check if the given file is an index, by checking the type and extension.
     * 
     * @param file The File instance to check.
     * @return true if it is an index file.
     */
    protected boolean isIndexFile( final File file ) {
        if (file != null && file.isFile() && file.exists()) {
            return isIndexFile(file.getName());
        }
        return false;
    }

    /**
     * Returns the location of the directory into which index files will be written
     * 
     * @return
     */
    protected String getIndexDirectoryPath() {
        if (this.indexDirectoryPath == null) {
            this.indexDirectoryPath = TEMP_DIR;
        }
        return this.indexDirectoryPath;
    }

    protected void setIndexDirectoryPath( final String path ) {
        this.indexDirectoryPath = path;
    }

}
