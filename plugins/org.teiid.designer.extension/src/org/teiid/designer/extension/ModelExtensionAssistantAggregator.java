/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;


/**
 * 
 *
 * @since 8.0
 */
public class ModelExtensionAssistantAggregator {

    private final ModelExtensionRegistry registry;

    /**
     * @param registry the registry being used to find assistants to aggregate results (cannot be <code>null</code>)
     */
    public ModelExtensionAssistantAggregator( ModelExtensionRegistry registry ) {
        CoreArgCheck.isNotNull(registry, "registry is null"); //$NON-NLS-1$
        this.registry = registry;
    }

    public ModelObjectExtensionAssistant getModelObjectExtensionAssistant( String namespacePrefix ) {
        ModelExtensionAssistant assistant = this.registry.getModelExtensionAssistant(namespacePrefix);

        if (assistant instanceof ModelObjectExtensionAssistant) {
            return (ModelObjectExtensionAssistant)assistant;
        }

        return null;
    }

    /**
     * @param modelObject the model object whose overridden property values are being requested (cannot be <code>null</code>)
     * @return the properties whose default values have been changed (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the overridden property values
     */
    public Properties getOverriddenValues( Object modelObject ) throws Exception {
        Properties props = new Properties();

        for (String namespacePrefix : this.registry.getAllNamespacePrefixes()) {
            ModelObjectExtensionAssistant assistant = getModelObjectExtensionAssistant(namespacePrefix);

            if ((assistant != null) && assistant.supportsMyNamespace(modelObject)) {
                props.putAll(assistant.getOverriddenValues(modelObject));
            }
        }

        return props;
    }

    /**
     * @param modelObject the model objects whose property definitions are being requested (cannot be <code>null</code>)
     * @return the property definitions (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the property definitions
     */
    public Collection<ModelExtensionPropertyDefinition> getPropertyDefinitions( Object modelObject ) throws Exception {
        final String metaclassName = modelObject.getClass().getName();
        final Collection<ModelExtensionAssistant> assistants = this.registry.getModelExtensionAssistants(metaclassName);

        if (assistants.isEmpty()) {
            return Collections.emptyList();
        }

        final Collection<ModelExtensionPropertyDefinition> propDefns = new ArrayList<ModelExtensionPropertyDefinition>();

        for (ModelExtensionAssistant assistant : assistants) {
            if (assistant instanceof ModelObjectExtensionAssistant) {
                propDefns.addAll(((ModelObjectExtensionAssistant)assistant).getPropertyDefinitions(modelObject));
            } else {
                propDefns.addAll(assistant.getModelExtensionDefinition().getPropertyDefinitions(metaclassName));
            }
        }

        return propDefns;
    }

    /**
     * @param modelObject the model object whose property values are being requested (cannot be <code>null</code>)
     * @return the properties (never <code>null</code>)
     * @throws Exception if there is a problem obtaining the properties
     */
    public Properties getPropertyValues( Object modelObject ) throws Exception {
        Properties props = new Properties();

        for (String namespacePrefix : this.registry.getAllNamespacePrefixes()) {
            ModelObjectExtensionAssistant assistant = getModelObjectExtensionAssistant(namespacePrefix);

            if ((assistant != null) && assistant.supportsMyNamespace(modelObject)) {
                props.putAll(assistant.getPropertyValues(modelObject));
            }
        }

        return props;
    }

    /**
     * @param modelObject the model object whose supported namespace prefixes are being requested (cannot be <code>null</code>)
     * @return the namespace prefixes of the MEDs stored in the model (never <code>null</code>)
     * @throws Exception if there is a problem access the model object
     */
    public Collection<String> getSupportedNamespacePrefixes( Object modelObject ) throws Exception {
        ModelObjectExtensionAssistant assistant = ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant();
        return assistant.getSupportedNamespaces(modelObject);
    }

    /**
     * @param file the model file being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the model file contains extension properties
     * @throws Exception if there is a problem determining if the model file has extension properties
     */
    public boolean hasExtensionProperties( File file ) throws Exception {
        for (String namespacePrefix : this.registry.getAllNamespacePrefixes()) {
            ModelObjectExtensionAssistant assistant = getModelObjectExtensionAssistant(namespacePrefix);

            if ((assistant != null) && assistant.hasExtensionProperties(file)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param modelObject the model object being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the model object has extension properties
     * @throws Exception if there is a problem determining if the model object has extension properties
     */
    public boolean hasExtensionProperties( Object modelObject ) throws Exception {
        for (String namespacePrefix : this.registry.getAllNamespacePrefixes()) {
            ModelObjectExtensionAssistant assistant = getModelObjectExtensionAssistant(namespacePrefix);

            if ((assistant != null) && assistant.hasExtensionProperties(modelObject)) {
                return true;
            }
        }

        return false;
    }
}
