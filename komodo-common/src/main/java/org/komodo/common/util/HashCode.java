/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.util;

import java.util.Arrays;

/**
 * Utilities for use in calculating hashCodes.
 */
public class HashCode {

    // Prime number used in improving distribution
    private static final int PRIME = 103;

    /**
     * Compute a combined hash code from the supplied objects using the supplied seed.
     * 
     * @param seed a value upon which the hash code will be based; may be 0
     * @param objects the objects that should be used to compute the hash code
     * @return the hash code
     */
    private static int _compute(final int seed,
                                final Object... objects) {
        if ((objects == null) || (objects.length == 0)) {
            return seed * HashCode.PRIME;
        }

        // Compute the hash code for all of the objects ...
        int hc = seed;

        for (final Object object : objects) {
            hc = HashCode.PRIME * hc;

            if (object instanceof byte[]) {
                hc += Arrays.hashCode((byte[])object);
            } else if (object instanceof boolean[]) {
                hc += Arrays.hashCode((boolean[])object);
            } else if (object instanceof short[]) {
                hc += Arrays.hashCode((short[])object);
            } else if (object instanceof int[]) {
                hc += Arrays.hashCode((int[])object);
            } else if (object instanceof long[]) {
                hc += Arrays.hashCode((long[])object);
            } else if (object instanceof float[]) {
                hc += Arrays.hashCode((float[])object);
            } else if (object instanceof double[]) {
                hc += Arrays.hashCode((double[])object);
            } else if (object instanceof char[]) {
                hc += Arrays.hashCode((char[])object);
            } else if (object instanceof Object[]) {
                hc += Arrays.hashCode((Object[])object);
            } else if (object != null) {
                hc += object.hashCode();
            }
        }

        return hc;
    }

    /**
     * Compute a combined hash code from the supplied objects. This method always returns 0 if no objects are supplied.
     * 
     * @param objects the objects that should be used to compute the hash code
     * @return the hash code
     */
    public static int compute(final Object... objects) {
        return _compute(0, objects);
    }

    /**
     * Don't allow public construction.
     */
    private HashCode() {
        // nothing to do
    }

}
