/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import java.util.Comparator;
import org.eclipse.core.runtime.IPath;

/**
 * IPathComparator
 */
public class IPathComparator implements Comparator {

    /**
     * Construct an instance of IPathComparator.
     * 
     */
    public IPathComparator() {
    }
    
    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive 
     * integer as the first argument is less than, equal to, or greater than the second.
     * The implementor must ensure that sgn(compare(x, y)) == -sgn(compare(y, x)) for all 
     * x and y. (This implies that compare(x, y) must throw an exception if and only if 
     * compare(y, x) throws an exception.)
     * The implementor must also ensure that the relation is transitive: 
     *     ((compare(x, y)>0)  && (compare(y, z)>0)) implies compare(x, z)>0.
     * Finally, the implementer must ensure that 
     *     compare(x, y)==0 
     * implies that sgn(compare (x, z))==sgn(compare(y, z)) for all z.
     * It is generally the case, but not strictly required that 
     *     (compare(x, y)==0) == (x.equals(y)). 
     * Generally speaking, any comparator that violates this condition should clearly 
     *  indicate this fact. The recommended language is "Note: this comparator imposes 
     *  orderings that are inconsistent with equals." 
     * 
     * Utility to compare two {@link IPath} objects.
     * <p>
     * Note:  this method <i>is</i> consistent with {@link IPath#equals(java.lang.Object)}, meaning
     * that <code>(compare(x, y)==0) == (x.equals(y))</code>.
     * </p>
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return  a negative integer, zero, or a positive integer as the first argument is less 
     * than, equal to, or greater than the second.
     * @throws ClassCastException if the arguments' types prevent them from being 
     * compared by this Comparator.
     */
    public int compare( final Object o1, final Object o2) {
        final IPath path1 = (IPath)o1;
        final IPath path2 = (IPath)o2;
        if ( path1 == path2 ) {
            return 0;
        }
        if ( path1 == null ) {
            return -1;
        }
        if ( path2 == null ) {
            return 1;
        }
        // Both are non-null, so use String's lexicographical comparison
        final String pathStr1 = path1.toString();
        final String pathStr2 = path2.toString();
        return pathStr1.compareTo(pathStr2);
    }

}
