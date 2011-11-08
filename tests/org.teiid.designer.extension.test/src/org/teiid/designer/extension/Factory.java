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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionAssistantAdapter;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionWriter;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinitionImpl;
import org.teiid.designer.extension.properties.NamespacePrefixProvider;
import org.teiid.designer.extension.properties.Translation;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;

/**
 * 
 */
public class Factory implements Constants {

    public static ModelExtensionAssistant createAssistant() {
        return new ModelExtensionAssistantAdapter();
    }

    public static Set<Locale> createDefaultLocales() {
        Set<Locale> locales = new HashSet<Locale>(DEFAULT_LOCALES.length);
        locales.addAll(Arrays.asList(DEFAULT_LOCALES));
        return locales;
    }

    public static NamespacePrefixProvider createDefaultNamespacePrefixProvider() {
        return new NamespacePrefixProvider() {

            @Override
            public String getNamespacePrefix() {
                return DEFAULT_NAMESPACE_PREFIX;
            }
        };
    }

    public static Set<String> createDefaultStringAllowedValues() {
        Set<String> allowedValues = new HashSet<String>(DEFAULT_STRING_ALLOWED_VALUES.length);
        allowedValues.addAll(Arrays.asList(DEFAULT_STRING_ALLOWED_VALUES));
        return allowedValues;
    }

    public static ModelExtensionPropertyDefinition createDefaultPropertyDefinition() {
        ModelExtensionDefinition med = createDefinitionWithOneMetaclassAndNoPropertyDefinitions();
        return new ModelExtensionPropertyDefinitionImpl(med,
                                                        DEFAULT_SIMPLE_ID,
                                                        DEFAULT_RUNTIME_TYPE,
                                                        Boolean.toString(ModelExtensionPropertyDefinition.REQUIRED_DEFAULT),
                                                        null,
                                                        null,
                                                        Boolean.toString(ModelExtensionPropertyDefinition.ADVANCED_DEFAULT),
                                                        Boolean.toString(ModelExtensionPropertyDefinition.MASKED_DEFAULT),
                                                        Boolean.toString(ModelExtensionPropertyDefinition.INDEX_DEFAULT),
                                                        Utils.getStringAllowedValues(),
                                                        Utils.getTranslations(),
                                                        Utils.getTranslations());
    }

    public static Set<Translation> createDefaultTranslations() {
        Set<Translation> translations = new HashSet<Translation>(DEFAULT_TRANSLATIONS.length);
        translations.addAll(Arrays.asList(DEFAULT_TRANSLATIONS));
        return translations;
    }

    public static ModelExtensionDefinition createDefinitionWithNoPropertyDefinitions() {
        return new ModelExtensionDefinition(createAssistant(),
                                            DEFAULT_NAMESPACE_PREFIX,
                                            DEFAULT_NAMESPACE_URI,
                                            DEFAULT_METAMODEL_URI,
                                            DEFAULT_MED_DESCRIPTION,
                                            DEFAULT_VERSION);
    }

    public static ModelExtensionDefinition createDefinitionWithOneMetaclassAndNoPropertyDefinitions() {
        ModelExtensionDefinition med = new ModelExtensionDefinition(createAssistant(),
                                                                    DEFAULT_NAMESPACE_PREFIX,
                                                                    DEFAULT_NAMESPACE_URI,
                                                                    DEFAULT_METAMODEL_URI,
                                                                    DEFAULT_MED_DESCRIPTION,
                                                                    DEFAULT_VERSION);
        med.addMetaclass(DEFAULT_METACLASS);
        return med;
    }

    public static ModelExtensionDefinition createDefinitionWithOneMetaclassWithOnePropertyDefinition() {
        ModelExtensionDefinition med = createDefinitionWithOneMetaclassAndNoPropertyDefinitions();
        med.addPropertyDefinition(med.getExtendedMetaclasses()[0], createDefaultPropertyDefinition());
        return med;
    }

    public static ModelExtensionDefinitionParser createParser() {
        return new ModelExtensionDefinitionParser(new File(MED_SCHEMA));
    }

    public static ModelExtensionDefinitionWriter createWriter() {
        return new ModelExtensionDefinitionWriter();
    }

    public static Listener createPropertyChangeListener() {
        return new Listener();
    }

    public static ModelExtensionRegistry createRegistry() throws Exception {
        ModelExtensionRegistry registry = new ModelExtensionRegistry(new File(MED_SCHEMA));
        registry.setMetamodelUris(Constants.Utils.getExtendableMetamodelUris());
        return registry;
    }

