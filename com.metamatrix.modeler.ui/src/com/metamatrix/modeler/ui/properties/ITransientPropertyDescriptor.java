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
