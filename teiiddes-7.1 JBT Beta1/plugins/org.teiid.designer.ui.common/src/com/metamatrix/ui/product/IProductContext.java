/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.product;


/**
 * A product context is used to identify areas supported by an application. Usually only used when a
 * feature or parts of a feature are being removed.
 * @since 4.4
 */
public interface IProductContext {

    /**
     * Obtains the unique identifier of the product context. 
     * @return the ID (never <code>null</code>)
     * @since 4.4
     */
    String getId();

}
