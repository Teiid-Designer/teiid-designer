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
 * @since 8.0
 */
public interface RelationalModelExtensionConstants {

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
            return "relational"; //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespaceUri()
         */
        @Override
        public String getNamespaceUri() {
            return "http://www.jboss.org/teiiddesigner/ext/relational/2012"; //$NON-NLS-1$
        }
    };

    /**
     * The fully qualified extension property definition identifiers.
     */
    interface PropertyIds {

        /**
         * The property definition identifer for the deterministic boolean property.
         */
        String DETERMINISTIC = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "deterministic"); //$NON-NLS-1$
        String NATIVE_QUERY = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "native-query"); //$NON-NLS-1$
        String NON_PREPARED = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "non-prepared"); //$NON-NLS-1$

    }

}