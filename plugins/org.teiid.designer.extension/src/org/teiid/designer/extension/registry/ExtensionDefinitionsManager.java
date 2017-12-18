/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.registry;

import static org.teiid.designer.extension.ExtensionPlugin.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.util.TempInputStream;
import org.teiid.designer.extension.ExtensionConstants;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;

/**
 * The <code>UserDefinitionsManager</code> class manages the persistence of User-Defined ModelExtensionDefintions
 *
 * @since 8.0
 */
public final class ExtensionDefinitionsManager implements ExtensionConstants {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The path where the User-Defined ModelExtensionDefinitions are persisted or <code>null</code> if not persisted.
     */
    private final String medsDirPath;

    /**
     * The .mxd filename filter
     */
    private final FilenameFilter mxdFilter;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param directoryPath the directory where the User Defined Meds are persisted (may be <code>null</code> if persistence is
     *        not desired)
     */
    public ExtensionDefinitionsManager( String directoryPath) {
        this.medsDirPath = directoryPath;
        // create a FilenameFilter and override its accept-method
        this.mxdFilter = new FilenameFilter() {

            /**
             * {@inheritDoc}
             *
             * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
             */
            @Override
            public boolean accept( File dir,
                                   String name ) {
                // if the file extension is .mxd return true, else false
                return name.endsWith(ExtensionConstants.DOT_MED_EXTENSION);
            }
        };
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /*
     * @return the directory storing the definitions.
     *
     * User definitions are stored in the root of this directory
     * Imported definitions are stored in /imported of this directory
     */
    private String getDefinitionDirectory(MxdType mxdType) {
        StringBuilder pathBuilder = new StringBuilder(this.medsDirPath);
        if (MxdType.IMPORTED.equals(mxdType))
            pathBuilder.append(File.separator).append(ExtensionConstants.TEIID_IMPORT_DIRECTORY);

        File directory = new File(pathBuilder.toString());
        if (! directory.exists())
            directory.mkdirs();

        return pathBuilder.toString();
    }

    /**
     * Restore the MEDs from the previous session from the save location
     * @param mxdType type of mxd files to retrieve
     *
     * @return a status indicating if the previous session state was restored successfully
     */
    public Collection<File> retrieveDefinitionFiles(MxdType mxdType) {
        Collection<File> defnFiles = new ArrayList<File>();

        if (this.medsDirPath == null)
            return Collections.emptyList();

        String mxdDirPath = getDefinitionDirectory(mxdType);

        File dir = new File(mxdDirPath);
        if (! dir.exists() || ! dir.isDirectory())
            return Collections.emptyList();

        File[] files = dir.listFiles(mxdFilter);
        for (int i = 0; i < files.length; i++) {
            File mxdFile = files[i];
            if (!mxdFile.isFile() || !mxdFile.exists() || !mxdFile.canRead()) {
                Util.log(IStatus.ERROR,
                         NLS.bind(Messages.errorUserDefinitionNotFoundOrNotReadable, mxdFile.getAbsolutePath()));
                continue;
            }

            defnFiles.add(mxdFile);
        }

        return defnFiles;
    }

    private void writeFile( InputStream inputStream,
                            File outFile ) throws IOException {
        OutputStream out = new FileOutputStream(outFile);
        byte buf[] = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0)
            out.write(buf, 0, len);
        out.close();
        inputStream.close();
    }

    private void deleteExistingMed( String medFilePath ) {
        File medFile = new File(medFilePath);
        if (! medFile.exists())
            return;

        medFile.setWritable(true);

        if (! medFile.isFile() || ! medFile.canWrite()) {
            Util.log(IStatus.ERROR,
                     NLS.bind(Messages.errorUserDefinitionDeleteNotFoundOrCantWrite, medFile.getAbsolutePath()));
            return;
        }

        medFile.delete();
    }

    /**
     * Saves the given definition to the directory based on whether its is either custom or imported
     *
     * @param medWriter
     * @param med
     * @return the file the definition is saved to
     */
    public File saveDefinition(ModelExtensionDefinitionWriter medWriter, ModelExtensionDefinition med) {
        MxdType mxdType = MxdType.USER;
        if (med.isImported())
            mxdType = MxdType.IMPORTED;

        // Get the correct directory according to the mxd type
        String mxdDirPath = getDefinitionDirectory(mxdType);
        String namespacePrefix = med.getNamespacePrefix();
        int version = med.getVersion();
        String fName = namespacePrefix + version + ExtensionConstants.DOT_MED_EXTENSION;
        File file = new File(mxdDirPath + File.separator + fName);

        // Delete any existing mxds at the save location
        deleteExistingMed(file.getAbsolutePath());

        // Construct Name using namespace and version
        TempInputStream stream = medWriter.writeAsStream(med);
        try {
            writeFile(stream.getRealInputStream(), file);
        } catch (IOException e) {
            Util.log(IStatus.ERROR, NLS.bind(Messages.errorUserDefinitionNotSaved, file.getAbsolutePath()));
            return null;
        } finally {
            if (stream != null) {
                try {
                	stream.getRealInputStream().close();
                	stream.deleteTempFile();
                } catch (IOException e) {
                    Util.log(IStatus.ERROR, NLS.bind(Messages.errorUserDefinitionNotSaved, file.getAbsolutePath()));
                    return null;
                }
            }
        }

        return file;
    }

    /**
     * Save the User Defined MEDs to the specified save location. Any MEDs currently stored at the location will be deleted or
     * overwritten.
     *
     * @param mxdDefns
     */
    public void saveDefinitions( Collection<ModelExtensionDefinition> mxdDefns ) {
        ModelExtensionDefinitionWriter medWriter = new ModelExtensionDefinitionWriter();
        for (ModelExtensionDefinition med : mxdDefns) {
            saveDefinition(medWriter, med);
        }
    }

}
