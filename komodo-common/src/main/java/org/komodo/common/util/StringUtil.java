/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.util;

import java.util.Collections;
import java.util.List;

/**
 * Utilities for use with string types.
 */
public class StringUtil {

    /**
     * The default delimiter used when combining strings. Value is {@value}.
     */
    public static final String DEFAULT_DELIMITER = ","; //$NON-NLS-1$

    /**
     * An empty string array constant.
     */
    public static final String[] EMPTY_ARRAY = {};

    /**
     * An empty string constant.
     */
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$

    /**
     * An empty string list constant.
     */
    public static final List<String> EMPTY_LIST = Collections.emptyList();

    /**
     * @param strings the strings being joined (cannot be <code>null</code> or empty)
     * @return a string combining all input values separated by the default delimiter
     */
    public static String createDelimitedString(final String... strings) {
        return createDelimitedString(DEFAULT_DELIMITER, strings);
    }

    /**
     * @param delimiter the string to use to separate the values (cannot be <code>null</code> or empty)
     * @param strings the strings being joined (cannot be <code>null</code> or empty)
     * @return a string combining all input values separated by the specified delimiter
     */
    public static String createDelimitedString(final String delimiter,
                                               final String... strings) {
        Precondition.notEmpty(delimiter, "delimiter"); //$NON-NLS-1$
        Precondition.notEmpty(strings, "strings"); //$NON-NLS-1$

        final StringBuilder result = new StringBuilder();
        boolean firstTime = true;

        for (final String string : strings) {
            if (firstTime) {
                firstTime = false;
            } else {
                result.append(delimiter);
            }

            result.append(string);
        }

        return result.toString();
    }

    /**
     * @param text the string being checked (can be <code>null</code> or empty)
     * @return <code>true</code> if <code>null</code> or empty
     */
    public static boolean isEmpty(final String text) {
        return ((text == null) || text.isEmpty());
    }

    /**
     * @param thisString one of the strings being checked (can be <code>null</code> or empty)
     * @param thatString the other string being checked (can be <code>null</code> or empty)
     * @return <code>true</code> if both strings are <code>null</code> or equals
     */
    public static boolean matches(final String thisString,
                                  final String thatString) {
        return ObjectUtil.matches(thisString, thatString);
    }

    /**
     * Don't allow public construction.
     */
    private StringUtil() {
        // nothing to do
    }

}
