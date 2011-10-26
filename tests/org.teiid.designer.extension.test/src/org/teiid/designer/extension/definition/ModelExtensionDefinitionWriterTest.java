/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.definition;

import static junit.framework.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.Factory;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinition;
import org.teiid.designer.extension.properties.ModelExtensionPropertyDefinitionImpl;

/**
 * ModelExtensionDefinitionWriterTest. This is a round-trip test of the writer and parser. The tests construct a MED, write it
 * out, read it back (parser), then compare the resulting MED to the starting MED.
 */
public class ModelExtensionDefinitionWriterTest implements Constants {

    private ModelExtensionAssistant assistant;
    private ModelExtensionDefinitionWriter writer;
    private ModelExtensionDefinitionParser parser;

    private InputStream write( ModelExtensionDefinition med ) throws Exception {
        return writer.writeAsStream(med);
    }

    private ModelExtensionDefinition parse( String fileName,
                                            ModelExtensionDefinitionParser parser ) throws Exception {
        return parser.parse(new FileInputStream(new File(fileName)), this.assistant);
    }

    @Before
    public void beforeEach() {
        this.assistant = Factory.createAssistant();
        this.writer = Factory.createWriter();
        this.parser = Factory.createParser();
    }

    @Test
    public void shouldWriteBuiltInMeds() throws Exception {
        for (String medFileName : BUILT_IN_MEDS) {
            ModelExtensionDefinition med = parse(medFileName, this.parser);

            // Write it, then read it back into another MED
            ModelExtensionDefinition resultMed = roundTrip(med);

            assertEquals(med, resultMed);
        }
    }

