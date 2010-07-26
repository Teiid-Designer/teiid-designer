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
import com.metamatrix.modeler.internal.core.index.IndexViewer;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlColumnAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlModelAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlTableAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.RuntimeAdapter;
import org.teiid.query.metadata.QueryMetadataInterface;

/**
 * TestTransformationMetadata
 */
public class TestTransformationMetadata extends TestCase {

    private static final List WORD_ENTRIES = new ArrayList(7);
    private static final String TEST_INDEX_FILE_NAME = "test.INDEX"; //$NON-NLS-1$
    static final String TEST_INDEX_DIRECTORY_PATH = SmartTestSuite.getTestDataPath();
    private static final String TEST_INDEX_FILE_PATH = TEST_INDEX_DIRECTORY_PATH + File.separator + TEST_INDEX_FILE_NAME;
    private static final String FAKE_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator + "PartsSupplierVirtual.vdb"; //$NON-NLS-1$

    /**
     * Constructor for TestTransformationMetadata.
     * 
     * @param name
     */
    public TestTransformationMetadata( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestTransformationMetadata"); //$NON-NLS-1$
        suite.addTestSuite(TestTransformationMetadata.class);

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

        // create all index files needed for tests
        String[] indexes = new String[] {TEST_INDEX_FILE_NAME, IndexConstants.INDEX_NAME.TABLES_INDEX,
            IndexConstants.INDEX_NAME.COLUMNS_INDEX};

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

    public static char[] helpCreateModelWord( String name,
                                              String alias,
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
        aspect.fullName = path.replace(IPath.SEPARATOR, IndexConstants.NAME_DELIM_CHAR);
        aspect.name = name;
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
        return word.getWord();
    }

    public static char[] helpCreateTableWord( String name,
                                              String alias,
                                              String path,
                                              String uuid,
                                              String pkID,
                                              String[] columnIDs,
                                              String[] foreignKeyIDs) {
        FakeSqlTableAspect aspect = new FakeSqlTableAspect();
        aspect.name = name;
        aspect.nameInSource = alias;
        aspect.fullName = path.replace(IPath.SEPARATOR, IndexConstants.NAME_DELIM_CHAR);
        aspect.path = new Path(path);
        aspect.uuid = uuid;
        aspect.primaryKey = pkID;
        aspect.columns = Arrays.asList(columnIDs);
        aspect.foreignKeys = Arrays.asList(foreignKeyIDs);

        WORD_ENTRIES.clear();
        RuntimeAdapter.addTableWord(aspect, null, null, null, WORD_ENTRIES);
        WordEntry word = (WordEntry)WORD_ENTRIES.get(0);
        System.out.println("word = " + word); //$NON-NLS-1$
        return word.getWord();
    }

    public static char[] helpCreateColumnWord( String name,
                                               String alias,
                                               String path,
                                               String uuid,
                                               String datatypeName,
                                               boolean selectable,
                                               boolean updatable,
                                               int nullType ) {
        FakeSqlColumnAspect aspect = new FakeSqlColumnAspect();
        aspect.name = name;
        aspect.fullName = path.replace(IPath.SEPARATOR, IndexConstants.NAME_DELIM_CHAR);
        aspect.nameInSource = alias;
        aspect.path = new Path(path);
        aspect.uuid = uuid;
        aspect.datatypeName = datatypeName;
        aspect.selectable = selectable;
        aspect.updatable = updatable;
        aspect.nullType = nullType;

        WORD_ENTRIES.clear();
        RuntimeAdapter.addColumnWord(aspect, null, null, WORD_ENTRIES);
        WordEntry word = (WordEntry)WORD_ENTRIES.get(0);
        System.out.println("word = " + word); //$NON-NLS-1$
        return word.getWord();
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

    public void printIndexFile() throws Exception {
        final File indexDirectory = new File(TEST_INDEX_DIRECTORY_PATH);
        Index index = new Index(indexDirectory, TEST_INDEX_FILE_NAME, true);
        String content = IndexViewer.getStringContent(index);
        System.out.println(content);
    }

    public void testCreate1() {
        System.out.println("\nTestTransformationMetadata.testCreate1()"); //$NON-NLS-1$
    }

    public void testQueryIndexFile() throws Exception {
        System.out.println("\nTestTransformationMetadata.testQueryIndexFile()"); //$NON-NLS-1$

        System.out.println(TEST_INDEX_FILE_PATH);
        final File indexDirectory = new File(TEST_INDEX_DIRECTORY_PATH);

        Index index = null;
        index = new Index(indexDirectory, TEST_INDEX_FILE_NAME, true);
        assertEquals(1, index.getNumDocuments());

        String word = IndexConstants.RECORD_TYPE.TABLE + "*table1*"; //$NON-NLS-1$
        char[] charWord = word.toCharArray();
        IEntryResult[] results = index.queryEntriesMatching(charWord, true);
        helpCheckQueryResults(results, IndexConstants.RECORD_TYPE.TABLE);
        printQueryResults(results);

        word = IndexConstants.RECORD_TYPE.COLUMN + "*columnAlias1B*"; //$NON-NLS-1$
        charWord = word.toCharArray();
        results = index.queryEntriesMatching(charWord, true);
        helpCheckQueryResults(results, IndexConstants.RECORD_TYPE.COLUMN);
        printQueryResults(results);

        // fixed case sensitive look up with patterns
        word = IndexConstants.RECORD_TYPE.COLUMN + "*column*"; //$NON-NLS-1$
        charWord = word.toCharArray();
        results = index.queryEntriesMatching(charWord, true);
        helpCheckQueryResults(results, IndexConstants.RECORD_TYPE.COLUMN);
        printQueryResults(results);
        assertNotNull(index);
    }

    public void testQueryTransformationMetadata() throws Exception {
        System.out.println("\nTestTransformationMetadata.testQueryTransformationMetadata()"); //$NON-NLS-1$

        final TestIndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        selector.addIndexFileName(IndexConstants.INDEX_NAME.TABLES_INDEX);

        QueryMetadataContext context = new QueryMetadataContext(selector);
        QueryMetadataInterface queryMetadata = new TestMetadata(context);

        Object result = queryMetadata.getGroupID("model1.table1"); //$NON-NLS-1$
        assertNotNull(result);
        assertTrue(result instanceof TableRecord);
        assertEquals("table1", ((TableRecord)result).getName()); //$NON-NLS-1$
        helpCheckQueryResults(result, IndexConstants.RECORD_TYPE.TABLE);
        printQueryResults(result);

        result = queryMetadata.getElementID("model1.table1.column1C"); //$NON-NLS-1$
        assertNotNull(result);
        assertTrue(result instanceof ColumnRecord);
        assertEquals("column1C", ((ColumnRecord)result).getName()); //$NON-NLS-1$
        helpCheckQueryResults(result, IndexConstants.RECORD_TYPE.COLUMN);
        printQueryResults(result);
    }

    public void testQueryTransformationMetadataForAllRecords() throws Exception {
        System.out.println("\nTestTransformationMetadata.testQueryTransformationMetadataForAllRecords()"); //$NON-NLS-1$

        final TestIndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        selector.addIndexFileName(IndexConstants.INDEX_NAME.TABLES_INDEX);

        QueryMetadataContext context = new QueryMetadataContext(selector);
        QueryMetadataInterface queryMetadata = new TestMetadata(context);

        printIndexFile();
        assertNotNull(queryMetadata.getGroupID("model1.table1")); //$NON-NLS-1$
        assertNotNull(queryMetadata.getGroupID("model2.table2")); //$NON-NLS-1$
        assertNotNull(queryMetadata.getGroupID("model2.table3")); //$NON-NLS-1$
        assertNotNull(queryMetadata.getElementID("model1.table1.column1A")); //$NON-NLS-1$
        assertNotNull(queryMetadata.getElementID("model1.table1.column1B")); //$NON-NLS-1$
        assertNotNull(queryMetadata.getElementID("model1.table1.column1C")); //$NON-NLS-1$
        assertNotNull(queryMetadata.getElementID("model2.table1.column2A")); //$NON-NLS-1$
        assertNotNull(queryMetadata.getElementID("model2.table1.column2B")); //$NON-NLS-1$
        assertNotNull(queryMetadata.getElementID("model2.table1.column2C")); //$NON-NLS-1$
    }

    public void testGetShortName() {
        System.out.println("\nTestTransformationMetadata.testGetShortName()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        QueryMetadataInterface queryMetadata = TransformationMetadataFactory.getInstance().getServerMetadata(selector);

        String shortName = null;
        try {
            shortName = queryMetadata.getShortElementName("Model.table.column"); //$NON-NLS-1$
        } catch (QueryMetadataException e) {
            e.printStackTrace();
        } catch (TeiidComponentException e) {
            e.printStackTrace();
        }
        assertEquals("column", shortName); //$NON-NLS-1$
    }

    public void testGetFullElementName() {
        System.out.println("\nTestTransformationMetadata.testGetFullElementName()"); //$NON-NLS-1$

        final IndexSelector selector = new TestIndexSelector(IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        QueryMetadataInterface queryMetadata = TransformationMetadataFactory.getInstance().getServerMetadata(selector);

        String shortName = null;
        try {
            shortName = queryMetadata.getFullElementName("Model.table", "column"); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (QueryMetadataException e) {
            e.printStackTrace();
        } catch (TeiidComponentException e) {
            e.printStackTrace();
        }
        assertEquals("Model.table.column", shortName); //$NON-NLS-1$
    }

    static class TestIndexer implements IIndexer {
        public String[] getFileTypes() {
            return null;
        }

        public void index( IDocument document,
                           IIndexerOutput output ) {
            output.addDocument(document);
            if (document instanceof TestDocument) {
                output.addRef(helpCreateModelWord("model1", "modelAlias1", "model1", "mmuuid:id1", true, true, true, true, true, true, 10)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                output.addRef(helpCreateModelWord("model2", "modelAlias2", "model2", "mmuuid:id2", true, false, true, false, true, false, 5)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                output.addRef(helpCreateTableWord("table1", "tableAlias1", "model1/table1", "mmuuid:id3", "mmuuid:pk1", new String[] {"mmuuid:id5,mmuuid:id6,mmuuid:id7"}, new String[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                output.addRef(helpCreateTableWord("table2", "tableAlias2", "model2/table2", "mmuuid:id4", "mmuuid:pk2", new String[] {"mmuuid:id8,mmuuid:id9,mmuuid:id10"}, new String[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
                output.addRef(helpCreateTableWord("table3", "tableAlias3", "model2/table3", "mmuuid:id11", "mmuuid:pk3", new String[0], new String[0])); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                output.addRef(helpCreateColumnWord("column1A", "columnAlias1A", "model1/table1/column1A", "mmuuid:id5", "String", true, true, 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                output.addRef(helpCreateColumnWord("column1B", "columnAlias1B", "model1/table1/column1B", "mmuuid:id6", "String", true, true, 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                output.addRef(helpCreateColumnWord("column1C", "columnAlias1C", "model1/table1/column1C", "mmuuid:id7", "String", true, true, 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                output.addRef(helpCreateColumnWord("column2A", "columnAlias2A", "model2/table1/column2A", "mmuuid:id8", "String", true, true, 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                output.addRef(helpCreateColumnWord("column2B", "columnAlias2B", "model2/table1/column2B", "mmuuid:id9", "String", true, true, 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                output.addRef(helpCreateColumnWord("column2C", "columnAlias2C", "model2/table1/column2C", "mmuuid:id10", "String", true, true, 1)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
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

    private static class TestIndexSelector extends AbstractIndexSelector {
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

    private class TestMetadata extends TransformationMetadata {
        public TestMetadata( QueryMetadataContext context ) {
            super(context);
        }
    }
}
