/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.extension;

import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistantFactory;

/**
 * 
 */
public class EmfModelExtensionAssistantFactory implements ModelObjectExtensionAssistantFactory {

    /**
     * The model object assistant type created. Value is {@value} .
     */
    public static final String MODEL_OBJECT_TYPE = "EMF"; //$NON-NLS-1$

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistantFactory#createAssistant()
     */
    @Override
    public ModelObjectExtensionAssistant createAssistant() {
        return new EmfModelObjectExtensionAssistant();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistantFactory#getModelObjectType()
     */
    @Override
    public String getModelObjectType() {
        return MODEL_OBJECT_TYPE;
    }

}
