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
import com.metamatrix.core.index.IIndexer;
import com.metamatrix.core.index.IIndexerOutput;
import com.metamatrix.core.util.SmartTestDesignerSuite;
import com.metamatrix.core.util.Stopwatch;
import com.metamatrix.internal.core.index.FileDocument;
import com.metamatrix.internal.core.index.Index;
import com.metamatrix.internal.core.index.WordEntry;
import com.metamatrix.modeler.core.index.IndexConstants;
import com.metamatrix.modeler.core.index.IndexSelector;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlColumnAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlModelAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.FakeSqlTableAspect;
import com.metamatrix.modeler.internal.core.metadata.runtime.RuntimeAdapter;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceIndexManager;
import org.teiid.query.metadata.QueryMetadataInterface;

/**
 * TestPerformanceIndexFilePerRecordType
 */
public class TestPerformanceIndexFilePerRecordType extends TestCase {

    private static final List WORD_ENTRIES = new ArrayList(7);

    //private static final String TEST_INDEX_FILE_NAME = "test.index"; //$NON-NLS-1$
    static final String TEST_INDEX_DIRECTORY_PATH = SmartTestDesignerSuite.getTestDataPath();
    // private static final String TEST_INDEX_FILE_PATH = TEST_INDEX_DIRECTORY_PATH + File.separator + TEST_INDEX_FILE_NAME;
    private static final String FAKE_FILE_PATH = SmartTestDesignerSuite.getTestDataPath() + File.separator + "PartsRelational.mmm"; //$NON-NLS-1$

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
    public TestPerformanceIndexFilePerRecordType( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestPerformanceIndexFilePerRecordType"); //$NON-NLS-1$
        suite.addTestSuite(TestPerformanceIndexFilePerRecordType.class);
        //suite.addTest(new TestPerformanceIndexFilePerRecordType("test1x10x10")); //$NON-NLS-1$        

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
        System.out.println("  Time to create indexs " + sw3.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
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
        System.out.println("  Time to create documents " + sw3.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
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
        System.out.println("  Time to build index files is " + sw1.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
        System.out.println("  Time to save index files is  " + sw2.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
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
        //System.out.println("word = "+word); //$NON-NLS-1$
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
        //System.out.println("word = "+word); //$NON-NLS-1$
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
        //System.out.println("word = "+word); //$NON-NLS-1$
        return word.getWord();
    }

    public static void runTest( int numModels,
                                int numTables,
                                int numColumns ) throws Exception {
        helpCreateIndexFiles(numModels, numTables, numColumns);
        final IndexSelector selector = new TestIndexSelector();
        QueryMetadataInterface queryMetadata = TransformationMetadataFactory.getInstance().getServerMetadata(selector);

        Stopwatch sw = new Stopwatch();
        // Name of a column at the end of the model
        int modelNum = numModels;
        int tableNum = numTables;
        int columnNum = numColumns;
        String fullname1 = createFullColumnName(modelNum, tableNum, columnNum);

        // Name of a column in the middle of the model
        modelNum = (numModels / 2 < 1 ? 1 : numModels / 2);
        tableNum = (numTables / 2 < 1 ? 1 : numTables / 2);
        columnNum = (numColumns / 2 < 1 ? 1 : numColumns / 2);
        String fullname2 = createFullColumnName(modelNum, tableNum, columnNum);

        // Name of a column at the beginning of the model
        modelNum = 1;
        tableNum = 1;
        columnNum = 1;
        String fullname3 = createFullColumnName(modelNum, tableNum, columnNum);

        sw.start();
        Object result = queryMetadata.getElementID(fullname1);
        assertNotNull(result);
        result = queryMetadata.getElementID(fullname2);
        assertNotNull(result);
        result = queryMetadata.getElementID(fullname3);
        assertNotNull(result);
        sw.stop();
        System.out.println("  Number of models  = " + numModels); //$NON-NLS-1$
        System.out.println("  Number of tables  = " + numTables); //$NON-NLS-1$
        System.out.println("  Number of columns = " + numColumns); //$NON-NLS-1$
        System.out.println("  Processing time   = " + sw.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
    }

    public void FAILINGtestCreate1() {
        System.out.println("\nTestPerformanceIndexFilePerRecordType.testCreate1()"); //$NON-NLS-1$
        helpCreateIndexFiles(1, 2, 3);
        indexManager.disposeAll();
    }

    public void test1x10x10() throws Exception {
        System.out.println("\nTestPerformanceIndexFilePerRecordType.test1x10x10()"); //$NON-NLS-1$
        // TODO: Fix this test???
//        runTest(1, 10, 10);
        indexManager.disposeAll();
    }

    public void test1x100x10() throws Exception {
        System.out.println("\nTestPerformanceIndexFilePerRecordType.test1x100x10()"); //$NON-NLS-1$
     // TODO: Fix this test???
//        runTest(1, 100, 10);
        indexManager.disposeAll();
    }

    public void test1x1000x10() throws Exception {
        System.out.println("\nTestPerformanceIndexFilePerRecordType.test1x1000x10()"); //$NON-NLS-1$
     // TODO: Fix this test???
//        runTest(1, 1000, 10);
        indexManager.disposeAll();
    }

    public void test1x5000x10() throws Exception {
        System.out.println("\nTestPerformanceIndexFilePerRecordType.test1x5000x10()"); //$NON-NLS-1$
     // TODO: Fix this test???
//        runTest(1, 5000, 10);
        indexManager.disposeAll();
    }

    public void test1x1000x50() throws Exception {
        System.out.println("\nTestPerformanceIndexFilePerRecordType.test1x1000x50()"); //$NON-NLS-1$
     // TODO: Fix this test???
//        runTest(1, 1000, 50);
        indexManager.disposeAll();
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
            // sw1.stop();
            //System.out.println("  Total Indexing  time   = "+sw1.getTotalDuration()+" ms");  //$NON-NLS-1$//$NON-NLS-2$            
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
    }
}
