/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.komodo.repository.util;

import java.text.MessageFormat;

/**
 * Utilities useful in testing method preconditions.
 */
public class Precondition {

    private static final String MISSING_IDENTIFIER = "{*** missing identifier ***}"; //$NON-NLS-1$

    /**
     * @param actual the value being checked (can be <code>null</code> or empty)
     * @param expected the expected value (can be <code>null</code> or empty)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the actual text does not exactly match the expected text
     */
    public static void matchesExactly(String actual,
                                      String expected,
                                      String identifier) {
        boolean matches = true;

        if (actual == null) {
            matches = (expected == null);
        } else if (expected == null) {
            matches = false;
        } else {
            matches = actual.equals(expected);
        }

        if (!matches) {
            throw new IllegalArgumentException(MessageFormat.format("'{0}' does not exactly match", identifier)); //$NON-NLS-1$
        }
    }

    /**
     * @param textBeingChecked the text being checked (can be <code>null</code> or empty)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the text is <code>null</code> or empty
     */
    public static void notEmpty(final String textBeingChecked,
                                String identifier) {
        if (StringUtil.isEmpty(textBeingChecked)) {
            if (StringUtil.isEmpty(identifier)) {
                identifier = MISSING_IDENTIFIER;
            }

            throw new IllegalArgumentException(MessageFormat.format("'{0}' is empty", identifier)); //$NON-NLS-1$
        }
    }

    /**
     * @param objectBeingChecked the object being checked (can be <code>null</code>)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(final Object objectBeingChecked,
                               String identifier) {
        if (objectBeingChecked == null) {
            if (StringUtil.isEmpty(identifier)) {
                identifier = MISSING_IDENTIFIER;
            }

            assert ((identifier != null) && !identifier.isEmpty()) : "the argument identifier is empty"; //$NON-NLS-1$
            throw new IllegalArgumentException(MessageFormat.format("'{0}' is null", identifier)); //$NON-NLS-1$
        }
    }

    /**
     * Don't allow construction.
     */
    private Precondition() {
        // nothing to do
    }
}
