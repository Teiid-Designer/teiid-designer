/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.product;

/**
 * Collection of Modeler product contexts used to determine if features are supported by the current
 * application.
 * @since 4.4
 */
public interface IProductContexts {
    
    /**
     * A list of categories used in defining the product contexts. Not visible outside the interface. 
     * @since 4.3
     */
    class Categories {
        /**
         * Product context category.
         * @since 4.4
         */
        private static final String PRODUCT = "product."; //$NON-NLS-1$
    }
    
    interface Product {
        /**
         * This context can be used to determine if a product is an IDE Application.
         * @since 4.4
         */
        IProductContext IDE_APPLICATION = new ProductContext(Categories.PRODUCT, "ideApplication"); //$NON-NLS-1$
    }
    
}
