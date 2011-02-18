/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.uml2.uml.Stereotype;
import org.eclipse.uml2.uml.ValueSpecification;
import com.metamatrix.core.interceptor.InvocationFactoryHelper;

/**
 * EmfInvocationFactoryHelper
 */
public class UmlInvocationFactoryHelper implements InvocationFactoryHelper {
    
    private Map methodWritableState;
    
    /**
     * Construct an instance of EmfInvocationFactoryHelper.
     * 
     */
    public UmlInvocationFactoryHelper() {
        super();
        this.methodWritableState = new HashMap();
    }

    /**
     * Return whether this invocation is known to modify the object.  This is intended to be used 
     * in conjunction with the {@link com.metamatrix.modeler.internal.core.invocation.StandardInvocationFactoryHelper}.
     * <p>
     * The following logic is used to determine whether an invocation is considered writable:
     * <ul>
     *  <li>return <code>READ_ONLY</code> if the name is <code>createExtension</code> (see {@link StereoType})</li>
     *  <li>otherwise return <code>WRITABLE_UNKNOWN</code></li>
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
        // Check the cache first to see if this method was checked before
        final Integer writableState = (Integer)this.methodWritableState.get(method);
        if (writableState != null) {
            return writableState.intValue();
        }
        
        // Check the class the method is being invoked upon along with the 
        // method name to see if processing is required ... 
        final String methodName = method.getName();
        final Class targetClass = method.getDeclaringClass();
        
        if ( Stereotype.class.isAssignableFrom(targetClass)  ) {
            if ( methodName.equals("createExtension") ) { //$NON-NLS-1$
                this.updateMap(method,READ_ONLY);
                return READ_ONLY;
            }
        } else if ( ValueSpecification.class.isAssignableFrom(targetClass) ) {
            if ( methodName.equals("stringValue") ) { //$NON-NLS-1$
                this.updateMap(method,READ_ONLY);
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
        return "UML Invocation Factory Helper"; //$NON-NLS-1$
    }
    
    private void updateMap(final Method method, int writableState) {
        this.methodWritableState.put(method,new Integer(writableState));
    }


}
