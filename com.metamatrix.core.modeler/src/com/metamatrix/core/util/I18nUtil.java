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

/**
 * @since 4.0
 */
public final class I18nUtil {
    //============================================================================================================================
    // Constants

    /**<p>
     * </p>
     * @since 4.0
     */
    public static interface Constants {
        char PROPERTY_NAME_SEPARATOR_CHAR = '.';
    }

    //============================================================================================================================
	// Static Methods

    /**<p>
     * @param clazz A class.
     * @return The un-package-qualified name of the specified class followed by the
     * {@link Constants#PROPERTY_NAME_SEPARATOR_CHAR}.
     * </p>
     * @since 4.0
     */
    public static String getPropertyPrefix(final Class clazz) {
    	ArgCheck.isNotNull(clazz);
        return clazz.getSimpleName() + Constants.PROPERTY_NAME_SEPARATOR_CHAR;
    }

    /**<p>
     * Convenience method that simple calls {@link #getPropertyPrefix(Class)}.
     * @param clazz A class.
     * @return The un-package-qualified name of the specified class followed by the
     * {@link Constants#PROPERTY_NAME_SEPARATOR_CHAR}.
     * </p>
     * @since 4.0
     */
    public static String getPropertyPrefix(final Object object) {
        ArgCheck.isNotNull(object);
        return getPropertyPrefix(object.getClass());
    }

    //============================================================================================================================
    // Constructors

    /**<p>
     * Prevents instantiation.
     * </p>
     * @since 4.0
     */
    private I18nUtil() {
    }
}
