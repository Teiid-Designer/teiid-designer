/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.metadata;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.teiid.core.TeiidComponentException;
import org.teiid.api.exception.query.QueryMetadataException;
import com.metamatrix.core.index.AbstractIndexSelector;
import com.metamatrix.core.index.CompositeIndexSelector;
import com.metamatrix.core.index.IDocument;
import com.metamatrix.core.index.IEntryResult;
import com.metamatrix.core.index.IIndexer;
import com.metamatrix.core.index.IIndexerOutput;
import com.metamatrix.core.index.IQueryResult;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.internal.core.index.FileDocument;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.internal.core.index.WordEntry;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.core.metadata.runtime.ColumnRecord;
import com.metamatrix.modeler.core.metadata.runtime.MetadataRecord;
import com.metamatrix.modeler.core.metadata.runtime.TableRecord;
import com.metamatrix.modeler.internal.core.index.WordEntryComparator;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlColumnAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlModelAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlProcedureAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlTableAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.RuntimeAdapter;
import org.teiid.query.metadata.StoredProcedureInfo;

/**
 * TestTransformationMetadata
 */
public class TestTransformationMetadataFacade extends TestCase {

    private static final List WORD_ENTRIES = new ArrayList(7);
    private static final String TEST_INDEX_FILE_NAME = "test.index"; //$NON-NLS-1$
    static final String TEST_INDEX_DIRECTORY_PATH = SmartTestSuite.getTestScratchPath();
    private static final String TEST_INDEX_FILE_PATH = TEST_INDEX_DIRECTORY_PATH + File.separator + TEST_INDEX_FILE_NAME;
    private static final String FAKE_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator + "PartsRelational.mmm"; //$NON-NLS-1$

    /**
     * Constructor for TestTransformationMetadataFacade.
     * 
     * @param name
     */
    public TestTransformationMetadataFacade( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestTransformationMetadataFacade"); //$NON-NLS-1$
        suite.addTestSuite(TestTransformationMetadataFacade.class);
        //suite.addTest(new TestTransformationMetadataFacade("testGetGroupID")); //$NON-NLS-1$

        return new TestSetup(suite) { // junit.extensions package
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }

            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }

    public static void main( String args[] ) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public static void oneTimeSetUp() {
        helpCreateIndexFile();
    }

    public static void oneTimeTearDown() {
        // Delete the index file used for testing
        final File testIndexFile = new File(TEST_INDEX_FILE_PATH);
        testIndexFile.delete();
    }

    private static void helpCreateIndexFile() {
        // Create the index
        System.out.println(TEST_INDEX_FILE_PATH);
        final File indexDirectory = new File(TEST_INDEX_DIRECTORY_PATH);
        if (!indexDirectory.exists()) {
            indexDirectory.mkdir();
        }

        // create all index files needed for tests
        String[] indexes = new String[] {/*TEST_INDEX_FILE_NAME,*/
        IndexConstants.INDEX_NAME.TABLES_INDEX, IndexConstants.INDEX_NAME.COLUMNS_INDEX,
            IndexConstants.INDEX_NAME.PROCEDURES_INDEX};

        for (int i = 0; i < indexes.length; ++i) {
            Index index = null;
            try {
                index = new Index(indexDirectory, indexes[i], false);
            } catch (Throwable t) {
                t.printStackTrace(System.err);
            }
            assertNotNull(index);

            final IDocument doc = new TestDocument(FAKE_FILE_PATH);
            try {
                index.add(doc, new TestIndexer());
            } catch (Throwable t) {
                t.printStackTrace(System.err);
            }

            // Save the index file
            try {
                index.save();
                assertEquals(1, index.getNumDocuments());
            } catch (Throwable t) {
                t.printStackTrace(System.err);
            }
        }
    }

