/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.product;

import org.teiid.core.designer.util.CoreArgCheck;


/**
 * Basic implementation of an <code>IProductContext</code>. 
 * @since 8.0
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
        CoreArgCheck.isNotEmpty(theCategory);
        CoreArgCheck.isNotEmpty(theContextId);

        this.category = theCategory;
        this.id = theContextId;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * @see org.teiid.designer.ui.common.product.IProductContext#getId()
     * @since 4.4
     */
    @Override
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
