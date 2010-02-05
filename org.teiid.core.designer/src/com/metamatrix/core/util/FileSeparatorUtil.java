/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.io.File;

/**
 * Utility class used to centralize determining File Separator strings independent of OS.
 * 
 * @since 5.0
 */
public final class FileSeparatorUtil {

    private static final String FILE_SEPARATOR_WIN32 = "\\"; //$NON-NLS-1$
    private static final String FILE_SEPARATOR_LINUX = "/"; //$NON-NLS-1$

    /**
     * Returns a file separator for a given input string path If either the WIN32 or LINUX separators are present, it is returned.
     * If NONE is detected, then the File.separator (OS specific) is returned.
     * 
     * @param somePath
     * @return
     * @since 5.0
     */
    public static String getFileSeparator( String somePath ) {
        if (somePath.indexOf(FILE_SEPARATOR_WIN32) > -1) {
            return FILE_SEPARATOR_WIN32;
        }

        if (somePath.indexOf(FILE_SEPARATOR_LINUX) > -1) {
            return FILE_SEPARATOR_LINUX;
        }

        return File.separator;
    }
}
