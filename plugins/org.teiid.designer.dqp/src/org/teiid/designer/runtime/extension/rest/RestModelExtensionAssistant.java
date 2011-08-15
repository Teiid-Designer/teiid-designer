/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.extension.rest;

import static org.teiid.designer.runtime.extension.rest.RestModelExtensionConstants.NAMESPACE_PREFIX;

import org.teiid.designer.core.extension.ModelObjectExtensionAssistant;

/**
 * 
 */
public class RestModelExtensionAssistant extends ModelObjectExtensionAssistant {

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#getNamespacePrefix()
     */
    @Override
    public String getNamespacePrefix() {
        return NAMESPACE_PREFIX;
    }

}
