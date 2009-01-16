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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.UiConstants;



/**
 * The <code>AbstractProductCustomizer</code> can be used as a base class for an
 * {@link com.metamatrix.ui.product.IProductCustomizer}.
 * @since 4.3
 */
public abstract class AbstractProductCustomizer implements IProductCustomizer,
                                                           UiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Constant used to indicate all values of a context.
     */
    private static final List ALL_VALUES = Collections.singletonList(new Object());
    
    /**
     * i18n properties key prefix.
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(AbstractProductCustomizer.class);
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Stores supported context values for the product. Key=ProductContext, Value=Set
     */
    protected Map supportedMap = new HashMap();
    
    /**
     * Stores unsupported context values for the product. Key=ProductContext, Value=Set
     */
    protected Map unsupportedMap = new HashMap();
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Adds support for all values of the specified context. If an unsupported value for this context
     * has previously been added then support is not added.
     * @param theContext the context whose values are all supported
     * @return <code>true</code> if support succeeds; <code>false</code> otherwise
     */
    protected boolean addContextSupport(IProductContext theContext) {
        boolean result = true;
        
        if (!this.unsupportedMap.containsKey(theContext)) {
            this.supportedMap.put(theContext, ALL_VALUES);
        } else {
            result = false;
            Util.log(IStatus.WARNING, getString(PREFIX, "addContextSupportFailed", new Object[] {theContext})); //$NON-NLS-1$
        }
        
        return result;
    }
    
    /**
     * Adds support for a value of the specified context. If an unsupported value for this context
     * has previously been added then support is not added.
     * @param theContext the context whose value is being supported
     * @param theValue the value being supported
     * @return <code>true</code> if support succeeds; <code>false</code> otherwise
     */
    protected boolean addContextValueSupport(IProductContext theContext,
                                             Object theValue) {
        boolean result = true;
        
        if (!this.unsupportedMap.containsKey(theContext)) {
            Set values = (Set)this.supportedMap.get(theContext);
            
            if (values == null) {
                values = new HashSet();
                this.supportedMap.put(theContext, values);
            }
            
            // only add if not already supporting all values
            if (!values.equals(ALL_VALUES)) {
                values.add(theValue);
            }
        } else {
            result = false;
            Util.log(IStatus.WARNING, getString(PREFIX, "addContextValueSupportFailed", new Object[] {theValue, theContext})); //$NON-NLS-1$
        }
        
        return result;
    }
    
    /**
     * Removes support for all values of the specified context. If a supported value for this context
     * has previously been added then removing support fails.
     * @param theContext the context who is no longer being supported
     * @return <code>true</code> if removing support succeeds; <code>false</code> otherwise
     */
    protected boolean removeContextSupport(IProductContext theContext) {
        boolean result = true;
        
        if (!this.supportedMap.containsKey(theContext)) {
            this.unsupportedMap.put(theContext, ALL_VALUES);
        } else {
            result = false;
            Util.log(IStatus.WARNING, getString(PREFIX, "removeContextSupportFailed", new Object[] {theContext})); //$NON-NLS-1$
        }
        
        return result;
    }
    
    /**
     * Removes support for a context value. If a supported value for this context has previously been added then removing
     * support fails.
     * @param theContext the context whose value is being removed
     * @param theValue the value no longer supported
     * @return <code>true</code> if removing support succeeds; <code>false</code> otherwise
     */
    protected boolean removeContextValueSupport(IProductContext theContext,
                                                Object theValue) {
        boolean result = true;
        
        if (!this.supportedMap.containsKey(theContext)) {
            Set values = (Set)this.unsupportedMap.get(theContext);
            
            if (values == null) {
                values = new HashSet();
                this.unsupportedMap.put(theContext, values);
            }
            
            // only add if not already unsupporting all values
            if (!values.equals(ALL_VALUES)) {
                values.add(theValue);
            }
        } else {
            result = false;
            Util.log(IStatus.WARNING, getString(PREFIX, "removeContextValueSupportFailed", new Object[] {theValue, theContext})); //$NON-NLS-1$
        }
        
        return result;
    }
    
    /**
     * Helper method to obtain i18n properties values. 
     * @param thePrefix the key prefix
     * @param theKey the key
     * @return the value
     * @since 4.3
     */
    protected String getString(String thePrefix,
                               String theKey) {
        return Util.getStringOrKey(thePrefix + theKey);
    }
    
    /**
     * Helper method to obtain i18n properties values. 
     * @param thePrefix the key prefix
     * @param theKey the key
     * @param theParams the parameters to substitute in the i18n properties value
     * @return the value
     * @since 4.3
     */
    protected String getString(String thePrefix,
                               String theKey,
                               Object[] theParams) {
        return Util.getString(thePrefix + theKey, theParams);
    }
    
    /** 
     * @see com.metamatrix.ui.product.IProductCustomizer#supports(com.metamatrix.ui.product.IProductContext)
     * @since 4.3
     */
    public boolean supports(IProductContext theContext) {
        boolean result = true;
        
        Collection values = (Collection)this.supportedMap.get(theContext);
        
        if (values != null) {
            result = (values.equals(ALL_VALUES));
        } else if (this.unsupportedMap.get(theContext) != null) {
            result = false;
        }
        
        return result;
    }

    /** 
     * @see com.metamatrix.ui.product.IProductCustomizer#supports(com.metamatrix.ui.product.IProductContext, java.lang.Object)
     * @since 4.3
     */
    public boolean supports(IProductContext theContext,
                            Object theValue) {
        boolean result = true;
        
        Collection values = (Collection)this.supportedMap.get(theContext);
        
        if (values == null) {
            values = (Collection)this.unsupportedMap.get(theContext);
            
            if (values != null) {
                if (values.equals(ALL_VALUES)) {
                    result = false;
                } else {
                    result = !values.contains(theValue);
                }
            }
        } else if (!values.equals(ALL_VALUES)) {
            result = values.contains(theValue);
        }
        
        return result;
    }

}
