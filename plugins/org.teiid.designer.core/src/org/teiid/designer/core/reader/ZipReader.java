/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.reader;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 */
public class ZipReader {

    private final File file;

    /**
     * @param file
     */
    public ZipReader(File file) {
        this.file = file;
    }

    /**
     * Read entries in zip file and return an {@link InputStream} to the
     * file that matches the given fileName or null if there is no file
     *
     * @param fileName
     * @param callback
     *
     * @throws Exception
     */
    public void readEntry(String fileName, ZipReaderCallback callback) throws Exception {
        ZipFile zipFile = null;
        InputStream inputStream = null;
        try {
            zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                final ZipEntry zipEntry = entries.nextElement();
                if (!zipEntry.getName().equals(fileName)) continue;

                inputStream = zipFile.getInputStream(zipEntry);
                callback.process(inputStream);
                break;
            }

        } finally {
            if (inputStream != null)
                inputStream.close();

            if (zipFile != null)
                zipFile.close();
        }
    }

}
