/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.io.File;
import java.net.URL;

/**
 * @author John Verhaeg
 */
public final class ProjectUtil {

    /**
     * @param clazz A class.
     * @return The name of the project containing <code>class</code>.
     * @since 5.5.2
     */
    public static String getProjectName( Class clazz ) {
        return getProjectPathSegments(clazz)[0];
    }

    /**
     * @param object An object.
     * @return The name of the project containing <code>object</code>.
     * @since 5.5.2
     */
    public static String getProjectName( Object object ) {
        return getProjectName(object.getClass());
    }

    /**
     * @param clazz A class.
     * @return The path of the project containing <code>class</code>.
     * @since 5.5.2
     */
    public static String getProjectPath( Class clazz ) {
        // Get project path segments
        String[] segs = getProjectPathSegments(clazz);
        // Build path from segments
        StringBuilder builder = new StringBuilder();
        int segCount = segs.length;
        builder.append(segs[0]);
        for (int ndx = 1; ndx < segCount; ndx++) {
            builder.append(File.separatorChar);
            builder.append(segs[ndx]);
        }
        builder.append(File.separatorChar);
        return builder.toString();
    }

    /**
     * @param object An object.
     * @return The path of the project containing <code>object</code>.
     * @since 5.5.2
     */
    public static String getProjectPath( Object object ) {
        return getProjectPath(object.getClass());
    }

    /**
     * @param clazz A class.
     * @return The path segments of the project containing <code>class</code>.
     * @since 5.5.2
     */
    public static String[] getProjectPathSegments( Class clazz ) {
        // Get class' source location
        final URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        // Find project folder
        String[] segs = url.getPath().split("/"); //$NON-NLS-1$
        int segCount = segs.length;
        if ("bin".equals(segs[segCount - 1])) { //$NON-NLS-1$
            String[] pathSegs = new String[segCount - 1];
            System.arraycopy(segs, 0, pathSegs, 0, pathSegs.length);
            return pathSegs;
        }
        return segs;
    }

    /**
     * @param object An object.
     * @return The path segments of the project containing <code>object</code>.
     * @since 5.5.2
     */
    public static String[] getProjectPathSegments( Object object ) {
        return getProjectPathSegments(object.getClass());
    }

    private ProjectUtil() {
    }
}
