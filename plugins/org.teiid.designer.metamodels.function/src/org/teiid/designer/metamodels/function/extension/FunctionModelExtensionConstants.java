/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.metamodels.function.extension;

import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.NamespaceProvider;

/**
 * @since 8.0
 *
 */
public interface FunctionModelExtensionConstants {

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
            return "function"; //$NON-NLS-1$
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.properties.NamespaceProvider#getNamespaceUri()
         */
        @Override
        public String getNamespaceUri() {
            return "http://www.jboss.org/teiiddesigner/ext/function/2012"; //$NON-NLS-1$
        }
    };

    /**
     * The fully qualified extension property definition identifiers.
     */
    interface PropertyIds {

        /**
         * The property definition identifer for the deterministic boolean property.
         */
        String UDF_JAR_PATH = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "udfJarPath"); //$NON-NLS-1$
        String AGGREGATE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "aggregate"); //$NON-NLS-1$
        String ANALYTIC = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "analytic"); //$NON-NLS-1$
        String ALLOWS_ORDERBY = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "allows-orderby"); //$NON-NLS-1$
        String USES_DISTINCT_ROWS = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "uses-distinct-rows"); //$NON-NLS-1$
        String ALLOWS_DISTINCT = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "allows-distinct"); //$NON-NLS-1$
        String DECOMPOSABLE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "decomposable"); //$NON-NLS-1$

    }

}
