/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.core.modeler.util;

import java.io.File;

import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.FileUtils.Constants;

public final class FileUtil {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /**
     * Constants for common file extensions.
     * 
     * @since 6.0.0
     */
    public interface Extensions {
        /**
         * Value is "{@value} ."
         * 
         * @since 6.0.0
         */
        String CLASS_LOWER = ".class"; //$NON-NLS-1$

        /**
         * Value is "{@value} ."
         * 
         * @since 6.0.0
         */
        String CLASS_UPPER = ".CLASS"; //$NON-NLS-1$

        /**
         * Value is "{@value} ."
         * 
         * @since 6.0.0
         */
        String JAVA_LOWER = ".java"; //$NON-NLS-1$

        /**
         * Value is "{@value} ."
         * 
         * @since 6.0.0
         */
        String JAVA_UPPER = ".JAVA"; //$NON-NLS-1$

        /**
         * An archive file extension with the value of "{@value}."
         * 
         * @since 6.0.0
         */
        String JAR_LOWER = ".jar"; //$NON-NLS-1$

        /**
         * An archive file extension with the value of "{@value}."
         * 
         * @since 6.0.0
         */
        String JAR_UPPER = ".JAR"; //$NON-NLS-1$

        /**
         * An archive file extension with the value of "{@value}."
         * 
         * @since 6.0.0
         */
        String ZIP_LOWER = ".zip"; //$NON-NLS-1$

        /**
         * An archive file extension with the value of "{@value}."
         * 
         * @since 6.0.0
         */
        String ZIP_UPPER = ".ZIP"; //$NON-NLS-1$
    }

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    /**
     * Checks the specified file name to see if it has an archive extension.
     * 
     * @param name the file name being checked
     * @param checkZipExtension indicates if zip files should be considered an archive file
     * @return <code>true</code> if file name has an archive extension
     * @since 6.0.0
     * @see Extensions#JAR_LOWER
     * @see Extensions#JAR_UPPER
     * @see Extensions#ZIP_LOWER
     * @see Extensions#ZIP_UPPER
     */
    public final static boolean isArchiveFileName( String name,
                                                   boolean checkZipExtension ) {
        if (name.endsWith(Extensions.JAR_LOWER) || name.endsWith(Extensions.JAR_UPPER)) {
            return (name.length() > Extensions.JAR_LOWER.length());
        }

        if (checkZipExtension) {
            if (name.endsWith(Extensions.ZIP_LOWER) || name.endsWith(Extensions.ZIP_UPPER)) {
                return (name.length() > Extensions.ZIP_LOWER.length());
            }
        }

        return false;
    }

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Prevents instantiation.
     * 
     * @since 6.0
     */
    private FileUtil() {
        // nothing to do
    }
    
    /**
     * Returns the file extension portion of this file, or an empty string if there is none.
     * <p>
     * The file extension portion is defined as the string
     * following the last period (".") character in the file name.
     * If there is no period in the file name, the file has no
     * file extension portion. If the name ends in a period,
     * the file extension portion is the empty string.
     * </p>
     * @param resource
     * @return the file extension or <code>null</code>
     * @since 4.3
     */
    public static String getFileExtension( final File resource ) {
        if ( resource != null ) {
        	String ext = getExtension(resource);
        	if( ext != null ) {
        		return ext;
        	}
        }
        return StringUtil.Constants.EMPTY_STRING;
    }
    
    /**
     * Obtains the file extension of the specified <code>File</code>. The extension is considered to be all the
     * characters after the last occurrence of {@link Constants#FILE_EXTENSION_SEPARATOR_CHAR} in the pathname
     * of the input.
     * @param theFile the file whose extension is being requested
     * @return the extension or <code>null</code> if not found
     * @since 4.2
     */
    public static String getExtension(File theFile) {
        return getExtension(theFile.getPath());
    }
    
    /**
     * Obtains the file extension of the specified file name. The extension is considered to be all the
     * characters after the last occurrence of {@link Constants#FILE_EXTENSION_SEPARATOR_CHAR}.
     * @param theFileName the file whose extension is being requested
     * @return the extension or <code>null</code> if not found
     * @since 4.2
     */
    public static String getExtension(String theFileName) {
        String result = StringUtil.Constants.EMPTY_STRING;
        final int index = theFileName.lastIndexOf(Constants.FILE_EXTENSION_SEPARATOR_CHAR);
        
        // make sure extension char is found and is not the last char in the path
        if ((index != -1) && ((index + 1) != theFileName.length())) {
            result = theFileName.substring(index + 1);
        }
        
        return result;
    }
}
