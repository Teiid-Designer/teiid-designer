/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.metadata;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.teiid.api.exception.query.QueryMetadataException;
import com.metamatrix.core.index.CompositeIndexSelector;
import com.metamatrix.core.index.RuntimeIndexSelector;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.metadata.runtime.ColumnRecord;
import com.metamatrix.modeler.core.metadata.runtime.ColumnSetRecord;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord;
import com.metamatrix.modeler.core.metadata.runtime.TableRecord;
import org.teiid.query.mapping.relational.QueryNode;
import org.teiid.query.mapping.xml.MappingNode;
import org.teiid.query.metadata.QueryMetadataInterface;
import org.teiid.query.metadata.StoredProcedureInfo;

/**
 * TestServerRuntimeMetadata
 */
public class TestServerRuntimeMetadata extends TestCase {

    private static final String PARTS_VDB = "Parts.vdb"; //$NON-NLS-1$
    private static final String BQT_VDB = "BQT.vdb"; //$NON-NLS-1$
    private static final String XML_VDB = "XMLVdb.vdb"; //$NON-NLS-1$
	private static final String Annotation_VDB = "smallAVDB2.vdb"; //$NON-NLS-1$
	private static final String SOAP_VDB = "SOAPTest.vdb"; //$NON-NLS-1$	
	private static final String QUERY_TEST_VDB = "QT_DB2v72DS_1.vdb"; //$NON-NLS-1$
    private static final String BOOKS_VDB = "LotsaBooks.vdb"; //$NON-NLS-1$
	private static final String TXN_VDB = "TxnTest.vdb"; //$NON-NLS-1$
    private static final String BOOKS_SERVICE_VDB = "BooksWebService.vdb"; //$NON-NLS-1$
    private static final String RESOURCE_FOLDER = "indexTestFolder"; //$NON-NLS-1$
    private static final char DELIMITER = File.separatorChar;

    /**
     * Constructor for TestServerRuntimeMetadata.
     * 
     * @param name
     */
    public TestServerRuntimeMetadata(String name) {
        super(name);
    }

