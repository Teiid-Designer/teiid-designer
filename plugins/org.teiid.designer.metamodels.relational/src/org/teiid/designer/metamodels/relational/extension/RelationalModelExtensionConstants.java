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
        String JAVA_CLASS = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "java-class"); //$NON-NLS-1$
        String JAVA_METHOD = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "java-method"); //$NON-NLS-1$
        String FUNCTION_CATEGORY = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "function-category"); //$NON-NLS-1$
        String UDF_JAR_PATH = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "udfJarPath"); //$NON-NLS-1$
        String NATIVE_TYPE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "native_type"); //$NON-NLS-1$
        String GLOBAL_TEMP_TABLE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "global-temp-table"); //$NON-NLS-1$
        String ALLOW_MATVIEW_MANAGEMENT = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "ALLOW_MATVIEW_MANAGEMENT"); //$NON-NLS-1$
        String MATVIEW_STATUS_TABLE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MATVIEW_STATUS_TABLE"); //$NON-NLS-1$
        String MATVIEW_BEFORE_LOAD_SCRIPT = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MATVIEW_BEFORE_LOAD_SCRIPT"); //$NON-NLS-1$
        String MATVIEW_LOAD_SCRIPT = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MATVIEW_LOAD_SCRIPT"); //$NON-NLS-1$
        String MATVIEW_AFTER_LOAD_SCRIPT = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MATVIEW_AFTER_LOAD_SCRIPT"); //$NON-NLS-1$
        String MATVIEW_SHARE_SCOPE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MATVIEW_SHARE_SCOPE"); //$NON-NLS-1$
        String MATERIALIZED_STAGE_TABLE = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MATERIALIZED_STAGE_TABLE"); //$NON-NLS-1$
        String ON_VDB_START_SCRIPT = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "ON_VDB_START_SCRIPT"); //$NON-NLS-1$
        String ON_VDB_DROP_SCRIPT = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "ON_VDB_DROP_SCRIPT"); //$NON-NLS-1$
        String MATVIEW_ONERROR_ACTION = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MATVIEW_ONERROR_ACTION"); //$NON-NLS-1$
        String MATVIEW_TTL = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PROVIDER, "MATVIEW_TTL"); //$NON-NLS-1$
    }
    
    interface PropertyKeysNoPrefix {
        String JAVA_CLASS = "java-class"; //$NON-NLS-1$
        String JAVA_METHOD = "java-method"; //$NON-NLS-1$
        String FUNCTION_CATEGORY = "function-category"; //$NON-NLS-1$
        String UDF_JAR_PATH = "udfJarPath"; //$NON-NLS-1$
        String VARARGS = "varargs"; //$NON-NLS-1$
        String NULL_ON_NULL= "null-on-null"; //$NON-NLS-1$
        String DETERMINISTIC= "deterministic"; //$NON-NLS-1$
        String AGGREGATE= "aggregate"; //$NON-NLS-1$
        
        String ALLOWS_ORDER_BY = "allows-orderby"; //$NON-NLS-1$
        String ALLOWS_DISTINCT = "allows-distinct"; //$NON-NLS-1$
        String ANALYTIC = "analytic"; //$NON-NLS-1$
        String DECOMPOSABLE= "decomposable"; //$NON-NLS-1$
        String NATIVE_QUERY = "native-query"; //$NON-NLS-1$
        String NON_PREPARED = "non-prepared"; //$NON-NLS-1$
        String USES_DISTINCT_ROWS = "uses-distinct-rows"; //$NON-NLS-1$
        String ALLOW_JOIN = "allow-join"; //$NON-NLS-1$
        String NATIVE_TYPE = "native_type"; //$NON-NLS-1$
        String GLOBAL_TEMP_TABLE = "global-temp-table"; //$NON-NLS-1$
        String ALLOW_MATVIEW_MANAGEMENT = "ALLOW_MATVIEW_MANAGEMENT"; //$NON-NLS-1$
        String MATVIEW_STATUS_TABLE = "MATVIEW_STATUS_TABLE"; //$NON-NLS-1$
        String MATVIEW_BEFORE_LOAD_SCRIPT = "MATVIEW_BEFORE_LOAD_SCRIPT"; //$NON-NLS-1$
        String MATVIEW_LOAD_SCRIPT = "MATVIEW_LOAD_SCRIPT"; //$NON-NLS-1$
        String MATVIEW_AFTER_LOAD_SCRIPT = "MATVIEW_AFTER_LOAD_SCRIPT"; //$NON-NLS-1$
        String MATVIEW_SHARE_SCOPE = "MATVIEW_SHARE_SCOPE"; //$NON-NLS-1$
        String MATERIALIZED_STAGE_TABLE = "MATERIALIZED_STAGE_TABLE"; //$NON-NLS-1$
        String ON_VDB_START_SCRIPT = "ON_VDB_START_SCRIPT"; //$NON-NLS-1$
        String ON_VDB_DROP_SCRIPT = "ON_VDB_DROP_SCRIPT"; //$NON-NLS-1$
        String MATVIEW_ONERROR_ACTION = "MATVIEW_ONERROR_ACTION"; //$NON-NLS-1$
        String MATVIEW_TTL = "MATVIEW_TTL"; //$NON-NLS-1$
    }

}