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
import java.util.List;
import junit.framework.TestCase;
import org.eclipse.core.runtime.Path;
import com.metamatrix.core.index.AbstractIndexSelector;
import com.metamatrix.core.index.IDocument;
import com.metamatrix.core.index.IIndexer;
import com.metamatrix.core.index.IIndexerOutput;
import com.metamatrix.core.util.SmartTestSuite;
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

public class TestPerformanceMultipleRecordTypesInOneIndexFile extends TestCase {

    private static final List WORD_ENTRIES = new ArrayList(7);
    private static final String TEST_INDEX_FILE_NAME = "test.index"; //$NON-NLS-1$
    static final String TEST_INDEX_DIRECTORY_PATH = SmartTestSuite.getTestDataPath();
    private static final String TEST_INDEX_FILE_PATH = TEST_INDEX_DIRECTORY_PATH + File.separator + TEST_INDEX_FILE_NAME;
    private static final String FAKE_FILE_PATH = SmartTestSuite.getTestDataPath() + File.separator + "PartsRelational.mmm"; //$NON-NLS-1$

    private static final String MODEL_NAME = "Model"; //$NON-NLS-1$
    private static final String TABLE_NAME = "Table"; //$NON-NLS-1$
    private static final String COLUMN_NAME = "Column"; //$NON-NLS-1$
    private static final char DELIMITER = '.';

    static ModelWorkspaceIndexManager indexManager = new ModelWorkspaceIndexManager();

    /**
     * Constructor for TestPerformanceMultipleRecordTypesInOneIndexFile.
     * 
     * @param name
     */
    public TestPerformanceMultipleRecordTypesInOneIndexFile( String name ) {
        super(name);
    }

    private static void helpCreateIndexFile( int numModels,
                                             int numTables,
                                             int numColumns,
                                             String theFileName ) {
        Stopwatch sw1 = new Stopwatch();
        Stopwatch sw2 = new Stopwatch();
        sw1.start();

        // Create the index
        final File indexDirectory = new File(TEST_INDEX_DIRECTORY_PATH);
        Index index = null;
        try {
            index = new Index(indexDirectory, theFileName, false);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        assertNotNull(index);

        final IDocument doc = new TestDocument(FAKE_FILE_PATH);
        try {
            index.add(doc, new TestIndexer(numModels, numTables, numColumns));
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        sw1.stop();

        // Save the index file
        sw2.start();
        try {
            index.save();
            assertEquals(1, index.getNumDocuments());
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
        sw2.stop();
        File indexFile = new File(TEST_INDEX_FILE_PATH);
        System.out.println("  " + TEST_INDEX_FILE_PATH + " is " + indexFile.length() + " bytes"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        System.out.println("  Time to build index file is " + sw1.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
        System.out.println("  Time to save index file is  " + sw2.getTotalDuration() + " ms"); //$NON-NLS-1$//$NON-NLS-2$
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
                                int numColumns,
                                String theFileName ) throws Exception {
        helpCreateIndexFile(numModels, numTables, numColumns, theFileName);
        final IndexSelector selector = new TestIndexSelector(theFileName);
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

    public void testCreate1() {
        System.out.println("\nTestPerformanceMultipleRecordTypesInOneIndexFile.testCreate1()"); //$NON-NLS-1$
        helpCreateIndexFile(1, 2, 3, TEST_INDEX_FILE_NAME);
        final File testIndexFile = new File(TEST_INDEX_FILE_PATH);
        testIndexFile.delete();

    }

    public void test1x10x10() throws Exception {
        System.out.println("\nTestPerformanceMultipleRecordTypesInOneIndexFile.test1x10x10()"); //$NON-NLS-1$
        runTest(1, 10, 10, IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        indexManager.disposeAll();
    }

    public void test1x100x10() throws Exception {
        System.out.println("\nTestPerformanceMultipleRecordTypesInOneIndexFile.test1x100x10()"); //$NON-NLS-1$
        runTest(1, 100, 10, IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        indexManager.disposeAll();
    }

    public void test1x1000x10() throws Exception {
        System.out.println("\nTestPerformanceMultipleRecordTypesInOneIndexFile.test1x1000x10()"); //$NON-NLS-1$
        runTest(1, 1000, 10, IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        indexManager.disposeAll();
    }

    public void test1x5000x10() throws Exception {
        System.out.println("\nTestPerformanceMultipleRecordTypesInOneIndexFile.test1x5000x10()"); //$NON-NLS-1$
        runTest(1, 5000, 10, IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        indexManager.disposeAll();
    }

    public void test1x1000x50() throws Exception {
        System.out.println("\nTestPerformanceMultipleRecordTypesInOneIndexFile.test1x1000x50()"); //$NON-NLS-1$
        runTest(1, 1000, 50, IndexConstants.INDEX_NAME.COLUMNS_INDEX);
        indexManager.disposeAll();
    }

    private static class TestIndexer implements IIndexer {
        private int nm = 1;
        private int nt = 1;
        private int nc = 1;

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
            output.addDocument(document);
            if (document instanceof TestDocument) {
                for (int k = 1; k <= nm; k++) {
                    output.addRef(helpCreateModelWord(k));
                    for (int j = 1; j <= nt; j++) {
                        output.addRef(helpCreateTableWord(k, j));
                        for (int i = 1; i <= nc; i++) {
                            output.addRef(helpCreateColumnWord(k, j, i));
                        }
                    }
                }
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
                String indexName = (String)this.indexNames.get(i);
                String fullPathFileName = indexDirectory.getAbsolutePath() + File.separator + indexName;
                Index index = indexManager.getIndex(indexName, fullPathFileName, indexName);
                // Index index = new Index(indexDirectory, (String)this.indexNames.get(i), false);
                index.save();
                assertNotNull(index);
                assertEquals(1, index.getNumDocuments());
                indexes[i] = index;
            }
            return indexes;
        }
    }
}
