/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.Factory;

/**
 * 
 */
public class ModelExtensionAssistantTest implements Constants {

    @Test
    public void shouldAddSupportedModelType() {
        ModelExtensionDefinition med = Factory.createDefinitionWithNoMetaclasses();
        ModelExtensionAssistant assistant = med.getModelExtensionAssistant();

        assertTrue("Supported model types should be empty", med.getSupportedModelTypes().isEmpty()); //$NON-NLS-1$

        assistant.addSupportedModelType(MODEL_TYPES[0]);
        assertTrue("Supported model types should be empty", med.getSupportedModelTypes().contains(MODEL_TYPES[0])); //$NON-NLS-1$
    }

    @Test
    public void shouldCreateMedCorrectlyFromParameters() {
        final Set<String> MODEL_TYPES = Constants.Utils.getDefaultModelTypes();
        ModelExtensionAssistant assistant = Factory.createAssistant();
        final ModelExtensionDefinition med = assistant.createModelExtensionDefinition(DEFAULT_NAMESPACE_PREFIX,
                                                                                      DEFAULT_NAMESPACE_URI, DEFAULT_METAMODEL_URI,
                                                                                      MODEL_TYPES, DEFAULT_MED_DESCRIPTION,
                                                                                      DEFAULT_VERSION);

        assertEquals("Incorrect MED namespace prefix", DEFAULT_NAMESPACE_PREFIX, med.getNamespacePrefix()); //$NON-NLS-1$
        assertEquals("Incorrect MED namespace URI", DEFAULT_NAMESPACE_URI, med.getNamespaceUri()); //$NON-NLS-1$
        assertEquals("Incorrect MED metamodel URI", DEFAULT_METAMODEL_URI, med.getMetamodelUri()); //$NON-NLS-1$
        assertEquals("Incorrect MED description", DEFAULT_MED_DESCRIPTION, med.getDescription()); //$NON-NLS-1$
        assertEquals("Incorrect MED version", DEFAULT_VERSION, Integer.toString(med.getVersion())); //$NON-NLS-1$

        Set<String> supportedModelTypes = med.getSupportedModelTypes();
        assertEquals("Incorrect number of MED supported model types", MODEL_TYPES.size(), supportedModelTypes.size()); //$NON-NLS-1$

        for (String modelType : MODEL_TYPES) {
            assertTrue("Model type " + modelType + " is not supported", supportedModelTypes.contains(modelType)); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    @Test
    public void shouldCreateMedCorrectlyFromHeader() {
        final Set<String> MODEL_TYPES = Constants.Utils.getDefaultModelTypes();
        ModelExtensionDefinitionHeader header = new ModelExtensionDefinitionHeader(DEFAULT_NAMESPACE_PREFIX,
                                                                                   DEFAULT_NAMESPACE_URI,
                                                                                   DEFAULT_METAMODEL_URI,
                                                                                   MODEL_TYPES,
                                                                                   DEFAULT_MED_DESCRIPTION,
                                                                                   Integer.valueOf(DEFAULT_VERSION));
        ModelExtensionAssistant assistant = Factory.createAssistant();
        final ModelExtensionDefinition med = assistant.createModelExtensionDefinition(header);

        assertEquals("Incorrect MED namespace prefix", DEFAULT_NAMESPACE_PREFIX, med.getNamespacePrefix()); //$NON-NLS-1$
        assertEquals("Incorrect MED namespace URI", DEFAULT_NAMESPACE_URI, med.getNamespaceUri()); //$NON-NLS-1$
        assertEquals("Incorrect MED metamodel URI", DEFAULT_METAMODEL_URI, med.getMetamodelUri()); //$NON-NLS-1$
        assertEquals("Incorrect MED description", DEFAULT_MED_DESCRIPTION, med.getDescription()); //$NON-NLS-1$
        assertEquals("Incorrect MED version", DEFAULT_VERSION, Integer.toString(med.getVersion())); //$NON-NLS-1$

        Set<String> supportedModelTypes = med.getSupportedModelTypes();
        assertEquals("Incorrect number of MED supported model types", MODEL_TYPES.size(), supportedModelTypes.size()); //$NON-NLS-1$

        for (String modelType : MODEL_TYPES) {
            assertTrue("Model type " + modelType + " is not supported", supportedModelTypes.contains(modelType)); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

}
