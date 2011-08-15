/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.extension.rest;

import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;

/**
 * 
 */
public interface RestModelExtensionConstants {

    /**
     * The namespace prefix for the REST extension properties.
     */
    String NAMESPACE_PREFIX = "rest"; //$NON-NLS-1$

    /**
     * The fully qualified extension property definition identifiers.
     */
    interface PropertyIds {

        /**
         * The property definition identifer for the rest method.
         */
        String REST_METHOD = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PREFIX, "restMethod"); //$NON-NLS-1$

        /**
         * The property definition identifier for the URI.
         */
        String URI = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PREFIX, "uri"); //$NON-NLS-1$
    }

}
