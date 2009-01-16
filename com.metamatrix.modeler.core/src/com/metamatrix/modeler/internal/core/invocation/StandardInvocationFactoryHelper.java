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

package com.metamatrix.modeler.internal.core.invocation;

import java.lang.reflect.Method;

import com.metamatrix.core.interceptor.InvocationFactoryHelper;

/**
 * The StandardInvocationFactoryHelper basically knows about typical method forms of most Java classes,
 * such as getters and setters (as defined by JavaBeans) and the methods on {@link Object}.
 */
public class StandardInvocationFactoryHelper implements InvocationFactoryHelper {
    
    /**
     * Construct an instance of StandardInvocationFactoryHelper.
     */
    public StandardInvocationFactoryHelper() {
        super();
    }

    /**
     * Return whether this invocation is known to modify the object.  This is a conservative/cautious
     * implementation that returns true in most cases.
     * <p>
     * The following logic is used to determine whether an invocation is considered writable:
     * <ul>
     *  <li>return <code>false</code> if the signature is <code>toString():String</code></li>
     *  <li>return <code>false</code> if the signature is <code>hashCode():int</code></li>
     *  <li>return <code>false</code> if the signature is <code>equals(Object):boolean</code></li>
     *  <li>return <code>false</code> if the signature is <code>compareTo(Object):int</code></li>
     *  <li>return <code>false</code> if the name begins with <code>get</code></li>
     *  <li>return <code>true</code> if the name begins with <code>set</code></li>
     *  <li>return <code>true</code> if the return type is {@link Void#TYPE void}, which assumes
     *      that any method not returning something is altering the object</li>
     *  <li>return <code>false</code> if the name begins with <code>is</code></li>
     *  <li>return <code>true</code> if the name begins with <code>add</code></li>
     *  <li>return <code>true</code> if the name begins with <code>remove</code></li>
     *  <li>return <code>true</code> if the name begins with <code>clear</code></li>
     *  <li>return <code>true</code> if the name begins with <code>put</code></li>
     *  <li>return <code>false</code> if the name is <code>clone</code></li>
     *  <li>return <code>false</code> if the name is <code>size</code></li>
     *  <li>otherwise return <code>true</code></li>
     * </ul>
     * </p>
     * <p>
     * This method caches the determination of whether the method may modify the target object,
     * so repetitive calls to this method are not expensive.
     * Subclasses wishing to specialize this behavior should consider overriding the 
     * {@link #determineIsWriteMethod()} method.
     * </p>
     * @return {@link #WRITABLE} if this invocation is known to alter the state of the target object, 
     * {@link #READ_ONLY} if the invocation is known to <i>not</i> alter the state, 
     * or {@link #WRITABLE_UNKNOWN} if such a determination could not be made.
     */
    public int isWrite(final Method method) {
        
        final String methodName = method.getName();
        if ( methodName.equals("toString") && //$NON-NLS-1$
             method.getReturnType() == String.class && 
             method.getParameterTypes().length == 0 ) {
            return READ_ONLY;
        }
        if ( methodName.equals("hashCode") && //$NON-NLS-1$
             method.getReturnType() == Integer.TYPE && 
             method.getParameterTypes().length == 0 ) {
            return READ_ONLY;
        }
        if ( methodName.equals("equals") && //$NON-NLS-1$
             method.getReturnType() == Boolean.TYPE && 
             method.getParameterTypes().length == 1 ) {
            return READ_ONLY;
        }
        if ( methodName.equals("compareTo") && //$NON-NLS-1$
             method.getReturnType() == Integer.TYPE && 
             method.getParameterTypes().length == 1 ) {
            return READ_ONLY;
        }
        if ( methodName.startsWith("get") ) { //$NON-NLS-1$
            return READ_ONLY;
        }
        if ( methodName.startsWith("set") ) { //$NON-NLS-1$
            return WRITABLE;
        }

        if ( method.getReturnType() == Void.TYPE ) {
            return WRITABLE;
        }

        if ( methodName.startsWith("is") ) { //$NON-NLS-1$
            return READ_ONLY;
        }
        if ( methodName.startsWith("add") ) { //$NON-NLS-1$
            return WRITABLE;
        }
        if ( methodName.startsWith("remove") ) { //$NON-NLS-1$
            return WRITABLE;
        }
        if ( methodName.startsWith("clear") ) { //$NON-NLS-1$
            return WRITABLE;
        }
        if ( methodName.startsWith("put") ) { //$NON-NLS-1$
            return WRITABLE;
        }
        if ( methodName.equals("clone") ) { //$NON-NLS-1$
            return READ_ONLY;
        }
        if ( methodName.equals("size") ) { //$NON-NLS-1$
            return READ_ONLY;
        }

        return WRITABLE_UNKNOWN;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // Check if instances are identical ...
        if (this == obj) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        
        // Check if object can be compared to this one
        if (obj.getClass().equals(this.getClass()) ) {
            // All instances of this class are considered identical
            return true;
        }
        
        // Otherwise not comparable ...
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 1;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Standard Invocation Factory Helper"; //$NON-NLS-1$
    }

}
