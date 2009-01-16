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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import com.metamatrix.core.interceptor.InvocationFactoryHelper;

/**
 * EmfInvocationFactoryHelper
 */
public class EmfInvocationFactoryHelper implements InvocationFactoryHelper {

    /**
     * Construct an instance of EmfInvocationFactoryHelper.
     * 
     */
    public EmfInvocationFactoryHelper() {
        super();
    }

    /**
     * Return whether this invocation is known to modify the object.  This is intended to be used 
     * in conjunction with the {@link com.metamatrix.modeler.internal.core.invocation.StandardInvocationFactoryHelper}.
     * <p>
     * The following logic is used to determine whether an invocation is considered writable:
     * <ul>
     *  <li>return <code>false</code> if the name is <code>eGet</code> (see {@link EObject})</li>
     *  <li>return <code>false</code> if the name is <code>eIsProxy</code> (see {@link EObject})</li>
     *  <li>return <code>false</code> if the name is <code>eIsSet</code> (see {@link EObject})</li>
     *  <li>return <code>false</code> if the name is <code>eClass</code> (see {@link EObject})</li>
     *  <li>return <code>false</code> if the name is <code>eStaticClass (see {@link EObject})</code></li>
     *  <li>return <code>false</code> if the name is <code>eURIFragmentSegment</code> (see {@link InternalEObject})</li>
     *  <li>return <code>false</code> if the name is <code>eInternalResource</code> (see {@link InternalEObject})</li>
     *  <li>return <code>false</code> if the name is <code>eDerivedStructuralFeatureID</code> (see {@link InternalEObject})</li>
     *  <li>return <code>false</code> if the name is <code>eContainerFeatureID</code> (see {@link InternalEObject})</li>
     *  <li>return <code>false</code> if the name is <code>eBaseStructuralFeatureID</code> (see {@link InternalEObject})</li>
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
        final Class targetClass = method.getDeclaringClass();
        if ( EObject.class.isAssignableFrom(targetClass)  ) {
            if ( methodName.equals("eSet") ) { //$NON-NLS-1$
                return WRITABLE;
            }
            if ( methodName.equals("eUnset") ) { //$NON-NLS-1$
                return WRITABLE;
            }
            if ( methodName.equals("eIsProxy") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eIsSet") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eAllContents") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eClass") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eContainer") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eContainmentFeature") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eContents") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eGet") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eStaticClass") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eResource") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
        }
        if ( InternalEObject.class.isAssignableFrom(targetClass)  ) {
            if ( methodName.equals("eURIFragmentSegment") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eInternalResource") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eDerivedStructuralFeatureID") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eContainerFeatureID") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eBaseStructuralFeatureID") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
            if ( methodName.equals("eProxyURI") ) { //$NON-NLS-1$
                return READ_ONLY;
            }
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
        return "EMF Invocation Factory Helper"; //$NON-NLS-1$
    }


}
