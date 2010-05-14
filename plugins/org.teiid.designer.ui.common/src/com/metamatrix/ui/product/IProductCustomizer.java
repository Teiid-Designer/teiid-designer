/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.product;

/**
 * An <code>IProductCustomizer</code> provides the ability to customize the Teiid Designer product.
 * 
 * @since 4.3
 */
public interface IProductCustomizer {

    /**
     * Obtains the product identifier.
     * 
     * @return the ID (never <code>null</code>)
     * @since 4.3
     */
    String getProductId();

    /**
     * Informs the customizer to load it's product customizations.
     * 
     * @since 4.3
     */
    void loadCustomizations();

    /**
     * Indicates if the product supports the specified context.
     * 
     * @param theContext the context being checked
     * @return <code>true</code> if supported; <code>false</code> otherwise.
     * @throws IllegalArgumentException if an input parameter is <code>null</code>
     * @since 4.3
     */
    boolean supports( IProductContext theContext );

    /**
     * Indicates if the product supports the specified value in the specified context.
     * 
     * @param theContext the context being checked
     * @param theValue the value being checked
     * @return <code>true</code> if supported; <code>false</code> otherwise.
     * @throws IllegalArgumentException if an input parameter is <code>null</code>
     * @since 4.3
     */
    boolean supports( IProductContext theContext,
                      Object theValue );

    /**
     * Obtains the product's primary navigation view id. This is required by some actions so they can programmatically set
     * selection or reveal specific objects... typically in a tree view. The default will be eclipse's resource navigator.
     * 
     * @return the ID
     * @since 4.3
     */
    IProductCharacteristics getProductCharacteristics();

}