    @Test
    public void shouldWriteEmptyMed() throws Exception {
        // Create a default MED
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    @Test
    public void shouldWriteMedNamespacePrefix1() throws Exception {
        // Create MED with namespace prefix only
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);
        med.setNamespacePrefix("myPrefix"); //$NON-NLS-1$

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    @Test
    public void shouldWriteMedNamespacePrefix2() throws Exception {
        // Create MED with namespace prefix containing special chars
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);
        med.setNamespacePrefix("Wierd<Chars%Here"); //$NON-NLS-1$

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    @Test
    public void shouldWriteAllHeaderValues() throws Exception {
        // Create MED - all header field values set
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);
        med.setNamespacePrefix("My-Custom-NS"); //$NON-NLS-1$
        med.setNamespaceUri("org.teiid.designer.extension.mycustom"); //$NON-NLS-1$
        med.setMetamodelUri("http://www.metamatrix.com/metamodels/Relational"); //$NON-NLS-1$
        med.setDescription("My Custom 7.4 Extension Definition"); //$NON-NLS-1$

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    @Test
    public void shouldWriteAllHeaderValues2() throws Exception {
        // Create MED - all header field values set, have html chars
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);
        med.setNamespacePrefix("My<Custom>NS"); //$NON-NLS-1$
        med.setNamespaceUri("org.teiid.designer.extension.mycustom:yep"); //$NON-NLS-1$
        med.setMetamodelUri("http://www.metamatrix.com/metamodels/Relational#1"); //$NON-NLS-1$
        med.setDescription("My Custom 7.4 @ Extension Definition"); //$NON-NLS-1$

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    @Test
    public void shouldWriteExtendedMetaclass1() throws Exception {
        // Create MED - all header field values set
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);
        med.setNamespacePrefix("My-Custom-NS"); //$NON-NLS-1$
        med.setNamespaceUri("org.teiid.designer.extension.mycustom"); //$NON-NLS-1$
        med.setMetamodelUri("http://www.metamatrix.com/metamodels/Relational"); //$NON-NLS-1$
        med.setDescription("My Custom 7.4 Extension Definition"); //$NON-NLS-1$

        // Add a single metaclass - with no properties
        med.addMetaclass("com.metamatrix.metamodels.relational.impl.ProcedureImpl"); //$NON-NLS-1$

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    @Test
    public void shouldWriteExtendedMetaclass2() throws Exception {
        // Create MED - all header field values set
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);
        med.setNamespacePrefix("My-Custom-NS"); //$NON-NLS-1$
        med.setNamespaceUri("org.teiid.designer.extension.mycustom"); //$NON-NLS-1$
        med.setMetamodelUri("http://www.metamatrix.com/metamodels/Relational"); //$NON-NLS-1$
        med.setDescription("My Custom 7.4 Extension Definition"); //$NON-NLS-1$

        // Add multiple metaclasses - with no properties
        med.addMetaclass("com.metamatrix.metamodels.relational.impl.ProcedureImpl"); //$NON-NLS-1$
        med.addMetaclass("com.metamatrix.metamodels.relational.impl.BaseTableImpl"); //$NON-NLS-1$
        med.addMetaclass("com.metamatrix.metamodels.relational.impl.ColumnImpl"); //$NON-NLS-1$

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    @Test
    public void shouldWriteExtendedMetaclassWProp() throws Exception {
        // Create MED - all header field values set
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);
        med.setNamespacePrefix("My-Custom-NS"); //$NON-NLS-1$
        med.setNamespaceUri("org.teiid.designer.extension.mycustom"); //$NON-NLS-1$
        med.setMetamodelUri("http://www.metamatrix.com/metamodels/Relational"); //$NON-NLS-1$
        med.setDescription("My Custom 7.4 Extension Definition"); //$NON-NLS-1$

        // Add single metaclass - with one default property
        med.addMetaclass("com.metamatrix.metamodels.relational.impl.BaseTableImpl"); //$NON-NLS-1$
        ModelExtensionPropertyDefinition propDefn = new ModelExtensionPropertyDefinitionImpl(med);
        med.addPropertyDefinition("com.metamatrix.metamodels.relational.impl.BaseTableImpl", propDefn); //$NON-NLS-1$

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    @Test
    public void shouldWriteExtendedMetaclassWProps1() throws Exception {
        // Create MED - all header field values set
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);
        med.setNamespacePrefix("My-Custom-NS"); //$NON-NLS-1$
        med.setNamespaceUri("org.teiid.designer.extension.mycustom"); //$NON-NLS-1$
        med.setMetamodelUri("http://www.metamatrix.com/metamodels/Relational"); //$NON-NLS-1$
        med.setDescription("My Custom 7.4 Extension Definition"); //$NON-NLS-1$

        // Will extend a single metaclass - with a few properties of different types
        med.addMetaclass(TABLE_METACLASS_NAME);

        // Factory method creates three different property definitions
        List<ModelExtensionPropertyDefinition> propDefns = Factory.getTestPropertyDefns(med);

        // Add the definitions for BaseTable metaclass
        med.addPropertyDefinitions(TABLE_METACLASS_NAME, propDefns);

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    @Test
    public void shouldWriteExtendedMetaclassWProps2() throws Exception {
        // Create MED - all header field values set
        ModelExtensionDefinition med = new ModelExtensionDefinition(this.assistant);
        med.setNamespacePrefix("My-Custom-NS"); //$NON-NLS-1$
        med.setNamespaceUri("org.teiid.designer.extension.mycustom"); //$NON-NLS-1$
        med.setMetamodelUri("http://www.metamatrix.com/metamodels/Relational"); //$NON-NLS-1$
        med.setDescription("My Custom 7.4 Extension Definition"); //$NON-NLS-1$

        // Will extend a two metaclasses - with a different number of properties on each
        med.addMetaclass(TABLE_METACLASS_NAME);
        med.addMetaclass(COLUMN_METACLASS_NAME);

        // Put three property defns on table
        List<ModelExtensionPropertyDefinition> tableDefns = Factory.getTestPropertyDefns(med);
        // Put one property defn on column
        List<ModelExtensionPropertyDefinition> columnDefns = Factory.getTestPropertyDefns(med);
        columnDefns.remove(1); // remove will shift elements, do it twice...
        columnDefns.remove(1);

        // Add the definitions for BaseTable metaclass
        med.addPropertyDefinitions(TABLE_METACLASS_NAME, tableDefns);
        // Add the definitions for Column metaclass
        med.addPropertyDefinitions(COLUMN_METACLASS_NAME, columnDefns);

        // Write it, then read it back into another MED
        ModelExtensionDefinition resultMed = roundTrip(med);

        assertEquals(med, resultMed);
    }

    /*
     * This will 'round-trip' the MED.  It is written out to a temp file, then parsed (read back in).  The 
     * resulting MED is returned.
     */
    private ModelExtensionDefinition roundTrip( ModelExtensionDefinition med ) throws Exception {
        // Write the incoming MED to a temp file
        InputStream stream = write(med);
        File outFile = new File(TEMP_MED_FILE_NAME);
        writeToFile(stream, outFile);

        // Now read the temp file back in, creating a new Med
        ModelExtensionDefinition resultMed = parse(TEMP_MED_FILE_NAME, this.parser);
        outFile.delete();
        return resultMed;
    }

    private void writeToFile(InputStream inputStream, File file) throws IOException, FileNotFoundException {
        // write the inputStream to a FileOutputStream
        OutputStream out = new FileOutputStream(file);
     
        int read = 0;
        byte[] bytes = new byte[1024];
     
        while ((read = inputStream.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
     
        inputStream.close();
        out.flush();
        out.close();
    }
    
}