    /** 
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestServerRuntimeMetadata"); //$NON-NLS-1$
        suite.addTestSuite(TestServerRuntimeMetadata.class);
        //suite.addTest(new TestServerRuntimeMetadata("testXMLResultSetColumnPosition")); //$NON-NLS-1$
        //suite.addTest(new TestServerRuntimeMetadata("testGetElementIDsInKey13760")); //$NON-NLS-1$

        return new TestSetup(suite) { // junit.extensions package
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
            }
            @Override
            public void tearDown() {
            }
        };
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    public ServerRuntimeMetadata helpGetMetadata(String vdb) throws Exception {
        List<RuntimeIndexSelector> selectors = new ArrayList<RuntimeIndexSelector>();
        selectors.add(new RuntimeIndexSelector(SmartTestSuite.getTestDataPath()+DELIMITER+"/indexTestFolder/"+vdb));  //$NON-NLS-1$        
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        return new ServerRuntimeMetadata(context);
    }

    public ServerRuntimeMetadata helpGetMetadata( String vdb1,
                                                  String vdb2 ) throws Exception {
        List<RuntimeIndexSelector> selectors = new ArrayList<RuntimeIndexSelector>();
        selectors.add(new RuntimeIndexSelector(SmartTestSuite.getTestDataPath()+DELIMITER+"/indexTestFolder/"+vdb1));  //$NON-NLS-1$
        selectors.add(new RuntimeIndexSelector(SmartTestSuite.getTestDataPath()+DELIMITER+"/indexTestFolder/"+vdb2));  //$NON-NLS-1$
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        return new ServerRuntimeMetadata(context);
    }

    private void helpPrintResult(final Collection records) {
        if (records instanceof List) {
            Collections.sort((List)records,new MetadataRecordComparator());
        }
        for (Iterator iter = records.iterator(); iter.hasNext();) {
            MetadataRecord record = (MetadataRecord)iter.next();
            System.out.println(record.getFullName());
        }
    }

    private IndexSelector helpGetRuntimeSelector1(URL url) throws Exception {
        return new RuntimeIndexSelector(url);
    }
    
    private IndexSelector helpGetRuntimeSelector2(String filePath) throws Exception {
        return new RuntimeIndexSelector(filePath);
    }    
    
    private QueryMetadataInterface helpGetTransformatrionMetadata(IndexSelector selector)  {
        return TransformationMetadataFactory.getInstance().getServerMetadata(selector);
    }

    public void testGetElementID1() {
        try {
            String url = SmartTestSuite.getTestDataPath()+"/indexTestFolder/BQT.vdb"; //$NON-NLS-1$
            IndexSelector selector = helpGetRuntimeSelector1(new File(url).toURI().toURL());

            QueryMetadataInterface metadata = helpGetTransformatrionMetadata(selector);

            Object elementID = metadata.getElementID("bQT.bQt2.HuGEA.INTKEY"); //$NON-NLS-1$

            assertNotNull(elementID);
            assertTrue(elementID instanceof ColumnRecord);

            ColumnRecord record = (ColumnRecord) elementID;

            assertEquals("INTKEY", record.getName()); //$NON-NLS-1$
            assertEquals("BQT/BQT2/HUGEA/INTKEY", record.getPath());           //$NON-NLS-1$                      
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testGetElementID2() {
        try {
            String resourcePath =  SmartTestSuite.getTestDataPath()+DELIMITER+RESOURCE_FOLDER+DELIMITER+BQT_VDB; 
            IndexSelector selector = helpGetRuntimeSelector2(resourcePath);

            QueryMetadataInterface metadata = helpGetTransformatrionMetadata(selector);

            Object elementID = metadata.getElementID("bQT.bQt2.HuGEA.INTKEY"); //$NON-NLS-1$

            assertNotNull(elementID);
            assertTrue(elementID instanceof ColumnRecord);

            ColumnRecord record = (ColumnRecord) elementID;

            assertEquals("INTKEY", record.getName()); //$NON-NLS-1$
            assertEquals("BQT/BQT2/HUGEA/INTKEY", record.getPath());           //$NON-NLS-1$                      
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetTempGroupsForDocument() {
        try {
            String url = SmartTestSuite.getTestDataPath()+DELIMITER+"indexTestFolder/QueryTest2.vdb"; //$NON-NLS-1$
            IndexSelector selector = helpGetRuntimeSelector1(new File(url).toURI().toURL());

            QueryMetadataInterface metadata = helpGetTransformatrionMetadata(selector);

            Object groupID = metadata.getGroupID("XQTNestedDoc.testBoundTempTable"); //$NON-NLS-1$

            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);

            TableRecord record = (TableRecord) groupID;
            
            Collection tempGroups = metadata.getXMLTempGroups(record);
            
            assertEquals(2, tempGroups.size());
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }    

    public void testColumnRecord0() throws Exception {
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BQT_VDB);
            metadata.getElementID(null);
            fail("Expected Arg Check failure"); //$NON-NLS-1$
        } catch(Exception e) {
            // failure expected
        }
    }
    
    public void testGetElementIDsInKey13760() throws Exception {
        QueryMetadataInterface metadata = helpGetMetadata(QUERY_TEST_VDB);
        
        Object groupID = metadata.getGroupID("BQT2.SMALLB"); //$NON-NLS-1$

        Collection ukeys = metadata.getUniqueKeysInGroup(groupID);

        assertNotNull(ukeys);
        assertEquals(2, ukeys.size());
        for(final Iterator iter = ukeys.iterator(); iter.hasNext();) {
            ColumnSetRecord record = (ColumnSetRecord) iter.next();
            if(record.getFullName().equalsIgnoreCase("BQT2.SMALLB.Pk_SmallB")) { //$NON-NLS-1$
                Collection elements = metadata.getElementIDsInKey(record);
                assertNotNull(elements);
                assertEquals(1, elements.size());
                ColumnRecord colRecord = (ColumnRecord) elements.iterator().next();
                assertEquals("BQT2.SMALLB.INTKEY", colRecord.getFullName().toUpperCase()); //$NON-NLS-1$
            } else if(record.getFullName().equalsIgnoreCase("BQT2.SMALLB.Uk_SmallB")) { //$NON-NLS-1$
                Collection elements = metadata.getElementIDsInKey(record);
                assertNotNull(elements);
                assertEquals(1, elements.size());
                ColumnRecord colRecord = (ColumnRecord) elements.iterator().next();
                assertEquals("BQT2.SMALLB.STRINGKEY", colRecord.getFullName().toUpperCase());                 //$NON-NLS-1$
            }
        }
    }

    public void testGetGroupID1() throws Exception {
        ServerRuntimeMetadata metadata = helpGetMetadata(QUERY_TEST_VDB);

        Object groupID = metadata.getGroupID("mmuuid:8d2b0b00-3455-1dfa-9931-e83d04ce10a0"); //$NON-NLS-1$

        assertNotNull(groupID);
        assertTrue(groupID instanceof TableRecord);

        TableRecord record = (TableRecord) groupID;

        assertEquals("SMALLB", record.getName().toUpperCase()); //$NON-NLS-1$
        assertEquals("BQT2/SmallB", record.getPath());           //$NON-NLS-1$                      
    }    

    public void testGetElementID3() throws Exception {
        QueryMetadataInterface metadata = helpGetMetadata(BQT_VDB);

        Object elementID = metadata.getElementID("BQT.BQT2.HUGEA.INTKEY"); //$NON-NLS-1$

        assertNotNull(elementID);
        assertTrue(elementID instanceof ColumnRecord);

        ColumnRecord record = (ColumnRecord) elementID;

        assertEquals("INTKEY", record.getName()); //$NON-NLS-1$
        assertEquals("BQT/BQT2/HUGEA/INTKEY", record.getPath());           //$NON-NLS-1$                      
    }
    
    public void testGetElementID4() throws Exception {
        QueryMetadataInterface metadata = helpGetMetadata(PARTS_VDB);

        Object elementID = metadata.getElementID("PartsSupplier.PartsSupplier.Parts.Part_Color"); //$NON-NLS-1$

        assertNotNull(elementID);
        assertTrue(elementID instanceof ColumnRecord);

        ColumnRecord record = (ColumnRecord) elementID;

        assertEquals("PART_COLOR", record.getName()); //$NON-NLS-1$
        assertEquals("PartsSupplier/PARTSSUPPLIER/PARTS/PART_COLOR", record.getPath());           //$NON-NLS-1$                      
    }

    // defect 16920 
	public void testGetStroredProc16920 () throws Exception {
		QueryMetadataInterface metadata = helpGetMetadata(SOAP_VDB);

		try {
		    metadata.getStoredProcedureInfoForProcedure("UPD_ST_ADDR"); //$NON-NLS-1$
        } catch (QueryMetadataException e) {
        }// expected exception
	}

    public void testGetVirtualDatabaseName() throws Exception {
        QueryMetadataInterface metadata = helpGetMetadata(BQT_VDB);

        String vdbName = metadata.getVirtualDatabaseName();

        assertNotNull(vdbName);
        assertEquals("BQT", vdbName); //$NON-NLS-1$
    }

    public void testGetVirtualXmlPlan() throws Exception {
        QueryMetadataInterface metadata = helpGetMetadata(XML_VDB);
        Object groupID = metadata.getGroupID("BooksDoc.bookSetMixedDocument"); //$NON-NLS-1$
        assertNotNull(groupID);
        assertTrue(groupID instanceof TableRecord);

        QueryNode node = metadata.getVirtualPlan(groupID);
        assertNotNull(node);
    }

    public void testGetXmlSchemas() throws Exception {
        QueryMetadataInterface metadata = helpGetMetadata(XML_VDB);
        Object groupID = metadata.getGroupID("BooksDoc.bookSetMixedDocument"); //$NON-NLS-1$
        assertNotNull(groupID);
        assertTrue(groupID instanceof TableRecord);

        List schemas = metadata.getXMLSchemas(groupID);
        assertNotNull(schemas);
        assertEquals(1, schemas.size());
    }

    public void testGetMapping() throws Exception {
        QueryMetadataInterface metadata = helpGetMetadata(XML_VDB);
        Object groupID = metadata.getGroupID("BooksDoc.bookSetMixedDocument"); //$NON-NLS-1$
        assertNotNull(groupID);
        assertTrue(groupID instanceof TableRecord);
        MappingNode node = metadata.getMappingNode(groupID);
        assertNotNull(node);
        //assertEquals("BQT", vdbName); //$NON-NLS-1$
    }    

    public void testGetGroupsForPartialName() throws Exception {
        QueryMetadataInterface metadata = helpGetMetadata(BQT_VDB);

        Collection groups = metadata.getGroupsForPartialName("HUGEA"); //$NON-NLS-1$

        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals("BQT.BQT2.HUGEA", groups.iterator().next()); //$NON-NLS-1$
    }
    
	public void testExtensions() throws Exception {
		QueryMetadataInterface metadata = helpGetMetadata(Annotation_VDB);

		StoredProcedureInfo info = metadata.getStoredProcedureInfoForProcedure("smallA.smallA"); //$NON-NLS-1$
		Object procID = info.getProcedureID();
		assertNotNull(info.getProcedureID());		
		Properties props = metadata.getExtensionProperties(procID);
		assertNotNull(props);
	}

	public void testNativeType() throws Exception {
		QueryMetadataInterface metadata = helpGetMetadata(BQT_VDB);

        Object elementID = metadata.getElementID("BQT.BQT2.HUGEA.INTKEY"); //$NON-NLS-1$

        assertNotNull(elementID);
        assertTrue(elementID instanceof ColumnRecord);

        ColumnRecord record = (ColumnRecord) elementID;
		assertEquals("NUMBER", metadata.getNativeType(record)); //$NON-NLS-1$
	}
	
    public void testColumnRecordWithNullArg() {
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);
            metadata.getElementID(null);
            fail("Expected Arg Check failure"); //$NON-NLS-1$
        } catch(Exception e) {
            // failure expected
        }
    }

    public void testGetGroupID() {
        System.out.println("\nTestServerRuntimeMetadata.testGetGroupID()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("AuthorsDoc.authorBooksDocument"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);

            TableRecord record = (TableRecord) groupID;
            assertEquals("authorBooksDocument", record.getName()); //$NON-NLS-1$
            assertEquals("AuthorsDoc/authorBooksDocument", record.getPath());           //$NON-NLS-1$                      
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetElementIDsForGroupID() {
        System.out.println("\nTestServerRuntimeMetadata.testGetElementIDsForGroupID()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("AuthorsDoc.authorBooksDocument"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            List records = metadata.getElementIDsInGroupID(groupID);
            helpPrintResult(records);
            assertEquals(16,records.size());
            for (Iterator iter = records.iterator(); iter.hasNext();) {
                ColumnRecord element = (ColumnRecord)iter.next();
                assertNotNull( metadata.getElementID(element.getFullName()) );
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetElementIDsForGroupID2() {
        System.out.println("\nTestServerRuntimeMetadata.testGetElementIDsForGroupID2()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("AuthorsDoc.authorsBooksDocument"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            List records = metadata.getElementIDsInGroupID(groupID);
            helpPrintResult(records);
            assertEquals(17,records.size());
            for (Iterator iter = records.iterator(); iter.hasNext();) {
                ColumnRecord element = (ColumnRecord)iter.next();
                assertNotNull( metadata.getElementID(element.getFullName()) );
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetElementIDsForGroupID3() {
        System.out.println("\nTestServerRuntimeMetadata.testGetElementIDsForGroupID3()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("BOOKSO.BOOKS"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            List records = metadata.getElementIDsInGroupID(groupID);
            helpPrintResult(records);
            assertEquals(7,records.size());
            for (Iterator iter = records.iterator(); iter.hasNext();) {
                ColumnRecord element = (ColumnRecord)iter.next();
                assertNotNull( metadata.getElementID(element.getFullName()) );
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetElementIDsForGroupID4() {
        System.out.println("\nTestServerRuntimeMetadata.testGetElementIDsForGroupID4()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("BooksDoc.bookListingDocument"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            List records = metadata.getElementIDsInGroupID(groupID);
            helpPrintResult(records);
            assertEquals(9,records.size());
            for (Iterator iter = records.iterator(); iter.hasNext();) {
                ColumnRecord element = (ColumnRecord)iter.next();
                assertNotNull( metadata.getElementID(element.getFullName()) );
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetElementIDsForGroupID5() {
        System.out.println("\nTestServerRuntimeMetadata.testGetElementIDsForGroupID5()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("BooksDoc.bookSetMixedDocument.MappingClasses.bookSetMixed"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            List records = metadata.getElementIDsInGroupID(groupID);
            helpPrintResult(records);
            assertEquals(8,records.size());
            for (Iterator iter = records.iterator(); iter.hasNext();) {
                ColumnRecord element = (ColumnRecord)iter.next();
                assertNotNull( metadata.getElementID(element.getFullName()) );
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }


    public void testGetUniqueKeysInGroup() {
        System.out.println("\nTestServerRuntimeMetadata.testGetUniqueKeysInGroup()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("BOOKSO.BOOK_AUTHORS"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            Collection records = metadata.getUniqueKeysInGroup(groupID);
            assertEquals(1,records.size());
            helpPrintResult(records);
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetElementIDsInKey() {
        System.out.println("\nTestServerRuntimeMetadata.testGetElementIDsInKey()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("BOOKSO.BOOK_AUTHORS"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            Collection records = metadata.getUniqueKeysInGroup(groupID);
            assertEquals(1,records.size());
            MetadataRecord record = (MetadataRecord)records.iterator().next();
            records = metadata.getElementIDsInKey(record);
            assertEquals(2,records.size());
            helpPrintResult(records);
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetForeignKeysInGroup() {
        System.out.println("\nTestServerRuntimeMetadata.testGetForeignKeysInGroup()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("BOOKSO.BOOK_AUTHORS"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            Collection records = metadata.getForeignKeysInGroup(groupID);
            assertEquals(2,records.size());
            helpPrintResult(records);
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetPrimaryKeyIDForForeignKeyID() {
        System.out.println("\nTestServerRuntimeMetadata.testGetPrimaryKeyIDForForeignKeyID()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("BOOKSO.BOOK_AUTHORS"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            Collection records = metadata.getForeignKeysInGroup(groupID);
            assertEquals(2,records.size());
            helpPrintResult(records);
            
            for (Iterator iter = records.iterator(); iter.hasNext();) {
                MetadataRecord record = (MetadataRecord)iter.next();
                MetadataRecord result = (MetadataRecord)metadata.getPrimaryKeyIDForForeignKeyID(record);
                assertNotNull(result);
                System.out.println(result.getFullName());
            }
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

    public void testGetVirtualPlan() {
        System.out.println("\nTestServerRuntimeMetadata.testGetVirtualPlan()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("BOOKSR.LIBRARY"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            QueryNode result = metadata.getVirtualPlan(groupID);
            assertNotNull(result);
            System.out.println(result);
        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());    
        }
    }

	public void testGetInsertPlan() {
		System.out.println("\nTestServerRuntimeMetadata.testGetVirtualPlan()"); //$NON-NLS-1$
		try {
			QueryMetadataInterface metadata = helpGetMetadata(TXN_VDB);

			Object groupID = metadata.getGroupID("TxnTestVirtual.PartTxn"); //$NON-NLS-1$
			assertNotNull(groupID);
			assertTrue(groupID instanceof TableRecord);
            
			String result = metadata.getInsertPlan(groupID);
			assertNotNull(result);
			System.out.println(result);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());    
		}
	}
    
    public void testGetVDBResourcePaths() {
        System.out.println("\nTestServerRuntimeMetadata.testGetVDBResourcePaths()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(PARTS_VDB, BQT_VDB);
            String[] paths = metadata.getVDBResourcePaths();
            assertNotNull(paths);
            assertEquals(18, paths.length);
        } catch(Exception e) {
            fail(e.getMessage());    
        }
    }

    public void testGetCharacterVDBResource() {
        System.out.println("\nTestServerRuntimeMetadata.testGetCharacterVDBResource()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(PARTS_VDB, BQT_VDB);
            String contents = metadata.getCharacterVDBResource("/PartsPrj/PartsSupplier.xmi"); //$NON-NLS-1$
            assertNotNull(contents);
        } catch(Exception e) {
            fail(e.getMessage());    
        }        
    }

    public void testGetBinaryVDBResource() {
        System.out.println("\nTestServerRuntimeMetadata.testGetBinaryVDBResource()"); //$NON-NLS-1$
        try {
            QueryMetadataInterface metadata = helpGetMetadata(PARTS_VDB, BQT_VDB);
            byte[] contents = metadata.getBinaryVDBResource("/PartsPrj/PartsSupplier.xmi"); //$NON-NLS-1$
            assertNotNull(contents);
        } catch(Exception e) {
            fail(e.getMessage());    
        }        
    }

    public void testXMLResultSetColumnPosition() throws Exception{
        QueryMetadataInterface metadata = helpGetMetadata(BOOKS_SERVICE_VDB);      
        Object elementID1 = metadata.getElementID("BooksWebService.Books.getBookCollection.getBookCollectionOutput.Message Samples"); //$NON-NLS-1$
        assertNotNull(elementID1);
        assertTrue(elementID1 instanceof ColumnRecord);
        ColumnRecord record1 = (ColumnRecord) elementID1;
        assertEquals(1, record1.getPosition());
        
        elementID1 = metadata.getElementID("BooksFile.getBooks.NewXmlOutput.xml"); //$NON-NLS-1$
        assertNotNull(elementID1);
        assertTrue(elementID1 instanceof ColumnRecord);
        record1 = (ColumnRecord) elementID1;
        assertEquals(1, record1.getPosition());
    }
    
    static class MetadataRecordComparator implements Comparator {
        public int compare( Object obj1,
                            Object obj2 ) {
            if (obj1 == null && obj2 == null) {
                return 0;
            } else if (obj1 == null && obj2 != null) {
                return -1;
            } else if (obj1 != null && obj2 == null) {
                return 1;
            }
            MetadataRecord r1 = (MetadataRecord) obj1;
            MetadataRecord r2 = (MetadataRecord) obj2;
            String value1 = r1.getFullName();
            String value2 = r2.getFullName();
            return value1.compareToIgnoreCase(value2);
        }
    }	
}
