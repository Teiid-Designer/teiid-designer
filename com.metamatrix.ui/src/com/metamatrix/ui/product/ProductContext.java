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

import com.metamatrix.core.util.ArgCheck;


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
