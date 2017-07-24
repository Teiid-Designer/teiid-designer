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
import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.Test;
import org.teiid.core.designer.util.ModelType;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.extension.Constants;
import org.teiid.designer.extension.ExtensionPlugin;
import org.teiid.designer.extension.convertor.MxdConstants.TargetObjectMappings;
import org.teiid.designer.extension.convertor.mxd.DisplayType;
import org.teiid.designer.extension.convertor.mxd.MetaclassType;
import org.teiid.designer.extension.convertor.mxd.ObjectFactory;
import org.teiid.designer.extension.convertor.mxd.PropertyType;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

@SuppressWarnings( {"nls", "javadoc"} )
public class TestMxdConvertor implements Constants {

    private final MxdConvertor convertor = MxdConvertor.getInstance();

    private final ObjectFactory factory = new ObjectFactory();

    private enum Tags {
        MODEL_EXTENSION("modelExtension"),
        MODEL_TYPE("modelType"),
        EXT_META_CLASS("extendedMetaclass"),
        PROPERTY("property"),
        DISPLAY("display"),
        DESCRIPTION("description");

        private String tag;

        private Tags(String tag) {
            this.tag = tag;
        }

        /**
         * @return the tag
         */
        public String tag() {
            return this.tag;
        }
    }

    private final String ODATA_URI = "{http://www.jboss.org/teiiddesigner/ext/odata/2012}";

    private final ExpectedProperty JOIN_COLUMN_PROPERTY = new ExpectedProperty("JoinColumn",
                                                                             TargetObjectMappings.COLUMN, 
                                                                             Boolean.class,
                                                                             false, true, false, false,
                                                                             "Join Column",
                                                                             "On Link tables this property defines the join column");

    private final ExpectedProperty COMPLEX_TYPE_PROPERTY = new ExpectedProperty("ComplexType",
                                                                                TargetObjectMappings.COLUMN,
                                                                                String.class,
                                                                                false, true, false, false,
                                                                                "Complex Type Name", "Name of the Complex Type in EDM");

    private final ExpectedProperty COLUMN_GROUP_PROPERTY = new ExpectedProperty("ColumnGroup",
                                                                                TargetObjectMappings.COLUMN,
                                                                                String.class,
                                                                                false, true, false, false,
                                                                                "Column Group", "Name of the Column Group");

    private final ExpectedProperty HTTP_METHOD_PROPERTY = new ExpectedProperty("HttpMethod",
                                                                               TargetObjectMappings.PROCEDURE,
                                                                               String.class,
                                                                               false, true, false, true,
                                                                               "Http Method", "Http method used for procedure invocation");

    private final ExpectedProperty ENTITY_TYPE_PROC_PROPERTY = new ExpectedProperty("EntityType",
                                                                               TargetObjectMappings.PROCEDURE,
                                                                               String.class,
                                                                               false, true, false, true,
                                                                               "Entity Type Name", "Name of the Entity Type in EDM");


    private final ExpectedProperty ENTITY_TYPE_TABLE_PROPERTY = new ExpectedProperty("EntityType",
                                                                               TargetObjectMappings.TABLE,
                                                                               String.class,
                                                                               false, true, false, true,
                                                                               "Entity Type Name", "Name of the Entity Type in EDM");

    private final ExpectedProperty LINK_TABLES_PROPERTY = new ExpectedProperty("LinkTables",
                                                                               TargetObjectMappings.TABLE,
                                                                               String.class,
                                                                               false, true, false, false,
                                                                               "Link Tables",
                                                                               "Used to define navigation relationship in many to many case");