    public static WordEntry helpCreateModelWord( String alias,
                                                 String path,
                                                 String uuid,
                                                 boolean supportsOrderBy,
                                                 boolean supportsOuterJoin,
                                                 boolean supportsWhereAll,
                                                 boolean supportsDistinct,
                                                 boolean supportsJoin,
                                                 boolean isVisible,
                                                 int maxSetSize ) {
        FakeSqlModelAspect aspect = new FakeSqlModelAspect();
        aspect.fullName = aspect.name = path.replace(IPath.SEPARATOR, IndexConstants.NAME_DELIM_CHAR);
        aspect.nameInSource = alias;
        aspect.path = new Path(path);
        aspect.uuid = uuid;
        aspect.supportsOrderBy = supportsOrderBy;
        aspect.supportsOuterJoin = supportsOuterJoin;
        aspect.supportsWhereAll = supportsWhereAll;
        aspect.supportsDistinct = supportsDistinct;
        aspect.supportsJoin = supportsJoin;
        aspect.isVisible = isVisible;
        aspect.maxSetSize = maxSetSize;

        WORD_ENTRIES.clear();
        RuntimeAdapter.addModelWord(aspect, null, null, WORD_ENTRIES);
        WordEntry word = (WordEntry)WORD_ENTRIES.get(0);
        System.out.println("word = " + word); //$NON-NLS-1$
        return word;
    }

    public static WordEntry helpCreateTableWord( String alias,
                                                 String path,
                                                 String uuid,
                                                 String pkID,
                                                 String[] columnIDs,
                                                 String[] foreignKeyIDs ) {
        FakeSqlTableAspect aspect = new FakeSqlTableAspect();
        aspect.fullName = path.replace(IPath.SEPARATOR, IndexConstants.NAME_DELIM_CHAR);
        aspect.nameInSource = alias;
        aspect.path = new Path(path);
        aspect.uuid = uuid;
        aspect.primaryKey = pkID;
        aspect.columns = Arrays.asList(columnIDs);
        aspect.foreignKeys = Arrays.asList(foreignKeyIDs);

        WORD_ENTRIES.clear();
        RuntimeAdapter.addTableWord(aspect, null, null, null, WORD_ENTRIES);
        WordEntry word = (WordEntry)WORD_ENTRIES.get(0);
        System.out.println("word = " + word); //$NON-NLS-1$
        return word;
    }

    public static WordEntry helpCreateProcedureWord( String alias,
                                                     String path,
                                                     String uuid ) {
        FakeSqlProcedureAspect aspect = new FakeSqlProcedureAspect();
        aspect.fullName = path.replace(IPath.SEPARATOR, IndexConstants.NAME_DELIM_CHAR);
        aspect.nameInSource = alias;
        aspect.path = new Path(path);
        aspect.uuid = uuid;

        WORD_ENTRIES.clear();
        RuntimeAdapter.addCallableWord(aspect, null, null, WORD_ENTRIES);
        WordEntry word = (WordEntry)WORD_ENTRIES.get(0);
        System.out.println("word = " + word); //$NON-NLS-1$
        return word;
    }

    public static WordEntry helpCreateColumnWord( String alias,
                                                  String path,
                                                  String uuid,
                                                  String runtimeType,
                                                  boolean selectable,
                                                  boolean updatable,
                                                  int nullType,
                                                  String parentUuid ) {
        FakeSqlColumnAspect aspect = new FakeSqlColumnAspect();
        aspect.fullName = path.replace(IPath.SEPARATOR, IndexConstants.NAME_DELIM_CHAR);
        aspect.nameInSource = alias;
        aspect.path = new Path(path);
        aspect.uuid = uuid;
        aspect.runtimeType = runtimeType;
        aspect.selectable = selectable;
        aspect.updatable = updatable;
        aspect.nullType = nullType;
        aspect.parentUuid = parentUuid;

        WORD_ENTRIES.clear();
        RuntimeAdapter.addColumnWord(aspect, null, null, WORD_ENTRIES);
        WordEntry word = (WordEntry)WORD_ENTRIES.get(0);
        System.out.println("word = " + word); //$NON-NLS-1$
        return word;
    }

    public void helpCheckQueryResults( IQueryResult[] results ) {
        assertNotNull(results);
        for (int i = 0; i < results.length; i++) {
            IQueryResult result = results[i];
            assertNotNull(result);
        }
    }

    public void helpCheckQueryResults( IEntryResult[] results,
                                       char expectedRecordType ) {
        assertNotNull(results);
        for (int i = 0; i < results.length; i++) {
            IEntryResult result = results[i];
            assertNotNull(result);
            assertEquals(expectedRecordType, result.getWord()[0]);
        }
    }

