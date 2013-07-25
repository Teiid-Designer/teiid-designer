/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.extension.odata;

import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.NamespaceProvider;

/**
 * ODataModelExtensionConstants
 * The constants for OData ModelExtension properties
 * @since 8.2
 */
public interface ODataModelExtensionConstants {

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
            return "odata"; //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespaceUri()
         */
        @Override
        public String getNamespaceUri() {
            return "http://www.jboss.org/teiiddesigner/ext/odata/2012"; //$NON-NLS-1$
        }
    };

    /**
     * The fully qualified extension property definition identifiers.
     */
    interface PropertyIds {

        /**
         * The property definition identifier for the entityalias.
         * @since 8.2
         */
        String ENTITY_ALIAS = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "EntityAlias"); //$NON-NLS-1$
        
        /**
         * The property definition identifier for the entitytype.
         * @since 8.2
         */
        String ENTITY_TYPE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "EntityType"); //$NON-NLS-1$
    }

}
