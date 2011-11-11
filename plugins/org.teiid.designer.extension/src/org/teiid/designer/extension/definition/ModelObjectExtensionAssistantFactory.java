/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

/**
 * A <code>ModelObjectExtensionAssistantFactory</code> creates <code>ModelObjectExtensionAssistant</code> for a specific type of
 * model object.
 */
public interface ModelObjectExtensionAssistantFactory {

    /**
     * @return the assistant (never <code>null</code>)
     */
    ModelObjectExtensionAssistant createAssistant();

    /**
     * @return the model object type (never <code>null</code>)
     */
    String getModelObjectType();

}
