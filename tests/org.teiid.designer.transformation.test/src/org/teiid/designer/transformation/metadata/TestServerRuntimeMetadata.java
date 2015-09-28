/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.metadata;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.teiid.core.util.SmartTestDesignerSuite;
import org.teiid.core.util.TestUtilities;
import org.teiid.designer.core.index.CompositeIndexSelector;
import org.teiid.designer.core.index.IndexSelector;
import org.teiid.designer.core.index.RuntimeIndexSelector;
import org.teiid.designer.metadata.runtime.ColumnRecord;
import org.teiid.designer.metadata.runtime.MetadataRecord;
import org.teiid.designer.metadata.runtime.TableRecord;
import org.teiid.designer.query.metadata.IQueryMetadataInterface;
import org.teiid.designer.query.metadata.IStoredProcedureInfo;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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

    @Override
    public void setUp() {
        TestUtilities.setDefaultTeiidVersion();
    }

    @Override
    public void tearDown() {
        TestUtilities.unregisterTeiidServerManager();
    }
    
    public ServerRuntimeMetadata helpGetMetadata(String vdb) throws Exception {
        List<RuntimeIndexSelector> selectors = new ArrayList<RuntimeIndexSelector>();
        selectors.add(new RuntimeIndexSelector(SmartTestDesignerSuite.getTestDataPath(getClass())+DELIMITER+"/indexTestFolder/"+vdb));  //$NON-NLS-1$        
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        return new ServerRuntimeMetadata(context);
    }

    public ServerRuntimeMetadata helpGetMetadata( String vdb1,
                                                  String vdb2 ) throws Exception {
        List<RuntimeIndexSelector> selectors = new ArrayList<RuntimeIndexSelector>();
        selectors.add(new RuntimeIndexSelector(SmartTestDesignerSuite.getTestDataPath(getClass())+DELIMITER+"/indexTestFolder/"+vdb1));  //$NON-NLS-1$
        selectors.add(new RuntimeIndexSelector(SmartTestDesignerSuite.getTestDataPath(getClass())+DELIMITER+"/indexTestFolder/"+vdb2));  //$NON-NLS-1$
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
    
    private IQueryMetadataInterface helpGetTransformatrionMetadata(IndexSelector selector)  {
        return TransformationMetadataFactory.getInstance().getServerMetadata(selector);
    }

    public void testGetElementID1() {
        try {
            String url = SmartTestDesignerSuite.getTestDataPath(getClass())+"/indexTestFolder/BQT.vdb"; //$NON-NLS-1$
            IndexSelector selector = helpGetRuntimeSelector1(new File(url).toURI().toURL());

            IQueryMetadataInterface metadata = helpGetTransformatrionMetadata(selector);

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
            String resourcePath =  SmartTestDesignerSuite.getTestDataPath(getClass())+DELIMITER+RESOURCE_FOLDER+DELIMITER+BQT_VDB; 
            IndexSelector selector = helpGetRuntimeSelector2(resourcePath);

            IQueryMetadataInterface metadata = helpGetTransformatrionMetadata(selector);

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

    public void testColumnRecord0() throws Exception {
        try {
            IQueryMetadataInterface metadata = helpGetMetadata(BQT_VDB);
            metadata.getElementID(null);
            fail("Expected Arg Check failure"); //$NON-NLS-1$
        } catch(Exception e) {
            // failure expected
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
        IQueryMetadataInterface metadata = helpGetMetadata(BQT_VDB);

        Object elementID = metadata.getElementID("BQT.BQT2.HUGEA.INTKEY"); //$NON-NLS-1$

        assertNotNull(elementID);
        assertTrue(elementID instanceof ColumnRecord);

        ColumnRecord record = (ColumnRecord) elementID;

        assertEquals("INTKEY", record.getName()); //$NON-NLS-1$
        assertEquals("BQT/BQT2/HUGEA/INTKEY", record.getPath());           //$NON-NLS-1$                      
    }
    
    public void testGetElementID4() throws Exception {
        IQueryMetadataInterface metadata = helpGetMetadata(PARTS_VDB);

        Object elementID = metadata.getElementID("PartsSupplier.PartsSupplier.Parts.Part_Color"); //$NON-NLS-1$

        assertNotNull(elementID);
        assertTrue(elementID instanceof ColumnRecord);

        ColumnRecord record = (ColumnRecord) elementID;

        assertEquals("PART_COLOR", record.getName()); //$NON-NLS-1$
        assertEquals("PartsSupplier/PARTSSUPPLIER/PARTS/PART_COLOR", record.getPath());           //$NON-NLS-1$                      
    }

    // defect 16920 
	public void testGetStroredProc16920 () throws Exception {
		IQueryMetadataInterface metadata = helpGetMetadata(SOAP_VDB);

		try {
		    metadata.getStoredProcedureInfoForProcedure("UPD_ST_ADDR"); //$NON-NLS-1$
        } catch (Exception e) {
        }// expected exception
	}

    public void testGetVirtualXmlPlan() throws Exception {
        IQueryMetadataInterface metadata = helpGetMetadata(XML_VDB);
        Object groupID = metadata.getGroupID("BooksDoc.bookSetMixedDocument"); //$NON-NLS-1$
        assertNotNull(groupID);
        assertTrue(groupID instanceof TableRecord);

        Object node = metadata.getVirtualPlan(groupID);
        assertNotNull(node);
    }

//    public void testGetXmlSchemas() throws Exception {
//        IQueryMetadataInterface metadata = helpGetMetadata(XML_VDB);
//        Object groupID = metadata.getGroupID("BooksDoc.bookSetMixedDocument"); //$NON-NLS-1$
//        assertNotNull(groupID);
//        assertTrue(groupID instanceof TableRecord);
//
//        List schemas = metadata.getXMLSchemas(groupID);
//        assertNotNull(schemas);
//        assertEquals(1, schemas.size());
//    }

    public void testGetGroupsForPartialName() throws Exception {
        IQueryMetadataInterface metadata = helpGetMetadata(BQT_VDB);

        Collection groups = metadata.getGroupsForPartialName("HUGEA"); //$NON-NLS-1$

        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals("BQT.BQT2.HUGEA", groups.iterator().next()); //$NON-NLS-1$
    }
    
	public void testExtensions() throws Exception {
		IQueryMetadataInterface metadata = helpGetMetadata(Annotation_VDB);

		IStoredProcedureInfo info = metadata.getStoredProcedureInfoForProcedure("smallA.smallA"); //$NON-NLS-1$
		Object procID = info.getProcedureID();
		assertNotNull(info.getProcedureID());		
		Properties props = metadata.getExtensionProperties(procID);
		assertNotNull(props);
	}
	
    public void testColumnRecordWithNullArg() {
        try {
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);
            metadata.getElementID(null);
            fail("Expected Arg Check failure"); //$NON-NLS-1$
        } catch(Exception e) {
            // failure expected
        }
    }

    public void testGetGroupID() {
        System.out.println("\nTestServerRuntimeMetadata.testGetGroupID()"); //$NON-NLS-1$
        try {
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

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
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

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
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

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
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

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
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

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
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

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
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

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

    public void testGetForeignKeysInGroup() {
        System.out.println("\nTestServerRuntimeMetadata.testGetForeignKeysInGroup()"); //$NON-NLS-1$
        try {
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

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
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

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
            IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_VDB);

            Object groupID = metadata.getGroupID("BOOKSR.LIBRARY"); //$NON-NLS-1$
            assertNotNull(groupID);
            assertTrue(groupID instanceof TableRecord);
            
            Object result = metadata.getVirtualPlan(groupID);
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
			IQueryMetadataInterface metadata = helpGetMetadata(TXN_VDB);

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
    
    public void testXMLResultSetColumnPosition() throws Exception{
        IQueryMetadataInterface metadata = helpGetMetadata(BOOKS_SERVICE_VDB);      
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
        @Override
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
