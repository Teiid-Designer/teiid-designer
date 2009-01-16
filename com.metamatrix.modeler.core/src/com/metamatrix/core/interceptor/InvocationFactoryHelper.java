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
