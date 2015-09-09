package org.teiid.designer.vdb.dynamic;
/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.teiid.core.designer.EclipseMock;
import org.teiid.core.designer.util.StringConstants;
import org.teiid.designer.core.ModelWorkspaceMock;
import org.teiid.designer.core.workspace.MockFileBuilder;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.ddl.importer.DdlImporter;
import org.teiid.designer.metamodels.core.ModelAnnotation;
import org.teiid.designer.metamodels.core.ModelType;
import org.teiid.designer.transformation.ddl.TeiidModelToDdlGenerator;

/**
 *
 */
@SuppressWarnings( {"nls", "javadoc"} )
public class TestModelToDdlGenerator implements StringConstants {
	private static final String TEIID_DIALECT = "TEIID"; //$NON-NLS-1$
	
    private static final String EMPTY_XMI_CONTENTS = EMPTY_STRING +
            "<?xml version=\"1.0\" encoding=\"ASCII\"?>" + NEW_LINE +
            "<xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" " +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:diagram=\"http://www.metamatrix.com/metamodels/Diagram\" " +
                "xmlns:mmcore=\"http://www.metamatrix.com/metamodels/Core\" " +
                "xmlns:relational=\"http://www.metamatrix.com/metamodels/Relational\">" + NEW_LINE +
                    "<mmcore:ModelAnnotation xmi:uuid=\"mmuuid:0863dd9d-c34b-4291-9099-0b84910fa4e5\" " +
                        "modelType=\"VIRTUAL\" " +
                        "primaryMetamodelUri=\"http://www.metamatrix.com/metamodels/Relational\"/>" + NEW_LINE +
            "</xmi:XMI>";
    
