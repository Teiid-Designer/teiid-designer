/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.sdt.types;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreRuntimeException;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.types.DatatypeConstants;
import com.metamatrix.modeler.internal.core.index.ModelIndexer;
import com.metamatrix.modeler.internal.core.index.ResourceFileIndexSelector;
import com.metamatrix.modeler.sdt.ModelerSdtPlugin;

public class RebuildingBuiltInTypesIndexSelector extends ResourceFileIndexSelector {

    /** Defines the expected name of the built-in datatypes model file and index file */
    public static final String DATATYPES_ZIP_FILE_NAME = DatatypeConstants.DATATYPES_ZIP_FILE_NAME;
    public static final String DATATYPES_MODEL_FILE_NAME = DatatypeConstants.DATATYPES_MODEL_FILE_NAME;
    public static final String DATATYPES_INDEX_FILE_NAME = DatatypeConstants.DATATYPES_INDEX_FILE_NAME;

    private static IPath DATA_DIRECTORY_PATH = null;
    private static IPath INDEX_FILE_PATH = null;
    static {
        // Set the path to the directory into which the index file will be extracted
        try {
            DATA_DIRECTORY_PATH = ModelerSdtPlugin.getDefault().getStateLocation();
        } catch (Throwable e) {
            String TEMP_DIR = System.getProperty("user.dir") + "\\indexes"; //$NON-NLS-1$ //$NON-NLS-2$
            DATA_DIRECTORY_PATH = new Path(TEMP_DIR);
        }
        // Set the path to the index file
        INDEX_FILE_PATH = DATA_DIRECTORY_PATH.append(DATATYPES_INDEX_FILE_NAME);
    }

    /**
     * Construct an instance of BuiltInTypesIndexSelector.
     * 
     * @param filePath
     * @throws CoreException
     */
    public RebuildingBuiltInTypesIndexSelector() throws CoreException {
        // Construct with the path to the archive containing the builtInDatatypes.INDEX
        super(getZipFilePath());

        // If the built-in datatypes index file already exists in the data directory then
        // delete it so that the most recent one is extracted from the zip file.
        removeExistingIndexFile(INDEX_FILE_PATH.toFile());

        // Set the path to the directory location into which the
        // builtInDatatypes.INDEX will be created
        super.setIndexDirectoryPath(DATA_DIRECTORY_PATH.toOSString());
    }

    /**
     * Construct an instance of BuiltInTypesIndexSelector.
     * 
     * @param filePath
     * @throws CoreException
     */
    public RebuildingBuiltInTypesIndexSelector( final String filePath ) throws CoreException {
        // Construct with the path to the archive containing the builtInDatatypes.INDEX
        super(filePath);

        // If the built-in datatypes index file already exists in the data directory then
        // delete it so that the most recent one is extracted from the zip file.
        removeExistingIndexFile(INDEX_FILE_PATH.toFile());

        // Set the path to the directory location into which the
        // builtInDatatypes.INDEX will be created
        super.setIndexDirectoryPath(DATA_DIRECTORY_PATH.toOSString());
    }

    /*
     * @see com.metamatrix.modeler.core.index.IndexSelector#getIndexes()
     */
    @Override
    public Index[] getIndexes() throws IOException {
        // If the index file was removed at some point then force it to
        // be loaded again by resetting the Index[] reference to null
        if (!INDEX_FILE_PATH.toFile().exists()) {
            // System.err.println("Retrieving INDEX file from archive "+INDEX_FILE_PATH.toOSString());
            this.indexes = null;
        }
        return super.getIndexes();
    }

    /**
     * Create a {@link com.metamatrix.modeler.core.container.Container} instance to be used when loading the resources. The method
     * in ResourceFileIndexSelector has been overridden here in order to load the "built-in" datatype models into an <b>empty</b>
     * container instead of a container already containing these models loading through external resources.
     * 
     * @param name
     * @return
     */
    @Override
    protected Container createContainer( final String name ) throws CoreException {
        final Container container = ModelerCore.createEmptyContainer(name);

        // Add the external rsesource sets to the container as delegate resource sets
        ModelerCore.addExternalResourceSets(container);

        return container;
    }

    /**
     * Return the MtkIndex[] corresponding to the specified array of EMF resources. The method in ResourceFileIndexSelector has
     * been overriddent here in order to echo the contents of the index files to System.out
     * 
     * @param modelFiles
     * @return
     * @throws CoreException
     */
    @Override
    protected Index[] indexResources( final Resource[] models ) throws CoreException {
        ModelIndexer.PRINT_INDEX_CONTENTS = true;
        Index[] newIndexes = super.indexResources(models);
        ModelIndexer.PRINT_INDEX_CONTENTS = false;
        return newIndexes;
    }

    private static String getZipFilePath() {

        // Find the zip file location within the com.metamatrix.modeler.sdt plugin
        final URL installURL = ModelerSdtPlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
        String zipFilePath = null;
        try {
            zipFilePath = FileLocator.toFileURL(new URL(installURL, DATATYPES_ZIP_FILE_NAME)).getFile();
        } catch (Throwable t) {
            ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                      t,
                                      ModelerSdtPlugin.Util.getString("BuiltInTypesIndexSelector.Error_creating_local_URL_for_1")); //,DATATYPES_ZIP_FILE_NAME //$NON-NLS-1$
            throw new ModelerCoreRuntimeException(t.getMessage());
        }
        if (zipFilePath == null || zipFilePath.length() == 0) {
            ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                      ModelerSdtPlugin.Util.getString("BuiltInTypesIndexSelector.Unable_to_create_absolute_path_to_zip_file_2")); //,DATATYPES_ZIP_FILE_NAME //$NON-NLS-1$
        }
        final File zipFile = new File(zipFilePath);
        if (!zipFile.exists()) {
            ModelerSdtPlugin.Util.log(IStatus.ERROR,
                                      ModelerSdtPlugin.Util.getString("BuiltInTypesIndexSelector.The_file_cannot_be_found_on_the_file_system_3")); //,zipFilePath //$NON-NLS-1$
        }

        return zipFilePath;
    }

    private static void removeExistingIndexFile( final File indexFile ) {
        if (indexFile.exists()) {
            // System.err.println("Deleting INDEX file "+indexFile);
            indexFile.delete();
        }
    }
}
