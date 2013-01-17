/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.komodo.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utilities for use with collections.
 */
public class CollectionUtil {

    /**
     * @param map the map being checked (can be <code>null</code> or empty)
     * @return <code>true</code> if <code>null</code> or empty
     */
    public static boolean isEmpty(final Map<?, ?> map) {
        return ((map == null) || map.isEmpty());
    }

    /**
     * @param collection the collection being checked (can be <code>null</code> or empty)
     * @return <code>true</code> if <code>null</code> or empty
     */
    public static boolean isEmpty(final Collection<?> collection) {
        return ((collection == null) || collection.isEmpty());
    }

    /**
     * The order of the elements in the list does not matter.
     * 
     * @param thisList one of the lists being compared (can be <code>null</code>)
     * @param thatList the other list being compared (can be <code>null</code>)
     * @return <code>true</code> if the lists are both <code>null</code>, both empty, or contain all the same entries
     */
    public static boolean matches(final List<?> thisList,
                                  final List<?> thatList) {
        if (thisList == null) {
            return (thatList == null);
        }

        if (thatList == null) {
            return false;
        }

        if (thisList.size() != thatList.size()) {
            return false;
        }

        for (final Object element : thisList) {
            if (!thatList.contains(element)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param thisMap one of the maps being compared (can be <code>null</code>)
     * @param thatMap the other map being compared (can be <code>null</code>)
     * @return <code>true</code> if the maps are both <code>null</code>, both empty, or contain all the same entries
     */
    public static boolean matches(final Map<?, ?> thisMap,
                                  final Map<?, ?> thatMap) {
        if (thisMap == null) {
            return (thatMap == null);
        }

        if (thatMap == null) {
            return false;
        }

        if (thisMap.size() != thatMap.size()) {
            return false;
        }

        for (final Entry<?, ?> entry : thisMap.entrySet()) {
            final Object key = entry.getKey();

            if (!thatMap.containsKey(key)) {
                return false;
            }

            final Object value = entry.getValue();
            final Object otherValue = thatMap.get(key);

            if (value == null) {
                if (otherValue != null) {
                    return false;
                }
            } else if (otherValue == null) {
                return false;
            }

            if (!value.equals(otherValue)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Don't allow construction.
     */
    private CollectionUtil() {
        // nothing to do
    }

}
