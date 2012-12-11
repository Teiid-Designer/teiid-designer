/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Utilities for use with files.
 */
public class FileUtil {

    /**
     * @param file the text file whose contents are being read (cannot be <code>null</code>)
     * @return the file text (never <code>null</code> but can be empty)
     * @throws Exception if there is a problem reading the file
     */
    public static String toString(final File file) throws Exception {
        Precondition.notNull(file, "file"); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));

        try {
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n"); //$NON-NLS-1$
                line = br.readLine();
            }
        } finally {
            br.close();
        }

        return sb.toString();
    }

    /**
     * Don't allow construction.
     */
    private FileUtil() {
        // nothing to do
    }
}
