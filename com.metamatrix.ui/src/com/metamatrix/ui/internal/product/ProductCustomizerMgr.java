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

package com.metamatrix.ui.internal.product;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.ui.UiConstants;
import com.metamatrix.ui.product.DefaultProductCharacteristics;
import com.metamatrix.ui.product.IProductCharacteristics;
import com.metamatrix.ui.product.IProductContext;
import com.metamatrix.ui.product.IProductCustomizer;


/**
 * The <code>ProductCustomizerMgr</code> manages customization of the running product.
 * @since 4.3
 */
public final class ProductCustomizerMgr implements IProductCustomizer,
                                                   UiConstants {
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * i18n properties key prefix.
     * @since 4.3
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(ProductCustomizerMgr.class);

    /**
     * Identifier used when no product is running.
     * @since 4.3
     */
    public static final String DEFAULT_PRODUCT_ID = "noproductapplication"; //$NON-NLS-1$

    public static final DefaultProductCharacteristics DEFAULT_PRODUCT_CHARACTERISTICS = new DefaultProductCharacteristics();

    /**
     * Name used when no product is running.
     * @since 4.3
     */
    public static final String DEFAULT_PRODUCT_NAME = "No Product Application"; //$NON-NLS-1$

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The singleton instance.
     * @since 4.3
     */
    private static ProductCustomizerMgr instance;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Obtains the <code>ProductCustomizerMgr</code> singleton object.
     */
    public static ProductCustomizerMgr getInstance() {
        if (ProductCustomizerMgr.instance == null) {
            ProductCustomizerMgr.instance = new ProductCustomizerMgr();
        }

        return ProductCustomizerMgr.instance;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The actual customizer when a product is running.
     * @since 4.3
     */
    private IProductCustomizer delegate;

    /**
     * The name of the running product.
     * @since 4.3
     */
    private String productName = DEFAULT_PRODUCT_NAME;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Don't allow construction outside of this class.
     */
    private ProductCustomizerMgr() {}

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see com.metamatrix.ui.product.IProductCustomizer#getProductId()
     * @see #DEFAULT_PRODUCT_ID
     * @since 4.3
     */
    public String getProductId() {
        return (this.delegate == null) ? DEFAULT_PRODUCT_ID : this.delegate.getProductId();
    }

    /**
     * Obtains the name of the current product.
     * @return the name
     * @see #DEFAULT_PRODUCT_NAME
     * @since 4.3
     */
    public String getProductName() {
        return this.productName;
    }

    /**
     * @see com.metamatrix.ui.product.IProductCustomizer#loadCustomizations()
     * @since 4.3
     */
    public void loadCustomizations() {
        // establish the running product. no product if running via IDE.
        IProduct product = InternalPlatform.getDefault().getProduct();

        if (product != null) {
            // get the ProductAuthorizer extension point from the plugin class
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(PLUGIN_ID,
                                                                                               ExtensionPoints.ProductCustomizer.ID);

            // get the all extensions to this extension point
            IExtension[] extensions = extensionPoint.getExtensions();

            // make executable extensions for every CLASS_NAME
            for (int i = 0; i < extensions.length; ++i) {
                IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                for (int j = 0; j < elements.length; ++j) {
                    try {
                        Object extension = elements[j].createExecutableExtension(ExtensionPoints.ProductCustomizer.CLASS_NAME);

                        if (extension instanceof IProductCustomizer) {
                            IProductCustomizer customizer = (IProductCustomizer)extension;

                            // find customizer that matches current running product and load it's customizations
                            if (customizer.getProductId().equals(product.getId())) {
                                this.delegate = customizer;
                                this.productName = product.getName();
                                customizer.loadCustomizations();
                                break;
                            }
                        } else {
                            Util.log(IStatus.ERROR, Util.getString(PREFIX + "productCustomizerIncorrectClass", //$NON-NLS-1$
                                                                   extension.getClass().getName()));
                        }
                    } catch (Exception theException) {
                        // problem initializing the IProductCustomizer
                        String msg = Util.getString(PREFIX + "productCustomizerInitializationError", //$NON-NLS-1$
                                                    elements[j].getAttribute(ExtensionPoints.ProductCustomizer.CLASS_NAME));
                        Util.log(IStatus.ERROR, theException, msg);
                        this.delegate = null;
                    }
                }
            }
        }
    }

    /**
     * @see com.metamatrix.ui.product.IProductCustomizer#supports(com.metamatrix.modeler.ui.product.ProductContext)
     * @since 4.3
     */
    public boolean supports(IProductContext theContext) {
        return (this.delegate == null) ? true : this.delegate.supports(theContext);
    }

    /**
     * @see com.metamatrix.ui.product.IProductCustomizer#supports(com.metamatrix.modeler.ui.product.ProductContext, java.lang.Object)
     * @since 4.3
     */
    public boolean supports(IProductContext theContext,
                            Object theValue) {
        return (this.delegate == null) ? true : this.delegate.supports(theContext, theValue);
    }

    public IProductCharacteristics getProductCharacteristics() {
        return  (this.delegate == null) ? DEFAULT_PRODUCT_CHARACTERISTICS : this.delegate.getProductCharacteristics();
    }
}
