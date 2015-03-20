/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.extension;

import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.NamespaceProvider;

/**
 * 
 *
 * @since 8.0
 */
public interface RestModelExtensionConstants {
	String MED_PROBLEM_MARKER_ID = "org.teiid.designer.extension.ui.medMarker";  //$NON-NLS-1$
	
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
   interface CHARSETS {
   	String UDF8 = "UTF-8"; //$NON-NLS-1$
   	String USASCII = "US-ASCII"; //$NON-NLS-1$
   }
   
   /**
    * 
    */
   String[] CHARSETS_ARRAY = {CHARSETS.UDF8, CHARSETS.USASCII};

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
        
        /**
         * The property definition identifier for the Description.
         * @since 8.1
         */
        String DESCRIPTION = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "DESCRIPTION"); //$NON-NLS-1$
    
    }
    
    interface OLD_REST_INFO {
    	String OLD_REST_NAMESPACE_PREFIX = "rest";
    	String OLD_REST_PREFIX = "rest:";
    	String OLD_REST_METHOD_KEY = "rest:restMethod";
    	String OLD_REST_URI_KEY = "rest:uri";  //$NON-NLS-1$
    }

}
