/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.extension;

import static com.metamatrix.metamodels.relational.extension.SourceFunctionModelExtensionConstants.NAMESPACE_PREFIX;

import org.teiid.designer.core.extension.ModelObjectExtensionAssistant;

public class SourceFunctionModelExtensionAssistant extends ModelObjectExtensionAssistant {

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