    /**
     * @param metaClass
     * @param expProperties
     */
    private void checkExpectedValues(MetaclassType metaClass, List<ExpectedProperty> expProperties) {
        for (ExpectedProperty expProperty : expProperties) {

            boolean foundProp = false;

            for (PropertyType property : metaClass.getProperty()) {
                if (expProperty.getName().equals(property.getName())) {
                    foundProp = true;
                    assertEquals(expProperty.getType().getSimpleName().toLowerCase(), property.getType());
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
            if (metaClass.getName().equals(TargetObjectMappings.TABLE.getDesignerClass())) {
                assertEquals(2, metaClass.getProperty().size());

                List<ExpectedProperty> expProperties = new ArrayList<ExpectedProperty>();
                expProperties.add(ENTITY_TYPE_TABLE_PROPERTY);
                expProperties.add(LINK_TABLES_PROPERTY);
                checkExpectedValues(metaClass, expProperties);

            } else if (metaClass.getName().equals(TargetObjectMappings.PROCEDURE.getDesignerClass())) {
                assertEquals(2, metaClass.getProperty().size());

                List<ExpectedProperty> expProperties = new ArrayList<ExpectedProperty>();
                expProperties.add(ENTITY_TYPE_PROC_PROPERTY);
                expProperties.add(HTTP_METHOD_PROPERTY);
                checkExpectedValues(metaClass, expProperties);

            } else if (metaClass.getName().equals(TargetObjectMappings.COLUMN.getDesignerClass())) {
                assertEquals(3, metaClass.getProperty().size());

                List<ExpectedProperty> expProperties = new ArrayList<ExpectedProperty>();
                expProperties.add(JOIN_COLUMN_PROPERTY);
                expProperties.add(COMPLEX_TYPE_PROPERTY);
                expProperties.add(COLUMN_GROUP_PROPERTY);
                checkExpectedValues(metaClass, expProperties);
            }
        }
    }

    private TeiidPropertyDefinition createTeiidPropertyDefinition(ExpectedProperty expProperty) {
        TeiidPropertyDefinition defn = new TeiidPropertyDefinition();
        defn.setName(ODATA_URI + expProperty.getName());
        defn.setOwner(expProperty.getTarget().getTeiidClass());
        defn.setPropertyTypeClassName(expProperty.getType().getCanonicalName());
        defn.setAdvanced(expProperty.isAdvanced());
        defn.setMasked(expProperty.isMasked());
        defn.setRequired(expProperty.isRequired());
        defn.setDisplayName(expProperty.getDisplayName());
        defn.setDescription(expProperty.getDescription());
        return defn;
    }

    @Test
    public void testTranslatorToMetaClassConversion() {
        List<TeiidPropertyDefinition> defnCollection = new ArrayList<TeiidPropertyDefinition>();
        TeiidPropertyDefinition defn;
        
        defn = createTeiidPropertyDefinition(JOIN_COLUMN_PROPERTY);
        defnCollection.add(defn);

        defn = createTeiidPropertyDefinition(COMPLEX_TYPE_PROPERTY);
        defnCollection.add(defn);

        defn = createTeiidPropertyDefinition(COLUMN_GROUP_PROPERTY);
        defnCollection.add(defn);

        defn = createTeiidPropertyDefinition(HTTP_METHOD_PROPERTY);
        defnCollection.add(defn);

        defn = createTeiidPropertyDefinition(ENTITY_TYPE_PROC_PROPERTY);
        defnCollection.add(defn);

        defn = createTeiidPropertyDefinition(ENTITY_TYPE_TABLE_PROPERTY);
        defnCollection.add(defn);

        defn = createTeiidPropertyDefinition(LINK_TABLES_PROPERTY);
        defnCollection.add(defn);

        Collection<MetaclassType> metaClasses = convertor.read(defnCollection);
        assertNotNull(metaClasses);
        assertEquals(3, metaClasses.size());

        for (MetaclassType metaClass : metaClasses) {
            if (metaClass.getName().equals(TargetObjectMappings.TABLE.getDesignerClass())) {
                assertEquals(2, metaClass.getProperty().size());

                List<ExpectedProperty> expProperties = new ArrayList<ExpectedProperty>();
                expProperties.add(ENTITY_TYPE_TABLE_PROPERTY);
                expProperties.add(LINK_TABLES_PROPERTY);
                checkExpectedValues(metaClass, expProperties);

            } else if (metaClass.getName().equals(TargetObjectMappings.PROCEDURE.getDesignerClass())) {
                assertEquals(2, metaClass.getProperty().size());

                List<ExpectedProperty> expProperties = new ArrayList<ExpectedProperty>();
                expProperties.add(ENTITY_TYPE_PROC_PROPERTY);
                expProperties.add(HTTP_METHOD_PROPERTY);
                checkExpectedValues(metaClass, expProperties);

            } else if (metaClass.getName().equals(TargetObjectMappings.COLUMN.getDesignerClass())) {
                assertEquals(3, metaClass.getProperty().size());

                List<ExpectedProperty> expProperties = new ArrayList<ExpectedProperty>();
                expProperties.add(JOIN_COLUMN_PROPERTY);
                expProperties.add(COMPLEX_TYPE_PROPERTY);
                expProperties.add(COLUMN_GROUP_PROPERTY);
                checkExpectedValues(metaClass, expProperties);
            }
        }
    }

    private void checkExpectedValues(PropertyType propertyType, Element propElement) {
        String advanced = propElement.getAttribute("advanced");
        String required = propElement.getAttribute("required");
        String index = propElement.getAttribute("index");
        String masked = propElement.getAttribute("masked");
        String type = propElement.getAttribute("type");

        assertEquals(propertyType.getAdvanced(), Boolean.parseBoolean(advanced));
        assertEquals(propertyType.getRequired(), Boolean.parseBoolean(required));
        assertEquals(propertyType.getIndex(), index == "" ? true : Boolean.parseBoolean(index));
        assertEquals(propertyType.getMasked(), Boolean.parseBoolean(masked));
        assertEquals(propertyType.getType(), type);

        NodeList displayNodes = propElement.getElementsByTagName("display");
        assertEquals(1, displayNodes.getLength());
        assertEquals(propertyType.getDisplay().get(0).getValue(), displayNodes.item(0).getTextContent());

        NodeList descNodes = propElement.getElementsByTagName("description");
        assertEquals(1, descNodes.getLength());
        assertEquals(propertyType.getDescription().get(0).getValue(), descNodes.item(0).getTextContent());

    }

    private PropertyType createMetaClass(final MetaclassType tableMetaClass, ExpectedProperty expProperty) {
        PropertyType metaClassProperty = factory.createPropertyType();
        metaClassProperty.setName(expProperty.getName());
        metaClassProperty.setType(expProperty.getType().getSimpleName().toLowerCase());
        metaClassProperty.setAdvanced(expProperty.isAdvanced());
        metaClassProperty.setIndex(expProperty.isIndex());
        metaClassProperty.setMasked(expProperty.isMasked());
        metaClassProperty.setRequired(expProperty.isRequired());

        DisplayType displayName = factory.createDisplayType();
        displayName.setValue(expProperty.getDisplayName());
        metaClassProperty.getDisplay().add(displayName);

        DisplayType description = factory.createDisplayType();
        description.setValue(expProperty.getDescription());
        metaClassProperty.getDescription().add(description);

        tableMetaClass.getProperty().add(metaClassProperty);
        return metaClassProperty;
    }

    @Test
    public void testMetaClassesToFile() throws Exception {
        final MetaclassType tableMetaClass = factory.createMetaclassType();
        final MetaclassType procMetaClass = factory.createMetaclassType();
        final MetaclassType colMetaClass = factory.createMetaclassType();

        tableMetaClass.setName(TargetObjectMappings.TABLE.getDesignerClass());
        procMetaClass.setName(TargetObjectMappings.PROCEDURE.getDesignerClass());
        colMetaClass.setName(TargetObjectMappings.COLUMN.getDesignerClass());

        List<MetaclassType> metaClasses = new ArrayList<MetaclassType>();
        metaClasses.add(tableMetaClass);
        metaClasses.add(procMetaClass);
        metaClasses.add(colMetaClass);

/*      @ExtensionMetadataProperty(applicable=Table.class, datatype=String.class, display="Link Tables", 
 *                                                     description="Used to define navigation relationship in many to many case")    
 *      public static final String LINK_TABLES = MetadataFactory.ODATA_URI+"LinkTables"; //$NON-NLS-1$
 */
        PropertyType linkTables = createMetaClass(tableMetaClass, LINK_TABLES_PROPERTY);

/*        @ExtensionMetadataProperty(applicable=Procedure.class, datatype=String.class, display="Http Method",
 *                                                       description="Http method used for procedure invocation", required=true)
 *        public static final String HTTP_METHOD = MetadataFactory.ODATA_URI+"HttpMethod"; //$NON-NLS-1$
 */
        PropertyType httpMethod = createMetaClass(procMetaClass, HTTP_METHOD_PROPERTY);

/*        @ExtensionMetadataProperty(applicable=Column.class, datatype=Boolean.class, display="Join Column",
 *                                                       description="On Link tables this property defines the join column")    
 *        public static final String JOIN_COLUMN = MetadataFactory.ODATA_URI+"JoinColumn"; //$NON-NLS-1$
 */        
        PropertyType joinColumn = createMetaClass(colMetaClass, JOIN_COLUMN_PROPERTY);

/*        @ExtensionMetadataProperty(applicable= {Table.class, Procedure.class}, datatype=String.class, 
 *                                                       display="Entity Type Name", description="Name of the Entity Type in EDM", required=true)    
 *        public static final String ENTITY_TYPE = MetadataFactory.ODATA_URI+"EntityType"; //$NON-NLS-1$
 */
        // Added to tableMetaClass
        PropertyType entityType1 = createMetaClass(tableMetaClass, ENTITY_TYPE_TABLE_PROPERTY);

        // Added to procMetaClass
        PropertyType entityType2 = createMetaClass(procMetaClass, ENTITY_TYPE_PROC_PROPERTY);

/*        @ExtensionMetadataProperty(applicable=Column.class, datatype=String.class, display="Complex Type Name",
 *                                                       description="Name of the Complex Type in EDM")
 *        public static final String COMPLEX_TYPE = MetadataFactory.ODATA_URI+"ComplexType"; //$NON-NLS-1$
 */
        PropertyType complexType = createMetaClass(colMetaClass, COMPLEX_TYPE_PROPERTY);

/*        @ExtensionMetadataProperty(applicable=Column.class, datatype=String.class, display="Column Group",
 *                                                       description="Name of the Column Group")
 *        public static final String COLUMN_GROUP = MetadataFactory.ODATA_URI+"ColumnGroup"; //$NON-NLS-1$
 */
        PropertyType colGroupType = createMetaClass(colMetaClass, COLUMN_GROUP_PROPERTY);

        /* Perform test */
        File outputFile = File.createTempFile(this.getClass().getSimpleName(), StringConstants.DOT + StringConstants.XML);
        outputFile.deleteOnExit();
        FileOutputStream output = new FileOutputStream(outputFile);
        convertor.write("odata", null, ModelType.Type.PHYSICAL, metaClasses, output);
        output.close();

        InputStream inputStream = new FileInputStream(outputFile);
        Reader in = new InputStreamReader(inputStream,"UTF-8");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource(in);
        is.setEncoding("UTF-8");
        Document document = builder.parse(is);

        Element docElement = document.getDocumentElement();
        assertEquals(Tags.MODEL_EXTENSION.tag(), docElement.getNodeName());

        NodeList modelTypes = docElement.getElementsByTagName(Tags.MODEL_TYPE.tag());
        assertEquals(1, modelTypes.getLength());
        assertEquals(ModelType.Type.PHYSICAL.getName(), modelTypes.item(0).getTextContent());

        NodeList metaClassTypes = docElement.getElementsByTagName(Tags.EXT_META_CLASS.tag());
        assertEquals(3, metaClassTypes.getLength());

        int propertyElementsSeen = 0;
        for (int i = 0; i < metaClassTypes.getLength(); i++) {
            Node node = metaClassTypes.item(i);
            assertTrue(node instanceof Element);
            Element metaClassElement = (Element) node;
            String targetType = metaClassElement.getAttribute("name");

            NodeList propNodes = metaClassElement.getElementsByTagName(Tags.PROPERTY.tag());
            assertTrue(propNodes.getLength() > 0);
            propertyElementsSeen += propNodes.getLength();

            // Property Element
            for (int j = 0; j < propNodes.getLength(); ++j) {
                Node propNode = propNodes.item(j);
                assertTrue(propNode instanceof Element);
                Element propElement = (Element) propNode;
                String propName = propElement.getAttribute("name");

                if (linkTables.getName().equals(propName)) {
                    checkExpectedValues(linkTables, propElement);
                    assertEquals(tableMetaClass.getName(), targetType);
                } else if (httpMethod.getName().equals(propName)) {
                    checkExpectedValues(httpMethod, propElement);
                    assertEquals(procMetaClass.getName(), targetType);
                } else if (joinColumn.getName().equals(propName)) {
                    checkExpectedValues(joinColumn, propElement);
                    assertEquals(colMetaClass.getName(), targetType);
                } else if (entityType1.getName().equals(propName)) {

                    if (tableMetaClass.getName().equals(targetType))
                        checkExpectedValues(entityType1, propElement);
                    else if(procMetaClass.getName().equals(targetType))
                        checkExpectedValues(entityType2, propElement);
                    else
                        fail("entity type 1 or 2 is not targetting table or procedure");

                } else if (complexType.getName().equals(propName)) {
                    checkExpectedValues(complexType, propElement);
                    assertEquals(colMetaClass.getName(), targetType);
                } else if (colGroupType.getName().equals(propName)) {
                    checkExpectedValues(colGroupType, propElement);
                    assertEquals(colMetaClass.getName(), targetType);
                }
            }
        }

        assertEquals(7, propertyElementsSeen);

        /* See if the result parses as expected */
        inputStream = new FileInputStream(outputFile);
        ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(ExtensionPlugin.getInstance()
                                                                                                          .getMedSchema());
        ModelExtensionDefinition med = parser.parse(inputStream, ExtensionPlugin.getInstance().createDefaultModelObjectExtensionAssistant());
        assertNotNull(med);
        for (String err : parser.getErrors()) {
            System.out.println("Parser Error: " + err);
        }
        assertTrue(parser.getErrors().isEmpty());
    }
}
