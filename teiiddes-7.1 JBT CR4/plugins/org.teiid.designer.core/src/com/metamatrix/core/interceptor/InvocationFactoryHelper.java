/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.interceptor;

import java.lang.reflect.Method;

/**
 * InvocationFactoryHelper
 */
public interface InvocationFactoryHelper {
    
    public static final int WRITABLE = 1;
    public static final int READ_ONLY = 2;
    public static final int WRITABLE_UNKNOWN = 3;

    /**
     * Method that determines whether the method is considered writable.
     * @return {@link #WRITABLE} if the is known to alter the state of the target object, 
     * {@link #READ_ONLY} if the method is known to <i>not</i> alter the state, 
     * or {@link #WRITABLE_UNKNOWN} if such a determination could not be made.
     */
    public int isWrite( final Method method );

}
