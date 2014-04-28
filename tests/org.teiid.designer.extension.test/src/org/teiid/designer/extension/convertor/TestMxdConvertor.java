/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.extension.convertor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.Test;
import org.teiid.core.designer.util.ModelType;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.convertor.TranslatorAnnotationVisitor.NameMappings;
import org.teiid.designer.extension.convertor.mxd.DisplayType;
import org.teiid.designer.extension.convertor.mxd.MetaclassType;
import org.teiid.designer.extension.convertor.mxd.ObjectFactory;
import org.teiid.designer.extension.convertor.mxd.PropertyType;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

@SuppressWarnings( {"nls", "javadoc"} )
public class TestMxdConvertor implements Constants {

    private final MxdConvertor convertor = MxdConvertor.getInstance();

    private final ObjectFactory factory = new ObjectFactory();

    /**
     * @param metaClass
     * @param expProperties
     */
    private void checkExpectedValues(MetaclassType metaClass, List<ExpProperty> expProperties) {
        for (ExpProperty expProperty : expProperties) {

            boolean foundProp = false;

            for (PropertyType property : metaClass.getProperty()) {
                if (expProperty.getName().equals(property.getName())) {
                    foundProp = true;
                    assertEquals(expProperty.getType(), property.getType());
                    assertEquals(expProperty.isAdvanced(), property.getAdvanced());
                    assertEquals(expProperty.isIndex(), property.getIndex());
                    assertEquals(expProperty.isMasked(), property.getMasked());
                    assertEquals(expProperty.isRequired(), property.getRequired());
                    
                    assertEquals(1, property.getDisplay().size());
                    assertEquals(expProperty.getDisplayName(), property.getDisplay().get(0).getValue());
                }
            }

            assertTrue("Failed to match a metaclass property to an expected property", foundProp);
        }
    }

    @Test
    public void testFileToMetaClassConversion() throws Exception {
        File testFile = new File(ODATA_TRANSLATOR_SOURCE);
        Collection<MetaclassType> metaClasses = convertor.read(testFile);
        assertNotNull(metaClasses);
        assertEquals(3, metaClasses.size());

        for (MetaclassType metaClass : metaClasses) {
            if (metaClass.getName().equals(NameMappings.TABLE.getMapping())) {
                assertEquals(2, metaClass.getProperty().size());

                List<ExpProperty> expProperties = new ArrayList<ExpProperty>();
                expProperties.add(new ExpProperty("EntityType", "string", "Entity Type Name", false, true, false, true));
                expProperties.add(new ExpProperty("LinkTables", "string", "Link Tables", false, true, false, false));
                checkExpectedValues(metaClass, expProperties);

            } else if (metaClass.getName().equals(NameMappings.PROCEDURE.getMapping())) {
                assertEquals(2, metaClass.getProperty().size());

                List<ExpProperty> expProperties = new ArrayList<ExpProperty>();
                expProperties.add(new ExpProperty("EntityType", "string", "Entity Type Name", false, true, false, true));
                expProperties.add(new ExpProperty("HttpMethod", "string", "Http Method", false, true, false, true));
                checkExpectedValues(metaClass, expProperties);

            } else if (metaClass.getName().equals(NameMappings.COLUMN.getMapping())) {
                assertEquals(3, metaClass.getProperty().size());

                List<ExpProperty> expProperties = new ArrayList<ExpProperty>();
                expProperties.add(new ExpProperty("JoinColumn", "boolean", "Join Column", false, true, false, false));
                expProperties.add(new ExpProperty("ComplexType", "string", "Complex Type Name", false, true, false, false));
                expProperties.add(new ExpProperty("ColumnGroup", "string", "Column Group", false, true, false, false));
                checkExpectedValues(metaClass, expProperties);
            }
        }
    }

