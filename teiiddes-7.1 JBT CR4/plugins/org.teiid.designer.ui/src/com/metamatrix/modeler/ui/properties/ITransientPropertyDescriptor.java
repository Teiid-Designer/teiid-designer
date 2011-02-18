/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ui.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;


/** 
 * The <code>ITransientPropertyDescriptor</code> is an <code>IPropertyDescriptor</code> whose property is not
 * persisted and is not found in the model definition.
 * @since 4.3
 * @see com.metamatrix.modeler.internal.ui.properties.ModelObjectPropertySource
 */
public interface ITransientPropertyDescriptor extends IPropertyDescriptor {
    /**
     * Obtains the property value.
     * @return the value
     * @throws IllegalStateException if the associated object is <code>null</code>
     * @since 4.3
     * @see #setObject(Object)
     */
    Object getPropertyValue() throws IllegalStateException;
    
    /**
     * Set the object associated with this property descriptor.  
     * @param theObject the object
     * @throws IllegalArgumentException if object is not supported
     * @since 4.3
     * @see #supports(Object)
     */
    void setObject(Object theObject) throws IllegalStateException;
    
    /** 
     * Indicates if the specified object is supported by this descriptor.
     * @param theObject the object being checked
     * @return <code>true</code>if supported; <code>false</code> otherwise.
     * @since 4.3
     */
    boolean supports(Object theObject);
}
