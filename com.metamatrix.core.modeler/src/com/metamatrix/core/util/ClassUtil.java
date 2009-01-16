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

package com.metamatrix.core.util;

import java.net.URL;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Class-related utilities.
 * 
 * @since 4.0
 */
public final class ClassUtil {

    // ============================================================================================================================
    // Constants

    /**
     * @since 4.0
     */
    public static interface Constants extends
                                     FileUtils.Constants {

        String JAR_EXTENSION = FILE_EXTENSION_SEPARATOR + "jar"; //$NON-NLS-1$
        String ZIP_EXTENSION = FILE_EXTENSION_SEPARATOR + "zip"; //$NON-NLS-1$
        String CLASS_EXTENSION = FILE_EXTENSION_SEPARATOR + "class"; //$NON-NLS-1$

        String LIBRARY_FILTER = FILE_NAME_WILDCARD + JAR_EXTENSION + "; " + FILE_NAME_WILDCARD + ZIP_EXTENSION; //$NON-NLS-1$

        String PACKAGE_SEPARATOR = "."; //$NON-NLS-1$
        String INNER_CLASS_SEPARATOR = "$"; //$NON-NLS-1$

        char PACKAGE_SEPARATOR_CHAR = '.';
        char INNER_CLASS_SEPARATOR_CHAR = '$';
    }
    
    private static final String PLUGINS_FOLDER = "plugins"; //$NON-NLS-1$

    // ============================================================================================================================
    // Static Methods

    /**
     * Currently only handles when run from IDE.
     * 
     * @param clazz
     *            A class.
     * @return The name of the project containing the specified class.
     * @since 4.3
     */
    public static String getProjectName(final Class clazz) {
        final String path = getProjectPath(clazz);
        return (path == null ? null : new Path(path).lastSegment());
    }

    /**
     * Currently only handles when run from IDE.
     * 
     * @param clazz
     *            A class.
     * @return The path of the project containing the specified class.
     * @since 4.3
     */
    public static String getProjectPath(final Class clazz) {
        // Get class' source location
        final URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        // Find project folder, which should be the first folder below the last occurrence of PLUGINS_FOLDER
        IPath path = new Path(url.getPath());
        IPath parent = path.removeLastSegments(1);
        while (parent.segmentCount() > 0 && !PLUGINS_FOLDER.equalsIgnoreCase(parent.lastSegment())) {
            path = parent;
            parent = path.removeLastSegments(1);
        }
        // Return unqualified project folder name (if found)
        return (parent.segmentCount() > 0 ? path.toString() : null);
    }

    // ============================================================================================================================
    // Constructors

    /**
     * Prevents instantiation.
     * 
     * @since 4.0
     */
    private ClassUtil() {
    }
}
