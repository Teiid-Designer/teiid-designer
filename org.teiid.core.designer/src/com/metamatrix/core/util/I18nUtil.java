/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import com.metamatrix.core.modeler.util.ArgCheck;

/**
 * @since 4.0
 */
public final class I18nUtil {
    //============================================================================================================================
    // Constants

    /**<p>
     * </p>
     * @since 4.0
     */
    public static interface Constants {
        char PROPERTY_NAME_SEPARATOR_CHAR = '.';
    }

    //============================================================================================================================
	// Static Methods

    /**<p>
     * @param clazz A class.
     * @return The un-package-qualified name of the specified class followed by the
     * {@link Constants#PROPERTY_NAME_SEPARATOR_CHAR}.
     * </p>
     * @since 4.0
     */
    public static String getPropertyPrefix(final Class clazz) {
    	ArgCheck.isNotNull(clazz);
        return clazz.getSimpleName() + Constants.PROPERTY_NAME_SEPARATOR_CHAR;
    }

    /**<p>
     * Convenience method that simple calls {@link #getPropertyPrefix(Class)}.
     * @param clazz A class.
     * @return The un-package-qualified name of the specified class followed by the
     * {@link Constants#PROPERTY_NAME_SEPARATOR_CHAR}.
     * </p>
     * @since 4.0
     */
    public static String getPropertyPrefix(final Object object) {
        ArgCheck.isNotNull(object);
        return getPropertyPrefix(object.getClass());
    }

    //============================================================================================================================
    // Constructors

    /**<p>
     * Prevents instantiation.
     * </p>
     * @since 4.0
     */
    private I18nUtil() {
    }
}