    public void helpCheckQueryResults( Object result,
                                       char expectedRecordType ) {
        assertNotNull(result);
        if (result instanceof MetadataRecord) {
            final MetadataRecord record = (MetadataRecord)result;
            assertEquals(expectedRecordType, record.getRecordType());
        } else if (result instanceof Collection) {
            final Collection results = (Collection)result;
            System.out.println("\nMetadataRecords.size() = " + results.size()); //$NON-NLS-1$
            for (Iterator iter = results.iterator(); iter.hasNext();) {
                Object obj = iter.next();
                assertNotNull(obj);
                if (obj instanceof MetadataRecord) {
                    MetadataRecord record = (MetadataRecord)obj;
                    assertEquals(expectedRecordType, record.getRecordType());
                }
            }
        }
    }

    public void printQueryResults( IQueryResult[] results ) {
        System.out.println("\nIQueryResult[].length = " + (results == null ? "null" : Integer.toString(results.length))); //$NON-NLS-1$ //$NON-NLS-2$
        if (results != null) {
            for (int i = 0; i < results.length; i++) {
                System.out.println("  IQueryResult[] = " + results[i]); //$NON-NLS-1$
            }
        }
    }

    public void printQueryResults( IEntryResult[] results ) {
        System.out.println("\nIEntryResult[].length = " + (results == null ? "null" : Integer.toString(results.length))); //$NON-NLS-1$ //$NON-NLS-2$
        if (results != null) {
            for (int i = 0; i < results.length; i++) {
                System.out.println("  IEntryResult[] = " + results[i]); //$NON-NLS-1$
            }
        }
    }

    public void printQueryResults( Object result ) {
        if (result == null) {
            System.out.println("\nMetadataRecord = null"); //$NON-NLS-1$
        } else if (result instanceof MetadataRecord) {
            final MetadataRecord record = (MetadataRecord)result;
            System.out.println("\nMetadataRecord = " + record); //$NON-NLS-1$
        } else if (result instanceof Collection) {
            final Collection results = (Collection)result;
            System.out.println("\nMetadataRecords.size() = " + results.size()); //$NON-NLS-1$
            for (Iterator iter = results.iterator(); iter.hasNext();) {
                Object obj = iter.next();
                if (obj instanceof MetadataRecord) {
                    MetadataRecord record = (MetadataRecord)obj;
                    System.out.println("  MetadataRecord = " + record); //$NON-NLS-1$
                } else {
                    System.out.println("  MetadataRecord = " + obj); //$NON-NLS-1$
                }
            }
        }
    }

    public void testGetGroupID() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.testGetGroupID()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.TABLES_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        final String groupName = "model1.table1"; //$NON-NLS-1$
        Object result = facade.getGroupID(groupName);
        assertNotNull(result);
        assertTrue(result instanceof TableRecord);
        assertEquals("table1", ((TableRecord)result).getName()); //$NON-NLS-1$
        helpCheckQueryResults(result, IndexConstants.RECORD_TYPE.TABLE);
        printQueryResults(result);
        assertEquals(1, metadata.indexFileHitCount);

