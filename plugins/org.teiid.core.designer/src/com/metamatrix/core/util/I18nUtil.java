/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.util.Locale;

/**
 * @since 4.0
 */
public final class I18nUtil {
    // ============================================================================================================================
    // Constants

    /**<p>
     * </p>
     * @since 4.0
     */
    public static interface Constants {
        char PROPERTY_NAME_SEPARATOR_CHAR = '.';
    }

    // ============================================================================================================================
    // Static Methods

    /**
     * Parses a string representation of a {@link Locale}. Some example strings are "en", "de_DE", "_GB", "en_US_WIN", "de__POSIX",
     * and "fr_MAC." If "default" is passed in then the default <code>Locale</code> is returned.
     * 
     * @param localeString the text being parsed (cannot be <code>null</code> or empty
     * @return the locale
     * @throws IllegalArgumentException if <code>localeString</code> is <code>null</code> or empty
     */
    public static Locale parseLocaleString( String localeString ) {
        CoreArgCheck.isNotEmpty(localeString);

        if (CoreStringUtil.isEmpty(localeString)) {
            return null;
        }

        localeString = localeString.trim();

        if (localeString.toLowerCase().equals("default")) { //$NON-NLS-1$
            return Locale.getDefault();
        }

        final char DELIM = '_';

        // extract language
        int languageIndex = localeString.indexOf(DELIM);
        String language = null;

        if (languageIndex == -1) {
            // only language found
            return new Locale(localeString, CoreStringUtil.Constants.EMPTY_STRING);
        }

        language = localeString.substring(0, languageIndex);

        // extract country
        int countryIndex = localeString.indexOf(DELIM, (languageIndex + 1));
        String country = null;

        if (countryIndex == -1) {
            // only language and country found
            country = localeString.substring(languageIndex + 1);
            return new Locale(language, country);
        }

        // all remaining characters are the variant
        country = localeString.substring((languageIndex + 1), countryIndex);
        String variant = localeString.substring(countryIndex + 1);
        return new Locale(language, country, variant);
    }

    /**<p>
     * @param clazz A class.
     * @return The un-package-qualified name of the specified class followed by the
     * {@link Constants#PROPERTY_NAME_SEPARATOR_CHAR}.
     * </p>
     * @since 4.0
     */
    public static String getPropertyPrefix( final Class clazz ) {
        CoreArgCheck.isNotNull(clazz);
        return clazz.getSimpleName() + Constants.PROPERTY_NAME_SEPARATOR_CHAR;
    }

    /**<p>
     * Convenience method that simple calls {@link #getPropertyPrefix(Class)}.
     * @param clazz A class.
     * @return The un-package-qualified name of the specified class followed by the
     * {@link Constants#PROPERTY_NAME_SEPARATOR_CHAR}.
     *         </p>
     * @since 4.0
     */
    public static String getPropertyPrefix( final Object object ) {
        CoreArgCheck.isNotNull(object);
        return getPropertyPrefix(object.getClass());
    }

    // ============================================================================================================================
    // Constructors

    /**<p>
     * Prevents instantiation.
     * </p>
     * @since 4.0
     */
    private I18nUtil() {
    }
}
