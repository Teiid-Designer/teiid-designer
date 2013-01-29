/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.util;

import java.util.Collection;
import java.util.Map;
import org.komodo.common.CommonI18n;
import org.komodo.common.i18n.I18n;

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
                                  final String identifier,
                                  final Class requiredClass) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$
        Precondition.notNull(objectBeingChecked, "objectBeingChecked"); //$NON-NLS-1$
        Precondition.notNull(requiredClass, "requiredClass"); //$NON-NLS-1$

        if (!requiredClass.isInstance(objectBeingChecked)) {
            throw new IllegalArgumentException(I18n.bind(CommonI18n.objectIsNotInstanceOf, identifier, requiredClass.getName()));
        }
    }

    /**
     * @param objectBeingChecked the object being checked (can be <code>null</code>)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the object is <em>not</em> <code>null</code>
     */
    public static void isNull(final Object objectBeingChecked,
                              final String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$

        if (objectBeingChecked != null) {
            throw new IllegalArgumentException(I18n.bind(CommonI18n.objectIsNotNull, identifier));
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
            throw new IllegalArgumentException(I18n.bind(CommonI18n.stringsDoNotMatchExactly, identifier, actual, expected));
        }
    }

    /**
     * @param collectionBeingChecked the collection being checked (can be <code>null</code> or empty)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the collection is <code>null</code> or empty
     */
    public static void notEmpty(final Collection<?> collectionBeingChecked,
                                final String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$

        if (CollectionUtil.isEmpty(collectionBeingChecked)) {
            throw new IllegalArgumentException(I18n.bind(CommonI18n.collectionIsEmpty, identifier));
        }
    }

    /**
     * @param mapBeingChecked the map being checked (can be <code>null</code> or empty)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the collection is <code>null</code> or empty
     */
    public static void notEmpty(final Map<?, ?> mapBeingChecked,
                                final String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$

        if (CollectionUtil.isEmpty(mapBeingChecked)) {
            throw new IllegalArgumentException(I18n.bind(CommonI18n.mapIsEmpty, identifier));
        }
    }

    /**
     * @param textBeingChecked the text being checked (can be <code>null</code> or empty)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the text is <code>null</code> or empty
     */
    public static void notEmpty(final String textBeingChecked,
                                final String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$

        if (StringUtil.isEmpty(textBeingChecked)) {
            throw new IllegalArgumentException(I18n.bind(CommonI18n.stringIsEmpty, identifier));
        }
    }

    /**
     * @param objectBeingChecked the object being checked (can be <code>null</code>)
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(final Object objectBeingChecked,
                               final String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$

        if (objectBeingChecked == null) {
            throw new IllegalArgumentException(I18n.bind(CommonI18n.objectIsNull, identifier));
        }
    }

    /**
     * @param collection the collection whose size is being checked (cannot be <code>null</code>)
     * @param expected the expected size
     * @param identifier the identifier used in the error message (cannot be <code>null</code> or empty)
     * @throws IllegalArgumentException if the collection size is is not what is expected or if the collection is <code>null</code>
     */
    public static void sizeIs(final Object[] collection,
                              final int expected,
                              final String identifier) {
        assert !StringUtil.isEmpty(identifier) : "identifier cannot be empty"; //$NON-NLS-1$
        Precondition.notNull(collection, "collection"); //$NON-NLS-1$

        if (collection.length != expected) {
            throw new IllegalArgumentException(I18n.bind(CommonI18n.objectIsNull, identifier));
        }
    }

    /**
     * Don't allow construction.
     */
    private Precondition() {
        // nothing to do
    }
}
