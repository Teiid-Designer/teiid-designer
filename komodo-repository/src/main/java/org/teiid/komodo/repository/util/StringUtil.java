/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository.util;

/**
 * Utilities for use with string types.
 */
public class StringUtil {

    /**
     * @param text the string being checked (can be <code>null</code> or empty)
     * @return <code>true</code> if <code>null</code> or empty
     */
    public static boolean isEmpty( final String text ) {
        return ((text == null) || text.isEmpty());
    }

    /**
     * Don't allow construction.
     */
    private StringUtil() {
        // nothing to do
    }
}
