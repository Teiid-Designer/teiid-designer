/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.util;

import java.text.MessageFormat;

/**
 * Utilities useful in testing method preconditions.
 */
public class Precondition {

    /**
     * @param objectBeingChecked the object whose class is being checked (cannot be null)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @param requiredClass the class the object must be an <code>instanceof</code> (cannot be <code>null</code>)
     */
    public static void instanceOf(final Object objectBeingChecked,
                                  String identifier,
                                  final Class requiredClass) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$
        Precondition.notNull(objectBeingChecked, "objectBeingChecked"); //$NON-NLS-1$
        Precondition.notNull(requiredClass, "requiredClass"); //$NON-NLS-1$

        if (!requiredClass.isInstance(objectBeingChecked)) {
            throw new IllegalArgumentException(MessageFormat.format("\"{0}\" is not null", identifier)); //$NON-NLS-1$
        }
    }

    /**
     * @param objectBeingChecked the object being checked (can be <code>null</code>)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the object is <em>not</em> <code>null</code>
     */
    public static void isNull(final Object objectBeingChecked,
                              String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$

        if (objectBeingChecked != null) {
            throw new IllegalArgumentException(MessageFormat.format("\"{0}\" is not null", identifier)); //$NON-NLS-1$
        }
    }

    /**
     * @param actual the value being checked (can be <code>null</code> or empty)
     * @param expected the expected value (can be <code>null</code> or empty)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the actual text does not exactly match the expected text
     */
    public static void matchesExactly(final String actual,
                                      final String expected,
                                      final String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$
        boolean matches = true;

        if (actual == null) {
            matches = (expected == null);
        } else if (expected == null) {
            matches = false;
        } else {
            matches = actual.equals(expected);
        }

        if (!matches) {
            throw new IllegalArgumentException(MessageFormat.format("\"{0}\" does not exactly match", identifier)); //$NON-NLS-1$
        }
    }

    /**
     * @param textBeingChecked the text being checked (can be <code>null</code> or empty)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the text is <code>null</code> or empty
     */
    public static void notEmpty(final String textBeingChecked,
                                String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$

        if (StringUtil.isEmpty(textBeingChecked)) {
            throw new IllegalArgumentException(MessageFormat.format("\"{0}\" is empty", identifier)); //$NON-NLS-1$
        }
    }

    /**
     * @param objectBeingChecked the object being checked (can be <code>null</code>)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(final Object objectBeingChecked,
                               String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$

        if (objectBeingChecked == null) {
            throw new IllegalArgumentException(MessageFormat.format("\"{0}\" is null", identifier)); //$NON-NLS-1$
        }
    }

    /**
     * Don't allow construction.
     */
    private Precondition() {
        // nothing to do
    }
}