        facade.getGroupID(groupName);
        facade.getGroupID(groupName);
        facade.getGroupID(groupName);
        facade.getGroupID(groupName);
        facade.getGroupID(groupName);
        assertEquals(1, metadata.indexFileHitCount);
    }

    public void testGroupsForPartialName() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.testGroupsForPartialName()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.TABLES_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        final String partialGroupName = "Table1"; //$NON-NLS-1$
        Collection result = facade.getGroupsForPartialName(partialGroupName);
        assertNotNull(result);
        assertEquals("model1.table1", result.iterator().next()); //$NON-NLS-1$
        printQueryResults(result);
        assertEquals(1, metadata.indexFileHitCount);

        facade.getGroupsForPartialName(partialGroupName.toLowerCase());
        facade.getGroupsForPartialName(partialGroupName);
        facade.getGroupsForPartialName(partialGroupName);
        facade.getGroupsForPartialName(partialGroupName.toUpperCase());
        facade.getGroupsForPartialName(partialGroupName);
        assertEquals(1, metadata.indexFileHitCount);
    }

    public void testGetElementID() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.testGetElementID()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        final String elementName = "model1.table1.column1C"; //$NON-NLS-1$
        Object result = facade.getElementID(elementName);
        assertNotNull(result);
        assertTrue(result instanceof ColumnRecord);
        assertEquals("column1C", ((ColumnRecord)result).getName()); //$NON-NLS-1$
        helpCheckQueryResults(result, IndexConstants.RECORD_TYPE.COLUMN);
        printQueryResults(result);
        assertEquals(1, metadata.indexFileHitCount);

        facade.getElementID(elementName);
        facade.getElementID(elementName);
        facade.getElementID(elementName);
        facade.getElementID(elementName);
        facade.getElementID(elementName);
        facade.getElementID(elementName);
        assertEquals(1, metadata.indexFileHitCount);
    }

    public void testGetElementIDsInGroupID() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.testGetElementIDsInGroupID()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.TABLES_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        final Object groupID = facade.getGroupID("model1.table1"); //$NON-NLS-1$
        Object result = facade.getElementIDsInGroupID(groupID);
        assertTrue(result instanceof Collection);
        assertEquals(3, ((Collection)result).size());
        assertEquals(2, metadata.indexFileHitCount);
        helpCheckQueryResults(result, IndexConstants.RECORD_TYPE.COLUMN);
        printQueryResults(result);

        facade.getElementIDsInGroupID(groupID);
        facade.getElementIDsInGroupID(groupID);
        facade.getElementIDsInGroupID(groupID);
        facade.getElementIDsInGroupID(groupID);
        facade.getElementIDsInGroupID(groupID);
        facade.getElementIDsInGroupID(groupID);
        assertEquals(2, metadata.indexFileHitCount);
    }

    public void testGetElementIDsInGroupID2() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.testGetElementIDsInGroupID2()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.TABLES_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        try {
            final Object groupID = facade.getGroupID("model2.table3"); //$NON-NLS-1$
            facade.getElementIDsInGroupID(groupID);
            Object result = facade.getElementIDsInGroupID(groupID);
            assertTrue(result instanceof Collection);
            assertEquals(0, ((Collection)result).size());
            assertEquals(1, metadata.indexFileHitCount);
            helpCheckQueryResults(result, IndexConstants.RECORD_TYPE.COLUMN);
            printQueryResults(result);

            facade.getElementIDsInGroupID(groupID);
            facade.getElementIDsInGroupID(groupID);
            facade.getElementIDsInGroupID(groupID);
            facade.getElementIDsInGroupID(groupID);
            facade.getElementIDsInGroupID(groupID);
            facade.getElementIDsInGroupID(groupID);
            assertEquals(1, metadata.indexFileHitCount);
        } catch (QueryMetadataException e) {
            // Expected
        }
    }

    public void testGetIndexesInGroupID() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.testGetIndexesIDsInGroupID()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.TABLES_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        final Object groupID = facade.getGroupID("model1.table1"); //$NON-NLS-1$
        Object result = facade.getIndexesInGroup(groupID);
        assertTrue(result instanceof Collection);
        assertEquals(2, metadata.indexFileHitCount);

        facade.getIndexesInGroup(groupID);
        facade.getIndexesInGroup(groupID);
        facade.getIndexesInGroup(groupID);
        facade.getIndexesInGroup(groupID);
        facade.getIndexesInGroup(groupID);
        facade.getIndexesInGroup(groupID);
        assertEquals(2, metadata.indexFileHitCount);
    }

    public void testGetForeignKeysInGroupID() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.testGetForeignKeysInGroupID()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.TABLES_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        final Object groupID = facade.getGroupID("model1.table1"); //$NON-NLS-1$
        Object result = facade.getForeignKeysInGroup(groupID);
        assertTrue(result instanceof Collection);
        assertEquals(2, metadata.indexFileHitCount);

        facade.getForeignKeysInGroup(groupID);
        facade.getForeignKeysInGroup(groupID);
        facade.getForeignKeysInGroup(groupID);
        facade.getForeignKeysInGroup(groupID);
        facade.getForeignKeysInGroup(groupID);
        facade.getForeignKeysInGroup(groupID);
        assertEquals(2, metadata.indexFileHitCount);
    }

    public void testGetAccessPatternsInGroupID() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.testGetAccessPatternsInGroupID()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.TABLES_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        final Object groupID = facade.getGroupID("model1.table1"); //$NON-NLS-1$
        Object result = facade.getAccessPatternsInGroup(groupID);
        assertTrue(result instanceof Collection);
        assertEquals(2, metadata.indexFileHitCount);

        facade.getAccessPatternsInGroup(groupID);
        facade.getAccessPatternsInGroup(groupID);
        facade.getAccessPatternsInGroup(groupID);
        facade.getAccessPatternsInGroup(groupID);
        facade.getAccessPatternsInGroup(groupID);
        facade.getAccessPatternsInGroup(groupID);
        assertEquals(2, metadata.indexFileHitCount);
    }

    public void testGetUniqueKeysInGroupID() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.getUniqueKeysInGroup()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.TABLES_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        final Object groupID = facade.getGroupID("model1.table1"); //$NON-NLS-1$
        Object result = facade.getUniqueKeysInGroup(groupID);
        assertTrue(result instanceof Collection);
        assertEquals(2, metadata.indexFileHitCount);

        facade.getUniqueKeysInGroup(groupID);
        facade.getUniqueKeysInGroup(groupID);
        facade.getUniqueKeysInGroup(groupID);
        facade.getUniqueKeysInGroup(groupID);
        facade.getUniqueKeysInGroup(groupID);
        facade.getUniqueKeysInGroup(groupID);
        assertEquals(2, metadata.indexFileHitCount);
    }

    public void testGetStoredProcedureInfo() throws Exception {
        System.out.println("\nTestTransformationMetadataFacade.getUniqueKeysInGroup()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.PROCEDURES_INDEX);
        List selectors = new ArrayList(1);
        selectors.add(selector);
        IndexSelector composite = new CompositeIndexSelector(selectors);
        QueryMetadataContext context = new QueryMetadataContext(composite);
        final TestTransformationMetadata metadata = new TestTransformationMetadata(context);
        final TransformationMetadataFacade facade = new TransformationMetadataFacade(metadata);

        facade.getStoredProcedureInfoForProcedure("model2.proc1"); //$NON-NLS-1$
        assertEquals(2, metadata.indexFileHitCount);

        facade.getStoredProcedureInfoForProcedure("model2.proc1"); //$NON-NLS-1$
        facade.getStoredProcedureInfoForProcedure("model2.proc1"); //$NON-NLS-1$
        facade.getStoredProcedureInfoForProcedure("model2.proc1"); //$NON-NLS-1$
        facade.getStoredProcedureInfoForProcedure("model2.proc1"); //$NON-NLS-1$
        facade.getStoredProcedureInfoForProcedure("model2.proc1"); //$NON-NLS-1$
        facade.getStoredProcedureInfoForProcedure("model2.proc1"); //$NON-NLS-1$
        assertEquals(2, metadata.indexFileHitCount);
    }

    static class TestIndexer implements IIndexer {
        public String[] getFileTypes() {
            return null;
        }

        public void index( IDocument document,
                           IIndexerOutput output ) {
            output.addDocument(document);
            if (document instanceof TestDocument) {
                List words = new ArrayList();

                words.add(helpCreateModelWord("modelAlias1", "model1", "mmuuid:id1", true, true, true, true, true, true, 10)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
                words.add(helpCreateModelWord("modelAlias2", "model2", "mmuuid:id2", true, false, true, false, true, false, 5)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
                words.add(helpCreateTableWord("tableAlias1", "model1/table1", "mmuuid:id3", "mmuuid:pk1", new String[] {"mmuuid:id5,mmuuid:id6,mmuuid:id7"}, new String[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 
                words.add(helpCreateTableWord("tableAlias2", "model2/table2", "mmuuid:id4", "mmuuid:pk2", new String[] {"mmuuid:id8,mmuuid:id9,mmuuid:id10"}, new String[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ 
                words.add(helpCreateTableWord("tableAlias3", "model2/table3", "mmuuid:id11", "mmuuid:pk3", new String[0], new String[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
                words.add(helpCreateColumnWord("columnAlias1A", "model1/table1/column1A", "mmuuid:id5", "String", true, true, 1, "mmuuid:id3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                words.add(helpCreateColumnWord("columnAlias1B", "model1/table1/column1B", "mmuuid:id6", "String", true, true, 1, "mmuuid:id3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                words.add(helpCreateColumnWord("columnAlias1C", "model1/table1/column1C", "mmuuid:id7", "String", true, true, 1, "mmuuid:id3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                words.add(helpCreateColumnWord("columnAlias2A", "model2/table2/column2A", "mmuuid:id8", "String", true, true, 1, "mmuuid:id4")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                words.add(helpCreateColumnWord("columnAlias2B", "model2/table2/column2B", "mmuuid:id9", "String", true, true, 1, "mmuuid:id4")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                words.add(helpCreateColumnWord("columnAlias2C", "model2/table2/column2C", "mmuuid:id10", "String", true, true, 1, "mmuuid:id4")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                words.add(helpCreateProcedureWord("proc1", "model2/proc1", "mmuuid:id11")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

                Collections.sort(words, new WordEntryComparator());
                addEntries(output, words);
            }
        }

        /**
         * Add word entries to indexoutput.
         */
        private void addEntries( IIndexerOutput output,
                                 List entries ) {
            Iterator entryIter = entries.iterator();
            while (entryIter.hasNext()) {
                WordEntry entry = (WordEntry)entryIter.next();
                output.addRef(entry.getWord());
            }
        }

        public void setFileTypes( String[] fileTypes ) {
        }

        public boolean shouldIndex( IDocument document ) {
            return true;
        }
    }

    private static class TestDocument extends FileDocument {
        public TestDocument( String filePath ) {
            super(new File(filePath));
        }
    }

    static class TestIndexSelector extends AbstractIndexSelector {
        List indexNames = new ArrayList();

        public TestIndexSelector( String theIndexFileName ) {
            addIndexFileName(theIndexFileName);
        }

        public void addIndexFileName( String theFileName ) {
            indexNames.add(theFileName);
        }

        @Override
        public Index[] getIndexes() throws IOException {
            final File indexDirectory = new File(TEST_INDEX_DIRECTORY_PATH);
            if (!indexDirectory.exists()) {
                indexDirectory.mkdir();
            }

            Index[] indexes = new Index[this.indexNames.size()];
            for (int i = 0; i < indexes.length; ++i) {
                Index index = new Index(indexDirectory, (String)this.indexNames.get(i), true);
                assertNotNull(index);
                assertEquals(1, index.getNumDocuments());
                indexes[i] = index;
            }
            return indexes;
        }
    }

    static class TestTransformationMetadata extends TransformationMetadata {
        public int indexFileHitCount;

        public TestTransformationMetadata( QueryMetadataContext context ) {
            super(context);
            indexFileHitCount = 0;
        }

        @Override
        public Object getElementID( String elementName ) throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getElementID(elementName);
        }

        @Override
        public Object getModelID( Object groupOrElementID ) throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getModelID(groupOrElementID);
        }

        @Override
        public List getElementIDsInGroupID( Object groupID ) throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getElementIDsInGroupID(groupID);
        }

        @Override
        public Collection getIndexesInGroup( Object groupID ) throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getIndexesInGroup(groupID);
        }

        @Override
        public Collection getForeignKeysInGroup( Object groupID ) throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getForeignKeysInGroup(groupID);
        }

        @Override
        public Collection getAccessPatternsInGroup( Object groupID ) throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getAccessPatternsInGroup(groupID);
        }

        @Override
        public Collection getUniqueKeysInGroup( Object groupID ) throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getUniqueKeysInGroup(groupID);
        }

        @Override
        public StoredProcedureInfo getStoredProcedureInfoForProcedure( String procName )
            throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getStoredProcedureInfoForProcedure(procName);
        }

        @Override
        public Object getGroupID( String groupName ) throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getGroupID(groupName);
        }

        @Override
        public Collection getGroupsForPartialName( String partialGroupName )
            throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getGroupsForPartialName(partialGroupName);
        }

        @Override
        public Object getGroupIDForElementID( Object elementID ) throws TeiidComponentException, QueryMetadataException {
            indexFileHitCount++;
            return super.getGroupIDForElementID(elementID);
        }
    }
}
