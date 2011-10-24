/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

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
            assertTrue(this.parser.getErrors().isEmpty());
        }
    }

    @Test
    public void shouldParseEmptyMedAndProduceErrors() throws Exception {
        ModelExtensionDefinition med = parse(EMPTY_MED_FILE_NAME, this.parser);
        assertNotNull(med);
        assertTrue(!this.parser.getErrors().isEmpty());
    }

}
