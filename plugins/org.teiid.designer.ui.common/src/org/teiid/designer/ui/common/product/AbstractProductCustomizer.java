/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.product;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.teiid.core.util.I18nUtil;
import org.teiid.designer.ui.common.UiConstants;



/**
 * The <code>AbstractProductCustomizer</code> can be used as a base class for an
 * {@link org.teiid.designer.ui.common.product.IProductCustomizer}.
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
    private static final Set ALL_VALUES = Collections.singleton(new Object());
    
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
     * @see org.teiid.designer.ui.common.product.IProductCustomizer#supports(org.teiid.designer.ui.common.product.IProductContext)
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
     * @see org.teiid.designer.ui.common.product.IProductCustomizer#supports(org.teiid.designer.ui.common.product.IProductContext, java.lang.Object)
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
