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
    teiid_mongo:EMBEDDABLE - Means that data defined in this table is allowed to be included as an "embeddable" document 
    in any parent document. The parent document is referenced by the foreign key relationships. In this scenario, 
    Teiid maintains more than one copy of the data in MongoDB store, one in its own collection and also a copy in 
    each of the parent tables that have relationship to this table. You can even nest embeddable table inside another
    embeddable table with some limitations. Use this property on table, where table can exist, encompass all its 
    relations on its own. For example, a "Category" table that defines a "Product"'s category is independent of Product, 
    which can be embeddable in "Products" table.
    
    teiid_mongo:MERGE - Means that data of this table is merged with the defined parent table. There is only a single 
    copy of the data that is embedded in the parent document. Parent document is defined using the foreign key relationships.
	
	PLEASE NOTE:
	 A given table can contain either the "teiid_mongo:EMBEDDABLE" property or the "teiid_mongo:MERGE" property 
	 defining the type of nesting in MongoDB. A TABLE IS NOT ALLOWED TO HAVE BOTH PROPERTIES
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
