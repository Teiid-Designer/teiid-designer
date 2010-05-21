/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.util;

import java.io.File;
import java.util.Random;

import org.teiid.core.util.FileUtils;
import org.teiid.logging.LogManager;
import com.metamatrix.common.util.LogConstants;

/**
 * Creates and deletes temporary directories.
 */
public class TempDirectory {
    private static TempDirectoryMonitor monitor;

    private String path;

    public static void setMonitor( TempDirectoryMonitor tempDirectoryMonitor ) {
        monitor = tempDirectoryMonitor;
    }

    public TempDirectory( String tempDirPath,
                          long time,
                          long randomNumber ) {
        // Eliminate negetives
        randomNumber = Math.abs(randomNumber);
        if (tempDirPath == null || tempDirPath.length() == 0) {
            path = FileUtils.TEMP_DIRECTORY + time + "_" + randomNumber; //$NON-NLS-1$
        } else {
            final String basePath = (tempDirPath.endsWith(File.separator) ? tempDirPath : tempDirPath + File.separator);
            path = basePath + time + "_" + randomNumber; //$NON-NLS-1$
        }
    }

    public TempDirectory( long time,
                          long randomNumber ) {
        this(FileUtils.TEMP_DIRECTORY, time, randomNumber);
    }

    public String getPath() {
        return path;
    }

    public void create() {
        new File(path).mkdirs();
        if (monitor != null) {
            monitor.createdTempDirectory(this);
        }
    }

    public void remove() {
        File directory = new File(path);
        if (directory.exists()) {
            FileUtils.removeDirectoryAndChildren(directory);
        }
    }

    /**
     * Clean up the temp directory if for some reason remove() has not been called explicitly.
     * 
     * @see java.lang.Object#finalize()
     * @since 4.2
     */
    @Override
    public void finalize() {
        this.remove();
    }

    /**
     * Create a temporary directory under a given paret directory.
     * 
     * @param parentDirectory The paretDirectory under which the temporary directory needs to be created.
     * @return The parentDirectory under which the temporary directory needs to be created.
     * @since 4.3
     */
    public static synchronized TempDirectory getTempDirectory( String parentDirectoryPath ) {
        final Random RANDOM = new Random(System.currentTimeMillis());
        File parentDirectory = null;
        if (parentDirectoryPath != null) {
            // Use path passed in
            parentDirectory = new File(parentDirectoryPath);
        } else {
            // Use default tmp file location
            parentDirectory = new File(FileUtils.TEMP_DIRECTORY);
        }
        final String absolutePath = parentDirectory.getAbsolutePath();

        TempDirectory tempDirectory = new TempDirectory(absolutePath, System.currentTimeMillis(), RANDOM.nextLong());
        while (new File(tempDirectory.getPath()).exists()) {
            LogManager.logInfo(LogConstants.CTX_CONFIG,
                               "Temporary Folder " + tempDirectory.getPath() + " already exists; Creating new folder..."); //$NON-NLS-1$ //$NON-NLS-2$
            try {
                Thread.sleep(10);
            } catch (final InterruptedException ignored) {
            }
            tempDirectory = new TempDirectory(absolutePath, System.currentTimeMillis(), RANDOM.nextLong());
        }

        // Create it
        tempDirectory.create();

        return tempDirectory;
    }
}
