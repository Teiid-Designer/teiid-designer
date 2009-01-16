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

package com.metamatrix.ui.product;

/** 
 * An <code>IProductCustomizer</code> provides the ability to customize the MetaMatrix Modeler product.
 * @since 4.3
 */
public interface IProductCustomizer {
    
    /**
     * Obtains the product identifier. 
     * @return the ID (never <code>null</code>)
     * @since 4.3
     */
    String getProductId();
    
    /**
     * Informs the customizer to load it's product customizations. 
     * @since 4.3
     */
    void loadCustomizations();
    
    /**
     * Indicates if the product supports the specified context. 
     * @param theContext the context being checked
     * @return <code>true</code> if supported; <code>false</code> otherwise.
     * @throws IllegalArgumentException if an input parameter is <code>null</code>
     * @since 4.3
     */
    boolean supports(IProductContext theContext);
    
    /**
     * Indicates if the product supports the specified value in the specified context. 
     * @param theContext the context being checked
     * @param theValue the value being checked
     * @return <code>true</code> if supported; <code>false</code> otherwise.
     * @throws IllegalArgumentException if an input parameter is <code>null</code>
     * @since 4.3
     */
    boolean supports(IProductContext theContext, Object theValue);
    
    
    /**
     * Obtains the product's primary navigation view id.
     * This is required by some actions so they can programmatically set selection or reveal specific objects... typically
     * in a tree view.  The default will be eclipse's resource navigator.
     * @return the ID
     * @since 4.3
     */
    IProductCharacteristics getProductCharacteristics();
    
    
}
