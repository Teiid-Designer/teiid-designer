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
         * The property definition identifier for the LinkTables.
         * @since 8.3
         */
        String LINK_TABLES = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "LinkTables"); //$NON-NLS-1$

        /**
         * The property definition identifier for the HttpMethod.
         * @since 8.3
         */
        String HTTP_METHOD = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "HttpMethod"); //$NON-NLS-1$

        /**
         * The property definition identifier for the JoinColumn.
         * @since 8.3
         */
        String JOIN_COLUMN = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "JoinColumn"); //$NON-NLS-1$

        /**
         * The property definition identifier for the EntityType.
         * @since 8.2
         */
        String ENTITY_TYPE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "EntityType"); //$NON-NLS-1$

        /**
         * The property definition identifier for the ComplexType.
         * @since 8.3
         */
        String COMPLEX_TYPE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "ComplexType"); //$NON-NLS-1$

        /**
         * The property definition identifier for the ColumnGroup.
         * @since 8.3
         */
        String COLUMN_GROUP = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "ColumnGroup"); //$NON-NLS-1$

    }

}