    public static MedRegistryListener createRegistryListener() {
        return new MedRegistryListener();
    }

    public static List<ModelExtensionPropertyDefinition> getTestPropertyDefns( NamespacePrefixProvider prefixProvider ) {
        List<ModelExtensionPropertyDefinition> propertyDefns = new ArrayList<ModelExtensionPropertyDefinition>(3);

        String name = "testProp1"; //$NON-NLS-1$
        String type = "boolean"; //$NON-NLS-1$
        String required = "false"; //$NON-NLS-1$
        String defaultValue = ""; //$NON-NLS-1$
        String fixedValue = ""; //$NON-NLS-1$
        String advanced = "false"; //$NON-NLS-1$
        String masked = "false"; //$NON-NLS-1$
        String index = "false"; //$NON-NLS-1$
        Set<String> allowedValues = new HashSet<String>();
        Set<Translation> descriptions = new HashSet<Translation>();
        Set<Translation> displayNames = new HashSet<Translation>();
        ModelExtensionPropertyDefinition propDefn1 = new ModelExtensionPropertyDefinitionImpl(prefixProvider,
                                                                                              name,
                                                                                              type,
                                                                                              required,
                                                                                              defaultValue,
                                                                                              fixedValue,
                                                                                              advanced,
                                                                                              masked,
                                                                                              index,
                                                                                              allowedValues,
                                                                                              descriptions,
                                                                                              displayNames);
        name = "testProp2"; //$NON-NLS-1$
        type = "string"; //$NON-NLS-1$
        allowedValues.add("POST"); //$NON-NLS-1$
        allowedValues.add("GET"); //$NON-NLS-1$
        allowedValues.add("PUT"); //$NON-NLS-1$
        descriptions.add(new Translation(Locale.ENGLISH, "English Description")); //$NON-NLS-1$
        descriptions.add(new Translation(Locale.CHINESE, "Chinese Description")); //$NON-NLS-1$
        descriptions.add(new Translation(Locale.JAPANESE, "Japanese Description")); //$NON-NLS-1$
        displayNames.add(new Translation(Locale.ENGLISH, "English DisplayName")); //$NON-NLS-1$
        displayNames.add(new Translation(Locale.CHINESE, "Chinese DisplayName")); //$NON-NLS-1$
        displayNames.add(new Translation(Locale.JAPANESE, "Japanese DisplayName")); //$NON-NLS-1$

        ModelExtensionPropertyDefinition propDefn2 = new ModelExtensionPropertyDefinitionImpl(prefixProvider,
                                                                                              name,
                                                                                              type,
                                                                                              required,
                                                                                              defaultValue,
                                                                                              fixedValue,
                                                                                              advanced,
                                                                                              masked,
                                                                                              index,
                                                                                              allowedValues,
                                                                                              descriptions,
                                                                                              displayNames);
        name = "testProp3"; //$NON-NLS-1$
        type = "integer"; //$NON-NLS-1$
        advanced = "true"; //$NON-NLS-1$
        index = "true"; //$NON-NLS-1$
        defaultValue = "1"; //$NON-NLS-1$ 
        allowedValues.clear();
        allowedValues.add("1"); //$NON-NLS-1$
        allowedValues.add("2"); //$NON-NLS-1$
        descriptions.clear();
        descriptions.add(new Translation(Locale.ENGLISH, "English Description")); //$NON-NLS-1$
        descriptions.add(new Translation(Locale.CHINESE, "Chinese Description")); //$NON-NLS-1$
        displayNames.clear();
        displayNames.add(new Translation(Locale.ENGLISH, "English DisplayName")); //$NON-NLS-1$
        displayNames.add(new Translation(Locale.CHINESE, "Chinese DisplayName")); //$NON-NLS-1$

        ModelExtensionPropertyDefinition propDefn3 = new ModelExtensionPropertyDefinitionImpl(prefixProvider,
                                                                                              name,
                                                                                              type,
                                                                                              required,
                                                                                              defaultValue,
                                                                                              fixedValue,
                                                                                              advanced,
                                                                                              masked,
                                                                                              index,
                                                                                              allowedValues,
                                                                                              descriptions,
                                                                                              displayNames);
        propertyDefns.add(propDefn1);
        propertyDefns.add(propDefn2);
        propertyDefns.add(propDefn3);

        return propertyDefns;

    }

}
