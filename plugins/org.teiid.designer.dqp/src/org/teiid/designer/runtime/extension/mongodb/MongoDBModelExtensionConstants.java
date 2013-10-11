/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.extension.mongodb;

import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.NamespaceProvider;

/**
 *
 */
public interface MongoDBModelExtensionConstants {
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
			return "mongodb"; //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespaceUri()
		 */
		@Override
		public String getNamespaceUri() {
			return "http://www.jboss.org/teiiddesigner/ext/mongodb/2012"; //$NON-NLS-1$
		}
	};

	/**
	 * The fully qualified extension property definition identifiers.
	 */
	interface PropertyIds {

		/**
		 * The property definition identifier for the MERGE.
		 * 
		 * @since 8.3
		 */
		String MERGE = ModelExtensionPropertyDefinition.Utils
				.getPropertyId(NAMESPACE_PROVIDER, "MERGE"); //$NON-NLS-1$

		/**
		 * The property definition identifier for the EMBEDDABLE.
		 * 
		 * @since 8.3
		 */
		String EMBEDDABLE = ModelExtensionPropertyDefinition.Utils
				.getPropertyId(NAMESPACE_PROVIDER, "EMBEDDABLE"); //$NON-NLS-1$

	}

}
