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
import java.util.Iterator;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.index.AbstractIndexSelector;
import com.metamatrix.core.index.IDocument;
import com.metamatrix.core.index.IEntryResult;
import com.metamatrix.core.index.IIndexer;
import com.metamatrix.core.index.IIndexerOutput;
import com.metamatrix.core.util.SmartTestSuite;
import com.metamatrix.core.util.Stopwatch;
import com.metamatrix.internal.core.index.FileDocument;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.internal.core.index.WordEntry;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.internal.core.index.IndexUtil;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlColumnAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlModelAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlTableAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.RuntimeAdapter;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceIndexManager;

/**
 * @since 4.3
 */
public class TestPerformancePrefixAndPatternLookUp extends TestCase {
    private static final List WORD_ENTRIES = new ArrayList(7);

    //private static final String TEST_INDEX_FILE_NAME = "test.index"; //$NON-NLS-1$
    static final String TEST_INDEX_DIRECTORY_PATH = SmartTestSuite.getTestDataPath();
    // private static final String TEST_INDEX_FILE_PATH = TEST_INDEX_DIRECTORY_PATH + File.separator + TEST_INDEX_FILE_NAME;
    private static final String FAKE_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator + "PartsRelational.mmm"; //$NON-NLS-1$

    private static final String MODEL_NAME = "Model"; //$NON-NLS-1$
    private static final String TABLE_NAME = "Table"; //$NON-NLS-1$
    private static final String COLUMN_NAME = "Column"; //$NON-NLS-1$
    private static final char DELIMITER = IndexConstants.NAME_DELIM_CHAR;

    static ModelWorkspaceIndexManager indexManager = new ModelWorkspaceIndexManager();

