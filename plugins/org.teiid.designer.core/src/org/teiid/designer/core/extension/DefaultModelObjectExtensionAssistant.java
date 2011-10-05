/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.extension;

import org.teiid.designer.extension.definition.ModelExtensionDefinitionHeader;

/**
 * Default implementation of the ModelObjectExtensionAssistant.
 */
public class DefaultModelObjectExtensionAssistant extends ModelObjectExtensionAssistant {

    /*
     * constructor
     */
    public DefaultModelObjectExtensionAssistant( String namespacePrefix,
                                                 String namespaceUri,
                                                 String metamodelUri ) {
        createModelExtensionDefinition(namespacePrefix, namespaceUri, metamodelUri);
    }

    /*
     * constructor
     */
    public DefaultModelObjectExtensionAssistant( ModelExtensionDefinitionHeader medHeader ) {
        createModelExtensionDefinition(medHeader.getNamespacePrefix(), medHeader.getNamespaceUri(), medHeader.getMetamodelUri());
        setDefinitionDescription(medHeader.getDescription());
        setDefinitionVersion(String.valueOf(medHeader.getVersion()));
    }

}
