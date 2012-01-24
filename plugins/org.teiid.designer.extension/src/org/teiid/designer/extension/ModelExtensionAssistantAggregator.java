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

import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * 
 */
public class ModelExtensionAssistantAggregator {

    private final ModelExtensionRegistry registry;

    public ModelExtensionAssistantAggregator( ModelExtensionRegistry registry ) {
        CoreArgCheck.isNotNull(registry, "registry is null"); //$NON-NLS-1$
        this.registry = registry;
    }

    private ModelObjectExtensionAssistant getModelObjectExtensionAssistant( String namespacePrefix ) {
        ModelExtensionAssistant assistant = this.registry.getModelExtensionAssistant(namespacePrefix);

        if (assistant instanceof ModelObjectExtensionAssistant) {
            return (ModelObjectExtensionAssistant)assistant;
        }

        return null;
    }

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

    public Collection<ModelExtensionPropertyDefinition> getPropertyDefinitions( Object modelObject ) throws Exception {
        Collection<String> supportedNamespacePrefixes = getSupportedNamespacePrefixes(modelObject);

        if (supportedNamespacePrefixes.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<ModelExtensionPropertyDefinition> propDefns = new ArrayList<ModelExtensionPropertyDefinition>();
        String metaclassName = modelObject.getClass().getName();

        // only return properties that have namespace that is both supported by model and registered in the registry
        for (String namespacePrefix : supportedNamespacePrefixes) {
            ModelExtensionAssistant assistant = this.registry.getModelExtensionAssistant(namespacePrefix);

            if (assistant != null) {
                propDefns.addAll(assistant.getModelExtensionDefinition().getPropertyDefinitions(metaclassName));
            }
        }

        return propDefns;
    }

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

    public boolean hasExtensionProperties( File file ) throws Exception {
        for (String namespacePrefix : this.registry.getAllNamespacePrefixes()) {
            ModelObjectExtensionAssistant assistant = getModelObjectExtensionAssistant(namespacePrefix);

            if ((assistant != null) && assistant.hasExtensionProperties(file)) {
                return true;
            }
        }

        return false;
    }

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
