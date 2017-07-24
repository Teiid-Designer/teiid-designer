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

public interface InfinispanHotrodModelExtensionConstants {

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
            return "infinispan-hotrod"; //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespaceUri()
         */
        @Override
        public String getNamespaceUri() {
            return "http://www.teiid.org/translator/infinispan/2017"; //$NON-NLS-1$
        }
    };

    /**
     * The fully qualified extension property definition identifiers.
     */
    interface PropertyIds {
        String CACHE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "CACHE"); //$NON-NLS-1$
        String MERGE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MERGE"); //$NON-NLS-1$
        String MESSAGE_NAME = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MESSAGE_NAME"); //$NON-NLS-1$
        String PARENT_COLUMN_NAME = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "PARENT_COLUMN_NAME"); //$NON-NLS-1$
        String PARENT_TAG = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "PARENT_TAG"); //$NON-NLS-1$
        String PSEUDO = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "PSEUDO"); //$NON-NLS-1$
        String TAG = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "TAG"); //$NON-NLS-1$
    }

}