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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;
import org.teiid.designer.extension.Messages;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;

/**
 * The <code>UserDefinitionsManager</code> class manages the persistence of User-Defined ModelExtensionDefintions
 */
public final class UserExtensionDefinitionsManager {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /**
     * The file name extension used when persisting the user MEDs.
     */
    private static final String USER_MED_EXT = ".mxd"; //$NON-NLS-1$

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The path where the User-Defined ModelExtensionDefinitions are persisted or <code>null</code> if not persisted.
     */
    private final String userMedsDirPath;

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
    public UserExtensionDefinitionsManager( String directoryPath ) {
        this.userMedsDirPath = directoryPath;
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
                // if the file extension is .txt return true, else false
                return name.endsWith(USER_MED_EXT);
            }
        };
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * Restore the User Defined MEDs from the previous session from the save location
     * 
     * @return a status indicating if the previous session state was restored successfully
     */
    public Collection<File> getUserDefinitionFiles() {
        Collection<File> userDefnFiles = new ArrayList<File>();

        if (this.userMedsDirPath != null) {
            File dir = new File(this.userMedsDirPath);

            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles(mxdFilter);
                for (int i = 0; i < files.length; i++) {
                    File mxdFile = files[i];
                    if (!mxdFile.isFile() || !mxdFile.exists() || !mxdFile.canRead()) {
                        Util.log(IStatus.ERROR,
                                 NLS.bind(Messages.errorUserDefinitionNotFoundOrNotReadable, mxdFile.getAbsolutePath()));
                    } else {
                        userDefnFiles.add(mxdFile);
                    }
                }
            }
        }
        
        return userDefnFiles;
    }

    /**
     * Save the User Defined MEDs to the specified save location. Any MEDs currently stored at the location will be deleted or
     * overwritten.
     * 
     * @return a status indicating if the previous session state was restored successfully
     */
    public void saveUserDefinitions( Collection<ModelExtensionDefinition> userDefns ) {

        // Delete any existing mxds at the save location
        deleteExistingMeds(this.userMedsDirPath);
        File dir = new File(this.userMedsDirPath);

        if (dir.exists() && dir.isDirectory() && dir.canWrite()) {
            ModelExtensionDefinitionWriter medWriter = new ModelExtensionDefinitionWriter();
            for (ModelExtensionDefinition med : userDefns) {
                // Construct Name using namespace and version
                String namespacePrefix = med.getNamespacePrefix();
                int version = med.getVersion();
                String fName = namespacePrefix + version + USER_MED_EXT;

                File file = new File(dir.getAbsolutePath() + File.separator + fName);
                InputStream stream = medWriter.writeAsStream(med);
                try {
                    writeFile(stream, file);
                } catch (IOException e) {
                    Util.log(IStatus.ERROR, NLS.bind(Messages.errorUserDefinitionNotSaved, file.getAbsolutePath()));
                }
            }
        }
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

    private void deleteExistingMeds( String userMedsDirPath ) {
        if (this.userMedsDirPath != null) {
            File dir = new File(this.userMedsDirPath);

            if (dir.exists() && dir.isDirectory() && dir.canWrite()) {

                // Get Current mxds in the directory and remove them
                File[] files = dir.listFiles(mxdFilter);
                for (int i = 0; i < files.length; i++) {
                    File mxdFile = files[i];
                    if (!mxdFile.isFile() || !mxdFile.exists() || !mxdFile.canWrite()) {
                        Util.log(IStatus.ERROR,
                                 NLS.bind(Messages.errorUserDefinitionDeleteNotFoundOrCantWrite, mxdFile.getAbsolutePath()));
                    } else {
                        mxdFile.delete();
                    }
                }
            }
        }
    }

}
