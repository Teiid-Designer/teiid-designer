/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.ui.product;

import com.metamatrix.core.modeler.util.ArgCheck;


/**
 * Basic implementation of an <code>IProductContext</code>. 
 * @since 4.4
 */
public class ProductContext implements IProductContext {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private String category;
    
    private String id;
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constructs a product context.
     * @param theCategory the category
     * @param theContextId the identifier
     * @throws IllegalArgumentException if an input parameter is <code>null</code> or empty
     * 
     */
    public ProductContext(String theCategory,
                                 String theContextId) {
        ArgCheck.isNotEmpty(theCategory);
        ArgCheck.isNotEmpty(theContextId);

        this.category = theCategory;
        this.id = theContextId;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see com.metamatrix.ui.product.IProductContext#getId()
     * @since 4.4
     */
    public String getId() {
        return new StringBuffer().append(this.category).append('.').append(this.id).toString();
    }
    
    /** 
     * @see java.lang.Object#toString()
     * @since 4.4
     */
    @Override
    public String toString() {
        return getId();
    }

}
