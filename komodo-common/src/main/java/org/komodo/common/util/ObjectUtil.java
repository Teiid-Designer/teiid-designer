/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.util;

/**
 * Utilities for use with object types.
 */
public class ObjectUtil {

    /**
     * A empty object array constant.
     */
    public static final Object[] EMPTY_ARRAY = {};

    /**
     * @param thisObj one of the objects being checked (can be <code>null</code>)
     * @param thatObj the other object being checked (can be <code>null</code>)
     * @return <code>true</code> if both strings are <code>null</code> or equals
     */
    public static boolean matches(final Object thisObj,
                                  final Object thatObj) {
        if (thisObj == null) {
            return (thatObj == null);
        }

        return thisObj.equals(thatObj);
    }

}
