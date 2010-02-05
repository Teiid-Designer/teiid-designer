/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.modeler.util;

import java.util.Collection;
import com.metamatrix.core.modeler.CoreModelerPlugin;

/**
 * This class contains a set of static utility methods for checking method arguments. It contains many of the common checks that
 * are done, such as checking that an Object is non-null, checking the range of a value, etc. All of these methods throw
 * {@link #java.lang.IllegalArgumentException}.
 */
public class ArgCheck {

    /**
     * Can't construct - utility class
     */
    private ArgCheck() {
    }

    /**
     * Check that the boolean condition is true; throw an IllegalArgumentException if not.
     * 
     * @param condition The boolean condition to check
     * @param message Exception message if check fails
     * @throws IllegalArgumentException if condition is false
     */
    public static final void isTrue( boolean condition,
                                     String message ) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Check that the value is non-negative (>=0).
     * 
     * @param value Value
     * @throws IllegalArgumentException If value is negative (<0)
     */
    public static final void isNonNegative( int value ) {
        isNonNegative(value, null);
    }

    /**
     * Check that the value is non-negative (>=0).
     * 
     * @param value Value
     * @param message Exception message if check fails
     * @throws IllegalArgumentException If value is negative (<0)
     */
    public static final void isNonNegative( int value,
                                            String message ) {
        if (value < 0) {
            final String msg = message != null ? message : CoreModelerPlugin.Util.getString("ArgCheck.isNonNegativeInt"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the value is positive (>0).
     * 
     * @param value Value
     * @throws IllegalArgumentException If value is non-positive (<=0)
     */
    public static final void isPositive( int value ) {
        isPositive(value, null);
    }

    /**
     * Check that the value is positive (>0).
     * 
     * @param value Value
     * @param message Exception message if check fails
     * @throws IllegalArgumentException If value is non-positive (<=0)
     */
    public static final void isPositive( int value,
                                         String message ) {
        if (value <= 0) {
            final String msg = message != null ? message : CoreModelerPlugin.Util.getString("ArgCheck.isPositiveInt"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the string is non-null and has length > 0
     * 
     * @param value Value
     * @throws IllegalArgumentException If value is null or length == 0
     */
    public static final void isNotZeroLength( String value ) {
        isNotZeroLength(value, null);
    }

    /**
     * Check that the string is non-null and has length > 0
     * 
     * @param value Value
     * @param message Exception message if check fails
     * @throws IllegalArgumentException If value is null or length == 0
     */
    public static final void isNotZeroLength( String value,
                                              String message ) {
        isNotNull(value);
        if (value.length() <= 0) {
            final String msg = message != null ? message : CoreModelerPlugin.Util.getString("ArgCheck.isStringNonZeroLength"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the object is non-null
     * 
     * @param value Value
     * @throws IllegalArgumentException If value is null
     */
    public static final void isNotNull( Object value ) {
        isNotNull(value, null);
    }

    /**
     * Check that the object is non-null
     * 
     * @param value Value
     * @param message Exception message if check fails
     * @throws IllegalArgumentException If value is null
     */
    public static final void isNotNull( Object value,
                                        String message ) {
        if (value == null) {
            final String msg = message != null ? message : CoreModelerPlugin.Util.getString("ArgCheck.isNonNull"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the object is an instance of the specified Class
     * 
     * @param theClass Class
     * @param value Value
     * @throws IllegalArgumentException If value is null
     */
    public static final void isInstanceOf( Class theClass,
                                           Object value ) {
        isInstanceOf(theClass, value, null);
    }

    /**
     * Check that the object is an instance of the specified Class
     * 
     * @param theClass Class
     * @param value Value
     * @param message Exception message if check fails
     * @throws IllegalArgumentException If value is null
     */
    public static final void isInstanceOf( Class theClass,
                                           Object value,
                                           String message ) {
        isNotNull(value);
        if (!theClass.isInstance(value)) {
            final String msg = message != null ? message : CoreModelerPlugin.Util.getString("ArgCheck.isInstanceOf", theClass.getName(), value.getClass().getName()); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the collection is not empty
     * 
     * @param collection Collection
     * @throws IllegalArgumentException If collection is null or empty
     */
    public static final void isNotEmpty( Collection collection ) {
        isNotEmpty(collection, null);
    }

    /**
     * Check that the collection is not empty
     * 
     * @param collection Collection
     * @param message Exception message if check fails
     * @throws IllegalArgumentException If collection is null or empty
     */
    public static final void isNotEmpty( Collection collection,
                                         String message ) {
        isNotNull(collection);
        if (collection.isEmpty()) {
            final String msg = message != null ? message : CoreModelerPlugin.Util.getString("ArgCheck.isCollectionNotEmpty"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Check that the string is not empty
     * 
     * @param string String
     * @throws IllegalArgumentException If string is null or empty
     * @since 3.1
     */
    public static final void isNotEmpty( String string ) {
        isNotZeroLength(string, null);
    }

    /**
     * Check that the string is not empty
     * 
     * @param string String
     * @param message Exception message if check fails
     * @throws IllegalArgumentException If string is null or empty
     * @since 3.1
     */
    public static final void isNotEmpty( String string,
                                         String message ) {
        isNotZeroLength(string, message);
    }

    /**
     * Check that the collection contains the value
     * 
     * @param collection Collection to check
     * @param value Value to check for, may be null
     * @throws IllegalArgumentException If collection is null or doesn't contain value
     */
    public static final void contains( Collection collection,
                                       Object value ) {
        contains(collection, value, null);
    }

    /**
     * Check that the collection contains the value
     * 
     * @param collection Collection to check
     * @param value Value to check for, may be null
     * @param message Exception message if check fails
     * @throws IllegalArgumentException If collection is null or doesn't contain value
     */
    public static final void contains( Collection collection,
                                       Object value,
                                       String message ) {
        isNotNull(collection);
        if (!collection.contains(value)) {
            final String msg = message != null ? message : CoreModelerPlugin.Util.getString("ArgCheck.contains"); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
    }
}