    private interface BQT2_TYPE_DDL {
    	String 	TYPE_INT = "type_int integer OPTIONS(NAMEINSOURCE '\"type_int\"', NATIVE_TYPE 'int', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_INTEGER = "type_integer integer OPTIONS(NAMEINSOURCE '\"type_integer\"', NATIVE_TYPE 'int', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_SMALLINT = "type_smallint short OPTIONS(NAMEINSOURCE '\"type_smallint\"', NATIVE_TYPE 'smallint', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_TINYINT = "type_tinyint byte OPTIONS(NAMEINSOURCE '\"type_tinyint\"', NATIVE_TYPE 'tinyint', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DECIMAL = "type_decimal bigdecimal OPTIONS(NAMEINSOURCE '\"type_decimal\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DECIMAL_5 = "type_decimal_5 bigdecimal OPTIONS(NAMEINSOURCE '\"type_decimal_5\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DECIMAL_5_5 = "type_decimal_5_5 bigdecimal OPTIONS(NAMEINSOURCE '\"type_decimal_5_5\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DECIMAL_PRECISION = "type_double_precision float OPTIONS(NAMEINSOURCE '\"type_double_precision\"', NATIVE_TYPE 'float', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_FLOAT = "type_float float OPTIONS(NAMEINSOURCE '\"type_float\"', NATIVE_TYPE 'float', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_FLOAT_10 = "type_float_10 float OPTIONS(NAMEINSOURCE '\"type_float_10\"', NATIVE_TYPE 'real', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_NUMERIC = "type_numeric bigdecimal OPTIONS(NAMEINSOURCE '\"type_numeric\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_NUMERIC_5 = "type_numeric_5 bigdecimal OPTIONS(NAMEINSOURCE '\"type_numeric_5\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_NUMERIC_5_5 = "type_numeric_5_5 bigdecimal OPTIONS(NAMEINSOURCE '\"type_numeric_5_5\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_REAL = "type_real float OPTIONS(NAMEINSOURCE '\"type_real\"', NATIVE_TYPE 'real', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_BIT = "type_bit boolean OPTIONS(NAMEINSOURCE '\"type_bit\"', NATIVE_TYPE 'bit', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_CHARACTER = "type_character char(1) OPTIONS(NAMEINSOURCE '\"type_character\"', NATIVE_TYPE 'char', FIXED_LENGTH true)";
    	String 	TYPE_CHARACTER_10 = "type_character_10 string(10) OPTIONS(NAMEINSOURCE '\"type_character_10\"', NATIVE_TYPE 'char', FIXED_LENGTH true)";
    	String 	TYPE_CHAR = "type_char char(1) OPTIONS(NAMEINSOURCE '\"type_char\"', NATIVE_TYPE 'char', FIXED_LENGTH true)";
    	String 	TYPE_CHAR_10 = "type_char_10 string(10) OPTIONS(NAMEINSOURCE '\"type_char_10\"', NATIVE_TYPE 'char', FIXED_LENGTH true)";
    	String 	TYPE_NCHAR = "type_nchar string(1) OPTIONS(NAMEINSOURCE '\"type_nchar\"', NATIVE_TYPE 'nchar', FIXED_LENGTH true)";
    	String 	TYPE_NCHAR_10 = "type_nchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_nchar_10\"', NATIVE_TYPE 'nchar', FIXED_LENGTH true)";
    	String 	TYPE_VARCHAR = "type_varchar string(1) OPTIONS(NAMEINSOURCE '\"type_varchar\"', NATIVE_TYPE 'varchar')";
    	String 	TYPE_VARCHAR_10 = "type_varchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_varchar_10\"', NATIVE_TYPE 'varchar')";
    	String 	TYPE_LONG_NVARCHAR = "type_long_nvarchar string(1) OPTIONS(NAMEINSOURCE '\"type_long_nvarchar\"', NATIVE_TYPE 'nvarchar', FIXED_LENGTH true)";
    	String 	TYPE_LONG_NVARCHAR_10 = "type_long_nvarchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_long_nvarchar_10\"', NATIVE_TYPE 'nvarchar', FIXED_LENGTH true)";
    	String 	TYPE_TEXT = "type_text clob(2147483647) OPTIONS(NAMEINSOURCE '\"type_text\"', NATIVE_TYPE 'text', CASE_SENSITIVE 'FALSE', SEARCHABLE 'LIKE_ONLY')";
    	String 	TYPE_MONEY = "type_money bigdecimal OPTIONS(NAMEINSOURCE '\"type_money\"', NATIVE_TYPE 'money', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_SMALLMONEY = "type_smallmoney bigdecimal OPTIONS(NAMEINSOURCE '\"type_smallmoney\"', NATIVE_TYPE 'smallmoney', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DATETIME = "type_datetime timestamp OPTIONS(NAMEINSOURCE '\"type_datetime\"', NATIVE_TYPE 'datetime', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_BINARY = "type_binary object(1) OPTIONS(NAMEINSOURCE '\"type_binary\"', NATIVE_TYPE 'binary', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'UNSEARCHABLE')";
    	String 	TYPE_BINARY_2 = "type_binary_2 object(2) OPTIONS(NAMEINSOURCE '\"type_binary_2\"', NATIVE_TYPE 'binary', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'UNSEARCHABLE')";
    	String 	TYPE_IMAGE = "type_image blob(2147483647) OPTIONS(NAMEINSOURCE '\"type_image\"', NATIVE_TYPE 'image', CASE_SENSITIVE 'FALSE', SEARCHABLE 'UNSEARCHABLE')";
    	String 	TYPE_VARBINARY = "type_varbinary string(1) OPTIONS(NAMEINSOURCE '\"type_varbinary\"', NATIVE_TYPE 'varbinary')";
    }
    
    private interface EXPECTED_BQT2_TYPE_DDL {
    	String 	TYPE_INT = "type_int biginteger OPTIONS(NAMEINSOURCE '\"type_int\"', NATIVE_TYPE 'int', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_INTEGER = "type_integer biginteger OPTIONS(NAMEINSOURCE '\"type_integer\"', NATIVE_TYPE 'int', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_SMALLINT = "type_smallint short OPTIONS(NAMEINSOURCE '\"type_smallint\"', NATIVE_TYPE 'smallint', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_TINYINT = "type_tinyint byte OPTIONS(NAMEINSOURCE '\"type_tinyint\"', NATIVE_TYPE 'tinyint', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DECIMAL = "type_decimal bigdecimal(20) OPTIONS(NAMEINSOURCE '\"type_decimal\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DECIMAL_5 = "type_decimal_5 bigdecimal(20) OPTIONS(NAMEINSOURCE '\"type_decimal_5\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DECIMAL_5_5 = "type_decimal_5_5 bigdecimal(20) OPTIONS(NAMEINSOURCE '\"type_decimal_5_5\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DECIMAL_PRECISION = "type_double_precision float OPTIONS(NAMEINSOURCE '\"type_double_precision\"', NATIVE_TYPE 'float', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_FLOAT = "type_float float OPTIONS(NAMEINSOURCE '\"type_float\"', NATIVE_TYPE 'float', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_FLOAT_10 = "type_float_10 float OPTIONS(NAMEINSOURCE '\"type_float_10\"', NATIVE_TYPE 'real', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_NUMERIC = "type_numeric bigdecimal(20) OPTIONS(NAMEINSOURCE '\"type_numeric\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_NUMERIC_5 = "type_numeric_5 bigdecimal(20) OPTIONS(NAMEINSOURCE '\"type_numeric_5\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_NUMERIC_5_5 = "type_numeric_5_5 bigdecimal(20) OPTIONS(NAMEINSOURCE '\"type_numeric_5_5\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_REAL = "type_real float OPTIONS(NAMEINSOURCE '\"type_real\"', NATIVE_TYPE 'real', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_BIT = "type_bit boolean OPTIONS(NAMEINSOURCE '\"type_bit\"', NATIVE_TYPE 'bit', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_CHARACTER = "type_character char(1) OPTIONS(NAMEINSOURCE '\"type_character\"', NATIVE_TYPE 'char', FIXED_LENGTH 'TRUE')";
    	String 	TYPE_CHARACTER_10 = "type_character_10 string(10) OPTIONS(NAMEINSOURCE '\"type_character_10\"', NATIVE_TYPE 'char', FIXED_LENGTH 'TRUE')";
    	String 	TYPE_CHAR = "type_char char(1) OPTIONS(NAMEINSOURCE '\"type_char\"', NATIVE_TYPE 'char', FIXED_LENGTH 'TRUE')";
    	String 	TYPE_CHAR_10 = "type_char_10 string(10) OPTIONS(NAMEINSOURCE '\"type_char_10\"', NATIVE_TYPE 'char', FIXED_LENGTH 'TRUE')";
    	String 	TYPE_NCHAR = "type_nchar string(1) OPTIONS(NAMEINSOURCE '\"type_nchar\"', NATIVE_TYPE 'nchar', FIXED_LENGTH 'TRUE')";
    	String 	TYPE_NCHAR_10 = "type_nchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_nchar_10\"', NATIVE_TYPE 'nchar', FIXED_LENGTH 'TRUE')";
    	String 	TYPE_VARCHAR = "type_varchar string(1) OPTIONS(NAMEINSOURCE '\"type_varchar\"', NATIVE_TYPE 'varchar')";
    	String 	TYPE_VARCHAR_10 = "type_varchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_varchar_10\"', NATIVE_TYPE 'varchar')";
    	String 	TYPE_LONG_NVARCHAR = "type_long_nvarchar string(1) OPTIONS(NAMEINSOURCE '\"type_long_nvarchar\"', NATIVE_TYPE 'nvarchar', FIXED_LENGTH 'TRUE')";
    	String 	TYPE_LONG_NVARCHAR_10 = "type_long_nvarchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_long_nvarchar_10\"', NATIVE_TYPE 'nvarchar', FIXED_LENGTH 'TRUE')";
    	String 	TYPE_TEXT = "type_text clob(2147483647) OPTIONS(NAMEINSOURCE '\"type_text\"', NATIVE_TYPE 'text', CASE_SENSITIVE 'FALSE', SEARCHABLE 'LIKE_ONLY')";
    	String 	TYPE_MONEY = "type_money bigdecimal(20) OPTIONS(NAMEINSOURCE '\"type_money\"', NATIVE_TYPE 'money', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_SMALLMONEY = "type_smallmoney bigdecimal(20) OPTIONS(NAMEINSOURCE '\"type_smallmoney\"', NATIVE_TYPE 'smallmoney', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_DATETIME = "type_datetime timestamp OPTIONS(NAMEINSOURCE '\"type_datetime\"', NATIVE_TYPE 'datetime', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')";
    	String 	TYPE_BINARY = "type_binary object(1) OPTIONS(NAMEINSOURCE '\"type_binary\"', NATIVE_TYPE 'binary', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'UNSEARCHABLE')";
    	String 	TYPE_BINARY_2 = "type_binary_2 object(2) OPTIONS(NAMEINSOURCE '\"type_binary_2\"', NATIVE_TYPE 'binary', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'UNSEARCHABLE')";
    	String 	TYPE_IMAGE = "type_image blob(2147483647) OPTIONS(NAMEINSOURCE '\"type_image\"', NATIVE_TYPE 'image', CASE_SENSITIVE 'FALSE', SEARCHABLE 'UNSEARCHABLE')";
    	String 	TYPE_VARBINARY = "type_varbinary string(1) OPTIONS(NAMEINSOURCE '\"type_varbinary\"', NATIVE_TYPE 'varbinary')";
    }

    private String resourceName = "testModel";

    private TeiidModelToDdlGenerator generator;

    private EclipseMock eclipseMock;

    private ModelWorkspaceMock modelWorkspaceMock;

    @Before
    public void setup() throws Exception {
        generator = new TeiidModelToDdlGenerator();

        eclipseMock = new EclipseMock();
        modelWorkspaceMock = new ModelWorkspaceMock(eclipseMock);
    }

    @After
    public void tearDown() throws Exception {
        // Disposes the eclipse mock as well
        modelWorkspaceMock.dispose();
        modelWorkspaceMock = null;
        eclipseMock = null;
    }

    private String removeWhitespace(String value) {
        value = value.replaceAll("\\s+", SPACE);
        value = value.replaceAll("\\s\\)", CLOSE_BRACKET);
        value = value.replaceAll("\\(\\s", OPEN_BRACKET);
        return value.trim();
    }

    private MockFileBuilder createEmptyXmiFile() throws Exception, IOException {
        //
        // Set up the mock resource using a temp file.
        // In order for it to qualify as a model file, need to copy into it
        // some preliminary xml so that's its header is identifiable.
        //
        MockFileBuilder modelBuilder = new MockFileBuilder(resourceName, XMI);
        FileWriter writer = null;
        try {
            writer = new FileWriter(modelBuilder.getRealFile());
            writer.write(EMPTY_XMI_CONTENTS);
        } finally {
            if (writer != null)
                writer.close();
        }

        assertTrue(ModelUtil.isModelFile(modelBuilder.getPath()));
        return modelBuilder;
    }

    private ModelResource createModelResource(String ddl, boolean isVirtual) throws Exception {
        NullProgressMonitor monitor = new NullProgressMonitor();

        MockFileBuilder modelBuilder = createEmptyXmiFile();

        IPath path = new Path(File.separator + modelBuilder.getProject().getName() + File.separator + modelBuilder.getName());
        when(eclipseMock.workspaceRoot().findMember(path)).thenReturn(modelBuilder.getResourceFile());
        when(eclipseMock.workspace().validateName(isA(String.class), anyInt())).thenReturn(Status.OK_STATUS);

        ModelWorkspaceManager workspaceManager = ModelWorkspaceManager.getModelWorkspaceManager();
        ModelResource modelResource = (ModelResource) workspaceManager.findModelWorkspaceItem(modelBuilder.getResourceFile(), true);
        assertNotNull(modelResource);

        //
        // Apply the model annotation, necessary to allow the ddl importer to setModelName() correctly
        //
        ModelAnnotation annotation = modelResource.getModelAnnotation();
        assertNotNull(annotation);

        //
        // Save the model resource just in case
        //
        modelResource.save(monitor, false);

        //
        // Import the ddl using the ddl importer
        //
        DdlImporter importer = new DdlImporter(new IProject[] { modelBuilder.getProject() });
        importer.setSpecifiedParser(TEIID_DIALECT);
        
        importer.setModelFolder(modelBuilder.getProject());
        importer.setModelName(modelBuilder.getName());
        if( isVirtual ) {
        	importer.setModelType(ModelType.VIRTUAL_LITERAL);
        } else {
        	importer.setModelType(ModelType.PHYSICAL_LITERAL);
        }
        importer.importDdl(ddl, monitor, 1, new Properties());

        assertFalse(importer.noDdlImported());
        assertNull(importer.getParseErrorMessage());

        importer.save(monitor, 1);
        assertTrue(importer.getImportStatus().isOK());

        ModelResource mResource = modelWorkspaceMock.getModelEditor().findModelResource(modelBuilder.getResourceFile());
        assertNotNull(mResource);

        return mResource;
    }

    private String roundTrip(String ddl, boolean isVirtual) throws Exception, ModelWorkspaceException {
        ModelResource modelResource = createModelResource(ddl, isVirtual);
        String generatedDdl = generator.generate(modelResource);
        generatedDdl = removeWhitespace(generatedDdl);
        return generatedDdl;
    }

    @Test
    public void testSimpleColumns() throws Exception {
        String ddl = "CREATE VIEW StockPrices (" + NEW_LINE +
                            "symbol string," + NEW_LINE +
                            "price bigdecimal" + NEW_LINE +
                            ") AS SELECT * FROM Stock;";

        // TODO
        // Should these column option clauses be included if all the values are defaults???
        String expectedDdl = "CREATE VIEW StockPrices (" +
                                             "symbol string(10)" + COMMA + SPACE +
                                             "price bigdecimal(20)" +
                                             ") AS SELECT * FROM Stock;"; 

        String generatedDdl = roundTrip(ddl, true);
        assertEquals(expectedDdl, generatedDdl);
    }

    @Test
    public void testColumnProperties() throws Exception {
        String ddl = "CREATE VIEW StockPrices (" + NEW_LINE +
                            "symbol string(10) NOT NULL AUTO_INCREMENT, " + NEW_LINE +
                            "price bigdecimal(1) DEFAULT 10, " + NEW_LINE +
                            "company string(10) NOT NULL, " + NEW_LINE +
                            "companyID string(10) NOT NULL INDEX," + NEW_LINE +
                            "CONSTRAINT STOCK_PK PRIMARY KEY(symbol)," + NEW_LINE +
                            "CONSTRAINT STOCK_UC UNIQUE(company)" + NEW_LINE +
                            ") AS SELECT * FROM Stock;";

        

        String expectedDdl = "CREATE VIEW StockPrices (" +
                                             "symbol string(10) NOT NULL AUTO_INCREMENT" + COMMA + SPACE +
                                             "price bigdecimal(1) DEFAULT 10" + COMMA + SPACE +
                                             "company string(10) NOT NULL" + COMMA + SPACE +
                                             "companyID string(10) NOT NULL INDEX" + COMMA + SPACE +
                                             "CONSTRAINT STOCK_PK PRIMARY KEY(symbol)" + COMMA + SPACE +
                                             "CONSTRAINT STOCK_UC UNIQUE(company)" +
                                             ") AS SELECT * FROM Stock;";

        String generatedDdl = roundTrip(ddl, true);
        assertEquals(expectedDdl, generatedDdl);
    }
    
//    @Test
//    public void testBqt2ColumnProperties() throws Exception {
//        String ddl = "CREATE FOREIGN TABLE ALL_TYPES (" +
//	"type_int integer OPTIONS(NAMEINSOURCE '\"type_int\"', NATIVE_TYPE 'int', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE + 
//	"type_integer integer OPTIONS(NAMEINSOURCE '\"type_integer\"', NATIVE_TYPE 'int', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_smallint short OPTIONS(NAMEINSOURCE '\"type_smallint\"', NATIVE_TYPE 'smallint', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_tinyint byte OPTIONS(NAMEINSOURCE '\"type_tinyint\"', NATIVE_TYPE 'tinyint', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_decimal bigdecimal OPTIONS(NAMEINSOURCE '\"type_decimal\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_decimal_5 bigdecimal OPTIONS(NAMEINSOURCE '\"type_decimal_5\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_decimal_5_5 bigdecimal OPTIONS(NAMEINSOURCE '\"type_decimal_5_5\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_double_precision float OPTIONS(NAMEINSOURCE '\"type_double_precision\"', NATIVE_TYPE 'float', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_float float OPTIONS(NAMEINSOURCE '\"type_float\"', NATIVE_TYPE 'float', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_float_10 float OPTIONS(NAMEINSOURCE '\"type_float_10\"', NATIVE_TYPE 'real', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_numeric bigdecimal OPTIONS(NAMEINSOURCE '\"type_numeric\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_numeric_5 bigdecimal OPTIONS(NAMEINSOURCE '\"type_numeric_5\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_numeric_5_5 bigdecimal OPTIONS(NAMEINSOURCE '\"type_numeric_5_5\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_real float OPTIONS(NAMEINSOURCE '\"type_real\"', NATIVE_TYPE 'real', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_bit boolean OPTIONS(NAMEINSOURCE '\"type_bit\"', NATIVE_TYPE 'bit', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_character char(1) OPTIONS(NAMEINSOURCE '\"type_character\"', NATIVE_TYPE 'char', FIXED_LENGTH true)" + COMMA + NEW_LINE +
//	"type_character_10 string(10) OPTIONS(NAMEINSOURCE '\"type_character_10\"', NATIVE_TYPE 'char', FIXED_LENGTH true)" + COMMA + NEW_LINE +
//	"type_char char(1) OPTIONS(NAMEINSOURCE '\"type_char\"', NATIVE_TYPE 'char', FIXED_LENGTH true)" + COMMA + NEW_LINE +
//	"type_char_10 string(10) OPTIONS(NAMEINSOURCE '\"type_char_10\"', NATIVE_TYPE 'char', FIXED_LENGTH true)" + COMMA + NEW_LINE +
//	"type_nchar string(1) OPTIONS(NAMEINSOURCE '\"type_nchar\"', NATIVE_TYPE 'nchar', FIXED_LENGTH true)" + COMMA + NEW_LINE +
//	"type_nchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_nchar_10\"', NATIVE_TYPE 'nchar', FIXED_LENGTH true)" + COMMA + NEW_LINE +
//	"type_varchar string(1) OPTIONS(NAMEINSOURCE '\"type_varchar\"', NATIVE_TYPE 'varchar')" + COMMA + NEW_LINE +
//	"type_varchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_varchar_10\"', NATIVE_TYPE 'varchar')" + COMMA + NEW_LINE +
//	"type_long_nvarchar string(1) OPTIONS(NAMEINSOURCE '\"type_long_nvarchar\"', NATIVE_TYPE 'nvarchar', FIXED_LENGTH true)" + COMMA + NEW_LINE +
//	"type_long_nvarchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_long_nvarchar_10\"', NATIVE_TYPE 'nvarchar', FIXED_LENGTH true)" + COMMA + NEW_LINE +
//	"type_text clob(2147483647) OPTIONS(NAMEINSOURCE '\"type_text\"', NATIVE_TYPE 'text', CASE_SENSITIVE 'FALSE', SEARCHABLE 'LIKE_ONLY')" + COMMA + NEW_LINE +
//	"type_money bigdecimal OPTIONS(NAMEINSOURCE '\"type_money\"', NATIVE_TYPE 'money', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_smallmoney bigdecimal OPTIONS(NAMEINSOURCE '\"type_smallmoney\"', NATIVE_TYPE 'smallmoney', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_datetime timestamp OPTIONS(NAMEINSOURCE '\"type_datetime\"', NATIVE_TYPE 'datetime', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + NEW_LINE +
//	"type_binary object(1) OPTIONS(NAMEINSOURCE '\"type_binary\"', NATIVE_TYPE 'binary', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'UNSEARCHABLE')" + COMMA + NEW_LINE +
//	"type_binary_2 object(2) OPTIONS(NAMEINSOURCE '\"type_binary_2\"', NATIVE_TYPE 'binary', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'UNSEARCHABLE')" + COMMA + NEW_LINE +
//	"type_image blob(2147483647) OPTIONS(NAMEINSOURCE '\"type_image\"', NATIVE_TYPE 'image', CASE_SENSITIVE 'FALSE', SEARCHABLE 'UNSEARCHABLE')" + COMMA + NEW_LINE +
//	"type_varbinary string(1) OPTIONS(NAMEINSOURCE '\"type_varbinary\"', NATIVE_TYPE 'varbinary')"
//") OPTIONS(NAMEINSOURCE '\"bqt2\".\"BQT2\".\"ALL_TYPES\"')";

        

//        String expectedDdl =  "CREATE FOREIGN TABLE ALL_TYPES (" +
//        		"type_int integer OPTIONS(NAMEINSOURCE '\"type_int\"', NATIVE_TYPE 'int', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE + 
//        		"type_integer integer OPTIONS(NAMEINSOURCE '\"type_integer\"', NATIVE_TYPE 'int', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_smallint short OPTIONS(NAMEINSOURCE '\"type_smallint\"', NATIVE_TYPE 'smallint', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_tinyint byte OPTIONS(NAMEINSOURCE '\"type_tinyint\"', NATIVE_TYPE 'tinyint', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_decimal bigDecimal(1) OPTIONS(NAMEINSOURCE '\"type_decimal\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_decimal_5 bigDecimal(1) OPTIONS(NAMEINSOURCE '\"type_decimal_5\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_decimal_5_5 bigDecimal(1) OPTIONS(NAMEINSOURCE '\"type_decimal_5_5\"', NATIVE_TYPE 'decimal', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_double_precision float OPTIONS(NAMEINSOURCE '\"type_double_precision\"', NATIVE_TYPE 'float', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_float float OPTIONS(NAMEINSOURCE '\"type_float\"', NATIVE_TYPE 'float', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_float_10 float OPTIONS(NAMEINSOURCE '\"type_float_10\"', NATIVE_TYPE 'real', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_numeric bigDecimal(1) OPTIONS(NAMEINSOURCE '\"type_numeric\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_numeric_5 bigDecimal(1) OPTIONS(NAMEINSOURCE '\"type_numeric_5\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_numeric_5_5 bigDecimal(1) OPTIONS(NAMEINSOURCE '\"type_numeric_5_5\"', NATIVE_TYPE 'numeric', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_real float OPTIONS(NAMEINSOURCE '\"type_real\"', NATIVE_TYPE 'real', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_bit boolean OPTIONS(NAMEINSOURCE '\"type_bit\"', NATIVE_TYPE 'bit', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_character char(1) OPTIONS(NAMEINSOURCE '\"type_character\"', NATIVE_TYPE 'char', FIXED_LENGTH true)" + COMMA + SPACE +
//        		"type_character_10 string(10) OPTIONS(NAMEINSOURCE '\"type_character_10\"', NATIVE_TYPE 'char', FIXED_LENGTH true)" + COMMA + SPACE +
//        		"type_char char(1) OPTIONS(NAMEINSOURCE '\"type_char\"', NATIVE_TYPE 'char', FIXED_LENGTH true)" + COMMA + SPACE +
//        		"type_char_10 string(10) OPTIONS(NAMEINSOURCE '\"type_char_10\"', NATIVE_TYPE 'char', FIXED_LENGTH true)" + COMMA + SPACE +
//        		"type_nchar string(1) OPTIONS(NAMEINSOURCE '\"type_nchar\"', NATIVE_TYPE 'nchar', FIXED_LENGTH true)" + COMMA + SPACE +
//        		"type_nchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_nchar_10\"', NATIVE_TYPE 'nchar', FIXED_LENGTH true)" + COMMA + SPACE +
//        		"type_varchar string(1) OPTIONS(NAMEINSOURCE '\"type_varchar\"', NATIVE_TYPE 'varchar')" + COMMA + SPACE +
//        		"type_varchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_varchar_10\"', NATIVE_TYPE 'varchar')" + COMMA + SPACE +
//        		"type_long_nvarchar string(1) OPTIONS(NAMEINSOURCE '\"type_long_nvarchar\"', NATIVE_TYPE 'nvarchar', FIXED_LENGTH true)" + COMMA + SPACE +
//        		"type_long_nvarchar_10 string(10) OPTIONS(NAMEINSOURCE '\"type_long_nvarchar_10\"', NATIVE_TYPE 'nvarchar', FIXED_LENGTH true)" + COMMA + SPACE +
//        		"type_text clob(2147483647) OPTIONS(NAMEINSOURCE '\"type_text\"', NATIVE_TYPE 'text', CASE_SENSITIVE 'FALSE', SEARCHABLE 'LIKE_ONLY')" + COMMA + SPACE +
//        		"type_money bigDecimal(1) OPTIONS(NAMEINSOURCE '\"type_money\"', NATIVE_TYPE 'money', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_smallmoney bigDecimal(1) OPTIONS(NAMEINSOURCE '\"type_smallmoney\"', NATIVE_TYPE 'smallmoney', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_datetime timestamp OPTIONS(NAMEINSOURCE '\"type_datetime\"', NATIVE_TYPE 'datetime', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'ALL_EXCEPT_LIKE')" + COMMA + SPACE +
//        		"type_binary object(1) OPTIONS(NAMEINSOURCE '\"type_binary\"', NATIVE_TYPE 'binary', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'UNSEARCHABLE')" + COMMA + SPACE +
//        		"type_binary_2 object(2) OPTIONS(NAMEINSOURCE '\"type_binary_2\"', NATIVE_TYPE 'binary', CASE_SENSITIVE 'FALSE', FIXED_LENGTH 'TRUE', SEARCHABLE 'UNSEARCHABLE')" + COMMA + SPACE +
//        		"type_image blob(2147483647) OPTIONS(NAMEINSOURCE '\"type_image\"', NATIVE_TYPE 'image', CASE_SENSITIVE 'FALSE', SEARCHABLE 'UNSEARCHABLE')" + COMMA + SPACE +
//        		"type_varbinary string(1) OPTIONS(NAMEINSOURCE '\"type_varbinary\"', NATIVE_TYPE 'varbinary')"+
//        	") OPTIONS(NAMEINSOURCE '\"bqt2\".\"BQT2\".\"ALL_TYPES\"')";
//
//        String generatedDdl = roundTrip(ddl, false);
//        assertEquals(expectedDdl, generatedDdl);
//    }
    
    private void print_2_strings(String str1, String str2) {
    	System.out.println("\n[1] =>> " + str1  );
    	System.out.println("[2] =>> " + str2  + "\n");
    }
    
    @Test
    public void testDdl_TYPE_BINARY() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_BINARY + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_BINARY + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_BINARY_2() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_BINARY_2 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_BINARY_2 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_BIT() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_BIT + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_BIT + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_CHAR() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_CHAR + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_CHAR + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_CHAR_10() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_CHAR_10 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_CHAR_10 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_CHARACTER() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_CHARACTER + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_CHARACTER + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_CHARACTER_10() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_CHARACTER_10 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_CHARACTER_10 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_DATETIME() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_DATETIME + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_DATETIME + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_DECIMAL() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_DECIMAL + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_DECIMAL + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_DECIMAL_5() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_DECIMAL_5 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_DECIMAL_5 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_DECIMAL_5_5() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_DECIMAL_5_5 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_DECIMAL_5_5 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_DECIMAL_PRECISION() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_DECIMAL_PRECISION + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_DECIMAL_PRECISION + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_FLOAT() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_FLOAT + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_FLOAT + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_FLOAT_10() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_FLOAT_10 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_FLOAT_10 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_IMAGE() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_IMAGE + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_IMAGE + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_INT() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_INT + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_INT + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_INTEGER() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_INTEGER + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_INTEGER + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_LONG_NVARCHAR() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_LONG_NVARCHAR + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_LONG_NVARCHAR + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_LONG_NVARCHAR_10() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_LONG_NVARCHAR_10 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_LONG_NVARCHAR_10 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_MONEY() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_MONEY + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_MONEY + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_NCHAR() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_NCHAR + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_NCHAR + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_NCHAR_10() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_NCHAR_10 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_NCHAR_10 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_NUMERIC() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_NUMERIC + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_NUMERIC + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_NUMERIC_5() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_NUMERIC_5 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_NUMERIC_5 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_NUMERIC_5_5() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_NUMERIC_5_5 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_NUMERIC_5_5 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_REAL() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_REAL + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_REAL + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_SMALLINT() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_SMALLINT + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_SMALLINT + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_SMALLMONEY() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_SMALLMONEY + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_SMALLMONEY + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_TEXT() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_TEXT + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_TEXT + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_TINYINT() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_TINYINT + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_TINYINT + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_VARBINARY() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_VARBINARY + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_VARBINARY + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_VARCHAR() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_VARCHAR + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_VARCHAR + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
    
    @Test
    public void testDdl_TYPE_VARCHAR_10() throws Exception {
        String ddl = "CREATE FOREIGN TABLE ONE_TYPE (" + BQT2_TYPE_DDL.TYPE_VARCHAR_10 + ");";
        String expectedDdl =  "CREATE FOREIGN TABLE ONE_TYPE (" + EXPECTED_BQT2_TYPE_DDL.TYPE_VARCHAR_10 + ")";

        String generatedDdl = roundTrip(ddl, false);
        assertEquals(expectedDdl, generatedDdl);
    }
}
