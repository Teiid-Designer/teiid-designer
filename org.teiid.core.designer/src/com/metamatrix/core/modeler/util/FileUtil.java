/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.modeler.util;

import java.io.File;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.FileUtils.Constants;

public final class FileUtil {

    /**
     * Constants for common file extensions.
     * 
     * @since 6.0.0
     */
    public interface Extensions {

        /**
         * A jar file extension with the value of "{@value} ."
         * 
         * @since 6.0.0
         */
        String JAR = ".jar"; //$NON-NLS-1$

        /**
         * A zip file extension with the value of "{@value} ."
         * 
         * @since 6.0.0
         */
        String ZIP = ".zip"; //$NON-NLS-1$
    }

    /**
     * Checks the specified file name to see if it has an archive extension.
     * 
     * @param name the file name being checked
     * @param checkZipExtension indicates if zip files should be considered an archive file
     * @return <code>true</code> if file name has an archive extension
     * @since 6.0.0
     * @see Extensions#JAR
     * @see Extensions#ZIP
     */
    public final static boolean isArchiveFileName( String name,
                                                   boolean checkZipExtension ) {
        if (name.endsWith(Extensions.JAR)) {
            return (name.length() > Extensions.JAR.length());
        }

        if (checkZipExtension) {
            return isZipFileName(name);
        }

        return false;
    }

    /**
     * @param name the name being tested (never <code>null</code>)
     * @return <code>true</code> if the name ends with a zip file extension and has a simple name with length of one or more
     * @see Extensions#ZIP
     */
    public final static boolean isZipFileName( String name ) {
        return (name.endsWith(Extensions.ZIP) && (name.length() > Extensions.ZIP.length()));
    }

    /**
     * Obtains the file extension of the specified <code>File</code>. The extension is considered to be all the characters after
     * the last occurrence of {@link Constants#FILE_EXTENSION_SEPARATOR_CHAR} in the pathname of the input.
     * 
     * @param theFile the file whose extension is being requested
     * @return the extension or <code>null</code> if not found
     * @since 4.2
     */
    public static String getExtension( File theFile ) {
        return getExtension(theFile.getPath());
    }

    /**
     * Obtains the file extension of the specified file name. The extension is considered to be all the characters after the last
     * occurrence of {@link Constants#FILE_EXTENSION_SEPARATOR_CHAR}.
     * 
     * @param theFileName the file whose extension is being requested
     * @return the extension or <code>null</code> if not found
     * @since 4.2
     */
    public static String getExtension( String theFileName ) {
        String result = StringUtil.Constants.EMPTY_STRING;
        final int index = theFileName.lastIndexOf(Constants.FILE_EXTENSION_SEPARATOR_CHAR);

        // make sure extension char is found and is not the last char in the path
        if ((index != -1) && ((index + 1) != theFileName.length())) {
            result = theFileName.substring(index + 1);
        }

        return result;
    }

    /**
     * Prevents instantiation.
     * 
     * @since 6.0
     */
    private FileUtil() {
        // nothing to do
    }
}
