/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Test;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.Factory;

/**
 * 
 */
public class ModelExtensionDefinitionParserTest implements Constants {

    private ModelExtensionAssistant assistant;
    private ModelExtensionDefinitionParser parser;

    private ModelExtensionDefinition parse( String fileName,
                                            ModelExtensionDefinitionParser parser ) throws Exception {
        return parser.parse(new FileInputStream(new File(fileName)), this.assistant);
    }

    @Before
    public void beforeEach() {
        this.assistant = Factory.createAssistant();
        this.parser = Factory.createParser();
    }

    @Test
    public void shouldParseBuiltInMeds() throws Exception {
        for (String medFileName : BUILT_IN_MEDS) {
            parse(medFileName, this.parser);
            assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
            assertEquals("MED file '" + medFileName + "' had parse errors", 0, this.parser.getErrors().size()); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    @Test
    public void shouldParseEmptyMedAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(EMPTY_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue(!this.parser.getErrors().isEmpty());
    }

    @Test
    public void shouldParseMedWithoutMetaclassesAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(MED_WITHOUT_METACLASSES_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue(!this.parser.getErrors().isEmpty());
    }

    @Test
    public void shouldParseMedWithouPropertiesAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(MED_WITHOUT_PROPERTIES_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue(!this.parser.getErrors().isEmpty());
    }

    @Test
    public void shouldParseMedWithDuplicateMetaclassesAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_METACLASSES_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue("Parser should have errors and did not", !this.parser.getErrors().isEmpty()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithDuplicatePropertyIdsDifferentMetaclassesWithNoErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_PROP_IDS_DIFFERENT_METACLASSES_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertEquals("Parser should not have errors and did", 0, this.parser.getErrors().size()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithDuplicatePropertyIdsAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_PROP_IDS_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue("Parser should have errors and did not", !this.parser.getErrors().isEmpty()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithDuplicateModelTypesAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_MODEL_TYPES_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue("Parser should have errors and did not", !this.parser.getErrors().isEmpty()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithIllegalModelTypeAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(ILLEGAL_MODEL_TYPE_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue("Parser should have errors and did not", !this.parser.getErrors().isEmpty()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithTooManyModelTypeAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(MODEL_TYPES_MAX_EXCEEDED_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue("Parser should have errors and did not", !this.parser.getErrors().isEmpty()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithDuplicateAllowedValuesAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_ALLOWED_VALUES_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue("Parser should have errors and did not", !this.parser.getErrors().isEmpty()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithDuplicateAllowedValuesInDifferentPropsWithNoErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_ALLOWED_VALUES_DIFFERENT_PROPS_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertEquals("Parser should not have errors and did", 0, this.parser.getErrors().size()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithDuplicatePropertyDescriptionLocaleAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_PROPERTY_DESCRIPTION_LOCALE_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue("Parser should have errors and did not", !this.parser.getErrors().isEmpty()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithDuplicatePropertyDescriptionLocaleFromDifferentPropertiesWithNoErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_PROPERTY_DESCRIPTION_LOCALE_FROM_DIFFERENT_PROPERTIES_MED_FILE_NAME,
                                             this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertEquals("Parser should not have errors and did", 0, this.parser.getErrors().size()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithDuplicatePropertyDisplayLocaleAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_PROPERTY_DISPLAY_LOCALE_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue("Parser should have errors and did not", !this.parser.getErrors().isEmpty()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithDuplicatePropertyDisplayLocaleFromDifferentPropertiesWithNoErrors() throws Exception {
        ModelExtensionDefinition med = parse(DUPLICATE_PROPERTY_DISPLAY_LOCALE_FROM_DIFFERENT_PROPERTIES_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertEquals("Parser should not have errors and did", 0, this.parser.getErrors().size()); //$NON-NLS-1$
    }

    @Test
    public void shouldParseMedWithInvalidPropertyTypeAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(INVALID_PROPERTY_TYPE_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertEquals("Parser should not have fatal errors and did", 0, this.parser.getFatalErrors().size()); //$NON-NLS-1$
        assertTrue("Parser should have errors and did not", !this.parser.getErrors().isEmpty()); //$NON-NLS-1$
    }

}
