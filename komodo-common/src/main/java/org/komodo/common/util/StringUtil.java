/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.util;

/**
 * Utilities for use with string types.
 */
public class StringUtil {

    /**
     * A empty string constant.
     */
    public static final String EMPTY_STRING = ""; //$NON-NLS-1$

    /**
     * A empty string array constant.
     */
    public static final String[] EMPTY_ARRAY = {};

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
