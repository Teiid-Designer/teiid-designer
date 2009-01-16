/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.core.util;

import com.metamatrix.core.util.ArgCheck;

/**<p>
 * </p>
 * @since 4.0
 */
public final class ArrayUtil {
    //============================================================================================================================
    // Constants
    
    public static interface Constants {
        Object[] EMPTY_ARRAY = new Object[0];
    }

    //============================================================================================================================
    // Static Methods

    /**<p>
     * Returns whether the specified array is empty.
     * </p>
     * @param array The array to check; may not be null.
     * @return True if the specified array is not null and empty.
     * @since 4.0
     */
    public static boolean isEmpty(final Object[] array) {
        ArgCheck.isNotNull(array);
        return (array.length == 0);
    }
        
    /**<p>
     * Returns whether the specified array is null or empty.
     * </p>
     * @param array The array to check; may be null.
     * @return True if the specified array is null or empty.
     * @since 4.0
     */
    public static boolean isNullOrEmpty(final Object[] array) {
        return (array == null  ||  array.length == 0);
    }
    
    //============================================================================================================================
    // Constructors

    /**<p>
     * Prevents instantiation.
     * </p>
     * @since 4.0
     */
    private ArrayUtil() {
    }
}
