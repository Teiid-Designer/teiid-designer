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
