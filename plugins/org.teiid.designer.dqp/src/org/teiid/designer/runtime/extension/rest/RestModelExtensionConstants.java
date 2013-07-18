/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.extension.rest;

import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.NamespaceProvider;

/**
 * 
 *
 * @since 8.0
 */
public interface RestModelExtensionConstants {
	
    /**
     *
     */
    interface METHODS {
    	String GET = "GET"; //$NON-NLS-1$
    	String PUT = "PUT"; //$NON-NLS-1$
    	String POST = "POST"; //$NON-NLS-1$
    	String DELETE = "DELETE"; //$NON-NLS-1$
    }
    
    /**
     * 
     */
    String[] METHODS_ARRAY = {METHODS.GET, METHODS.PUT, METHODS.POST, METHODS.DELETE };

    /**
     * 
     */
    NamespaceProvider NAMESPACE_PROVIDER = new NamespaceProvider() {

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespacePrefix()
         */
        @Override
        public String getNamespacePrefix() {
            return "REST"; //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespaceUri()
         */
        @Override
        public String getNamespaceUri() {
            return "http://www.jboss.org/teiiddesigner/ext/rest/2012"; //$NON-NLS-1$
        }
    };

    /**
     * The fully qualified extension property definition identifiers.
     */
    interface PropertyIds {

        /**
         * The property definition identifer for the rest method.
         */
        String REST_METHOD = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "METHOD"); //$NON-NLS-1$

        /**
         * The property definition identifier for the URI.
         */
        String URI = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "URI"); //$NON-NLS-1$
        
        /**
         * The property definition identifier for the CHARSET.
         * @since 8.1
         */
        String CHARSET = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "charset"); //$NON-NLS-1$
        
        /**
         * The property definition identifier for the Headers.
         * @since 8.1
         */
        String HEADERS = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "headers"); //$NON-NLS-1$
    }

}
