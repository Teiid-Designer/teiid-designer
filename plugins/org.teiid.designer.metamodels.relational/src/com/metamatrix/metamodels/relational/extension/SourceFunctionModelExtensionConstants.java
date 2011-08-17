/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.extension;

import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;

public interface SourceFunctionModelExtensionConstants {

    /**
     * The namespace prefix for the Flat File extension properties.
     */
    String NAMESPACE_PREFIX = "sourcefunction"; //$NON-NLS-1$

    /**
     * The fully qualified extension property definition identifiers.
     */
    interface PropertyIds {

        /**
         * The property definition identifer for the deterministic boolean property.
         */
        String DETERMINISTIC = ModelExtensionPropertyDefinition.Utils.getPropertyId(NAMESPACE_PREFIX, "deterministic"); //$NON-NLS-1$

    }

}