    /**
     * Constructor for TestTransformationMetadataPerformance.
     * 
     * @param name
     */
    public TestPerformancePrefixAndPatternLookUp( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestPerformancePrefixAndPatternLookUp"); //$NON-NLS-1$
        suite.addTestSuite(TestPerformancePrefixAndPatternLookUp.class);

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

    @Override
    protected void tearDown() throws Exception {
        oneTimeTearDown();

    }

    public static void oneTimeSetUp() {
    }

    public static void oneTimeTearDown() {
        // Delete the index file used for testing
        indexManager.disposeAll();
    }

    private static void helpCreateIndexFiles( int numModels,
                                              int numTables,
                                              int numColumns ) {
        Stopwatch sw1 = new Stopwatch();
        Stopwatch sw2 = new Stopwatch();
        Stopwatch sw3 = new Stopwatch();
        sw1.start();

        sw3.reset();
        sw3.start();
        // Create the index
        final File indexDirectory = new File(TEST_INDEX_DIRECTORY_PATH);
        Index index1 = null;
        Index index2 = null;
        Index index3 = null;
        try {
            String fullPathFileName = indexDirectory.getAbsolutePath() + File.separator + IndexConstants.INDEX_NAME.COLUMNS_INDEX;
            index1 = indexManager.getIndex(IndexConstants.INDEX_NAME.COLUMNS_INDEX,
                                           fullPathFileName,
                                           IndexConstants.INDEX_NAME.COLUMNS_INDEX);
            fullPathFileName = indexDirectory.getAbsolutePath() + File.separator + IndexConstants.INDEX_NAME.TABLES_INDEX;
            index2 = indexManager.getIndex(IndexConstants.INDEX_NAME.TABLES_INDEX,
                                           fullPathFileName,
                                           IndexConstants.INDEX_NAME.TABLES_INDEX);
            fullPathFileName = indexDirectory.getAbsolutePath() + File.separator + IndexConstants.INDEX_NAME.MODELS_INDEX;
            index3 = indexManager.getIndex(IndexConstants.INDEX_NAME.MODELS_INDEX,
                                           fullPathFileName,
                                           IndexConstants.INDEX_NAME.MODELS_INDEX);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        sw3.stop();
        assertNotNull(index1);
        assertNotNull(index2);
        assertNotNull(index3);

        sw3.reset();
        sw3.start();
        final TestDocument doc1 = new TestDocument(FAKE_FILE_PATH);
        doc1.setIndexFileName(IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        final TestDocument doc2 = new TestDocument(FAKE_FILE_PATH);
        doc2.setIndexFileName(IndexConstants.INDEX_NAME.TABLES_INDEX);
        final TestDocument doc3 = new TestDocument(FAKE_FILE_PATH);
        doc3.setIndexFileName(IndexConstants.INDEX_NAME.MODELS_INDEX);
        sw3.stop();
        sw3.reset();
        sw3.start();
        try {
            TestIndexer indexer = new TestIndexer(numModels, numTables, numColumns);
            index1.add(doc1, indexer);
            index2.add(doc2, indexer);
            index3.add(doc3, indexer);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        sw1.stop();

        // Save the index file
        sw2.start();
        try {
            index1.save();
            index2.save();
            index3.save();
            assertEquals(1, index1.getNumDocuments());
            assertEquals(1, index2.getNumDocuments());
            assertEquals(1, index3.getNumDocuments());
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        sw2.stop();
        File indexFile1 = new File(TEST_INDEX_DIRECTORY_PATH + IPath.SEPARATOR + IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        System.out.println("  " + indexFile1 + " is " + indexFile1.length() + " bytes"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        File indexFile2 = new File(TEST_INDEX_DIRECTORY_PATH + IPath.SEPARATOR + IndexConstants.INDEX_NAME.TABLES_INDEX);
        System.out.println("  " + indexFile2 + " is " + indexFile2.length() + " bytes"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        File indexFile3 = new File(TEST_INDEX_DIRECTORY_PATH + IPath.SEPARATOR + IndexConstants.INDEX_NAME.MODELS_INDEX);
        System.out.println("  " + indexFile3 + " is " + indexFile3.length() + " bytes"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
    }

    private static String createModelName( int modelNum ) {
        return MODEL_NAME + getIntAsString(modelNum);
    }

    private static String createTableName( int tableNum ) {
        return TABLE_NAME + getIntAsString(tableNum);
    }

    private static String createColumnName( int columnNum ) {
        return COLUMN_NAME + getIntAsString(columnNum);
    }

    private static String createFullTableName( int modelNum,
                                               int tableNum ) {
        return createModelName(modelNum) + DELIMITER + TABLE_NAME + getIntAsString(tableNum);
    }

    private static String createFullColumnName( int modelNum,
                                                int tableNum,
                                                int columnNum ) {
        return createFullTableName(modelNum, tableNum) + DELIMITER + COLUMN_NAME + getIntAsString(columnNum);
    }

    private static String getIntAsString( int i ) {
        if (i < 10) {
            return "000" + Integer.toString(i); //$NON-NLS-1$
        } else if (i < 100) {
            return "00" + Integer.toString(i); //$NON-NLS-1$
        } else if (i < 1000) {
            return "0" + Integer.toString(i); //$NON-NLS-1$
        } else if (i < 10000) {
            return Integer.toString(i);
        } else if (i < 100000) {
            return Integer.toString(i);
        }
        throw new UnsupportedOperationException("The method does not support numbers of the form " + Integer.toString(i)); //$NON-NLS-1$
    }

    public static char[] helpCreateModelWord( int modelNum ) {
        String name = createModelName(modelNum);
        FakeSqlModelAspect aspect = new FakeSqlModelAspect();
        aspect.fullName = name;
        aspect.name = name;
        aspect.nameInSource = name;
        aspect.path = new Path(name);
        aspect.uuid = getIntAsString(modelNum);

        WORD_ENTRIES.clear();
        RuntimeAdapter.addModelWord(aspect, null, null, WORD_ENTRIES);
        WordEntry word = (WordEntry)WORD_ENTRIES.get(0);
        return word.getWord();
    }

    public static char[] helpCreateTableWord( int modelNum,
                                              int tableNum ) {
        String name = createTableName(tableNum);
        String fullname = createFullTableName(modelNum, tableNum);
        FakeSqlTableAspect aspect = new FakeSqlTableAspect();
        aspect.fullName = fullname;
        aspect.name = name;
        aspect.nameInSource = name;
        aspect.path = new Path(fullname);
        aspect.uuid = getIntAsString(modelNum) + getIntAsString(tableNum);

        WORD_ENTRIES.clear();
        RuntimeAdapter.addTableWord(aspect, null, null, null, WORD_ENTRIES);
        WordEntry word = (WordEntry)WORD_ENTRIES.get(0);
        return word.getWord();
    }

    public static char[] helpCreateColumnWord( int modelNum,
                                               int tableNum,
                                               int columnNum ) {
        String name = createColumnName(columnNum);
        String fullname = createFullColumnName(modelNum, tableNum, columnNum);
        FakeSqlColumnAspect aspect = new FakeSqlColumnAspect();
        aspect.fullName = fullname;
        aspect.name = name;
        aspect.nameInSource = name;
        aspect.path = new Path(fullname);
        aspect.uuid = getIntAsString(modelNum) + getIntAsString(tableNum) + getIntAsString(columnNum);

        WORD_ENTRIES.clear();
        RuntimeAdapter.addColumnWord(aspect, null, null, WORD_ENTRIES);
        WordEntry word = (WordEntry)WORD_ENTRIES.get(0);
        return word.getWord();
    }

    public static void runTest( int numModels,
                                int numTables,
                                int numColumns,
                                String prefix,
                                String pattern ) throws Exception {
        helpCreateIndexFiles(numModels, numTables, numColumns);

        TestIndexSelector selector1 = new TestIndexSelector();
        Stopwatch sw1 = new Stopwatch();
        sw1.start();
        IEntryResult[] results1 = IndexUtil.queryIndex(selector1.getIndexes(IndexConstants.INDEX_NAME.COLUMNS_INDEX),
                                                       prefix.toCharArray(),
                                                       true,
                                                       true);
        sw1.stop();
        System.out.println("  Prefix look up Processing time   = " + sw1.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
        // create new selector so that we have nothing cached as far as index lookups
        TestIndexSelector selector2 = new TestIndexSelector();
        Stopwatch sw2 = new Stopwatch();
        sw2.start();
        IEntryResult[] results2 = IndexUtil.queryIndex(selector2.getIndexes(IndexConstants.INDEX_NAME.COLUMNS_INDEX),
                                                       pattern.toCharArray(),
                                                       false,
                                                       true);
        sw2.stop();
        System.out.println("  Pattern look up Processing time   = " + sw2.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
        System.out.println("  Number of models  = " + numModels); //$NON-NLS-1$
        System.out.println("  Number of tables  = " + numTables); //$NON-NLS-1$
        System.out.println("  Number of columns = " + numColumns); //$NON-NLS-1$
        assertNotNull(results1);
        assertNotNull(results2);
        assertEquals(results1.length, results2.length);
        System.out.println("  Number of results = " + results1.length); //$NON-NLS-1$            
    }

     // This danged test might fail when run with all tests, but runs OK by itself.
    public void test1x1x100() throws Exception {
        System.out.println("\nTestPerformancePrefixAndPatternLookUp.test1x1x100()"); //$NON-NLS-1$
        runTest(1, 1, 100, "" + IndexConstants.RECORD_TYPE.COLUMN, "*"); //$NON-NLS-1$  //$NON-NLS-2$
    }

    public void test1x1x100PatternWithRecordType() throws Exception {
        System.out.println("\nTestPerformancePrefixAndPatternLookUp.test1x1x100PatternWithRecordType()"); //$NON-NLS-1$
        runTest(1, 1, 100, "" + IndexConstants.RECORD_TYPE.COLUMN, "" + IndexConstants.RECORD_TYPE.COLUMN + "*"); //$NON-NLS-1$  //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void test1x1x1000() throws Exception {
        System.out.println("\nTestPerformancePrefixAndPatternLookUp.test1x1x1000()"); //$NON-NLS-1$
        runTest(1, 1, 1000, "" + IndexConstants.RECORD_TYPE.COLUMN, "*"); //$NON-NLS-1$  //$NON-NLS-2$
    }

    public void test1x1x1000PatternWithRecordType() throws Exception {
        System.out.println("\nTestPerformancePrefixAndPatternLookUp.test1x1x1000PatternWithRecordType()"); //$NON-NLS-1$
        runTest(1, 1, 1000, "" + IndexConstants.RECORD_TYPE.COLUMN, "" + IndexConstants.RECORD_TYPE.COLUMN + "*"); //$NON-NLS-1$  //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void test1x1x10000() throws Exception {
        System.out.println("\nTestPerformancePrefixAndPatternLookUp.test1x1x10000()"); //$NON-NLS-1$
        runTest(1, 1, 10000, "" + IndexConstants.RECORD_TYPE.COLUMN, "*"); //$NON-NLS-1$  //$NON-NLS-2$
    }

    public void test1x1x10000PatternWithRecordType() throws Exception {
        System.out.println("\nTestPerformancePrefixAndPatternLookUp.test1x1x10000PatternWithRecordType()"); //$NON-NLS-1$
        runTest(1, 1, 10000, "" + IndexConstants.RECORD_TYPE.COLUMN, "" + IndexConstants.RECORD_TYPE.COLUMN + "*"); //$NON-NLS-1$  //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void test1x1x50000() throws Exception {
        System.out.println("\nTestPerformancePrefixAndPatternLookUp.test1x1x50000()"); //$NON-NLS-1$
        runTest(1, 1, 50000, "" + IndexConstants.RECORD_TYPE.COLUMN, "*"); //$NON-NLS-1$  //$NON-NLS-2$
    }

    public void test1x1x50000PatternWithRecordType() throws Exception {
        System.out.println("\nTestTransformationMetadataPerformance.test1x1x50000PatternWithRecordType()"); //$NON-NLS-1$
        runTest(1, 1, 50000, "" + IndexConstants.RECORD_TYPE.COLUMN, "" + IndexConstants.RECORD_TYPE.COLUMN + "*"); //$NON-NLS-1$  //$NON-NLS-2$ //$NON-NLS-3$
    }

    private static class TestIndexer implements IIndexer {
        private int nm = 1;
        private int nt = 1;
        private int nc = 1;
        boolean wordsCollected = false;
        List modelWords = new ArrayList();
        List tabelWords = new ArrayList();
        List columnWords = new ArrayList();
        Stopwatch sw = new Stopwatch();

        public TestIndexer( int numModels,
                            int numTables,
                            int numColumns ) {
            nm = numModels;
            nt = numTables;
            nc = numColumns;
        }

        public String[] getFileTypes() {
            return null;
        }

        public void index( IDocument document,
                           IIndexerOutput output ) {
            // Stopwatch sw1 = new Stopwatch();
            // sw1.reset();
            // sw1.start();
            TestDocument testDoc = (TestDocument)document;
            String indexFileName = testDoc.getIndexFileName();

            if (document instanceof TestDocument && !wordsCollected) {
                sw.reset();
                sw.start();
                for (int k = 1; k <= nm; k++) {
                    modelWords.add(helpCreateModelWord(k));
                    for (int j = 1; j <= nt; j++) {
                        tabelWords.add(helpCreateTableWord(k, j));
                        for (int i = 1; i <= nc; i++) {
                            columnWords.add(helpCreateColumnWord(k, j, i));
                        }
                    }
                }
                wordsCollected = true;
                sw.stop();
                System.out.println("  Word Collection  time   = " + sw.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$                
            }

            // add entried to the output if the indexName matches entity specic index
            // name
            if (indexFileName.equals(IndexConstants.INDEX_NAME.COLUMNS_INDEX)) {
                sw.reset();
                sw.start();
                output.addDocument(document);
                addEntries(output, columnWords);
                sw.stop();
                System.out.println("  Column Indexing  time   = " + sw.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$                
            } else if (indexFileName.equals(IndexConstants.INDEX_NAME.TABLES_INDEX)) {
                sw.reset();
                sw.start();
                output.addDocument(document);
                addEntries(output, tabelWords);
                sw.stop();
                System.out.println("  Table Indexing  time   = " + sw.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
            } else if (indexFileName.equals(IndexConstants.INDEX_NAME.MODELS_INDEX)) {
                sw.reset();
                sw.start();
                output.addDocument(document);
                addEntries(output, modelWords);
                sw.stop();
                System.out.println("  Model Indexing  time   = " + sw.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
            }
        }

        /**
         * Add word entries to indexoutput.
         */
        private void addEntries( IIndexerOutput output,
                                 List words ) {
            Iterator wordIter = words.iterator();
            while (wordIter.hasNext()) {
                char[] entry = (char[])wordIter.next();
                // String entry = String.valueOf(wordIter.next());
                output.addRef(entry);
            }
        }

        public void setFileTypes( String[] fileTypes ) {
        }

        public boolean shouldIndex( IDocument document ) {
            return true;
        }
    }

    private static class TestDocument extends FileDocument {
        private String indexFileName;

        public TestDocument( String filePath ) {
            super(new File(filePath));
        }

        public void setIndexFileName( String fileName ) {
            this.indexFileName = fileName;
        }

        public String getIndexFileName() {
            return this.indexFileName;
        }
    }

    static class TestIndexSelector extends AbstractIndexSelector {
        @Override
        public Index[] getIndexes() throws IOException {
            final File indexDirectory = new File(TEST_INDEX_DIRECTORY_PATH);

            Index[] indexes = new Index[3];
            String fullPathFileName = indexDirectory.getAbsolutePath() + File.separator + IndexConstants.INDEX_NAME.COLUMNS_INDEX;
            Index index1 = indexManager.getIndex(IndexConstants.INDEX_NAME.COLUMNS_INDEX,
                                                 fullPathFileName,
                                                 IndexConstants.INDEX_NAME.COLUMNS_INDEX);
            fullPathFileName = indexDirectory.getAbsolutePath() + File.separator + IndexConstants.INDEX_NAME.TABLES_INDEX;
            Index index2 = indexManager.getIndex(IndexConstants.INDEX_NAME.TABLES_INDEX,
                                                 fullPathFileName,
                                                 IndexConstants.INDEX_NAME.TABLES_INDEX);
            fullPathFileName = indexDirectory.getAbsolutePath() + File.separator + IndexConstants.INDEX_NAME.MODELS_INDEX;
            Index index3 = indexManager.getIndex(IndexConstants.INDEX_NAME.MODELS_INDEX,
                                                 fullPathFileName,
                                                 IndexConstants.INDEX_NAME.MODELS_INDEX);

            assertNotNull(index1);
            assertEquals(1, index1.getNumDocuments());
            assertNotNull(index2);
            assertEquals(1, index2.getNumDocuments());
            assertNotNull(index3);
            assertEquals(1, index3.getNumDocuments());
            indexes[0] = index1;
            indexes[1] = index2;
            indexes[2] = index3;
            return indexes;
        }

        public Index[] getIndexes( String indexName ) throws IOException {
            final File indexDirectory = new File(TEST_INDEX_DIRECTORY_PATH);

            Index[] indexes = new Index[3];
            // Index index1 = new Index(indexDirectory,indexName,true);
            String fullPathFileName = indexDirectory.getAbsolutePath() + File.separator + indexName;
            Index index1 = indexManager.getIndex(indexName, fullPathFileName, indexName);
            assertNotNull(index1);
            assertEquals(1, index1.getNumDocuments());
            indexes[0] = index1;
            return indexes;
        }
    }
}
