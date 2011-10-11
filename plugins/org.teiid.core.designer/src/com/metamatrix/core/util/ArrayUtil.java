/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;


/**
 * @since 4.0
 */
public final class ArrayUtil {

    public static interface Constants {
        Object[] EMPTY_ARRAY = new Object[0];
    }

    /**
     * Obtains the index of the first occurrence of the requested object.
     * 
     * @param array the array of items (cannot be <code>null</code>)
     * @param objectToFind the object whose index is being requested (can be <code>null</code>)
     * @return the index or -1 if not found
     */
    public static int indexOf( final Object[] array,
                               Object objectToFind ) {
        if (isNullOrEmpty(array)) {
            throw new IllegalArgumentException("array cannot be empty"); //$NON-NLS-1$
        }

        int i = 0;

        for (Object obj : array) {
            // both are null
            if ((objectToFind == null) && (obj == null)) {
                return i;
            }

            if ((obj != null) && obj.equals(objectToFind)) {
                return i;
            }

            ++i;
        }

        return -1;
    }

    /**
     * <p>
     * Returns whether the specified array is null or empty.
     * </p>
     * 
     * @param array The array to check; may be null.
     * @return True if the specified array is null or empty.
     * @since 4.0
     */
    public static boolean isNullOrEmpty( final Object[] array ) {
        return (array == null || array.length == 0);
    }

    /**
     * <p>
     * Prevents instantiation.
     * </p>
     * 
     * @since 4.0
     */
    private ArrayUtil() {
    }
}