    @Test
    public void testMetaClassesToFile() throws Exception {
        
        MetaclassType tableMetaClass = factory.createMetaclassType();
        MetaclassType procMetaClass = factory.createMetaclassType();
        MetaclassType colMetaClass = factory.createMetaclassType();

        tableMetaClass.setName("org.teiid.designer.metamodels.relational.impl.BaseTableImpl");
        procMetaClass.setName("org.teiid.designer.metamodels.relational.impl.ProcedureImpl");
        colMetaClass.setName("org.teiid.designer.metamodels.relational.impl.ColumnImpl");

        List<MetaclassType> metaClasses = new ArrayList<MetaclassType>();
        metaClasses.add(tableMetaClass);
        metaClasses.add(procMetaClass);
        metaClasses.add(colMetaClass);

/*      @ExtensionMetadataProperty(applicable=Table.class, datatype=String.class, display="Link Tables", 
 *                                                     description="Used to define navigation relationship in many to many case")    
 *      public static final String LINK_TABLES = MetadataFactory.ODATA_URI+"LinkTables"; //$NON-NLS-1$
 */    
        PropertyType linkTables = factory.createPropertyType();
        linkTables.setName("LinkTables");
        linkTables.setType("string");
        
        DisplayType displayName = factory.createDisplayType();
        displayName.setValue("Link Tables");
        linkTables.getDisplay().add(displayName);
        
        DisplayType description = factory.createDisplayType();
        description.setValue("Used to define navigation relationship in many to many case");
        linkTables.getDescription().add(description);

        tableMetaClass.getProperty().add(linkTables);

/*        @ExtensionMetadataProperty(applicable=Procedure.class, datatype=String.class, display="Http Method",
 *                                                       description="Http method used for procedure invocation", required=true)
 *        public static final String HTTP_METHOD = MetadataFactory.ODATA_URI+"HttpMethod"; //$NON-NLS-1$
 */
        PropertyType httpMethod = factory.createPropertyType();
        httpMethod.setName("HttpMethod");
        httpMethod.setType("string");
        httpMethod.setRequired(true);

        displayName = factory.createDisplayType();
        displayName.setValue("Http Method");
        httpMethod.getDisplay().add(displayName);
        
        description = factory.createDisplayType();
        description.setValue("Http method used for procedure invocation");
        httpMethod.getDescription().add(description);

        procMetaClass.getProperty().add(httpMethod);

/*        @ExtensionMetadataProperty(applicable=Column.class, datatype=Boolean.class, display="Join Column",
 *                                                       description="On Link tables this property defines the join column")    
 *        public static final String JOIN_COLUMN = MetadataFactory.ODATA_URI+"JoinColumn"; //$NON-NLS-1$
 */        
        PropertyType joinColumn = factory.createPropertyType();
        joinColumn.setName("JoinColumn");
        joinColumn.setType("boolean");

        displayName = factory.createDisplayType();
        displayName.setValue("Join Column");
        joinColumn.getDisplay().add(displayName);
        
        description = factory.createDisplayType();
        description.setValue("On Link tables this property defines the join column");
        joinColumn.getDescription().add(description);

        colMetaClass.getProperty().add(joinColumn);
        
/*        @ExtensionMetadataProperty(applicable= {Table.class, Procedure.class}, datatype=String.class, 
 *                                                       display="Entity Type Name", description="Name of the Entity Type in EDM", required=true)    
 *        public static final String ENTITY_TYPE = MetadataFactory.ODATA_URI+"EntityType"; //$NON-NLS-1$
 */
        // Added to tableMetaClass
        PropertyType entityType1 = factory.createPropertyType();
        entityType1.setName("EntityType");
        entityType1.setType("string");
        entityType1.setRequired(true);

        displayName = factory.createDisplayType();
        displayName.setValue("Entity Type Name");
        entityType1.getDisplay().add(displayName);
        
        description = factory.createDisplayType();
        description.setValue("Name of the Entity Type in EDM");
        entityType1.getDescription().add(description);

        tableMetaClass.getProperty().add(entityType1);

        // Added to procMetaClass
        PropertyType entityType2 = factory.createPropertyType();
        entityType2.setName("EntityType");
        entityType2.setType("string");
        entityType2.setRequired(true);

        displayName = factory.createDisplayType();
        displayName.setValue("Entity Type Name");
        entityType2.getDisplay().add(displayName);
        
        description = factory.createDisplayType();
        description.setValue("Name of the Entity Type in EDM");
        entityType2.getDescription().add(description);

        procMetaClass.getProperty().add(entityType2);

/*        @ExtensionMetadataProperty(applicable=Column.class, datatype=String.class, display="Complex Type Name",
 *                                                       description="Name of the Complex Type in EDM")
 *        public static final String COMPLEX_TYPE = MetadataFactory.ODATA_URI+"ComplexType"; //$NON-NLS-1$
 */
        PropertyType complexType = factory.createPropertyType();
        complexType.setName("ComplexType");
        complexType.setType("string");

        displayName = factory.createDisplayType();
        displayName.setValue("Complex Type Name");
        complexType.getDisplay().add(displayName);
        
        description = factory.createDisplayType();
        description.setValue("Name of the Complex Type in EDM");
        complexType.getDescription().add(description);

        colMetaClass.getProperty().add(complexType);
        
/*        @ExtensionMetadataProperty(applicable=Column.class, datatype=String.class, display="Column Group",
 *                                                       description="Name of the Column Group")
 *        public static final String COLUMN_GROUP = MetadataFactory.ODATA_URI+"ColumnGroup"; //$NON-NLS-1$
 */
        PropertyType colGroupType = factory.createPropertyType();
        colGroupType.setName("ColumnGroup");
        colGroupType.setType("string");

        displayName = factory.createDisplayType();
        displayName.setValue("Column Group");
        colGroupType.getDisplay().add(displayName);
        
        description = factory.createDisplayType();
        description.setValue("Name of the Column Group");
        colGroupType.getDescription().add(description);

        colMetaClass.getProperty().add(colGroupType);

        File outputFile = File.createTempFile(this.getClass().getSimpleName(), StringConstants.DOT + StringConstants.XML);
        outputFile.deleteOnExit();

        /* Actually peform test!! */
        FileOutputStream output = new FileOutputStream(outputFile);
        convertor.write("odata", ModelType.Type.PHYSICAL, metaClasses, output);
        output.close();

        /* Use sax to check the output file */
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DefaultHandler handler = new DefaultHandler() {

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                System.out.println(uri + " --- " + localName + " --- " + qName + " --- ");
            }
        };

        InputStream inputStream= new FileInputStream(outputFile);
        Reader reader = new InputStreamReader(inputStream,"UTF-8");
        InputSource is = new InputSource(reader);
        is.setEncoding("UTF-8");
        saxParser.parse(is, handler);
    }
}
